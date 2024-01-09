package com.onlie.voting.onlinevotingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RegisterActivity extends AppCompatActivity {

    private EditText Name, Password, Phone, OTPEntry;
    private Button Register, sendOTPButton;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;

    private ProgressDialog LoadingBar;
    private String verificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Name = findViewById(R.id.nameentry);
        Password = findViewById(R.id.passwordentry);
        Phone = findViewById(R.id.phoneentry);
        OTPEntry = findViewById(R.id.otpEntry);
        Register = findViewById(R.id.registerbutton);
        sendOTPButton = findViewById(R.id.sendOTPButton);

        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        LoadingBar = new ProgressDialog(this);

        sendOTPButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = "+91" + Phone.getText().toString();
                checkPhoneExistsAndSendOTP(phone);
            }
        });

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = "+91" + Phone.getText().toString();
                String otp = OTPEntry.getText().toString();

                if (TextUtils.isEmpty(otp)) {
                    Toast.makeText(RegisterActivity.this, "Please enter OTP", Toast.LENGTH_SHORT).show();
                } else {
                    verifyOTP(otp);
                }
            }
        });
    }

    private void sendOTP() {
        String phoneNumber = "+91" + Phone.getText().toString();

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,
                60L,
                TimeUnit.SECONDS,
                RegisterActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(RegisterActivity.this, "OTP verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);
                        verificationId = s;
                        Toast.makeText(RegisterActivity.this, "OTP sent successfully.", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void verifyOTP(String otp) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
        signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            createAccount();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Incorrect OTP. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkPhoneExistsAndSendOTP(String phone) {
        DatabaseReference usersRef = mRef.child("Users").child(phone);
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(RegisterActivity.this, "Phone number already exists.", Toast.LENGTH_SHORT).show();
                } else {
                    sendOTP();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RegisterActivity.this, "Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createAccount() {
        String name = Name.getText().toString();
        String phone = "+91" + Phone.getText().toString();
        String password = Password.getText().toString();

        Map<String, Object> user = new HashMap<>();
        user.put("ID", "");
        user.put("Name", name);
        user.put("Party", "");
        user.put("Password", password);
        user.put("Phone", phone);
        user.put("Vote", "0");

        mRef.child("Users").child(phone).setValue(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "Account created successfully.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
