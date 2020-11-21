package com.example.e_bridge.faculty;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.e_bridge.R;

public class AddFaculty extends AppCompatActivity {
    ImageView addFacultyImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_faculty);
        addFacultyImage = (ImageView) findViewById(R.id.addFacultyImage);

    }
}