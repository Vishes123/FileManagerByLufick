package com.example.filemanagerbylufic.adeptor;

import android.content.Intent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.filemanagerbylufic.CallBackImageLoasd;
import com.example.filemanagerbylufic.ImageModel;
import com.example.filemanagerbylufic.ImageViewActivity;
import com.example.filemanagerbylufic.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.File;
import java.util.List;

public class ImageAdeptor extends AbstractItem<ImageAdeptor, ImageAdeptor.ViewHolder> {
    ImageModel imageModel;
    FastItemAdapter<ImageAdeptor> adapter;
    CallBackImageLoasd callBackImageLoasd;

    public ImageAdeptor(ImageModel imageModel , CallBackImageLoasd callBackImageLoasd ) {
        this.imageModel = imageModel;
        this.callBackImageLoasd = callBackImageLoasd;
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
        return R.layout.image_item;
    }

    static class ViewHolder extends FastAdapter.ViewHolder<ImageAdeptor> {

        ImageView imageView, imageButton;
        TextView imageName;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewItem);
            imageName = itemView.findViewById(R.id.textViewImageName);
            imageButton = itemView.findViewById(R.id.imageButton);
        }

        @Override
        public void bindView(ImageAdeptor item, List<Object> payloads) {
            String path = item.imageModel.getImagePath();
            File file = new File(path);

            Glide.with(itemView.getContext())
                    .load(path)
                    .thumbnail(0.1f)
                    .into(imageView);

            imageName.setText(file.getName());

            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ImageViewActivity.class);
                intent.putExtra("image_path", path);
                v.getContext().startActivity(intent);
            });


            imageButton.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(v.getContext(), imageButton);
                popup.getMenu().add(0, 1, 0, "Share");
                popup.getMenu().add(0, 2, 1, "Edit");
                popup.getMenu().add(0, 3, 2, "Delete");

                popup.setOnMenuItemClickListener(menuItem -> {
                    int id = menuItem.getItemId();
                    if (id == 1) {
                        shareImage(file, v);
                        return true;
                    } else if (id == 2) {
                        editImage(file, v);
                        return true;
                    } else if (id == 3) {
                        deleteImage(file, v, item);
                        return true;
                    }
                    return false;
                });

                popup.show();
            });
        }

        @Override
        public void unbindView(ImageAdeptor item) {
            imageView.setImageDrawable(null);
            imageName.setText(null);
        }

        private void shareImage(File file, View view) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("image/*");
            intent.putExtra(Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(view.getContext(),
                            view.getContext().getPackageName() + ".provider", file));
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            view.getContext().startActivity(Intent.createChooser(intent, "Share image via"));
        }

        private void editImage(File file, View view) {
            Intent intent = new Intent(Intent.ACTION_EDIT);
            intent.setDataAndType(
                    FileProvider.getUriForFile(view.getContext(),
                            view.getContext().getPackageName() + ".provider", file),
                    "image/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            view.getContext().startActivity(Intent.createChooser(intent, "Edit image using"));
        }


        private void deleteImage(File file, View view, ImageAdeptor item) {
            if (file.exists() && file.delete()) {
                Toast.makeText(view.getContext(), "Deleted", Toast.LENGTH_SHORT).show();

                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && item.adapter != null) {
                    item.adapter.remove(position);
                }


                if (item.callBackImageLoasd != null) {
                    item.callBackImageLoasd.loadFinish();

                } else {
                    Toast.makeText(view.getContext(), "Delete failed", Toast.LENGTH_SHORT).show();
                }
            }

        }


    }


}
