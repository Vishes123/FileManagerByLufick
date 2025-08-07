package com.example.filemanagerbylufic.AIS;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanagerbylufic.R;
import com.example.filemanagerbylufic.adeptor.DownloadFileAdapter;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSearchActivity extends AppCompatActivity {
    int fileCount = 0;
    int folderCount = 0;
    private ItemAdapter<FileItem> itemAdapter;
    private FastAdapter<FileItem> fastAdapter;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_search);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(fastAdapter);

        progressBar = findViewById(R.id.progressBar);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setSubtitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Quick Search");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);}

        String path = getIntent().getStringExtra("root_path");
        File rootFolder = path != null ? new File(path) : Environment.getExternalStorageDirectory();


        fastAdapter.withOnClickListener((v, adapter, item, position) -> {
            if (item instanceof FileItem) {
                openFile(((FileItem) item).getFile());
            }
            return true;
        });

        loadFiles(rootFolder);




        showPromptDialog();
    }

private void showPromptDialog() {
    String[] suggestions = {
            "Show newest files", "Show oldest files", "Show largest files", "Show smallest files",
            "Show recent images", "Show large videos", "Show documents larger than 50MB",
            "Show music files", "Show folders named Documents", "Search files named Vishesh",
            "A to Z sort", "Z to A sort"
    };


    View view = LayoutInflater.from(this).inflate(R.layout.dialog_prompt_ai, null);
    EditText input = view.findViewById(R.id.et_prompt);
    LinearLayout container = view.findViewById(R.id.suggestions_container);

    for (String s : suggestions) {

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(20, 10, 20, 10);
        card.setLayoutParams(params);
        card.setPadding(24, 16, 24, 16);
        card.setBackgroundResource(R.drawable.card_background);


        TextView tv = new TextView(this);
        tv.setText("• " + s);
        tv.setTextColor(Color.BLACK);
        tv.setTextSize(16f);


        card.setClickable(true);
        card.setFocusable(true);
        card.setBackgroundResource(R.drawable.card_selector);
        card.setOnClickListener(v -> input.setText(s));

        card.addView(tv);
        container.addView(card);
    }

    new AlertDialog.Builder(this)
            .setTitle("Quick Assistant")
            .setView(view)
            .setPositiveButton("Ask", (dialog, which) -> {
                String prompt = input.getText().toString();
                handlePrompt(prompt);
            })
            .setNeutralButton("Voice", (dialog, which) -> startVoiceRecognition())
            .setNegativeButton("Cancel", null)
            .show();

}




    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) {
                handlePrompt(results.get(0));
            }
        }
    }

    private void handlePrompt(String prompt) {
        if (prompt.trim().isEmpty()) {
            Toast.makeText(this, "Please enter a name or query to search.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Searching for: " + prompt, Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {

            AIHelper.SearchQuery query = AIHelper.parsePrompt(prompt);
            Log.d("Prompt", "Parsed keyword=" + query.keyword +
                    ", fileType=" + query.fileType +
                    ", minSizeBytes=" + query.minSizeBytes +
                    ", createdAfterMs=" + query.createdAfterMs +
                    ", sortBy=" + query.sortBy);

            List<File> found = new ArrayList<>();
            File root = Environment.getExternalStorageDirectory();
            scanFolder(root, found, query);


            if (query.sortBy != null) {
                switch (query.sortBy) {
                    case "newest":
                        found.sort((f1, f2) -> Long.compare(f2.lastModified(), f1.lastModified()));
                        break;
                    case "oldest":
                        found.sort((f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));
                        break;
                    case "largest":
                        found.sort((f1, f2) -> Long.compare(f2.length(), f1.length()));
                        break;
                    case "smallest":
                        found.sort((f1, f2) -> Long.compare(f1.length(), f2.length()));
                        break;
                    case "a_to_z":
                        found.sort((f1, f2) -> f1.getName().compareToIgnoreCase(f2.getName()));
                        break;
                    case "z_to_a":
                        found.sort((f1, f2) -> f2.getName().compareToIgnoreCase(f1.getName()));
                        break;
                }
            }

            List<FileItem> items = new ArrayList<>();
            for (File f : found) items.add(new FileItem(f));


            for (File f : found) {
                if (f.isDirectory()) folderCount++;
                else fileCount++;
            }

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                itemAdapter.setNewList(items);

                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("Files: " + fileCount + " | Folders: " + folderCount);

                Toast.makeText(this, "Found " + fileCount + " files & " + folderCount + " folders", Toast.LENGTH_LONG).show();
            });

        }).start();
    }



    private void scanFolder(File folder, List<File> found, AIHelper.SearchQuery query) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File f : files) {
            if (f.isDirectory()) {
                // Recurse into subdirectories
                scanFolder(f, found, query);
            }

            boolean match = true;
            String lowerName = f.getName().toLowerCase();

            // 🔍 Keyword filter (only apply if it's a specific name search)
            if (query.keyword != null && !query.keyword.trim().isEmpty()) {
                String lowerKeyword = query.keyword.toLowerCase();
                if (!(lowerKeyword.startsWith("show ") || lowerKeyword.startsWith("search "))) {
                    if (!lowerName.contains(lowerKeyword)) {
                        match = false;
                    }
                }
            }

            // 📂 File type filter (for files only)
            if (f.isFile() && query.fileType != null) {
                switch (query.fileType) {
                    case "video":
                        if (!(lowerName.endsWith(".mp4") || lowerName.endsWith(".mkv") || lowerName.endsWith(".avi"))) {
                            match = false;
                        }
                        break;
                    case "image":
                        if (!(lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || lowerName.endsWith(".png") || lowerName.endsWith(".gif") || lowerName.endsWith(".webp"))) {
                            match = false;
                        }
                        break;
                    case "document":
                        if (!(lowerName.endsWith(".pdf") || lowerName.endsWith(".doc") || lowerName.endsWith(".docx") || lowerName.endsWith(".ppt") || lowerName.endsWith(".xls"))) {
                            match = false;
                        }
                        break;
                    case "music":
                        if (!(lowerName.endsWith(".mp3") || lowerName.endsWith(".wav") || lowerName.endsWith(".aac") || lowerName.endsWith(".flac"))) {
                            match = false;
                        }
                        break;
                }
            }


            if (f.isFile() && query.minSizeBytes > 0) {
                if (f.length() < query.minSizeBytes) {
                    match = false;
                }
            }


            if (query.createdAfterMs > 0) {
                if (f.lastModified() < query.createdAfterMs) {
                    match = false;
                }
            }


            if (match) {
                found.add(f);
            }
        }
    }

    private String getMimeType(String path) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(path);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.toLowerCase());
        }
        if (type == null) {
            type = "*/*";
        }
        return type;
    }
    private void openFile(File file) {
        if (file.isDirectory()) {

            Intent intent = new Intent(this, FileSearchActivity.class);
            intent.putExtra("root_path", file.getAbsolutePath());
            startActivity(intent);
            return;
        }

        try {
            Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
            String mime = getMimeType(file.getAbsolutePath());

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, mime);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Open with"));

        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "No app found to open this file type", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Cannot open file", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void loadFiles(File rootFolder) {
        progressBar.setVisibility(View.VISIBLE);
        fileCount = 0;
        folderCount = 0;

        new Thread(() -> {
            List<File> found = new ArrayList<>();
            scanFolder(rootFolder, found, new AIHelper.SearchQuery());

            List<FileItem> items = new ArrayList<>();
            for (File f : found) {
                if (f.isDirectory()) folderCount++;
                else fileCount++;
                items.add(new FileItem(f));
            }

            runOnUiThread(() -> {
                progressBar.setVisibility(View.GONE);
                itemAdapter.set(items);
                Toolbar toolbar = findViewById(R.id.toolbar);
                toolbar.setTitle("Files: " + fileCount + " | Folders: " + folderCount);
                toolbar.setSubtitle(rootFolder.getAbsolutePath());
            });

        }).start();
    }

}
