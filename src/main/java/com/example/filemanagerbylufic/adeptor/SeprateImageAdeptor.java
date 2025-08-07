package com.example.filemanagerbylufic.adeptor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.filemanagerbylufic.CallBackImageLoasd;
import com.example.filemanagerbylufic.ImageViewActivity;
import com.example.filemanagerbylufic.R;
import com.example.filemanagerbylufic.SqliteDbForFavorite.FavoritesDbHelper;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.io.File;
import java.util.List;

public class SeprateImageAdeptor extends AbstractItem<SeprateImageAdeptor, SeprateImageAdeptor.ViewHolder> {

    File imageFile;
    Context context;

    CallBackImageLoasd callBackImageLoasd;


    public SeprateImageAdeptor(File imageFile, Context context, CallBackImageLoasd callBackImageLoasd) {
        this.imageFile = imageFile;
        this.context = context;
        this.callBackImageLoasd = callBackImageLoasd;
    }

    public File getFile() {
        return imageFile;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return 252;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.sepratelistimage;
    }

    public static class ViewHolder extends FastAdapter.ViewHolder<SeprateImageAdeptor> {

        ImageView imageView, moreOptions;
        TextView imageName;
        IconicsImageView iconicsImageView;
        int position = getAdapterPosition();


        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.spimageView);
            imageName = itemView.findViewById(R.id.sptextViewImageName);
            moreOptions = itemView.findViewById(R.id.spmoreOptions);
            iconicsImageView = itemView.findViewById(R.id.Imagefevratebtn);
        }

        @Override
        public void bindView(SeprateImageAdeptor item, List<Object> payloads) {
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


            Glide.with(imageView.getContext())
                    .load(item.imageFile)
                    .into(imageView);

            File file = item.imageFile;
            imageName.setText(file.getName());

            if (item.isSelected()) {
                itemView.setBackgroundColor(Color.parseColor("#1a0000"));
                imageView.setImageResource(R.drawable.select); // Selected icon
            } else {
                itemView.setBackgroundColor(Color.TRANSPARENT);
            }

//============= Dark / Light Mode =============
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(item.context);
            String themeValue = prefs.getString("theme_choice", "system_default");

            if ("dark".equals(themeValue)) {
                imageName.setTextColor(ContextCompat.getColor(item.context, R.color.white));
                itemView.setBackgroundColor(ContextCompat.getColor(item.context, R.color.black));

            } else {
                imageName.setTextColor(ContextCompat.getColor(item.context, R.color.black));
                itemView.setBackgroundColor(Color.WHITE);
                moreOptions.setImageResource(R.drawable.lightmodethreedot);
            }

            moreOptions.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(v.getContext(), moreOptions);
                popup.inflate(R.menu.menu_image_options);

                FavoritesDbHelper dbHelperCheck = new FavoritesDbHelper(itemView.getContext());
                boolean isFav = dbHelperCheck.isFavorite(file.getAbsolutePath());
                dbHelperCheck.close();

                if (isFav) {
                    popup.getMenu().add("Remove from Favorite");
                } else {
                    popup.getMenu().add("Add to Favorite");
                }

                popup.setOnMenuItemClickListener(menuItem -> {
                    int itemId = menuItem.getItemId();
                    String title = menuItem.getTitle().toString();

                    if (itemId == R.id.menu_share) {
                        item.shareImage(file, v);
                        return true;
                    } else if (itemId == R.id.menu_edit) {
                        item.editImage(file, v);
                        return true;
//                    } else if (itemId == R.id.menu_delete) {
//                        int position = getAdapterPosition();
//                        if (position != RecyclerView.NO_POSITION) {
//                            item.deleteImage(file, item, position);
//                        }
//                        return true;
//                    }
                    }else if ("Add to Favorite".equals(title)) {
                        FavoritesDbHelper dbHelperAdd = new FavoritesDbHelper(itemView.getContext());
                        dbHelperAdd.addFavorite(file.getAbsolutePath());
                        dbHelperAdd.close();
                        Toast.makeText(itemView.getContext(), "Added to Favorite", Toast.LENGTH_SHORT).show();
                        iconicsImageView.setVisibility(View.VISIBLE);
                        iconicsImageView.setIcon(new IconicsDrawable(itemView.getContext(), CommunityMaterial.Icon2.cmd_star)
                                .color(ContextCompat.getColor(itemView.getContext(), R.color.Yello))
                                .sizeDp(21));
                        return true;
                    } else if ("Remove from Favorite".equals(title)) {
                        FavoritesDbHelper dbHelperRemove = new FavoritesDbHelper(itemView.getContext());
                        dbHelperRemove.removeFavorite(file.getAbsolutePath());
                        dbHelperRemove.close();
                        Toast.makeText(itemView.getContext(), "Removed from Favorite", Toast.LENGTH_SHORT).show();

                        if (iconicsImageView != null) {
                            iconicsImageView.setVisibility(View.GONE);
                        }
                        return true;
                    }
                    return false;
                });

                popup.show();
            });

        }

        @Override
        public void unbindView(SeprateImageAdeptor item) {
            imageView.setImageDrawable(null);
            imageName.setText(null);
        }
    }

    private void shareImage(File file, View contextView) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_STREAM,
                FileProvider.getUriForFile(contextView.getContext(),
                        contextView.getContext().getPackageName() + ".provider", file));
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        contextView.getContext().startActivity(Intent.createChooser(intent, "Share image via"));
    }

    private void editImage(File file, View contextView) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setDataAndType(
                FileProvider.getUriForFile(contextView.getContext(),
                        contextView.getContext().getPackageName() + ".provider", file), "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        contextView.getContext().startActivity(Intent.createChooser(intent, "Edit image using"));
    }



    private void deleteImage(File file, SeprateImageAdeptor item, int position) {
        if (file != null && file.exists() && file.delete()) {
            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();

            if (item.callBackImageLoasd != null) {
                item.callBackImageLoasd.loadFinish();
            }

        } else {
            Toast.makeText(context, "Delete failed", Toast.LENGTH_SHORT).show();
        }
    }



}
