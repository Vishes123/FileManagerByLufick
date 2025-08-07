package com.example.filemanagerbylufic;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.format.Formatter;
import android.view.MenuItem;   
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanagerbylufic.adeptor.AppsAdapter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ItemAdapter<AppsAdapter> itemAdapterApps;
    private FastAdapter<AppsAdapter> fastAdapterApps;
    private List<AppsAdapter> listApps = new ArrayList<>();
    private ProgressBar progressBarApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apps);

        Toolbar toolbar = findViewById(R.id.toolbarApps);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("App Manager");

            Drawable overflowIcon = toolbar.getOverflowIcon();
            if (overflowIcon != null) {
                overflowIcon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
            Drawable upArrow = toolbar.getNavigationIcon();
            if (upArrow != null) {
                upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
            }

        }

        recyclerView = findViewById(R.id.recyclerViewApps);
        progressBarApps = findViewById(R.id.progressbarApps);

        itemAdapterApps = new ItemAdapter<>();
        fastAdapterApps = FastAdapter.with(itemAdapterApps);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(fastAdapterApps);

        progressBarApps.setVisibility(View.VISIBLE);

        fastAdapterApps.withOnClickListener((v, adapter, item, position) -> {
            if (item.packageName != null) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage(item.packageName);
                if (launchIntent != null) {
                    startActivity(launchIntent);
                } else {
                    Toast.makeText(AppsActivity.this, "Can't open app", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        });


        loadingApp();
    }

    private void loadingApp() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executorService.execute(() -> {
            PackageManager packageManager = getPackageManager();
            List<ApplicationInfo> apps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

            for (ApplicationInfo app : apps) {
                Drawable icon = app.loadIcon(packageManager);
                String name = app.loadLabel(packageManager).toString();
                long size = new File(app.sourceDir).length();
                String sizeFormatted = Formatter.formatFileSize(AppsActivity.this, size);

                try {
                    PackageInfo pi = packageManager.getPackageInfo(app.packageName, 0);
                    String date = new SimpleDateFormat("dd MMM", Locale.getDefault())
                            .format(new Date(pi.firstInstallTime));

                 //   listApps.add(new AppsAdapter(icon, name, sizeFormatted, date));
                    listApps.add(new AppsAdapter(icon, name, sizeFormatted, date, app.packageName,AppsActivity.this));

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            handler.post(() -> {
                itemAdapterApps.setNewList(listApps);
                progressBarApps.setVisibility(View.GONE);


                if (getSupportActionBar() != null) {
                    getSupportActionBar().setSubtitle("Apps (" + listApps.size() + ")");
                }
            });
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
