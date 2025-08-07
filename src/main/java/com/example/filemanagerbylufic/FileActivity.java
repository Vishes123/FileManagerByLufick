package com.example.filemanagerbylufic;

import android.annotation.TargetApi;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.pm.ResolveInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.InputType;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;

import android.widget.ScrollView;
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
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.filemanagerbylufic.SqliteDbForFavorite.FavoritesDbHelper;
import com.example.filemanagerbylufic.adeptor.FileAdeptor;
import com.example.filemanagerbylufic.adeptor.HeaderItem;
import com.example.filemanagerbylufic.language.BaseActivity;
import com.example.filemanagerbylufic.safeBox.SafeBoxManager;
import com.example.filemanagerbylufic.settingScreen.Setting;
import com.example.filemanagerbylufic.storagePieCharteStatusGraph.Charte;
import com.example.filemanagerbylufic.tresh.TrashDbHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.select.SelectExtension;
import com.mikepenz.iconics.IconicsDrawable;
import com.mikepenz.iconics.view.IconicsImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileActivity extends BaseActivity implements OnFileClickListener , OptionMenuClicked {

    private boolean showPaste1 = false;
    private boolean showPaste2 = false;

    private boolean isPasteVisible = false;

    ItemAdapter<HeaderItem> headerItemItemAdapter;
    RecyclerView recyclerView;
    FastAdapter<FileAdeptor> fastAdapter;
    ItemAdapter<FileAdeptor> itemAdapter;

    private List<FileAdeptor> allFilesList = new ArrayList<FileAdeptor>();

    private File selectedFileForCopy3 = null;
    private boolean isCopyMode3 = false;
    private boolean isMoveMode3 = false;
    private boolean isFilteredMode = false;

    private String filterType = null;

    private MenuItem pasteMenuItem3;
    File selectedFileForCopy;
    boolean isGrid = false;
    MenuItem pasteMenuItem;
    LinearLayout bottomBar;
    ScrollView threeDotBottomBar , threeDotBottomBar22;
    View touchInterceptor;
    private boolean showHiddenFiles = false;
    boolean isFromQuickAccess = false;

    ImageView add;
    boolean isCopyTrue = false;

    Toolbar toolbar;
    Context context;
    LinearLayout pathBar;
    private List<File> copiedFiles = new ArrayList<>();

    private List<File> currentFiles = new ArrayList<>();

    DrawerLayout drawerLayout;
    ImageButton imageButton2;
    private File selectedFile = null;

    boolean isDragging = false;

    SelectExtension<FileAdeptor> selectExtension;
    boolean selectMode = false;
    boolean  isHiddenFileSelected = false;
    boolean selectForHide = false;

    boolean isAllSelected = false;
    boolean ifFav = false;

    static final int REQUEST_STORAGE_PERMISSION = 101;

    OnFileClickListener onFileClickListener;
    OptionMenuClicked optionMenuClicked;
    private final Stack<File> folderStack = new Stack<>();
    private File currentFolder;
    private boolean ignoreNextClick = false;

    MenuItem selectAllMenuItem;
    MenuItem deleteMenuItem;
   MenuItem copytwoMenuItem;
   boolean hide = false;

    MenuItem pasteMenuItemTwo;

    MenuItem MovedMenuItem;
   // private boolean isCopyMode = true;
   private boolean isCopyMode = false;
    private boolean isCopyMode2 = true;


    private List<File> copiedFilesTwo = new ArrayList<>();
    private File sourceFile;
    ArrayList<File> filesList;
    boolean isHidenTwo;
    private ItemTouchHelper touchHelper;

    private static final int PAGE_SIZE = 50;
    private int currentPage = 0;
    ProgressBar progressBar;
    private Map<String, List<File>> folderCache = new HashMap<>();
    private List<File> allFiles = new ArrayList<>();

    private List<File> filesToMove = new ArrayList<>();
    private boolean isMoveMode = false;
    private boolean isMoveMode2 = false;
    boolean isSelected = false;
    SwipeRefreshLayout swipeRefreshLayout;
    SharedPreferences sharedPreferences;
    private File selectedFileForBottomBar;
  // private List<File> selectedFileForBottomBar = new ArrayList<>();


    static final String PREF_NAME = "favorites_pref";
     static final String KEY_FAVORITES = "favorites_list";

boolean isFileFavorite = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_file);

        onFileClickListener = this;
        optionMenuClicked = this;
       // sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        sharedPreferences = getSharedPreferences("View", MODE_PRIVATE);
        showHiddenFiles = sharedPreferences.getBoolean("show_hidden", false);


        progressBar = findViewById(R.id.progressBar);
        context = this;
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My File");
        pathBar = findViewById(R.id.pathContainer);


        bottomBar = findViewById(R.id.bottom_action_bar);
        threeDotBottomBar = findViewById(R.id.threeDotBottoBar);
        threeDotBottomBar22 = findViewById(R.id.threeDotBottoBar2);
        recyclerView = findViewById(R.id.Filerecycle);
        //  imageButton = findViewById(R.id.menuLine);
        add = findViewById(R.id.add);
        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);
        // recyclerView.setAdapter(fastAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(fastAdapter);


        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertFileDilog();
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        context = getApplicationContext();
        if (AllPermissionOfFileManager.hasFullStoragePermission(this)) {
            loadInitialFiles();
        } else {
            AllPermissionOfFileManager.requestAllFilesAccessPermission(this);
        }


        //================Selection=========================
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

            File file = item.getFile();
            if (file.getName().startsWith(".")) {
                isHiddenFileSelected = true;
            } else {
                isHiddenFileSelected = false;
            }
            if (isFileFavorite(file)) {
                new AlertDialog.Builder(FileActivity.this)
                        .setTitle("Remove Favorite")
                        .setMessage("Remove " + file.getName() + " from favorites?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            removeFromFavorites(file);
                            showFavorites();
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }

            Toast.makeText(this, "CoustomLongClick" + position, Toast.LENGTH_SHORT).show();
            if (selectExtension != null) {
                Set<FileAdeptor> selectedItem = selectExtension.getSelectedItems();
                int count = selectedItem.size();
                fastAdapter.notifyItemChanged(position);
                int count2 = selectExtension.getSelectedItems().size();
                getSupportActionBar().setTitle(count2 + " selected");

                threeDotBottomBar.setVisibility(View.GONE);

                Toast.makeText(FileActivity.this, "select item : " + count, Toast.LENGTH_SHORT).show();
            }
            return true;
        });

        fastAdapter.withOnClickListener((v, adapter, item, position) -> {

            if (ignoreNextClick) {
                ignoreNextClick = false;
                return true;
            }

            if (threeDotBottomBar.getVisibility() == View.VISIBLE) {
                threeDotBottomBar.setVisibility(View.GONE);
                touchInterceptor.setVisibility(View.GONE);
                return true;
            }
            if (threeDotBottomBar22.getVisibility() == View.VISIBLE) {
                threeDotBottomBar22.setVisibility(View.GONE);
                touchInterceptor.setVisibility(View.GONE);
                return true;
            }



            if (selectMode) {
                fastAdapter.toggleSelection(position);
                return true;
            } else {
                sourceFile = item.getFile();
                File clickedFile = item.getFile();
                if (clickedFile.isDirectory()) {
                    currentFolder = clickedFile;
                    loadFiles(clickedFile);
                } else {
                    onClick(clickedFile);
                }
                return true;
            }
        });


        fastAdapter.withSelectionListener((item, selected) -> {
            selectForHide = !fastAdapter.getSelectedItems().isEmpty();
            updateToolbarIconsVisibility();
            if (selectExtension != null) {
                int count = selectExtension.getSelectedItems().size();

                if (count == 0) {
                    selectMode = false;
                    getSupportActionBar().setTitle("Select Items");
                } else {
                    selectMode = true;
                    getSupportActionBar().setTitle(count + " selected");
                    threeDotBottomBar.setVisibility(View.GONE);
                }
                for (FileAdeptor fileItem : itemAdapter.getAdapterItems()) {
                    fileItem.countSize = count;
                }
                boolean hiddenFound = false;
                for (FileAdeptor selectedItem : selectExtension.getSelectedItems()) {
                    File file = selectedItem.getFile();
                    if (file.getName().startsWith(".")) {
                        hiddenFound = true;
                        break;
                    }
                }
                isHiddenFileSelected = hiddenFound;

                fastAdapter.notifyAdapterDataSetChanged();
            }
        });


        ToolbarTitleSubTitleSize();
        restoreViewMode();
        swipeToRefresh();
        openFav();
        sortCut();
        thremeLightDark();



        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nv);
        Toolbar toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        setupDrawerMenu(navigationView, drawerLayout);

        threeDotBottomBar.setVisibility(View.GONE);
        threeDotBottomBar22.setVisibility(View.GONE);

        navigationView.setItemIconTintList(null);

    }

    void alertFileDilog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_create_folder, null);

        EditText input = dialogView.findViewById(R.id.folder_name_input);

        builder.setView(dialogView);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String fileName = input.getText().toString().trim();
            if (!fileName.isEmpty()) {
                createFolder(fileName);
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.show();
    }



    void createFolder(String folderName) {
        File folder = new File(currentFolder, folderName);


        if (!folder.exists()) {
            boolean success = folder.mkdirs();
            itemAdapter.add(new FileAdeptor(folder, null, false, null, this));

            if (success) {
                Toast.makeText(context, "Folder is created", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Folder not created", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context, "Folder already exists", Toast.LENGTH_SHORT).show();
        }
        loadFiles(currentFolder);

    }

    private void createNewFileWithName(String name) {
        try {
            File rootDirectory = Environment.getExternalStorageDirectory(); // /storage/emulated/0/
            File directory = new File(rootDirectory, "MyFiles");

            if (!directory.exists()) {
                directory.mkdirs();
            }

            if (!name.endsWith(".txt")) {
                name = name + ".txt";
            }

            File newFile = new File(directory, name);

            if (newFile.exists()) {
                Toast.makeText(this, "File already exists!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (newFile.createNewFile()) {
                FileWriter writer = new FileWriter(newFile);
                writer.append("New File Created.");
                writer.flush();
                writer.close();


                itemAdapter.add(new FileAdeptor(newFile, null, false, null, this));

                Toast.makeText(this, "File created at: " + newFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed to create file", Toast.LENGTH_SHORT).show();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    private void loadInitialFiles() {
        if (getIntent().hasExtra("root_path")) {
            bySDcard();
            return;
        }


        if (getIntent().hasExtra("shortcut_folder_path")) {
            return;
        }

        if (isFilteredMode) return;

        if (getIntent().hasExtra("clicked_path")) {
            return;
        }

        String path = getIntent().getStringExtra("folder_path");
        if (path == null) {
            path = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        loadFiles(new File(path));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                loadInitialFiles();
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        sharedPreferences = getSharedPreferences("View", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        int id = item.getItemId();

        if (id == R.id.menu_select_all || id==R.id.action_select_all) {
            if (!isSelected) {
                selectExtension.select();
                threeDotBottomBar.setVisibility(View.GONE);
                isSelected = true;
                item.setTitle("Deselect All");
            } else {
                selectExtension.deselect();
                isSelected = false;
                item.setTitle("Select All");
                getSupportActionBar().setTitle("My File");
                invalidateOptionsMenu();
            }

        } else if (id == R.id.menu_sort_by) {
            showSortBottomSheetWithRadio();
            return true;

        } else if (id == R.id.menu_add_folder) {
            alertFileDilog();
            return true;
        } else if (id == R.id.action_home) {
            Intent intent = new Intent(FileActivity.this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.view_grid) {
            item.setChecked(true);
            setRecyclerViewLayout(true);
            editor.putString("view_mode", "grid");
            editor.apply();
            return true;
        } else if (id == R.id.view_list) {
            item.setChecked(true);
            setRecyclerViewLayout(false);
            editor.putString("view_mode", "list");
            editor.apply();
            return true;

        }else if (id == R.id.HideUnhide) {
            boolean isChecked = !item.isChecked();
            item.setChecked(isChecked);
            sharedPreferences.edit().putBoolean("show_hidden",isChecked).apply();
            showHiddenFiles = isChecked;
            loadFiles(currentFolder);

            return true;
        }
        else if (item.getItemId() == R.id.action_paste) {

            //===============================new============================
            if (selectedFileForBottomBar != null) {

                File destination = new File(currentFolder, selectedFileForBottomBar.getName());

                if (destination.exists()) {
                    runOnUiThread(() -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(
                                FileActivity.this, R.style.MyAlertDialogStyle);

                        builder.setTitle("File/Folder already exists in same path")
                                .setMessage("Some file/folder with the same name already exists in this path.\n\nWhat would you like to do?")
                                .setPositiveButton("Rename", (dialogInterface, which) -> {
//                                    String newName = System.currentTimeMillis() + "_" + selectedFileForBottomBar.getName();
//                                    File renamedDest = new File(currentFolder, newName);
//                                    performPaste(selectedFileForBottomBar, renamedDest);

                                        showRenameDialog(selectedFileForBottomBar, currentFolder);


                                })
                                .setNeutralButton("Replace the file in destination", (dialogInterface, which) -> {
                                    //String newName = "Copy_" + selectedFileForBottomBar.getName();
                                    String newName = selectedFileForBottomBar.getName();
                                    File keepBothDest = new File(currentFolder, newName);
                                    performPaste(selectedFileForBottomBar, keepBothDest);
                                })
                                .setNegativeButton("Skip", (dialogInterface, which) -> {
                                    Toast.makeText(FileActivity.this, "Skipped", Toast.LENGTH_SHORT).show();
                                });

                        AlertDialog dialog = builder.create();
                        dialog.show();

                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#4CAF50")); // Green
                        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#2196F3"));  // Blue
                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#F44336")); // Red

                    });

                } else {
                    performPaste(selectedFileForBottomBar, destination);
                }

            } else {
                Toast.makeText(this, "Nothing to paste", Toast.LENGTH_SHORT).show();
            }



            return true;

        }else if(item.getItemId()==R.id.ShowFev){
            showFavorites();
            ifFav = true;

        }else if (id==R.id.id4) {
            showFavorites();
            ifFav = true;
        } else if (id==R.id.action_copy2) {
            copiedFilesTwo.clear();
            List<FileAdeptor> selectedItems = new ArrayList<>(fastAdapter.getSelectedItems());

            for (FileAdeptor item2 : selectedItems) {
                copiedFilesTwo.add(item2.getFile());
                isCopyTrue = true;
            }


            if (!copiedFilesTwo.isEmpty() && pasteMenuItemTwo != null) {
                pasteMenuItemTwo.setVisible(true);
            }

            if (selectAllMenuItem != null) {
                selectAllMenuItem.setVisible(false);
            }
            if (deleteMenuItem != null) {
                deleteMenuItem.setVisible(false);
            }
            if (copytwoMenuItem != null) {
                copytwoMenuItem.setVisible(false);
            }

            updateToolbarIconsVisibility();
            selectExtension.deselect();

            Toast.makeText(this, "Copied " + copiedFilesTwo.size() + " item(s)", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id==R.id.action_pasteTwo) {
          /*  progressBar.setVisibility(View.VISIBLE);

            new Thread(() -> {
                File destination = currentFolder;

                for (File file : copiedFilesTwo) {
                    File newFile = new File(destination, file.getName());
                    try {
                        copyFile(file, newFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                runOnUiThread(() -> {

                    progressBar.setVisibility(View.GONE);

                    Toast.makeText(this, "Pasted " + copiedFilesTwo.size() + " item(s)", Toast.LENGTH_SHORT).show();
                    pasteMenuItemTwo.setVisible(false);

                    isCopyTrue = false;
                    invalidateOptionsMenu();

                    loadFiles(currentFolder);
                });

            }).start();*/
            //============================New paste===================================
//            progressBar.setVisibility(View.VISIBLE);
//
//            new Thread(() -> {
//                File destination = currentFolder;
//
//                for (File file : copiedFilesTwo) {
//                    File newFile = new File(destination, file.getName());
//
//                    if (newFile.exists()) {
//                        runOnUiThread(() -> {
//                            new AlertDialog.Builder(this)
//                                    .setTitle("File already exists")
//                                    .setMessage("A file with the same name already exists in the destination.\n\n" + file.getName())
//                                    .setPositiveButton("Rename", (dialog, which) -> {
//                                        String renamedName = "Copy_" + System.currentTimeMillis() + "_" + file.getName();
//                                        File renamedFile = new File(destination, renamedName);
//                                        try {
//                                            copyFile(file, renamedFile);
//                                            showPasteSuccess();
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//                                    })
//                                    .setNegativeButton("Skip", (dialog, which) -> {
//                                        // skip this file
//                                    })
//                                    .setNeutralButton("Keep Both", (dialog, which) -> {
//                                        progressBar.setVisibility(View.VISIBLE);
//                                        File keepBothFile = getUniqueFile(destination, file.getName());
//                                        try {
//                                            copyFile(file, keepBothFile);
//                                            showPasteSuccess();
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//                                    })
//                                    .setCancelable(false)
//                                    .show();
//                        });
//
//                    } else {
//                        try {
//                            copyFile(file, newFile);
//                            showPasteSuccess();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                runOnUiThread(() -> {
//                    progressBar.setVisibility(View.GONE);
//                    Toast.makeText(this, "Pasted " + copiedFilesTwo.size() + " item(s)", Toast.LENGTH_SHORT).show();
//                    pasteMenuItemTwo.setVisible(false);
//                    isCopyTrue = false;
//                    invalidateOptionsMenu();
//                    loadFiles(currentFolder);
//                });
//
//            }).start();

            //=============================================Now it is working========================================================
//            progressBar.setVisibility(View.VISIBLE);
//
//            new Thread(() -> {
//                File destination = currentFolder;
//
//                for (int i = 0; i < copiedFilesTwo.size(); i++) {
//                    File file = copiedFilesTwo.get(i);
//                    File newFile = new File(destination, file.getName());
//
//                    if (newFile.exists()) {
//                        int finalI = i; // for use in inner class
//                        runOnUiThread(() -> {
//                            new AlertDialog.Builder(this)
//                                    .setTitle("File/Folder already exists in same path")
//                                    .setMessage("A file/folder with the same name already exists:\n\n" + file.getName())
//                                    .setPositiveButton("Rename", (dialog, which) -> {
//                                        String renamedName = "Copy_" + System.currentTimeMillis() + "_" + file.getName();
//                                        File renamedFile = new File(destination, renamedName);
//                                        try {
//                                            copyFile(file, renamedFile);
//                                            if (finalI == copiedFilesTwo.size() - 1) {
//                                                finishPasteOperation();
//                                            }
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//                                    })
//                                    .setNegativeButton("Skip", (dialog, which) -> {
//                                        if (finalI == copiedFilesTwo.size() - 1) {
//                                            finishPasteOperation();
//                                        }
//                                    })
//                                    .setNeutralButton("Keep Both", (dialog, which) -> {
//                                        dialog.dismiss(); //dialog turant dismiss ho
//
//                                        progressBar.setVisibility(View.VISIBLE);
//                                        File keepBothFile = getUniqueFile(destination, file.getName());
//                                        try {
//                                            copyFile(file, keepBothFile);
//                                            if (finalI == copiedFilesTwo.size() - 1) {
//                                                finishPasteOperation();
//                                            }
//                                        } catch (IOException e) {
//                                            e.printStackTrace();
//                                        }
//                                    })
//                                    .setCancelable(false)
//                                    .show();
//                        });
//
//                        return; // wait until user selects an option before continuing
//                    } else {
//                        try {
//                            copyFile(file, newFile);
//                            if (i == copiedFilesTwo.size() - 1) {
//                                runOnUiThread(this::finishPasteOperation);
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//
//                // If no conflict occurred
//                runOnUiThread(this::finishPasteOperation);
//
//            }).start();

            progressBar.setVisibility(View.VISIBLE);

            new Thread(() -> {
                File destination = currentFolder;
                processNextFile(0, destination);
            }).start();


            return true;


        } else if (id==R.id.menu_Hide) {
            hideItem();
        } else if (id==R.id.menu_Unhide) {
            unhideSelectedFiles();
        } else if (id==R.id.action_delete) {
            deleteSelectedFilesWithConfirmation();
        } else if (id==R.id.Close) {
            Intent intent = new Intent(FileActivity.this , MainActivity.class);
            startActivity(intent);
        } else if (id==R.id.removeToFav) {
            List<FileAdeptor> selectedItems = new ArrayList<>(fastAdapter.getSelectedItems());
            if (!selectedItems.isEmpty()) {
                for (FileAdeptor selectedItem : selectedItems) {
                    File file = selectedItem.getFile();
                    removeFromFavorites(file);
                }
                ifFav= false;
                invalidateOptionsMenu();
                Toast.makeText(context, "Remove", Toast.LENGTH_SHORT).show();
                }

        } else if (id==R.id.DandD) {
            isDragging = !item.isChecked();
            item.setChecked(isDragging);

            if (isDragging) {
                dregAndDrope();
            } else {
                if (touchHelper != null) {
                    touchHelper.attachToRecyclerView(null);
                }
            }
            Toast.makeText(this, isDragging ? "Drag ON" : "Drag OFF", Toast.LENGTH_SHORT).show();
            fastAdapter.notifyAdapterDataSetChanged();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    private void restoreViewMode() {
        sharedPreferences = getSharedPreferences("View", MODE_PRIVATE);
        String viewMode = sharedPreferences.getString("view_mode", "list");

        if (viewMode.equals("grid")) {
            setRecyclerViewLayout(true);
        } else {
            setRecyclerViewLayout(false);
        }
    }


    private void setRecyclerViewLayout(boolean isGrid) {
        if (isGrid) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        recyclerView.setAdapter(fastAdapter);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);

            SpannableString spanString = new SpannableString(item.getTitle());
            spanString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanString.length(), 0);
            item.setTitle(spanString);
        }
        //---------------Day/Light-------end-------

        MenuItem selectAllItem = menu.findItem(R.id.menu_select_all);
        if (isSelected) {
            selectAllItem.setTitle("Deselect All");

        } else {
            selectAllItem.setTitle("Select All");
        }

        String viewMode = sharedPreferences.getString("view_mode", "list");

        if (viewMode.equals("grid")) {
            menu.findItem(R.id.view_grid).setChecked(true);
            menu.findItem(R.id.view_list).setChecked(false);
        } else {
            menu.findItem(R.id.view_list).setChecked(true);
            menu.findItem(R.id.view_grid).setChecked(false);
        }

        pasteMenuItem = menu.findItem(R.id.action_paste);
        if (pasteMenuItem != null) {
            pasteMenuItem.setVisible(isCopyMode);
            if(!isCopyMode) {
                pasteMenuItem.setVisible(isMoveMode);
            }
        }
        pasteMenuItemTwo = menu.findItem(R.id.action_pasteTwo);
        if (pasteMenuItemTwo != null) {
            if(!isCopyMode) {
                pasteMenuItemTwo.setVisible(isCopyTrue);
            }
                //pasteMenuItemTwo.setVisible(isMoveMode);
        }



        showHiddenFiles = sharedPreferences.getBoolean("show_hidden", false);
        MenuItem hideUnhideItem = menu.findItem(R.id.HideUnhide);
        if (hideUnhideItem != null) {
            hideUnhideItem.setChecked(showHiddenFiles);
        }

        boolean isAnyItemSelected = !fastAdapter.getSelectedItems().isEmpty();
        menu.findItem(R.id.action_select_all).setVisible(isAnyItemSelected);
        menu.findItem(R.id.action_delete).setVisible(isAnyItemSelected);
//        menu.findItem(R.id.action_copy2).setVisible(isAnyItemSelected);
//        menu.findItem(R.id.action_pasteTwo).setVisible(isCopyTrue);
       // menu.findItem(R.id.action_paste).setVisible(isCopyTrue);




        return super.onPrepareOptionsMenu(menu);
    }


    private String loadSortPreference() {
        SharedPreferences prefs = getSharedPreferences("sort_prefs", MODE_PRIVATE);
        return prefs.getString("sort_option", "");
    }

    private void saveSortPreference(String sortType) {
        SharedPreferences preferences = getSharedPreferences("sort_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("sort_option", sortType);
        editor.apply();
    }
    private void applySort(String sortType) {
        List<File> sortedList = new ArrayList<>();
        for (File f : currentFiles) {
            if (f != null) {
                sortedList.add(f);
            }
        }

        Collections.sort(sortedList, (f1, f2) -> {
            if (f1.isDirectory() && !f2.isDirectory()) {
                return -1;
            } else if (!f1.isDirectory() && f2.isDirectory()) {
                return 1;
            } else {
                switch (sortType) {
                    case "name":
                    case "az":
                        return f1.getName().compareToIgnoreCase(f2.getName());
                    case "za":
                        return f2.getName().compareToIgnoreCase(f1.getName());
                    case "NewestDate":
                        return Long.compare(f2.lastModified(), f1.lastModified());
                    case "OldestDate":
                        return Long.compare(f1.lastModified(), f2.lastModified());
                    case "LargerSize":
                        return Long.compare(f2.length(), f1.length());
                    case "SmallerSize":
                        return Long.compare(f1.length(), f2.length());
                    default:
                        return f1.getName().compareToIgnoreCase(f2.getName());
                }
            }
        });

        currentFiles.clear();
        currentFiles.addAll(sortedList);
    }


    private void showSortBottomSheetWithRadio() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.sort_bottom_sheet, null);
        bottomSheetDialog.setContentView(view);

        RadioGroup radioGroup = view.findViewById(R.id.radioGroupSort);

        String selectedSort = loadSortPreference();
        switch (selectedSort) {
            case "name":
                radioGroup.check(R.id.radioName);
                break;
            case "NewestDate":
                radioGroup.check(R.id.radioDate);
                break;
            case "OldestDate":
                radioGroup.check(R.id.OldestradioDate);
                break;
            case "LargerSize":
                radioGroup.check(R.id.radioSize);
                break;
            case "SmallerSize":
                radioGroup.check(R.id.radioSmallestSize);
                break;
            case "az":
                radioGroup.check(R.id.radioAZ);
                break;
            case "za":
                radioGroup.check(R.id.radioZA);
                break;
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioName) {
                saveSortPreference("name");
                Toast.makeText(this, "Sorted by Name", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.radioDate) {
                saveSortPreference("NewestDate");
                Toast.makeText(this, "Sorted by Newest Date", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.OldestradioDate) {
                saveSortPreference("OldestDate");
                Toast.makeText(this, "Sorted by Oldest Date", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.radioSize) {
                saveSortPreference("LargerSize");
                Toast.makeText(this, "Sorted by Larger Size", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.radioSmallestSize) {
                saveSortPreference("SmallerSize");
                Toast.makeText(this, "Sorted by Smaller Size", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.radioAZ) {
                saveSortPreference("az");
                Toast.makeText(this, "Sorted A - Z", Toast.LENGTH_SHORT).show();
            } else if (checkedId == R.id.radioZA) {
                saveSortPreference("za");
                Toast.makeText(this, "Sorted Z - A", Toast.LENGTH_SHORT).show();
            }

            bottomSheetDialog.dismiss();
            applySort(loadSortPreference());
            // recreate();
            loadFiles(currentFolder);

        });

        bottomSheetDialog.show();
    }



    private void loadFiles(File folder) {
        currentFolder = folder;

        runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            itemAdapter.clear();
            updatePathBar(folder);
            updateToolbarCount(folder);
        });

        new Thread(() -> {
            File[] files = folder.listFiles();
            List<FileAdeptor> items = new ArrayList<>();
            boolean firstFolderShown = false;
            boolean firstFileShown = false;

            if (files != null) {
                List<File> visibleFiles = new ArrayList<>();

                // Filter hidden files
                for (File file : files) {
                    if (file != null && (showHiddenFiles || !file.getName().startsWith("."))) {
                        visibleFiles.add(file);
                    }
                }

                // Split into folders & files locally
                List<File> folders = new ArrayList<>();
                List<File> onlyFiles = new ArrayList<>();
                for (File file : visibleFiles) {
                    if (file.isDirectory()) {
                        folders.add(file);
                    } else {
                        onlyFiles.add(file);
                    }
                }

                // Merge folders and files into one local list
                List<File> localSortedFiles = new ArrayList<>();
                localSortedFiles.addAll(folders);
                localSortedFiles.addAll(onlyFiles);

                runOnUiThread(() -> {
                    // update currentFiles safely on UI thread
                    currentFiles.clear();
                    currentFiles.addAll(localSortedFiles);

                    // applySort on UI thread (since it modifies currentFiles)
                    String sortType = loadSortPreference();
                    applySort(sortType);

                    // Now create FileAdeptor items from sorted currentFiles
                    items.clear();
                    boolean localFirstFolderShown = false;
                    boolean localFirstFileShown = false;

                    for (File file : currentFiles) {
                        boolean showHeader = false;
                        String headerTxt = "";

                        if (file.isDirectory() && !localFirstFolderShown) {
                            showHeader = true;
                            headerTxt = "DIRECTORIES";
                            localFirstFolderShown = true;
                        } else if (!file.isDirectory() && !localFirstFileShown) {
                            showHeader = true;
                            headerTxt = "FILES";
                            localFirstFileShown = true;
                        }

                        items.add(new FileAdeptor(file, onFileClickListener, showHeader, headerTxt, this));
                    }

                    allFilesList.clear();
                    allFilesList.addAll(items);
                    itemAdapter.set(items);
                    progressBar.setVisibility(View.GONE);
                    updateToolbarCount(folder);
                });

            } else {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "No files found", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
















    private void updatePathBar(File currentFolder) {
        pathBar.removeAllViews();

        String fullPath = currentFolder.getAbsolutePath();
        String basePath = "/storage/emulated/0";

        StringBuilder cumulativePath = new StringBuilder();

        if (fullPath.startsWith(basePath)) {
            cumulativePath.append(basePath);

            TextView internalStorage = new TextView(this);
            internalStorage.setText("Internal Storage");
            internalStorage.setPadding(16, 0, 16, 0);
            internalStorage.setTextColor(getResources().getColor(R.color.white));
            internalStorage.setBackground(getResources().getDrawable(R.drawable.path_chip_background));

            Drawable internalIcon = new IconicsDrawable(this)
                    .icon(CommunityMaterial.Icon2.cmd_home)
                    .color(Color.WHITE)
                    .sizeDp(18);
            internalStorage.setCompoundDrawablesWithIntrinsicBounds(internalIcon, null, null, null);
            internalStorage.setCompoundDrawablePadding(8);

            internalStorage.setOnClickListener(v -> {
                File newFile = new File(basePath);
                if (newFile.exists() && newFile.isDirectory()) {
                    loadFiles(newFile);
                }
            });

            pathBar.addView(internalStorage);


            fullPath = fullPath.substring(basePath.length());
        }


        String[] parts = fullPath.split("/");

        for (String part : parts) {
            if (part.isEmpty()) continue;

            cumulativePath.append("/").append(part);
            final String path = cumulativePath.toString();

            TextView textView = new TextView(this);
            textView.setText(part);
            textView.setPadding(16, 0, 16, 0);
            textView.setTextColor(getResources().getColor(R.color.white));
            textView.setBackground(getResources().getDrawable(R.drawable.path_chip_background));

            Drawable icon = new IconicsDrawable(this)
                    .icon(CommunityMaterial.Icon.cmd_chevron_right)
                    .color(Color.WHITE)
                    .sizeDp(18);
            textView.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
            textView.setCompoundDrawablePadding(8);

            textView.setOnClickListener(v -> {
                File newFile = new File(path);
                if (newFile.exists() && newFile.isDirectory()) {
                    loadFiles(newFile);
                }
            });

            pathBar.addView(textView);
        }

    }

    private void updateToolbarCount(File currentFolder) {
        int fileCount = 0;
        int folderCount = 0;

        File[] files = currentFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    folderCount++;
                } else {
                    fileCount++;
                }
            }
        }

        String info = folderCount + " folders, " + fileCount + " files";
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setSubtitle(info);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_toolbar, menu);
        MenuItem selectAll = menu.findItem(R.id.menu_select_all);
        MenuItem sortby = menu.findItem(R.id.menu_sort_by);
        MenuItem ListViews = menu.findItem(R.id.view_list);
        MenuItem GrideViews = menu.findItem(R.id.view_grid);
        MenuItem CreateFolder = menu.findItem(R.id.menu_add_folder);
        MenuItem HideUnhide = menu.findItem(R.id.HideUnhide);
        MenuItem View = menu.findItem(R.id.action_view);
        pasteMenuItem3 = menu.findItem(R.id.action_paste);
//        pasteMenuItem3.setVisible(false);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        MenuItem home = menu.findItem(R.id.action_home);
        MenuItem fev = menu.findItem(R.id.ShowFev);
        selectAllMenuItem = menu.findItem(R.id.action_select_all);
        deleteMenuItem = menu.findItem(R.id.action_delete);//action_copy2"
        copytwoMenuItem = menu.findItem(R.id.action_copy2);
        pasteMenuItemTwo = menu.findItem(R.id.action_pasteTwo);

       // MovedMenuItem = menu.findItem(R.id.action_Moved);

        MenuItem Hide2 = menu.findItem(R.id.menu_Hide);
        MenuItem ShowHide = menu.findItem(R.id.menu_Unhide);
        MenuItem AdvanceSetting = menu.findItem(R.id.menu_AdvanceSetting);
        MenuItem close = menu.findItem(R.id.Close);
        MenuItem IsFav = menu.findItem(R.id.removeToFav);
        MenuItem DnD = menu.findItem(R.id.DandD);

//        IconicsDrawable m = new IconicsDrawable(this)
//                .icon(CommunityMaterial.Icon.cmd_content_paste)
//                .sizeDp(24)
//                .color(Color.WHITE);
//        MovedMenuItem.setIcon(m);

        IconicsDrawable dnd = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon2.cmd_select_drag)
                .sizeDp(24)
                .color(Color.WHITE);
                 DnD.setIcon(dnd);
            IsFav.setVisible(ifFav);
            IconicsDrawable cols2 = new IconicsDrawable(this)
                    .icon(CommunityMaterial.Icon.cmd_folder_remove)
                    .sizeDp(24)
                    .color(Color.WHITE);
            IsFav.setIcon(cols2);
        IconicsDrawable cols = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_cancel)
                .sizeDp(24)
                .color(Color.WHITE);
        close.setIcon(cols);

        IconicsDrawable Adv = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon2.cmd_settings)
                .sizeDp(24)
                .color(Color.WHITE);
        AdvanceSetting.setIcon(Adv);

        if (selectForHide&&!isHiddenFileSelected) {
            Hide2.setVisible(true);
            IconicsDrawable hideIcon = new IconicsDrawable(this)
                    .icon(CommunityMaterial.Icon.cmd_eye_off)
                    .sizeDp(24)
                    .color(Color.WHITE);
            Hide2.setIcon(hideIcon);
        } else {
            Hide2.setVisible(false);

        }
        if( isHiddenFileSelected){
            ShowHide.setVisible(true);
            IconicsDrawable hideIcon2 = new IconicsDrawable(this)
                    .icon(CommunityMaterial.Icon.cmd_eye)
                    .sizeDp(24)
                    .color(Color.WHITE);
            ShowHide.setIcon(hideIcon2);
        }else{
            ShowHide.setVisible(false);

        }


        selectAllMenuItem.setVisible(false);
        deleteMenuItem.setVisible(false);
        copytwoMenuItem.setVisible(false);
        pasteMenuItemTwo.setVisible(false);

//        if(isCopyTrue){
//            pasteMenuItem3.setVisible(true);
//        }else{
//            pasteMenuItem3.setVisible(false);
//        }

        IconicsDrawable iconPasteTwo = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_content_paste)
                .sizeDp(24)
                .color(Color.WHITE);
        pasteMenuItemTwo.setIcon(iconPasteTwo);

        IconicsDrawable iconCopyTwo = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_content_copy)
                .sizeDp(24)
                .color(Color.WHITE);
        copytwoMenuItem.setIcon(iconCopyTwo);

        IconicsDrawable iconSelectAll = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_check_all)
                .sizeDp(24)
                .color(Color.WHITE);
        selectAllMenuItem.setIcon(iconSelectAll);

        IconicsDrawable deleteMenuItemm = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_delete)
                .sizeDp(24)
                .color(Color.WHITE);
        deleteMenuItem.setIcon(deleteMenuItemm);

        IconicsDrawable iconPaste = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_content_paste)
                .sizeDp(24)
                .color(Color.WHITE);
        pasteMenuItem3.setIcon(iconPaste);

        IconicsDrawable fevitme = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon2.cmd_heart)
                .sizeDp(24)
                .color(Color.WHITE);
        fev.setIcon(fevitme);

        IconicsDrawable icon = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon2.cmd_home)
                .sizeDp(24)
                .color(Color.WHITE);
        home.setIcon(icon);
        IconicsDrawable selectall = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_check_all)
                .sizeDp(24)
                .color(Color.WHITE);
        selectAll.setIcon(selectall);
        IconicsDrawable sort = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon2.cmd_sort)
                .sizeDp(24)
                .color(Color.WHITE);
        sortby.setIcon(sort);
        IconicsDrawable list = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon2.cmd_view_list)
                .sizeDp(24)
                .color(Color.WHITE);
        ListViews.setIcon(list);
        IconicsDrawable gride = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon2.cmd_view_grid)
                .sizeDp(24)
                .color(Color.WHITE);
        GrideViews.setIcon(gride);
        IconicsDrawable addFolder = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_folder_image)
                .sizeDp(24)
                .color(Color.WHITE);
        CreateFolder.setIcon(addFolder);
        IconicsDrawable Hide = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_eye)
                .sizeDp(24)
                .color(Color.WHITE);
        HideUnhide.setIcon(Hide);
        IconicsDrawable view = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon.cmd_grid)
                .sizeDp(24)
                .color(Color.WHITE);
        View.setIcon(view);

        IconicsDrawable icon2 = new IconicsDrawable(this)
                .icon(CommunityMaterial.Icon2.cmd_magnify)
                .sizeDp(24)
                .color(Color.WHITE);
        searchItem.setIcon(icon2);

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(item.getTitle());
            spanString.setSpan(new ForegroundColorSpan(Color.parseColor("#ff4500")), 0, spanString.length(), 0); // white color
            item.setTitle(spanString);
        }

        searchView.setQueryHint("Search files...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                performSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                performSearch(newText);
                return true;
            }
        });


        try {
            java.lang.reflect.Field[] fields = menu.getClass().getDeclaredFields();
            for (java.lang.reflect.Field field : fields) {
                if ("mOptionalIconsVisible".equals(field.getName())) {
                    field.setAccessible(true);
                    field.setBoolean(menu, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean shouldExpand = getIntent().getBooleanExtra("openSearch", false);
        if (shouldExpand) {
            searchItem.expandActionView();
            searchView.requestFocus();
        }
        return true;
    }

    private void performSearch(String query) {
        query = query.toLowerCase(Locale.ROOT).trim();
        List<FileAdeptor> filtered = new ArrayList<>();

        for (File file : currentFiles) {
            String name = file.getName().toLowerCase(Locale.ROOT);
            if (name.contains(query)) {
                filtered.add(new FileAdeptor(file));
            }
        }
        itemAdapter.set(filtered);
    }



    @Override
    protected void onResume() {
        super.onResume();

        if (currentFolder == null || !currentFolder.exists()) {
            loadFiles(Environment.getExternalStorageDirectory());
        }


    }

    private void createMultipleFiles(String baseName, int count) {
        try {
            File rootDirectory = Environment.getExternalStorageDirectory();
            File directory = new File(rootDirectory, "MyFiles");

            if (!directory.exists()) {
                directory.mkdirs();
            }

            for (int i = 1; i <= count; i++) {
                String fileName = baseName + "_" + i + ".txt";
                File newFile = new File(directory, fileName);

                if (!newFile.exists()) {
                    if (newFile.createNewFile()) {
                        FileWriter writer = new FileWriter(newFile);
                        writer.append("This is file number " + i);
                        writer.flush();
                        writer.close();

                    } else {
                        Log.e("FileCreation", "Failed to create file: " + fileName);
                    }
                } else {
                    Log.w("FileCreation", "File already exists: " + fileName);
                }
            }

            Toast.makeText(this, count + " files created in " + directory.getAbsolutePath(), Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(File file) {
        selectedFile = file;
        if (file.isDirectory()) {
            if (currentFolder != null) {
                folderStack.push(currentFolder);
            }
            loadFiles(file);
        } else {
            openFile(file);
        }
    }

    private void openFile(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);

        String mime = getContentResolver().getType(uri);
        intent.setDataAndType(uri, mime);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivityForResult(intent, 100);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No app found to open this file", Toast.LENGTH_SHORT).show();
        }
    }


    void bottombar() {
        IconicsImageView btnDelete = findViewById(R.id.btn_delete);
        IconicsImageView btnSelectAll = findViewById(R.id.btn_select_all);
        //  IconicsImageView btnDeselectAll = findViewById(R.id.btn_deselect_all);
        IconicsImageView btnHide = findViewById(R.id.btn_open);
        IconicsImageView btnCopy = findViewById(R.id.btn_copy);
        IconicsImageView btnMove = findViewById(R.id.btn_move);
        TextView selectOrdeselect = findViewById(R.id.selectAlltxt);
        TextView CopyPasteTxt = findViewById(R.id.CopyPaste);
        TextView movetxt = findViewById(R.id.moveTxt);

        bottomBar.setVisibility(View.GONE);

        btnHide.setOnClickListener(v -> {
            Set<FileAdeptor> selectedItems = selectExtension.getSelectedItems();

            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean allSuccess = true;
            for (FileAdeptor item : selectedItems) {
                File originalFile = item.getFile();
                if (originalFile != null && originalFile.exists()) {
                    String parent = originalFile.getParent();
                    String newName = "." + originalFile.getName();
                    File hiddenFile = new File(parent, newName);

                    boolean success = originalFile.renameTo(hiddenFile);
                    if (!success) {
                        allSuccess = false;
                    }
                }
            }

            if (allSuccess) {
                Toast.makeText(this, "All selected files hidden", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Some files could not be hidden", Toast.LENGTH_SHORT).show();
            }

            selectExtension.deselect();
            loadFiles(currentFolder);
            threeDotBottomBar.setVisibility(View.GONE);
            bottomBar.setVisibility(View.GONE);
        });


        btnSelectAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isAllSelected) {
                    for (int i = 0; i < fastAdapter.getItemCount(); i++) {
                        selectExtension.select(i);
                    }
                    selectOrdeselect.setText("Deselect All");
                    btnSelectAll.setImageDrawable(new IconicsDrawable(FileActivity.this)
                            .icon(CommunityMaterial.Icon2.cmd_selection_off)
                            .color(Color.WHITE)
                            .sizeDp(24));
                    isAllSelected = true;
                    updateBottomBarVisibility();
                    threeDotBottomBar.setVisibility(View.GONE);
                } else {
                    // Deselect all
                    fastAdapter.deselect();
                    btnSelectAll.setImageDrawable(new IconicsDrawable(FileActivity.this)
                            .icon(CommunityMaterial.Icon2.cmd_select_all)
                            .color(Color.WHITE)
                            .sizeDp(24));
                    selectOrdeselect.setText("Select All");
                    isAllSelected = false;
                    updateBottomBarVisibility();
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Set<FileAdeptor> selectedItems = selectExtension.getSelectedItems();

                if (selectedItems.isEmpty()) {
                    Toast.makeText(FileActivity.this, "No files selected", Toast.LENGTH_SHORT).show();
                    return;
                }

                new AlertDialog.Builder(FileActivity.this)
                        .setTitle("Delete Files")
                        .setMessage("Are you sure you want to delete selected files?")
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                List<FileAdeptor> toRemove = new ArrayList<>();

                                for (FileAdeptor item : selectedItems) {
                                    File file = item.getFile(); // Ensure FileAdeptor has a 'file' field
                                    if (file.exists() && file.delete()) {
                                        toRemove.add(item);
                                    }
                                }

                                for (FileAdeptor item : toRemove) {
                                    int pos = itemAdapter.getAdapterPosition(item);
                                    if (pos != RecyclerView.NO_POSITION) {
                                        itemAdapter.remove(pos);
                                    }
                                }

                                fastAdapter.notifyAdapterDataSetChanged();
                                selectExtension.deselect();
                                loadFiles(currentFolder);

                                getSupportActionBar().setTitle("My File");
                                bottomBar.setVisibility(View.GONE);
                                threeDotBottomBar.setVisibility(View.GONE);
                                Toast.makeText(FileActivity.this, "Deleted " + toRemove.size() + " files", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();
            }
        });

        btnCopy.setOnClickListener(v -> {
            if (!isCopyMode2) {
                if (copiedFiles.isEmpty()) {
                    Toast.makeText(FileActivity.this, "No files to paste", Toast.LENGTH_SHORT).show();
                    return;
                }
                int pastedCount = 0;
                for (File sourceFile : copiedFiles) {
                    File destFile = new File(currentFolder, sourceFile.getName());

                    int index = 1;
                    while (destFile.exists()) {
                        String newName = sourceFile.getName().replaceFirst("(\\.[^\\.]+)?$", "_" + index + "$1");
                        destFile = new File(currentFolder, newName);
                        index++;
                    }

                    try {
                        copyFile(sourceFile, destFile);
                        pastedCount++;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                Toast.makeText(FileActivity.this, pastedCount + " file(s) pasted", Toast.LENGTH_SHORT).show();

                copiedFiles.clear();
                isCopyMode2 = true;

                btnCopy.setImageDrawable(new IconicsDrawable(FileActivity.this)
                        .icon(CommunityMaterial.Icon.cmd_content_copy)
                        .color(Color.WHITE)
                        .sizeDp(24));
                CopyPasteTxt.setText("Copy");

                bottomBar.setVisibility(View.GONE);
                threeDotBottomBar.setVisibility(View.GONE);
                loadFiles(currentFolder);

            } else {
                Set<FileAdeptor> selectedItems = selectExtension.getSelectedItems();

                if (selectedItems.isEmpty()) {
                    Toast.makeText(FileActivity.this, "No files selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                copiedFiles.clear();
                for (FileAdeptor item : selectedItems) {
                    copiedFiles.add(item.getFile());
                }
                selectExtension.deselect();
                Toast.makeText(FileActivity.this, copiedFiles.size() + " file(s) copied", Toast.LENGTH_SHORT).show();

                isCopyMode2= false;

                btnCopy.setImageDrawable(new IconicsDrawable(FileActivity.this)
                        .icon(CommunityMaterial.Icon.cmd_content_paste)
                        .color(Color.WHITE)
                        .sizeDp(24));
                CopyPasteTxt.setText("Paste");

                bottomBar.setVisibility(View.VISIBLE);
                threeDotBottomBar.setVisibility(View.GONE);

            }
        });


        btnMove.setOnClickListener(v -> {
            if (!isMoveMode) {
                Set<FileAdeptor> selectedItemsSet = fastAdapter.getSelectedItems();

                if (selectedItemsSet.isEmpty()) {
                    Toast.makeText(this, "Please select item(s) to move", Toast.LENGTH_SHORT).show();
                    return;
                }

                filesToMove.clear();
                for (FileAdeptor item : selectedItemsSet) {
                    filesToMove.add(item.getFile());
                }

                selectExtension.deselect();

                isMoveMode = true;

                movetxt.setText("Move to");
                btnMove.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_content_paste)
                        .color(getResources().getColor(R.color.white))
                        .sizeDp(24));
                bottomBar.setVisibility(View.VISIBLE);
                threeDotBottomBar.setVisibility(View.GONE);

                Toast.makeText(this, "Now navigate to destination and press Paste Here", Toast.LENGTH_SHORT).show();

            } else {
                for (File source : filesToMove) {
                    File destination = new File(currentFolder, source.getName());

                    if (destination.exists()) {
                        Toast.makeText(this, "Already exists: " + source.getName(), Toast.LENGTH_SHORT).show();
                        continue;
                    }

                    boolean moved = source.renameTo(destination);

                    if (!moved) {
                        Toast.makeText(this, "Failed: " + source.getName(), Toast.LENGTH_SHORT).show();
                    }
                }
                isMoveMode = false;
                filesToMove.clear();

                movetxt.setText("Move");
                bottomBar.setVisibility(View.GONE);
                threeDotBottomBar.setVisibility(View.GONE);

                loadFiles(currentFolder);

                Toast.makeText(this, "Move completed", Toast.LENGTH_SHORT).show();
            }
        });


        btnDelete.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_delete)
                .color(getResources().getColor(R.color.white))
                .sizeDp(24));

        btnSelectAll.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon2.cmd_select_all)
                .color(getResources().getColor(R.color.white))
                .sizeDp(24));

        btnHide.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_eye_off)
                .color(getResources().getColor(R.color.white))
                .sizeDp(24));

        btnCopy.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_content_copy)
                .color(getResources().getColor(R.color.white))
                .sizeDp(24));

        btnMove.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_folder_move)
                .color(getResources().getColor(R.color.white))
                .sizeDp(24));


    }

    private void updateBottomBarVisibility() {
        if (selectExtension.getSelections().isEmpty()) {
            bottomBar.setVisibility(View.GONE);
            getSupportActionBar().setTitle("My File");
        } else {
            bottomBar.setVisibility(View.VISIBLE);
        }
    }

    private void copyFile(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdirs();
            }

            String[] children = source.list();
            if (children != null) {
                for (String child : children) {
                    copyFile(new File(source, child), new File(destination, child));
                }
            }
        } else {
            try (InputStream in = new FileInputStream(source);
                 OutputStream out = new FileOutputStream(destination)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        if(threeDotBottomBar.getVisibility() == View.VISIBLE||threeDotBottomBar22.getVisibility() == View.VISIBLE){
            threeDotBottomBar.setVisibility(View.GONE);
            threeDotBottomBar22.setVisibility(View.GONE);
            touchInterceptor.setVisibility(View.GONE);
            return;
        }

        if (currentFolder != null && !currentFolder.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())) {
            File parent = currentFolder.getParentFile();
            if (parent != null) {
                loadFiles(parent);
                return;
            }
        }
        if (threeDotBottomBar.getVisibility() == View.VISIBLE) {
            threeDotBottomBar.setVisibility(View.GONE);
            threeDotBottomBar22.setVisibility(View.GONE);
            touchInterceptor.setVisibility(View.GONE);
        }
        copiedFiles.clear();
        if (pasteMenuItem != null) {
            pasteMenuItem.setVisible(false);
        }
        threeDotBottomBar.setVisibility(View.GONE);
        threeDotBottomBar22.setVisibility(View.GONE);
        if (isMoveMode) {
            isMoveMode = false;
            filesToMove.clear();
            // fastAdapter.desel
            selectExtension.deselect();
            // btnMove.setText("Move");
            Toast.makeText(this, "Move cancelled", Toast.LENGTH_SHORT).show();
        } else {
            super.onBackPressed();
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AllPermissionOfFileManager.REQUEST_STORAGE_ALL) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // Permission granted
                    loadInitialFiles();
                } else {
                    Toast.makeText(this, "All files access not granted", Toast.LENGTH_SHORT).show();
                }
            }
        }

        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            Uri treeUri = data.getData();
            String destPath = getFullPathFromTreeUri(treeUri, this);

            if (destPath != null && sourceFile != null) {
                File destFolder = new File(destPath);
                File destFile = new File(destFolder, sourceFile.getName());

                if (sourceFile.renameTo(destFile)) {
                    Toast.makeText(this, "Moved to " + destFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    loadFiles(currentFolder);
                } else {
                    Toast.makeText(this, "Move failed", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


    public static String getFullPathFromTreeUri(Uri treeUri, Context context) {
        if (treeUri == null) return null;

        String docId = DocumentsContract.getTreeDocumentId(treeUri);
        String[] split = docId.split(":");

        String type = split[0];
        String relativePath = split.length > 1 ? split[1] : "";

        String fullPath;

        if ("primary".equalsIgnoreCase(type)) {
            fullPath = Environment.getExternalStorageDirectory() + "/" + relativePath;
        } else {

            File[] external = ContextCompat.getExternalFilesDirs(context, null);
            if (external.length > 1) {
                String storageRoot = external[1].getAbsolutePath();
                storageRoot = storageRoot.substring(0, storageRoot.indexOf("/Android"));
                fullPath = storageRoot + "/" + relativePath;
            } else {
                return null;
            }
        }

        return fullPath;
    }

    public void unhideAllHiddenFiles(File currentFolder) {
        File[] files = currentFolder.listFiles();

        if (files == null || files.length == 0) {
            Toast.makeText(this, "No files in folder", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean anyUnhidden = false;

        for (File file : files) {
            String name = file.getName();
            if (name.startsWith(".")) {
                File unhiddenFile = new File(file.getParent(), name.substring(1));
                if (!unhiddenFile.exists()) {
                    boolean success = file.renameTo(unhiddenFile);
                    if (success) {
                        notifyMediaScanner(unhiddenFile);
                        anyUnhidden = true;
                    }
                }
            }
        }

        if (anyUnhidden) {
            Toast.makeText(this, "Hidden files unhidden", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No hidden files found", Toast.LENGTH_SHORT).show();
        }

        loadFiles(currentFolder);
    }


    private void notifyMediaScanner(File file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
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

            if (fromPos == 0 || toPos == 0) {
                return false;
            }

            Collections.swap(itemAdapter.getAdapterItems(), fromPos, toPos);
            fastAdapter.notifyAdapterItemMoved(fromPos, toPos);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        }

        @Override
        public boolean isLongPressDragEnabled() {
            return true;
        }
    };

    touchHelper = new ItemTouchHelper(dragCallback);
    touchHelper.attachToRecyclerView(recyclerView);
}



    @Override
    public void moreOptionsClocked(File file) {
        threeDotBottomBar.setVisibility(View.VISIBLE);

    }


    public void showThreeDotBottomBar(File file) {
        selectedFileForBottomBar = file;
        selectExtension.deselect();

        int layoutId = file.isDirectory()
                ? R.layout.three_dot_bottom_bar
                : R.layout.threedotbottombarfor_file;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(layoutId, null);
        bottomSheetDialog.setContentView(view);

        if (file.isDirectory()) {
            View bottomBar = view.findViewById(R.id.threeDotBottoBar);
            updateHideTextAndIcon(bottomBar, selectedFileForBottomBar);
            threeDotBottomBarButtons(view, bottomSheetDialog);
        }

        else {
            View bottomBar = view.findViewById(R.id.threeDotBottoBar2);
            updateHideTextAndIcon2(bottomBar, selectedFileForBottomBar);
            threeDotBottomBarButtons2(view, bottomSheetDialog);
        }

        bottomSheetDialog.show();
    }

private void threeDotBottomBarButtons(View view, BottomSheetDialog dialog) {

    TextView textView = view.findViewById(R.id.Rename);
    if(selectedFileForBottomBar!=null){
        textView.setText(selectedFileForBottomBar.getName());
    }

    LinearLayout favt = view.findViewById(R.id.fev);
    LinearLayout  hide = view.findViewById(R.id.hide);
    LinearLayout Details = view.findViewById(R.id.details);
    LinearLayout storage = view.findViewById(R.id.storsgeany);
    LinearLayout RenameLayout = view.findViewById(R.id.RenameLayout);
    LinearLayout addTosortcut = view.findViewById(R.id.addToSortCutLayout);
   // LinearLayout openWith = view.findViewById(R.id.openWithlayout2);
   // LinearLayout Share2 = view.findViewById(R.id.ShareLayout2);


   // IconicsImageView openwith = view.findViewById(R.id.openWithIcon);
   // IconicsImageView share2 = view.findViewById(R.id.shareIcon);
    IconicsImageView btnCopy = view.findViewById(R.id.btnCopy);
    IconicsImageView btnMove = view.findViewById(R.id.btnMove);
    IconicsImageView btnRename = view.findViewById(R.id.btnRename);
    IconicsImageView btnDelete = view.findViewById(R.id.btnDelete);
    IconicsImageView btnShare = view.findViewById(R.id.btnShare);
    IconicsImageView btnInfo = view.findViewById(R.id.detailsIcon);
    IconicsImageView btnhide = view.findViewById(R.id.hideicon);
    IconicsImageView compress = view.findViewById(R.id.copmress);
    IconicsImageView fevicon = view.findViewById(R.id.fevIcon);
    IconicsImageView storageicon = view.findViewById(R.id.storageIcon);
    IconicsImageView sortCut = view.findViewById(R.id.AddToSortCutIcon);




    storageicon.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_database)
            .color(getResources().getColor(R.color.white))
            .sizeDp(21));

    btnDelete.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_delete)
            .color(getResources().getColor(R.color.Red2))
            .sizeDp(22));

    btnRename.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon2.cmd_rename_box)
            .color(getResources().getColor(R.color.Orange))
            .sizeDp(22));

    btnShare.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon2.cmd_safe)
            .color(getResources().getColor(R.color.Red))
            .sizeDp(22));

    btnCopy.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_content_copy)
            .color(getResources().getColor(R.color.blue))
            .sizeDp(22));

    btnMove.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_content_cut)
            .color(getResources().getColor(R.color.Orange))
            .sizeDp(22));

    btnInfo.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon2.cmd_information)
            .color(getResources().getColor(R.color.white))
            .sizeDp(21));

    compress.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon2.cmd_zip_box)
            .color(getResources().getColor(R.color.deepblue))
            .sizeDp(22));
    sortCut.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_file_plus)
            .color(getResources().getColor(R.color.white))
            .sizeDp(22));


    addTosortcut.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (selectedFileForBottomBar != null && selectedFileForBottomBar.exists()) {
                createFolderShortcut(FileActivity.this, selectedFileForBottomBar);
                Toast.makeText(FileActivity.this, "Shortcut Added", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FileActivity.this, "Invalid file/folder", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        }
    });



    compress.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (selectedFileForBottomBar != null && selectedFileForBottomBar.exists()) {
                showZipNameDialog(selectedFileForBottomBar);
                dialog.dismiss();

            }
        }
    });


    btnDelete.setOnClickListener(v -> {
        if (selectedFileForBottomBar != null && selectedFileForBottomBar.exists()) {
            showDeleteDialog(selectedFileForBottomBar);
        } else {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        }

        dialog.dismiss();

    });

    favt.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (selectedFileForBottomBar != null && selectedFileForBottomBar.exists()) {
                FavoritesDbHelper dbHelper = new FavoritesDbHelper(FileActivity.this);
                String path = selectedFileForBottomBar.getAbsolutePath();

                Set<String> favSet = dbHelper.getAllFavorites();
                //avi avi kiya hu
                dbHelper.close();
                boolean isFavorite = favSet.contains(path);
                String toastMsg;

                if (isFavorite) {
                    //dbHelper.removeFromFavorites(path);
                    removeFromFavorites(selectedFileForBottomBar);
                    toastMsg = "Removed from Favorites";
                } else {
                    // dbHelper.addToFavorites(path);
                    addToFavorites(selectedFileForBottomBar);
                    toastMsg = "Added to Favorites";
                }

                View bottomBar = findViewById(R.id.threeDotBottoBar2);
                updateHideTextAndIcon2(bottomBar, selectedFileForBottomBar);

                Toast.makeText(FileActivity.this, toastMsg, Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(FileActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
            }

            //  threeDotBottomBar22.setVisibility(View.GONE);
            dialog.dismiss();
        }
    });

    hide.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean success = false;

            if (selectedFileForBottomBar != null && selectedFileForBottomBar.exists()) {
                View bottomBar = findViewById(R.id.threeDotBottoBar2);
                TextView hideText2 = bottomBar.findViewById(R.id.dialogHideTxt2);
                IconicsImageView btnhide = bottomBar.findViewById(R.id.hideicon2);

                if (selectedFileForBottomBar.getName().startsWith(".")) {
                    // Unhide
                    String name = selectedFileForBottomBar.getName().substring(1); // remove dot
                    File unhiddenFile = new File(selectedFileForBottomBar.getParent(), name);
                    success = selectedFileForBottomBar.renameTo(unhiddenFile);
                    if (success) {
                        selectedFileForBottomBar = unhiddenFile;
                        updateHideTextAndIcon2(bottomBar, selectedFileForBottomBar);
                    }
                } else {
                    // Hide
                    String newName = "." + selectedFileForBottomBar.getName();
                    File hiddenFile = new File(selectedFileForBottomBar.getParent(), newName);
                    success = selectedFileForBottomBar.renameTo(hiddenFile);
                    if (success) {
                        selectedFileForBottomBar = hiddenFile;
                        updateHideTextAndIcon2(bottomBar, selectedFileForBottomBar);
                    }
                }

                if (success) {
                    updateHideTextAndIcon2(bottomBar, selectedFileForBottomBar);
                    loadFiles(currentFolder);
                    Toast.makeText(context, "Hide/Show successful", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        }
    });

    Details.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (selectedFileForBottomBar == null || !selectedFileForBottomBar.exists()) {
                Toast.makeText(context, "No file selected or file not found", Toast.LENGTH_SHORT).show();
                return;
            }


            LinearLayout layout = new LinearLayout(FileActivity.this);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(60, 40, 60, 40);
            layout.setBackgroundColor(Color.parseColor("#212121"));

            String info = "Name: " + selectedFileForBottomBar.getName() +
                    "\n\nPath: " + selectedFileForBottomBar.getAbsolutePath() +
                    "\n\nSize: " + selectedFileForBottomBar.length() + " bytes" +
                    "\n\nLast Modified: " + new Date(selectedFileForBottomBar.lastModified());

            TextView textView = new TextView(FileActivity.this);
            textView.setText(info);
            textView.setTextColor(Color.parseColor("#FFFFFF"));
            textView.setTextSize(15);
            textView.setTypeface(Typeface.MONOSPACE);
            textView.setLineSpacing(10f, 1.2f);
            textView.setPadding(0, 0, 0, 0);

            layout.addView(textView);

            AlertDialog dialog2 = new AlertDialog.Builder(FileActivity.this)
                    .setTitle("Properties")
                    .setView(layout)
                    .setPositiveButton("CLOSE", null)
                    .create();

            dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#212121")));

            dialog2.show();


            TextView titleText = dialog2.findViewById(android.R.id.title);
            if (titleText != null) {
                titleText.setTextColor(Color.parseColor("#FF9800")); // Orange title
            }

            dialog.dismiss();
        }
    });


    btnRename.setOnClickListener(v -> {
        if (selectedFileForBottomBar == null) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }
        showRenameDialog(new FileAdeptor(selectedFileForBottomBar));
        selectedFileForBottomBar = null;
        threeDotBottomBar.setVisibility(View.GONE);
    });
    RenameLayout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (selectedFileForBottomBar == null) {
                return;
            }
            showRenameDialog(new FileAdeptor(selectedFileForBottomBar));
            selectedFileForBottomBar = null;
            //  threeDotBottomBar22.setVisibility(View.GONE);
            dialog.dismiss();
        }

    });

    btnCopy.setOnClickListener(v -> {
        if (selectedFileForBottomBar == null) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        isCopyMode = true;

        if (pasteMenuItem != null) {
            pasteMenuItem.setVisible(true);
        }

        Toast.makeText(this, "Item copied", Toast.LENGTH_SHORT).show();
        // threeDotBottomBar22.setVisibility(View.GONE);
        dialog.dismiss();
    });
    btnMove.setOnClickListener(v -> {
        if (selectedFileForBottomBar == null) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        isMoveMode = true;

        if (pasteMenuItem != null) {
            pasteMenuItem.setVisible(true);
        }
        Toast.makeText(this, "Select destination folder and click Paste", Toast.LENGTH_SHORT).show();
        dialog.dismiss();
    });
    // Share button
    btnShare.setOnClickListener(v -> {
        if (selectedFileForBottomBar != null && selectedFileForBottomBar.exists()) {
            SafeBoxManager safeBoxManager = new SafeBoxManager(FileActivity.this);
            safeBoxManager.showSafeBoxDialog(selectedFileForBottomBar);
        } else {
            Toast.makeText(FileActivity.this, "Invalid file/folder", Toast.LENGTH_SHORT).show();
        }
        dialog.dismiss();

    });

    storage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(FileActivity.this , Charte.class);
            startActivity(intent);
            dialog.dismiss();
        }

    });

}



    //----------------------Second threeDot Task------------------------------
    private void threeDotBottomBarButtons2(View view, BottomSheetDialog dialog) {

        TextView textView = view.findViewById(R.id.Rename2);
        if(selectedFileForBottomBar!=null){
            textView.setText(selectedFileForBottomBar.getName());
        }


        LinearLayout favt = view.findViewById(R.id.fev2);
        LinearLayout  hide = view.findViewById(R.id.hide2);
        LinearLayout Details = view.findViewById(R.id.details2);
        LinearLayout storage = view.findViewById(R.id.storsgeany2);
        LinearLayout RenameLayout = view.findViewById(R.id.RenameLayout2);
        LinearLayout openWith = view.findViewById(R.id.openWithlayout2);
        LinearLayout Share2 = view.findViewById(R.id.ShareLayout2);
        LinearLayout SortCutLayout = view.findViewById(R.id.LayoutSortCutFile);

        IconicsImageView sortCutIcon = view.findViewById(R.id.SortCutIcon2);
        IconicsImageView openwith = view.findViewById(R.id.openWithIcon2);
        IconicsImageView share2 = view.findViewById(R.id.shareIcon2);
        IconicsImageView btnCopy = view.findViewById(R.id.btnCopy2);
        IconicsImageView btnMove = view.findViewById(R.id.btnMove2);
        IconicsImageView btnRename = view.findViewById(R.id.btnRename2);
        IconicsImageView btnDelete = view.findViewById(R.id.btnDelete2);
        IconicsImageView btnShare = view.findViewById(R.id.btnShare2);
        IconicsImageView btnInfo = view.findViewById(R.id.detailsIcon2);
        IconicsImageView btnhide = view.findViewById(R.id.hideicon2);
        IconicsImageView compress = view.findViewById(R.id.copmress2);
        IconicsImageView fevicon = view.findViewById(R.id.fevIcon2);
        IconicsImageView storageicon = view.findViewById(R.id.storageIcon2);



        openwith.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_folder_open)
                .color(getResources().getColor(R.color.white))
                .sizeDp(21));

        share2.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon2.cmd_share_variant)
                .color(getResources().getColor(R.color.white))
                .sizeDp(22));

        storageicon.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_database)
                .color(getResources().getColor(R.color.white))
                .sizeDp(21));

        btnDelete.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_delete)
                .color(getResources().getColor(R.color.Red2))
                .sizeDp(22));

        btnRename.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon2.cmd_rename_box)
                .color(getResources().getColor(R.color.Orange))
                .sizeDp(22));

        btnShare.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon2.cmd_safe)
                .color(getResources().getColor(R.color.Red))
                .sizeDp(22));

        btnCopy.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_content_copy)
                .color(getResources().getColor(R.color.blue))
                .sizeDp(22));

        btnMove.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_content_cut)
                .color(getResources().getColor(R.color.Orange))
                .sizeDp(22));

        btnInfo.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon2.cmd_information)
                .color(getResources().getColor(R.color.white))
                .sizeDp(21));

        compress.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon2.cmd_zip_box)
                .color(getResources().getColor(R.color.deepblue))
                .sizeDp(22));
        sortCutIcon.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_file_plus)
                .color(getResources().getColor(R.color.white))
                .sizeDp(22));


        SortCutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFileForBottomBar != null && selectedFileForBottomBar.exists()) {
                    createFolderShortcut(FileActivity.this, selectedFileForBottomBar);
                    Toast.makeText(FileActivity.this, "Shortcut Added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(FileActivity.this, "Invalid file/folder", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });

        Share2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFileForBottomBar == null) {
                    Toast.makeText(FileActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_STREAM, FileProvider.getUriForFile(FileActivity.this, getPackageName() + ".provider", selectedFileForBottomBar));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(intent, "Share via"));

                selectedFileForBottomBar = null;
                dialog.dismiss();
            }
        });

        openWith.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFileForBottomBar != null) {
                    openFile(selectedFileForBottomBar);
                }
                dialog.dismiss();
            }
        });


        compress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFileForBottomBar != null && selectedFileForBottomBar.exists()) {
                    showZipNameDialog(selectedFileForBottomBar);
                    dialog.dismiss();

                }
            }
        });


        btnDelete.setOnClickListener(v -> {
            if (selectedFileForBottomBar != null && selectedFileForBottomBar.exists()) {
                showDeleteDialog(selectedFileForBottomBar);
            } else {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            dialog.dismiss();

        });

        favt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFileForBottomBar != null && selectedFileForBottomBar.exists()) {
                    FavoritesDbHelper dbHelper = new FavoritesDbHelper(FileActivity.this);
                    String path = selectedFileForBottomBar.getAbsolutePath();

                    Set<String> favSet = dbHelper.getAllFavorites();

                    dbHelper.close();

                    boolean isFavorite = favSet.contains(path);
                    String toastMsg;

                    if (isFavorite) {
                        //dbHelper.removeFromFavorites(path);
                        removeFromFavorites(selectedFileForBottomBar);
                        toastMsg = "Removed from Favorites";
                    } else {
                        // dbHelper.addToFavorites(path);
                        addToFavorites(selectedFileForBottomBar);
                        toastMsg = "Added to Favorites";
                    }

                    View bottomBar = findViewById(R.id.threeDotBottoBar2);
                    updateHideTextAndIcon2(bottomBar, selectedFileForBottomBar);

                    Toast.makeText(FileActivity.this, toastMsg, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(FileActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
                }

              //  threeDotBottomBar22.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });

        hide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean success = false;

                if (selectedFileForBottomBar != null && selectedFileForBottomBar.exists()) {
                    View bottomBar = findViewById(R.id.threeDotBottoBar2);
                    TextView hideText2 = bottomBar.findViewById(R.id.dialogHideTxt2);
                    IconicsImageView btnhide = bottomBar.findViewById(R.id.hideicon2);

                    if (selectedFileForBottomBar.getName().startsWith(".")) {
                        // Unhide
                        String name = selectedFileForBottomBar.getName().substring(1); // remove dot
                        File unhiddenFile = new File(selectedFileForBottomBar.getParent(), name);
                        success = selectedFileForBottomBar.renameTo(unhiddenFile);
                        if (success) {
                            selectedFileForBottomBar = unhiddenFile;
                            updateHideTextAndIcon2(bottomBar, selectedFileForBottomBar);
                        }
                    } else {
                        // Hide
                        String newName = "." + selectedFileForBottomBar.getName();
                        File hiddenFile = new File(selectedFileForBottomBar.getParent(), newName);
                        success = selectedFileForBottomBar.renameTo(hiddenFile);
                        if (success) {
                            selectedFileForBottomBar = hiddenFile;
                            updateHideTextAndIcon2(bottomBar, selectedFileForBottomBar);
                        }
                    }

                    if (success) {
                        updateHideTextAndIcon2(bottomBar, selectedFileForBottomBar);
                        loadFiles(currentFolder);
                        Toast.makeText(context, "Hide/Show successful", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                }
            }
        });

        Details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFileForBottomBar == null || !selectedFileForBottomBar.exists()) {
                    Toast.makeText(context, "No file selected or file not found", Toast.LENGTH_SHORT).show();
                    return;
                }


                LinearLayout layout = new LinearLayout(FileActivity.this);
                layout.setOrientation(LinearLayout.VERTICAL);
                layout.setPadding(60, 40, 60, 40);
                layout.setBackgroundColor(Color.parseColor("#212121"));

                String info = "Name: " + selectedFileForBottomBar.getName() +
                        "\n\nPath: " + selectedFileForBottomBar.getAbsolutePath() +
                        "\n\nSize: " + selectedFileForBottomBar.length() + " bytes" +
                        "\n\nLast Modified: " + new Date(selectedFileForBottomBar.lastModified());

                TextView textView = new TextView(FileActivity.this);
                textView.setText(info);
                textView.setTextColor(Color.parseColor("#FFFFFF"));
                textView.setTextSize(15);
                textView.setTypeface(Typeface.MONOSPACE);
                textView.setLineSpacing(10f, 1.2f);
                textView.setPadding(0, 0, 0, 0);

                layout.addView(textView);

                AlertDialog dialog2 = new AlertDialog.Builder(FileActivity.this)
                        .setTitle("Properties")
                        .setView(layout)
                        .setPositiveButton("CLOSE", null)
                        .create();

                dialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#212121")));

                dialog2.show();


                TextView titleText = dialog2.findViewById(android.R.id.title);
                if (titleText != null) {
                    titleText.setTextColor(Color.parseColor("#FF9800")); // Orange title
                }

                dialog.dismiss();
            }
        });


        btnRename.setOnClickListener(v -> {
            if (selectedFileForBottomBar == null) {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
                return;
            }
            showRenameDialog(new FileAdeptor(selectedFileForBottomBar));
            selectedFileForBottomBar = null;
            threeDotBottomBar.setVisibility(View.GONE);
        });
        RenameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedFileForBottomBar == null) {
                    return;
                }
                showRenameDialog(new FileAdeptor(selectedFileForBottomBar));
                selectedFileForBottomBar = null;
              //  threeDotBottomBar22.setVisibility(View.GONE);
                dialog.dismiss();
            }

        });

        btnCopy.setOnClickListener(v -> {
            if (selectedFileForBottomBar == null) {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
                return;
            }


            isCopyMode = true;// original
            //isCopyMode = false;

//            isCopyTrue = true;//ttt
//
//            if (pasteMenuItem != null) {
//                pasteMenuItem.setVisible(true); // main paste icon
//            }
//            if (pasteMenuItemTwo != null) {
//                pasteMenuItemTwo.setVisible(false); // hide 3-dot paste
//            }


             //invalidateOptionsMenu();

            if (pasteMenuItem != null) {
                pasteMenuItem.setVisible(true);
            }
           // isPasteVisible = true;
           // invalidateOptionsMenu();
            Toast.makeText(this, "Item copied", Toast.LENGTH_SHORT).show();
           // threeDotBottomBar22.setVisibility(View.GONE);
            dialog.dismiss();
        });
        btnMove.setOnClickListener(v -> {
            if (selectedFileForBottomBar == null) {
                Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
                return;
            }

            isMoveMode = true;

            if (pasteMenuItem != null) {
                pasteMenuItem.setVisible(true);
            }
            Toast.makeText(this, "Select destination folder and click Paste", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        // Share button
        btnShare.setOnClickListener(v -> {

            if (selectedFileForBottomBar != null && selectedFileForBottomBar.exists()) {
                SafeBoxManager safeBoxManager = new SafeBoxManager(FileActivity.this);
                safeBoxManager.showSafeBoxDialog(selectedFileForBottomBar);
            } else {
                Toast.makeText(FileActivity.this, "Invalid file/folder", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();

        });

        storage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FileActivity.this , Charte.class);
                startActivity(intent);
                dialog.dismiss();
            }

        });

    }




    private void showRenameDialog(FileAdeptor item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogStyle);

        View view = LayoutInflater.from(this).inflate(R.layout.rename_dialog, null);
        builder.setView(view);

        EditText editRename = view.findViewById(R.id.editRename);
        editRename.setText(item.getFile().getName());

        builder.setPositiveButton("SAVE", (dialog, which) -> {
            String newName = editRename.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            File oldFile = item.getFile();
            File newFile = new File(oldFile.getParent(), newName);
            if (oldFile.renameTo(newFile)) {
                Toast.makeText(this, "File renamed", Toast.LENGTH_SHORT).show();
                loadFiles(currentFolder);
                selectExtension.deselect();
            } else {
                Toast.makeText(this, "Rename failed", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("CANCEL", null);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
    }

    private void showRenameDialog(File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Rename File");

        final EditText input = new EditText(this);
        input.setText(file.getName());
        builder.setView(input);

        builder.setPositiveButton("Rename", (dialog, which) -> {
            String newName = input.getText().toString().trim();
            if (newName.isEmpty()) {
                Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }
            File newFile = new File(file.getParent(), newName);
            if (file.renameTo(newFile)) {
                Toast.makeText(this, "File renamed", Toast.LENGTH_SHORT).show();
                loadFiles(currentFolder);
            } else {
                Toast.makeText(this, "Rename failed", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private void copyAnything(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdirs();
            }

            File[] files = source.listFiles();
            if (files != null) {
                for (File file : files) {
                    File destFile = new File(destination, file.getName());
                    copyAnything(file, destFile);
                }
            }
        } else {
            try (InputStream in = new FileInputStream(source);
                 OutputStream out = new FileOutputStream(destination)) {

                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
        }
    }


    private void addToFavorites(File file) {
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(this);
        dbHelper.addFavorite(file.getAbsolutePath());

        loadFiles(currentFolder);
        Toast.makeText(this, "Added to favorites", Toast.LENGTH_SHORT).show();
        dbHelper.close();
    }
   private void removeFromFavorites(File file) {
       FavoritesDbHelper dbHelper = new FavoritesDbHelper(this);
       try {

           String path = file.getAbsolutePath();
           if (dbHelper.isFavorite(path)) {
               dbHelper.removeFavorite(path);
               getSupportActionBar().setTitle("My File");
               loadFiles(currentFolder);
               Toast.makeText(this, "Removed from favorites", Toast.LENGTH_SHORT).show();
           }
       }finally {
           dbHelper.close();
       }
   }

private void showFavorites() {
    FavoritesDbHelper dbHelper = new FavoritesDbHelper(this);
    Set<String> favSet = dbHelper.getAllFavorites();
    dbHelper.close();

    List<String> favPaths = new ArrayList<>(favSet);

    List<FileAdeptor> favItems = new ArrayList<>();
    int fileCount = 0;
    int folderCount = 0;

    for (String path : favPaths) {
        File file = new File(path);
        if (file.exists()) {
            if (file.isDirectory()) folderCount++;
            else fileCount++;

            favItems.add(new FileAdeptor(file));
        }
    }


        String result = folderCount + " Folders, " + fileCount + " Files";
        toolbar.setSubtitle(result);
        getSupportActionBar().setTitle("Favorites Section");
        itemAdapter.set(favItems);
}


    private boolean isFileFavorite(File file) {
        FavoritesDbHelper dbHelper = new FavoritesDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String query = "SELECT 1 FROM " + FavoritesDbHelper.TABLE_NAME +
                " WHERE " + FavoritesDbHelper.COLUMN_PATH + " = ? LIMIT 1";

        Cursor cursor = db.rawQuery(query, new String[]{file.getAbsolutePath()});

        boolean exists = cursor.moveToFirst();

        cursor.close();
        db.close();

        return exists;
    }




    //
private void setupDrawerMenu(NavigationView navigationView, DrawerLayout drawerLayout) {
    navigationView.setNavigationItemSelectedListener(item -> {
        int id = item.getItemId();

        if (id == R.id.id2) {
            Intent intent = new Intent(FileActivity.this,FileActivity.class);
            startActivity(intent);
            Toast.makeText(this, "Internal Storage", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.id3) {
            startActivity(new Intent(FileActivity.this, Charte.class));

        } else if (id == R.id.id4) {
            showFavorites();
            ifFav = true;
            invalidateOptionsMenu();

        } else if (id == R.id.id5) {
            Toast.makeText(this, "System Information", Toast.LENGTH_SHORT).show();

        } else if (id == R.id.nav_images) {
            showFilteredFilesRecursive(new String[]{".jpg", ".jpeg", ".png", ".gif", ".bmp"}, "Images");

        } else if (id == R.id.nav_videos) {
            showFilteredFilesRecursive(new String[]{".mp4", ".mkv", ".avi", ".mov"}, "Videos");

        } else if (id == R.id.nav_audio) {
            showFilteredFilesRecursive(new String[]{".mp3", ".wav", ".m4a", ".ogg"}, "Audios");

        } else if (id == R.id.nav_documents) {
            showFilteredFilesRecursive(new String[]{".pdf", ".doc", ".docx", ".xls", ".xlsx", ".txt"}, "Documents");

        } else if (id == R.id.nav_apks) {
            showFilteredFilesRecursive(new String[]{".apk"}, "APKs");
        }else if (id==R.id.nav_Setting){
            Intent intent = new Intent(FileActivity.this , Setting.class);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    });
}



    // three dot ka work
private void copyRecursively(File source, File dest) throws IOException {
    if (source.isDirectory()) {
        if (!dest.exists()) {
            dest.mkdirs();
        }

        File[] children = source.listFiles();
        if (children != null) {
            for (File child : children) {
                File destChild = new File(dest, child.getName());
                copyRecursively(child, destChild);
            }
        }
    } else {
        FileInputStream in = new FileInputStream(source);
        FileOutputStream out = new FileOutputStream(dest);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = in.read(buffer)) > 0) {
            out.write(buffer, 0, length);
        }
        in.close();
        out.close();
    }
}

    private void deleteRecursively(File fileOrDir) {
        if (fileOrDir.isDirectory()) {
            File[] children = fileOrDir.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteRecursively(child);
                }
            }
        }
        fileOrDir.delete();
    }

    private void copyDirectoryRecursively(File sourceDir, File destDir) throws IOException {
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        File[] children = sourceDir.listFiles();
        if (children != null) {
            for (File child : children) {
                File destChild = new File(destDir, child.getName());
                if (child.isDirectory()) {
                    copyDirectoryRecursively(child, destChild);
                } else {
                    copyFile(child, destChild);
                }
            }
        }
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN && threeDotBottomBar.getVisibility() == View.VISIBLE) {

            // Get bounds of bottom bar
            Rect outRect = new Rect();
            threeDotBottomBar.getGlobalVisibleRect(outRect);

            // Agar touch bottom bar ke bahar hua ho
            if (!outRect.contains((int) ev.getRawX(), (int) ev.getRawY())) {
                threeDotBottomBar.setVisibility(View.GONE);


            }
        }

        return super.dispatchTouchEvent(ev);
    }

    private void updateRenameText() {
        TextView rename = findViewById(R.id.Rename);
        if (selectedFileForBottomBar != null) {
            String fileName = selectedFileForBottomBar.getName();
            rename.setText(fileName);
        } else {
            rename.setText("No file selected");
        }
    }
    private void updateToolbarIconsVisibility() {
        boolean isAnyItemSelected = !fastAdapter.getSelectedItems().isEmpty();

        if (selectAllMenuItem != null && deleteMenuItem != null) {
            selectAllMenuItem.setVisible(isAnyItemSelected);
            deleteMenuItem.setVisible(isAnyItemSelected);
            copytwoMenuItem.setVisible(isAnyItemSelected);
            Drawable overflowIcon = toolbar.getOverflowIcon();
            if (overflowIcon != null) {
                overflowIcon.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
            Drawable upArrow = toolbar.getNavigationIcon();
            if (upArrow != null) {
                upArrow.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_ATOP);
            }
        }


    }

    private void zipFileOrFolder(File sourceFile, File zipFile) throws IOException {
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile));
        if (sourceFile.isDirectory()) {
            zipDirectory(sourceFile, sourceFile.getName(), zos);
        } else {
            zipSingleFile(sourceFile, zos);
        }
        zos.close();
    }

    private void zipDirectory(File folder, String parentFolder, ZipOutputStream zos) throws IOException {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                zipDirectory(file, parentFolder + "/" + file.getName(), zos);
            } else {
                FileInputStream fis = new FileInputStream(file);
                ZipEntry zipEntry = new ZipEntry(parentFolder + "/" + file.getName());
                zos.putNextEntry(zipEntry);

                byte[] buffer = new byte[1024];
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }

                zos.closeEntry();
                fis.close();
            }
        }
    }

    private void zipSingleFile(File file, ZipOutputStream zos) throws IOException {
        FileInputStream fis = new FileInputStream(file);
        ZipEntry zipEntry = new ZipEntry(file.getName());
        zos.putNextEntry(zipEntry);

        byte[] buffer = new byte[1024];
        int len;
        while ((len = fis.read(buffer)) > 0) {
            zos.write(buffer, 0, len);
        }

        zos.closeEntry();
        fis.close();
    }

    private void showZipNameDialog(File fileToZip) {
       // AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.CustomDialogStyle);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.compress, null);
        final EditText input = dialogView.findViewById(R.id.editRename2);
        builder.setView(dialogView);

        builder.setPositiveButton("OK", (dialog, which) -> {
            threeDotBottomBar.setVisibility(View.GONE);
            String zipFileName = input.getText().toString().trim();
            if (!zipFileName.endsWith(".zip")) {
                zipFileName += ".zip";
            }

            File zipFile = new File(fileToZip.getParent(), zipFileName);

            progressBar.setVisibility(View.VISIBLE);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            Handler handler = new Handler(Looper.getMainLooper());

            executor.execute(() -> {
                try {
                    zipFileOrFolder(fileToZip, zipFile);
                    handler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "ZIP created at: " + zipFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                        loadFiles(currentFolder);
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                    handler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Compression failed", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    private boolean deleteFileOrFolder(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] children = fileOrDirectory.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteFileOrFolder(child);
                }
            }
        }
        return fileOrDirectory.delete();
    }

   //++++++++++++++++====================Delete With Tresh By ThreeDot ================================================
  private String getRelativePath(File root, File file) {
      String rootPath = root.getAbsolutePath();
      String filePath = file.getAbsolutePath();
      if (filePath.startsWith(rootPath)) {
          return filePath.substring(rootPath.length());
      } else {
          return file.getName();
      }
  }

    private void showDeleteDialog(File fileToDelete) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_options, null);
        CheckBox checkbox = dialogView.findViewById(R.id.checkbox_move_to_trash);

        new AlertDialog.Builder(this)
                .setTitle("Delete Confirmation")
                .setView(dialogView)
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (checkbox.isChecked()) {
                        deleteWithProgress(fileToDelete);
                    } else {
                        deleteDirectly(fileToDelete);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void deleteWithProgress(File fileToDelete) {
        runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE));

        new Thread(() -> {
            try {
                File trashDir = new File(getFilesDir(), "trash");
                if (!trashDir.exists()) trashDir.mkdirs();

                File rootDir = fileToDelete.getParentFile();
                File trashedTarget = new File(trashDir, getRelativePath(rootDir, fileToDelete));

                boolean moved = copyRecursive(fileToDelete, trashedTarget);

                if (!moved) {
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(this, "Failed to copy to Trash", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                boolean deleted = deleteRecursive(fileToDelete);

                if (deleted) {
                    TrashDbHelper db = new TrashDbHelper(this);
                  //  addToTrashDBRecursive2(db, trashedTarget);
                    addToTrashDBRecursive2(db, trashedTarget, fileToDelete);

                }

                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    if (deleted) {
                        Toast.makeText(this, "Moved to Trash", Toast.LENGTH_SHORT).show();
                        loadFiles(currentFolder);
                    } else {
                        Toast.makeText(this, "Failed to delete original", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void deleteDirectly(File file) {
        boolean deleted = deleteRecursive(file);
        runOnUiThread(() -> {
            if (deleted) {
                Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show();
                loadFiles(currentFolder);
            } else {
                Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean copyRecursive(File source, File dest) {
        try {
            if (source.isDirectory()) {
                if (!dest.exists()) dest.mkdirs();
                File[] children = source.listFiles();
                if (children != null) {
                    for (File child : children) {
                        boolean success = copyRecursive(
                                new File(source, child.getName()),
                                new File(dest, child.getName())
                        );
                        if (!success) return false;
                    }
                }
            } else {
                try (InputStream in = new FileInputStream(source);
                     OutputStream out = new FileOutputStream(dest)) {
                    byte[] buffer = new byte[8192];
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
    private boolean deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            File[] children = fileOrDirectory.listFiles();
            if (children != null) {
                for (File child : children) {
                    if (!deleteRecursive(child)) {
                        return false;
                    }
                }
            }
        }
        return fileOrDirectory.delete();
    }



    private void addToTrashDBRecursive2(TrashDbHelper db, File trashFile, File originalFile) {
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





    void swipeToRefresh(){
    swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

    swipeRefreshLayout.setOnRefreshListener(() -> {

        loadFiles(currentFolder);
        updateToolbarCount(currentFolder);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
        }, 1000);
        getSupportActionBar().setTitle("My File");
        ifFav = false;
    });

    swipeRefreshLayout.setColorSchemeResources(
            R.color.blue,
            R.color.Orange,
            R.color.Yello

    );
}

void hideItem(){
    Set<FileAdeptor> selectedItems = selectExtension.getSelectedItems();

    if (selectedItems.isEmpty()) {
        Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
        return;
    }

    boolean allSuccess = true;
    for (FileAdeptor item : selectedItems) {
        File originalFile = item.getFile();
        if (originalFile != null && originalFile.exists()) {
            String parent = originalFile.getParent();
            String newName = "." + originalFile.getName();
            File hiddenFile = new File(parent, newName);

            boolean success = originalFile.renameTo(hiddenFile);
            if (!success) {
                allSuccess = false;
            }
        }
    }

    if (allSuccess) {
        Toast.makeText(this, "All selected files hidden", Toast.LENGTH_SHORT).show();
    } else {
        Toast.makeText(this, "Some files could not be hidden", Toast.LENGTH_SHORT).show();
    }

    selectExtension.deselect();
    loadFiles(currentFolder);
}
    private void unhideSelectedFiles() {
        Set<FileAdeptor> selectedItems = selectExtension.getSelectedItems();

        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "No hidden file selected", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean allSuccess = true;

        for (FileAdeptor item : selectedItems) {
            File hiddenFile = item.getFile();

            if (hiddenFile != null && hiddenFile.exists() && hiddenFile.getName().startsWith(".")) {
                String parent = hiddenFile.getParent();
                String originalName = hiddenFile.getName().substring(1);
                File unhiddenFile = new File(parent, originalName);

                boolean success = hiddenFile.renameTo(unhiddenFile);
                if (!success) {
                    allSuccess = false;
                }
            }
        }

        if (allSuccess) {
            Toast.makeText(this, "All selected files unhidden", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Some files could not be unhidden", Toast.LENGTH_SHORT).show();
        }

        selectExtension.deselect();
        loadFiles(currentFolder);

    }


    private void deleteSelectedFilesWithConfirmation() {
        final Set<FileAdeptor> selectedItems = new HashSet<>(selectExtension.getSelectedItems());

        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }


        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_delete_options, null);
        CheckBox checkbox = dialogView.findViewById(R.id.checkbox_move_to_trash);

        new AlertDialog.Builder(FileActivity.this)
                .setTitle("Delete Confirmation")
                .setView(dialogView)
                .setPositiveButton("Yes", (dialog, which) -> {

                    boolean moveToTrash = checkbox.isChecked();
                    progressBar.setVisibility(View.VISIBLE);

                    ExecutorService executor = Executors.newSingleThreadExecutor();
                    executor.execute(() -> {
                        TrashDbHelper trashDbHelper = new TrashDbHelper(FileActivity.this);

                        File trashDir = new File(getFilesDir(), "Trash");
                        if (!trashDir.exists()) trashDir.mkdirs();

                        File uniqueTrashFolder = new File(trashDir, "trash_" + System.currentTimeMillis());
                        if (!uniqueTrashFolder.exists()) uniqueTrashFolder.mkdirs();

                        AtomicBoolean allSuccess = new AtomicBoolean(true);

                        for (FileAdeptor item : selectedItems) {
                            File sourceFile = item.getFile();

                            if (sourceFile != null && sourceFile.exists()) {

                                if (moveToTrash) {
                                    File dest = new File(uniqueTrashFolder, sourceFile.getName());

                                    boolean copied = copyDirectoryOrFile(sourceFile, dest);
                                    if (copied) {
                                        boolean deleted = deleteRecursive2(sourceFile);
                                        if (deleted) {
                                           // addToTrashDBRecursive(FileActivity.this,trashDbHelper, dest);
                                            addToTrashDBRecursive(trashDbHelper, dest, sourceFile);
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
                                Toast.makeText(FileActivity.this, moveToTrash ? "Moved to Trash" : "Deleted", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(FileActivity.this, "Some files couldn't be processed", Toast.LENGTH_SHORT).show();
                            }

                            selectExtension.deselect();
                            loadFiles(currentFolder);
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













    // ================================================================================================================








    void ToolbarTitleSubTitleSize(){
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            View view = toolbar.getChildAt(i);
            if (view instanceof TextView) {
                TextView tv = (TextView) view;
                if (tv.getText().toString().equals(toolbar.getTitle().toString())) {
                    tv.setTextSize(18); 
                }

                else if (tv.getText().toString().equals(toolbar.getSubtitle().toString())) {
                    tv.setTextSize(10);
                }
            }
        }
        }

//===========================SecondThreeDotBottomBar=============================

  private void updateHideTextAndIcon2(View bottomBar, File file) {
      TextView hideText2 = bottomBar.findViewById(R.id.dialogHideTxt2);
      IconicsImageView btnhide2 = bottomBar.findViewById(R.id.hideicon2);

      IconicsImageView fevicon2 = bottomBar.findViewById(R.id.fevIcon2);
      TextView FavText2 = bottomBar.findViewById(R.id.dialogHideFav2);


      if (file != null && file.getName().startsWith(".")) {
          hideText2.setText("Show");
          btnhide2.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_eye)
                  .color(getResources().getColor(R.color.white))
                  .sizeDp(21));
      } else {
          hideText2.setText("Hide");
          btnhide2.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_eye_off)
                  .color(getResources().getColor(R.color.white))
                  .sizeDp(21));
      }
      // Favorite icon setup using SQLite
      if (file != null) {
          FavoritesDbHelper dbHelper = new FavoritesDbHelper(this);
          boolean isFavorite = dbHelper.isFavorite(file.getAbsolutePath());

          if (isFavorite) {
              FavText2.setText("Remove Favorite");
              fevicon2.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon2.cmd_star_off)
                      .color(getResources().getColor(R.color.Yello))
                      .sizeDp(21));
          } else {
              FavText2.setText("Add to Favorite");
              fevicon2.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon2.cmd_star)
                      .color(getResources().getColor(R.color.Yello))
                      .sizeDp(21));
          }
      }
  }


//==========================First==========================
    private void updateHideTextAndIcon(View bottomBar, File file) {
        TextView hideText2 = bottomBar.findViewById(R.id.dialogHideTxt);
        IconicsImageView btnhide2 = bottomBar.findViewById(R.id.hideicon);

        IconicsImageView fevicon2 = bottomBar.findViewById(R.id.fevIcon);
        TextView FavText2 = bottomBar.findViewById(R.id.dialogHideFav);


        if (file != null && file.getName().startsWith(".")) {
            hideText2.setText("Show");
            btnhide2.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_eye)
                    .color(getResources().getColor(R.color.white))
                    .sizeDp(21));
        } else {
            hideText2.setText("Hide");
            btnhide2.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon.cmd_eye_off)
                    .color(getResources().getColor(R.color.white))
                    .sizeDp(21));
        }
        // Favorite icon setup using SQLite
        if (file != null) {
            FavoritesDbHelper dbHelper = new FavoritesDbHelper(this);
            boolean isFavorite = dbHelper.isFavorite(file.getAbsolutePath());

            if (isFavorite) {
                FavText2.setText("Remove Favorite");
                fevicon2.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon2.cmd_star_off)
                        .color(getResources().getColor(R.color.Yello))
                        .sizeDp(21));
            } else {
                FavText2.setText("Add to Favorite");
                fevicon2.setIcon(new IconicsDrawable(this, CommunityMaterial.Icon2.cmd_star)
                        .color(getResources().getColor(R.color.Yello))
                        .sizeDp(21));
            }
        }
    }


    private void showFilteredFilesRecursive(String[] extensions, String sectionTitle) {
        runOnUiThread(() -> progressBar.setVisibility(View.VISIBLE)); // Show progress

        Context context = this;

        new Thread(() -> {
            List<FileAdeptor> filteredItems = new ArrayList<>();
            int fileCount = 0;
            int folderCount = 0;

            File root = Environment.getExternalStorageDirectory();
            collectMatchingFiles(root, extensions, filteredItems);

            for (FileAdeptor item : filteredItems) {
                File file = item.getFile();
                if (file.isDirectory()) folderCount++;
                else fileCount++;
            }

            String result = folderCount + " Folders, " + fileCount + " Files";

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                toolbar.setSubtitle(result);

                ActionBar actionBar = getSupportActionBar();
                if (actionBar != null) {
                    actionBar.setTitle(sectionTitle);
                }

                itemAdapter.set(filteredItems);
            });
        }).start();
    }





    void seprateImg(){
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String[] extensions = intent.getStringArrayExtra("extensions");

        if (title != null && extensions != null) {
            showFilteredFilesRecursive(extensions, title);
        } else {
            Toast.makeText(this, "No filter received", Toast.LENGTH_SHORT).show();
        }
    }

    private void collectMatchingFiles(File folder, String[] extensions, List<FileAdeptor> list) {
        if (folder == null || !folder.exists() || !folder.isDirectory()) return;

        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (!showHiddenFiles && file.getName().startsWith(".")) continue;

            if (file.isDirectory()) {
                collectMatchingFiles(file, extensions, list);
            } else {
                String name = file.getName().toLowerCase();
                for (String ext : extensions) {
                    if (name.endsWith(ext)) {
                        list.add(new FileAdeptor(file, onFileClickListener, false, "", this));
                        break;
                    }
                }
            }
        }
    }
void bySDcard(){
    String rootPath = getIntent().getStringExtra("root_path");

    File rootFolder;
    if (rootPath != null && new File(rootPath).exists()) {
        rootFolder = new File(rootPath);
    } else {
        rootFolder = Environment.getExternalStorageDirectory();
    }

    loadFiles(rootFolder);
}

void opener(){
    String filterType = getIntent().getStringExtra("filter_type");

    if (filterType != null) {
        switch (filterType) {
            case "Images":
                showFilteredFilesRecursive(new String[]{".jpg", ".jpeg", ".png", ".gif", ".bmp"}, "Images");
                break;

            case "Videos":
                showFilteredFilesRecursive(new String[]{".mp4", ".mkv", ".avi", ".mov"}, "Videos");
                break;

            case "Audios":
                showFilteredFilesRecursive(new String[]{".mp3", ".wav", ".m4a", ".ogg"}, "Audios");
                break;

            case "Documents":
                showFilteredFilesRecursive(new String[]{".pdf", ".doc", ".docx", ".xls", ".xlsx", ".txt"}, "Documents");
                break;

            case "APKs":
                showFilteredFilesRecursive(new String[]{".apk"}, "APKs");
                break;
        }
    }

}

    void openFav(){
        String path = getIntent().getStringExtra("clicked_path");
        if (path != null) {
            File clickedFile = new File(path);
            if (clickedFile.isDirectory()) {
                loadFiles(clickedFile);
            } else {
                openFile(clickedFile);
            }
        }
    }
//=================================QuckVideo=======================
private void loadVideoFoldersFromMediaStore() {
    Set<String> folderPaths = new HashSet<>();
    ContentResolver cr = getContentResolver();

    String[] projection = {
            MediaStore.Video.Media.DATA
    };

    Cursor cursor = cr.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            projection,
            null, null,
            MediaStore.Video.Media.DATE_ADDED + " DESC"
    );

    if (cursor != null) {
        int dataIndex = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
        while (cursor.moveToNext()) {
            String fullPath = cursor.getString(dataIndex);
            File videoFile = new File(fullPath);
            String folder = videoFile.getParent();
            folderPaths.add(folder);
        }
        cursor.close();
    }

    List<FileAdeptor> items = new ArrayList<>();
    for (String path : folderPaths) {
        File folder = new File(path);
        if (folder.exists() && folder.isDirectory()) {
            items.add(new FileAdeptor(folder));
        }
    }

    runOnUiThread(() -> {
        itemAdapter.set(items);
        Toast.makeText(this, items.size() + " video folders found", Toast.LENGTH_SHORT).show();
    });
}


//++++++++++++++++++++++++++++++++++SortCut++++++++++++++++++++++++++++++++++++++++++++++
@TargetApi(Build.VERSION_CODES.O)
public void createFolderShortcut(Context context, File folder) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

        Intent intent = new Intent(context, FileActivity.class);
        intent.setAction(Intent.ACTION_VIEW);
        intent.putExtra("shortcut_folder_path", folder.getAbsolutePath());

        ShortcutInfo shortcut = new ShortcutInfo.Builder(context, "shortcut_" + folder.getName())
                .setShortLabel(folder.getName())
                .setLongLabel("Open " + folder.getName())
                .setIcon(Icon.createWithResource(context, R.drawable.circulfolder)) // icon of folder
                .setIntent(intent)
                .build();

        shortcutManager.requestPinShortcut(shortcut, null);

    } else {
        Intent shortcutIntent = new Intent(context, FileActivity.class);
        shortcutIntent.setAction(Intent.ACTION_VIEW);
        shortcutIntent.putExtra("shortcut_folder_path", folder.getAbsolutePath());

        Intent addIntent = new Intent();
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, folder.getName());
        addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                Intent.ShortcutIconResource.fromContext(context, R.drawable.circulfolder));
        addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");

        context.sendBroadcast(addIntent);
    }
}


void sortCut() {
    String shortcutPath = getIntent().getStringExtra("shortcut_folder_path");
    if (shortcutPath != null) {
        File folder2 = new File(shortcutPath);
        if (folder2.exists() && folder2.isDirectory()) {
            if (currentFolder == null || !currentFolder.getAbsolutePath().equals(folder2.getAbsolutePath())) {
                currentFolder = folder2;
                loadFiles(folder2);
                return;
            }
        }
    }
}

void thremeLightDark(){
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
        RecyclerView r = findViewById(R.id.Filerecycle);
        r.setBackgroundColor(Color.BLACK);
        LinearLayout linearLayout = findViewById(R.id.FileLiner);
        linearLayout.setBackgroundColor(Color.BLACK);

    } else if("light".equals(value)) {
        RecyclerView r = findViewById(R.id.Filerecycle);
        r.setBackgroundColor(Color.WHITE);
        LinearLayout linearLayout = findViewById(R.id.FileLiner);
        linearLayout.setBackgroundColor(Color.WHITE);





        NavigationView navigationView1 = findViewById(R.id.nv);
        navigationView1.setBackground(ContextCompat.getDrawable(this, R.drawable.gride_seprate));
        navigationView1.setItemTextColor(ContextCompat.getColorStateList(this, android.R.color.black));
    }else if ("system_default".equals(value)) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {

            // Dark drawables bhi yaha set karo agar hain
        } else {
            RecyclerView r = findViewById(R.id.Filerecycle);
            r.setBackgroundColor(Color.WHITE);
            LinearLayout linearLayout = findViewById(R.id.FileLiner);
            linearLayout.setBackgroundColor(Color.WHITE);

            NavigationView navigationView1 = findViewById(R.id.nv);
            navigationView1.setBackground(ContextCompat.getDrawable(this, R.drawable.gride_seprate));
            navigationView1.setItemTextColor(ContextCompat.getColorStateList(this, android.R.color.black));


        }
    }}


    private void performPaste(File source, File destination) {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                boolean isSamePath = source.getAbsolutePath().equals(destination.getAbsolutePath());

                if (!isSamePath) {
                    // Only perform actual copy if not same path
                    if (source.isDirectory()) {
                        copyDirectoryRecursively(source, destination);
                    } else {
                        copyFile(source, destination);
                    }

                    if (isMoveMode) {
                        deleteRecursively(source); // Delete only if path is different
                        isMoveMode = false;
                    }
                }

                runOnUiThread(() -> {
                    Toast.makeText(this,
                            isMoveMode ? (isSamePath ? "Move skipped (same location)" : "Moved")
                                    : (isSamePath ? "Copy skipped (same location)" : "Copied"),
                            Toast.LENGTH_SHORT).show();

                    selectedFileForBottomBar = null;
                    if (pasteMenuItem != null) {
                        pasteMenuItem.setVisible(false);
                    }

                    loadFiles(currentFolder);
                    progressBar.setVisibility(View.GONE);

                    isMoveMode = false;
                    isCopyMode = false;
                    invalidateOptionsMenu();
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(this, "Operation failed", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
            }

        }).start();
    }





    private File getUniqueFile(File directory, String originalName) {
        File file = new File(directory, originalName);
        String name = originalName;
        String baseName = name;
        String extension = "";

        int dotIndex = name.lastIndexOf('.');
        if (dotIndex != -1) {
            baseName = name.substring(0, dotIndex);
            extension = name.substring(dotIndex);
        }

        int counter = 1;
        while (file.exists()) {
            file = new File(directory, baseName + "_copy" + counter + extension);
            counter++;
        }

        return file;
    }
    private void showPasteSuccess() {
        runOnUiThread(() -> {
            Toast.makeText(this, "File copied", Toast.LENGTH_SHORT).show();
        });
    }


    private void finishPasteOperation() {
        progressBar.setVisibility(View.GONE);
        Toast.makeText(this, "Paste complete", Toast.LENGTH_SHORT).show();
        pasteMenuItemTwo.setVisible(false);
        isCopyTrue = false;
        invalidateOptionsMenu();
        loadFiles(currentFolder);
    }

        private void processNextFile(int index, File destination) {
        if (index >= copiedFilesTwo.size()) {
            runOnUiThread(this::finishPasteOperation);
            return;
        }

        File file = copiedFilesTwo.get(index);
        File newFile = new File(destination, file.getName());

        if (newFile.exists()) {
            int finalIndex = index;

            runOnUiThread(() -> {
                AlertDialog dialog = new AlertDialog.Builder(FileActivity.this, R.style.MyAlertDialogStyle)
                        .setTitle("File/Folder already exists")
                        .setMessage("A file/folder with the same name already exists:\n\n" + file.getName())
                        .setPositiveButton("Rename", (d, which) -> {
                            if (file.getParent().equals(destination.getAbsolutePath())) {
                                // Same path, just show rename dialog
                                showRenameDialog(file, destination);
                                processNextFile(finalIndex + 1, destination);
                            } else {
                                new Thread(() -> {
                                    String renamedName = "Copy_" + System.currentTimeMillis() + "_" + file.getName();
                                    File renamedFile = new File(destination, renamedName);
                                    try {
                                        copyFile(file, renamedFile);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    processNextFile(finalIndex + 1, destination);
                                }).start();
                            }
                        })
                        .setNegativeButton("Skip", (d, which) -> {
                            new Thread(() -> processNextFile(finalIndex + 1, destination)).start();
                        })
                        .setNeutralButton("Replace the file in destination", (d, which) -> {
                            if (file.getParent().equals(destination.getAbsolutePath())) {
                                // Same path — no operation, just toast
                                runOnUiThread(() -> Toast.makeText(FileActivity.this, "Copy successfully", Toast.LENGTH_SHORT).show());
                                processNextFile(finalIndex + 1, destination);
                            } else {
                                new Thread(() -> {
                                    File keepBothFile = getUniqueFile(destination, file.getName());
                                    try {
                                        copyFile(file, keepBothFile);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    processNextFile(finalIndex + 1, destination);
                                }).start();
                            }
                        })
                        .setCancelable(false)
                        .show();

                // Set button colors
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#4CAF50")); // Green
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#F44336")); // Red
                dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(Color.parseColor("#2196F3")); // Blue
            });
        } else {
            new Thread(() -> {
                try {
                    copyFile(file, newFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                processNextFile(index + 1, destination);
            }).start();
        }
    }



    //=======================================RenamDialog=============================================
private void showRenameDialog(File sourceFile, File currentFolder) {
    LayoutInflater inflater = LayoutInflater.from(this);
    View renameView = inflater.inflate(R.layout.rename_dialog, null);

    EditText editRename = renameView.findViewById(R.id.editRename);
    editRename.setText(sourceFile.getName());
    editRename.setSelection(editRename.getText().length());

    AlertDialog.Builder renameBuilder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);
    renameBuilder.setView(renameView)
            .setCancelable(false)
            .setPositiveButton("OK", null)
            .setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());

    AlertDialog renameDialog = renameBuilder.create();
    renameDialog.show();

    renameDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#4CAF50")); // Green
    renameDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor("#F44336")); // Red

    renameDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(view -> {
        String newName = editRename.getText().toString().trim();
        if (!newName.isEmpty()) {
            File renamedDest = new File(currentFolder, newName);
            performPaste(sourceFile, renamedDest);
            renameDialog.dismiss();
        } else {
            Toast.makeText(this, "Name cannot be empty", Toast.LENGTH_SHORT).show();
        }
    });
}


}
