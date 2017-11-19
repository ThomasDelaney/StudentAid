package com.example.teohe.studentaid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ModuleListActivity extends AppCompatActivity
{
    //for editing toolbar images at runtime
    Menu toolbarMenu;

    ArrayList<Module> modules;
    DatabaseManager databaseManager;
    ListView moduleListView;

    //mode for what to do when modules are clicked
    //0 if default mode (click to see marks)
    //1 if edit mode (click to edit)
    //2 if delete mode (click to delete)
    int mode = 0;

    //true if list is empty, false if not, used to make sure that a user cant edit or delete the 'empty row' which tells the user the list is empty
    boolean empty = false;

    private class ModuleAdapter extends ArrayAdapter<Module>
    {
        private ModuleAdapter(Context context, int rowLayoutId, ArrayList<Module> myArrayData)
        {
            super(context, rowLayoutId, myArrayData);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View row = convertView;

            if (row == null)
            {
                LayoutInflater inflater = getLayoutInflater();
                row = inflater.inflate(R.layout.module_row, parent, false);
            }

            TextView moduleName = (TextView) row.findViewById(R.id.moduleNameRow);
            TextView moduleWorth = (TextView) row.findViewById(R.id.moduleWorthRow);

            Module currentModule = modules.get(position);
            String moduleWorthStr;

            moduleName.setText(currentModule.getModuleName());

            if (currentModule.getModuleWorth() == 101)
            {
                moduleWorthStr = "";
                //set ems to 11 so the name fills up the screen rather than wraps at 6 ems normally
                moduleName.setEms(11);
            }
            else
            {
                moduleWorthStr = Integer.toString(100 - currentModule.getModuleWorth()) + "/" + currentModule.getModuleWorth();
                //set ems to 6 so the name wraps once it reaches near the module worth
                moduleName.setEms(6);
            }

            moduleWorth.setText(moduleWorthStr);

            return row;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_module_list);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_calculator);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Module List");
        actionBar.show();

        moduleListView = (ListView)findViewById(R.id.moduleList);

        databaseManager = new DatabaseManager(getApplicationContext());

        modules = new ArrayList<Module>();

        databaseManager.open();
        Cursor moduleCursor = databaseManager.getModules();

        //check if list is empty
        populateModulesFromCursor(moduleCursor);
        databaseManager.close();

        if (modules.isEmpty())
        {
            //add CA as 101, which will be no way enterable by the user cause 100 will be the max, this is used to make the worth column text view empty
            Module temp = new Module("Your Module List is Empty! Click the Add Button to Add Some Modules", 101);
            empty = true;
            modules.add(temp);
            moduleListView.setAdapter(new ModuleAdapter (ModuleListActivity.this, R.layout.module_row, modules));
        }
        else
        {
            empty = false;
            moduleListView.setAdapter(new ModuleAdapter(ModuleListActivity.this, R.layout.module_row, modules));
        }


        moduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id)
            {
                //if delete mode
                if (mode == 2 && !empty)
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
                else if (mode == 1 && !empty)
                {
                    AlertDialog.Builder areYouSure = new AlertDialog.Builder(ModuleListActivity.this);
                    areYouSure.setTitle("Are You Sure You Want to Edit This Module?");
                    areYouSure.setMessage(modules.get(position).getModuleName());

                    areYouSure.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener()
                            {
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
                else
                {
                    Intent toMarkList = new Intent(ModuleListActivity.this, MarkListActivity.class);
                    toMarkList.putExtra("moduleName", modules.get(position).getModuleName());
                    startActivity(toMarkList);
                }
            }
        });
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
                Intent toAddModule = new Intent(ModuleListActivity.this, SubmitModuleActivity.class);
                toAddModule.putExtra("type", 1);
                startActivity(toAddModule);
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

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.navigation_home:
                    Intent toHomeIntent = new Intent(ModuleListActivity.this, HomeActivity.class);
                    toHomeIntent.putExtra("First Time on Home Page?", 0);
                    startActivity(toHomeIntent);
                    finish();
                    return true;
                case R.id.navigation_calculator:
                    return true;
                case R.id.navigation_timetables:
                    Intent toTimetableIntent = new Intent(ModuleListActivity.this, TimetableActivity.class);
                    startActivity(toTimetableIntent);
                    finish();
                    return true;
                case R.id.navigation_food:
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    private void populateModulesFromCursor(Cursor c)
    {
        while(c.moveToNext())
        {
            Module module = new Module(c.getString(0), c.getInt(1));
            modules.add(module);
        }
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        //will reload the activity
        recreate();
    }
}
