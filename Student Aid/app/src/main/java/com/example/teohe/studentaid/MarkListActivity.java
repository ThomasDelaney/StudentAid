package com.example.teohe.studentaid;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
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
    Module module;

    //mode for what to do when marks are clicked
    //0 if default mode
    //1 if edit mode (click to edit)
    //2 if delete mode (click to delete)
    //3 if list is empty
    int mode = 0;

    //true if list is empty, false if not, used to make sure that a user cant edit or delete the 'empty row' which tells the user the list is empty
    boolean empty = false;

    private class MarkAdapter extends ArrayAdapter<Mark>
    {
        private MarkAdapter(Context context, int rowLayoutId, ArrayList<Mark> myArrayData)
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
                row = inflater.inflate(R.layout.mark_row, parent, false);
            }

            TextView markName = (TextView) row.findViewById(R.id.markNameRow);
            TextView markWorth = (TextView) row.findViewById(R.id.markWorthRow);
            TextView markScore = (TextView) row.findViewById(R.id.markScoreRow);

            Mark currentMark = marks.get(position);

            markName.setText(currentMark.getMarkName());

            if (currentMark.getMarkWorth() == 101)
            {
                markWorth.setText("");
                markScore.setText("");
                //set ems to 11 so the name fills up the screen rather than wraps at 6 ems normally
                markName.setEms(11);
            }
            else
            {
                markWorth.setText(Integer.toString(currentMark.getMarkWorth())+"%");
                markScore.setText(Integer.toString(currentMark.getMarkScore())+"%");
                //set ems to 6 so the name wraps once it reaches near the mark worth
                markName.setEms(6);
            }

            return row;
        }
    }

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

        databaseManager.open();
        Cursor markCursor = databaseManager.getMarks(moduleName);
        Cursor moduleCursor = databaseManager.getModule(moduleName);

        if (moduleCursor.moveToNext())
        {
            module = new Module(moduleName, moduleCursor.getInt(1));
        }
        else
        {
            module = null;
        }

        //check if list is empty
        populateMarksFromCursor(markCursor);
        databaseManager.close();

        if (marks.isEmpty())
        {
            //add CA as 101, which will be no way enterable by the user cause 100 will be the max, this is used to make the worth column text view empty
            Mark temp = new Mark(module, "Your Mark List is Empty! Click the Add Button to Add Some Marks", 101, 101);
            empty = true;
            marks.add(temp);
            markListView.setAdapter(new MarkListActivity.MarkAdapter(MarkListActivity.this, R.layout.mark_row, marks));
        }
        else
        {
            empty = false;
            markListView.setAdapter(new MarkListActivity.MarkAdapter(MarkListActivity.this, R.layout.mark_row, marks));
        }


        markListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id)
            {
                //if delete mode
                if (mode == 2 && !empty)
                {
                    AlertDialog.Builder areYouSure = new AlertDialog.Builder(MarkListActivity.this);
                    areYouSure.setTitle("Are You Sure You Want to Delete This Mark?");
                    areYouSure.setMessage(marks.get(position).getMarkName());

                    areYouSure.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener()
                            {
                                //since we're using inner class, need to get position and sore in local variable for onClick, or it will ask for final int
                                int rowPosition = position;

                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    databaseManager.open();
                                    long deleteResult = databaseManager.deleteMark(marks.get(rowPosition).getMarkName());
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
                    AlertDialog.Builder areYouSure = new AlertDialog.Builder(MarkListActivity.this);
                    areYouSure.setTitle("Are You Sure You Want to Edit This Mark?");
                    areYouSure.setMessage(marks.get(position).getMarkName());

                    areYouSure.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener()
                            {
                                //since we're using inner class, need to get position and sore in local variable for onClick, or it will ask for final int
                                int rowPosition = position;

                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    Intent toUpdateMarkIntent = new Intent(MarkListActivity.this, SubmitMarkActivity.class);
                                    toUpdateMarkIntent.putExtra("type", 2);
                                    toUpdateMarkIntent.putExtra("markName", marks.get(position).getMarkName());
                                    toUpdateMarkIntent.putExtra("moduleName", marks.get(position).getMarkModuleName());
                                    toUpdateMarkIntent.putExtra("moduleWorth", getRemainingCA()+marks.get(position).getMarkWorth());
                                    startActivity(toUpdateMarkIntent);
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
                if (getRemainingCA() != 0)
                {
                    Intent toAddMark = new Intent(MarkListActivity.this, SubmitMarkActivity.class);
                    toAddMark.putExtra("type", 1);
                    toAddMark.putExtra("moduleName", moduleName);
                    toAddMark.putExtra("moduleWorth", getRemainingCA());
                    startActivity(toAddMark);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "There is no Remaining CA to be Allocated", Toast.LENGTH_LONG).show();
                }
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

    private void populateMarksFromCursor(Cursor c)
    {
        while(c.moveToNext())
        {
            Mark mark = new Mark(module, c.getString(0), c.getInt(1), c.getInt(2));
            marks.add(mark);
        }
    }

    @Override
    protected void onRestart()
    {
        super.onRestart();
        //will reload the activity
        recreate();
    }

    private int getRemainingCA()
    {
        int remainingCA = module.getModuleWorth();

        for (Mark mark : marks)
        {
            //if mark worth is 101 then we can skip because its only a placeholder key for identifying if there is no marks for the module
            if (mark.getMarkWorth() == 101)
            {
                continue;
            }
            remainingCA -= mark.getMarkWorth();
        }

        return remainingCA;
    }
}
