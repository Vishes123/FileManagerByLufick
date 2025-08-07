package com.example.filemanagerbylufic.adeptor;

import static android.content.Intent.getIntent;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.media.RouteListingPreference;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.filemanagerbylufic.FileActivity;
import com.example.filemanagerbylufic.OnFileClickListener;
import com.example.filemanagerbylufic.OptionMenuClicked;
import com.example.filemanagerbylufic.R;
import com.example.filemanagerbylufic.SqliteDbForFavorite.FavoritesDbHelper;
import com.example.filemanagerbylufic.language.LanguageUtility;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
public class FileAdeptor extends AbstractItem<FileAdeptor , FileAdeptor.ViewHolder > {
    private File fileList;
    private SharedPreferences sharedPreferences;


    boolean showHeader = false;
    String headerTxt = "";

    public int countSize;
    File file;
    List<File> filelist = new ArrayList<>();
    static OnFileClickListener onFileClickListener;
    static Context context;




    public FileAdeptor(File file, OnFileClickListener onFileClickListener , boolean showHeader , String headerTxt , Context context) {
        this.file = file;
        this.onFileClickListener = onFileClickListener;
        this.showHeader = showHeader;
        this.headerTxt = headerTxt;
        this.context = context;
        sharedPreferences = context.getSharedPreferences("favorites_pref", Context.MODE_PRIVATE);
    }



    public FileAdeptor(int countSize) {
        this.countSize = countSize;
    }

    public FileAdeptor(String headerTxt) {
        this.headerTxt = headerTxt;
    }

    public FileAdeptor(File file) {
        this.file = file;
    }


    public FileAdeptor(List<File> filelist) {
        this.filelist = filelist;
    }

    public File getFile() {
        return file;
    }

    public List<File> getFilelist() {
        return filelist;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return 102;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.file_item;
    }

    @Override
    public boolean isSelectable() {
        return true;
    }



    static class ViewHolder extends FastAdapter.ViewHolder<FileAdeptor>{

        TextView fileName , fileDetails , headerTxt;
        ImageView imageView ,moreOptions;


        IconicsImageView fev;
        private int[] File;
        LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            fileName = itemView.findViewById(R.id.textName);
            fileDetails = itemView.findViewById(R.id.textDetails);
            imageView = itemView.findViewById(R.id.imageIcon);
            headerTxt = itemView.findViewById(R.id.headerTitle1);
            moreOptions = itemView.findViewById(R.id.moreOptions);
            fev = itemView.findViewById(R.id.fevratebtn);
            linearLayout=itemView.findViewById(R.id.bgColor);

        }

        @Override
        public void bindView(FileAdeptor item, List<Object> payloads) {

            File file = item.getFile();
            FavoritesDbHelper dbHelper = new FavoritesDbHelper(context);

            if (dbHelper.isFavorite(file.getAbsolutePath())) {
                fev.setVisibility(View.VISIBLE);
                fev.setIcon(new IconicsDrawable(itemView.getContext(), CommunityMaterial.Icon2.cmd_star)
                        .color(ContextCompat.getColor(itemView.getContext(), R.color.Yello))
                        .sizeDp(21));
            } else {
                fev.setVisibility(View.GONE);
            }




            if (item.showHeader) {
                headerTxt.setVisibility(View.VISIBLE);
                headerTxt.setText(item.headerTxt);
            } else {
                headerTxt.setVisibility(View.GONE);
            }

            moreOptions.setOnClickListener(v -> {
                if (context instanceof FileActivity) {
                    ((FileActivity) context).showThreeDotBottomBar(item.getFile());
                }
            });


            File f  = item.getFile();
            String localizedName = LanguageUtility.getLocalizedFolderName(f.getName(), context);
            fileName.setText(localizedName);

            //========size calculate into mb============
//            long bytes = f.length();
//            double megabytes = bytes / (1024.0 * 1024.0);
//            String size = String.format("%.2f MB", megabytes);
            long bytes = f.length();
            double megabytes = bytes / (1024.0 * 1024.0);

            String size;
            if (megabytes < 0.01) {
                size = bytes + " Bytes";
            } else {
                size = String.format("%.2f MB", megabytes);
            }




            String details = (f.isDirectory() ? "Folder" : "File")+" | "+size;
            fileDetails.setText(details);


            try {
                BasicFileAttributes attr = null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    attr = Files.readAttributes(f.toPath(), BasicFileAttributes.class);
                    long millis = attr.creationTime().toMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
                    //String creationDate = sdf.format(millis);
                   // details += " | " + creationDate;
                } else {
                   // details += " | Created: N/A";
                }
            } catch (Exception e) {
                e.printStackTrace();
                details += " | Created: N/A";
            }
            fileDetails.setText(details);



//========================DarkLightMode======================================
       SharedPreferences prefss = PreferenceManager.getDefaultSharedPreferences(context);
        String value = prefss.getString("theme_choice", "system_default");

            switch (value) {
                    case "light":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                    case "dark":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                    case "system_default":
                    default:
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                        break;
                }
            if ("dark".equals(value)) {
                fileName.setTextColor(ContextCompat.getColor(context, R.color.white));
                fileDetails.setTextColor(ContextCompat.getColor(context, R.color.white));
                headerTxt.setTextColor(ContextCompat.getColor(context, R.color.white));
                headerTxt.setBackgroundColor(ContextCompat.getColor(context, R.color.black));
                linearLayout.setBackgroundColor(Color.BLACK);

            } else {
                fileName.setTextColor(ContextCompat.getColor(context, R.color.black));
                fileDetails.setTextColor(ContextCompat.getColor(context, R.color.black));
                headerTxt.setTextColor(ContextCompat.getColor(context, R.color.black));
                headerTxt.setBackgroundColor(ContextCompat.getColor(context, R.color.white));
                linearLayout.setBackgroundColor(Color.WHITE);
                moreOptions.setImageResource(R.drawable.lightmodethreedot);

            }







            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            boolean isSwitchOn = prefs.getBoolean("Circularicon", false);

            if (isSwitchOn) {
                String name = item.file.getName().toLowerCase();
                if (item.isSelected()) {
                    itemView.setBackgroundColor(Color.parseColor("#1a0000"));
                    imageView.setImageResource(R.drawable.select);
                } else {
                    itemView.setBackgroundColor(Color.parseColor("#000000"));
                    if (item.file.isDirectory()) {
                        imageView.setImageResource(R.drawable.circulfolder);
                    } else if (isImageFile(name)) {

                        Glide.with(itemView.getContext())
                                .load(item.file)
                                .placeholder(R.drawable.gallery)
                                .circleCrop()
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
                        imageView.setImageResource(R.drawable.file);
                    }

                }
            } else {
                // OFF par kya karna hai
            String name = item.file.getName().toLowerCase();
            if (item.isSelected()) {
                itemView.setBackgroundColor(Color.parseColor("#1a0000"));
                imageView.setImageResource(R.drawable.select);
            } else {
                itemView.setBackgroundColor(Color.parseColor("#000000"));
                if (item.file.isDirectory()) {
                    imageView.setImageResource(R.drawable.main_icon);
                } else if (isImageFile(name)) {

                    Glide.with(itemView.getContext())
                            .load(item.file)
                            .placeholder(R.drawable.gallery)
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
                    imageView.setImageResource(R.drawable.file);
                }

            }
            }
        }
        private boolean isImageFile(String name) {
            return name.endsWith(".jpg") || name.endsWith(".jpeg") ||
                    name.endsWith(".png") || name.endsWith(".webp") ||
                    name.endsWith(".gif") || name.endsWith(".bmp");
        }
        private boolean isVideoFile(String name) {
            String lower = name.toLowerCase();
            return lower.endsWith(".mp4") || lower.endsWith(".mkv") || lower.endsWith(".avi") ||
                    lower.endsWith(".mov") || lower.endsWith(".flv") || lower.endsWith(".wmv");
        }

        private boolean isAudioFile(String name) {
            String lower = name.toLowerCase();
            return lower.endsWith(".mp3") || lower.endsWith(".wav") || lower.endsWith(".aac") ||
                    lower.endsWith(".ogg") || lower.endsWith(".m4a") || lower.endsWith(".flac");
        }



        @Override
        public void unbindView(FileAdeptor item) {
            fileName.setText(null);
            fileDetails.setText(null);
        }



    }


}
