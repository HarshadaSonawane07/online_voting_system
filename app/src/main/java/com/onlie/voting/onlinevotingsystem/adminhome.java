package com.onlie.voting.onlinevotingsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class adminhome extends AppCompatActivity {

    private Button showResultsButton;
    private Button ResetData;
    private Button voterreg;
    private TextView resultTextView;
    private DatabaseReference mref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adminhome);

        // Initialize Firebase reference
        mref = FirebaseDatabase.getInstance().getReference();

        // Find views by ID
        voterreg=findViewById(R.id.voterRegistration);
        showResultsButton = findViewById(R.id.result);
        resultTextView = findViewById(R.id.resultTextView);
        ResetData=findViewById(R.id.Reset);

        // Set onClickListener for the showResultsButton
        showResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call a method to fetch and display vresults
                fetchAndDisplayResults();
            }
        });
        ResetData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call a method to reset data
                resetData();
            }


        });
        voterreg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(adminhome.this, VoterRegister.class);
                startActivity(intent);
            }


        });


    }

    private void fetchAndDisplayResults() {
        mref.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Initialize counters for each party
                int party1Votes = 0;
                int party2Votes = 0;
                int party3Votes = 0;

                // Iterate through each user
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    // Get Party value under each user
                    String party = userSnapshot.child("Party").getValue(String.class);

                    // Count votes for each party
                    if ("Party1".equals(party)) {
                        party1Votes++;
                    } else if ("Party2".equals(party)) {
                        party2Votes++;
                    } else if ("Party3".equals(party)) {
                        party3Votes++;
                    }
                }

                // Display results in the TextView
                String results = "B.J.P. : " + party1Votes + " votes\n"
                        + "N.C.P. : " + party2Votes + " votes\n"
                        + "I.N.C. : " + party3Votes + " votes";

                resultTextView.setText(results);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                resultTextView.setText("Error fetching data");
            }
        });
    }
    private void resetData() {
        mref.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Iterate through each user and set Party to null
                for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                    mref.child("Users").child(userSnapshot.getKey()).child("Party").setValue("null");
                    mref.child("Users").child(userSnapshot.getKey()).child("VoteCount").setValue("0");
                }

                // Inform the user that data has been reset
                resultTextView.setText("Data reset successfully");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle errors
                resultTextView.setText("Error resetting data");
            }
        });
    }

}
