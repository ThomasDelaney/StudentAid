package com.example.teohe.studentaid;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class SubmitTimeslotActivity extends AppCompatActivity
{
    DatabaseManager databaseManager;
    TextView titleText;
    Spinner moduleSpinner;
    Spinner classTypeSpinner;
    EditText lecturerName;
    EditText room;
    Button submitButton;

    int type;
    int dayInt;
    String dayString;
    int slotInt;
    String slotString;

    ArrayList<String> moduleNames = new ArrayList<String>();

    private class MouduleSpinnerAdapter extends ArrayAdapter<String>
    {
        private MouduleSpinnerAdapter(Context context, int rowLayoutId, ArrayList<String> myArrayData)
        {
            super(context, rowLayoutId, myArrayData);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            TextView moduleName = new TextView(getApplicationContext());

            moduleName.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            moduleName.setSingleLine(true);
            moduleName.setTextColor(getColor(R.color.black));
            moduleName.setText(moduleNames.get(position));

            return moduleName;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_timeslot);

        type = getIntent().getExtras().getInt("type");
        dayInt = getIntent().getExtras().getInt("dayInt");
        dayString = getIntent().getExtras().getString("dayString");
        slotInt = getIntent().getExtras().getInt("slotInt");
        slotString = getIntent().getExtras().getString("slotString");

        databaseManager = new DatabaseManager(getApplication());

        titleText = (TextView)findViewById(R.id.submitModuleTitle);
        moduleSpinner = (Spinner) findViewById(R.id.moduleNameSpinner);
        classTypeSpinner = (Spinner)findViewById(R.id.classTypeSpinner);
        lecturerName = (EditText) findViewById(R.id.lecturerNameSubmit);
        room = (EditText) findViewById(R.id.roomSubmit);
        submitButton = (Button)findViewById(R.id.submitButtonTimeslot);

        titleText.setText("Submit a Timeslot for "+slotString+" "+dayString);

        //get all the module names
        databaseManager.open();
        Cursor modules = databaseManager.getModules();

        while(modules.moveToNext())
        {
            moduleNames.add(modules.getString(0));
        }

        databaseManager.close();

        moduleSpinner.setAdapter(new MouduleSpinnerAdapter(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, moduleNames));

        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String moduleNameStr = moduleSpinner.getSelectedItem().toString();
                String classTypeStr = classTypeSpinner.getSelectedItem().toString();
                String lecturerNameStr = lecturerName.getText().toString();
                String roomStr = room.getText().toString();

                if(lecturerNameStr.equals("") || roomStr.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please Enter All Fields", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    databaseManager.open();

                    if (type == 1 )
                    {
                        addTimeslot(moduleNameStr, classTypeStr, lecturerNameStr, roomStr);
                    }
                    //if updating and the current module name is the same as the module name OR the module name is not the same name as any other module, then update
                    else if (type == 2)
                    {
                        updateModule(moduleNameStr, classTypeStr, lecturerNameStr, roomStr);
                    }

                    databaseManager.close();
                }
            }
        });
    }


    private void addTimeslot(String moduleNameStr, String classTypeStr, String lecturerNameStr, String roomStr)
    {
        Log.e("real shit?", moduleNameStr+ " "+classTypeStr+" "+lecturerNameStr+" "+roomStr+" "+Integer.toString(dayInt)+" "+Integer.toString(slotInt));

        long successValue = databaseManager.insertTimeslot(moduleNameStr, classTypeStr, lecturerNameStr, roomStr, dayInt, slotInt);

        AlertDialog.Builder isTimeSlotAddedAlert = new AlertDialog.Builder(SubmitTimeslotActivity.this);
        if (successValue == -1) {
            isTimeSlotAddedAlert.setTitle("An Error Occurred Adding the Timeslot!");
        } else {
            isTimeSlotAddedAlert.setTitle("Timeslot Added!");
        }

        isTimeSlotAddedAlert.setPositiveButton("Okay",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog dialog = isTimeSlotAddedAlert.create();
        dialog.show();
    }

    private void updateModule(String moduleNameStr, String classTypeStr, String lecturerNameStr, String roomStr)
    {
        long successValue = databaseManager.updateTimeslot(moduleNameStr, classTypeStr, lecturerNameStr, roomStr, dayInt, slotInt);

        AlertDialog.Builder isTimeslotUpdatedAlert = new AlertDialog.Builder(SubmitTimeslotActivity.this);
        if (successValue == -1) {
            isTimeslotUpdatedAlert.setTitle("An Error Occurred Updating the Timeslot!");
        } else {
            isTimeslotUpdatedAlert.setTitle("Timeslot Updated!");
        }

        isTimeslotUpdatedAlert.setPositiveButton("Okay",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog dialog = isTimeslotUpdatedAlert.create();
        dialog.show();
    }
}
