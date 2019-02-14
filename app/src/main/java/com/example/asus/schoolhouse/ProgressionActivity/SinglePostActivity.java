package com.example.asus.schoolhouse.ProgressionActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.schoolhouse.Chat.ProfileActivity;
import com.example.asus.schoolhouse.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SinglePostActivity extends AppCompatActivity {

    private String mPost_key=null;

    FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mCommentDatabase;
    private DatabaseReference mDatabase;
    private DatabaseReference mDatabaseLike;
    private FirebaseUser mCurrentUser;

    private ImageView mPostSingleImage;
    private TextView mPostSingleTitle;
    private TextView mPostSingleDesc;
    private TextView mPostSingleUrl;
    private TextView mPostSingleDate;
    private TextView mPostSingleUsername;
    private ImageButton mPostSingleLikes;
    private ImageButton mPostCommentBtn;
    private EditText mCommentDetail;

    private RecyclerView mCommentList;

    private boolean mProccessLike = false;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        getSupportActionBar().setTitle("Progression Details");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPost_key = getIntent().getExtras().getString("post_id");

        mAuth=FirebaseAuth.getInstance();
        final String current_user_id = mAuth.getCurrentUser().getUid();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();

        mUserDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mCommentDatabase=FirebaseDatabase.getInstance().getReference().child("Comments").child(mPost_key);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Post");
        mDatabaseLike=FirebaseDatabase.getInstance().getReference().child("Likes");



        mProgressDialog = new ProgressDialog(this);

        mPostSingleDesc=(TextView)findViewById(R.id.single_post_text);
        mPostSingleUrl=(TextView)findViewById(R.id.single_post_url);
        mPostSingleImage=(ImageView)findViewById(R.id.single_post_image);
        mPostSingleTitle=(TextView)findViewById(R.id.single_post_title);
        mPostSingleDate=(TextView)findViewById(R.id.single_post_date);
        mPostSingleUsername=(TextView)findViewById(R.id.single_post_username);
        mPostSingleLikes=(ImageButton)findViewById(R.id.single_like_btn);
        mPostCommentBtn=(ImageButton)findViewById(R.id.comment_send_btn);
        mCommentDetail=(EditText) findViewById(R.id.comment_input);

        //CommentList Activity

        mCommentList = (RecyclerView)findViewById(R.id.comment_list);
        mCommentList.setHasFixedSize(true);
        mCommentList.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setReverseLayout(true);
        mLayoutManager.setStackFromEnd(true);
        mCommentList.setLayoutManager(mLayoutManager);

        //Toast.makeText(SinglePostActivity.this, post_key, Toast.LENGTH_SHORT).show();

        mDatabase.child(mPost_key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String post_title=(String)dataSnapshot.child("title").getValue();
                String post_desc=(String)dataSnapshot.child("desc").getValue();
                String post_url=(String)dataSnapshot.child("url").getValue();
                String post_image=(String)dataSnapshot.child("image").getValue();
                final String post_uid=(String)dataSnapshot.child("uid").getValue();
                String post_username=(String)dataSnapshot.child("username").getValue();
                String post_date=(String)dataSnapshot.child("date").getValue();

                mPostSingleTitle.setText(post_title);
                mPostSingleDesc.setText(post_desc);
                mPostSingleUrl.setText(post_url);
                mPostSingleUsername.setText(post_username);
                mPostSingleDate.setText(post_date);
                Picasso.with(SinglePostActivity.this).load(post_image).into(mPostSingleImage);

                //Click on Username to Profile
                mPostSingleUsername.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent profileIntent = new Intent(SinglePostActivity.this, ProfileActivity.class);
                        profileIntent.putExtra("user_id", post_uid);
                        startActivity(profileIntent);
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Change Like Btn color
        mDatabaseLike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(dataSnapshot.hasChild(mPost_key)){

                    mPostSingleLikes.setColorFilter(Color.argb(255,255,0,0));

                }else{

                    mPostSingleLikes.setColorFilter(Color.argb(255,192,192,192));

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Press Likes or Cancel Likes
        mPostSingleLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mProccessLike=true;

                mDatabaseLike.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (mProccessLike) {

                            if (dataSnapshot.hasChild(mPost_key)) {

                                mDatabaseLike.child(mPost_key).removeValue();
                                mProccessLike = false;

                            } else {

                                mDatabaseLike.child(mPost_key).child(current_user_id).setValue("RandomV");
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


        mPostCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    startCommenting();
            }
        });

    }

    private void startCommenting() {

        mProgressDialog.setTitle("Posting Your Comment...");
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCanceledOnTouchOutside(false);

        final String comment_detail = mCommentDetail.getText().toString().trim();
        final String current_uid = mCurrentUser.getUid();

        if(!TextUtils.isEmpty(comment_detail)) {

            mProgressDialog.show();
            final DatabaseReference commentPost = mCommentDatabase.push();

            SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy 'at' hh:mma");
            Date date = new Date();
            final String currentDate = df.format(date);

            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    commentPost.child("uid").setValue(current_uid);
                    commentPost.child("comment_detail").setValue(comment_detail);
                    commentPost.child("date").setValue(currentDate);
                    commentPost.child("username").setValue(dataSnapshot.child("name").getValue());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mProgressDialog.dismiss();
            mCommentDetail.getText().clear();
            Toast.makeText(SinglePostActivity.this, "Your Comment Has Been Posted.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStart(){
        super.onStart();

        FirebaseRecyclerAdapter<Comment, CommentViewHolder> commentRecyclerViewAdapter = new FirebaseRecyclerAdapter<Comment, CommentViewHolder>(

                Comment.class,
                R.layout.comment_row,
                CommentViewHolder.class,
                mCommentDatabase


        ) {
            @Override
            protected void populateViewHolder(CommentViewHolder viewHolder, Comment model, int position) {



                    viewHolder.setUsername(model.getUsername());
                    viewHolder.setComment(model.getComment_detail());
                    viewHolder.setDate(model.getDate());


            }
        };

        mCommentList.setAdapter(commentRecyclerViewAdapter);

    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public CommentViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setUsername(String username){

            TextView comment_user =(TextView)mView.findViewById(R.id.comment_user);
            comment_user.setText(username);
        }

        public void setComment(String comment_detail){

            TextView post_comment =(TextView)mView.findViewById(R.id.comment_detail);
            post_comment.setText(comment_detail);
        }

        public void setDate(String date){

            TextView comment_date =(TextView)mView.findViewById(R.id.comment_date);
            comment_date.setText(date);
        }



    }



}
