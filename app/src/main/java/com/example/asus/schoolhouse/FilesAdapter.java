package com.example.asus.schoolhouse;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Asus on 27/11/2017.
 */

public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.ViewHolder> {

    private Context context;
    private List<UploadFile> uploads;

    public FilesAdapter(Context context, List<UploadFile> uploads) {
        this.uploads = uploads;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_files, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        UploadFile upload = uploads.get(position);

        holder.textViewFileName.setText(upload.getUrl());
        holder.textViewFileDate.setText(upload.getDate());

    }

    @Override
    public int getItemCount() {
        return uploads.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewFileName;
        public TextView textViewFileDate;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewFileName = (TextView) itemView.findViewById(R.id.textViewFileName);
            textViewFileDate = (TextView)itemView.findViewById(R.id.textViewFileDate);
        }
    }


}
