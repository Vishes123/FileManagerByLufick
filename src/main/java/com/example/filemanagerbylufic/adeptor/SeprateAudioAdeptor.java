package com.example.filemanagerbylufic.adeptor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import com.example.filemanagerbylufic.CallBackAudio;
import com.example.filemanagerbylufic.R;
import com.example.filemanagerbylufic.SqliteDbForFavorite.FavoritesDbHelper;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.io.File;
import java.util.List;

public class SeprateAudioAdeptor extends AbstractItem<SeprateAudioAdeptor ,SeprateAudioAdeptor.ViewHolderr> {
    File documentFile;
    Context context;
    CallBackAudio callBackAudio;

    public SeprateAudioAdeptor(File documentFile ,Context context ,CallBackAudio callBackAudio) {
        this.documentFile = documentFile;
        this.context = context;
        this.callBackAudio = callBackAudio;
    }

    public File getFile() {
        return documentFile;
    }

    @NonNull
    @Override
    public ViewHolderr getViewHolder(View v) {
        return new ViewHolderr(v);
    }

    @Override
    public int getType() {
        return 125;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.audio_item;
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    class ViewHolderr extends FastAdapter.ViewHolder<SeprateAudioAdeptor>{

        TextView name, size;
        ImageView imageView, imageButton;
        IconicsImageView  iconicsImageView;
        LinearLayout linearLayout;
        public ViewHolderr(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.txtNamedocAudio);
            size = itemView.findViewById(R.id.txtSizedocAudio);
            imageView = itemView.findViewById(R.id.ducImageA);
            imageButton = itemView.findViewById(R.id.imageButtonAudio);
            iconicsImageView = itemView.findViewById(R.id.Audiofevratebtn);
            linearLayout = itemView.findViewById(R.id.LinearAudio);
        }

        @Override
        public void bindView(SeprateAudioAdeptor item, List<Object> payloads) {
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
            }
            else {
                itemView.setBackgroundColor(Color.TRANSPARENT);
                imageView.setImageResource(R.drawable.audio);
            }
            if(file.getName().endsWith(".zip")){
                imageView.setImageResource(R.drawable.zip);
            }


            //============= Dark / Light Mode =====================
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
                linearLayout.setBackgroundColor(Color.WHITE);
            }




            if (imageButton != null) {
                imageButton.setOnClickListener(v -> {
                    PopupMenu popupMenu = new PopupMenu(itemView.getContext(), imageButton);
                    popupMenu.getMenu().add("Share");
                  //  popupMenu.getMenu().add("Delete");
                    popupMenu.getMenu().add("Rename");

                    FavoritesDbHelper dbHelper2 = new FavoritesDbHelper(itemView.getContext());
                    boolean isFav = dbHelper2.isFavorite(file.getAbsolutePath());
                    dbHelper2.close();

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
                                deleteFile(file, item.callBackAudio);
                                return true;
                            case "Rename":
                                renameFile(file);
                                return true;
                            case "Add to Favorite": {
                                FavoritesDbHelper dbHelper1 = new FavoritesDbHelper(itemView.getContext());
                                dbHelper1.addFavorite(file.getAbsolutePath());
                                dbHelper1.close();
                                Toast.makeText(itemView.getContext(), "Added to Favorite", Toast.LENGTH_SHORT).show();
                                iconicsImageView.setVisibility(View.VISIBLE);
                                iconicsImageView.setIcon(new IconicsDrawable(itemView.getContext(), CommunityMaterial.Icon2.cmd_star)
                                        .color(ContextCompat.getColor(itemView.getContext(), R.color.Yello))
                                        .sizeDp(21));
                                return true;
                            }
                            case "Remove from Favorite": {
                                FavoritesDbHelper dbHelper3 = new FavoritesDbHelper(itemView.getContext());
                                dbHelper3.removeFavorite(file.getAbsolutePath());
                                dbHelper3.close();
                                Toast.makeText(itemView.getContext(), "Removed from Favorite", Toast.LENGTH_SHORT).show();
                                iconicsImageView.setVisibility(View.GONE);
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


        private void deleteFile(File file, CallBackAudio callback) {
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle("Delete")
                    .setMessage("Are you sure you want to delete this file?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (file.delete()) {
                            Toast.makeText(itemView.getContext(), "File deleted", Toast.LENGTH_SHORT).show();

                            if (callback != null) {
                                callback.loadFinish();
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
        public void unbindView(SeprateAudioAdeptor item) {

        }
    }
}
