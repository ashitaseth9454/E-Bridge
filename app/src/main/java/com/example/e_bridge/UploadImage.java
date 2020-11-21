package com.example.e_bridge;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.util.ArrayList;

public class UploadImage extends AppCompatActivity {
    CardView addGalleryImage;
    Spinner categoryOfImage;
    Button uploadImageFromGalleryButton;
    ImageView galleryImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        addGalleryImage = (CardView) findViewById(R.id.addGalleryImage);
        categoryOfImage = (Spinner) findViewById(R.id.categoryOfImage);
        uploadImageFromGalleryButton = (Button) findViewById(R.id.uploadImageFromGalleryButton);
        galleryImageView = (ImageView) findViewById(R.id.galleryImageView);

        ArrayList<String> categories = new ArrayList<String>();
        categories.add("Select Categories");
        categories.add("Cultural Event");
        categories.add("Technical Event");
        categories.add("Workshop");
        categories.add("Ceremony");
        categories.add("Webinar");
        categories.add("Conferences");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        categoryOfImage.setAdapter(arrayAdapter);

    }
}