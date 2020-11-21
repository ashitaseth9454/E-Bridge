package com.example.e_bridge;

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
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class UploadImage extends AppCompatActivity {
    final int REQ = 1;
    CardView addGalleryImage;
    Spinner categoryOfImage;
    Button uploadImageFromGalleryButton;
    ImageView galleryImageView;
    Bitmap selectedImage;


    String downloadImageURL = "";//if no image is uploaded then it will send empty String
    //making a progress dialog
    ProgressDialog progressDialog;
    private DatabaseReference databaseReference;

    String categorySelected;
    private StorageReference storageReference; // so they cannot be accessed by other classes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        addGalleryImage = (CardView) findViewById(R.id.addGalleryImage);
        categoryOfImage = (Spinner) findViewById(R.id.categoryOfImage);
        uploadImageFromGalleryButton = (Button) findViewById(R.id.uploadImageFromGalleryButton);
        galleryImageView = (ImageView) findViewById(R.id.galleryImageView);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("gallery");
        storageReference = FirebaseStorage.getInstance().getReference().child("gallery");//because we are working on gallery


        final ArrayList<String> categories = new ArrayList<String>();
        categories.add("Select Categories");
        categories.add("Cultural Event");
        categories.add("Technical Event");
        categories.add("Workshop");
        categories.add("Ceremony");
        categories.add("Webinar");
        categories.add("Conferences");
        categories.add("Other Events");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        categoryOfImage.setAdapter(arrayAdapter);
        categorySelected = categories.get(0);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
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
                //checking if image is uploaded or not
                if (selectedImage == null) {
                    Toast.makeText(UploadImage.this, "Please Upload Image!", Toast.LENGTH_SHORT).show();
                } else if (categorySelected.equals(categories.get(0))) {
                    Toast.makeText(UploadImage.this, "Please Select Category!", Toast.LENGTH_SHORT).show();
                } else {
                    progressDialog.show();
                    //calling uploadImage method just like we did in uploadNotice class
                    uploadImage();

                }

            }
        });

    }

    //using the code from uploadingNotices

    private void uploadImage() {

        //compressing the image and then storing it
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] finalImage = byteArrayOutputStream.toByteArray();
        final StorageReference filePath;
        //storing in firebase
        filePath = storageReference.child(finalImage + "jpg");//hamne gallery already set kiya hai in onCreate methodso we don't have to add .chile("gallery")

        final UploadTask uploadTask = filePath.putBytes(finalImage);
        uploadTask.addOnCompleteListener(UploadImage.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadImageURL = String.valueOf(uri);
                                    uploadData();
                                }
                            });
                        }
                    });


                } else {
                    progressDialog.dismiss();//if pertaining ay error
                    Toast.makeText(UploadImage.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void uploadData() {
        databaseReference = databaseReference.child(categorySelected);//storing in the category that the user has selected
        //defining uniqueKey
        final String uniqueKey = databaseReference.push().getKey();

        databaseReference.child(uniqueKey).setValue(downloadImageURL).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(UploadImage.this, "Image Uploaded Successfully!", Toast.LENGTH_SHORT).show();


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UploadImage.this, "Upload Failed!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void openGallery() {

        Intent pickImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickImage, REQ);


    }

    //taking this code form Upload Notice class
    //setting Image to the Image View
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