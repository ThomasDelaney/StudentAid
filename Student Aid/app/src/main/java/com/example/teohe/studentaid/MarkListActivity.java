package com.example.teohe.studentaid;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Thomas on 17/11/2017.
 */

public class MarkListActivity extends AppCompatActivity
{
    //for editing toolbar images at runtime
    Menu toolbarMenu;

    String moduleName;
    ArrayList<Mark> marks;
    DatabaseManager databaseManager;
    ListView markListView;

    //mode for what to do when modules are clicked
    //0 if default mode (click to see marks)
    //1 if edit mode (click to edit)
    //2 if delete mode (2 to delete)
    int mode = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_list);

        moduleName = getIntent().getExtras().getString("moduleName");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(moduleName);
        actionBar.show();

        markListView = (ListView)findViewById(R.id.markList);

        databaseManager = new DatabaseManager(getApplicationContext());

        marks = new ArrayList<Mark>();

        /*databaseManager.open();
        Cursor markCursor = databaseManager.getMarks(moduleName);

        //check if list is empty
        populateMarksFromCursor(markCursor);
        databaseManager.close();

        if (marks.isEmpty())
        {
            //add CA as 101, which will be no way enterable by the user cause 100 will be the max, this is used to make the worth column text view empty
            Module temp = new Module("Your Module List is Empty! Click the Add Button to Add Some Modules", 101);
            marks.add(temp);
            markListView.setAdapter(new ModuleListActivity.ModuleAdapter(ModuleListActivity.this, R.layout.module_row, modules));
        }
        else
        {
            moduleListView.setAdapter(new ModuleListActivity.ModuleAdapter(ModuleListActivity.this, R.layout.module_row, modules));
        }


        moduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id)
            {
                //if delete mode
                if (mode == 2)
                {
                    AlertDialog.Builder areYouSure = new AlertDialog.Builder(ModuleListActivity.this);
                    areYouSure.setTitle("Are You Sure You Want to Delete This Module?");
                    areYouSure.setMessage(modules.get(position).getModuleName());

                    areYouSure.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener()
                            {
                                //since we're using inner class, need to get position and sore in local variable for onClick, or it will ask for final int
                                int rowPosition = position;

                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    databaseManager.open();
                                    long deleteResult = databaseManager.deleteModule(modules.get(rowPosition).getModuleName());
                                    databaseManager.close();

                                    dialog.cancel();

                                    if (deleteResult == -1)
                                    {
                                        Toast.makeText(getApplicationContext(), "Error Deleting Module", Toast.LENGTH_SHORT).show();
                                    }

                                    mode = 0;
                                    toolbarMenu.getItem(2).setIcon(R.drawable.ic_delete_white_24dp); //change icon back to normal
                                    recreate();
                                }
                            });

                    areYouSure.setNegativeButton("No",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog dialog = areYouSure.create();
                    dialog.show();
                }
                //if edit mode
                else if (mode == 1)
                {
                    AlertDialog.Builder areYouSure = new AlertDialog.Builder(ModuleListActivity.this);
                    areYouSure.setTitle("Are You Sure You Want to Edit This Module?");
                    areYouSure.setMessage(modules.get(position).getModuleName());

                    areYouSure.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener()
                            {
                                //since we're using inner class, need to get position and sore in local variable for onClick, or it will ask for final int
                                int rowPosition = position;

                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Intent toUpdateModuleIntent = new Intent(ModuleListActivity.this, SubmitModuleActivity.class);
                                    toUpdateModuleIntent.putExtra("type", 2);
                                    toUpdateModuleIntent.putExtra("moduleName", modules.get(position).getModuleName());
                                    startActivity(toUpdateModuleIntent);
                                }
                            });

                    areYouSure.setNegativeButton("No",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog dialog = areYouSure.create();
                    dialog.show();
                }
            }
        });*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        this.toolbarMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // action with ID action_refresh was selected
            case R.id.menu_add:
                break;
            case R.id.menu_edit:
                //to change icon colours based on modes, while also changing modes
                if (mode == 0 || mode == 2)
                {
                    toolbarMenu.getItem(1).setIcon(R.drawable.ic_create_black_24dp);
                    toolbarMenu.getItem(2).setIcon(R.drawable.ic_delete_white_24dp);
                    mode = 1;
                }
                else if (mode == 1)
                {
                    toolbarMenu.getItem(1).setIcon(R.drawable.ic_create_white_24dp);
                    mode = 0;
                }
                break;
            case R.id.menu_delete:
                if (mode == 0 || mode == 1)
                {
                    toolbarMenu.getItem(2).setIcon(R.drawable.ic_delete_black_24dp);
                    toolbarMenu.getItem(1).setIcon(R.drawable.ic_create_white_24dp);
                    mode = 2;
                }
                else if (mode == 2)
                {
                    toolbarMenu.getItem(2).setIcon(R.drawable.ic_delete_white_24dp);
                    mode = 0;
                }
                break;
            default:
                break;
        }

        return true;
    }

    /*public void populateMarksFromCursor(Cursor c)
    {
        while(c.moveToNext())
        {
            Mark mark = new Mark(c.getString(0), c.getFloat(1), c.getFloat(2));
            marks.add(mark);
        }
    }*/

    @Override
    protected void onRestart()
    {
        super.onRestart();
        //will reload the activity
        recreate();
    }
}
