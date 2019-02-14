package com.example.asus.schoolhouse.ProgressionActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.schoolhouse.Chat.ProfileActivity;
import com.example.asus.schoolhouse.Chat.SettingsActivity;
import com.example.asus.schoolhouse.EventActivity.UpcomingEvent;
import com.example.asus.schoolhouse.MainActivity;
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
import com.squareup.picasso.Picasso;

import static com.example.asus.schoolhouse.R.id.drawer;

public class NavDrawer extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;

    private RecyclerView mPostList;

    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseLike;
    private DatabaseReference mDatabaseComment;
    private DatabaseReference mPost_uid;

    private boolean mProccessLike = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);


        mDrawerLayout = (DrawerLayout)findViewById(drawer);
        mToggle=new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        NavigationView navDrawer = (NavigationView)findViewById(R.id.nav);
        mToggle.syncState();
        getSupportActionBar().setTitle("Project Progression");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setUpDrawerContent(navDrawer);

        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Post");
        mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");
        mDatabaseComment=FirebaseDatabase.getInstance().getReference().child("Comments");

        mDatabase.keepSynced(true);
        mDatabaseLike.keepSynced(true);

        //Post List Activity

        mPostList = (RecyclerView)findViewById(R.id.post_list);
        mPostList.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mPostList.setLayoutManager(mLayoutManager);

    }

    @Override
    public void onStart() {
        super.onStart();
        final FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {

            sendToStart();

        }else{

            FirebaseRecyclerAdapter<Post,PostViewHolder>firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Post, PostViewHolder>(

                    Post.class,
                    R.layout.post_row,
                    PostViewHolder.class,
                    mDatabase

            ) {
                @Override
                protected void populateViewHolder(final PostViewHolder viewHolder, Post model, int position) {

                    final String post_key = getRef(position).getKey();
                    final FirebaseUser currentUser = mAuth.getCurrentUser();
                    final String current_uid=currentUser.getUid();
                    mPost_uid=FirebaseDatabase.getInstance().getReference().child("Post").child(post_key).child("uid");

                    viewHolder.setTitle(model.getTitle());
                    viewHolder.setDesc(model.getDesc());
                    viewHolder.setUrl(model.getUrl());
                    viewHolder.setUsername(model.getUsername());
                    viewHolder.setDate(model.getDate());
                    viewHolder.setImage(getApplicationContext(),model.getImage());
                    viewHolder.setLikeBtn(post_key);

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Intent singlePostIntent = new Intent(NavDrawer.this, SinglePostActivity.class);
                            singlePostIntent.putExtra("post_id", post_key);
                            startActivity(singlePostIntent);

                        }
                    });


                    mPost_uid.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            final String post_uid=(String)dataSnapshot.getValue();

                            if(current_uid.equals (post_uid)) {

                                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                                    // Called when the user long-clicks on someView
                                    public boolean onLongClick(View view) {

                                        CharSequence options[] = new CharSequence[]{"Delete Post"};

                                        final AlertDialog.Builder builder = new AlertDialog.Builder(NavDrawer.this);

                                        builder.setTitle("Are you sure you want to delete this post?");
                                        builder.setItems(options, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                //Click Event for each item.
                                                if (i == 0) {

                                                    mDatabase.child(post_key).removeValue();
                                                    mDatabaseLike.child(post_key).removeValue();
                                                    mDatabaseComment.child(post_key).removeValue();

                                                }
                                            }
                                        });

                                        builder.show();

                                        return false;
                                    }
                                });
                            }

                            viewHolder.mUsername.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    Intent profileIntent = new Intent(NavDrawer.this, ProfileActivity.class);
                                    profileIntent.putExtra("user_id", post_uid);
                                    startActivity(profileIntent);

                                }
                            });

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    viewHolder.mLikebtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            mProccessLike=true;

                            final String current_user_id = mAuth.getCurrentUser().getUid();




                                mDatabaseLike.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if (mProccessLike) {

                                            if (dataSnapshot.hasChild(post_key)) {

                                                mDatabaseLike.child(post_key).removeValue();
                                                mProccessLike = false;

                                            } else {

                                                mDatabaseLike.child(post_key).child(current_user_id).setValue("RandomV");
                                                mProccessLike = false;

                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                    });

                }
            };

            mPostList.setAdapter(firebaseRecyclerAdapter);


        }
    }


    //Post Activity

    public static class PostViewHolder extends RecyclerView.ViewHolder{

        View mView;

        ImageButton mLikebtn;
        TextView mUsername;

        DatabaseReference mDatabaseLike;
        FirebaseAuth mAuth;

        public PostViewHolder(View itemView) {
            super(itemView);

            mView=itemView;

            mLikebtn = (ImageButton)mView.findViewById(R.id.like_btn);
            mUsername=(TextView)mView.findViewById(R.id.post_username);

            mDatabaseLike =FirebaseDatabase.getInstance().getReference().child("Likes");
            mAuth=FirebaseAuth.getInstance();

            mDatabaseLike.keepSynced(true);
        }

        public void setLikeBtn(final String post_key){


            mDatabaseLike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.hasChild(post_key)){

                        mLikebtn.setColorFilter(Color.argb(255,255,0,0));

                    }else{

                        mLikebtn.setColorFilter(Color.argb(255,192,192,192));

                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }

        public void setUid(String uid){



        }

        public void setUrl(String url){

            TextView post_url = (TextView)mView.findViewById(R.id.post_url);
            post_url.setText(url);
            //Pattern pattern = Pattern.compile(url);
            //Linkify.addLinks(post_url, pattern, "http://");
            //post_url.setText(Html.fromHtml(html));
            //post_url.setMovementMethod(LinkMovementMethod.getInstance());
        }

        public void setTitle(String title){

            TextView post_title =(TextView)mView.findViewById(R.id.post_title);
            post_title.setText(title);

        }

        public void setDesc(String desc){

            TextView post_desc =(TextView)mView.findViewById(R.id.post_text);
            post_desc.setText(desc);
            post_desc.setMaxLines(4);
            post_desc.setEllipsize(TextUtils.TruncateAt.END);
        }

        public void setUsername(String username){

            TextView post_username =(TextView)mView.findViewById(R.id.post_username);
            post_username.setText("Posted by: "+ username);
        }


        public void setDate(String date){

            TextView post_date =(TextView)mView.findViewById(R.id.post_date);
            post_date.setText(date);
        }

        public void setImage(Context ctx, String image){
            ImageView post_image =(ImageView)mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);
        }
    }




    public void selectDrawerItem(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.recent_activity_btn:
                Intent RecentActivityIntent = new Intent(NavDrawer.this, NavDrawer.class);
                startActivity(RecentActivityIntent);
                break;
            case R.id.upcoming_event_btn:
                Intent UpcomingEventIntent = new Intent(NavDrawer.this, UpcomingEvent.class);
                startActivity(UpcomingEventIntent);
                break;
            case R.id.submit_project_btn:
                Intent SubmitProjectIntent = new Intent(NavDrawer.this, SubmitProject.class);
                startActivity(SubmitProjectIntent);
                break;
            case R.id.chat_nav_btn:
                Intent ChatNavIntent = new Intent(NavDrawer.this, MainActivity.class);
                startActivity(ChatNavIntent);
                break;
            case R.id.account_settings_btn:
                Intent AccountSettingsIntent = new Intent(NavDrawer.this, SettingsActivity.class);
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
        Intent startIntent = new Intent(NavDrawer.this, StartActivity.class);
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

        if(item.getItemId()==R.id.action_add){

            startActivity(new Intent(NavDrawer.this, PostActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.recent_activity_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }
}

