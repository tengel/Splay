package org.tengel.splay;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Environment;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.util.Collections;
import android.util.Log;

import java.util.Vector;
import android.widget.Toast;
import android.content.Context;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;

public class PlaylistActivity extends ListActivity
{
    public List<ListItem> m_list = null;
    private File          m_root;


    public class DeleteDialogListener implements DialogInterface.OnClickListener
    {
        int              m_pos;
        PlaylistActivity m_playlistActivity;

        public DeleteDialogListener(int pos, PlaylistActivity fs)
        {
            m_pos              = pos;
            m_playlistActivity = fs;
        }

        public void onClick (DialogInterface dialog, int which)
        {
            if (which == DialogInterface.BUTTON_POSITIVE)
            {
                Playlist.instance().remove(m_pos);
                m_playlistActivity.updateList();
            }
        }
    }


    public class DeleteDialog implements OnItemLongClickListener
    {
        PlaylistActivity m_playlistActivity;

        public DeleteDialog(PlaylistActivity playlistActivity)
        {
            m_playlistActivity = playlistActivity;
        }

        public boolean onItemLongClick(AdapterView<?> parent, View v,
                                       int position, long id)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(m_playlistActivity);
            builder.setMessage("Remove from playlist " +
                               m_playlistActivity.m_list.get(position));
            DeleteDialogListener listener = new DeleteDialogListener(
                position, m_playlistActivity);
            builder.setPositiveButton("Yes", listener);
            builder.setNegativeButton("No", listener);
            builder.create().show();
            return true;
        }
    }


    public class ListItem implements Comparable<ListItem>
    {
        File    m_file;
        boolean m_isParent;

        public ListItem(File f, boolean isParent)
        {
            m_file     = f;
            m_isParent = isParent;
        }

        public ListItem(File f)
        {
            this(f, false);
        }

        public int compareTo(ListItem other)
        {
            if (m_isParent)
            {
                return -1;
            }
            else if (m_file.isDirectory() && !other.m_file.isDirectory())
            {
                return -1;
            }
            else if (!m_file.isDirectory() && other.m_file.isDirectory())
            {
                return 1;
            }

            else
            {
                return m_file.compareTo(other.m_file);
            }
        }

        public File getFile()
        {
            return m_file;
        }

        @Override
        public String toString()
        {
            String s = m_file.getName();
            if (m_isParent)
            {
                return "..";
            }
            else if (m_file.isDirectory())
            {
                return s + "/";
            }
            else
            {
                return s;
            }
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.file_selector);
        CheckBox enableCb = (CheckBox) findViewById(R.id.enable_checkbox);
        enableCb.setChecked(Playlist.instance().isEnabled());

        DeleteDialog d = new DeleteDialog(this);
        this.getListView().setLongClickable(true);
        this.getListView().setOnItemLongClickListener(d);

        updateList();
    }


    @Override
    protected void onResume()
    {
        super.onResume();
        updateList();
    }


    private void updateList()
    {
        if (m_list == null)
        {
            m_list = new ArrayList<ListItem>();
        }
        else
        {
            m_list.clear();
        }
        Vector<File> list = Playlist.instance().getList();
        for(int i = 0; i < list.size(); ++i)
        {
            m_list.add(new ListItem(list.get(i)));
        }
        ArrayAdapter<ListItem> fileList =
            new ArrayAdapter<ListItem>(this, R.layout.row, m_list);
        setListAdapter(fileList);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        File file = m_list.get(position).getFile();
        Toast toast = Toast.makeText(getApplicationContext(),
                                     "playing " + file.toString(),
                                     Toast.LENGTH_SHORT);
        toast.show();
        Playlist.instance().setPos(position);
    }


   @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.playermenu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();
        if (id == R.id.select_file)
        {
            Intent intent = new Intent(this, FileSelector.class);
            startActivity(intent);
            Log.d("Splay", "FileSelector done");
            updateList();
        }
        else if (id == R.id.show_playlist)
        {
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


    public void onCheckboxEnable(View view)
    {
        boolean checked = ((CheckBox) view).isChecked();
        Playlist.instance().setEnabled(checked);
    }
}
