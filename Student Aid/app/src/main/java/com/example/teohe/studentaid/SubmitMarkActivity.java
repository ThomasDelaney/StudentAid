package com.example.teohe.studentaid;

import android.content.DialogInterface;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class SubmitMarkActivity extends AppCompatActivity
{
    DatabaseManager databaseManager;
    EditText markName;
    SeekBar worthBar;
    TextView worthView;
    SeekBar scoreBar;
    TextView scoreView;
    Button submitButton;

    String moduleName;
    int moduleWorth;

    int type;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_mark);

        type = getIntent().getExtras().getInt("type");
        moduleName = getIntent().getExtras().getString("moduleName");
        moduleWorth = getIntent().getExtras().getInt("moduleWorth");

        databaseManager = new DatabaseManager(getApplication());
        markName = (EditText) findViewById(R.id.markNameSubmit);

        worthBar = (SeekBar)findViewById(R.id.seekBarMarkWorth);
        worthBar.setMax(moduleWorth);
        worthView = (TextView)findViewById(R.id.seekBarWorthMarkView);

        scoreBar = (SeekBar)findViewById(R.id.seekBarMarkScore);
        scoreView = (TextView)findViewById(R.id.seekBarScoreMarkView);

        submitButton = (Button)findViewById(R.id.submitMarkButton);

        worthView.setText(worthBar.getProgress()+"/"+worthBar.getMax()+"% going towards the Remaining Continuous Assessment");
        scoreView.setText("You Scored: "+scoreBar.getProgress()+"/"+scoreBar.getMax()+"%");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(moduleName);
        actionBar.show();

        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String makrNameStr = markName.getText().toString();

                if(makrNameStr.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please Enter Mark Name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    databaseManager.open();

                    if (type == 1)
                    {
                        addMark(markName.getText().toString());
                    }
                    else if (type == 2)
                    {
                        updateMark(getIntent().getExtras().getString("markName"), markName.getText().toString());
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
                worthView.setText(worthBar.getProgress()+"/"+worthBar.getMax()+"% going towards the Remaining Continuous Assessment");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        scoreBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b)
            {
                scoreView.setText("You Scored: "+scoreBar.getProgress()+"/"+scoreBar.getMax()+"%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void addMark(String markNameStr)
    {
        long successValue = databaseManager.insertMark(moduleName, markNameStr, worthBar.getProgress(), scoreBar.getProgress());

        AlertDialog.Builder isMarkAddedAlert = new AlertDialog.Builder(SubmitMarkActivity.this);
        if (successValue == -1) {
            isMarkAddedAlert.setTitle("An Error Occurred Adding the Mark!");
        } else {
            isMarkAddedAlert.setTitle("Mark Added!");
        }

        isMarkAddedAlert.setPositiveButton("Okay",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog dialog = isMarkAddedAlert.create();
        dialog.show();
    }

    private void updateMark(String originalName, String newName)
    {
        long successValue = databaseManager.updateMark(originalName, newName, worthBar.getProgress(), scoreBar.getProgress());

        AlertDialog.Builder isMarkUpdatedAlert = new AlertDialog.Builder(SubmitMarkActivity.this);
        if (successValue == -1) {
            isMarkUpdatedAlert.setTitle("An Error Occurred Updating the Mark!");
        } else {
            isMarkUpdatedAlert.setTitle("Mark Updated!");
        }

        isMarkUpdatedAlert.setPositiveButton("Okay",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog dialog = isMarkUpdatedAlert.create();
        dialog.show();
    }
}
