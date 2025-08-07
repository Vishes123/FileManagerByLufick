package com.example.filemanagerbylufic.adeptor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import com.example.filemanagerbylufic.CallBackDocument;
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

public class SeprateDoucumentAdepter extends AbstractItem<SeprateDoucumentAdepter, SeprateDoucumentAdepter.ViewHolder> {

    File documentFile;
    Context  context;
    CallBackDocument callBackDocument;

    public SeprateDoucumentAdepter(File documentFile,Context context,CallBackDocument callBackDocument) {
        this.documentFile = documentFile;
        this.context = context;
        this.callBackDocument = callBackDocument;
    }

    public File getFile() {
        return documentFile;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return 132;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.seprate_document_item;
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    static class ViewHolder extends FastAdapter.ViewHolder<SeprateDoucumentAdepter> {
        TextView name, size;
        ImageView imageView, imageButton;
        IconicsImageView iconicsImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtNamedoc);
            size = itemView.findViewById(R.id.txtSizedoc);
            imageView = itemView.findViewById(R.id.ducImage);
            imageButton = itemView.findViewById(R.id.imageButtond);
            iconicsImageView = itemView.findViewById(R.id.Dockfevratebtn);
        }

        @Override
        public void bindView(SeprateDoucumentAdepter item, List<Object> payloads) {
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

            File file = item.documentFile;

            name.setText(file.getName());

            long fileSizeInBytes = file.length();
            String readableSize = android.text.format.Formatter.formatShortFileSize(itemView.getContext(), fileSizeInBytes);
            size.setText(readableSize);
            if (item.isSelected()) {
                itemView.setBackgroundColor(Color.parseColor("#1a0000"));
                imageView.setImageResource(R.drawable.select);
            } else {
                if (imageView != null) {
                    String fileName = file.getName().toLowerCase();
                    if (fileName.endsWith(".pdf")) {
                        imageView.setImageResource(R.drawable.pdf);
                    } else if (fileName.endsWith(".docx")) {
                        imageView.setImageResource(R.drawable.doc);
                    } else if (fileName.endsWith(".txt")) {
                        imageView.setImageResource(R.drawable.txt);
                    } else if (fileName.endsWith(".zip")) {
                        imageView.setImageResource(R.drawable.zip);
                    }
                }
            }

            //============= Dark / Light Mode =============
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(item.context);
            String themeValue = prefs.getString("theme_choice", "system_default");

            if ("dark".equals(themeValue)) {
                name.setTextColor(ContextCompat.getColor(item.context, R.color.white));
                size.setTextColor(ContextCompat.getColor(item.context, R.color.white));
                itemView.setBackgroundColor(ContextCompat.getColor(item.context, R.color.black));

            } else {
                name.setTextColor(ContextCompat.getColor(item.context, R.color.black));
                size.setTextColor(ContextCompat.getColor(item.context, R.color.black));
                itemView.setBackgroundColor(Color.WHITE);
                imageButton.setImageResource(R.drawable.lightmodethreedot);
            }




            if (imageButton != null) {
                imageButton.setOnClickListener(v -> {
                    PopupMenu popupMenu = new PopupMenu(itemView.getContext(), imageButton);
                    popupMenu.getMenu().add("Share");
                   // popupMenu.getMenu().add("Delete");
                    popupMenu.getMenu().add("Rename");

                    FavoritesDbHelper dbHelperCheck = new FavoritesDbHelper(itemView.getContext());
                    boolean isFav = dbHelperCheck.isFavorite(file.getAbsolutePath());
                    dbHelperCheck.close();

                    if (isFav) {
                        popupMenu.getMenu().add("Remove from Favorite");
                    } else {
                        popupMenu.getMenu().add("Add to Favorite");
                    }

                    popupMenu.setOnMenuItemClickListener(menuItem -> {
                        String title = menuItem.getTitle().toString();
                        switch (title) {
                            case "Share":
                                shareFile(file);
                                return true;
                            case "Delete":
                                deleteFile(file, item.callBackDocument);
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



        }

        private void shareFile(File file) {
            try {
                Uri fileUri = FileProvider.getUriForFile(
                        itemView.getContext(),
                        itemView.getContext().getPackageName() + ".provider",
                        file
                );

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("*/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                itemView.getContext().startActivity(Intent.createChooser(shareIntent, "Share file via"));
            } catch (Exception e) {
                Toast.makeText(itemView.getContext(), "Failed to share", Toast.LENGTH_SHORT).show();
            }
        }

        private void deleteFile(File file, CallBackDocument callBackVideo) {
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
                    .setTitle("Rename File")
                    .setView(editText)
                    .setPositiveButton("Rename", (dialog, which) -> {
                        String newName = editText.getText().toString().trim();
                        if (!newName.isEmpty()) {
                            File newFile = new File(file.getParent(), newName);
                            if (file.renameTo(newFile)) {
                                Toast.makeText(itemView.getContext(), "Renamed successfully", Toast.LENGTH_SHORT).show();
                                name.setText(newFile.getName());
                            } else {
                                Toast.makeText(itemView.getContext(), "Rename failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        @Override
        public void unbindView(SeprateDoucumentAdepter item) {
            name.setText(null);
            size.setText(null);
        }
    }
}
