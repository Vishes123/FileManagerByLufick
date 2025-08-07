package com.example.filemanagerbylufic.adeptor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.filemanagerbylufic.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.iconics.view.IconicsImageView;

import java.util.List;

public class AppsAdapter extends AbstractItem<AppsAdapter, AppsAdapter.ViewHolder> {

    public Drawable icon;
    public String name;
    public String size;
    public String date;
    public String packageName;
    Context context;

    public AppsAdapter(Drawable icon, String name, String size, String date, String packageName ,Context context) {
        this.icon = icon;
        this.name = name;
        this.size = size;
        this.date = date;
        this.packageName = packageName;
        this.context = context;
    }



    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.apps_item;
    }

    public static class ViewHolder extends FastAdapter.ViewHolder<AppsAdapter> {

        private final IconicsImageView fileIcons;
        private final TextView fileName;
        private final TextView fileDate;
        private final TextView fileSize;
        ImageView imageView, imageButton;

        public ViewHolder(View itemView) {
            super(itemView);

            fileIcons = itemView.findViewById(R.id.fileIcons);
            fileName = itemView.findViewById(R.id.tvFileName);
            fileDate = itemView.findViewById(R.id.tvFileModifierData);
            fileSize = itemView.findViewById(R.id.tvFileSize);
            imageButton = itemView.findViewById(R.id.imageButtonAudio);
        }

        @Override
        public void bindView(AppsAdapter item, List<Object> payloads) {
            fileIcons.setImageDrawable(item.icon);
            fileName.setText(item.name);
            fileDate.setText(item.date);
            fileSize.setText(item.size);

            // ========== Dark / Light / System Default mode ==========
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(item.context);
            String themeValue = prefs.getString("theme_choice", "system_default");

            if ("dark".equals(themeValue)) {
                fileName.setTextColor(ContextCompat.getColor(item.context, R.color.white));
                fileDate.setTextColor(ContextCompat.getColor(item.context, R.color.white));
                fileSize.setTextColor(ContextCompat.getColor(item.context, R.color.white));
                itemView.setBackgroundColor(ContextCompat.getColor(item.context, R.color.black));
            } else {
                fileName.setTextColor(ContextCompat.getColor(item.context, R.color.black));
                fileDate.setTextColor(ContextCompat.getColor(item.context, R.color.black));
                fileSize.setTextColor(ContextCompat.getColor(item.context, R.color.black));
                itemView.setBackgroundColor(Color.WHITE);
                imageButton.setImageResource(R.drawable.lightmodethreedot);
            }

            // ========= imageButton click listener for menu =========
            imageButton.setOnClickListener(v -> {
                android.widget.PopupMenu popupMenu = new android.widget.PopupMenu(itemView.getContext(), imageButton);
                popupMenu.getMenu().add("Open");
                popupMenu.getMenu().add("Backup");
                popupMenu.getMenu().add("Uninstall");
                popupMenu.getMenu().add("Properties");
                popupMenu.getMenu().add("Play Store");
                popupMenu.getMenu().add("Share");

                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    String title = menuItem.getTitle().toString();
                    Context context = itemView.getContext();

                    switch (title) {
                        case "Open":
                            try {
                                Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(item.packageName);
                                if (launchIntent != null) {
                                    context.startActivity(launchIntent);
                                } else {
                                    Toast.makeText(context, "Cannot open app", Toast.LENGTH_SHORT).show();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(context, "Failed to open", Toast.LENGTH_SHORT).show();
                            }
                            return true;

                        case "Backup":
                            Toast.makeText(context, "Backup not implemented", Toast.LENGTH_SHORT).show();
                            return true;

                        case "Uninstall":
                            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE);
                            uninstallIntent.setData(android.net.Uri.parse("package:" + item.packageName));
                            context.startActivity(uninstallIntent);
                            return true;

                        case "Properties":
                            try {
                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(android.net.Uri.parse("package:" + item.packageName));
                                context.startActivity(intent);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return true;

                        case "Play Store":
                            try {
                                Intent intent = new Intent(Intent.ACTION_VIEW,
                                        android.net.Uri.parse("market://details?id=" + item.packageName));
                                context.startActivity(intent);
                            } catch (Exception e) {
                                // fallback
                                Intent intent = new Intent(Intent.ACTION_VIEW,
                                        android.net.Uri.parse("https://play.google.com/store/apps/details?id=" + item.packageName));
                                context.startActivity(intent);
                            }
                            return true;

                        case "Share":
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            String shareText = "Check this app: https://play.google.com/store/apps/details?id=" + item.packageName;
                            shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
                            context.startActivity(Intent.createChooser(shareIntent, "Share via"));
                            return true;

                        default:
                            return false;
                    }
                });
                popupMenu.show();
            });
        }

        @Override
        public void unbindView(AppsAdapter item) {

        }

    }
}