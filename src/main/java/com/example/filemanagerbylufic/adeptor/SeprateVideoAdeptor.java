package com.example.filemanagerbylufic.adeptor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.example.filemanagerbylufic.CallBackVideo;
import com.example.filemanagerbylufic.R;
import com.example.filemanagerbylufic.SqliteDbForFavorite.FavoritesDbHelper;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.io.File;
import java.util.List;

public class SeprateVideoAdeptor extends AbstractItem<SeprateVideoAdeptor, SeprateVideoAdeptor.ViewHolder> {

    public File videoFile;
    Context context;

    CallBackVideo callBackVideo;

    public SeprateVideoAdeptor(File videoFile ,Context context ,CallBackVideo callBackVideo) {
        this.videoFile = videoFile;
        this.context = context;
        this.callBackVideo = callBackVideo;
    }

    public File getFile() {
        return videoFile;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return 125;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.sepratevideolist;
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    static class ViewHolder extends FastAdapter.ViewHolder<SeprateVideoAdeptor> {

        ImageView videoThumbnail, videoOptions;
        TextView videoName, videoSize;
        IconicsImageView iconicsImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            videoThumbnail = itemView.findViewById(R.id.videoThumbnail);
            videoName = itemView.findViewById(R.id.videoName);
            videoSize = itemView.findViewById(R.id.videoSize);
            videoOptions = itemView.findViewById(R.id.videoOptions);
            iconicsImageView =  itemView.findViewById(R.id.Videofevratebtn);
        }

        @Override
        public void bindView(SeprateVideoAdeptor item, List<Object> payloads) {
            //=============fav icon=====================
            File file2 = item.getFile();
            FavoritesDbHelper dbHelper = new FavoritesDbHelper(item.context);
            dbHelper.close();

            if (dbHelper.isFavorite(file2.getAbsolutePath())) {
                iconicsImageView.setVisibility(View.VISIBLE);
                iconicsImageView.setIcon(new IconicsDrawable(itemView.getContext(), CommunityMaterial.Icon2.cmd_star)
                        .color(ContextCompat.getColor(itemView.getContext(), R.color.Yello))
                        .sizeDp(21));
            } else {
                iconicsImageView.setVisibility(View.GONE);
            }
            //====================================================

            File file = item.videoFile;

            videoName.setText(file.getName());

            // Selected state
            if (item.isSelected()) {
                itemView.setBackgroundColor(Color.parseColor("#1a0000"));
                videoThumbnail.setImageResource(R.drawable.select);
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT);
                // videoThumbnail.setImageResource(R.drawable.video);


                // Size
                long fileSizeInBytes = file.length();
                String readableSize = android.text.format.Formatter.formatShortFileSize(itemView.getContext(), fileSizeInBytes);
                videoSize.setText(readableSize);

                // Glide thumbnail loading
                Glide.with(itemView.getContext())
                        .load(file)                         // Pass File directly, NOT Uri
                        .thumbnail(0.1f)                    // Low-res preview
                        .placeholder(R.drawable.video)      // Placeholder
                        .error(R.drawable.video)            // Fallback if fail
                        .into(videoThumbnail);
            }
            // Options menu
//            if (videoOptions != null) {
//                videoOptions.setOnClickListener(v -> {
//                    PopupMenu popupMenu = new PopupMenu(itemView.getContext(), videoOptions);
//                    popupMenu.getMenu().add("Share");
//                    popupMenu.getMenu().add("Delete");
//                    popupMenu.getMenu().add("Rename");
//
//                    popupMenu.setOnMenuItemClickListener(menuItem -> {
//                        switch (menuItem.getTitle().toString()) {
//                            case "Share":   shareFile(file); return true;
//                            case "Delete":  deleteFile(file); return true;
//                            case "Rename":  renameFile(file); return true;
//                            default:        return false;
//                        }
//                    });
//                    popupMenu.show();
//                });
//            }
            if (videoOptions != null) {
                videoOptions.setOnClickListener(v -> {
                    PopupMenu popupMenu = new PopupMenu(itemView.getContext(), videoOptions);
                    popupMenu.getMenu().add("Share");
                   // popupMenu.getMenu().add("Delete");
                    popupMenu.getMenu().add("Rename");

                    // check favorite status
                    FavoritesDbHelper dbHelperCheck = new FavoritesDbHelper(itemView.getContext());
                    boolean isFav = dbHelperCheck.isFavorite(file.getAbsolutePath());
                    dbHelperCheck.close();

                    if (isFav) {
                        popupMenu.getMenu().add("Remove from Favorite");
                    } else {
                        popupMenu.getMenu().add("Add to Favorite");
                    }

                    popupMenu.setOnMenuItemClickListener(menuItem -> {
                        switch (menuItem.getTitle().toString()) {
                            case "Share":
                                shareFile(file);
                                return true;
                            case "Delete":

                                deleteFile(file, item.callBackVideo);
                                return true;
                            case "Rename":
                                renameFile(file);
                                return true;
                            case "Add to Favorite": {
                                FavoritesDbHelper dbHelperAdd = new FavoritesDbHelper(itemView.getContext());
                                dbHelperAdd.addFavorite(file.getAbsolutePath());
                                dbHelperAdd.close();
                                Toast.makeText(itemView.getContext(), "Added to Favorite", Toast.LENGTH_SHORT).show();
                                iconicsImageView.setVisibility(View.VISIBLE);
                                iconicsImageView.setIcon(new IconicsDrawable(itemView.getContext(), CommunityMaterial.Icon2.cmd_star)
                                        .color(ContextCompat.getColor(itemView.getContext(), R.color.Yello))
                                        .sizeDp(21));
                                return true;
                            }
                            case "Remove from Favorite": {
                                FavoritesDbHelper dbHelperRemove = new FavoritesDbHelper(itemView.getContext());
                                dbHelperRemove.removeFavorite(file.getAbsolutePath());
                                dbHelperRemove.close();
                                Toast.makeText(itemView.getContext(), "Removed from Favorite", Toast.LENGTH_SHORT).show();

                                if (iconicsImageView != null) {
                                    iconicsImageView.setVisibility(View.GONE);
                                }
                                return true;
                            }
                            default:
                                return false;
                        }
                    });
                    popupMenu.show();
                });
            }

//============= Dark / Light Mode =============
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(item.context);
            String themeValue = prefs.getString("theme_choice", "system_default");

            if ("dark".equals(themeValue)) {
                videoName.setTextColor(ContextCompat.getColor(item.context, R.color.white));
                videoSize.setTextColor(ContextCompat.getColor(item.context, R.color.white));
                itemView.setBackgroundColor(ContextCompat.getColor(item.context, R.color.black));

            } else {
                videoName.setTextColor(ContextCompat.getColor(item.context, R.color.black));
                videoSize.setTextColor(ContextCompat.getColor(item.context, R.color.black));
                itemView.setBackgroundColor(Color.WHITE);
                videoOptions.setImageResource(R.drawable.lightmodethreedot);
            }

        }



        private void shareFile(File file) {
            try {
                Uri fileUri = FileProvider.getUriForFile(
                        itemView.getContext(),
                        itemView.getContext().getPackageName() + ".provider",
                        file
                );

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("video/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                itemView.getContext().startActivity(Intent.createChooser(shareIntent, "Share video via"));
            } catch (Exception e) {
                Toast.makeText(itemView.getContext(), "Failed to share", Toast.LENGTH_SHORT).show();
            }
        }

private void deleteFile(File file, CallBackVideo callBackVideo) {
    new AlertDialog.Builder(itemView.getContext())
            .setTitle("Delete")
            .setMessage("Are you sure you want to delete this video?")
            .setPositiveButton("Yes", (dialog, which) -> {
                if (callBackVideo != null) callBackVideo.loadStarted();

                if (file.delete()) {
                    Toast.makeText(itemView.getContext(), "Video deleted", Toast.LENGTH_SHORT).show();

                    if (callBackVideo != null) {
                        callBackVideo.loadFinish();
                    }

                } else {
                    Toast.makeText(itemView.getContext(), "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            })
            .setNegativeButton("No", null)
            .show();
}


        private void renameFile(File file) {
            android.widget.EditText editText = new android.widget.EditText(itemView.getContext());
            editText.setText(file.getName());

            new AlertDialog.Builder(itemView.getContext())
                    .setTitle("Rename Video")
                    .setView(editText)
                    .setPositiveButton("Rename", (dialog, which) -> {
                        String newName = editText.getText().toString().trim();
                        if (!newName.isEmpty()) {
                            File newFile = new File(file.getParent(), newName);
                            if (file.renameTo(newFile)) {
                                Toast.makeText(itemView.getContext(), "Renamed successfully", Toast.LENGTH_SHORT).show();
                                videoName.setText(newFile.getName());
                            } else {
                                Toast.makeText(itemView.getContext(), "Rename failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        @Override
        public void unbindView(SeprateVideoAdeptor item) {
            videoName.setText(null);
            videoSize.setText(null);
            videoThumbnail.setImageBitmap(null);
        }
    }
}
