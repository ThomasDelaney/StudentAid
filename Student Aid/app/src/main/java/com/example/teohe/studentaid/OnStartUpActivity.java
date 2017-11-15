package com.example.teohe.studentaid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class OnStartUpActivity extends AppCompatActivity
{
    Button imageButton;

    String imagePath;

    private static final int IMAGE_REQUEST_CODE = 653;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_start_up);

        imageButton = (Button)findViewById(R.id.imageButtonSetup);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, IMAGE_REQUEST_CODE);
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
            Toast.makeText(OnStartUpActivity.this, imagePath, Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(OnStartUpActivity.this, "You haven't picked Image",Toast.LENGTH_LONG).show();
        }
    }
}
