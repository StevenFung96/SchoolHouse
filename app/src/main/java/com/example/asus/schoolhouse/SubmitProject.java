package com.example.asus.schoolhouse;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.schoolhouse.Chat.SettingsActivity;
import com.example.asus.schoolhouse.EventActivity.UpcomingEvent;
import com.example.asus.schoolhouse.ProgressionActivity.NavDrawer;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.asus.schoolhouse.R.id.drawer;

public class SubmitProject extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    protected AlphaAnimation fadeIn = new AlphaAnimation( 1.0f , 0.0f );
    protected AlphaAnimation fadeOut = new AlphaAnimation(0.0f , 1.0f );

    private Uri resultUri;
    private Uri mImageUri;
    private TextView mInstrucText;

    private StorageReference mImageStorage;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUsersDatabase;
    private DatabaseReference mProjectDatabase;

    private static final int FILE_PICK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_project);

        mDrawerLayout = (DrawerLayout)findViewById(drawer);
        mToggle=new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        NavigationView navDrawer = (NavigationView)findViewById(R.id.nav);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpDrawerContent(navDrawer);
        getSupportActionBar().setTitle("Project Drive");


        mInstrucText=(TextView)findViewById(R.id.instruc_text);


        mImageStorage = FirebaseStorage.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mProjectDatabase = FirebaseDatabase.getInstance().getReference().child("Project Files");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());


        final FloatingActionButton fab = new FloatingActionButton.Builder(this)
                .setBackgroundDrawable(R.drawable.reactor1)
                .build();

/*
        FloatingActionButton.LayoutParams layoutParams = new FloatingActionButton.LayoutParams(400,400); //int width,int height
        layoutParams.setMargins(340,800,0,0);//int left, int top, int right, int bottom
        fab.setLayoutParams(layoutParams);

        SubActionButton.Builder itemSurvey = new SubActionButton.Builder(this);
        FloatingActionButton.LayoutParams params=new FloatingActionButton.LayoutParams(200,200);
        itemSurvey.setLayoutParams(params);
*/



        FloatingActionButton.LayoutParams layoutParams = new FloatingActionButton.LayoutParams(280,280); //int width,int height
        layoutParams.setMargins(220,500,0,0);//int left, int top, int right, int bottom
        fab.setLayoutParams(layoutParams);

        SubActionButton.Builder itemSurvey = new SubActionButton.Builder(this);
        FloatingActionButton.LayoutParams params=new FloatingActionButton.LayoutParams(150,150);
        itemSurvey.setLayoutParams(params);


        ImageView survey = new ImageView(this);
        survey.setImageResource(R.drawable.surveys);
        SubActionButton surveyBtn = itemSurvey.setContentView(survey).build();

        ImageView schoology = new ImageView(this);
        schoology.setImageResource(R.drawable.photo);
        SubActionButton schoologyBtn = itemSurvey.setContentView(schoology).build();

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .setStartAngle(0)
                .setEndAngle(360)
                .addSubActionView(schoologyBtn)
                .addSubActionView(surveyBtn)
                //       .addSubActionView(userBtn)
                // ...
                .attachTo(fab)
                .build();


        surveyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                Intent fileIntent = new Intent();
                fileIntent.setType("file/*");
                fileIntent.setAction(Intent.ACTION_GET_CONTENT);
                String[] mimetypes = {"application/vnd.openxmlformats-officedocument.wordprocessingml.document", "application/msword"};
                fileIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(Intent.createChooser(fileIntent, "SELECT FILE"), FILE_PICK);

                startUploadFiles();
                */

                Intent status_intent = new Intent(SubmitProject.this, UploadFilesActivity.class);
                startActivity(status_intent);

            }
        });

        schoologyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*
                Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"), GALLERY_PICK);
                */

                Intent status_intent = new Intent(SubmitProject.this, UploadImageActivity.class);
                startActivity(status_intent);

            }
        });




        //Fade IN Fade OUT action
        mInstrucText.startAnimation(fadeIn);
        mInstrucText.startAnimation(fadeOut);
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);
        fadeIn.setStartOffset(1000);
        fadeOut.setDuration(500);
        fadeOut.setFillAfter(true);
        fadeOut.setStartOffset(1000);

        fadeIn.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationEnd(Animation arg0) {
                // start fadeOut when fadeIn ends (continue)
                mInstrucText.startAnimation(fadeOut);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

        fadeOut.setAnimationListener(new Animation.AnimationListener(){
            @Override
            public void onAnimationEnd(Animation arg0) {
                // start fadeIn when fadeOut ends (repeat)
                mInstrucText.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation arg0) {
            }

            @Override
            public void onAnimationStart(Animation arg0) {
            }
        });

        mInstrucText.startAnimation(fadeIn);


    }

    /*
    private void startUploadFiles() {


            final String current_uid = mCurrentUser.getUid();
            StorageReference filepath = mImageStorage.child("project_files");

            if(resultUri != null ) {

                filepath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                        final DatabaseReference newPost = mProjectDatabase.push();

                        SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy 'at' hh:mma");
                        Date date = new Date();
                        final String currentDate = df.format(date);

                        mUsersDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {


                                newPost.child("files").setValue(downloadUrl.toString());
                                newPost.child("uid").setValue(current_uid);
                                newPost.child("date").setValue(currentDate);
                                newPost.child("username").setValue(dataSnapshot.child("name").getValue());

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                                Toast.makeText(SubmitProject.this, "Error in uploading.", Toast.LENGTH_LONG).show();

                            }
                        });

                    }
                });
            }

    }
    */


    public void selectDrawerItem(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.recent_activity_btn:
                Intent RecentActivityIntent = new Intent(SubmitProject.this, NavDrawer.class);
                startActivity(RecentActivityIntent);
                break;
            case R.id.upcoming_event_btn:
                Intent UpcomingEventIntent = new Intent(SubmitProject.this, UpcomingEvent.class);
                startActivity(UpcomingEventIntent);
                break;
            case R.id.submit_project_btn:
                Intent SubmitProjectIntent = new Intent(SubmitProject.this, SubmitProject.class);
                startActivity(SubmitProjectIntent);
                break;
            case R.id.chat_nav_btn:
                Intent ChatNavIntent = new Intent(SubmitProject.this, MainActivity.class);
                startActivity(ChatNavIntent);
                break;
            case R.id.account_settings_btn:
                Intent AccountSettingsIntent = new Intent(SubmitProject.this, SettingsActivity.class);
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
        Intent startIntent = new Intent(SubmitProject.this, StartActivity.class);
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


    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == FILE_PICK && resultCode == RESULT_OK){

            resultUri = data.getData();

        }

    }
    */
}
