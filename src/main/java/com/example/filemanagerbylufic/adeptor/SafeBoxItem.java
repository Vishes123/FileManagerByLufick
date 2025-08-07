package com.example.filemanagerbylufic.adeptor;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.filemanagerbylufic.R;
import com.example.filemanagerbylufic.safeBox.SafeBoxManager;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SafeBoxItem extends AbstractItem<SafeBoxItem, SafeBoxItem.ViewHolder> {

    public File file;
    Context context;

    public SafeBoxItem(File file ,Context context) {
        this.file = file;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return 858;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.safebox_item;
    }

    static class ViewHolder extends FastAdapter.ViewHolder<SafeBoxItem> {

        ImageView imageIcon;
        TextView textName;
        TextView textDetails;
        ImageView moreOptions;

        public ViewHolder(View itemView) {
            super(itemView);
            imageIcon = itemView.findViewById(R.id.imageIconSafeBox);
            textName = itemView.findViewById(R.id.textNameSafeBox);
            textDetails = itemView.findViewById(R.id.textDetailsSafeBox);
            moreOptions = itemView.findViewById(R.id.moreOptionsSafeBox);
        }

        @Override
        public void bindView(SafeBoxItem item, List<Object> payloads) {
            textName.setText(item.file.getName());
            textDetails.setText(formatDetails(item.file));
            imageIcon.setImageResource(item.file.isDirectory() ? R.drawable.greenfolder : R.drawable.file); // or any other icon

            moreOptions.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(itemView.getContext(), moreOptions);
                popup.getMenu().add("Restore");

                popup.setOnMenuItemClickListener(menuItem -> {
                    if (menuItem.getTitle().equals("Restore")) {
                        SafeBoxManager manager = new SafeBoxManager(itemView.getContext());
                        boolean success = manager.restoreFile(item.file);
                        if (success) {
                            Toast.makeText(itemView.getContext(), "Restored to original location", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(itemView.getContext(), "Restore failed", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                    }
                    return false;
                });

                popup.show();
            });

            //============ Dark / Light Mode ==============
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(item.context);
            String themeValue = prefs.getString("theme_choice", "system_default");

            if ("dark".equals(themeValue)) {
                textName.setTextColor(ContextCompat.getColor(item.context, R.color.white));
                textDetails.setTextColor(ContextCompat.getColor(item.context, R.color.white));
                itemView.setBackgroundColor(ContextCompat.getColor(item.context, R.color.black));
               // moreOptions.setImageResource(R.drawable.darkmodethreedot);
            } else {
                textName.setTextColor(ContextCompat.getColor(item.context, R.color.black));
                textDetails.setTextColor(ContextCompat.getColor(item.context, R.color.black));
                itemView.setBackgroundColor(Color.WHITE);
                moreOptions.setImageResource(R.drawable.lightmodethreedot);
            }

        }

        private String formatDetails(File file) {
            long size = file.length();
            long modified = file.lastModified();
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());
            return readableFileSize(size) + " · " + sdf.format(new Date(modified));
        }

        private String readableFileSize(long size) {
            if (size <= 0) return "0 B";
            final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
            int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
            return String.format(Locale.getDefault(), "%.1f %s",
                    size / Math.pow(1024, digitGroups), units[digitGroups]);
        }

        @Override
        public void unbindView(SafeBoxItem item) {
            textName.setText(null);
            textDetails.setText(null);
        }
    }
}
