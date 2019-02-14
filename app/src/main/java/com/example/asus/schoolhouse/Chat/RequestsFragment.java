package com.example.asus.schoolhouse.Chat;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.schoolhouse.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestsFragment extends Fragment {

    private RecyclerView mRequestsList;

    private DatabaseReference mFriendsRequestDatabase;
    private DatabaseReference mFriendsDatabaseRef;
    private DatabaseReference mFriendsReqDatabaseRef;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;
    private String mCurrent_user_id;

    private View mMainView;


    public RequestsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_requests, container, false);

        mRequestsList = (RecyclerView) mMainView.findViewById(R.id.requests_list);

        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsRequestDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req").child(mCurrent_user_id);
        ;
        mFriendsRequestDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        mFriendsReqDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendsDatabaseRef = FirebaseDatabase.getInstance().getReference().child("Friends");

        mRequestsList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mRequestsList.setLayoutManager(linearLayoutManager);

        // Inflate the layout for this fragment
        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Requests, RequestViewHolder> RequestsRecyclerAdapter = new FirebaseRecyclerAdapter<Requests, RequestViewHolder>
                (
                        Requests.class,
                        R.layout.friend_request_single_layout,
                        RequestViewHolder.class,
                        mFriendsRequestDatabase

                ) {
            @Override
            protected void populateViewHolder(final RequestViewHolder requestViewHolder, Requests model, int position) {

                final String list_user_id = getRef(position).getKey();

                DatabaseReference get_type_ref = getRef(position).child("request_type").getRef();

                get_type_ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            String request_type = dataSnapshot.getValue().toString();

                            if (request_type.equals("received")) {
                                //final Button mAcceptReqBtn = (Button) mMainView.findViewById(R.id.request_accept_btn);
                                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        final String userName = dataSnapshot.child("name").getValue().toString();
                                        final String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                                        final String userStatus = dataSnapshot.child("status").getValue().toString();


                                        requestViewHolder.setName(userName);
                                        requestViewHolder.setUserImage(userThumb, getContext());
                                        requestViewHolder.setUserStatus(userStatus);

                                        //mAcceptReqBtn.setOnClickListener(new View.OnClickListener() {

                                        requestViewHolder.mAcceptReqBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                CharSequence options[] = new CharSequence[]{"Accept Friend Request"};

                                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                                builder.setTitle("Do you want to Accept Friend Request?");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int position) {

                                                        //Click Event for each item.
                                                        if (position == 0) {

                                                            final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

                                                            mFriendsDatabaseRef.child(mCurrent_user_id).child(list_user_id).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    mFriendsDatabaseRef.child(list_user_id).child(mCurrent_user_id).child("date").setValue(currentDate).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {

                                                                            mFriendsReqDatabaseRef.child(mCurrent_user_id).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if (task.isSuccessful()) {
                                                                                        mFriendsReqDatabaseRef.child(list_user_id).child(mCurrent_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {

                                                                                                    Toast.makeText(getContext(), "Friend Request Accepted Successfully!", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    }
                                                                                }
                                                                            });

                                                                        }
                                                                    });

                                                                }
                                                            });

                                                        }
                                                    }
                                                });

                                                builder.show();

                                            }
                                        });

                                        requestViewHolder.mDeclineReqBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                CharSequence options[] = new CharSequence[]{"Decline Friend Request"};

                                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                                builder.setTitle("Do you want to Decline Friend Request?");
                                                builder.setItems(options, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int position) {

                                                        //Click Event for each item.

                                                        if (position == 0) {

                                                            mFriendsReqDatabaseRef.child(mCurrent_user_id).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        mFriendsReqDatabaseRef.child(list_user_id).child(mCurrent_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {

                                                                                    Toast.makeText(getContext(), "Friend Request Has Been Declined.", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });

                                                        }
                                                    }
                                                });

                                                builder.show();

                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });


                            } else if (request_type.equals("sent")) {

                                //final Button mAcceptReqBtn = (Button) mMainView.findViewById(R.id.request_accept_btn);
                                requestViewHolder.mAcceptReqBtn.setText(R.string.request_sent);

                                requestViewHolder.mView.findViewById(R.id.request_decline_btn).setVisibility(View.INVISIBLE);

                                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        final String userName = dataSnapshot.child("name").getValue().toString();
                                        final String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                                        final String userStatus = dataSnapshot.child("status").getValue().toString();


                                        requestViewHolder.setName(userName);
                                        requestViewHolder.setUserImage(userThumb, getContext());
                                        requestViewHolder.setUserStatus(userStatus);


                                        requestViewHolder.mAcceptReqBtn.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {

                                                CharSequence options[] = new CharSequence[]{"Cancel Friend Request"};

                                                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                                                builder.setTitle("Do you want to Cancel Friend Request? ");
                                                builder.setItems(options,
                                                        new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {

                                                        //Click Event for each item.
                                                        if (i == 0) {

                                                            mFriendsReqDatabaseRef.child(mCurrent_user_id).child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        mFriendsReqDatabaseRef.child(list_user_id).child(mCurrent_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                if (task.isSuccessful()) {

                                                                                    Toast.makeText(getContext(), "Friend Request Has Been Canceled.", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });

                                                        }
                                                    }
                                                });

                                                builder.show();

                                            }
                                        });

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                            }

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };

        mRequestsList.setAdapter(RequestsRecyclerAdapter);

    }


    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        View mView;
        private Button mAcceptReqBtn, mDeclineReqBtn;

        public RequestViewHolder(View itemView) {

            super(itemView);

            mView = itemView;

            mAcceptReqBtn = (Button) itemView.findViewById(R.id.request_accept_btn);
            mDeclineReqBtn = (Button) itemView.findViewById(R.id.request_decline_btn);
        }

        public void setName(String name) {

            TextView userNameView = (TextView) mView.findViewById(R.id.request_profile_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.request_profile_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

        }

        public void setUserStatus(String userStatus) {

            TextView user_status = (TextView) mView.findViewById(R.id.request_profile_status);
            user_status.setText(userStatus);

        }
    }

}
