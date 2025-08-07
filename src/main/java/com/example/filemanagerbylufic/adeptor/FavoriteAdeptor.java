package com.example.filemanagerbylufic.adeptor;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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

import com.bumptech.glide.Glide;
import com.example.filemanagerbylufic.R;
import com.example.filemanagerbylufic.SqliteDbForFavorite.FavoritesDbHelper;
import com.example.filemanagerbylufic.language.LanguageUtility;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.File;
import java.util.Date;
import java.util.List;

public class FavoriteAdeptor extends AbstractItem<FavoriteAdeptor, FavoriteAdeptor.ViewHolder> {

    private final File file;
    Context context;

    public FavoriteAdeptor(File file , Context context) {
        this.file = file;
        this.context = context;
    }

    public File getFile() {
        return file;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return 121;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.fav_itemforrecycle;
    }

    static class ViewHolder extends FastAdapter.ViewHolder<FavoriteAdeptor> {
        ImageView imageView, FavmoreOptions;
        TextView textViewName, textViewDetails;
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.FavimageIcon2);
            textViewName = itemView.findViewById(R.id.FavtextName2);
            textViewDetails = itemView.findViewById(R.id.FavtextDetails2);
            FavmoreOptions = itemView.findViewById(R.id.FavmoreOptions2);
            linearLayout = itemView.findViewById(R.id.FavLinear);
        }

        @Override
        public void bindView(FavoriteAdeptor item, List<Object> payloads) {

            File file  = item.getFile();
            String name = file.getName();
            String localizedName = LanguageUtility.getLocalizedFolderName(file.getName(), item.context);
            textViewName.setText(localizedName);

            String details = "";
            if (file.isFile()) {
                details += getReadableFileSize(file.length()) + " · ";
            }
            details += android.text.format.DateFormat.format("dd MMM yyyy hh:mm a", new Date(file.lastModified()));
            textViewDetails.setText(details);

//====================================Day/Light=============================================
            SharedPreferences prefs2 = PreferenceManager.getDefaultSharedPreferences(itemView.getContext());
            String value = prefs2.getString("theme_choice", "system_default");


            if ("light".equals(value)) {
                linearLayout.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.gride_seprate));
                textViewName.setTextColor(Color.BLACK);
                textViewDetails.setTextColor(Color.DKGRAY);

            } else if ("dark".equals(value)) {
                linearLayout.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_card_dark));
                textViewName.setTextColor(Color.WHITE);
                textViewDetails.setTextColor(Color.LTGRAY);

            } else {
                int currentNightMode = itemView.getContext().getResources().getConfiguration().uiMode
                        & Configuration.UI_MODE_NIGHT_MASK;

                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    linearLayout.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.bg_card_dark));
                    textViewName.setTextColor(Color.WHITE);
                    textViewDetails.setTextColor(Color.LTGRAY);
                } else {
                    linearLayout.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.gride_seprate));
                    textViewName.setTextColor(Color.BLACK);
                    textViewDetails.setTextColor(Color.DKGRAY);
                }
            }




            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(item.context);
            boolean isSwitchOn = prefs.getBoolean("Circularicon", false);

            if (isSwitchOn) {

                if (file.isDirectory()) {
                    imageView.setImageResource(R.drawable.circulfolder);
                } else if (isImageFile(name)) {
                    Glide.with(itemView.getContext())
                            .load(file)
                            .placeholder(R.drawable.favoritefile)
                            .into(imageView);
                } else if (name.endsWith(".pdf")) {
                    imageView.setImageResource(R.drawable.circulpdf);
                } else if (name.endsWith(".doc") || name.endsWith(".docx")) {
                    imageView.setImageResource(R.drawable.circledoc);
                } else if (name.endsWith(".txt")) {
                    imageView.setImageResource(R.drawable.txt);
                } else if (name.endsWith(".zip")) {
                    imageView.setImageResource(R.drawable.circlezip);
                } else if (name.endsWith(".apk")) {
                    imageView.setImageResource(R.drawable.circleapk);
                } else if (isVideoFile(name)) {
                    imageView.setImageResource(R.drawable.circlevideo);
                } else if (isAudioFile(name)) {
                    imageView.setImageResource(R.drawable.circlemusic);
                } else {
                    imageView.setImageResource(R.drawable.favoritefile);
                }


            }else {

                if (file.isDirectory()) {
                    imageView.setImageResource(R.drawable.main_icon);
                } else if (isImageFile(name)) {
                    Glide.with(itemView.getContext())
                            .load(file)
                            .placeholder(R.drawable.favoritefile)
                            .into(imageView);
                } else if (name.endsWith(".pdf")) {
                    imageView.setImageResource(R.drawable.pdf);
                } else if (name.endsWith(".doc") || name.endsWith(".docx")) {
                    imageView.setImageResource(R.drawable.doc);
                } else if (name.endsWith(".txt")) {
                    imageView.setImageResource(R.drawable.txt);
                } else if (name.endsWith(".zip")) {
                    imageView.setImageResource(R.drawable.zip);
                } else if (name.endsWith(".apk")) {
                    imageView.setImageResource(R.drawable.apk);
                } else if (isVideoFile(name)) {
                    imageView.setImageResource(R.drawable.video);
                } else if (isAudioFile(name)) {
                    imageView.setImageResource(R.drawable.audio);
                } else {
                    imageView.setImageResource(R.drawable.favoritefile);
                }
            }
            FavmoreOptions.setOnClickListener(v -> {
                PopupMenu popupMenu = new PopupMenu(itemView.getContext(), FavmoreOptions);
                popupMenu.getMenu().add("Share");
                popupMenu.getMenu().add("Delete");
                popupMenu.getMenu().add("Rename");

                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    String title = menuItem.getTitle().toString();

                    if (title.equals("Share")) {
                        shareFile(file);
                        return true;
                    } else if (title.equals("Delete")) {
                        deleteFile(file);
                        return true;
                    } else if (title.equals("Rename")) {
                        renameFile(file);
                        return true;
                    }

                    return false;
                });
                popupMenu.show();

            });
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

        private void deleteFile(File file) {
            new AlertDialog.Builder(itemView.getContext())
                    .setTitle("Delete")
                    .setMessage("Are you sure you want to delete this file?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        if (file.delete()) {
                            Toast.makeText(itemView.getContext(), "File deleted", Toast.LENGTH_SHORT).show();

                            FavoritesDbHelper dbHelper = new FavoritesDbHelper(itemView.getContext());
                            dbHelper.removeFavorite(file.getAbsolutePath());
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
                                textViewName.setText(newFile.getName());
                            } else {
                                Toast.makeText(itemView.getContext(), "Rename failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        @Override
        public void unbindView(FavoriteAdeptor item) {
            textViewName.setText(null);
            textViewDetails.setText(null);
            imageView.setImageDrawable(null);
        }

        private String getReadableFileSize(long size) {
            if (size <= 0) return "0 B";
            final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
            int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
            return String.format("%.1f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
        }

        private boolean isImageFile(String name) {
            String lower = name.toLowerCase();
            return lower.endsWith(".png") || lower.endsWith(".jpg") || lower.endsWith(".jpeg")
                    || lower.endsWith(".gif") || lower.endsWith(".bmp") || lower.endsWith(".webp");
        }

        private boolean isVideoFile(String name) {
            String lower = name.toLowerCase();
            return lower.endsWith(".mp4") || lower.endsWith(".mkv") || lower.endsWith(".avi")
                    || lower.endsWith(".mov") || lower.endsWith(".wmv");
        }

        private boolean isAudioFile(String name) {
            String lower = name.toLowerCase();
            return lower.endsWith(".mp3") || lower.endsWith(".wav") || lower.endsWith(".aac")
                    || lower.endsWith(".ogg") || lower.endsWith(".flac")||lower.endsWith(".m4a");
        }
    }
}
