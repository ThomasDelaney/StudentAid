package com.example.teohe.studentaid;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity
{
    DatabaseManager databaseManager;

    TextView fullName;
    TextView collegeName;
    TextView courseTitle;
    ImageView profileImageView;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener()
    {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item)
        {
            switch (item.getItemId())
            {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_calculator:
                    Intent toModuleListIntent = new Intent(HomeActivity.this, ModuleListActivity.class);
                    startActivity(toModuleListIntent);
                    finish();
                    return true;
                case R.id.navigation_timetables:
                    Intent toTimetableIntent = new Intent(HomeActivity.this, TimetableActivity.class);
                    toTimetableIntent.putExtra("prevDay", 0);
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

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        databaseManager = new DatabaseManager(getApplicationContext());

        databaseManager.open();
        Profile profile = databaseManager.getProfile();
        databaseManager.close();

        int firstTimeLoginCheck = 0;

        firstTimeLoginCheck = getIntent().getExtras().getInt("First Time on Home Page?");

        if (firstTimeLoginCheck == 1)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
            builder.setTitle("Welcome "+profile.getFirstName());
            builder.setMessage("This is YOUR Student Aid App, You can create your own custom timetables," +
                    " calculate CA, look for Food places and set personalised Notifications!");
            builder.setPositiveButton("Let's Begin",
                    new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                        }
                    });

            AlertDialog dialog = builder.create();
            dialog.show();
        }

        fullName = (TextView)findViewById(R.id.studentName);
        String fullNameStr = profile.getFirstName() + " " + profile.getLastName();
        fullName.setText(fullNameStr);

        collegeName = (TextView)findViewById(R.id.collegeName);
        collegeName.setText(profile.getCollegeName());

        courseTitle = (TextView)findViewById(R.id.courseTitle);
        courseTitle.setText(profile.getCourseTitle());

        profileImageView = (ImageView) findViewById(R.id.profileImage);
        profileImageView.setImageBitmap(profile.getImage());

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_home);
    }

}
