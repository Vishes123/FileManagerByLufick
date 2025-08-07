package com.example.filemanagerbylufic.favorites;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanagerbylufic.FileActivity;
import com.example.filemanagerbylufic.R;
import com.example.filemanagerbylufic.adeptor.FavoriteAdeptor;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Favorite extends AppCompatActivity {
    RecyclerView recyclerView;
    FastAdapter<FavoriteAdeptor> fastAdapter;
    ItemAdapter<FavoriteAdeptor> itemAdapter;
    private File currentFolder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favorite);

        Toolbar toolbar = findViewById(R.id.favToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Favorites");

        toolbar.setNavigationOnClickListener(v -> finish());


        recyclerView = findViewById(R.id.FavRecycleView);
        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        SharedPreferences sharedPreferences = getSharedPreferences("favorites_pref", MODE_PRIVATE);
        Set<String> favSet = sharedPreferences.getStringSet("favorites_list", new HashSet<>());

        List<FavoriteAdeptor> favoriteItems = new ArrayList<>();
        if (favSet != null) {
            for (String path : favSet) {
                File file = new File(path);
                if (file.exists()) {
                    favoriteItems.add(new FavoriteAdeptor(file, Favorite.this));
                }
            }
        }
        itemAdapter.add(favoriteItems);
        recyclerView.setAdapter(fastAdapter);

        fastAdapter.withOnClickListener((v, adapter, item, position) -> {
            File clickedFile = item.getFile();

            if (clickedFile.isDirectory()) {
                currentFolder = Environment.getExternalStorageDirectory();
                loadFiles(currentFolder);

                    File file = item.getFile();
                    if (file.isDirectory()) {
                        loadFiles(file);
                    } else {
                        openFile(file);
                    }
                    return true;

            } else {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri uri = FileProvider.getUriForFile(Favorite.this, getPackageName() + ".provider", clickedFile);
                intent.setDataAndType(uri, getMimeType(clickedFile.getAbsolutePath()));
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(Favorite.this, "No app found to open this file", Toast.LENGTH_SHORT).show();
                }
            }
            return true;
        });
        setupToolbarWithCount(favSet);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private String getMimeType(String path) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type != null ? type : "*/*";
    }
    private void loadFiles(File folder) {
        currentFolder = folder;
        File[] files = folder.listFiles();
        List<FavoriteAdeptor> itemList = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                itemList.add(new FavoriteAdeptor(file,Favorite.this));
            }
        }

        itemAdapter.set(itemList);
    }
    private void openFile(File file) {
        Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
        String mime = getMimeType(file.getAbsolutePath());

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, mime != null ? mime : "*/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(Intent.createChooser(intent, "Open with"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No app found to open this file", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onBackPressed() {
        File parent = currentFolder.getParentFile();
        if (parent != null && parent.canRead()) {
            loadFiles(parent);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fav_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();


        return super.onOptionsItemSelected(item);
    }

    private void setupToolbarWithCount(Set<String> favSet) {
        int fileCount = 0;
        int folderCount = 0;

        if (favSet != null) {
            for (String path : favSet) {
                File file = new File(path);
                if (file.exists()) {
                    if (file.isDirectory()) {
                        folderCount++;
                    } else {
                        fileCount++;
                    }

                }
            }
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle( fileCount + " Files, " + folderCount + " Folders");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }


}