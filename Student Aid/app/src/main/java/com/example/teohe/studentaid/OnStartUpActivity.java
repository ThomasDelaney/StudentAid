package com.example.teohe.studentaid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OnStartUpActivity extends AppCompatActivity
{
    Button imageButton;
    Button submitButton;
    EditText firstName;
    EditText lastName;
    EditText collegeName;
    EditText courseTitle;

    String imagePath;

    DatabaseManager databaseManager;
    UserChecker userChecker = new UserChecker();

    private static final int IMAGE_REQUEST_CODE = 653;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        //getApplicationContext().deleteDatabase("StudentAid");
        //SharedPreferences currentPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        //SharedPreferences.Editor editor = currentPreferences.edit();
        //editor.clear();
        //editor.apply();

        //Log.e("Is Logged In?", Integer.toString(userChecker.getUser(getApplicationContext())));

        if (userChecker.getUser(getApplicationContext()) == 1)
        {
            Intent toHomeIntent = new Intent(OnStartUpActivity.this, HomeActivity.class);
            toHomeIntent.putExtra("First Time on Home Page?", 0);
            startActivity(toHomeIntent);
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_start_up);

        databaseManager = new DatabaseManager(getApplicationContext());

        firstName = (EditText)findViewById(R.id.firstNameSetup);
        lastName = (EditText)findViewById(R.id.lastNameSetup);
        collegeName = (EditText)findViewById(R.id.collegeNameSetup);
        courseTitle = (EditText)findViewById(R.id.courseTitleSetup);

        imageButton = (Button)findViewById(R.id.imageButtonSetup);
        submitButton = (Button)findViewById(R.id.submitButtonSetup);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(photoPickerIntent, IMAGE_REQUEST_CODE);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (firstName.getText().toString().equals("") && lastName.getText().toString().equals("") &&
                        collegeName.getText().toString().equals("") && courseTitle.getText().toString().equals(""))
                {
                    Toast.makeText(OnStartUpActivity.this, "Please Enter All Fields", Toast.LENGTH_LONG).show();
                }
                else
                {

                    databaseManager.open();
                    databaseManager.setProfile(firstName.getText().toString(), lastName.getText().toString(), collegeName.getText().toString(), courseTitle.getText().toString(), imagePath);
                    databaseManager.close();

                    userChecker.setUser(getApplicationContext());

                    Intent toHomeIntent = new Intent(OnStartUpActivity.this, HomeActivity.class);
                    toHomeIntent.putExtra("First Time on Home Page?", 1);
                    startActivity(toHomeIntent);
                    finish();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data)
    {
        super.onActivityResult(reqCode, resultCode, data);

        if (resultCode == RESULT_OK)
        {
            Uri imageUri = data.getData();
            imagePath = imageUri.getPath();
            Toast.makeText(OnStartUpActivity.this, "Image Selected", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(OnStartUpActivity.this, "No Image Selected",Toast.LENGTH_SHORT).show();
        }
    }
}
