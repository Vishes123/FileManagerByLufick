package com.example.filemanagerbylufic.AIS;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.example.filemanagerbylufic.R;
import com.example.filemanagerbylufic.SqliteDbForFavorite.FavoritesDbHelper;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.io.File;
import java.text.DecimalFormat;
import java.util.List;

public class FileItem extends AbstractItem<FileItem, FileItem.ViewHolder> {
    private final File file;

    public FileItem(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    @Override
    public int getType() {
        return 121;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_file;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(@NonNull View v) {
        return new ViewHolder(v);
    }

    protected static class ViewHolder extends FastAdapter.ViewHolder<FileItem> {
        TextView tvName, tvSize;
        ImageView ducImageA;
        IconicsImageView Audiofevratebtn;
        View bgLayout;

        Context context;

        ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            tvName = itemView.findViewById(R.id.tvName);
            tvSize = itemView.findViewById(R.id.tvSize);
            ducImageA = itemView.findViewById(R.id.ducImageA);
            Audiofevratebtn = itemView.findViewById(R.id.Audiofevratebtn);
            bgLayout = itemView.findViewById(R.id.LinearAudio);
        }

        @Override
        public void bindView(@NonNull FileItem item, @NonNull List<Object> payloads) {
            File f = item.getFile();
            tvName.setText(f.getName());
            tvSize.setText(readableFileSize(f.length()));

            FavoritesDbHelper dbHelper = new FavoritesDbHelper(context);
            if (dbHelper.isFavorite(f.getAbsolutePath())) {
                Audiofevratebtn.setVisibility(View.VISIBLE);
                Audiofevratebtn.setIcon(new IconicsDrawable(context, CommunityMaterial.Icon2.cmd_star)
                        .color(ContextCompat.getColor(context, R.color.Yello))
                        .sizeDp(21));
            } else {
                Audiofevratebtn.setVisibility(View.GONE);
            }

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String theme = prefs.getString("theme_choice", "system_default");

            if ("dark".equals(theme)) {
                tvName.setTextColor(Color.WHITE);
                tvSize.setTextColor(Color.LTGRAY);
                bgLayout.setBackgroundColor(Color.parseColor("#010312"));
            } else {
                tvName.setTextColor(Color.BLACK);
                tvSize.setTextColor(Color.DKGRAY);
                bgLayout.setBackgroundColor(Color.WHITE);
            }

            boolean isCircular = prefs.getBoolean("Circularicon", false);
            String name = f.getName().toLowerCase();

            if (f.isDirectory()) {
                ducImageA.setImageResource(isCircular ? R.drawable.circulfolder : R.drawable.main_icon);
            } else if (isImageFile(name)) {
                Glide.with(context)
                        .load(f)
                        .placeholder(R.drawable.gallery)
                        .apply(isCircular ? new com.bumptech.glide.request.RequestOptions().circleCrop() : new com.bumptech.glide.request.RequestOptions())
                        .into(ducImageA);
            } else if (name.endsWith(".pdf")) {
                ducImageA.setImageResource(isCircular ? R.drawable.circulpdf : R.drawable.pdf);
            } else if (name.endsWith(".doc") || name.endsWith(".docx")) {
                ducImageA.setImageResource(isCircular ? R.drawable.circledoc : R.drawable.doc);
            } else if (name.endsWith(".txt")) {
                ducImageA.setImageResource(R.drawable.txt);
            } else if (name.endsWith(".zip")) {
                ducImageA.setImageResource(isCircular ? R.drawable.circlezip : R.drawable.zip);
            } else if (name.endsWith(".apk")) {
                ducImageA.setImageResource(isCircular ? R.drawable.circleapk : R.drawable.apk);
            } else if (isVideoFile(name)) {
                ducImageA.setImageResource(isCircular ? R.drawable.circlevideo : R.drawable.video);
            } else if (isAudioFile(name)) {
                ducImageA.setImageResource(isCircular ? R.drawable.circlemusic : R.drawable.audio);
            } else {
                ducImageA.setImageResource(R.drawable.file);
            }
        }

        @Override
        public void unbindView(@NonNull FileItem item) {
            tvName.setText(null);
            tvSize.setText(null);
            ducImageA.setImageDrawable(null);
            Audiofevratebtn.setVisibility(View.GONE);
        }

        private String readableFileSize(long size) {
            if (size <= 0) return "0";
            final String[] units = {"B", "KB", "MB", "GB", "TB"};
            int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
            return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups))
                    + " " + units[digitGroups];
        }

        private boolean isImageFile(String name) {
            return name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png")
                    || name.endsWith(".webp") || name.endsWith(".gif") || name.endsWith(".bmp");
        }

        private boolean isVideoFile(String name) {
            return name.endsWith(".mp4") || name.endsWith(".mkv") || name.endsWith(".avi")
                    || name.endsWith(".mov") || name.endsWith(".flv") || name.endsWith(".wmv");
        }

        private boolean isAudioFile(String name) {
            return name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".aac")
                    || name.endsWith(".ogg") || name.endsWith(".m4a") || name.endsWith(".flac");
        }
    }
}
