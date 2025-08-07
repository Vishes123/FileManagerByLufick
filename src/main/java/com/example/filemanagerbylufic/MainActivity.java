package com.example.filemanagerbylufic;


import android.annotation.SuppressLint;
import android.Manifest;

import android.annotation.TargetApi;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.widget.NestedScrollView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanagerbylufic.AIS.AIHelper;
import com.example.filemanagerbylufic.AIS.FileItem;
import com.example.filemanagerbylufic.AIS.FileSearchActivity;
import com.example.filemanagerbylufic.SqliteDbForFavorite.FavoritesDbHelper;
import com.example.filemanagerbylufic.adeptor.FavoriteAdeptor;
import com.example.filemanagerbylufic.adeptor.RecentFilesAdapter;
import com.example.filemanagerbylufic.language.BaseActivity;
import com.example.filemanagerbylufic.separateWork.RecentFiles;
import com.example.filemanagerbylufic.separateWork.SeprateApk;
import com.example.filemanagerbylufic.separateWork.SeprateAudio;
import com.example.filemanagerbylufic.separateWork.SeprateDocument;
import com.example.filemanagerbylufic.separateWork.SeprateDowenlode;
import com.example.filemanagerbylufic.separateWork.SeprateImage;
import com.example.filemanagerbylufic.separateWork.SeprateVideo;
import com.example.filemanagerbylufic.settingScreen.Setting;
import com.example.filemanagerbylufic.settingScreen.SettingFragment;
import com.example.filemanagerbylufic.storagePieCharteStatusGraph.Charte;
import com.example.filemanagerbylufic.tresh.TreshActivity;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.material.navigation.NavigationView;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

public class MainActivity extends BaseActivity {
    Toolbar toolbar;
    IconicsImageView iconicsImageView;

    private static final int REQUEST_CODE = 102;

    RecyclerView recyclerView , favrecycleView;
    ItemAdapter<AbstractItem> itemAdapter;
    FastAdapter<AbstractItem> fastAdapter;
    ItemAdapter<FavoriteAdeptor> itemAdapter2;
    FastAdapter<FavoriteAdeptor> fastAdapter2;
    //TextView   image , video , document;
    LinearLayout fileFoldertxt,image , video , document , sdCard;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    ImageButton imageButton2;
    private Stack<File> folderHistory = new Stack<>();
    private PieChart pieChart;
    private GridLayout legendLayout;
    private TextView centerText;

    private final List<ImageModel> imageList = new ArrayList<>();
    private final long[] categorySizes = new long[6];

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);


        recyclerView = findViewById(R.id.recyclerRecent);
        fileFoldertxt = findViewById(R.id.fileFolder);
        image = findViewById(R.id.imageIdd);
        video = findViewById(R.id.videoId);
        document = findViewById(R.id.dcumentId);
        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);
      //  recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
       /* recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(fastAdapter);*/
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(fastAdapter);



        new Thread(() -> {
            checkPermissions();
            showFavoriteInGrid();
            HideUnhide();

            runOnUiThread(() -> {

            });
        }).start();



        TextView internalstorage = findViewById(R.id.internalstor);

        new Thread(() -> {
            String infoText = getInternalStorageInfo();

            runOnUiThread(() -> {
                internalstorage.setText(infoText);
            });
        }).start();



        TextView avlevalStorage = findViewById(R.id.internalstor2);
        new Thread(() -> {
            String availableStorage = getAvailableInternalStorage();
            runOnUiThread(() -> {
                avlevalStorage.setText("Available storage: " + availableStorage);
                setStorageProgress();
            });
        }).start();


// Drawer setup
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nv2);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setItemIconTintList(null);

         ai();
         checkAndShowNoFavImage();
         sdCardVisibility();

        GoToFileActivityBySDcard();
        goToFile();
        setAllIconsToClose();
        image();
        video();
        document();
        goToAudio();
        sideDrawerClick();
//        NestedScrollView nestedScrollView = findViewById(R.id.dregDrop);
//        LinearLayout mainContainer = findViewById(R.id.main112);
//        setupDrag(nestedScrollView);
//
//        mainContainer.setOnDragListener(dragListener);  // optional, agar container par bhi drag events chahiye

//        LinearLayout dcumentId = findViewById(R.id.dcumentId);
//        LinearLayout fileFolder2 = findViewById(R.id.fileFolder2);
//        LinearLayout videoId = findViewById(R.id.videoId);
//        LinearLayout imageIdd = findViewById(R.id.imageIdd);
//        LinearLayout fileFolder = findViewById(R.id.fileFolder);
//
//        setupDragForCategory(dcumentId);
//        setupDragForCategory(fileFolder2);
//        setupDragForCategory(videoId);
//        setupDragForCategory(imageIdd);
//        setupDragForCategory(fileFolder);



      //  drawerLayout = findViewById(R.id.drawer_layout);

        pieChart = findViewById(R.id.pieChart3);
       // legendLayout = findViewById(R.id.legendLayout3);
        legendLayout = findViewById(R.id.legendLayout3);
        centerText = findViewById(R.id.centerText3);

       // centerText.setText("Internal");
        setupPieChart();


        //=====================Theme ke liye===================
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
//        String value = prefs.getString("theme_choice", "system_default");
        applyTheme();

    }

   /* private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE);
        }
    }*/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, int deviceId) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId);
        if (requestCode == REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
           // loadImagesFromGallery();
        } else {
          //  Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    void GoToFileActivityBySDcard(){
        sdCard = findViewById(R.id.fileFolderSD);
        sdCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSDCard();
            }
        });
    }

    private void openSDCard() {
        File[] externalDirs = ContextCompat.getExternalFilesDirs(this, null);

        if (externalDirs.length > 1 && externalDirs[1] != null) {

            File sdRoot = externalDirs[1].getParentFile().getParentFile().getParentFile().getParentFile();

            if (sdRoot != null && sdRoot.exists()) {
                Intent intent = new Intent(MainActivity.this, FileActivity.class);
                intent.putExtra("root_path", sdRoot.getAbsolutePath());
                startActivity(intent);
            } else {
                Toast.makeText(this, "SD Card not accessible", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "No SD Card detected", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadImagesFromGallery() {
        new Thread(() -> {
            List<ImageModel> tempList = new ArrayList<>();
            Uri collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.Images.Media.DATA};
            String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

            try (Cursor cursor = getContentResolver().query(collection, projection, null, null, sortOrder)) {
                if (cursor != null) {
                    int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    while (cursor.moveToNext()) {
                        String path = cursor.getString(dataColumn);
                        tempList.add(new ImageModel(path));
                    }
                }
            }

            runOnUiThread(() -> {
                imageList.clear();
                imageList.addAll(tempList);

            });
        }).start();
    }



    void goToFile(){
        fileFoldertxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , FileActivity.class);
                startActivity(intent);
            }
        });
    }
    private void setAllIconsToClose() {
        Context context = this;

       // IconicsImageView searchIcon = findViewById(R.id.search);
        IconicsImageView downloadIcon = findViewById(R.id.dowenloaddd);
        IconicsImageView imageIcon = findViewById(R.id.image);
        IconicsImageView videoIcon = findViewById(R.id.video);
        IconicsImageView fileIcon = findViewById(R.id.file);
        IconicsImageView storage = findViewById(R.id.storage);
        IconicsImageView storage2 = findViewById(R.id.storage22);
        IconicsImageView audio = findViewById(R.id.audiooIcon);
        IconicsImageView doc = findViewById(R.id.dd);
        IconicsImageView recet = findViewById(R.id.recenticon1);
        IconicsImageView trash = findViewById(R.id.treshIcon);
        IconicsImageView safeBoxIcon = findViewById(R.id.safeBoxIcon);
        IconicsImageView safeBoxIcon2 = findViewById(R.id.safeBoxIcon2);
        //  IconicsImageView storageIcon = findViewById(R.id.storage);

        if (context != null) {
//            if (searchIcon != null) {
//                searchIcon.setIcon(new IconicsDrawable(context)
//                        .icon(CommunityMaterial.Icon2.cmd_search_web)
//                        .color(ContextCompat.getColor(context, R.color.white))
//                        .sizeDp(32));
//            }
            if (downloadIcon != null) {
                downloadIcon.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_file_document)
                        .color(ContextCompat.getColor(context, R.color.deepblue))
                        .sizeDp(32));
            }
            if (imageIcon != null) {
                imageIcon.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_android)
                        .color(ContextCompat.getColor(context, R.color.deepblue))
                        .sizeDp(32));
            }
            if (videoIcon != null) {
                videoIcon.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon2.cmd_video)
                        .color(ContextCompat.getColor(context, R.color.blue))
                        .sizeDp(32));
            }
            if (fileIcon != null) {
                fileIcon.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon2.cmd_image)
                        .color(ContextCompat.getColor(context, R.color.pink))
                        .sizeDp(32));
            }
            if (audio != null) {
                audio.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_audiobook)
                        .color(ContextCompat.getColor(context, R.color.Red))
                        .sizeDp(32));
            }

//            if (storageIcon != null) {
//                storageIcon.setIcon(new IconicsDrawable(context)
//                        .icon("cmd-close")
//                        .color(ContextCompat.getColor(context, R.color.white))
//                        .sizeDp(32));
//            }
            if (fileIcon != null) {
                storage.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_cellphone_android)
                        .color(ContextCompat.getColor(context, R.color.blue))
                        .sizeDp(32));
            }
            if (fileIcon != null) {
                storage2.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon2.cmd_sd)
                        .color(ContextCompat.getColor(context, R.color.blue))
                        .sizeDp(32));
            }
            if (fileIcon != null) {
                doc.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_download)
                        .color(ContextCompat.getColor(context, R.color.blue))
                        .sizeDp(32));
            }
            if (fileIcon != null) {
                recet.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon2.cmd_reload)
                        .color(ContextCompat.getColor(context, R.color.deepblue))
                        .sizeDp(32));
            }
            if (fileIcon != null) {
                trash.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_delete)
                        .color(ContextCompat.getColor(context, R.color.Yello))
                        .sizeDp(32));
            }
            if (fileIcon != null) {
                safeBoxIcon.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon2.cmd_security_home)
                        .color(ContextCompat.getColor(context, R.color.pink))
                        .sizeDp(32));
            }

            if (fileIcon != null) {
                safeBoxIcon2.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon2.cmd_view_grid)
                        .color(ContextCompat.getColor(context, R.color.deepblue))
                        .sizeDp(32));
            }

        }
    }

    void image(){
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , SeprateImage.class);
                startActivity(intent);
//                Intent intent = new Intent(MainActivity.this, FileActivity.class);
//                intent.putExtra("title", "Images");
//                intent.putExtra("extensions", new String[]{".jpg", ".jpeg", ".png", ".gif", ".bmp"});
//                startActivity(intent);

            }
        });


    }
    void video(){
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , SeprateVideo.class);
                startActivity(intent);





            }
        });
    }
    void document(){
        document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , SeprateDocument.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        MenuItem refresh = menu.findItem(R.id.refresh);
        IconicsDrawable icon = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon2.cmd_refresh)
                .sizeDp(24)
                .color(Color.WHITE);
        refresh.setIcon(icon);

        IconicsDrawable icon2 = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon2.cmd_magnify)
                .sizeDp(24)
                .color(Color.WHITE);
        searchItem.setIcon(icon2);


       return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_search) {
            Intent intent = new Intent(MainActivity.this, FileActivity.class);
            intent.putExtra("openSearch", true);
            startActivity(intent);
            return true;
        } else if (item.getItemId()==R.id.refresh) {
            Intent intent = new Intent(MainActivity.this , MainActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private View draggedView = null;
    private ViewGroup parentView = null;
    private View targetView = null;
    private ViewGroup draggedFromParent = null;

    // DragListener (can be common or separate per container)
    private View.OnDragListener createDragListener(final ViewGroup categoryContainer) {
        return new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        // Accept drag events only if from this container
                        return true;

                    case DragEvent.ACTION_DRAG_ENTERED:
                        v.setBackgroundColor(Color.LTGRAY);
                        return true;

                    case DragEvent.ACTION_DRAG_EXITED:
                        v.setBackgroundColor(Color.TRANSPARENT);
                        return true;

                    case DragEvent.ACTION_DROP:
                        if (draggedView != null && draggedFromParent == categoryContainer) {
                            float dropX = event.getX();
                            int dropIndex = getDropIndex(categoryContainer, dropX);

                            // Remove from old parent and add to new index in the same container
                            draggedFromParent.removeView(draggedView);
                            categoryContainer.addView(draggedView, dropIndex);
                            draggedView.setVisibility(View.VISIBLE);
                        }
                        return true;

                    case DragEvent.ACTION_DRAG_ENDED:
                        v.setBackgroundColor(Color.TRANSPARENT);
                        if (draggedView != null) {
                            draggedView.setVisibility(View.VISIBLE);
                        }
                        draggedView = null;
                        draggedFromParent = null;
                        return true;

                    default:
                        return false;
                }
            }
        };
    }

    private int getDropIndex(ViewGroup parent, float dropX) {
        int index = 0;
        for (int i = 0; i < parent.getChildCount(); i++) {
            View child = parent.getChildAt(i);
            if (dropX > child.getLeft() + child.getWidth() / 2) {
                index = i + 1;
            }
        }
        return Math.min(index, parent.getChildCount());
    }

    private void setupDragForCategory(final ViewGroup categoryContainer) {
        // Set drag listeners on children
        for (int i = 0; i < categoryContainer.getChildCount(); i++) {
            View child = categoryContainer.getChildAt(i);
            child.setOnLongClickListener(v -> {
                draggedView = v;
                draggedFromParent = categoryContainer;

                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                v.startDragAndDrop(data, shadowBuilder, v, 0);
                v.setVisibility(View.INVISIBLE);
                return true;
            });
        }

        // Set drag listener on the container itself
        categoryContainer.setOnDragListener(createDragListener(categoryContainer));
    }

    public String getInternalStorageInfo() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());

        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long availableBlocks = stat.getAvailableBlocksLong();

        long totalSize = totalBlocks * blockSize;
        long availableSize = availableBlocks * blockSize;
        long usedSize = totalSize - availableSize;
        String total = android.text.format.Formatter.formatFileSize(this, totalSize);
        String used = android.text.format.Formatter.formatFileSize(this, usedSize);
        String free = android.text.format.Formatter.formatFileSize(this, availableSize);

        //return "Used: " +used + "/Free:"+free + "/Total:"+total;


        return "Used: " + formatSize(usedSize) +
                " / Total: " + formatSize(totalSize);

    }
    public String formatSize(long size) {
        float kb = size / 1024f;
        float mb = kb / 1024f;
        float gb = mb / 1024f;

        if (gb >= 1) {
            return String.format(Locale.getDefault(), "%.2f GB", gb);
        } else if (mb >= 1) {
            return String.format(Locale.getDefault(), "%.2f MB", mb);
        } else {
            return String.format(Locale.getDefault(), "%.2f KB", kb);
        }
    }
    public String getAvailableInternalStorage() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());

        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        long availableSize = availableBlocks * blockSize;

        return formatSize(availableSize); // e.g., "4.53 GB"
    }

    private void setStorageProgress() {
        ProgressBar storageProgress = findViewById(R.id.storageProgress);
        // TextView storageProgressText = findViewById(R.id.storageProgressText);

        new Thread(new Runnable() {
            @Override
            public void run() {
                File path = Environment.getDataDirectory();
                StatFs stat = new StatFs(path.getPath());

                long blockSize = stat.getBlockSizeLong();
                long totalBlocks = stat.getBlockCountLong();
                long availableBlocks = stat.getAvailableBlocksLong();

                long totalSize = totalBlocks * blockSize;
                long availableSize = availableBlocks * blockSize;
                long usedSize = totalSize - availableSize;

                int percentUsed = (int) ((usedSize * 100.0f) / totalSize);

                String usedStr = formatSize(usedSize);
                String totalStr = formatSize(totalSize);

                // Update UI on main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        storageProgress.setMax(100);
                        storageProgress.setProgress(percentUsed);
                        // storageProgressText.setText("Used: " + usedStr + " / Total: " + totalStr);
                    }
                });
            }
        }).start();
    }




    void sideDrawerClick(){
    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();

            if (id == R.id.id2) {
                Intent intent = new Intent(MainActivity.this,FileActivity.class);
                startActivity(intent);
                Toast.makeText(MainActivity.this, "Internal Storage", Toast.LENGTH_SHORT).show();

            } else if (id == R.id.id3) {
                Intent intent = new Intent(MainActivity.this, Charte.class);
                startActivity(intent);
            } else if (id == R.id.id4) {
//                showFavorites();
//                ifFav = true;

            } else if (id == R.id.id5) {
                Toast.makeText(MainActivity.this, "System Information", Toast.LENGTH_SHORT).show();

            }else if (id == R.id.nav_images) {
//                Intent intent = new Intent(MainActivity.this, FileActivity.class);
//                intent.putExtra("filter_type", "Images");
//                startActivity(intent);
                Intent intent = new Intent(MainActivity.this, SeprateImage.class);
                startActivity(intent);

            } else if (id == R.id.nav_videos) {
//                Intent intent = new Intent(MainActivity.this, FileActivity.class);
//                intent.putExtra("filter_type", "Videos");
//                startActivity(intent);
                Intent intent = new Intent(MainActivity.this, SeprateVideo.class);
                startActivity(intent);

            } else if (id == R.id.nav_audio) {
//                Intent intent = new Intent(MainActivity.this, FileActivity.class);
//                intent.putExtra("filter_type", "Audios");
//                startActivity(intent);
                Intent intent = new Intent(MainActivity.this, SeprateAudio.class);
                startActivity(intent);

            } else if (id == R.id.nav_documents) {
//                Intent intent = new Intent(MainActivity.this, FileActivity.class);
//                intent.putExtra("filter_type", "Documents");
//                startActivity(intent);
                Intent intent = new Intent(MainActivity.this, SeprateDocument.class);
                startActivity(intent);

            } else if (id == R.id.nav_apks) {
//                Intent intent = new Intent(MainActivity.this, FileActivity.class);
//                intent.putExtra("filter_type", "APKs");
//                startActivity(intent);
                Intent intent = new Intent(MainActivity.this, SeprateApk.class);
                startActivity(intent);
            }

            else if (id==R.id.nav_Setting){
                Intent intent = new Intent(MainActivity.this , Setting.class);
                startActivity(intent);
            }

            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        }
    });
}


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private final ActivityResultLauncher<String[]> permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            result -> {
                Boolean read = result.getOrDefault(Manifest.permission.READ_EXTERNAL_STORAGE, false);
                Boolean media = result.getOrDefault(Manifest.permission.READ_MEDIA_IMAGES, false);
                if (read || media) {
                    loadRecentFiles();
                } else {
                    Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
                }
            });

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(new String[]{
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_MEDIA_AUDIO
                });
                return;
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionLauncher.launch(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE
                });
                return;
            }
        }

        loadRecentFiles(); // If permission already granted
    }



private void loadRecentFiles() {
    new Thread(() -> {
        // Background thread work
        Set<RecentFilesModel> recentFileSet = new HashSet<>();

        recentFileSet.addAll(FileScanner.getRecentImages(this));
        recentFileSet.addAll(FileScanner.getRecentDocuments(this));
        recentFileSet.addAll(FileScanner.getRecentVideos(this));

        List<RecentFilesModel> recentFiles = new ArrayList<>(recentFileSet);

        Collections.sort(recentFiles, (f1, f2) -> Long.compare(f2.getDateAdded(), f1.getDateAdded()));

        // Group by folderName
        Map<String, List<RecentFilesModel>> groupedMap = new LinkedHashMap<>();
        for (RecentFilesModel model : recentFiles) {
            String folder = model.getFolderName();
            if (!groupedMap.containsKey(folder)) {
                groupedMap.put(folder, new ArrayList<>());
            }
            groupedMap.get(folder).add(model);
        }

        List<AbstractItem> items = new ArrayList<>();

        for (Map.Entry<String, List<RecentFilesModel>> entry : groupedMap.entrySet()) {
            String folderName = entry.getKey();
            List<RecentFilesModel> fileList = entry.getValue();

            // Agar header chahiye toh uncomment karo
            // items.add(new RecentHeaderItem(folderName));

            for (RecentFilesModel model : fileList) {
                items.add(new RecentFilesAdapter(this, model));
            }
        }

        // Switch to main thread to update UI
        runOnUiThread(() -> {
            itemAdapter.set(items);
        });

    }).start();
}



    private void showFavoriteInGrid() {
        new Thread(() -> {
            // Background thread: heavy work
            int favCountToShow = SettingFragment.getFavCount(this);

            FavoritesDbHelper dbHelper = new FavoritesDbHelper(this);
            Set<String> pathSet = dbHelper.getAllFavorites();
            dbHelper.close();
            List<String> favPaths = new ArrayList<>(pathSet);

            List<FavoriteAdeptor> favItems = new ArrayList<>();
            for (String path : favPaths) {
                File file = new File(path);
                if (file.exists()) {
                    favItems.add(new FavoriteAdeptor(file, MainActivity.this));
                }
            }

            // Limit items to show
            int maxItemsToShow = 10;
            List<FavoriteAdeptor> limitedItems = (favItems.size() > favCountToShow)
                    ? favItems.subList(0, favCountToShow)
                    : favItems;

            // Switch to main thread to update UI
            runOnUiThread(() -> {
                favrecycleView = findViewById(R.id.recyclerViewforFav);

                itemAdapter2 = new ItemAdapter<>();
                fastAdapter2 = FastAdapter.with(itemAdapter2);

                favrecycleView.setLayoutManager(new GridLayoutManager(this, 2));
                favrecycleView.setAdapter(fastAdapter2);

                itemAdapter2.set(limitedItems);

                dregAndDrope(); // Make sure this method is lightweight

                fastAdapter2.withOnClickListener((v, adapter, item, position) -> {
                    File file = item.getFile();
                    Intent intent = new Intent(MainActivity.this, FileActivity.class);
                    intent.putExtra("clicked_path", file.getAbsolutePath());
                    startActivity(intent);
                    return true;
                });
            });

        }).start();
    }






    //====================================================================================================



void HideUnhide(){
    TextView favToggle = findViewById(R.id.favToggle);
    LinearLayout fevSection = findViewById(R.id.fevSection);

    favToggle.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (fevSection.getVisibility() == View.VISIBLE) {
                fevSection.setVisibility(View.GONE);
                favToggle.setText("Show");
            } else {
                fevSection.setVisibility(View.VISIBLE);
                favToggle.setText("Hide");
            }
        }
    });
    TextView recentToggle = findViewById(R.id.recentHideShow);
    RecyclerView recyclerRecent = findViewById(R.id.recyclerRecent);

    recentToggle.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (recyclerRecent.getVisibility() == View.VISIBLE) {
                recyclerRecent.setVisibility(View.GONE);
                recentToggle.setText("Show");
            } else {
                recyclerRecent.setVisibility(View.VISIBLE);
                recentToggle.setText("Hide");
            }
        }
    });

}
    void dregAndDrope() {
        ItemTouchHelper.SimpleCallback dragCallback = new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                int fromPos = viewHolder.getAdapterPosition();
                int toPos = target.getAdapterPosition();

                // Swap items
                Collections.swap(itemAdapter2.getAdapterItems(), fromPos, toPos);
                fastAdapter2.notifyAdapterItemMoved(fromPos, toPos);
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // no swipe for now
            }
        };

        ItemTouchHelper touchHelper = new ItemTouchHelper(dragCallback);
        touchHelper.attachToRecyclerView(favrecycleView);

//        fastAdapter.withOnClickListener((v, adapter, item, position) -> {
//            //Toast.makeText(this, "Clicked: " + item.name, Toast.LENGTH_SHORT).show();
//            return true;
//        });
    }


    void  dregging(){
        LinearLayout parentLayout = findViewById(R.id.dregDrop);

        LinearLayout dcumentId = findViewById(R.id.dcumentId);
        LinearLayout fileFolder2 = findViewById(R.id.fileFolder);
        LinearLayout fileFolder = findViewById(R.id.fileFolderSD);
        LinearLayout fav = findViewById(R.id.fevSection);

        View.OnLongClickListener dragStartListener = v -> {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
            v.startDragAndDrop(data, shadowBuilder, v, 0);
            return true;
        };

        View.OnDragListener dragListener = (v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DROP:
                    View draggedView = (View) event.getLocalState();
                    View targetView = v;

                    int draggedIndex = parentLayout.indexOfChild(draggedView);
                    int targetIndex = parentLayout.indexOfChild(targetView);

                    // Remove and re-add views in new order
                    parentLayout.removeView(draggedView);
                    parentLayout.addView(draggedView, targetIndex);
                    break;
            }
            return true;
        };

// Apply drag to all views
        dcumentId.setOnLongClickListener(dragStartListener);
        fileFolder2.setOnLongClickListener(dragStartListener);
        fileFolder.setOnLongClickListener(dragStartListener);
        fav.setOnLongClickListener(dragStartListener);

        dcumentId.setOnDragListener(dragListener);
        fileFolder2.setOnDragListener(dragListener);
        fileFolder.setOnDragListener(dragListener);
        fav.setOnDragListener(dragListener);

// Repeat for videoId, imageIdd, fileFolder etc.


    }

    void goToAudio(){
    LinearLayout linearLayout;
    linearLayout =findViewById(R.id.Audioo);
    linearLayout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this , SeprateAudio.class);
            startActivity(intent);

        }
    });

    LinearLayout linearLayout2;
    linearLayout2 = findViewById(R.id.apklayout);
    linearLayout2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(MainActivity.this , SeprateApk.class);
            startActivity(intent);
        }
    });
        LinearLayout linearLayout3;
        linearLayout3 = findViewById(R.id.dol);
        linearLayout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , SeprateDowenlode.class);
                startActivity(intent);
            }
        });
        LinearLayout linearLayout4;
        linearLayout4 = findViewById(R.id.RecentLayout);
        linearLayout4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , RecentFiles.class);
                startActivity(intent);
            }
        });
        LinearLayout linearLayout5;
        linearLayout5 = findViewById(R.id.TreshId);
        linearLayout5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , TreshActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout linearLayout6;
        linearLayout6 = findViewById(R.id.safeBoxLayout);
        linearLayout6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , SafeBoxActivity.class);
                startActivity(intent);
            }
        });

        LinearLayout linearLayout7;
        linearLayout7 = findViewById(R.id.safeBoxLayout2);
        linearLayout7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , AppsActivity.class);
                startActivity(intent);
            }
        });


        //safeBoxLayout2
    }

    //==========================================================================


     private void addLegendRowT(String label, float sizeInMB, int color) {
        LinearLayout row = new LinearLayout(this);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setPadding(0, 28, 0, 4);
        row.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));


        View colorDot = new View(this);
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(30, 30);
        dotParams.rightMargin = 16;
        colorDot.setLayoutParams(dotParams);

// Make it circular using GradientDrawable
        GradientDrawable circleDrawable = new GradientDrawable();
        circleDrawable.setShape(GradientDrawable.OVAL);
        circleDrawable.setColor(color);
        circleDrawable.setSize(30, 30);
        colorDot.setBackground(circleDrawable);




        // Label TextView
        TextView labelText = new TextView(this);
        labelText.setText(label);
        labelText.setTextColor(Color.LTGRAY);
        labelText.setTextSize(16);

        // Size TextView
        TextView sizeText = new TextView(this);
        sizeText.setText(convertSize(sizeInMB));
        sizeText.setTextColor(Color.LTGRAY);
        sizeText.setTextSize(13);
        sizeText.setPadding(0, 0, 10, 0);

        // Add to row
        row.addView(colorDot);
        row.addView(labelText);
        row.addView(sizeText);

        // Add row to legend
        legendLayout.addView(row);
    }


    private void addLegendRow(String label, float sizeInMB, int color) {
        Context context = getApplicationContext();
        if (context == null) return;

        // Parent layout (horizontal row)
        LinearLayout itemLayout = new LinearLayout(context);
        itemLayout.setOrientation(LinearLayout.HORIZONTAL);
        itemLayout.setGravity(Gravity.CENTER_VERTICAL);

        // Tight padding (kam spacing)
        itemLayout.setPadding(4, 4, 4, 4);

        // Colored dot
        View dot = new View(context);
        LinearLayout.LayoutParams dotParams = new LinearLayout.LayoutParams(18, 18);
        dotParams.setMargins(8, 0, 20, 0); // tighter
        dot.setLayoutParams(dotParams);

        GradientDrawable circle = new GradientDrawable();
        circle.setShape(GradientDrawable.OVAL);
        circle.setColor(color);
        dot.setBackground(circle);

        // Label
        TextView labelText = new TextView(context);
        labelText.setText(label);
        labelText.setTextColor(Color.WHITE);
        labelText.setTextSize(13); // slightly smaller
        labelText.setPadding(0, 0, 19, 0); // spacing between label and size

        // Size
        TextView sizeText = new TextView(context);
        sizeText.setText(convertSize(sizeInMB));
        sizeText.setTextColor(Color.LTGRAY);
        sizeText.setTextSize(12);


        //  Theme-wise text color
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String value = prefs.getString("theme_choice", "system_default");
        if ("light".equals(value)) {
            labelText.setTextColor(Color.BLACK);
            sizeText.setTextColor(Color.DKGRAY);
        } else if("dark".equals(value)) {
            labelText.setTextColor(Color.WHITE);
            sizeText.setTextColor(Color.LTGRAY);
        }else if ("system_default".equals(value)) {
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                labelText.setTextColor(Color.WHITE);
                sizeText.setTextColor(Color.LTGRAY);
            } else {
                labelText.setTextColor(Color.BLACK);
                sizeText.setTextColor(Color.DKGRAY);
            }
        }


        // Add views to row
        itemLayout.addView(dot);
        itemLayout.addView(labelText);
        itemLayout.addView(sizeText);

        // Grid cell layout (tight margins)
        GridLayout.LayoutParams gridParams = new GridLayout.LayoutParams();
        gridParams.setMargins(4, 15, 0, 15);  // left/right tight
        gridParams.setGravity(Gravity.START);

        itemLayout.setLayoutParams(gridParams);

        // Add to GridLayout
        legendLayout.addView(itemLayout);
    }

    //=============================ChartUsingChae=====================
  private void saveScanResultToCache() {
      SharedPreferences prefs = getSharedPreferences("scan_cache", MODE_PRIVATE);
      SharedPreferences.Editor editor = prefs.edit();
      for (int i = 0; i < categorySizes.length; i++) {
          editor.putLong("size_" + i, categorySizes[i]);
      }
      editor.apply();
  }
    private boolean loadCachedScanResult() {
        SharedPreferences prefs = getSharedPreferences("scan_cache", MODE_PRIVATE);
        boolean hasData = false;
        for (int i = 0; i < categorySizes.length; i++) {
            long size = prefs.getLong("size_" + i, -1);
            if (size != -1) {
                categorySizes[i] = size;
                hasData = true;
            }
        }
        return hasData;
    }
    private void setupPieChart() {
        // UI par "Scanning..." text
        centerText.setText("Scanning...");
        pieChart.setCenterTextSize(47f);

        boolean hasCache = loadCachedScanResult();
        if (hasCache) {
            updatePieChart();
        } else {

            runOnUiThread(() -> {
                legendLayout.removeAllViews();
                pieChart.clear();
                centerText.setText("Loading...");
            });
        }

        // Fresh scan background me
        new Thread(() -> {
            try {
                Arrays.fill(categorySizes, 0);
                File rootDir = Environment.getExternalStorageDirectory();
                scanStorage(rootDir);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    categorySizes[3] = getTotalSizeOfInstalledUserApps();
                }

                saveScanResultToCache();

                runOnUiThread(() -> updatePieChart());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    private void updatePieChart() {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        String[] labels = {"Audio", "Video", "Image", "Apps", "APK", "Doc"};
        int[] colorValues = {
                Color.RED, Color.parseColor("#6699FF"), Color.parseColor("#9C27B0"),
                Color.parseColor("#AEEA00"), Color.parseColor("#40C4FF"), Color.parseColor("#00FF99")
        };

        for (int i = 0; i < labels.length; i++) {
            float sizeInMB = categorySizes[i] / (1024f * 1024f);
            if (sizeInMB > 0) {
                entries.add(new PieEntry(sizeInMB, labels[i]));
                colors.add(colorValues[i]);
            }
        }

        // check before making dataset
        if (entries.isEmpty()) {
            centerText.setText("No data");
            return;
        }

        legendLayout.removeAllViews();
        for (int i = 0; i < entries.size(); i++) {
            addLegendRow(entries.get(i).getLabel(), entries.get(i).getValue(), colors.get(i));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(colors);
        dataSet.setDrawValues(false);

        PieData pieData = new PieData(dataSet);
        pieChart.setData(pieData);
        pieChart.setHoleRadius(65f);
        pieChart.setTransparentCircleRadius(70f);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.getLegend().setEnabled(false);

        // add animation here
        pieChart.animateY(1000, com.github.mikephil.charting.animation.Easing.EaseInOutQuad);

        pieChart.invalidate();

        centerText.setText("Internal");
        pieChart.setCenterTextSize(18f);
    }


//=======================EndChart============================





    private String convertSize(float sizeMB) {
        if (sizeMB >= 1024) {
            float sizeGB = sizeMB / 1024;
            return String.format("%.1f GB", sizeGB);
        } else {
            return String.format("%.0f MB", sizeMB);
        }
    }

    private void scanStorage(File dir) {
        if (dir == null || !dir.exists()) return;

        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                // Recursively scan subdirectories
                scanStorage(file);
            } else {
                String name = file.getName().toLowerCase();

                // Audio files
                if (name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".aac")
                        || name.endsWith(".flac") || name.endsWith(".m4a") || name.endsWith(".ogg")) {
                    categorySizes[0] += file.length(); // Audio
                }

                // Video files
                else if (name.endsWith(".mp4") || name.endsWith(".mkv") || name.endsWith(".avi") || name.endsWith(".mov")) {
                    categorySizes[1] += file.length(); // Video
                }
                // Image files
                else if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif") || name.endsWith(".webp")) {
                    categorySizes[2] += file.length(); // Image
                }
                // APK files
                else if (name.endsWith(".apk")) {
                    categorySizes[4] += file.length(); // APK
                }
                // Document files
                else if (name.endsWith(".pdf") || name.endsWith(".doc") || name.endsWith(".docx") || name.endsWith(".txt") || name.endsWith(".xls")) {
                    categorySizes[5] += file.length(); // Doc
                }
            }
        }
    }





    @TargetApi(Build.VERSION_CODES.O)
    private long getTotalSizeOfInstalledUserApps() {
        long totalAppSize = 0;
        PackageManager pm = getPackageManager();

        StorageStatsManager storageStatsManager = (StorageStatsManager) getSystemService(Context.STORAGE_STATS_SERVICE);
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);

        for (ApplicationInfo app : apps) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) { // User apps only
                try {
                    StorageStats stats = storageStatsManager.queryStatsForPackage(
                            app.storageUuid,
                            app.packageName,
                            android.os.Process.myUserHandle()
                    );
                    totalAppSize += stats.getAppBytes();   // .apk
                    totalAppSize += stats.getDataBytes();  // internal data
                    totalAppSize += stats.getCacheBytes(); // cache
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return totalAppSize;
    }


    //=================================DarkandLightMode=====================================

private void applyTheme() {

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
    if (!prefs.contains("theme_choice")) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("theme_choice", "system_default");
        editor.apply();
    }
    String themePref = prefs.getString("theme_choice", "system_default");

    List<TextView> textViews = Arrays.asList(
            findViewById(R.id.Imagetext),
            findViewById(R.id.Recenttext),
            findViewById(R.id.Dowenlodetext),
            findViewById(R.id.Treshtext),
            findViewById(R.id.Audiotext),
            findViewById(R.id.Videotext),
            findViewById(R.id.Apktext),
            findViewById(R.id.Documenttext),
            findViewById(R.id.SafeBoxtext),
            findViewById(R.id.SafeBoxtext2),
            findViewById(R.id.header),
            findViewById(R.id.RecentText),
            findViewById(R.id.AllStorageText),
            findViewById(R.id.storageTitle),
            findViewById(R.id.internalstor2),
            findViewById(R.id.internalstor),
            findViewById(R.id.SDTetxt),
            findViewById(R.id.favoritesTitle)
    );

    List<LinearLayout> layouts = Arrays.asList(
            findViewById(R.id.imageIdd), findViewById(R.id.RecentLayout), findViewById(R.id.dol),
            findViewById(R.id.TreshId), findViewById(R.id.Audioo), findViewById(R.id.videoId),
            findViewById(R.id.apklayout), findViewById(R.id.dcumentId), findViewById(R.id.safeBoxLayout), findViewById(R.id.safeBoxLayout2)
    );

    LinearLayout fileAndFolder = findViewById(R.id.fileFolder);
    LinearLayout sdCard = findViewById(R.id.SDCARD);
    LinearLayout oHeader = findViewById(R.id.o_header);
    LinearLayout fevLayout = findViewById(R.id.fevLayout);
    NestedScrollView nestedScrollView = findViewById(R.id.dregDrop);
    LinearLayout dlMode = findViewById(R.id.DLMode);
    GridLayout gridLayout = findViewById(R.id.GLDMode);
    RecyclerView recyclerViewFav = findViewById(R.id.recyclerViewforFav);
    TextView header = findViewById(R.id.header);

    if ("light".equals(themePref)) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        nestedScrollView.setBackgroundColor(Color.parseColor("#e6e6e6"));

        NavigationView navigationView1 = findViewById(R.id.nv2);
        navigationView1.setBackground(ContextCompat.getDrawable(this, R.drawable.gride_seprate));
        navigationView1.setItemTextColor(ContextCompat.getColorStateList(this, android.R.color.black));


        dlMode.setBackground(ContextCompat.getDrawable(this, R.drawable.gride_seprate));
        gridLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.gride_light_mode));
        recyclerViewFav.setBackground(ContextCompat.getDrawable(this, R.drawable.gride_light_mode));
        header.setBackground(ContextCompat.getDrawable(this, R.drawable.h_card));
       // header.setBackgroundColor(Color.parseColor("#e6e6e6"));
        oHeader.setBackground(ContextCompat.getDrawable(this, R.drawable.gride_seprate));
        fevLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.gride_seprate));

        Drawable gridItemBg = ContextCompat.getDrawable(this, R.drawable.gride_seprate);
        fileAndFolder.setBackground(gridItemBg);
        sdCard.setBackground(gridItemBg);

        for (LinearLayout l : layouts) {
            l.setBackground(gridItemBg);
        }

        for (TextView tv : textViews) {
            tv.setTextColor(Color.BLACK);
        }

    } else if ("dark".equals(themePref)) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        nestedScrollView.setBackgroundColor(Color.parseColor("#0C0C0C"));
        for (TextView tv : textViews) {
            tv.setTextColor(Color.WHITE);
        }
        // Dark drawables bhi set karo agar hain

    } else if ("system_default".equals(themePref)) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            // System dark mode active
            nestedScrollView.setBackgroundColor(Color.parseColor("#0C0C0C"));
            for (TextView tv : textViews) {
                tv.setTextColor(Color.WHITE);
            }
            // Dark drawables bhi yaha set karo agar hain
        } else {

            NavigationView navigationView1 = findViewById(R.id.nv2);
            navigationView1.setBackground(ContextCompat.getDrawable(this, R.drawable.gride_seprate));
            navigationView1.setItemTextColor(ContextCompat.getColorStateList(this, android.R.color.black));

            // System light mode active
            nestedScrollView.setBackgroundColor(Color.parseColor("#e6e6e6"));

            dlMode.setBackground(ContextCompat.getDrawable(this, R.drawable.gride_seprate));
            gridLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.gride_light_mode));
            recyclerViewFav.setBackground(ContextCompat.getDrawable(this, R.drawable.gride_light_mode));
            header.setBackground(ContextCompat.getDrawable(this, R.drawable.h_card));
           // header.setBackgroundColor(Color.parseColor("#e6e6e6"));
            oHeader.setBackground(ContextCompat.getDrawable(this, R.drawable.gride_seprate));
            fevLayout.setBackground(ContextCompat.getDrawable(this, R.drawable.gride_seprate));

            Drawable gridItemBg = ContextCompat.getDrawable(this, R.drawable.gride_seprate);
            fileAndFolder.setBackground(gridItemBg);
            sdCard.setBackground(gridItemBg);

            for (LinearLayout l : layouts) {
                l.setBackground(gridItemBg);
            }

            for (TextView tv : textViews) {
                tv.setTextColor(Color.BLACK);
            }
        }
    }
}



    private void checkAndShowNoFavImage() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        String themePref = prefs.getString("theme_choice", "system_default");

        FavoritesDbHelper dbHelper = new FavoritesDbHelper(MainActivity.this);
        Set<String> favorites = dbHelper.getAllFavorites();

        ImageView noFav = findViewById(R.id.noFav);

        if (favorites.isEmpty()) {
            noFav.setVisibility(View.VISIBLE);

            if ("light".equals(themePref)) {
                noFav.setImageResource(R.drawable.light_no_fav);

            } else if ("dark".equals(themePref)) {
                noFav.setImageResource(R.drawable.no_fav);
                noFav.setBackgroundColor(Color.BLACK);
            } else {
                int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    noFav.setImageResource(R.drawable.no_fav);
                    noFav.setBackgroundColor(Color.BLACK);
                } else {
                    noFav.setImageResource(R.drawable.light_no_fav);
                }
            }
        } else {
            noFav.setVisibility(View.GONE);
        }
    }


private void ai() {}
void sdCardVisibility() {
    LinearLayout sdCardLayout = findViewById(R.id.SDCARD);
    ProgressBar sdProgressBar = findViewById(R.id.storageProgressSD);

    File[] externalStorageVolumes = ContextCompat.getExternalFilesDirs(this, null);

    Log.d("SD_CHECK", "Total Volumes: " + externalStorageVolumes.length);

    for (int i = 0; i < externalStorageVolumes.length; i++) {
        Log.d("SD_CHECK", "Volume " + i + ": " + externalStorageVolumes[i].getAbsolutePath());
    }

    if (externalStorageVolumes.length > 1 && externalStorageVolumes[1] != null) {
        File sdCard = externalStorageVolumes[1];

        sdCardLayout.setVisibility(View.VISIBLE);
        sdProgressBar.setVisibility(View.VISIBLE);

        StatFs stat = new StatFs(sdCard.getPath());
        long total = stat.getBlockCountLong() * stat.getBlockSizeLong();
        long free = stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
        long used = total - free;

        int progress = total > 0 ? (int) ((used * 100) / total) : 0;
        sdProgressBar.setProgress(progress);

    } else {
        Log.w("SD_CHECK", "No SD card found");
        sdCardLayout.setVisibility(View.GONE);
    }
}




}
