package com.example.filemanagerbylufic;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.filemanagerbylufic.adeptor.SafeBoxItem;
import com.example.filemanagerbylufic.safeBox.SafeBoxManager;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.FastAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SafeBoxActivity extends AppCompatActivity {
Toolbar toolbar;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    private RecyclerView recyclerView;
    private ItemAdapter<SafeBoxItem> itemAdapter;
    private FastAdapter<SafeBoxItem> fastAdapter;
    private SafeBoxManager safeBoxManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_safe_box);
        toolbar = findViewById(R.id.toolbarSB);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Safe Box");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
           // getSupportActionBar().setSubtitle(files.size() + " files");
        }

        recyclerView = findViewById(R.id.SafeRecycleBox);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemAdapter = new ItemAdapter<>();
        fastAdapter = FastAdapter.with(itemAdapter);
        recyclerView.setAdapter(fastAdapter);

        safeBoxManager = new SafeBoxManager(this);
       promptPasswordAndLoad();




        fastAdapter.withOnClickListener((v, adapter, item, position) -> {
            handleItemClick(item.file);
            return true;
        });

    }

private void promptPasswordAndLoad() {
    View dialogView = getLayoutInflater().inflate(R.layout.dialog_safe_box_login, null);
    EditText etPassword = dialogView.findViewById(R.id.etPassword);
    TextView forgotPassword = dialogView.findViewById(R.id.forgotPassword);
    Button btnFingerprint = dialogView.findViewById(R.id.btnFingerprint);


    AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle("SafeBox Login")
            .setView(dialogView)
            .setCancelable(false)
            .setPositiveButton("Login", null)
            .setNegativeButton("Cancel", (d, w) -> {
                d.dismiss();
                finish();
            })
            .create();

    dialog.setOnShowListener(d -> {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String password = etPassword.getText().toString();
            if (safeBoxManager.verifyLogin(password)) {
                dialog.dismiss();
                loadSafeBoxFiles();
            } else {
                Toast.makeText(this, "Incorrect Password", Toast.LENGTH_SHORT).show();
            }
        });
    });

    forgotPassword.setOnClickListener(v -> {
        dialog.dismiss();
        showSecurityQuestionDialog();
    });

    btnFingerprint.setOnClickListener(v -> {
        authenticateWithFingerprint(() -> {
            dialog.dismiss();
            loadSafeBoxFiles();
        });
    });

    dialog.show();
}
    private void authenticateWithFingerprint(Runnable onSuccess) {
        BiometricPrompt prompt = new BiometricPrompt(this,
                ContextCompat.getMainExecutor(this),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        onSuccess.run();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(SafeBoxActivity.this, "Fingerprint not recognized", Toast.LENGTH_SHORT).show();
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Authenticate with Fingerprint")
                .setNegativeButtonText("Cancel")
                .build();

        prompt.authenticate(promptInfo);
    }

private void showSecurityQuestionDialog() {
    SQLiteDatabase db = safeBoxManager.getDbHelper().getReadableDatabase();
    Cursor cursor = db.rawQuery("SELECT answer FROM safebox_credentials WHERE question = ?", new String[]{"What's your favorite book?"});

    if (cursor.moveToFirst()) {
        // Question already answered, now verify
        String correctAnswer = cursor.getString(0);

        final EditText input = new EditText(this);
        input.setHint("Answer");

        new AlertDialog.Builder(this)
                .setTitle("Security Question")
                .setMessage("What's your favorite book?")
                .setView(input)
                .setPositiveButton("Verify", (dialog, which) -> {
                    if (input.getText().toString().trim().equalsIgnoreCase(correctAnswer)) {
                        Toast.makeText(this, "Access granted", Toast.LENGTH_SHORT).show();
                        loadSafeBoxFiles();
                    } else {
                        Toast.makeText(this, "Wrong answer", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .show();
    } else {
        // First time, ask user to set the answer
        askUserToSetAnswer();
    }

    cursor.close();
    db.close();
}
    private void askUserToSetAnswer() {
        final EditText input = new EditText(this);
        input.setHint("Your answer");

        new AlertDialog.Builder(this)
                .setTitle("Set Security Answer")
                .setMessage("What's your favorite book?")
                .setView(input)
                .setCancelable(false)
                .setPositiveButton("Save", (dialog, which) -> {
                    String answer = input.getText().toString().trim();
                    if (!answer.isEmpty()) {
                        saveSecurityAnswerToDB("What's your favorite book?", answer);
                        Toast.makeText(this, "Answer saved!", Toast.LENGTH_SHORT).show();
                        loadSafeBoxFiles();
                    } else {
                        Toast.makeText(this, "Answer cannot be empty", Toast.LENGTH_SHORT).show();
                        askUserToSetAnswer(); // Retry
                    }
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .show();
    }
    private void saveSecurityAnswerToDB(String question, String answer) {
        SQLiteDatabase db = safeBoxManager.getDbHelper().getWritableDatabase();
        db.execSQL("INSERT INTO safebox_credentials (question, answer) VALUES (?, ?)", new Object[]{question, answer});
        db.close();
    }



    private void loadSafeBoxFiles() {
        List<File> safeFiles = safeBoxManager.getSafeBoxFiles();
        List<SafeBoxItem> items = new ArrayList<>();
        for (File file : safeFiles) {
            items.add(new SafeBoxItem(file ,SafeBoxActivity.this));
        }
        itemAdapter.set(items);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void handleItemClick(File selectedFile) {
        if (selectedFile.isDirectory()) {
            File[] files = selectedFile.listFiles();
            if (files != null) {
                List<SafeBoxItem> items = new ArrayList<>();
                for (File file : files) {
                    items.add(new SafeBoxItem(file , SafeBoxActivity.this));
                }
                itemAdapter.set(items);
                toolbar.setSubtitle(files.length + " items");
            } else {
                Toast.makeText(this, "Folder is empty or inaccessible", Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
                Uri uri = FileProvider.getUriForFile(this, getPackageName() + ".provider", selectedFile);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(uri, getMimeType(selectedFile.getAbsolutePath()));
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(this, "Cannot open this file", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getMimeType(String path) {
        String extension = android.webkit.MimeTypeMap.getFileExtensionFromUrl(path);
        if (extension != null) {
            return android.webkit.MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return "*/*";
    }

    @Override
    public void onBackPressed() {
        File parent = null;
        if (itemAdapter.getAdapterItemCount() > 0) {
            File first = itemAdapter.getAdapterItem(0).file;
            parent = first.getParentFile();
        }

        if (parent != null && !parent.getAbsolutePath().equals(safeBoxManager.getSafeBoxDir().getAbsolutePath())) {
            handleItemClick(parent);
        } else {
            super.onBackPressed();
        }
    }
    private void promptFingerprintFirst() {
        biometricPrompt = new BiometricPrompt(this,
                ContextCompat.getMainExecutor(this),
                new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        loadSafeBoxFiles();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        Toast.makeText(SafeBoxActivity.this, "Fingerprint not recognized", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                        if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON ||
                                errorCode == BiometricPrompt.ERROR_USER_CANCELED ||
                                errorCode == BiometricPrompt.ERROR_CANCELED) {
                            promptPasswordAndLoad();
                        }
                    }
                });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Unlock SafeBox")
                .setSubtitle("Use your fingerprint to continue")
                .setNegativeButtonText("Use Password")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }



}
