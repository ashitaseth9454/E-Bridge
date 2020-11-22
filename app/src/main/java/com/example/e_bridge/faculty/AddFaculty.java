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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.e_bridge.R;
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
    String name, email, post, downloadImageUrl = "";

    DatabaseReference databaseReference, dbref;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_faculty);
        addFacultyImage = (ImageView) findViewById(R.id.addFacultyImage);
        addFacultyButton = (Button) findViewById(R.id.addFacultyButton);
        addFacultyName = (EditText) findViewById(R.id.addFacultyName);
        addFacultyEmail = (EditText) findViewById(R.id.addFacultyEmail);
        addFacultyPost = (EditText) findViewById(R.id.addFacultyPost);
        addFacultyCategory = (Spinner) findViewById(R.id.addFacultyCategory);
        progressDialog = new ProgressDialog(this);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Faculty");
        storageReference = FirebaseStorage.getInstance().getReference();

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
        addFacultyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();//checking if any fields is null or unfilled


            }
        });


    }

    private void checkValidation() {
        name = addFacultyName.getText().toString();
        email = addFacultyEmail.getText().toString();
        post = addFacultyPost.getText().toString();

        if (name.isEmpty()) {
            addFacultyName.setError("Empty!");
            addFacultyName.requestFocus();

        } else if (email.isEmpty()) {
            addFacultyEmail.setError("Empty!");
            addFacultyEmail.requestFocus();

        } else if (post.isEmpty()) {
            addFacultyPost.setError("Empty!");
            addFacultyPost.requestFocus();

        } else if (categorySelected.equals("Select Category")) {
            Toast.makeText(this, "Select the Category!", Toast.LENGTH_SHORT).show();

        } else if (selectedImage == null) {
            progressDialog.setMessage("Uploading...");
            progressDialog.show();
            insertData();

        } else {
            progressDialog.setMessage("Uploading...");
            progressDialog.show();
            uploadImage();

        }
    }

    private void uploadImage() {
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        //compressing the image nad then storing it
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] finalImage = byteArrayOutputStream.toByteArray();
        final StorageReference filePath;
        //storing in firebase
        filePath = storageReference.child("Faculty").child(finalImage + "jpg");
        final UploadTask uploadTask = filePath.putBytes(finalImage);
        uploadTask.addOnCompleteListener(AddFaculty.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()) {
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadImageUrl = String.valueOf(uri);
                                    insertData();
                                }
                            });
                        }
                    });


                } else {
                    progressDialog.dismiss();//if pertaining ay error
                    Toast.makeText(AddFaculty.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void insertData() {
//teacher>category(CSE,ME...)>unique key>data of faculty
        dbref = databaseReference.child(categorySelected);
        final String uniqueKey = dbref.push().getKey();


        //passing arguments to the notice data class
        FacultyData facultyData = new FacultyData(name, email, post, downloadImageUrl, uniqueKey);
        /*title-notice title
        downloadImageUrl= Bitmao image compressed and converted to url
        date= dat
        time=time
        uniqueKey =key
        * */
        //passsing and storing data in firebase
        dbref.child(uniqueKey).setValue(facultyData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(AddFaculty.this, "Faculty Details Uploaded", Toast.LENGTH_SHORT).show();
                addFacultyEmail.setText("");
                addFacultyName.setText("");
                addFacultyPost.setText("");


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(AddFaculty.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
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