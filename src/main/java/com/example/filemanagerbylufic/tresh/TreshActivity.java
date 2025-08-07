package com.example.filemanagerbylufic.tresh;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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

import com.example.filemanagerbylufic.FileActivity;
import com.example.filemanagerbylufic.MainActivity;
import com.example.filemanagerbylufic.R;
import com.example.filemanagerbylufic.RestoreListener;
import com.example.filemanagerbylufic.TrashDeleteCallBack;
import com.example.filemanagerbylufic.adeptor.RecentFileAdapter;
import com.example.filemanagerbylufic.adeptor.TreshAdeptor;
import com.example.filemanagerbylufic.separateWork.RecentFiles;
import com.example.filemanagerbylufic.separateWork.SeprateVideo;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class TreshActivity extends AppCompatActivity implements TrashDeleteCallBack {
    List<TrashModel> trashList;
    TreshAdeptor adapter;

    private static final String PREF_NAME = "view_mode_pref";
    private static final String KEY_VIEW_MODE = "view_mode";
    int savedMode = 0;
    File currentFolder;
    private RecyclerView recyclerView;
    private Toolbar toolbar;
    private TrashDbHelper dbHelper;
    private FastAdapter<TreshAdeptor> fastAdapter;
    private ItemAdapter<TreshAdeptor> itemAdapter;
    SelectExtension<TreshAdeptor> selectExtension;
    boolean  selectMode = false;

    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tresh);

        toolbar = findViewById(R.id.toolbarTresh);
        toolbar.setTitle("Trash");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Trash");
            // getSupportActionBar().setSubtitle("Loading...");

            Drawable overflowIcon = toolbar.getOverflowIcon();
            if (overflowIcon != null) {
                overflowIcon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
            Drawable upArrow = toolbar.getNavigationIcon();
            if (upArrow != null) {
                upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
        }

        dbHelper = new TrashDbHelper(this);

        recyclerView = findViewById(R.id.seprateTreshRecycle);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);
        recyclerView.setAdapter(fastAdapter);

        //==============================SF=======================
//        SharedPreferences preferences = getSharedPreferences(PREF_SORT, MODE_PRIVATE);
//        int savedSortOption = preferences.getInt(KEY_SORT_OPTION, 0); // Default is Name
//        applySort(savedSortOption);
//============================ViewList===========================
        SharedPreferences modePrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        savedMode = modePrefs.getInt(KEY_VIEW_MODE, 0);
        applyViewMode(savedMode);

        setupFastAdapterSelection();
        progressBar = findViewById(R.id.Tpro);


//===================Trash Delated According to User Prefrance By Settings=======================
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(TreshActivity.this);
        int trashDays = Integer.parseInt(prefs.getString("trash_delete_days", "15"));
        TrashDbHelper dbHelper = new TrashDbHelper(this);
        dbHelper.deleteTrashOlderThan(trashDays);
        setHeaderText(trashDays);
        loadTrashItems();

        ThemeChgange();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }


    public void loadTrashItems() {
        List<TrashModel> trashItems = dbHelper.getAllTrashItems();
        List<TreshAdeptor> newItemList = new ArrayList<>();
        int i = 0;

        for (TrashModel trashModel : trashItems) {
            String path = trashModel.getPath();
            File file = new File(path);
            if (!file.exists()) continue;

            // यदि TrashModel में name, size, deletedAt पहले से मौजूद हैं तो ये लाइनें जरूरी नहीं
            trashModel.setName(file.getName());
            trashModel.setSize(file.length() + " bytes");

            TreshAdeptor item = new TreshAdeptor(this, trashModel, dbHelper,
                    new RestoreListener() {
                        @Override
                        public void onRestoreStarted() {
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onRestoreFinished() {
                            progressBar.setVisibility(View.GONE);
                        }
                    },
                    new TrashDeleteCallBack() {
                        @Override
                        public void onDelete(int position) {
                            itemAdapter.remove(position);
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setSubtitle(itemAdapter.getAdapterItemCount() + " items");
                            }
                        }

                        @Override
                        public boolean loadStarted() {
                            progressBar.setVisibility(View.VISIBLE);
                            return true;
                        }

                        @Override
                        public boolean loadFinish() {
                            progressBar.setVisibility(View.GONE);
                            if (getSupportActionBar() != null) {
                                getSupportActionBar().setSubtitle(itemAdapter.getAdapterItemCount() + " items");
                            }
                            return true;
                        }
                    });

            item.setPosition(i++);
            newItemList.add(item);
        }

        itemAdapter.set(newItemList);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(newItemList.size() + " items");
        }
    }







    private void openFile(Context context, File file) {
    if (!file.exists()) {
        Toast.makeText(context, "File not found", Toast.LENGTH_SHORT).show();
        return;
    }

    if (file.isDirectory()) {
        openFolderInternally(file);
        return;
    }
    Uri uri = FileProvider.getUriForFile(
            context,
            context.getPackageName() + ".provider",
            file
    );

    String mimeType = context.getContentResolver().getType(uri);
    if (mimeType == null) {
        mimeType = "*/*";
    }

    Intent intent = new Intent(Intent.ACTION_VIEW);
    intent.setDataAndType(uri, mimeType);
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);

    try {
        context.startActivity(intent);
    } catch (Exception e) {
        Toast.makeText(context, "No app found to open this file", Toast.LENGTH_SHORT).show();
    }
}
    private void openFolderInternally(File folder) {

        currentFolder = folder;
        loadFiles(folder);
    }
//    private void loadFiles(File folder) {
//        List<TreshAdeptor> folderItems = new ArrayList<>();
//
//        File[] files = folder.listFiles();
//        if (files != null) {
//            for (File file : files) {
//
//                TrashModel model = new TrashModel(
//                        file.getAbsolutePath(),
//                        file.getName(),
//                        file.isFile() ? (file.length() / 1024 + " KB") : "Folder",
//                        String.valueOf(System.currentTimeMillis())
//                );
//                folderItems.add(new TreshAdeptor(this, model, dbHelper, new RestoreListener() {
//                    @Override
//                    public void onRestoreStarted() {
//                        progressBar.setVisibility(View.VISIBLE);
//                    }
//
//                    @Override
//                    public void onRestoreFinished() {
//                        progressBar.setVisibility(View.GONE);
//                    }
//                }));
//            }
//        }

//        itemAdapter.set(folderItems);
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setSubtitle(folderItems.size() + " items");
//        }
//    }

    private void loadFiles(File folder) {
        List<TreshAdeptor> folderItems = new ArrayList<>();

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {

                TrashModel model = new TrashModel(
                        file.getAbsolutePath(),
                        file.getName(),
                        file.isFile() ? (file.length() / 1024 + " KB") : "Folder",
                        String.valueOf(System.currentTimeMillis())
                );

                folderItems.add(new TreshAdeptor(this, model, dbHelper,
                        new RestoreListener() {
                            @Override
                            public void onRestoreStarted() {
                                progressBar.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onRestoreFinished() {
                                progressBar.setVisibility(View.GONE);
                            }
                        },
                        new TrashDeleteCallBack() {
                            @Override
                            public void onDelete(int position) {

                            }
                            @Override
                            public boolean loadStarted() {
                                progressBar.setVisibility(View.VISIBLE);
                                return true;
                            }

                            @Override
                            public boolean loadFinish() {
                                progressBar.setVisibility(View.GONE);
                                loadFiles(folder);
                                return true;
                            }
                        }
                ));
            }
        }

        itemAdapter.set(folderItems);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(folderItems.size() + " items");
        }
    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences prefs = getSharedPreferences("view_mode_pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (id == R.id.action_home2) {
            Toast.makeText(this, "Home clicked", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(TreshActivity.this , MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_search2) {
            Intent intent = new Intent(TreshActivity.this, FileActivity.class);
            intent.putExtra("openSearch", true);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_sort2) {

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
            applyViewMode(0);
            return true;
        } else if (id==R.id.view_grid2) {
            item.setChecked(true);
            editor.putInt(KEY_VIEW_MODE, 1);
            editor.apply();
            applyViewMode(1);
            return true;
        } else if (id==R.id.action_select_all2) {
            toggleSelectAll();
            return true;
        } else if (id==R.id.action_delete_selected2) {
            //deleteSelectedFilesWithConfirmation();
           // deleteRecursive2(currentFolder);
          //  removeFromTrash(currentFolder);
            if (selectExtension != null && !selectExtension.getSelectedItems().isEmpty()) {

                Set<TreshAdeptor> selectedItems = new HashSet<>(selectExtension.getSelectedItems());

                TrashDbHelper dbHelper = new TrashDbHelper(this);

                for (TreshAdeptor adaptorItem : selectedItems) {
                    File file = adaptorItem.getFile();

                    // Delete file from storage
                    if (file != null && file.exists()) {
                        file.delete();
                    }

                    // Delete entry from DB
                    dbHelper.removeFromTrash(file.getAbsolutePath());

                    // Remove item from model list
                   // modelList.remove(adaptorItem);
                }

                // Refresh adapter
                fastAdapter.notifyAdapterDataSetChanged();

                // Clear selection
                selectExtension.deselect();

                Toast.makeText(this, "Selected files deleted permanently", Toast.LENGTH_SHORT).show();


                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Trash");
                }

                selectMode = false;
                loadTrashItems();
            } else {
                Toast.makeText(this, "No files selected", Toast.LENGTH_SHORT).show();
            }

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
      //  MenuItem search = menu.findItem(R.id.action_search2);
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
         //   search.setVisible(false);
            home.setVisible(false);
            sort.setVisible(false);
            advance.setVisible(false);

        } else {
            compress.setVisible(false);
            properties.setVisible(false);
            delete.setVisible(false);
            select.setVisible(false);
          //  search.setVisible(true);
            home.setVisible(true);
            sort.setVisible(false);
            advance.setVisible(false);
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

//        IconicsDrawable iconSearch = new IconicsDrawable(this)
//                .icon(CommunityMaterial.Icon2.cmd_magnify)
//                .sizeDp(24)
//                .color(Color.WHITE);
//        search.setIcon(iconSearch);

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

    //=====+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


    //=========================View Mode==========================
    private void applyViewMode(int mode) {
        if (mode == 0) {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        } else {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }


        recyclerView.setAdapter(fastAdapter);
    }



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

            if (item instanceof TreshAdeptor) {
                File file = ((TreshAdeptor) item).getFile();

                if (selectExtension != null) {
                    Set<TreshAdeptor> selectedItems = selectExtension.getSelectedItems();
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
                if (item instanceof TreshAdeptor) {
                    File file = ((TreshAdeptor) item).getFile();
                    openFile(TreshActivity.this,file);

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
            Set<TreshAdeptor> selectedItems = selectExtension.getSelectedItems();
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
//    private void deleteSelectedFilesWithConfirmation() {
//        final Set<TreshAdeptor> selectedItems = new HashSet<>(selectExtension.getSelectedItems());
//
//        if (selectedItems.isEmpty()) {
//            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//
//        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_options, null);
//        CheckBox checkbox = dialogView.findViewById(R.id.checkbox_move_to_trash);
//
//
//        new AlertDialog.Builder(TreshActivity.this)
//                .setTitle("Delete Confirmation")
//                .setView(dialogView)
//                .setPositiveButton("Yes", (dialog, which) -> {
//
//                    boolean moveToTrash = checkbox.isChecked();
//                    progressBar.setVisibility(View.VISIBLE);
//
//                    ExecutorService executor = Executors.newSingleThreadExecutor();
//                    executor.execute(() -> {
//                        TrashDbHelper trashDbHelper = new TrashDbHelper(TreshActivity.this);
//
//                        File trashDir = new File(getFilesDir(), "Trash");
//                        if (!trashDir.exists()) trashDir.mkdirs();
//
//                        File uniqueTrashFolder = new File(trashDir, "trash_" + System.currentTimeMillis());
//                        if (!uniqueTrashFolder.exists()) uniqueTrashFolder.mkdirs();
//
//                        AtomicBoolean allSuccess = new AtomicBoolean(true);
//
//                        for (TreshAdeptor item : selectedItems) {
//                            File sourceFile = item.getFile();
//
//                            if (sourceFile != null && sourceFile.exists()) {
//
//                                if (moveToTrash) {
//                                    File dest = new File(uniqueTrashFolder, sourceFile.getName());
//
//                                    boolean copied = copyDirectoryOrFile(sourceFile, dest);
//                                    if (copied) {
//                                        boolean deleted = deleteRecursive2(sourceFile);
//                                        if (deleted) {
//                                            addToTrashDBRecursive(trashDbHelper, dest);
//                                        } else {
//                                            allSuccess.set(false);
//                                        }
//                                    } else {
//                                        allSuccess.set(false);
//                                    }
//
//                                } else {
//                                    boolean deleted = deleteRecursive2(sourceFile);
//                                    if (!deleted) {
//                                        allSuccess.set(false);
//                                    }
//                                }
//                            }
//                        }
//
//                        runOnUiThread(() -> {
//                            progressBar.setVisibility(View.GONE);
//                            if (allSuccess.get()) {
//                                Toast.makeText(TreshActivity.this, moveToTrash ? "Moved to Trash" : "Deleted", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(TreshActivity.this, "Some files couldn't be processed", Toast.LENGTH_SHORT).show();
//                            }
//
//                            selectExtension.deselect();
//                            // loadFiles(currentFolder);
//                            recreate();
//                        });
//                    });
//                })
//                .setNegativeButton("Cancel", null)
//                .show();
//    }




//    private boolean copyDirectoryOrFile(File source, File dest) {
//        try {
//            if (source.isDirectory()) {
//                if (!dest.exists()) dest.mkdirs();
//                File[] children = source.listFiles();
//                if (children != null) {
//                    for (File child : children) {
//                        File newDest = new File(dest, child.getName());
//                        if (!copyDirectoryOrFile(child, newDest)) return false;
//                    }
//                }
//            } else {
//                try (InputStream in = new FileInputStream(source);
//                     OutputStream out = new FileOutputStream(dest)) {
//                    byte[] buffer = new byte[1024];
//                    int length;
//                    while ((length = in.read(buffer)) > 0) {
//                        out.write(buffer, 0, length);
//                    }
//                }
//            }
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
   // }
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
//        String path = file.getAbsolutePath();
//        String name = file.getName();
//        String size = file.isFile() ? (file.length() / 1024 + " KB") : "Folder";
//        String deletedAt = String.valueOf(System.currentTimeMillis());
//
//        db.addToTrash(path, name, size, deletedAt );
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
                Set<TreshAdeptor> selectedItems = selectExtension.getSelectedItems();
                List<File> filesToZip = new ArrayList<>();

                for (TreshAdeptor item : selectedItems) {
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
        Set<TreshAdeptor> selectedItems = selectExtension.getSelectedItems();

        int fileCount = 0;
        int folderCount = 0;
        long totalSize = 0;

        for (TreshAdeptor item : selectedItems) {
            if (item instanceof TreshAdeptor) {
                File file = ((TreshAdeptor) item).getFile();

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

    void setHeaderText(int days){
        LinearLayout includedLayout = findViewById(R.id.trashHeader);
        TextView trashTitle = includedLayout.findViewById(R.id.Texttrash);
        trashTitle.setText("Items in Trash older than "+days+" days will be permanently deleted.");

    }

    void ThemeChgange(){
        //============== Dark / Light Mode ==============
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(TreshActivity.this);
        String themeValue = prefs.getString("theme_choice", "system_default");

        if ("dark".equals(themeValue)) {
            RecyclerView r = findViewById(R.id.seprateTreshRecycle);
            r.setBackgroundColor(Color.BLACK);
        } else {
            RecyclerView r = findViewById(R.id.seprateTreshRecycle);
            r.setBackgroundColor(Color.WHITE);
        }
    }


    @Override
    public void onDelete(int position) {
loadTrashItems();
    }

    @Override
    public boolean loadStarted() {
        return true;
    }

    @Override
    public boolean loadFinish() {
        loadTrashItems();
        return true;
    }
    private void removeFromTrash(File file) {
        TrashDbHelper dbHelper = new TrashDbHelper(this);  // ya jo bhi aapka DB helper ka class hai
        try {
            String path = file.getAbsolutePath();

            // Delete file from storage if it exists
            if (file.exists()) {
                if (file.delete()) {
                    int rows = dbHelper.removeFromTrash(path);
                    if (rows > 0) {
                        Toast.makeText(this, "Deleted permanently", Toast.LENGTH_SHORT).show();
                        loadTrashItems();  // UI refresh
                    } else {
                        Toast.makeText(this, "Failed to remove from DB", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Failed to delete file", Toast.LENGTH_SHORT).show();
                }
            } else {
                // If file doesn't exist, still remove from DB
                int rows = dbHelper.removeFromTrash(path);
                if (rows > 0) {
                    Toast.makeText(this, "File not found, but removed from DB", Toast.LENGTH_SHORT).show();
                    loadTrashItems();  // UI refresh
                } else {
                    Toast.makeText(this, "File doesn't exist and couldn't remove from DB", Toast.LENGTH_SHORT).show();
                }
            }
        } finally {
            dbHelper.close();
        }
    }


}
