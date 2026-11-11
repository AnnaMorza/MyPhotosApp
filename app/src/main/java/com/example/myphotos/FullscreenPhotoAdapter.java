package com.example.myphotos;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class FullscreenPhotoAdapter extends RecyclerView.Adapter<FullscreenPhotoAdapter.ViewHolder> {

    private final List<Uri> photoUris;
    private final Runnable onTapListener;

    public FullscreenPhotoAdapter(List<Uri> photoUris, Runnable onTapListener) {
        this.photoUris = photoUris;
        this.onTapListener = onTapListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_fullscreen_photo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.photoView.setOnViewTapListener((view, x, y) -> {
            if (onTapListener != null) {
                onTapListener.run();
            }
        });
        Uri uri = photoUris.get(position);

        // Сбрасываем зум перед загрузкой нового фото в этот ViewHolder
        holder.photoView.setScale(1f, false);

        Glide.with(holder.itemView.getContext())
                .load(uri)
                .fitCenter()
                .into(holder.photoView);
    }

    @Override
    public int getItemCount() {
        return photoUris.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        PhotoView photoView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            photoView = itemView.findViewById(R.id.fullscreenImage);
        }
    }
}
