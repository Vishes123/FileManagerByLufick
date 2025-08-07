package com.example.filemanagerbylufic.imageViewrSwipeSlide;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.filemanagerbylufic.R;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ImageViewerActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private List<File> imageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        viewPager = findViewById(R.id.viewPager);
        imageList = (List<File>) getIntent().getSerializableExtra("image_list");
        int position = getIntent().getIntExtra("position", 0);

        ImageSliderAdapter adapter = new ImageSliderAdapter(imageList);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position, false);
    }
}
