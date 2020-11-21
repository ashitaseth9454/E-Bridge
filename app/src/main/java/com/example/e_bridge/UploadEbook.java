package com.example.e_bridge;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class UploadEbook extends AppCompatActivity {
    //Request Code
    final int REQ = 1;
    CardView uploadPdf;
    Button uploadPdfButton;
    EditText pdfTitle;
    TextView pdfTextView;
    Uri pdfData;

    String downloadImageURL = "";//if no image is uploaded then it will send empty String
    //making a progress dialog
    ProgressDialog progressDialog;
    String categorySelected;
    private DatabaseReference databaseReference;
    private StorageReference storageReference; // so they cannot be accessed by other classes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_ebook);
        uploadPdf = (CardView) findViewById(R.id.uploadPdf);
        uploadPdfButton = (Button) findViewById(R.id.uploadPdfButton);
        pdfTextView = (TextView) findViewById(R.id.pdfTextView);
        pdfTitle = (EditText) findViewById(R.id.pdfTitle);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("gallery");
        storageReference = FirebaseStorage.getInstance().getReference().child("gallery");

        uploadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


    }


    private void openGallery() {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        //startActivity(intent);

        startActivityForResult(intent.createChooser(intent, "Select File"), REQ);


    }

    //taking this code form Upload Notice class
    //setting Image to the Image View

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ && resultCode == RESULT_OK) {
            //Uri pdfData upar define hai Globally
            pdfData = data.getData();//data taken from Intent data(Argument passed in this method)

//showing what is in out pdf data(being uploaded by the  user)
            Toast.makeText(this, "Selected Pdf" + pdfData, Toast.LENGTH_SHORT).show();


        }
    }
}