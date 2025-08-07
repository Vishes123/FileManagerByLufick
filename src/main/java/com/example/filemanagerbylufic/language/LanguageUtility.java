package com.example.filemanagerbylufic.language;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.preference.PreferenceManager;

public class LanguageUtility {

    public static String getLocalizedFolderName(String originalName, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String lang = prefs.getString("language", "en");

        switch (lang) {
            case "hi": return getHindiName(originalName);
            case "es": return getSpanishName(originalName);
            case "ar": return getArabicName(originalName);
            case "ru": return getRussianName(originalName);
            case "de": return getGermanName(originalName);
            case "zh": return getChineseName(originalName);
            case "fr": return getFrenchName(originalName);
            case "ja": return getJapaneseName(originalName);
            case "en":
            default: return originalName;
        }
    }

    private static String getHindiName(String name) {
        switch (name) {
            case "Downloads": case "Download": return "डाउनलोड";
            case "Pictures": case "DCIM": return "चित्र";
            case "Music": return "संगीत";
            case "Documents": return "दस्तावेज़";
            case "Videos": return "वीडियो";
            case "MIUI": return "एमआईयूआई";
            case "Android": return "एंड्रॉइड";
            case "News": return "समाचार";
            case "Movies": return "फ़िल्में";
            case "Notifications": return "सूचनाएँ";
            case "Ringtones": return "रिंगटोन्स";
            case "ShareMe": case "Shareme": return "शेयरमी";
            case "cache": return "कैश";
            case "Audiobooks": return "ऑडियोबुक";
            default: return name;
        }
    }

    private static String getSpanishName(String name) {
        switch (name) {
            case "Downloads": case "Download": return "Descargas";
            case "Pictures": case "DCIM": return "Imágenes";
            case "Music": return "Música";
            case "Documents": return "Documentos";
            case "Videos": return "Videos";
            case "MIUI": return "MIUI";
            case "Android": return "Android";
            case "News": return "Noticias";
            case "Movies": return "Películas";
            case "Notifications": return "Notificaciones";
            case "Ringtones": return "Tonos de llamada";
            case "ShareMe": case "Shareme": return "Compartir";
            case "cache": return "Caché";
            case "Audiobooks": return "Audiolibros";
            default: return name;
        }
    }

    private static String getArabicName(String name) {
        switch (name) {
            case "Downloads": case "Download": return "التنزيلات";
            case "Pictures": case "DCIM": return "الصور";
            case "Music": return "الموسيقى";
            case "Documents": return "المستندات";
            case "Videos": return "مقاطع الفيديو";
            case "MIUI": return "MIUI";
            case "Android": return "أندرويد";
            case "News": return "الأخبار";
            case "Movies": return "أفلام";
            case "Notifications": return "الإشعارات";
            case "Ringtones": return "نغمات الرنين";
            case "ShareMe": case "Shareme": return "شاركني";
            case "cache": return "الذاكرة المؤقتة";
            case "Audiobooks": return "الكتب الصوتية";
            default: return name;
        }
    }

    private static String getRussianName(String name) {
        switch (name) {
            case "Downloads": case "Download": return "Загрузки";
            case "Pictures": case "DCIM": return "Изображения";
            case "Music": return "Музыка";
            case "Documents": return "Документы";
            case "Videos": return "Видео";
            case "MIUI": return "MIUI";
            case "Android": return "Андроид";
            case "News": return "Новости";
            case "Movies": return "Фильмы";
            case "Notifications": return "Уведомления";
            case "Ringtones": return "Рингтоны";
            case "ShareMe": case "Shareme": return "Поделиться";
            case "cache": return "Кэш";
            case "Audiobooks": return "Аудиокниги";
            default: return name;
        }
    }

    private static String getGermanName(String name) {
        switch (name) {
            case "Downloads": case "Download": return "Downloads";
            case "Pictures": case "DCIM": return "Bilder";
            case "Music": return "Musik";
            case "Documents": return "Dokumente";
            case "Videos": return "Videos";
            case "MIUI": return "MIUI";
            case "Android": return "Android";
            case "News": return "Nachrichten";
            case "Movies": return "Filme";
            case "Notifications": return "Benachrichtigungen";
            case "Ringtones": return "Klingeltöne";
            case "ShareMe": case "Shareme": return "Teilen";
            case "cache": return "Cache";
            case "Audiobooks": return "Hörbücher";
            default: return name;
        }
    }

    private static String getChineseName(String name) {
        switch (name) {
            case "Downloads": case "Download": return "下载";
            case "Pictures": case "DCIM": return "图片";
            case "Music": return "音乐";
            case "Documents": return "文档";
            case "Videos": return "视频";
            case "MIUI": return "小米";
            case "Android": return "安卓";
            case "News": return "新闻";
            case "Movies": return "电影";
            case "Notifications": return "通知";
            case "Ringtones": return "铃声";
            case "ShareMe": case "Shareme": return "分享我";
            case "cache": return "缓存";
            case "Audiobooks": return "有声书";
            default: return name;
        }
    }

    private static String getFrenchName(String name) {
        switch (name) {
            case "Downloads": case "Download": return "Téléchargements";
            case "Pictures": case "DCIM": return "Images";
            case "Music": return "Musique";
            case "Documents": return "Documents";
            case "Videos": return "Vidéos";
            case "MIUI": return "MIUI";
            case "Android": return "Android";
            case "News": return "Actualités";
            case "Movies": return "Films";
            case "Notifications": return "Notifications";
            case "Ringtones": return "Sonneries";
            case "ShareMe": case "Shareme": return "Partager";
            case "cache": return "Cache";
            case "Audiobooks": return "Livres audio";
            default: return name;
        }
    }

    private static String getJapaneseName(String name) {
        switch (name) {
            case "Downloads": case "Download": return "ダウンロード";
            case "Pictures": case "DCIM": return "画像";
            case "Music": return "音楽";
            case "Documents": return "ドキュメント";
            case "Videos": return "ビデオ";
            case "MIUI": return "MIUI";
            case "Android": return "アンドロイド";
            case "News": return "ニュース";
            case "Movies": return "映画";
            case "Notifications": return "通知";
            case "Ringtones": return "着信音";
            case "ShareMe": case "Shareme": return "共有する";
            case "cache": return "キャッシュ";
            case "Audiobooks": return "オーディオブック";
            default: return name;
        }
    }
}
