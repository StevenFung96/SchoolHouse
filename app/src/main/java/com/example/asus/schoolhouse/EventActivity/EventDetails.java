package com.example.asus.schoolhouse.EventActivity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.asus.schoolhouse.Chat.ProfileActivity;
import com.example.asus.schoolhouse.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EventDetails extends AppCompatActivity {

    private String mPost_key=null;

    private FirebaseUser mCurrentUser;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mEventDatabase;

    private TextView mEventTitle;
    private TextView mEventDay;
    private TextView mEventTime;
    private TextView mEventLocation;
    private TextView mEventDesc;
    private TextView mEventUsername;
    private TextView mEventPostedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        mPost_key = getIntent().getExtras().getString("event_id");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserDatabase= FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mEventDatabase= FirebaseDatabase.getInstance().getReference().child("Event");

        mEventTitle=(TextView)findViewById(R.id.event_details_title);
        mEventDay=(TextView)findViewById(R.id.event_details_day);
        mEventTime=(TextView)findViewById(R.id.event_details_time);
        mEventLocation=(TextView)findViewById(R.id.event_details_location);
        mEventDesc=(TextView)findViewById(R.id.event_details_desc);
        mEventUsername=(TextView)findViewById(R.id.event_details_user);
        mEventPostedDate=(TextView)findViewById(R.id.event_details_postedDate);

        getSupportActionBar().setTitle("Task Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        mEventDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String event_title=(String)dataSnapshot.child("title").getValue();
                String event_date=(String)dataSnapshot.child("date").getValue();
                String event_time=(String)dataSnapshot.child("time").getValue();
                String event_location=(String)dataSnapshot.child("location").getValue();
                String event_desc=(String)dataSnapshot.child("desc").getValue();
                String event_username=(String)dataSnapshot.child("username").getValue();
                String event_postedDate = (String)dataSnapshot.child("postDate").getValue();
                final String post_uid=(String)dataSnapshot.child("uid").getValue();

                mEventTitle.setText(event_title);
                mEventDay.setText(event_date);
                mEventTime.setText(event_time);

                mEventDesc.setText(event_desc);
                mEventUsername.setText(event_username);
                mEventPostedDate.setText(event_postedDate);

                if(dataSnapshot.hasChild("location")){

                    mEventLocation.setText(event_location);

                }

                if(dataSnapshot.child("location").getValue().equals("")){

                    mEventLocation.setText("-");

                }


                mEventUsername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profileIntent = new Intent(EventDetails.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id", post_uid);
                        startActivity(profileIntent);

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
