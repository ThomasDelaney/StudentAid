package com.example.teohe.studentaid;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
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
    ArrayList<Module> modules;
    DatabaseManager databaseManager;
    ListView moduleListView;

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
            }
            else
            {
                moduleWorthStr = Integer.toString(100 - currentModule.getModuleWorth()) + "/" + currentModule.getModuleWorth();
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
        actionBar.setTitle("CA Calculator");
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
            modules.add(temp);
            moduleListView.setAdapter(new ModuleAdapter (ModuleListActivity.this, R.layout.module_row, modules));
        }
        else
        {
            moduleListView.setAdapter(new ModuleAdapter(ModuleListActivity.this, R.layout.module_row, modules));
        }


        moduleListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id)
            {
                Toast.makeText(getApplicationContext(), "Module LIST BOI", Toast.LENGTH_SHORT).show();
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // action with ID action_refresh was selected
            case R.id.menu_add:
                Toast.makeText(this, "Add", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_edit:
                Toast.makeText(this, "Edit", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_delete:
                Toast.makeText(this, "Delete", Toast.LENGTH_SHORT).show();
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
                    return true;
                case R.id.navigation_food:
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    public void populateModulesFromCursor(Cursor c)
    {
        while(c.moveToNext())
        {
            Module module = new Module(c.getString(0), c.getInt(1));
            modules.add(module);
        }
    }
}
