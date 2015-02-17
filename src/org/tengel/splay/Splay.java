package org.tengel.splay;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import android.view.View;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import java.io.File;
import java.util.TimerTask;
import java.util.Timer;
import android.os.Handler;
import android.app.AlertDialog;
import android.view.MenuItem;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

public class Splay extends Activity
{
    Handler    m_handler = new Handler();
    Button     m_playPauseButton;
    Button     m_sleepButton;
    SleepTimer m_timer = new SleepTimer();
    SharedPreferences m_prefs;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.main);

            m_playPauseButton = (Button) findViewById(R.id.playPause);
            m_sleepButton = (Button) findViewById(R.id.sleep);
            m_prefs = getSharedPreferences("splay_prefs", 0);

            Runnable updateTask = new Runnable()
            {
                public void run()
                {
                    SharedPreferences.Editor ed = m_prefs.edit();
                    ed.putString("player_file",
                                 Player.instance().getFile().getAbsolutePath());
                    ed.putInt("player_position",
                              Player.instance().getCurrentPosition());
                    ed.commit();

                    m_timer.tick();
                    updateUi();
                    if (m_timer.isElapsed())
                    {
                        Player.instance().pause();
                    }
                    m_handler.postDelayed(this, 100);
                }
            };
            m_handler.postDelayed(updateTask, 100);

            if (Player.instance().isIdle())
            {
                Player.instance().setFile(new File(m_prefs.getString("player_file", "no_file")));
                Player.instance().seek(m_prefs.getInt("player_position", 0));
            }
        }
        catch (Exception e)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("onCreate: " + e.toString() +
                               e.getStackTrace()[0].toString());
            builder.create().show();
        }
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playermenu, menu);
        return true;
    }


    public void updateUi()
    {
        TextView durationText = (TextView) findViewById(R.id.duration_text);
        durationText.setText(Player.instance().getDuration());
        TextView actualText = (TextView) findViewById(R.id.actual_text);
        actualText.setText(Util.msToStr(Player.instance().getCurrentPosition()));
        TextView leftText = (TextView) findViewById(R.id.left_text);
        leftText.setText(Player.instance().getTimeLeft());
        m_sleepButton.setText("Sleep in " + m_timer.getTimeLeft() + " min");
        if (Player.instance().isPlaying())
        {
            m_playPauseButton.setText("Pause");
        }
        else
        {
            m_playPauseButton.setText("Play");
        }

        TextView filenameText = (TextView) findViewById(R.id.filename_text);
        filenameText.setText(Player.instance().getFile().getAbsolutePath());
    }

    public void setSleep(View view)
    {
        try
        {
            m_timer.start();
            Player.instance().start();
            updateUi();
        }
        catch (Exception e)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("geht nicht " + e.toString() +
                               e.getStackTrace()[0].toString());
            builder.create().show();

        }
    }

    public void playPause(View view)
    {
        try
        {
            Player.instance().playPause();
            updateUi();
        }
        catch (Exception e)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("geht nicht " + e.toString() +
                               e.getStackTrace()[0].toString());
            builder.create().show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.select_file)
        {
            Intent intent = new Intent(this, FileSelector.class);
            startActivity(intent);
            updateUi();
        }
        else if (id == R.id.help)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Hello, World!");
            builder.setPositiveButton(
                R.string.ok,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id)
                    {
                        // User clicked OK button
                    }
                });
            builder.create().show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void seekForward(View view)
    {
        Player.instance().seek(Player.instance().getCurrentPosition() + 30000);
    }

    public void seekBackward(View view)
    {
        Player.instance().seek(Player.instance().getCurrentPosition() - 30000);
    }

    public void onVolumeBoost(View view)
    {
        boolean boost = ((CheckBox) view).isChecked();
        Toast.makeText(getApplicationContext(),
                       "volume boost: " + boost, Toast.LENGTH_SHORT).show();
        Player.instance().boostVolume(boost);
    }
}
