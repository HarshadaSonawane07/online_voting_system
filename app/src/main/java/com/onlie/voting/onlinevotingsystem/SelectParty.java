package com.onlie.voting.onlinevotingsystem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SelectParty extends AppCompatActivity {

    Button Party1, Party2, Party3;
    private DatabaseReference mref;
    private ProgressDialog LoadingBar;
    String Phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_party);

        Party1 = findViewById(R.id.party1);
        Party2 = findViewById(R.id.party2);
        Party3 = findViewById(R.id.party3);

        Intent i = getIntent();
        Phone = i.getStringExtra("phone");
        mref = FirebaseDatabase.getInstance().getReference();
        LoadingBar = new ProgressDialog(this);

        Party1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voteForParty("Party1");
            }
        });

        Party2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voteForParty("Party2");
            }
        });

        Party3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                voteForParty("Party3");
            }
        });
    }

    private void voteForParty(final String partyName) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(SelectParty.this);
        builder.setTitle("Confirm Your Party");
        builder.setMessage("Do you want to give your vote to " + partyName);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LoadingBar.setTitle("Voting In Progress");
                LoadingBar.setMessage("Please wait..");
                LoadingBar.setCanceledOnTouchOutside(false);
                LoadingBar.show();

                mref.child("Users").child(Phone).child("VoteCount").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(SelectParty.this, "You have already voted.", Toast.LENGTH_LONG).show();
                            LoadingBar.dismiss();
                        } else {
                            mref.child("Users").child(Phone).child("VoteCount").setValue(1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mref.child("Users").child(Phone).child("Party").setValue(partyName); // Store the party name

                                    mref.child("Parties").child(partyName).child("Vote").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                long currentVotes = (long) snapshot.getValue();
                                                mref.child("Parties").child(partyName).child("Vote").setValue(currentVotes + 1); // Increment vote count
                                            } else {
                                                mref.child("Parties").child(partyName).child("Vote").setValue(1); // If vote count doesn't exist, create and set to 1
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Handle error
                                        }
                                    });

                                    Intent i = new Intent(SelectParty.this, FinalActivity.class);
                                    i.putExtra("phone", Phone);
                                    i.putExtra("partyname", partyName);
                                    startActivity(i);
                                    LoadingBar.dismiss();
                                    Toast.makeText(SelectParty.this, "Your vote is submitted to our database.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Handle error
                    }
                });
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }


    @Override
    public void onBackPressed() {
        // Handle back button press if needed
    }
}

