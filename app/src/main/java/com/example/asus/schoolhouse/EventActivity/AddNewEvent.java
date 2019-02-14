package com.example.asus.schoolhouse.EventActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.asus.schoolhouse.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddNewEvent extends AppCompatActivity {

    DateFormat formatDateTime = DateFormat.getDateTimeInstance();
    Calendar mCurrentDate = Calendar.getInstance();

    private EditText mEventTitle;
    private EditText mEventLoc;
    private EditText mEventDesc;
    private TextView text_date;
    private TextView text_time;
    private ImageButton btn_date;
    private ImageButton btn_time;
    private Button btn_create;
    private Button btn_discard;
    private int day,month,year,hour,min;
    private CheckBox mCBdateline;
    private CheckBox mCBmeeting;

    private DatabaseReference mEventDatabase;
    private DatabaseReference mUsersDatabase;
    private FirebaseUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_event);

        getSupportActionBar().setTitle("Create A New Task");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEventTitle = (EditText) findViewById(R.id.eventTitle);
        mEventLoc=(EditText)findViewById(R.id.eventLocation);
        mEventDesc=(EditText)findViewById(R.id.eventDesc);
        text_date = (TextView) findViewById(R.id.eventDate);
        text_time = (TextView) findViewById(R.id.eventTime);
        btn_date = (ImageButton) findViewById(R.id.pickDateBtn);
        btn_time = (ImageButton) findViewById(R.id.pickTimeBtn);
        btn_create=(Button)findViewById(R.id.createEvent);
        btn_discard=(Button)findViewById(R.id.discardEvent);
        mCBdateline=(CheckBox)findViewById(R.id.checkBox_dateline);
        mCBmeeting=(CheckBox)findViewById(R.id.checkBox_meeting);



        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mEventDatabase = FirebaseDatabase.getInstance().getReference().child("Event");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        mCBdateline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    mCBmeeting.setChecked(false);
                }
            }
        });
        mCBmeeting.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                {
                    mCBdateline.setChecked(false);
                }
            }
        });

        btn_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDate();
            }
        });

        btn_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateTime();
            }
        });

        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEvent();
            }
        });

        btn_discard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent mainIntent = new Intent(AddNewEvent.this, UpcomingEvent.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            }
        });


    }

    private void updateDate() {

        day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        month= mCurrentDate.get(Calendar.MONTH);
        year=mCurrentDate.get(Calendar.YEAR);

        month=month+1;

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddNewEvent.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear=monthOfYear+1;
                text_date.setText(dayOfMonth+"/"+monthOfYear+"/"+year);
                btn_date.setColorFilter(Color.argb(255,255,140,0));
            }
        },year,month,day);
        datePickerDialog.show();

    }

    private void updateTime() {

        hour= mCurrentDate.get(Calendar.HOUR_OF_DAY);
        min=mCurrentDate.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(AddNewEvent.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                text_time.setText(hourOfDay+":"+minute);
                btn_time.setColorFilter(Color.argb(255,255,140,0));
            }
        },hour,min,false);
        timePickerDialog.show();

    }

    private void updateEvent() {

        final String d = text_date.getText().toString();
        final String t = text_time.getText().toString();
        final String dt = d+"  "+t;

        final String title_val = mEventTitle.getText().toString().trim();
        final String loc_val = mEventLoc.getText().toString().trim();
        final String desc_val = mEventDesc.getText().toString().trim();
        final String current_uid = mCurrentUser.getUid();

        final String cbDateline=mCBdateline.getText().toString();
        final String cbMeeting=mCBmeeting.getText().toString();


        /*if((!TextUtils.isEmpty(title_val) &&(!TextUtils.isEmpty(cbDateline) || !TextUtils.isEmpty(cbMeeting)) && !TextUtils.isEmpty(loc_val) && !TextUtils.isEmpty(desc_val) && !TextUtils.isEmpty(dt))
                ||(!TextUtils.isEmpty(title_val) &&(!TextUtils.isEmpty(cbDateline) || !TextUtils.isEmpty(cbMeeting) ) && TextUtils.isEmpty(loc_val) &&
                !TextUtils.isEmpty(desc_val) && !TextUtils.isEmpty(dt))){*/


        if(!TextUtils.isEmpty(title_val) && (!TextUtils.isEmpty(loc_val) || TextUtils.isEmpty(loc_val)) && !TextUtils.isEmpty(desc_val) && !TextUtils.isEmpty(d)
                && !TextUtils.isEmpty(t) && (mCBdateline.isChecked() || mCBmeeting.isChecked())){

            final DatabaseReference newEvent = mEventDatabase.push();

            SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy 'at' hh:mma");
            Date date = new Date();
            final String currentDate = df.format(date);


            mUsersDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    newEvent.child("title").setValue(title_val);
                    newEvent.child("location").setValue(loc_val);
                    newEvent.child("desc").setValue(desc_val);
                    newEvent.child("date").setValue(d);
                    newEvent.child("time").setValue(t);
                    newEvent.child("dateTime").setValue(dt);
                    newEvent.child("uid").setValue(current_uid);
                    newEvent.child("postDate").setValue(currentDate);
                    newEvent.child("username").setValue(dataSnapshot.child("name").getValue());

                    if(mCBmeeting.isChecked()){

                        newEvent.child("type").setValue(cbMeeting);
                    }

                    if(mCBdateline.isChecked()){

                        newEvent.child("type").setValue(cbDateline);

                    }

                    Toast.makeText(AddNewEvent.this, "Your Task Has Been Posted." ,Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {



                }
            });


            Intent mainIntent = new Intent(AddNewEvent.this, UpcomingEvent.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();


        }


        /*

        if((!TextUtils.isEmpty(title_val) &&(!TextUtils.isEmpty(cbDateline) || !TextUtils.isEmpty(cbMeeting)) && !TextUtils.isEmpty(loc_val) && !TextUtils.isEmpty(desc_val) && !TextUtils.isEmpty(dt))
                ||(!TextUtils.isEmpty(title_val) &&(!TextUtils.isEmpty(cbDateline) || !TextUtils.isEmpty(cbMeeting) ) && TextUtils.isEmpty(loc_val) &&
                !TextUtils.isEmpty(desc_val) && !TextUtils.isEmpty(dt))){



            mPDialog.show();

            final DatabaseReference newEvent = mEventDatabase.push();

            SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy 'at' hh:mma");
            Date date = new Date();
            final String currentDate = df.format(date);

            mUsersDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    newEvent.child("title").setValue(title_val);
                    newEvent.child("location").setValue(loc_val);
                    newEvent.child("desc").setValue(desc_val);
                    newEvent.child("date").setValue(d);
                    newEvent.child("time").setValue(t);
                    newEvent.child("dateTime").setValue(dt);
                    newEvent.child("uid").setValue(current_uid);
                    newEvent.child("postDate").setValue(currentDate);
                    newEvent.child("username").setValue(dataSnapshot.child("name").getValue());

                    if(mCBmeeting.isChecked()){

                        newEvent.child("type").setValue(cbMeeting);
                    }

                    if(mCBdateline.isChecked()){

                        newEvent.child("type").setValue(cbDateline);

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            //mPDialog.dismiss();

            Intent mainIntent = new Intent(AddNewEvent.this, UpcomingEvent.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(mainIntent);
            finish();

        }

        */


    }



}
