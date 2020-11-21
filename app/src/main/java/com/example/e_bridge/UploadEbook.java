package com.example.e_bridge;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

public class UploadEbook extends AppCompatActivity {
    //Request Code
    final int REQ = 1;
    CardView uploadPdf;
    Button uploadPdfButton;
    EditText pdfTitle;
    TextView pdfTextView;
    Uri pdfData;

    String title;

    String pdfName;
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

        uploadPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checking if the user has uploaded pdf and title or not
                title = pdfTitle.getText().toString();
                if (title.isEmpty()) {
                    pdfTitle.setError("Empty");
                    pdfTitle.requestFocus();

                } else if (pdfData == null) {
                    Toast.makeText(UploadEbook.this, "Please Upload the File!", Toast.LENGTH_SHORT).show();
                } else {
                    uploadPdf();
                }
            }
        });


        uploadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });


    }

    private void uploadPdf() {
        StorageReference ref = storageReference.child("pdf/" + pdfName + "-" + System.currentTimeMillis());//we want the pdf name to ube unique so we used current time
        ref.putFile(pdfData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();//using taskSnapshot to get the url
                while (!uriTask.isComplete())
                    ;// we want this loop to run till our task is finishes so we are using not of task complete
                Uri uri = uriTask.getResult();
                uploadData(String.valueOf(uri));

            }
        });


    }

    private void uploadData(String valueOf) {
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
            if (pdfData.toString().startsWith("content://")) {
                Cursor c = null;

                try {
                    c = UploadEbook.this.getContentResolver().query(pdfData, null, null, null, null);
                    //condition
                    if (c != null && c.moveToFirst()) {
                        pdfName = c.getString(c.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (pdfData.toString().startsWith("file://")) {
                pdfName = new File(pdfData.toString()).getName();

            }
            pdfTextView.setText(pdfName);//set text to pdf text view
        }
    }
}