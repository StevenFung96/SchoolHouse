package com.example.asus.schoolhouse.Chat;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.asus.schoolhouse.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Asus on 20/10/2017.
 */

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private List<Messages> mMessageList;
    private FirebaseAuth mAuth;
    private DatabaseReference mUserDatabase;

    public MessageAdapter(List<Messages> mMessageList) {

        this.mMessageList = mMessageList;

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent, false);


        return new MessageViewHolder(v);

    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        mAuth = FirebaseAuth.getInstance();

        Messages c = mMessageList.get(i);

        String from_user = c.getFrom();
        String message_type = c.getType();

        if (mAuth.getCurrentUser() != null) {
            String current_user_id = mAuth.getCurrentUser().getUid();
            mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(from_user);

            mUserDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    //String name = dataSnapshot.child("name").getValue().toString();
                    String image = dataSnapshot.child("thumb_image").getValue().toString();

                    //viewHolder.displayName.setText(name);

                    Picasso.with(viewHolder.profileImage.getContext()).load(image)
                            .placeholder(R.drawable.default_avatar).into(viewHolder.profileImage);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            if (from_user.equals(current_user_id)) {

                viewHolder.messageText.setBackgroundResource(R.drawable.message_text_background);
                viewHolder.messageText.setTextColor(Color.WHITE);
                viewHolder.messageText.setGravity(Gravity.END);

            } else {

                viewHolder.messageText.setBackgroundResource(R.drawable.message2_text_background);
                viewHolder.messageText.setTextColor(Color.BLACK);
                viewHolder.messageText.setGravity(Gravity.START);

            }

            if (message_type.equals("text")) {

                viewHolder.messageText.setText(c.getMessage());
                viewHolder.messageImage.setVisibility(View.INVISIBLE);

            } else {

                viewHolder.messageText.setVisibility(View.INVISIBLE);

                Picasso.with(viewHolder.profileImage.getContext()).load(c.getMessage())
                        .placeholder(R.drawable.default_avatar).into(viewHolder.messageImage);

            }

            viewHolder.messageText.setText(c.getMessage());

        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView messageText;
        public CircleImageView profileImage;
        public ImageView messageImage;
        //public RelativeLayout messageLayout;
        //public TextView displayName;

        public MessageViewHolder(View view) {
            super(view);

            messageText = (TextView) view.findViewById(R.id.message_text_layout);
            profileImage = (CircleImageView) view.findViewById(R.id.message_profile_layout);
            messageImage = (ImageView) view.findViewById(R.id.message_image_layout);
            //RelativeLayout messageLayout= (RelativeLayout) view.findViewById(R.id.message_single_layout);

            //displayName = (TextView) view.findViewById(R.id.name_text_layout);

        }
    }


}
