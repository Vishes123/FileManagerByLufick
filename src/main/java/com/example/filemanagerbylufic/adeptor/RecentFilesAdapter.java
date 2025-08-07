package com.example.filemanagerbylufic.adeptor;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.example.filemanagerbylufic.R;
import com.example.filemanagerbylufic.RecentFilesModel;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

public class RecentFilesAdapter extends AbstractItem<RecentFilesAdapter, RecentFilesAdapter.ViewHolder> {

    private final RecentFilesModel fileModel;
    private final Context context;

    public RecentFilesAdapter(Context context, RecentFilesModel fileModel) {
        this.context = context;
        this.fileModel = fileModel;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return 303;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_recent_file;
    }

    static class ViewHolder extends FastAdapter.ViewHolder<RecentFilesAdapter> {
        ImageView imageIcon;
        TextView textName, textType;
        LinearLayout linearLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            imageIcon = itemView.findViewById(R.id.imageIcon2);
            textName = itemView.findViewById(R.id.textName2);
            textType = itemView.findViewById(R.id.textType2);
            linearLayout = itemView.findViewById(R.id.LinearRecent);
        }

        @Override
        public void bindView(@NonNull RecentFilesAdapter item, @NonNull List<Object> payloads) {
            Uri fileUri = item.fileModel.getFileUri();
            textName.setText(item.fileModel.getFileName());
            textType.setText(item.fileModel.getFileType());

            String type = item.fileModel.getFileType();
            if (type.equalsIgnoreCase("Images")) {
                Glide.with(itemView.getContext()).load(fileUri).into(imageIcon);
            } else if (type.equalsIgnoreCase("Documents")) {
                imageIcon.setImageResource(R.drawable.doc);
            } else if (type.equalsIgnoreCase("Videos")) {
                imageIcon.setImageResource(R.drawable.video);
            } else if (type.endsWith(".mp3") || type.endsWith(".wav")) {
                imageIcon.setImageResource(R.drawable.audio);
            } else if (type.endsWith(".mp4") || type.endsWith(".mkv")) {
                imageIcon.setImageResource(R.drawable.video);
            } else if (type.endsWith(".apk")) {
                imageIcon.setImageResource(R.drawable.apk);
            } else if (type.endsWith(".pdf")) {
                imageIcon.setImageResource(R.drawable.pdf);
            } else if (type.endsWith(".jpg") || type.endsWith(".jpeg") || type.endsWith(".png")) {
                imageIcon.setImageResource(R.drawable.gallery);
            } else {
                imageIcon.setImageResource(R.drawable.doc);
            }



            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(fileUri, item.context.getContentResolver().getType(fileUri));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                try {
                    item.context.startActivity(intent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(item.context, "No app found to open this file", Toast.LENGTH_SHORT).show();
                }
            });


            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(item.context);
            String value = prefs.getString("theme_choice", "system_default");

            if ("light".equals(value)) {
                linearLayout.setBackground(ContextCompat.getDrawable(item.context, R.drawable.gride_seprate));
                textName.setTextColor(Color.BLACK);
                textType.setTextColor(Color.DKGRAY);

            } else if ("dark".equals(value)) {
                linearLayout.setBackground(ContextCompat.getDrawable(item.context, R.drawable.bg_card_dark));
                textName.setTextColor(Color.WHITE);
                textType.setTextColor(Color.LTGRAY);

            } else {
                int currentNightMode = item.context.getResources().getConfiguration().uiMode
                        & Configuration.UI_MODE_NIGHT_MASK;

                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    linearLayout.setBackground(ContextCompat.getDrawable(item.context, R.drawable.bg_card_dark));
                    textName.setTextColor(Color.WHITE);
                    textType.setTextColor(Color.LTGRAY);
                } else {
                    linearLayout.setBackground(ContextCompat.getDrawable(item.context, R.drawable.gride_seprate));
                    textName.setTextColor(Color.BLACK);
                    textType.setTextColor(Color.DKGRAY);
                }
            }

        }

        @Override
        public void unbindView(@NonNull RecentFilesAdapter item) {
            imageIcon.setImageDrawable(null);
            textName.setText(null);
            textType.setText(null);
        }
    }
}
