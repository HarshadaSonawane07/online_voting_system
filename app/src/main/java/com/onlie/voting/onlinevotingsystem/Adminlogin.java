package com.onlie.voting.onlinevotingsystem;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Adminlogin extends AppCompatActivity {

    private EditText Phone, Password;
    private Button Login;
    private ProgressDialog LoadingBar;
    private String myphone, mypassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginadmin);

        Phone = findViewById(R.id.loginphone);
        Password = findViewById(R.id.loginpassword);
        Login = findViewById(R.id.loginbutton);

        LoadingBar = new ProgressDialog(this);
        mypassword = "Bharat@2024";
        myphone = "7020169543";

        Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                authenticateAdmin();
            }
        });
    }

    private void authenticateAdmin() {
        String enteredPhone = Phone.getText().toString();
        String enteredPassword = Password.getText().toString();

        if (enteredPhone.equals(myphone) && enteredPassword.equals(mypassword)) {
            // Successful authentication, navigate to AdminHomeActivity
            Intent intent = new Intent(Adminlogin.this, adminhome.class);
            startActivity(intent);
            finish(); // Optional: Close the current activity to prevent going back
        } else {
            // Failed authentication, show a toast
            Toast.makeText(Adminlogin.this, "Failed to authenticate", Toast.LENGTH_SHORT).show();
        }
    }
}
