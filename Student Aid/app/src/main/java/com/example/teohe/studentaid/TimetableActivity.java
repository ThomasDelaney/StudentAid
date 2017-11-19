package com.example.teohe.studentaid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import java.util.Collections;

public class TimetableActivity extends AppCompatActivity
{
    String[] times = {"9am", "10am", "11am", "12pm", "1pm", "2pm", "3pm", "4pm", "5pm", "6pm"};
    //for editing toolbar images at runtime
    Menu toolbarMenu;
    ArrayList<Timeslot> timeslots;
    DatabaseManager databaseManager;

    ListView timeslotList;

    //mode for what to do when timeslots are clicked
    //0 if default mode
    //1 if edit mode (click to edit)
    //2 if delete mode (click to delete)
    //3 if add mode (click to add)
    int mode = 0;

    private class TimeslotAdapter extends ArrayAdapter<Timeslot>
    {
        private TimeslotAdapter(Context context, int rowLayoutId, ArrayList<Timeslot> myArrayData)
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
                row = inflater.inflate(R.layout.timeslot_row, parent, false);
            }

            TextView time = (TextView) row.findViewById(R.id.timeslotTime);
            TextView moduleName = (TextView) row.findViewById(R.id.moduleNameTimeslot);
            TextView classType = (TextView) row.findViewById(R.id.classTypeRow);
            TextView lecturerName = (TextView) row.findViewById(R.id.lecturerNameRow);
            TextView room = (TextView) row.findViewById(R.id.roomRow);

            Timeslot currentTimeslot = timeslots.get(position);

            //since the slot attribute holds the index of a time string in the strings array, we can just access it directly
            time.setText(times[currentTimeslot.getSlot()]);
            moduleName.setText(currentTimeslot.getModuleName());
            classType.setText(currentTimeslot.getClassType());

            //for colouring the class types
            if (currentTimeslot.getClassType().equals("Lecture"))
            {
                classType.setTextColor(getColor(R.color.purple));
            }
            else if (currentTimeslot.getClassType().equals("Lab"))
            {
                classType.setTextColor(getColor(R.color.red));
            }
            else
            {
                classType.setTextColor(getColor(R.color.green));
            }

            lecturerName.setText(currentTimeslot.getLecturerName());
            room.setText(currentTimeslot.getRoom());

            return row;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_timetables);

        final TabLayout tabLayout = (TabLayout)findViewById(R.id.daysOfWeekTabs);
        tabLayout.addOnTabSelectedListener(onTabSelectedListener);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Timetables");
        actionBar.show();

        timeslotList = (ListView)findViewById(R.id.timeSlotList);

        databaseManager = new DatabaseManager(getApplicationContext());

        timeslots = new ArrayList<Timeslot>();

        databaseManager.open();
        //since monday is default, we will get the timeslots for monday
        final Cursor timeslotCursor = databaseManager.getTimeslots(0);

        //check if list is empty
        populateTimeslotsFromCursor(timeslotCursor);
        databaseManager.close();

        addBlankSlots(0);

        timeslotList.setAdapter(new TimeslotAdapter(TimetableActivity.this, R.layout.timeslot_row, timeslots));

        timeslotList.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id)
            {
                //if delete mode and its not an empty timeslot, moduleName used for reference
                if (mode == 2 && !timeslots.get(position).getModuleName().equals(""))
                {
                    //get current tab, for used in the alert message to get the tab text which is a day of the week e.g Monday
                    TabLayout.Tab currentTab = tabLayout.getTabAt(tabLayout.getSelectedTabPosition());

                    AlertDialog.Builder areYouSure = new AlertDialog.Builder(TimetableActivity.this);
                    areYouSure.setTitle("Are You Sure You Want to Delete This Timeslot?");
                    areYouSure.setMessage(timeslots.get(position).getModuleName()+" "+timeslots.get(position).getClassType()+" at "+times[timeslots.get(position).getSlot()]+" "+currentTab.getText());

                    areYouSure.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener()
                            {
                                //since we're using inner class, need to get position and sore in local variable for onClick, or it will ask for final int
                                int rowPosition = position;

                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    databaseManager.open();
                                    long deleteResult = databaseManager.deleteTimeslot(timeslots.get(rowPosition).getDayOfTheWeek(), timeslots.get(rowPosition).getSlot());
                                    databaseManager.close();

                                    dialog.cancel();

                                    if (deleteResult == -1)
                                    {
                                        Toast.makeText(getApplicationContext(), "Error Deleting Timeslot", Toast.LENGTH_SHORT).show();
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
                //if edit mode and its not an empty timeslot, moduleName used for reference
                else if (mode == 1 && !timeslots.get(position).getModuleName().equals(""))
                {
                    //get current tab, for used in the alert message to get the tab text which is a day of the week e.g Monday, //final because it must be accessed by inner class and will not change
                    final TabLayout.Tab currentTab = tabLayout.getTabAt(tabLayout.getSelectedTabPosition());

                    AlertDialog.Builder areYouSure = new AlertDialog.Builder(TimetableActivity.this);
                    areYouSure.setTitle("Are You Sure You Want to Edit This Timeslot?");
                    areYouSure.setMessage(timeslots.get(position).getModuleName()+" "+timeslots.get(position).getClassType()+" at "+times[timeslots.get(position).getSlot()]+" "+currentTab.getText());

                    areYouSure.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Intent toUpdateTimeslotIntent = new Intent(TimetableActivity.this, SubmitTimeslotActivity.class);
                                    toUpdateTimeslotIntent.putExtra("type", 2);
                                    toUpdateTimeslotIntent.putExtra("dayInt", timeslots.get(position).getDayOfTheWeek());
                                    toUpdateTimeslotIntent.putExtra("dayString", currentTab.getText());
                                    toUpdateTimeslotIntent.putExtra("slotInt", timeslots.get(position).getSlot());
                                    toUpdateTimeslotIntent.putExtra("slotString", times[timeslots.get(position).getSlot()]);
                                    startActivity(toUpdateTimeslotIntent);
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
                //if add mode and IS an empty timeslot, moduleName used for reference
                else if (mode == 3 && timeslots.get(position).getModuleName().equals(""))
                {
                    TabLayout.Tab currentTab = tabLayout.getTabAt(tabLayout.getSelectedTabPosition());

                    Intent toAddTimeslotIntent = new Intent(TimetableActivity.this, SubmitTimeslotActivity.class);
                    toAddTimeslotIntent.putExtra("type", 1);
                    toAddTimeslotIntent.putExtra("dayInt", timeslots.get(position).getDayOfTheWeek());
                    toAddTimeslotIntent.putExtra("dayString", currentTab.getText());
                    toAddTimeslotIntent.putExtra("slotInt", timeslots.get(position).getSlot());
                    toAddTimeslotIntent.putExtra("slotString", times[timeslots.get(position).getSlot()]);
                    startActivity(toAddTimeslotIntent);
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
                if (mode == 1 || mode == 2 || mode == 0)
                {
                    toolbarMenu.getItem(0).setIcon(R.drawable.ic_add_box_black_24dp);
                    toolbarMenu.getItem(1).setIcon(R.drawable.ic_create_white_24dp);
                    toolbarMenu.getItem(2).setIcon(R.drawable.ic_delete_white_24dp);
                    mode = 3;
                }
                else if (mode == 3)
                {
                    toolbarMenu.getItem(0).setIcon(R.drawable.ic_add_box_white_24dp);
                    mode = 0;
                }
                break;
            case R.id.menu_edit:
                //to change icon colours based on modes, while also changing modes
                if (mode == 0 || mode == 2 || mode == 3)
                {
                    toolbarMenu.getItem(1).setIcon(R.drawable.ic_create_black_24dp);
                    toolbarMenu.getItem(2).setIcon(R.drawable.ic_delete_white_24dp);
                    toolbarMenu.getItem(0).setIcon(R.drawable.ic_add_box_white_24dp);
                    mode = 1;
                }
                else if (mode == 1)
                {
                    toolbarMenu.getItem(1).setIcon(R.drawable.ic_create_white_24dp);
                    mode = 0;
                }
                break;
            case R.id.menu_delete:
                if (mode == 0 || mode == 1 || mode == 3)
                {
                    toolbarMenu.getItem(2).setIcon(R.drawable.ic_delete_black_24dp);
                    toolbarMenu.getItem(1).setIcon(R.drawable.ic_create_white_24dp);
                    toolbarMenu.getItem(0).setIcon(R.drawable.ic_add_box_white_24dp);
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
                    Intent toHomeIntent = new Intent(TimetableActivity.this, HomeActivity.class);
                    toHomeIntent.putExtra("First Time on Home Page?", 0);
                    startActivity(toHomeIntent);
                    finish();
                    return true;
                case R.id.navigation_calculator:
                    Intent toCalculatorIntent = new Intent(TimetableActivity.this, ModuleListActivity.class);
                    startActivity(toCalculatorIntent);
                    finish();
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

    private TabLayout.OnTabSelectedListener onTabSelectedListener = new TabLayout.OnTabSelectedListener()
    {
        @Override
        public void onTabSelected(TabLayout.Tab tab)
        {
            resetTimeslotListByDay(tab.getPosition());
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private void populateTimeslotsFromCursor(Cursor c)
    {
        while(c.moveToNext())
        {
            Log.e("timeslot name", c.getString(0));
            Timeslot timeslot = new Timeslot(c.getString(0), c.getString(1), c.getString(2), c.getString(3), c.getInt(4), c.getInt(5));
            timeslots.add(timeslot);
        }
    }

    //method with algorithm which goes through every of the week index (0-4) where 0 is Monday
    //it then goes through all the timeslots that have already been gathered from the database and checks if it already exists in the module list
    //if it doesnt then we add a blank timeslot for that day and slot (time)
    private void addBlankSlots(int dayOfTheWeek)
    {
        for (int i = 0; i < times.length; i++)
        {
            //if exists then it doesn't matter, if it doesn't then we need to add a blank timeslot for that slot (time)
            boolean exists = false;

            for (Timeslot timeslot : timeslots)
            {
                if (timeslot.getSlot() == i)
                {
                    exists = true;
                    break;
                }
            }

            if(!exists)
            {
                Timeslot blankSlot = new Timeslot("", "", "", "", dayOfTheWeek, i);
                timeslots.add(blankSlot);
            }
        }

        Collections.sort(timeslots, new TimeslotComparator());
    }

    private void resetTimeslotListByDay(int dayOfTheWeek)
    {
        timeslots.clear();

        TimeslotAdapter currentAdapter = (TimeslotAdapter)timeslotList.getAdapter();
        currentAdapter.clear();

        databaseManager.open();

        Cursor timeslotCursor = databaseManager.getTimeslots(dayOfTheWeek);
        populateTimeslotsFromCursor(timeslotCursor);

        databaseManager.close();

        addBlankSlots(dayOfTheWeek);

        timeslotList.setAdapter(new TimeslotAdapter(TimetableActivity.this, R.layout.timeslot_row, timeslots));
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        //will reload the activity
        recreate();
    }
}
