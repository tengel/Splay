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

public class FileSelector extends ListActivity
{
    public List<ListItem>      m_list = null;
    private TextView          m_myPath;
    private SharedPreferences m_prefs;
    private File              m_root;


    public class DeleteDialogListener implements DialogInterface.OnClickListener
    {
        File m_file;
        FileSelector m_fileSelector;

        public DeleteDialogListener(File f, FileSelector fs)
        {
            m_file = f;
            m_fileSelector = fs;
        }

        public void onClick (DialogInterface dialog, int which)
        {
            if (which == DialogInterface.BUTTON_POSITIVE)
            {
                m_file.delete();
                m_fileSelector.getDir(m_fileSelector.m_root);
            }
        }
    }


    public class DeleteDialog implements OnItemLongClickListener
    {
        FileSelector m_fileSelector;

        public DeleteDialog(FileSelector fileSelector)
        {
            m_fileSelector = fileSelector;
        }

        public boolean onItemLongClick(AdapterView<?> parent, View v,
                                       int position, long id)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(m_fileSelector);
            builder.setMessage("Delete file " +
                               m_fileSelector.m_list.get(position));
            DeleteDialogListener listener = new DeleteDialogListener(
                m_fileSelector.m_list.get(position).getFile(),
                m_fileSelector);
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
            m_file = f;
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
        m_myPath = (TextView)findViewById(R.id.path);
        m_prefs = getSharedPreferences("fileselector_prefs", 0);

        DeleteDialog d = new DeleteDialog(this);
        this.getListView().setLongClickable(true);
        this.getListView().setOnItemLongClickListener(d);

        String root = m_prefs.getString("root_path", null);
        File rootFile;
        if (root == null)
        {
            rootFile = Environment.getExternalStorageDirectory();
        }
        else
        {
            rootFile = new File(root);
        }
        if (rootFile.canRead())
        {
            getDir(rootFile);
        }
        else
        {
            getDir(new File("/"));
        }
    }

    private void getDir(File dirPath)
    {
        Log.d("Splay", "getDir: " + dirPath.toString());
        SharedPreferences.Editor ed = m_prefs.edit();
        ed.putString("root_path", dirPath.getAbsolutePath());
        ed.commit();
        m_root = dirPath;
        m_myPath.setText("Location: " + dirPath.getAbsolutePath());
        m_list = new ArrayList<ListItem>();
        File[] files = dirPath.listFiles();
        File parent = dirPath.getParentFile();
        if (parent != null)
        {
            m_list.add(new ListItem(parent, true));
        }
        for(int i=0; i < files.length; i++)
        {
            File file = files[i];
            if(!file.isHidden() && file.canRead())
            {
                m_list.add(new ListItem(file));
            }
        }
        Collections.sort(m_list);
        ArrayAdapter<ListItem> fileList =
            new ArrayAdapter<ListItem>(this, R.layout.row, m_list);
        setListAdapter(fileList);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        File file = m_list.get(position).getFile();

        if (file.isDirectory())
        {
            if(file.canRead())
            {
                getDir(m_list.get(position).getFile());
            }
            else
            {
                new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle("[" + file.getName() + "] folder can't be read!")
                    .setPositiveButton("OK", null).show();
            }
        }
        else
        {
            try
            {
                Player.instance().setFile(file);
            }
            catch (Exception e)
            {
            }
            finish();
        }
    }

}