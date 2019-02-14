package com.example.asus.schoolhouse.Chat;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.schoolhouse.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    private View mMainView;
    private RecyclerView mChatList;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    public ChatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMainView = inflater.inflate(R.layout.fragment_chats, container, false);

        mChatList = (RecyclerView) mMainView.findViewById(R.id.chat_list);

        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        mChatList.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        mChatList.setLayoutManager(linearLayoutManager);

        // Inflate the layout for this fragment
        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Chats, ChatsFragment.ChatsViewHolder> ChatsRecyclerViewAdapter = new FirebaseRecyclerAdapter<Chats, ChatsFragment.ChatsViewHolder>(

                Chats.class,
                R.layout.users_single_layout,
                ChatsFragment.ChatsViewHolder.class,
                mFriendsDatabase


        ) {
            @Override
            protected void populateViewHolder(final ChatsFragment.ChatsViewHolder viewHolder, Chats model, int position) {

                final String list_user_id = getRef(position).getKey();

                mUsersDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();
                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
                        String userStatus = dataSnapshot.child("status").getValue().toString();

                        if (dataSnapshot.hasChild("online")) {

                            String userOnline = dataSnapshot.child("online").getValue().toString();
                            viewHolder.setUserOnline(userOnline);

                        }

                        viewHolder.setName(userName);
                        viewHolder.setUserImage(userThumb, getContext());
                        viewHolder.setUserStatus(userStatus);

                        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {


                                Intent chatIntent = new Intent(getContext(), ChatActivity.class);
                                chatIntent.putExtra("user_id", list_user_id);
                                chatIntent.putExtra("user_name", userName);
                                startActivity(chatIntent);


                            }
                        });


                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        mChatList.setAdapter(ChatsRecyclerViewAdapter);

    }


    public static class ChatsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public ChatsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setName(String name) {

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Picasso.with(ctx).load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);

        }

        public void setUserOnline(String online_status) {

            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);

            if (online_status.equals("true")) {

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }


        public void setUserStatus(String userStatus) {

            TextView user_status = (TextView) mView.findViewById(R.id.user_single_status);
            user_status.setText(userStatus);

        }
    }

}
