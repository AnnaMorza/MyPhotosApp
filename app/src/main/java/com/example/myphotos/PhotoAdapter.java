package com.example.myphotos;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {

    public interface OnPhotoClickListener {
        void onPhotoClick(int position);
    }

    private final List<Uri> photoUris;
    private final OnPhotoClickListener listener;

    public PhotoAdapter(List<Uri> photoUris, OnPhotoClickListener listener) {
        this.photoUris = photoUris;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_photo_thumbnail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Uri uri = photoUris.get(position);
        Glide.with(holder.itemView.getContext())
                .load(uri)
                .centerCrop()
                .into(holder.thumbnailImage);

        holder.itemView.setOnClickListener(v -> listener.onPhotoClick(position));
    }

    @Override
    public int getItemCount() {
        return photoUris.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnailImage;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            thumbnailImage = itemView.findViewById(R.id.thumbnailImage);
        }
    }
}
