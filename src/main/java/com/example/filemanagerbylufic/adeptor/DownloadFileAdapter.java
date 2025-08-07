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

import com.example.filemanagerbylufic.CallBackDowenload;
import com.example.filemanagerbylufic.R;
import com.example.filemanagerbylufic.SqliteDbForFavorite.FavoritesDbHelper;
import com.example.filemanagerbylufic.language.LanguageUtility;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.io.File;
import java.util.List;

public class DownloadFileAdapter extends AbstractItem<DownloadFileAdapter, DownloadFileAdapter.ViewHolder> {

    Context context;
    private final File file;
    CallBackDowenload callBackDowenload;

    public DownloadFileAdapter(File file,Context context , CallBackDowenload callBackDowenload) {
        this.file = file;
        this.context = context;
        this.callBackDowenload =callBackDowenload;
    }

    public File getFile() {
        return file;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return 2001;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.dowenlode_item;
    }

    @Override
    public boolean isSelectable() {
        return  true;
    }

    static class ViewHolder extends FastAdapter.ViewHolder<DownloadFileAdapter> {

        TextView fileName, fileSize;
        ImageView fileIcon, fileOptions;
        IconicsImageView iconicsImageView;

        public ViewHolder(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.fileName);
            fileSize = itemView.findViewById(R.id.fileSize);
            fileIcon = itemView.findViewById(R.id.fileIcon);
            fileOptions = itemView.findViewById(R.id.fileOptions);
            iconicsImageView = itemView.findViewById(R.id.fevratebtnfordowenlode);
        }

        @Override
        public void bindView(DownloadFileAdapter item, List<Object> payloads) {
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



              File file = item.file;
//            fileName.setText(file.getName());
            String localizedName = LanguageUtility.getLocalizedFolderName(item.file.getName(), item.context );
            fileName.setText(localizedName);

            String readableSize = android.text.format.Formatter.formatShortFileSize(itemView.getContext(), file.length());
            fileSize.setText(readableSize);


            String name = item.file.getName().toLowerCase();
            if (item.isSelected()) {
                itemView.setBackgroundColor(Color.parseColor("#1a0000"));
                fileIcon.setImageResource(R.drawable.select);
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT);
               if(file.isDirectory()){
                   fileIcon.setImageResource(R.drawable.greenfolder);
               }else{
                   if (name.endsWith(".mp3") || name.endsWith(".wav")) {
                       fileIcon.setImageResource(R.drawable.audio);
                   } else if (name.endsWith(".mp4") || name.endsWith(".mkv")) {
                       fileIcon.setImageResource(R.drawable.video);
                   } else if (name.endsWith(".apk")) {
                       fileIcon.setImageResource(R.drawable.apk);
                   } else if (name.endsWith(".pdf")) {
                       fileIcon.setImageResource(R.drawable.pdf);
                   } else if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")) {
                       fileIcon.setImageResource(R.drawable.gallery);
                   } else {
                       fileIcon.setImageResource(R.drawable.doc);
                   }
               }
            }


            //========= Dark / Light Mode ===========
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(item.context);
            String themeValue = prefs.getString("theme_choice", "system_default");

            if ("dark".equals(themeValue)) {
                fileName.setTextColor(ContextCompat.getColor(item.context, R.color.white));
                fileSize.setTextColor(ContextCompat.getColor(item.context, R.color.white));
                itemView.setBackgroundColor(ContextCompat.getColor(item.context, R.color.black));

            } else {
                fileName.setTextColor(ContextCompat.getColor(item.context, R.color.black));
                fileSize.setTextColor(ContextCompat.getColor(item.context, R.color.black));
                itemView.setBackgroundColor(Color.WHITE);
                fileOptions.setImageResource(R.drawable.lightmodethreedot);
            }

            fileOptions.setOnClickListener(v -> {
                PopupMenu menu = new PopupMenu(itemView.getContext(), fileOptions);
                menu.getMenu().add("Open");
                menu.getMenu().add("Share");
              //  menu.getMenu().add("Delete");


                boolean isFav = dbHelper.isFavorite(file.getAbsolutePath());
                if (isFav) {
                    menu.getMenu().add("Remove from Favorite");
                } else {
                    menu.getMenu().add("Add to Favorite");
                }

                menu.setOnMenuItemClickListener(menuItem -> {
                    String title = menuItem.getTitle().toString();
                    switch (title) {
                        case "Open":
                            openFile(file);
                            return true;
                        case "Share":
                            shareFile(file);
                            return true;
                        case "Delete":
                            deleteFile(file, item.callBackDowenload);
                            return true;
                        case "Add to Favorite":
                            dbHelper.addFavorite(file.getAbsolutePath());
                            Toast.makeText(itemView.getContext(), "Added to Favorite", Toast.LENGTH_SHORT).show();
                            iconicsImageView.setVisibility(View.VISIBLE);
                            iconicsImageView.setIcon(new IconicsDrawable(itemView.getContext(), CommunityMaterial.Icon2.cmd_star)
                                    .color(ContextCompat.getColor(itemView.getContext(), R.color.Yello))
                                    .sizeDp(21));
                            return true;
                        case "Remove from Favorite":
                            dbHelper.removeFavorite(file.getAbsolutePath());
                            Toast.makeText(itemView.getContext(), "Removed from Favorite", Toast.LENGTH_SHORT).show();
                            iconicsImageView.setVisibility(View.GONE);
                            return true;
                        default:
                            return false;
                    }
                });
                menu.show();
            });


        }

        private void openFile(File file) {
            try {
                Uri uri = FileProvider.getUriForFile(itemView.getContext(),
                        itemView.getContext().getPackageName() + ".provider", file);
                String mime = itemView.getContext().getContentResolver().getType(uri);
                if (mime == null) mime = "*/*";
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, mime);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                itemView.getContext().startActivity(Intent.createChooser(intent, "Open with"));
            } catch (Exception e) {
                Toast.makeText(itemView.getContext(), "No app to open this file", Toast.LENGTH_SHORT).show();
            }
        }

        private void shareFile(File file) {
            try {
                Uri uri = FileProvider.getUriForFile(itemView.getContext(),
                        itemView.getContext().getPackageName() + ".provider", file);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                itemView.getContext().startActivity(Intent.createChooser(intent, "Share via"));
            } catch (Exception e) {
                Toast.makeText(itemView.getContext(), "Unable to share file", Toast.LENGTH_SHORT).show();
            }
        }

private void deleteFile(File file, CallBackDowenload callback) {
    new AlertDialog.Builder(itemView.getContext())
            .setTitle("Delete")
            .setMessage("Are you sure to delete this file?")
            .setPositiveButton("Yes", (dialog, which) -> {
                if (file.delete()) {
                    Toast.makeText(itemView.getContext(), "File deleted", Toast.LENGTH_SHORT).show();
                    fileName.setText("[Deleted]");

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


        @Override
        public void unbindView(DownloadFileAdapter item) {
            fileName.setText(null);
            fileSize.setText(null);
            fileIcon.setImageDrawable(null);
        }
    }
}
