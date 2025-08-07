package com.example.filemanagerbylufic.storagePieCharteStatusGraph;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class FileCharte {

    public long imageSize = 0;
    public long videoSize = 0;
    public long audioSize = 0;
    public long documentSize = 0;

    public void analyzeFiles(File root) {
        if (root == null || !root.exists()) return;

        Queue<File> queue = new LinkedList<>();
        queue.add(root);

        while (!queue.isEmpty()) {
            File file = queue.poll();

            if (file == null) continue;

            if (file.isDirectory()) {
                File[] children = file.listFiles();
                if (children != null) Collections.addAll(queue, children);
            } else {
                long size = file.length();
                String name = file.getName().toLowerCase();

                if (name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".webp"))
                    imageSize += size;
                else if (name.endsWith(".mp4") || name.endsWith(".mkv") || name.endsWith(".avi"))
                    videoSize += size;
                else if (name.endsWith(".mp3") || name.endsWith(".wav") || name.endsWith(".aac"))
                    audioSize += size;
                else if (name.endsWith(".pdf") || name.endsWith(".docx") || name.endsWith(".txt"))
                    documentSize += size;
            }
        }
    }
}
