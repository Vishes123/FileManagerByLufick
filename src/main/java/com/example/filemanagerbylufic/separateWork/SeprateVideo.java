package com.example.filemanagerbylufic.separateWork;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanagerbylufic.AllPermissionOfFileManager;
import com.example.filemanagerbylufic.CallBackVideo;
import com.example.filemanagerbylufic.FileActivity;
import com.example.filemanagerbylufic.MainActivity;
import com.example.filemanagerbylufic.R;
import com.example.filemanagerbylufic.adeptor.SeprateImageAdeptor;
import com.example.filemanagerbylufic.adeptor.SeprateVideoAdeptor;
import com.example.filemanagerbylufic.tresh.TrashDbHelper;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SeprateVideo extends AppCompatActivity implements CallBackVideo {
    private static final String PREF_SORT = "sort_preference3";
    private static final String KEY_SORT_OPTION = "sort_option3";

    private static final String PREF_NAME = "view_mode_pref3";
    private static final String KEY_VIEW_MODE = "view_mode3";
    private int savedMode = 0;
    private RecyclerView recyclerView;
    private FastAdapter<SeprateVideoAdeptor> fastAdapter;
    private ItemAdapter<SeprateVideoAdeptor> itemAdapter;
    SelectExtension<SeprateVideoAdeptor> selectExtension;

    boolean selectMode = false;
    ProgressBar progressBar;
    private Toolbar toolbar;
    private boolean isGrid = false;
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_seprate_video);
        progressBar =findViewById(R.id.videoProgress);

        toolbar = findViewById(R.id.toolbar2);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Videos");


            Drawable overflowIcon = toolbar.getOverflowIcon();
            if (overflowIcon != null) {
                overflowIcon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
            Drawable upArrow = toolbar.getNavigationIcon();
            if (upArrow != null) {
                upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
        }

        // RecyclerView setup
        recyclerView = findViewById(R.id.Videorerecycle);
        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(fastAdapter);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);


        fastAdapter.withOnClickListener((v, adapter, item, position) -> {
            if (item instanceof SeprateVideoAdeptor) {
                File file = ((SeprateVideoAdeptor) item).videoFile;
                openVideo(file);
            }
            return true;
        });

        // Load videos based on permission
        if (AllPermissionOfFileManager.hasVideoPermission(this)) {
            loadVideos();
        } else {
            AllPermissionOfFileManager.requestVideoPermission(this);
        }

        //==============================SP=======================
        SharedPreferences preferences = getSharedPreferences(PREF_SORT, MODE_PRIVATE);
        int savedSortOption = preferences.getInt(KEY_SORT_OPTION, 4); // Default is Name
        applySort(savedSortOption);
//============================ViewList===========================
        SharedPreferences modePrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        savedMode = modePrefs.getInt(KEY_VIEW_MODE, 0);
        applyViewMode(savedMode);

        setupFastAdapterSelection();
        ThemeChgange();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    private void openVideo(File file) {
        try {
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "video/*");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(Intent.createChooser(intent, "Play video using"));
        } catch (Exception e) {
            Toast.makeText(this, "Can't open video", Toast.LENGTH_SHORT).show();
        }
    }


    private void loadVideos() {
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {

            List<File> videoFiles = getAllVideosFromMediaStore();

            SharedPreferences preferences = getSharedPreferences(PREF_SORT, MODE_PRIVATE);
            int savedSortOption = preferences.getInt(KEY_SORT_OPTION, 0);
            sortFileList(videoFiles, savedSortOption);

            List<SeprateVideoAdeptor> items = new ArrayList<>();
            for (File file : videoFiles) {
                items.add(new SeprateVideoAdeptor(file,SeprateVideo.this,SeprateVideo.this));
            }
            runOnUiThread(() -> {
                itemAdapter.set(items);
                progressBar.setVisibility(View.GONE);

                if (getSupportActionBar() != null) {
                    getSupportActionBar().setSubtitle(videoFiles.size() + " Videos Found");
                }
            });
        }).start();
    }


    private List<File> getAllVideosFromMediaStore() {
        List<File> videoFiles = new ArrayList<>();

        String[] projection = {
                MediaStore.Video.Media.DATA
        };

        Cursor cursor = getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Video.Media.DATE_ADDED + " DESC"
        );

        if (cursor != null) {
            int dataColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            while (cursor.moveToNext()) {
                String path = cursor.getString(dataColumn);
                File file = new File(path);
                if (file.exists()) {
                    videoFiles.add(file);
                }
            }
            cursor.close();
        }

        return videoFiles;
    }




    private List<File> getAllVideos() {
        List<File> videoList = new ArrayList<>();
        File root = Environment.getExternalStorageDirectory();
        scanVideos(root, videoList);
        return videoList;
    }

    private void scanVideos(File dir, List<File> list) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanVideos(file, list);
            } else if (isVideoFile(file)) {
                list.add(file);
            }
        }
    }

    private boolean isVideoFile(File file) {
        String[] exts = {".mp4", ".mkv", ".3gp", ".avi", ".mov"};
        for (String ext : exts) {
            if (file.getName().toLowerCase().endsWith(ext)) return true;
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 103) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadVideos();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_home2) {
            Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(SeprateVideo.this , MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_search2) {
            Intent intent = new Intent(SeprateVideo.this, FileActivity.class);
            intent.putExtra("openSearch", true);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_sort2) {
            showSortPopup();
            return true;
        } else if (id == R.id.action_view2) {
            Toast.makeText(this, "View Option clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_advance2) {
            Toast.makeText(this, "Advance Settings clicked", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_close2) {
            Toast.makeText(this, "Closing...", Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }else if(id==R.id.view_list2){
            item.setChecked(true);
            editor.putInt(KEY_VIEW_MODE, 0);
            editor.apply();
            return true;
        } else if (id==R.id.view_grid2) {
            item.setChecked(true);
            editor.putInt(KEY_VIEW_MODE, 1);
            editor.apply();
            return true;
        }else if (id==R.id.action_select_all2) {
            toggleSelectAll();
            return true;
        } else if (id==R.id.action_delete_selected2) {
            deleteSelectedFilesWithConfirmation();
            return true;
        } else if (id==R.id.action_compress2) {
            File output = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                    "Compressed_" + System.currentTimeMillis() + ".zip");

            compressSelectedFilesToZip(output);
            return true;
        } else if (id==R.id.action_properties2) {
            showSelectedItemsPropertiesDialog();
            return true;
        }

        else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.seprate_utility, menu);
        MenuItem delete = menu.findItem(R.id.action_delete_selected2);
        MenuItem select = menu.findItem(R.id.action_select_all2);
        MenuItem home = menu.findItem(R.id.action_home2);
        MenuItem search = menu.findItem(R.id.action_search2);
        MenuItem sort = menu.findItem(R.id.action_sort2);
        MenuItem viewOption = menu.findItem(R.id.action_view2);
        MenuItem advance = menu.findItem(R.id.action_advance2);
        MenuItem close = menu.findItem(R.id.action_close2);
        MenuItem ListVieww = menu.findItem(R.id.view_list2);
        MenuItem GrideVieww = menu.findItem(R.id.view_grid2);
        MenuItem compress = menu.findItem(R.id.action_compress2);
        MenuItem properties = menu.findItem(R.id.action_properties2);

        if (selectMode) {
            delete.setVisible(true);
            select.setVisible(true);
            compress.setVisible(true);
            properties.setVisible(true);
            search.setVisible(false);
            home.setVisible(false);
            advance.setVisible(false);
        } else {
            advance.setVisible(false);
            compress.setVisible(false);
            properties.setVisible(false);
            delete.setVisible(false);
            select.setVisible(false);
            search.setVisible(true);
            home.setVisible(true);
        }


        IconicsDrawable List = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon2.cmd_view_list)
                .sizeDp(24)
                .color(Color.WHITE);
        ListVieww.setIcon(List);

        IconicsDrawable iconSearc = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon2.cmd_view_grid)
                .sizeDp(24)
                .color(Color.WHITE);
        GrideVieww.setIcon(iconSearc);

        IconicsDrawable iconHome = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon2.cmd_home)
                .sizeDp(24)
                .color(Color.WHITE);
        home.setIcon(iconHome);

        IconicsDrawable iconSearch = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon2.cmd_magnify)
                .sizeDp(24)
                .color(Color.WHITE);
        search.setIcon(iconSearch);

        IconicsDrawable iconSort = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon2.cmd_sort)
                .sizeDp(24)
                .color(Color.WHITE);
        sort.setIcon(iconSort);

        IconicsDrawable iconView = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_grid)
                .sizeDp(24)
                .color(Color.WHITE);
        viewOption.setIcon(iconView);

        IconicsDrawable iconAdvance = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon2.cmd_settings)
                .sizeDp(24)
                .color(Color.WHITE);
        advance.setIcon(iconAdvance);

        IconicsDrawable iconClose = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_close)
                .sizeDp(24)
                .color(Color.WHITE);
        close.setIcon(iconClose);

        IconicsDrawable iconAdvance2 = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_delete)
                .sizeDp(24)
                .color(Color.WHITE);
        delete.setIcon(iconAdvance2);

        IconicsDrawable iconClose2 = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_check_all)
                .sizeDp(24)
                .color(Color.WHITE);
        select.setIcon(iconClose2);

        IconicsDrawable iconAdvance22 = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon2.cmd_zip_box)
                .sizeDp(24)
                .color(Color.WHITE);
        compress.setIcon(iconAdvance22);

        IconicsDrawable iconClose22 = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_details)
                .sizeDp(24)
                .color(Color.WHITE);
        properties.setIcon(iconClose22);


        ListVieww.setChecked(savedMode == 0);
        GrideVieww.setChecked(savedMode == 1);

        return super.onCreateOptionsMenu(menu);

    }
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);

            SpannableString spanString = new SpannableString(item.getTitle());
            spanString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanString.length(), 0);
            item.setTitle(spanString);
        }
        return super.onPrepareOptionsMenu(menu);

    }
    private void showSortPopup() {
        String[] options = {"Name", "Size", "Last Modified", "Type", "Newest First", "Oldest First"};

        SharedPreferences preferences = getSharedPreferences(PREF_SORT, MODE_PRIVATE);
        int savedOption = preferences.getInt(KEY_SORT_OPTION, 0); // Default is Name

        final int[] selectedOption = {savedOption};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sort By");

        builder.setSingleChoiceItems(options, selectedOption[0], (dialog, which) -> {
            selectedOption[0] = which;
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            // Save to SharedPreferences
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(KEY_SORT_OPTION, selectedOption[0]);
            editor.apply();

            String selected = options[selectedOption[0]];
            Toast.makeText(this, "Sorted by " + selected, Toast.LENGTH_SHORT).show();

            applySort(selectedOption[0]);
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void applySort(int option) {
        loadVideos();
    }

    private void sortFileList(List<File> files, int option) {
        switch (option) {
            case 0:
                Collections.sort(files, (f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
                break;
            case 1:
                Collections.sort(files, (f1, f2) -> Long.compare(f2.length(), f1.length()));
                break;
            case 2:
                Collections.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
                break;
            case 3:
                Collections.sort(files, (f1, f2) -> {
                    String ext1 = getFileExtension(f1.getName());
                    String ext2 = getFileExtension(f2.getName());
                    return ext1.compareToIgnoreCase(ext2);
                });
                break;
            case 4:
                Collections.sort(files, (f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
                break;
            case 5:
                Collections.sort(files, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));
                break;
        }
    }

    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }
    //=========================View Mode==========================
    private void applyViewMode(int mode) {
        if (mode == 0) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }

        recyclerView.setAdapter(fastAdapter);
    }

    //=====================================================================================
    private void setupFastAdapterSelection() {
        fastAdapter.withSelectable(true);
        fastAdapter.withMultiSelect(true);
        selectExtension = fastAdapter.getExtension(SelectExtension.class);

        if (selectExtension != null) {
            selectExtension.withSelectable(true);
            selectExtension.withMultiSelect(true);
            selectExtension.withAllowDeselection(true);
            selectExtension.withSelectOnLongClick(true);
        }


        fastAdapter.withOnLongClickListener((v, adapter, item, position) -> {
            selectMode = true;

            if (item instanceof SeprateVideoAdeptor) {
                File file = ((SeprateVideoAdeptor) item).getFile();

                if (selectExtension != null) {
                    Set<SeprateVideoAdeptor> selectedItems = selectExtension.getSelectedItems();
                    int count = selectedItems.size();
                    fastAdapter.notifyItemChanged(position);

                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(count + " selected");
                    }

                    Toast.makeText(this, "Selected item: " + count, Toast.LENGTH_SHORT).show();
                }
            }

            return true;
        });

        fastAdapter.withOnClickListener((v, adapter, item, position) -> {
            if (selectMode) {
                fastAdapter.toggleSelection(position);
                return true;
            } else {
                if (item instanceof SeprateVideoAdeptor) {
                    File file = ((SeprateVideoAdeptor) item).getFile();
                    openVideo(file);

                }
                return true;
            }
        });

        // SelectionListener
        fastAdapter.withSelectionListener((item, selected) -> {
            if (selectExtension != null) {
                int count = selectExtension.getSelectedItems().size();

                if (count == 0) {
                    selectMode = false;
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle("Recent");
                    }
                } else {
                    selectMode = true;
                    if (getSupportActionBar() != null) {
                        getSupportActionBar().setTitle(count + " selected");
                    }
                }
                invalidateOptionsMenu();

                fastAdapter.notifyAdapterDataSetChanged();
            }
        });
    }

    //===============================Togele Delete ==========================
    private void toggleSelectAll() {
        if (selectExtension != null) {
            Set<SeprateVideoAdeptor> selectedItems = selectExtension.getSelectedItems();
            if (selectedItems.size() == fastAdapter.getItemCount()) {
                // Deselect All
                selectExtension.deselect();
            } else {
                // Select All
                for (int i = 0; i < fastAdapter.getItemCount(); i++) {
                    selectExtension.select(i);
                }
            }
            fastAdapter.notifyAdapterDataSetChanged();
        }
    }

    ///===============================Add to Tresh =======================================
    private void deleteSelectedFilesWithConfirmation() {
        final Set<SeprateVideoAdeptor> selectedItems = new HashSet<>(selectExtension.getSelectedItems());

        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_options, null);
        CheckBox checkbox = dialogView.findViewById(R.id.checkbox_move_to_trash);

        new AlertDialog.Builder(SeprateVideo.this)
                .setTitle("Delete Confirmation")
                .setView(dialogView)
                .setPositiveButton("Yes", (dialog, which) -> {

                    boolean moveToTrash = checkbox.isChecked();
                    progressBar.setVisibility(View.VISIBLE);

                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        TrashDbHelper trashDbHelper = new TrashDbHelper(SeprateVideo.this);

                        File trashDir = new File(getFilesDir(), "Trash");
                        if (!trashDir.exists()) trashDir.mkdirs();

                        File uniqueTrashFolder = new File(trashDir, "trash_" + System.currentTimeMillis());
                        if (!uniqueTrashFolder.exists()) uniqueTrashFolder.mkdirs();

                        AtomicBoolean allSuccess = new AtomicBoolean(true);

                        for (SeprateVideoAdeptor item : selectedItems) {
                            File sourceFile = item.getFile();

                            if (sourceFile != null && sourceFile.exists()) {

                                if (moveToTrash) {
                                    File dest = new File(uniqueTrashFolder, sourceFile.getName());

                                    boolean copied = copyDirectoryOrFile(sourceFile, dest);
                                    if (copied) {
                                        boolean deleted = deleteRecursive2(sourceFile);
                                        if (deleted) {
                                            //addToTrashDBRecursive(trashDbHelper, dest);
                                            addToTrashDBRecursive(trashDbHelper,dest,sourceFile);
                                        } else {
                                            allSuccess.set(false);
                                        }
                                    } else {
                                        allSuccess.set(false);
                                    }

                                } else {
                                    boolean deleted = deleteRecursive2(sourceFile);
                                    if (!deleted) {
                                        allSuccess.set(false);
                                    }
                                }
                            }
                        }

                        runOnUiThread(() -> {
                            progressBar.setVisibility(View.GONE);
                            if (allSuccess.get()) {
                                Toast.makeText(SeprateVideo.this, moveToTrash ? "Moved to Trash" : "Deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SeprateVideo.this, "Some files couldn't be processed", Toast.LENGTH_SHORT).show();
                            }

                            selectExtension.deselect();
                            // loadFiles(currentFolder);
                            recreate();
                        });
                    });
                })
                .setNegativeButton("Cancel", null)
                .show();
    }




    private boolean copyDirectoryOrFile(File source, File dest) {
        try {
            if (source.isDirectory()) {
                if (!dest.exists()) dest.mkdirs();
                File[] children = source.listFiles();
                if (children != null) {
                    for (File child : children) {
                        File newDest = new File(dest, child.getName());
                        if (!copyDirectoryOrFile(child, newDest)) return false;
                    }
                }
            } else {
                try (InputStream in = new FileInputStream(source);
                     OutputStream out = new FileOutputStream(dest)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) {
                        out.write(buffer, 0, length);
                    }
                }
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    private boolean deleteRecursive2(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] children = fileOrDirectory.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (!deleteRecursive2(child)) return false;
                }
            }
        }
        return fileOrDirectory.delete();
    }

//    private void addToTrashDBRecursive(TrashDbHelper db, File file) {
//        String originalPath = file.getAbsolutePath();
//
//        String path = file.getAbsolutePath();
//        String name = file.getName();
//        String size = file.isFile() ? (file.length() / 1024 + " KB") : "Folder";
//        String deletedAt = String.valueOf(System.currentTimeMillis());
//
//        db.addToTrash(path, name, size, deletedAt);
//
//        if (file.isDirectory()) {
//            File[] children = file.listFiles();
//            if (children != null) {
//                for (File child : children) {
//                    addToTrashDBRecursive(db, child);
//                }
//            }
//        }
//    }

    private void addToTrashDBRecursive(TrashDbHelper db, File trashFile, File originalFile) {
        String path = trashFile.getAbsolutePath();
        String originalPath = originalFile.getAbsolutePath();
        String name = trashFile.getName();
        String size = trashFile.isFile() ? (trashFile.length() / 1024 + " KB") : "Folder";
        String deletedAt = String.valueOf(System.currentTimeMillis());

        // Add to DB with original path also
        db.addToTrash(path, originalPath, name, size, deletedAt);

        // Handle folder children recursively
        if (trashFile.isDirectory()) {
            File[] trashChildren = trashFile.listFiles();
            File[] originalChildren = originalFile.listFiles();

            if (trashChildren != null && originalChildren != null) {
                for (int i = 0; i < trashChildren.length; i++) {
                    addToTrashDBRecursive(db, trashChildren[i], originalChildren[i]);
                }
            }
        }
    }


    private void compressSelectedFilesToZip(File outputZipFile) {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                Set<SeprateVideoAdeptor> selectedItems = selectExtension.getSelectedItems();
                List<File> filesToZip = new ArrayList<>();

                for (SeprateVideoAdeptor item : selectedItems) {
                    filesToZip.add(item.getFile());
                }

                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(outputZipFile));

                for (File file : filesToZip) {
                    zipFile(file, file.getName(), zos);
                }

                zos.close();

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    recreate();
                    Toast.makeText(this, "Compressed to: " + outputZipFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Compression failed", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }



    private void zipFile(File fileToZip, String fileName, ZipOutputStream zos) throws IOException {
        if (fileToZip.isHidden()) return;

        if (fileToZip.isDirectory()) {
            if (!fileName.endsWith("/")) fileName += "/";
            zos.putNextEntry(new ZipEntry(fileName));
            zos.closeEntry();

            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    zipFile(childFile, fileName + childFile.getName(), zos);
                }
            }
            return;
        }

        FileInputStream fis = new FileInputStream(fileToZip);
        zos.putNextEntry(new ZipEntry(fileName));

        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, length);
        }

        fis.close();
    }



    private void showSelectedItemsPropertiesDialog() {
        Set<SeprateVideoAdeptor> selectedItems = selectExtension.getSelectedItems();

        int fileCount = 0;
        int folderCount = 0;
        long totalSize = 0;

        for (SeprateVideoAdeptor item : selectedItems) {
            if (item instanceof SeprateVideoAdeptor) {
                File file = ((SeprateVideoAdeptor) item).getFile();

                if (file.isDirectory()) {
                    folderCount++;
                    totalSize += getFolderSize(file);
                } else {
                    fileCount++;
                    totalSize += file.length();
                }
            }
        }

        String message = "Total Items: " + selectedItems.size() + "\n"
                + "Files: " + fileCount + "\n"
                + "Folders: " + folderCount + "\n"
                + "Total Size: " + android.text.format.Formatter.formatFileSize(this, totalSize);

        new AlertDialog.Builder(this)
                .setTitle("Properties")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
    private long getFolderSize(File folder) {
        long length = 0;
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    length += file.length();
                } else {
                    length += getFolderSize(file);
                }
            }
        }
        return length;
    }


    void ThemeChgange(){
        //============== Dark / Light Mode ==============
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SeprateVideo.this);
        String themeValue = prefs.getString("theme_choice", "system_default");

        if ("dark".equals(themeValue)) {
            RecyclerView r = findViewById(R.id.Videorerecycle);
            r.setBackgroundColor(Color.BLACK);
        } else {
            RecyclerView r = findViewById(R.id.Videorerecycle);
            r.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public boolean loadStarted() {
        progressBar.setVisibility(View.VISIBLE);
        return true;
    }

    @Override
    public boolean loadFinish() {
        loadVideos();
        progressBar.setVisibility(View.GONE);
        return true;
    }
}
