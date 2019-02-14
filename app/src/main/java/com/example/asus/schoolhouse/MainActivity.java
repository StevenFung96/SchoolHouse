package com.example.asus.schoolhouse;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.asus.schoolhouse.Chat.SettingsActivity;
import com.example.asus.schoolhouse.Chat.UsersActivity;
import com.example.asus.schoolhouse.EventActivity.UpcomingEvent;
import com.example.asus.schoolhouse.ProgressionActivity.NavDrawer;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import static com.example.asus.schoolhouse.R.id.drawer;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private FirebaseAuth mAuth;
    private Toolbar mToolbar;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout mTabLayout;
    private DatabaseReference mUserRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        //mToolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        //setSupportActionBar(mToolbar);
        mDrawerLayout = (DrawerLayout)findViewById(drawer);
        mToggle=new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        NavigationView navDrawer = (NavigationView)findViewById(R.id.nav);
        mToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpDrawerContent(navDrawer);
        getSupportActionBar().setTitle("School House");

        //Tabs
        mViewPager = (ViewPager) findViewById(R.id.main_tabPager);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager.setAdapter(mSectionsPagerAdapter);

        mTabLayout = (TabLayout) findViewById(R.id.main_tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        if (mAuth.getCurrentUser() != null) {


            mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            sendToStart();

        } else {

            mUserRef.child("online").setValue("true");

        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

        }

    }

    private void sendToStart() {

        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.main_logout_btn) {

            FirebaseAuth.getInstance().signOut();
            sendToStart();

        }
        if (item.getItemId() == R.id.main_settings_btn) {

            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);

        }

        if (item.getItemId() == R.id.main_all_btn) {

            Intent settingsIntent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(settingsIntent);

        }


        if(mToggle.onOptionsItemSelected(item)){
            return true;
        }

        return true;
    }

    public void selectDrawerItem(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.recent_activity_btn:
                Intent RecentActivityIntent = new Intent(MainActivity.this, NavDrawer.class);
                startActivity(RecentActivityIntent);
                break;
            case R.id.upcoming_event_btn:
                Intent UpcomingEventIntent = new Intent(MainActivity.this, UpcomingEvent.class);
                startActivity(UpcomingEventIntent);
                break;
            case R.id.submit_project_btn:
                Intent SubmitProjectIntent = new Intent(MainActivity.this, SubmitProject.class);
                startActivity(SubmitProjectIntent);
                break;
            case R.id.chat_nav_btn:
                Intent ChatNavIntent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(ChatNavIntent);
                break;
            case R.id.account_settings_btn:
                Intent AccountSettingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
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

}
