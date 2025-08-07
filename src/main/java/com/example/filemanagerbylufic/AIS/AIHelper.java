package com.example.filemanagerbylufic.AIS;

public class AIHelper {
    public static class SearchQuery {
        public String keyword = "";
        public String fileType = null;
        public long minSizeBytes = 0;
        public long createdAfterMs = 0;
        public String sortBy = null;
    }

    public static SearchQuery parsePrompt(String prompt) {
        SearchQuery query = new SearchQuery();
        prompt = prompt.toLowerCase();

        if (prompt.contains("named")) {
            String[] words = prompt.split("named");
            if (words.length > 1) {
                query.keyword = words[1].trim();
            }
        } else {

            query.keyword = prompt.trim();
        }


        if (prompt.contains("image")) query.fileType = "image";
        else if (prompt.contains("video")) query.fileType = "video";
        else if (prompt.contains("document")) query.fileType = "document";
        else if (prompt.contains("music")) query.fileType = "music";
        else if (prompt.contains("folder")) query.fileType = "folder";


        if (prompt.contains("50mb")) {
            query.minSizeBytes = 50L * 1024 * 1024;
        }


        if (prompt.contains("recent")) {
            long oneWeekAgo = System.currentTimeMillis() - 7L * 24 * 60 * 60 * 1000;
            query.createdAfterMs = oneWeekAgo;
        }


        if (prompt.contains("newest")) query.sortBy = "newest";
        else if (prompt.contains("oldest")) query.sortBy = "oldest";
        else if (prompt.contains("largest")) query.sortBy = "largest";
        else if (prompt.contains("smallest")) query.sortBy = "smallest";
        else if (prompt.contains("a to z")) query.sortBy = "a_to_z";
        else if (prompt.contains("z to a")) query.sortBy = "z_to_a";

        return query;
    }
}
