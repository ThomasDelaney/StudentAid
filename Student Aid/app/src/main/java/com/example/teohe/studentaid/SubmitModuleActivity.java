package com.example.teohe.studentaid;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class SubmitModuleActivity extends AppCompatActivity
{
    DatabaseManager databaseManager;
    EditText moduleName;
    SeekBar worthBar;
    TextView worthView;
    Button submitButton;
    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_module);

        type = getIntent().getExtras().getInt("type");

        databaseManager = new DatabaseManager(getApplication());
        moduleName = (EditText) findViewById(R.id.moduleNameSubmitAdd);
        worthBar = (SeekBar)findViewById(R.id.seekBar);
        worthView = (TextView)findViewById(R.id.seekBarWorth);
        submitButton = (Button)findViewById(R.id.submitModuleButtonAdd);

        worthView.setText(worthBar.getProgress()+"/"+worthBar.getMax()+"% going towards Continuous Assessment");

        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String moduleNameStr = moduleName.getText().toString();

                if(moduleNameStr.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please Enter Module Name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    databaseManager.open();

                    if (type == 1)
                    {
                        addModule(moduleName.getText().toString());
                    }
                    else if (type == 2)
                    {
                        updateModule(getIntent().getExtras().getString("moduleName"), moduleNameStr);
                    }

                    databaseManager.close();
                }
            }
        });

        worthBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                worthView.setText(worthBar.getProgress()+"/"+worthBar.getMax()+"% going towards Continuous Assessment");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void addModule(String moduleNameStr)
    {
        long successValue = databaseManager.insertModule(moduleNameStr, worthBar.getProgress());

        AlertDialog.Builder isModuleAddedAlert = new AlertDialog.Builder(SubmitModuleActivity.this);
        if (successValue == -1) {
            isModuleAddedAlert.setTitle("An Error Occurred Adding the Module!");
        } else {
            isModuleAddedAlert.setTitle("Module Added!");
        }

        isModuleAddedAlert.setPositiveButton("Okay",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog dialog = isModuleAddedAlert.create();
        dialog.show();
    }

    private void updateModule(String originalName, String newName)
    {
        long successValue = databaseManager.updateModule(originalName, newName, worthBar.getProgress());

        AlertDialog.Builder isModuleUpdatedAlert = new AlertDialog.Builder(SubmitModuleActivity.this);
        if (successValue == -1) {
            isModuleUpdatedAlert.setTitle("An Error Occurred Updating the Module!");
        } else {
            isModuleUpdatedAlert.setTitle("Module Updated!");
        }

        isModuleUpdatedAlert.setPositiveButton("Okay",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog dialog = isModuleUpdatedAlert.create();
        dialog.show();
    }
}
