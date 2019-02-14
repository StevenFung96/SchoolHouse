package com.example.asus.schoolhouse.EventActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.schoolhouse.Chat.SettingsActivity;
import com.example.asus.schoolhouse.MainActivity;
import com.example.asus.schoolhouse.ProgressionActivity.NavDrawer;
import com.example.asus.schoolhouse.R;
import com.example.asus.schoolhouse.StartActivity;
import com.example.asus.schoolhouse.SubmitProject;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.example.asus.schoolhouse.R.id.drawer;

public class UpcomingEvent extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private ImageButton newEventBtn;
    private ImageButton calendarBtn;

    private FirebaseUser mCurrentUser;
    private DatabaseReference mEventDatabase;
    private DatabaseReference mEvent_uid;
    private DatabaseReference mUserDatabase;

    private RecyclerView mEventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upcoming_event);

        newEventBtn=(ImageButton)findViewById(R.id.newEventBtn);
        calendarBtn=(ImageButton)findViewById(R.id.calendarBtn);

        mDrawerLayout = (DrawerLayout)findViewById(drawer);
        mToggle=new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        NavigationView navDrawer = (NavigationView)findViewById(R.id.nav);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpDrawerContent(navDrawer);
        getSupportActionBar().setTitle("Dateline / Meeting Date");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mEventDatabase = FirebaseDatabase.getInstance().getReference().child("Event");
        mUserDatabase =FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        //EventList Activity

        mEventList = (RecyclerView)findViewById(R.id.event_list);
        mEventList.setHasFixedSize(true);
        mEventList.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mEventList.setLayoutManager(mLayoutManager);


        newEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent addNewEventIntent = new Intent(UpcomingEvent.this, AddNewEvent.class);
                startActivity(addNewEventIntent);

            }
        });

        calendarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent calendarIntent = new Intent(UpcomingEvent.this, CalendarView.class);
                startActivity(calendarIntent);

            }
        });


    }


    public void selectDrawerItem(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.recent_activity_btn:
                Intent RecentActivityIntent = new Intent(UpcomingEvent.this, NavDrawer.class);
                startActivity(RecentActivityIntent);
                break;
            case R.id.upcoming_event_btn:
                Intent UpcomingEventIntent = new Intent(UpcomingEvent.this, UpcomingEvent.class);
                startActivity(UpcomingEventIntent);
                break;
            case R.id.submit_project_btn:
                Intent SubmitProjectIntent = new Intent(UpcomingEvent.this, SubmitProject.class);
                startActivity(SubmitProjectIntent);
                break;
            case R.id.chat_nav_btn:
                Intent ChatNavIntent = new Intent(UpcomingEvent.this, MainActivity.class);
                startActivity(ChatNavIntent);
                break;
            case R.id.account_settings_btn:
                Intent AccountSettingsIntent = new Intent(UpcomingEvent.this, SettingsActivity.class);
                startActivity(AccountSettingsIntent);
                break;
            case R.id.logout_nav_btn:
                FirebaseAuth.getInstance().signOut();
                sendToStart();
                break;
            default:
                break;
        }

        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawerLayout.closeDrawers();
    }

    private void sendToStart() {
        Intent startIntent = new Intent(UpcomingEvent.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    private void setUpDrawerContent(NavigationView navigationView){
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStart(){
        super.onStart();

        final FirebaseRecyclerAdapter<Event, EventViewHolder> eventRecyclerViewAdapter = new FirebaseRecyclerAdapter<Event, EventViewHolder>(

                Event.class,
                R.layout.event_row,
                EventViewHolder.class,
                mEventDatabase

        ) {
            @Override
            protected void populateViewHolder(final EventViewHolder viewHolder, Event model, int position) {

                final String current_uid=mCurrentUser.getUid();
                final String post_key = getRef(position).getKey();
                mEvent_uid = FirebaseDatabase.getInstance().getReference().child("Event").child(post_key).child("uid");

                viewHolder.setTitle(model.getTitle());
                viewHolder.setDateTime(model.getDateTime());
                //viewHolder.setType(model.getType());


                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent eventDetailIntent = new Intent(UpcomingEvent.this, EventDetails.class);
                        eventDetailIntent.putExtra("event_id", post_key);
                        startActivity(eventDetailIntent);

                    }
                });

                mEvent_uid.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String post_uid=(String)dataSnapshot.getValue();

                        if(current_uid.equals (post_uid)) {

                            viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                                // Called when the user long-clicks on someView
                                public boolean onLongClick(View view) {

                                    CharSequence options[] = new CharSequence[]{"Delete Task"};

                                    final AlertDialog.Builder builder = new AlertDialog.Builder(UpcomingEvent.this);

                                    builder.setTitle("Are you sure you want to delete this task?");
                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                            //Click Event for each item.
                                            if (i == 0) {

                                                mEventDatabase.child(post_key).removeValue();

                                            }
                                        }
                                    });

                                    builder.show();

                                    return false;
                                }
                            });
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mEventList.setAdapter(eventRecyclerViewAdapter);


    }

    public static class EventViewHolder extends RecyclerView.ViewHolder{

        View mView;
        //String mDType="Due Date";
        //String mMType="Meeting Date";
        ImageView eventTypeImage;

        public EventViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            eventTypeImage = (ImageView)mView.findViewById(R.id.event_type_image);

        }

        public void setTitle(String title){

            TextView event_title =(TextView)mView.findViewById(R.id.event_row_title);
            event_title.setText(title);
            event_title.setMaxLines(1);
            event_title.setEllipsize(TextUtils.TruncateAt.END);
        }


        public void setDateTime(String dateTime){

            TextView event_dateTime =(TextView)mView.findViewById(R.id.event_row_datetime);
            event_dateTime.setText(dateTime);
        }

        /*
        public void setType(String type){

            if(type == mDType){

                eventTypeImage.setImageResource(R.drawable.online_icon);

            }

            if(type == mMType){

                eventTypeImage.setImageResource(R.mipmap.ic_meeting);

            }
        }
        */


    }

}
