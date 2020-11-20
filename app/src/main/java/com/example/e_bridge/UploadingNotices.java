package com.example.e_bridge;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.IOException;

public class UploadingNotices extends AppCompatActivity {
    private final int REQ = 1;
    CardView selectImage;
    ImageView imageView;
    Bitmap image;
    EditText noticeTitle;
    Button uploadNoticeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploading_notices);

        selectImage = (CardView) findViewById(R.id.selectImage);
        imageView = (ImageView) findViewById(R.id.noticeImageView);
        noticeTitle = (EditText) findViewById(R.id.noticeTitle);
        uploadNoticeButton = (Button) findViewById(R.id.uploadNoticeButton);

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
                } else {
                    uploadNotice();

                }
            }
        });
    }

    //if the user have not uploaded any image (only title)
    private void uploadData() {


    }


    //the user has uploaded both image nad title
    private void uploadNotice() {


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