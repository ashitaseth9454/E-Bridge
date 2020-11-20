package com.example.e_bridge;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UploadingNotices extends AppCompatActivity {
    private final int REQ = 1;
    CardView selectImage;
    ImageView imageView;
    Bitmap image;
    EditText noticeTitle;
    Button uploadNoticeButton;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    String downloadImageURL = "";//if no image is uploaded then it will send empty String
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploading_notices);

        selectImage = (CardView) findViewById(R.id.selectImage);
        imageView = (ImageView) findViewById(R.id.noticeImageView);
        noticeTitle = (EditText) findViewById(R.id.noticeTitle);
        uploadNoticeButton = (Button) findViewById(R.id.uploadNoticeButton);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        selectImage.setOnClickListener(new View.OnClickListener() {
            //starting mainbhi laga skte the par hamko bas ek hi bulana hai
            @Override
            public void onClick(View view) {
                openGallery();

            }
        });
        uploadNoticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noticeTitle.getText().toString().isEmpty()) {
                    noticeTitle.setError("Empty!");
                    noticeTitle.requestFocus();
                    //if empty the it will focus on edit text view, so that the user can write the title

                }
                //checking if image is uploaded or not (we have used Bitmap)
                else if (image == null) {
                    uploadData();
                    //directly storing title
                } else {
                    uploadImage();
                    //converting(compressing) image and storing it

                }
            }
        });
    }

    //storing data on firebase
    private void uploadData() {
        databaseReference = databaseReference.child("Notice");
        final String uniqueKey = databaseReference.push().getKey();
        String title = noticeTitle.getText().toString();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yy");
        //as we use mm for minutes
        String date = currentDate.format(calForDate.getTime());//only for date as only date will be stored in dd-MM-yy format

        //for time
        Calendar calForTime = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        //a for AM and PM

        String time = currentTime.format(calForTime.getTime());

        //passing arguments to the notice data class
        NoticeData noticeData = new NoticeData(title, downloadImageURL, date, time, uniqueKey);
        /*title-notice title
        downloadImageUrl= Bitmao image compressed and converted to url
        date= dat
        time=time
        uniqueKey =key
        * */
        //passsing and storing data in firebase
        databaseReference.child(uniqueKey).setValue(noticeData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toast.makeText(UploadingNotices.this, "Notice Uploaded", Toast.LENGTH_SHORT).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(UploadingNotices.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
            }
        });


    }


    //if the user have not uploaded any image (only title)
    private void uploadImage() {
        progressDialog.setMessage("Uploading...");
        progressDialog.show();
        //compressing the image nad then storing it
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] finalImage = byteArrayOutputStream.toByteArray();
        final StorageReference filePath;
        //storing in firebase
        filePath = storageReference.child("Notice").child(finalImage + "jpg");
        final UploadTask uploadTask = filePath.putBytes(finalImage);
        uploadTask.addOnCompleteListener(UploadingNotices.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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
                    Toast.makeText(UploadingNotices.this, "Something went wrong!", Toast.LENGTH_SHORT).show();
                }
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
                image = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageView.setImageBitmap(image);
        }
    }
}