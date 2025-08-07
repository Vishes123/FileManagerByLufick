package com.example.filemanagerbylufic.adeptor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.media.RouteListingPreference;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanagerbylufic.R;
import com.example.filemanagerbylufic.RestoreListener;
import com.example.filemanagerbylufic.TrashDeleteCallBack;
import com.example.filemanagerbylufic.tresh.TrashDbHelper;
import com.example.filemanagerbylufic.tresh.TrashModel;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.*;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TreshAdeptor extends AbstractItem<TreshAdeptor, TreshAdeptor.ViewHolder> {
    private final TrashModel model;
    private final Context context;
    private final TrashDbHelper dbHelper;
    private final RestoreListener listener;
    private final TrashDeleteCallBack trashDeleteCallBack;
    private final File file;

    public TreshAdeptor(Context context, TrashModel model, TrashDbHelper dbHelper, RestoreListener listener, TrashDeleteCallBack trashDeleteCallBack) {
        this.model = model;
        this.context = context;
        this.dbHelper = dbHelper;
        this.listener = listener;
        this.trashDeleteCallBack = trashDeleteCallBack;
        this.file = new File(model.getPath());
    }

    private int position;

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

    public File getFile() {
        return file;
    }

    @Override
    public int getType() {
        return 454;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.trash_item;
    }

    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    public class ViewHolder extends FastAdapter.ViewHolder<TreshAdeptor> {
        TextView fileName, fileInfo;
        ImageView moreOptions, fileIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.fileNamet);
            fileInfo = itemView.findViewById(R.id.fileInfo);
            moreOptions = itemView.findViewById(R.id.moreOptionst);
            fileIcon = itemView.findViewById(R.id.TreshView);
        }

        @Override
        public void bindView(TreshAdeptor item, List<Object> payloads) {
            File file = item.file;

            if (file != null && file.exists()) {
                fileName.setText(file.getName());
                if(item.isSelected()){
                    itemView.setBackgroundColor(Color.parseColor("#1a0000"));
                    fileIcon.setImageResource(R.drawable.select);
                }else {
                    setFileIcon(file);
                }
            } else {
                fileName.setText("Invalid File");
            }

            //fileInfo.setText(model.getSize() + " • Deleted: " + DateFormat.getDateTimeInstance().format(new Date(Long.parseLong(model.getDeletedAt()))));
            SimpleDateFormat customFormat = new SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault());
            long fileSizeInBytes = file.length();
            String fileSizeInMB = String.format(Locale.getDefault(), "%.2f MB", fileSizeInBytes / (1024.0 * 1024.0));

            fileInfo.setText(fileSizeInMB + " • Deleted: " +
                    customFormat.format(
                            model.getDeletedAt() != null && !model.getDeletedAt().isEmpty()
                                    ? new Date(Long.parseLong(model.getDeletedAt()))
                                    : new Date()
                    )
            );


            moreOptions.setOnClickListener(v -> {
                showPopupMenu(v, item.model);
            });



            if(item.isSelected()){

            }

            applyTheme();
        }

        private void showPopupMenu(View view, TrashModel selectedModel) {// int position
            PopupMenu popup = new PopupMenu(context, moreOptions);
            popup.inflate(R.menu.trash_menu);
            popup.setOnMenuItemClickListener(item -> {
                int itemId = item.getItemId();

//                if (itemId == R.id.menu_open) {
//                    openFile(model.getPath());
//                    return true;
//                }
                if (itemId == R.id.menu_restore) {
                   // restoreToDownloads(selectedModel);
                    restoreToOriginalLocation(selectedModel);
                    return true;
               } //else if (itemId == R.id.menu_delete_perm) {
////deletePermanently(file.getAbsoluteFile());
//                    removeFromTrash(new File(model.getPath()));
//
//                    return true;
//                }

                return false;
            });
            popup.show();
        }



        private boolean deleteFileRecursively(File fileOrDirectory) {
            if (fileOrDirectory == null || !fileOrDirectory.exists()) return false;

            if (fileOrDirectory.isDirectory()) {
                File[] children = fileOrDirectory.listFiles();
                if (children != null) {
                    for (File child : children) {
                        deleteFileRecursively(child);
                    }
                }
            }

            return fileOrDirectory.delete();
        }


       /* private void restoreToDownloads(File model) {
            if (listener != null) listener.onRestoreStarted();

            new Thread(() -> {
                File source = new File(model.getPath());
                File dest = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), model.getName());

                try {
                    if (dest.exists()) deleteFileRecursively(dest);

                    if (source.isDirectory()) copyDirectory(source, dest);
                    else copyFile(source, dest);

                    deleteFileRecursively(source);
                    dbHelper.removeFromTrash(model.getPath());

                    ((Activity) context).runOnUiThread(() -> {
                        if (listener != null) listener.onRestoreFinished();
                        Toast.makeText(context, "Restored", Toast.LENGTH_SHORT).show();
                        ((Activity) context).recreate();
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }*/


        private void restoreToOriginalLocation(TrashModel model) {
            if (listener != null) listener.onRestoreStarted();

            new Thread(() -> {
                File source = new File(model.getPath()); // path in trash
                File dest = new File(model.getOriginalPath()); // restore to original path

                try {
                    // Handle if something already exists at destination
                    if (dest.exists()) {
                        deleteFileRecursively(dest);
                    }

                    // Copy file or folder
                    if (source.isDirectory()) copyDirectory(source, dest);
                    else copyFile(source, dest);

                    // Remove from trash and delete from trash folder
                    deleteFileRecursively(source);
                    dbHelper.removeFromTrash(model.getPath());

                    ((Activity) context).runOnUiThread(() -> {
                        if (listener != null) listener.onRestoreFinished();
                        Toast.makeText(context, "Restored to original location", Toast.LENGTH_SHORT).show();
                        ((Activity) context).recreate();
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }







        private void copyFile(File source, File destination) throws IOException {
            try (InputStream in = new FileInputStream(source); OutputStream out = new FileOutputStream(destination)) {
                byte[] buffer = new byte[4096];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
        }

        private void copyDirectory(File sourceDir, File destDir) throws IOException {
            if (!destDir.exists()) destDir.mkdirs();
            File[] files = sourceDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    File destFile = new File(destDir, file.getName());
                    if (file.isDirectory()) copyDirectory(file, destFile);
                    else copyFile(file, destFile);
                }
            }
        }



        private void deletePermanently(File file, TrashModel model, int position) {
            Context context = itemView.getContext();

            new AlertDialog.Builder(context)
                    .setTitle("Delete Permanently")
                    .setMessage("Are you sure you want to permanently delete this file?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        boolean deleted = false;

                        if (file.exists()) {
                            deleted = file.delete();
                            Log.d("DELETE_CHECK", "File existed and deleted: " + deleted);
                        } else {
                            Log.d("DELETE_CHECK", "File does not exist: " + file.getAbsolutePath());
                        }

                        int rows = dbHelper.removeFromTrash(model.getPath());
                        Log.d("DB_REMOVE", "Rows deleted: " + rows + " for path: " + model.getPath());

                        if (deleted || rows > 0) {
                            // Tell Activity to remove this item from UI
                            if (trashDeleteCallBack != null) {
                                trashDeleteCallBack.onDelete(position);
                            }
                            Toast.makeText(context, "File deleted permanently", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "File already deleted", Toast.LENGTH_SHORT).show();
                        }

                        if (trashDeleteCallBack != null) {
                            trashDeleteCallBack.loadFinish();
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
        }

//        private void removeFromTrash(File file) {
//            TrashDbHelper dbHelper = new TrashDbHelper(context);
//            try {
//                String path = file.getAbsolutePath();
//
//                // Delete file from storage if it exists
//                if (file.exists()) {
//                    if (file.delete()) {
//                        int rows = dbHelper.removeFromTrash(path);
//                        if (rows > 0) {
//                            Toast.makeText(context, "Deleted permanently", Toast.LENGTH_SHORT).show();
//
//                            if (trashDeleteCallBack != null) {
//                                trashDeleteCallBack.onDelete(getPosition());
//                                trashDeleteCallBack.loadFinish();
//                            }
//                        } else {
//                            Toast.makeText(context, "Failed to remove from DB", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(context, "Failed to delete file", Toast.LENGTH_SHORT).show();
//                    }
//                } else {
//                    // If file doesn't exist, still remove from DB
//                    int rows = dbHelper.removeFromTrash(path);
//                    if (rows > 0) {
//                        Toast.makeText(context, "File not found, but removed from DB", Toast.LENGTH_SHORT).show();
//
//                        if (trashDeleteCallBack != null) {
//                            trashDeleteCallBack.onDelete(getPosition());
//                            trashDeleteCallBack.loadFinish();
//                        }
//                    } else {
//                        Toast.makeText(context, "File doesn't exist and couldn't remove from DB", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            } finally {
//                dbHelper.close();
//            }
//        }






//        private void openFile(String path) {
//            File file = new File(path);
//            if (!file.exists()) {
//                Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show();
//                return;
//            }
//
//            Uri uri = FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            intent.setDataAndType(uri, "*/*");
//            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//
//            try {
//                context.startActivity(intent);
//            } catch (Exception e) {
//                Toast.makeText(context, "No app found to open this file", Toast.LENGTH_SHORT).show();
//            }
//        }

        private void setFileIcon(File file) {
            String name = file.getName().toLowerCase();
            if (file.isDirectory()) fileIcon.setImageResource(R.drawable.main_icon);
            else if (name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".m4a")) fileIcon.setImageResource(R.drawable.audio);
            else if (name.endsWith(".mp4") || name.endsWith(".mkv")) fileIcon.setImageResource(R.drawable.video);
            else if (name.endsWith(".apk")) fileIcon.setImageResource(R.drawable.apk);
            else if (name.endsWith(".pdf")) fileIcon.setImageResource(R.drawable.pdf);
            else if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")) fileIcon.setImageResource(R.drawable.gallery);
            else fileIcon.setImageResource(R.drawable.doc);
        }

        private void applyTheme() {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String themeValue = prefs.getString("theme_choice", "system_default");
            if ("dark".equals(themeValue)) {
                fileName.setTextColor(ContextCompat.getColor(context, R.color.white));
                fileInfo.setTextColor(ContextCompat.getColor(context, R.color.white));
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
            } else {
                fileName.setTextColor(ContextCompat.getColor(context, R.color.black));
                fileInfo.setTextColor(ContextCompat.getColor(context, R.color.black));
                itemView.setBackgroundColor(Color.WHITE);
                moreOptions.setImageResource(R.drawable.lightmodethreedot);
            }
        }

        @Override
        public void unbindView(TreshAdeptor item) {
            fileName.setText(null);
            fileInfo.setText(null);
        }
    }
}
