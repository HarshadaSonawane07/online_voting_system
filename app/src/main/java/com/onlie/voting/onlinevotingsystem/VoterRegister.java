package com.onlie.voting.onlinevotingsystem;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VoterRegister extends AppCompatActivity {

    EditText fullNameEntry, aadharEntry, voterIdEntry, phoneNumberEntry;
    Button submitButton;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voterregister);

        fullNameEntry = findViewById(R.id.fullNameEntry);
        phoneNumberEntry = findViewById(R.id.phoneNumberEntry);
        aadharEntry = findViewById(R.id.aadharEntry);
        voterIdEntry = findViewById(R.id.voterIdEntry);
        submitButton = findViewById(R.id.submitButton);

        databaseReference = FirebaseDatabase.getInstance().getReference("UserDetails");

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName = fullNameEntry.getText().toString();
                String phoneNumber = phoneNumberEntry.getText().toString();
                String aadharNumber = aadharEntry.getText().toString();
                String voterId = voterIdEntry.getText().toString();

                if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(phoneNumber) ||
                        TextUtils.isEmpty(aadharNumber) || TextUtils.isEmpty(voterId)) {
                    Toast.makeText(VoterRegister.this, "Please fill in all the details", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (aadharNumber.length() != 12) {
                    Toast.makeText(VoterRegister.this, "Aadhar Number must be 12 digits", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (voterId.length() != 10 || !voterId.substring(0, 3).matches("[a-zA-Z]+") || !voterId.substring(3).matches("[0-9]+")) {
                    Toast.makeText(VoterRegister.this, "Invalid Voter-ID format. Voter-ID must start with 3 alphabets followed by 7 numbers.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (phoneNumber.length() != 10) {
                    Toast.makeText(VoterRegister.this, "Phone Number must be 10 digits", Toast.LENGTH_SHORT).show();
                    return;
                }
                phoneNumber = "+91" + phoneNumber;

                // All fields are valid, store data in Firebase
                databaseReference.child("PhoneNumbers").child(voterId).setValue(phoneNumber);

                UserDetails userDetails = new UserDetails(fullName, aadharNumber, voterId);

                databaseReference.child("UserDetails").child(voterId).setValue(userDetails);

                Toast.makeText(VoterRegister.this, "Registration Successful!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(VoterRegister.this, adminhome.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
