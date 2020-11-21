package com.example.e_bridge.faculty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.e_bridge.R;

import java.io.IOException;
import java.util.ArrayList;

public class AddFaculty extends AppCompatActivity {
    ImageView addFacultyImage;
    final int REQ = 1;
    EditText addFacultyName;
    EditText addFacultyEmail;
    EditText addFacultyPost;
    Spinner addFacultyCategory;
    Button addFacultyButton;
    ProgressDialog progressDialog;
    String categorySelected;
    Bitmap selectedImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_faculty);
        addFacultyImage = (ImageView) findViewById(R.id.addFacultyImage);
        addFacultyButton = (Button) findViewById(R.id.addFacultyButton);
        addFacultyName = (EditText) findViewById(R.id.addFacultyName);
        addFacultyEmail = (EditText) findViewById(R.id.addFacultyEmail);
        addFacultyPost = (EditText) findViewById(R.id.addFacultyPost);
        addFacultyCategory = (Spinner) findViewById(R.id.addFacutyCategory);
        progressDialog = new ProgressDialog(this);

        addFacultyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        final ArrayList<String> categories = new ArrayList<String>();
        categories.add("Select Categories");
        categories.add("B Tech CSE");
        categories.add("B Tech ECE");
        categories.add("B Tech ME");
        categories.add("B Tech CE");
        categories.add("B Tech CCV");
        categories.add("B Tech Cyber Security");
        categories.add("Other Branch");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        addFacultyCategory.setAdapter(arrayAdapter);
        categorySelected = categories.get(0);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        addFacultyCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categorySelected = addFacultyCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                categorySelected = categories.get(0);
            }
        });


    }

    private void openGallery() {

        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage, REQ);


    }

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
            addFacultyImage.setImageBitmap(selectedImage);
        }
    }

}