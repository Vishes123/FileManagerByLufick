package com.example.filemanagerbylufic.imageViewrSwipeSlide;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.filemanagerbylufic.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.io.File;
import java.util.List;

public class ImagePagerAdapter extends AbstractItem<ImagePagerAdapter, ImagePagerAdapter.ViewHolder> {

    private  File file;

    public ImagePagerAdapter(File file) {
        this.file = file;
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
        return 120;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.image_item_layout;
    }

    static class ViewHolder extends FastAdapter.ViewHolder<ImagePagerAdapter> {
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }

        @Override
        public void bindView(ImagePagerAdapter item, List<Object> payloads) {
            Glide.with(imageView.getContext())
                    .load(item.file)
                    .into(imageView);
        }

        @Override
        public void unbindView(ImagePagerAdapter item) {
            imageView.setImageDrawable(null);
        }
    }
}
