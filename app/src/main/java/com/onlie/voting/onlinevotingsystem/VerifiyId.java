package com.onlie.voting.onlinevotingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.TimeUnit;

public class VerifiyId extends AppCompatActivity {

    String Phone;
    EditText Id, otpEditText;
    Button IdButton, sendOTPButton;
    private DatabaseReference mref;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifiy_age);

        Intent i = getIntent();
        Phone = i.getStringExtra("phone");

        Id = findViewById(R.id.idproof);
        IdButton = findViewById(R.id.verifyagebutton);
        sendOTPButton = findViewById(R.id.sendOTPButton);
        otpEditText = findViewById(R.id.otpEditText);
        mref = FirebaseDatabase.getInstance().getReference();

        sendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredId = Id.getText().toString();
                checkVoterIdExists(enteredId);
            }
        });

        IdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(Id.getText().toString())) {
                    Toast.makeText(VerifiyId.this, "Please Enter your ID..", Toast.LENGTH_LONG).show();
                } else {
                    String enteredOTP = otpEditText.getText().toString();
                    verifyOTP(enteredOTP);
                }
            }
        });
    }

    private void checkVoterIdExists(String enteredId) {
        DatabaseReference userDetailsRef = FirebaseDatabase.getInstance().getReference().child("UserDetails");

        userDetailsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("PhoneNumbers").child(enteredId).exists()) {
                    String phoneNumber = dataSnapshot.child("PhoneNumbers").child(enteredId).getValue(String.class);
                    sendOTP(enteredId, phoneNumber);
                } else {
                    Toast.makeText(VerifiyId.this, "Voter ID does not exist or is not linked to a phone number.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(VerifiyId.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendOTP(String enteredId, String phoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60L,
                TimeUnit.SECONDS,
                VerifiyId.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // Auto-retrieval or instant verification of OTP has occurred.
                        // Proceed with automatic authentication.
                        signInWithPhoneAuthCredential(phoneAuthCredential, enteredId);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(VerifiyId.this, "OTP verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationId = s;
                        Toast.makeText(VerifiyId.this, "OTP sent successfully.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void verifyOTP(String enteredOTP) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, enteredOTP);
        signInWithPhoneAuthCredential(credential, "");
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential, String enteredId) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // OTP verified successfully
                            mref.child("UserDetails").child("PhoneNumbers").child(enteredId)
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                mref.child("Users").child(Phone).child("ID").setValue(enteredId)
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Intent intent = new Intent(VerifiyId.this, SelectParty.class);
                                                                    intent.putExtra("phone", Phone);
                                                                    startActivity(intent);
                                                                } else {
                                                                    Toast.makeText(VerifiyId.this, "Failed to store ID", Toast.LENGTH_SHORT).show();
                                                                }
                                                            }
                                                        });
                                            } else {
                                                Toast.makeText(VerifiyId.this, "Invalid voter", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            // Handle the error
                                        }
                                    });
                        } else {
                            Toast.makeText(VerifiyId.this, "Failed to verify OTP", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
