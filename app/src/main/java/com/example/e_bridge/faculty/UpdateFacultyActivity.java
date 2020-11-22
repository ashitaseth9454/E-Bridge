package com.example.e_bridge.faculty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class UpdateFacultyActivity extends AppCompatActivity {
    final int REQ = 1;
    ImageView updateFacultyImage;
    EditText updateFacultyName, updateFacultyEmail, updateFacultyPost;
    Button updateFacultyBtn, deleteFacultyBtn;
    String name, email, image, post;
    String uniqueKey, category;
    Bitmap selectedImage = null;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String downloadImageUrl;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_faculty2);
        progressDialog = new ProgressDialog(this);
        name = getIntent().getStringExtra("name");
        email = getIntent().getStringExtra("email");
        image = getIntent().getStringExtra("image");
        post = getIntent().getStringExtra("post");
        uniqueKey = getIntent().getStringExtra("key");
        category = getIntent().getStringExtra("category");

        updateFacultyName = (EditText) findViewById(R.id.updateFacultyName);

        updateFacultyImage = (ImageView) findViewById(R.id.updateFacultyImage);
        updateFacultyEmail = (EditText) findViewById(R.id.updateFacultyEmail);
        updateFacultyPost = (EditText) findViewById(R.id.updateFacultyPost);
        updateFacultyBtn = (Button) findViewById(R.id.updateFacultyBtn);
        deleteFacultyBtn = (Button) findViewById(R.id.deleteFacultyBtn);

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Faculty");
        storageReference = FirebaseStorage.getInstance().getReference();

        try {
            Picasso.get().load(image).into(updateFacultyImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateFacultyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        updateFacultyName.setText(name);
        updateFacultyPost.setText(post);
        updateFacultyEmail.setText(email);

        updateFacultyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                name = updateFacultyName.getText().toString();
                email = updateFacultyEmail.getText().toString();
                post = updateFacultyPost.getText().toString();

                checkValidation();

            }
        });

        deleteFacultyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });


    }

    private void checkValidation() {
        if (name.isEmpty()) {
            updateFacultyName.setError("Empty");
            updateFacultyName.requestFocus();
        } else if (post.isEmpty()) {
            updateFacultyPost.setError("Empty");
            updateFacultyPost.requestFocus();
        } else if (email.isEmpty()) {
            updateFacultyEmail.setError("Empty");
            updateFacultyEmail.requestFocus();
        } else if (selectedImage == null) {
            updateData(image);
        } else {
            uploadImage();
        }

    }

    private void uploadImage() {
        progressDialog.setMessage("Updating...");
        progressDialog.show();
        //compressing the image nad then storing it
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] finalImage = byteArrayOutputStream.toByteArray();
        final StorageReference filePath;
        //storing in firebase
        filePath = storageReference.child("Faculty").child(finalImage + "jpg");
        final UploadTask uploadTask = filePath.putBytes(finalImage);
        uploadTask.addOnCompleteListener(UpdateFacultyActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                                    updateData(downloadImageUrl);
                                }
                            });
                        }
                    });


                } else {
                    progressDialog.dismiss();//if pertaining ay error
                    Toast.makeText(UpdateFacultyActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    private void updateData(String image) {
        HashMap hashMap = new HashMap();
        hashMap.put("name", name);
        hashMap.put("email", email);
        hashMap.put("post", post);
        hashMap.put("image", image);
        //String uniqueKey = getIntent().getStringExtra("key");
        //String category = getIntent().getStringExtra("category");

        databaseReference.child(category).child(uniqueKey).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Toast.makeText(UpdateFacultyActivity.this, "Faculty Updated!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateFacultyActivity.this, UpdateFaculty.class);
                //adding flag so that when we press back it goesto update faculty activity instead of goij back to itself
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);


            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateFacultyActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });


    }


    private void deleteData() {
        databaseReference.child(category).child(uniqueKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override

            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(UpdateFacultyActivity.this, "Faculty Details Deleted", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(UpdateFacultyActivity.this, UpdateFaculty.class);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UpdateFacultyActivity.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
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
            updateFacultyImage.setImageBitmap(selectedImage);
        }
    }

}