package com.example.asus.schoolhouse.ProgressionActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.asus.schoolhouse.R;
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
import com.theartofdev.edmodo.cropper.CropImage;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PostActivity extends AppCompatActivity {

    private ImageButton mSelectImage;
    private EditText mPostTitle;
    private EditText mPostDesc;
    private EditText mPostUrl;
    private Button mSubmitBtn;

    private Uri mImageUri;
    private Uri resultUri;
    private static final int GALLERY_PICK=1;

    private StorageReference mImageStorage;
    private DatabaseReference mPostDatabase;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mUsersDatabase;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        getSupportActionBar().setTitle("Create A New Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSelectImage=(ImageButton)findViewById(R.id.imageSelect);
        mPostTitle=(EditText)findViewById(R.id.titleField);
        mPostDesc=(EditText)findViewById(R.id.descField);
        mPostUrl=(EditText)findViewById(R.id.urlField);
        mSubmitBtn=(Button)findViewById(R.id.submitPost);

        mProgressDialog = new ProgressDialog(this);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mImageStorage = FirebaseStorage.getInstance().getReference();
        mPostDatabase = FirebaseDatabase.getInstance().getReference().child("Post");
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        mSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_PICK);

            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startPosting();

            }
        });
    }

    private void startPosting() {

        mProgressDialog.setTitle("Posting...");
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.setCanceledOnTouchOutside(false);


        final String title_val = mPostTitle.getText().toString().trim();
        final String desc_val = mPostDesc.getText().toString().trim();
        final String url_val = mPostUrl.getText().toString().trim();
        final String current_uid = mCurrentUser.getUid();

        if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val)  && mImageUri !=null){

            mProgressDialog.show();
            StorageReference filepath = mImageStorage.child("post_images").child(mImageUri.getLastPathSegment());

            filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    final Uri downloadUrl = taskSnapshot.getDownloadUrl();

                    final DatabaseReference newPost = mPostDatabase.push();

                    SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy 'at' hh:mma");
                    Date date = new Date();
                    final String currentDate = df.format(date);

                    mUsersDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newPost.child("title").setValue(title_val);
                            newPost.child("desc").setValue(desc_val);
                            newPost.child("image").setValue(downloadUrl.toString());
                            newPost.child("uid").setValue(current_uid);
                            newPost.child("date").setValue(currentDate);
                            newPost.child("username").setValue(dataSnapshot.child("name").getValue());

                            if(!TextUtils.isEmpty(url_val)){

                                newPost.child("url").setValue(url_val);

                            }

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                    mProgressDialog.dismiss();

                    Intent mainIntent = new Intent(PostActivity.this, NavDrawer.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();


                }
            });
        }
        if(!TextUtils.isEmpty(title_val) && !TextUtils.isEmpty(desc_val) && mImageUri == null){

            mProgressDialog.show();

            mPostDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    final DatabaseReference newPost = mPostDatabase.push();

                    SimpleDateFormat df = new SimpleDateFormat("EEE, d MMM yyyy 'at' hh:mma");
                    Date date = new Date();
                    final String currentDate = df.format(date);

                    //final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                    mUsersDatabase.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            newPost.child("title").setValue(title_val);
                            newPost.child("desc").setValue(desc_val);
                            newPost.child("uid").setValue(current_uid);
                            newPost.child("date").setValue(currentDate);
                            newPost.child("username").setValue(dataSnapshot.child("name").getValue());

                            if(!TextUtils.isEmpty(url_val)){

                                newPost.child("url").setValue(url_val);

                            }


                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    mProgressDialog.dismiss();

                    Intent mainIntent = new Intent(PostActivity.this, NavDrawer.class);
                    mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(mainIntent);
                    finish();

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            /*final DatabaseReference newPost = mPostDatabase.push();

            newPost.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Map chatAddMap = new HashMap();
                    chatAddMap.put("title", title_val);
                    chatAddMap.put("desc", desc_val);

                    newPost.updateChildren(chatAddMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null) {

                                Log.d("CHAT_LOG", databaseError.getMessage().toString());

                            }

                        }
                    });

                    mProgressDialog.dismiss();

                    startActivity(new Intent(PostActivity.this, NavDrawer.class));

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });*/

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

            resultUri = data.getData();

            CropImage.activity(resultUri)
                    .setAspectRatio(1, 1)
                    .setMinCropWindowSize(500, 500)
                    .start(this);



        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK) {

                mImageUri = result.getUri();

                mSelectImage.setImageURI(mImageUri);

            }else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }
}
