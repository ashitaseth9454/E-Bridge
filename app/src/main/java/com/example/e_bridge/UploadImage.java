package com.example.e_bridge;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.IOException;
import java.util.ArrayList;

public class UploadImage extends AppCompatActivity {
    final int REQ = 1;
    CardView addGalleryImage;
    Spinner categoryOfImage;
    Button uploadImageFromGalleryButton;
    ImageView galleryImageView;
    Bitmap selectedImage;

    String categorySelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        addGalleryImage = (CardView) findViewById(R.id.addGalleryImage);
        categoryOfImage = (Spinner) findViewById(R.id.categoryOfImage);
        uploadImageFromGalleryButton = (Button) findViewById(R.id.uploadImageFromGalleryButton);
        galleryImageView = (ImageView) findViewById(R.id.galleryImageView);


        final ArrayList<String> categories = new ArrayList<String>();
        categories.add("Select Categories");
        categories.add("Cultural Event");
        categories.add("Technical Event");
        categories.add("Workshop");
        categories.add("Ceremony");
        categories.add("Webinar");
        categories.add("Conferences");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        categoryOfImage.setAdapter(arrayAdapter);
        categorySelected = categories.get(0);
        categoryOfImage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categorySelected = categoryOfImage.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                categorySelected = categories.get(0);
            }
        });


        addGalleryImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


        uploadImageFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void openGallery() {

        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage, REQ);


    }

    //taking this code form Upload Notice class
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                selectedImage = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            galleryImageView.setImageBitmap(selectedImage);
        }
    }
}