package com.example.asus.schoolhouse;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Asus on 26/11/2017.
 */

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ViewHolder>{

    private Context context;
    private List<UploadImage> uploads;

    public ImagesAdapter(Context context, List<UploadImage> uploads) {
        this.uploads = uploads;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_images, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UploadImage upload = uploads.get(position);

        holder.textViewName.setText(upload.getName());
        holder.textViewDate.setText(upload.getDate());

        Picasso.with(context).load(upload.getUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewName;
        public ImageView imageView;
        public TextView textViewDate;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewName = (TextView) itemView.findViewById(R.id.textViewFileName);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            textViewDate = (TextView)itemView.findViewById(R.id.textViewDate);
        }
    }
}
