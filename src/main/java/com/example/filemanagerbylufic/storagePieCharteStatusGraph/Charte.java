package com.example.filemanagerbylufic.storagePieCharteStatusGraph;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.StatFs;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanagerbylufic.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Charte extends AppCompatActivity {

    private static final int REQUEST_CODE = 100;
    private PieChart pieChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charte);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        thremeLightDark();

        pieChart = findViewById(R.id.pieChart);
        checkStoragePermission();
    }

    private void checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {

            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
                Toast.makeText(this, "Please allow Manage All Files permission", Toast.LENGTH_LONG).show();
            } else {
                loadChart();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_AUDIO
                }, REQUEST_CODE);
            } else {
                loadChart();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
            } else {
                loadChart();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            boolean granted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    granted = false;
                    break;
                }
            }
            if (granted) {
                loadChart();
            } else {
                Toast.makeText(this, "All permissions are required!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadChart() {
        ProgressBar progressBar = findViewById(R.id.progressbar);
        TextView tvCenterText = findViewById(R.id.tvCenterText);
   tvCenterText.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(Charte.this);
        boolean isFirstScan = prefs.getBoolean("scanned_once", false);

        if (!isFirstScan) {
            if (pieChart != null) {
                pieChart.setCenterText("Scanning...");
                pieChart.invalidate();
            }
        }

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            long imageSize, videoSize, audioSize, documentSize;

            if (isFirstScan) {
                imageSize = prefs.getLong("imageSize", 0);
                videoSize = prefs.getLong("videoSize", 0);
                audioSize = prefs.getLong("audioSize", 0);
                documentSize = prefs.getLong("documentSize", 0);
            } else {
                File root = Environment.getExternalStorageDirectory();
                FileCharte fileCharte = new FileCharte();
                fileCharte.analyzeFiles(root);

                imageSize = fileCharte.imageSize;
                videoSize = fileCharte.videoSize;
                audioSize = fileCharte.audioSize;
                documentSize = fileCharte.documentSize;

                prefs.edit()
                        .putLong("imageSize", imageSize)
                        .putLong("videoSize", videoSize)
                        .putLong("audioSize", audioSize)
                        .putLong("documentSize", documentSize)
                        .putBoolean("scanned_once", true)
                        .apply();
            }

            final long usedStorage = getUsedStorage();
            final long freeStorage = getAvailableStorage();
            final long totalStorage = usedStorage + freeStorage;

            long tempOthers = usedStorage - (imageSize + videoSize + audioSize + documentSize);
            if (tempOthers < 0) tempOthers = 0;
            final long others = tempOthers;

            handler.post(() -> {
                ArrayList<PieEntry> entries = new ArrayList<>();
                ArrayList<Integer> colors = new ArrayList<>();

                if (imageSize > 0) {
                    entries.add(new PieEntry(imageSize, "Images"));
                    colors.add(Color.parseColor("#FF5722"));
                }
                if (videoSize > 0) {
                    entries.add(new PieEntry(videoSize, "Videos"));
                    colors.add(Color.parseColor("#3F51B5"));
                }
                if (audioSize > 0) {
                    entries.add(new PieEntry(audioSize, "Audio"));
                    colors.add(Color.parseColor("#009688"));
                }
                if (documentSize > 0) {
                    entries.add(new PieEntry(documentSize, "Documents"));
                    colors.add(Color.parseColor("#9C27B0"));
                }
                if (others > 0) {
                    entries.add(new PieEntry(others, "Others"));
                    colors.add(Color.parseColor("#607D8B"));
                }
                if (freeStorage > 0) {
                    entries.add(new PieEntry(freeStorage, "Free"));
                    colors.add(Color.parseColor("#4CAF50"));
                }

                if (entries.isEmpty()) {
                    entries.add(new PieEntry(1, "Empty"));
                    colors.add(Color.LTGRAY);
                }

                PieDataSet dataSet = new PieDataSet(entries, "Storage Usage");
                dataSet.setColors(colors);
                dataSet.setValueTextColor(Color.WHITE);
                dataSet.setValueTextSize(12f);

                PieData pieData = new PieData(dataSet);
                pieChart.setData(pieData);
                pieChart.setUsePercentValues(true);
                pieChart.getDescription().setEnabled(false);
                pieChart.setEntryLabelColor(Color.BLACK);
                pieChart.invalidate();
                pieChart.setHoleRadius(40f);
                pieChart.setTransparentCircleRadius(45f);
                pieChart.setCenterText("Used Storage");
                pieChart.setCenterTextSize(16f);
                pieChart.setDrawEntryLabels(true);
                pieChart.getLegend().setEnabled(true);
                pieChart.animateY(1000);

                updateCategoryUI(R.id.textImage, R.id.progressImage, imageSize, totalStorage);
                updateCategoryUI(R.id.textVideo, R.id.progressVideo, videoSize, totalStorage);
                updateCategoryUI(R.id.textDocs, R.id.progressDocs, documentSize, totalStorage);
                updateCategoryUI(R.id.textOthers, R.id.progressOthers, others, totalStorage);

                progressBar.setVisibility(View.GONE);
                tvCenterText.setVisibility(View.GONE);
            });
        });
    }


    private void updateCategoryUI(int textViewId, int progressBarId, long categorySize, long totalSize) {
        TextView textView = findViewById(textViewId);
        ProgressBar progressBar = findViewById(progressBarId);

        String readableSize = android.text.format.Formatter.formatShortFileSize(this, categorySize);
        String readableTotal = android.text.format.Formatter.formatShortFileSize(this, totalSize);

        textView.setText(readableSize + " / " + readableTotal);

        int percent = totalSize > 0 ? (int) ((categorySize * 100) / totalSize) : 0;
        progressBar.setProgress(percent);
    }
    public static long getAvailableStorage() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        return stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
    }

    public static long getUsedStorage() {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        return stat.getTotalBytes() - stat.getAvailableBytes();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    void thremeLightDark() {
        //========================DarkLightMode======================================
        SharedPreferences prefss = PreferenceManager.getDefaultSharedPreferences(Charte.this);
        String value = prefss.getString("theme_choice", "system_default");

        List<TextView> textViews = Arrays.asList(
                findViewById(R.id.tvImagesLabel),
                findViewById(R.id.textImage),
                findViewById(R.id.tvVideosLabel),
                findViewById(R.id.textVideo),
                findViewById(R.id.tvDocsLabel),
                findViewById(R.id.textDocs),
                findViewById(R.id.tvOthersLabel),
                findViewById(R.id.textOthers)
        );

        List<CardView> cardViews = Arrays.asList(
                findViewById(R.id.cvChartCard),
                findViewById(R.id.cvStorageInfo)
        );

        LinearLayout llRoot = findViewById(R.id.llRoot);
        ScrollView scrollView = findViewById(R.id.main);

        switch (value) {
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

                llRoot.setBackgroundColor(Color.WHITE);
                for (TextView tv : textViews) {
                    tv.setTextColor(Color.parseColor("#333333"));
                }
                for (CardView cv : cardViews) {
                    cv.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                }
                break;

            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                llRoot.setBackgroundColor(Color.BLACK);
                for (TextView tv : textViews) {
                    tv.setTextColor(Color.parseColor("#333333"));
                }
                for (CardView cv : cardViews) {
                    cv.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
                }
                scrollView.setBackgroundColor(Color.BLACK);
                break;

            case "system_default":
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

                int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {

                    llRoot.setBackgroundColor(Color.BLACK);
                    for (TextView tv : textViews) {
                        tv.setTextColor(Color.parseColor("#333333"));
                    }
                    for (CardView cv : cardViews) {
                        cv.setCardBackgroundColor(Color.parseColor("#1E1E1E"));
                    }
                    scrollView.setBackgroundColor(Color.BLACK);

                } else {

                    llRoot.setBackgroundColor(Color.WHITE);
                    for (TextView tv : textViews) {
                        tv.setTextColor(Color.parseColor("#333333"));
                    }
                    for (CardView cv : cardViews) {
                        cv.setCardBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                }
                break;
        }
    }



}
