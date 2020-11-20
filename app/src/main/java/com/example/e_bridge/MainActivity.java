package com.example.e_bridge;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    boolean valid = false;
    private EditText sroll;
    private EditText spass;
    private TextView failed;


    private String testname = "181500145";
    private String testpass = "12345";
    private Button login;
    private int counter = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //connecting variables to their respective widget using id in activity main file
       /* sroll = (EditText)findViewById(R.id.username);
        //typecasting in EditText incase we see any error due to the input
        spass = (EditText)findViewById(R.id.password);
        failed = findViewById(R.id.textv);
        login = findViewById(R.id.button2);


        //now setting the onClick listener
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the name and password given by the user
                //converted into string
                String ipname = sroll.getText().toString();
                String ippass = spass.getText().toString();
                //checking whether the user gave any input or not
                if(ipname.isEmpty() || ippass.isEmpty()){
                    //Dispalying an error message
                    Toast.makeText(MainActivity.this, "You haven't entered the Details Properly",Toast.LENGTH_SHORT).show();

                }
                else{
                    valid= checkCred(ipname,ippass);
                    //when it is true we do not have to do anything . but as we need to pop a message saying "wrongg credentials" , we need to check that valid is false
                    if(!valid){
                        counter--;

                        Toast.makeText(MainActivity.this, "You have entered a wrong username or password",Toast.LENGTH_SHORT).show();
                        if(counter == 0){
                            Toast.makeText(MainActivity.this, "You have entered a wrong username or password 5 times, Contact your advisor",Toast.LENGTH_SHORT).show();
                            login.setEnabled(false);
                            //disabling LOGIN button after 5 attemts

                        }

                    }


                }

            }
        });*/

    }
   /* private boolean checkCred(String name, String pass){
        //making it case sensitive
        if(name.equals(testname) && pass.equals(testpass)){
            //returning true only if both passwords and username are correct
            return true;
        }
        else{
            return false;
        }

    }
}*/
}