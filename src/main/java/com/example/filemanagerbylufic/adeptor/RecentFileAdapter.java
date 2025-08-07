package com.example.filemanagerbylufic.adeptor;

import android.app.AlertDialog;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.text.format.DateFormat;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;

import com.example.filemanagerbylufic.R;
import com.example.filemanagerbylufic.imageViewrSwipeSlide.ImageViewerActivity;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecentFileAdapter extends AbstractItem<RecentFileAdapter, RecentFileAdapter.ViewHolder> {

    private final File file;
    Context context;

    public RecentFileAdapter(File file , Context context) {
        this.file = file;
        this.context = context;
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
        return 1110;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.recent_item;
    }
    @Override
    public boolean isSelectable() {
        return true;
    }

    static class ViewHolder extends FastAdapter.ViewHolder<RecentFileAdapter> {

        TextView name, modified;
        ImageView icon, options;
        LinearLayout linearLayout;
        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recentFileName);
            modified = itemView.findViewById(R.id.recentFileDate);
            icon = itemView.findViewById(R.id.recentFileIcon);
            options = itemView.findViewById(R.id.recentFileOptions);
            linearLayout = itemView.findViewById(R.id.recentItemRoot);
        }

        @Override
        public void bindView(RecentFileAdapter item, List<Object> payloads) {
            File file = item.file;
            name.setText(file.getName());

            String modTime = DateFormat.format("dd MMM yyyy, hh:mm a", new Date(file.lastModified())).toString();
            modified.setText(modTime);


            if (item.isSelected()) {
                itemView.setBackgroundColor(Color.parseColor("#1a0000"));
                icon.setImageResource(R.drawable.select);
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT);

                if (icon != null && file != null) {
                    String fileName = file.getName().toLowerCase();

                    if (fileName.endsWith(".pdf")) {
                        icon.setImageResource(R.drawable.pdf);
                    } else if (fileName.endsWith(".doc") || fileName.endsWith(".docx")) {
                        icon.setImageResource(R.drawable.doc);
                    } else if (fileName.endsWith(".txt")) {
                        icon.setImageResource(R.drawable.txt);
                    } else if (fileName.endsWith(".zip") || fileName.endsWith(".rar")) {
                        icon.setImageResource(R.drawable.zip);
                    } else if (fileName.endsWith(".mp3")) {
                        icon.setImageResource(R.drawable.audio);
                    } else if (fileName.endsWith(".mp4") || fileName.endsWith(".mkv")) {
                        icon.setImageResource(R.drawable.video);
                    } else if (fileName.endsWith(".apk")) {
                        icon.setImageResource(R.drawable.apk);
                    } else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png")) {
                        icon.setImageResource(R.drawable.gallery);
                    } else {
                        icon.setImageResource(R.drawable.doc);
                    }
                }
            }




            options.setOnClickListener(v -> {
                PopupMenu menu = new PopupMenu(itemView.getContext(), options);
                menu.getMenu().add("Open");
                menu.getMenu().add("Share");
              //  menu.getMenu().add("Delete");

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
                            deleteFile(file);
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

        private void deleteFile(File file) {
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle("Delete")
                    .setMessage("Are you sure to delete this file?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (file.delete()) {
                            Toast.makeText(itemView.getContext(), "File deleted", Toast.LENGTH_SHORT).show();
                            name.setText("[Deleted]");
                        } else {
                            Toast.makeText(itemView.getContext(), "Failed to delete", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }

        @Override
        public void unbindView(RecentFileAdapter item) {
            name.setText(null);
            modified.setText(null);
            icon.setImageDrawable(null);

            linearLayout.setBackgroundColor(Color.TRANSPARENT);
        }
    }
}
