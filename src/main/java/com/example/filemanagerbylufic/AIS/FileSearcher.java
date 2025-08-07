package com.example.filemanagerbylufic.AIS;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileSearcher {
    public static List<File> searchFiles(File root, String fileType, long minSizeMB, long createdAfter) {
        List<File> result = new ArrayList<>();
        if(root == null || !root.exists()) return result;

        File[] files = root.listFiles();
        if(files != null) {
            for(File f : files) {
                if(f.isDirectory()) {
                    result.addAll(searchFiles(f, fileType, minSizeMB, createdAfter));
                } else {
                    boolean matchType = fileType==null || f.getName().toLowerCase().endsWith(fileType);
                    boolean matchSize = minSizeMB<=0 || f.length() > minSizeMB*1024*1024;
                    boolean matchDate = createdAfter<=0 || f.lastModified() > createdAfter;

                    if(matchType && matchSize && matchDate)
                        result.add(f);
                }
            }
        }
        return result;
    }
}
