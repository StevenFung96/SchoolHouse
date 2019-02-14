package com.example.asus.schoolhouse;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShowImagesActivity extends AppCompatActivity {

        private RecyclerView recyclerView;

        //adapter object
        private RecyclerView.Adapter adapter;

        private FirebaseUser mCurrentUser;

        //database reference
        private DatabaseReference mDatabase;
        private DatabaseReference mImage_uid;

        //progress dialog
        private ProgressDialog progressDialog;

        //list to hold all the uploaded images
        private List<UploadImage> uploads;

        @Override
        protected void onCreate (Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_show_images);

            getSupportActionBar().setTitle("Submission");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.setReverseLayout(true);
            mLayoutManager.setStackFromEnd(true);
            recyclerView.setLayoutManager(mLayoutManager);


            progressDialog = new ProgressDialog(this);

            uploads = new ArrayList<>();

            //displaying progress dialog while fetching images
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
            mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS);
            mDatabase.keepSynced(true);
            //mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            //final String current_uid = mCurrentUser.getUid();

            //final String mDatabaseKey = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS).getKey();
            //mImage_uid = mDatabase.child(mDatabaseKey).child("uid");


            //adding an event listener to fetch values
            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {

                    //dismissing the progress dialog
                    progressDialog.dismiss();
                    //final String mDatabaseKey = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_UPLOADS).getKey();
                    //final String image_uid = (String)snapshot.child(mDatabaseKey).child("uid").getValue();
                    //if(current_uid.equals(image_uid)) {

                    //iterating through all the values in database
                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                        UploadImage upload = postSnapshot.getValue(UploadImage.class);
                        uploads.add(upload);
                    }
                    //creating adapter
                    adapter = new ImagesAdapter(getApplicationContext(), uploads);

                    //adding adapter to recyclerview
                    recyclerView.setAdapter(adapter);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                    progressDialog.dismiss();
                }
            });


        }


}
