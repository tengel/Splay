package org.tengel.splay;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.Menu;
import android.view.View;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import java.io.File;
import java.util.TimerTask;
import java.util.Timer;
import android.os.Handler;
import java.text.DecimalFormat;
import android.app.AlertDialog;
import android.view.MenuItem;
import android.content.DialogInterface;
import android.content.Context;
import java.lang.Exception;
import android.os.PowerManager;
import android.media.audiofx.Equalizer;

enum PlayerState {IDLE, PLAYING, PAUSED};

public class Player
{
    PlayerState   m_state = PlayerState.IDLE;
    File          m_audioFile;
    MediaPlayer   m_player;
    Equalizer     m_equalizer;
    static Player m_instance = null;

    private Player()
    {
    }

    public static Player instance()
    {
        if (m_instance == null)
        {
            m_instance = new Player();
        }
        return m_instance;
    }


    public void setFile(File audioFile) throws Exception
    {
        m_audioFile = audioFile;
        if (m_state != PlayerState.IDLE)
        {
            m_player.stop();
        }
        if (m_audioFile == null || !m_audioFile.canRead())
        {
            m_state = PlayerState.IDLE;
            return;
        }
        m_player = MediaPlayer.create(null, Uri.fromFile(m_audioFile));
        if (m_player == null)
        {
            throw new Exception(audioFile.getAbsolutePath() + ": Can't play file");
        }
        m_state = PlayerState.PAUSED;
    }

    public File getFile()
    {
        if (m_state != PlayerState.IDLE)
        {
            return m_audioFile;
        }
        else
        {
            return new File("");
        }
    }

    public void pause()
    {
        if (m_state != PlayerState.IDLE)
        {
            m_state = PlayerState.PAUSED;
            m_player.pause();
        }
    }

    public void start()
    {
        if (m_state != PlayerState.IDLE)
        {
            m_state = PlayerState.PLAYING;
            m_player.start();
        }
    }

    public void playPause()
    {
        if (m_state == PlayerState.IDLE || m_state == PlayerState.PAUSED)
        {
            start();
        }
        else if (m_state == PlayerState.PLAYING)
        {
            pause();
        }
    }


    public boolean isPlaying()
    {
        return m_state == PlayerState.PLAYING;
    }

    public boolean isCompleted()
    {
        return m_state == PlayerState.PLAYING && !m_player.isPlaying();
    }

    public boolean isIdle()
    {
        return m_state == PlayerState.IDLE;
    }


    public int getCurrentPosition()
    {
        int pos;
        if (m_state == PlayerState.IDLE)
        {
            pos = 0;
        }
        else
        {
            pos = m_player.getCurrentPosition();
        }
        return pos;
    }

    public void seek(int pos)
    {
        if (m_state != PlayerState.IDLE)
        {
            m_player.seekTo(pos);
        }
    }

    public String getDuration()
    {
        int dur;
        if (m_state == PlayerState.IDLE)
        {
            dur = 0;
        }
        else
        {
            dur = m_player.getDuration();
        }
        return Util.msToStr(dur);
    }

    public String getTimeLeft()
    {
        int left;
        if (m_state == PlayerState.IDLE)
        {
            left = 0;
        }
        else
        {
            left = m_player.getDuration() - m_player.getCurrentPosition();
        }
        return Util.msToStr(left);
    }

    public void boostVolume(boolean doBoost)
    {
        m_equalizer = new Equalizer(0, m_player.getAudioSessionId());
        if (doBoost)
        {
            m_equalizer.setEnabled(true);
            short bands = m_equalizer.getNumberOfBands();
            short maxEQLevel = m_equalizer.getBandLevelRange()[1];
            for (short band = 0; band < bands; ++band)
            {
                m_equalizer.setBandLevel(band, (short) maxEQLevel);
            }
        }
        else
        {
            m_equalizer.setEnabled(false);
        }
    }

}
