package org.tengel.splay;

import java.util.Vector;
import java.io.File;

public class Playlist
{
    private static Playlist m_instance = null;
    private Vector<File>    m_list;
    private int             m_pos;
    private boolean         m_enabled;

    private Playlist()
    {
        m_list    = new Vector<File>();
        m_pos     = 0;
        m_enabled = false;
    }

    public static Playlist instance()
    {
        if (m_instance == null)
        {
            m_instance = new Playlist();
        }
        return m_instance;
    }

    public Vector<File> getList()
    {
        return m_list;
    }

    public void addFile(File file)
    {
        m_list.add(file);
    }

    public void setPos(int pos)
    {
        m_pos = pos;
        try
        {
            Player.instance().setFile(m_list.get(m_pos));
            Player.instance().start();
        }
        catch(Exception e)
        {
        }
    }

    public void timer()
    {
        try
        {
            if (m_enabled && Player.instance().isCompleted())
            {
                Player.instance().setFile(next());
                Player.instance().start();
            }
        }
        catch(Exception e)
        {
        }
    }


    public File next()
    {
        if (m_list == null || m_list.size() == 0)
        {
            return null;
        }
        if (m_pos < m_list.size() - 1)
        {
            m_pos += 1;
        }
        return m_list.get(m_pos);
    }


    public void remove(int pos)
    {
        m_list.remove(pos);
    }


    public boolean isEnabled()
    {
        return m_enabled;
    }


    public void setEnabled(boolean enabled)
    {
        m_enabled = enabled;
    }
}
