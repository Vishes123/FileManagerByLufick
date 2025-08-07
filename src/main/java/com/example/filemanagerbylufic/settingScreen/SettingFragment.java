package com.example.filemanagerbylufic.settingScreen;


import static android.content.Context.MODE_PRIVATE;

import static androidx.core.app.ActivityCompat.recreate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;

import com.example.filemanagerbylufic.MainActivity;
import com.example.filemanagerbylufic.R;
import com.example.filemanagerbylufic.language.LocaleHelper;
import com.mikepenz.community_material_typeface_library.CommunityMaterial;
import com.mikepenz.iconics.IconicsDrawable;

import java.io.File;
import java.util.Locale;


public class SettingFragment extends PreferenceFragmentCompat {
    FrameLayout frameLayout;
    LinearLayout linearLayout;
    Context context;
    int color;

    SharedPreferences prefs;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

        setPreferencesFromResource(R.xml.root_prefrence, rootKey);
         foriconicset();
         aboutUs();
         genralSetting();
        Context context = requireContext();

        Preference clearCachePref = findPreference("clear_cache");
        if (clearCachePref != null) {
            clearCachePref.setOnPreferenceClickListener(preference -> {
                clearAllSharedPreferencesWithConfirmation(context);
                return true;
            });
        }

        //===================================================================




        prefs = getPreferenceManager().getSharedPreferences();

        if (!prefs.contains("theme_choice")) {
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("theme_choice", "system_default");
            editor.apply();
        }


        applyTheme(prefs.getString("theme_choice", "system_default"));



         prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String limit = prefs.getString("cache_limit", "50");
        int limitInMb = Integer.parseInt(limit);



         listener = (sharedPreferences, key) -> {
            if (key.equals("language")) {
                String langCode = sharedPreferences.getString("language", "en");
                LocaleHelper.setLocale(requireContext(), langCode);
                requireActivity().recreate();
            }
        };



        ListPreference tempUnitPref = findPreference("temperature_unit");
        if (tempUnitPref != null) {
            tempUnitPref.setOnPreferenceChangeListener((preference, newValue) -> {
                requireActivity().recreate();
                return true;
            });
        }



    }

   void foriconicset(){
       Context context = getContext();

        if (context != null) {
            Preference tempUnit = findPreference("Circularicon");
            if (tempUnit != null) {
                tempUnit.setIcon(new IconicsDrawable(context).icon(CommunityMaterial.Icon.cmd_folder_image).color(ContextCompat.getColor(context, R.color.orange)).sizeDp(26));
            }

            Preference windSpeed = findPreference("appIconOnfolder");
            if (windSpeed != null) {
                windSpeed.setIcon(new IconicsDrawable(context).icon(CommunityMaterial.Icon2.cmd_open_in_app).color(ContextCompat.getColor(context, R.color.orange)).sizeDp(26));
            }

            Preference pressure = findPreference("pressure");
            if (pressure != null) {
                pressure.setIcon(new IconicsDrawable(context).icon(CommunityMaterial.Icon.cmd_gauge).color(ContextCompat.getColor(context, R.color.orange)).sizeDp(26));
            }

            Preference location = findPreference("Location");
            if (location != null) {
                location.setIcon(new IconicsDrawable(context).icon(CommunityMaterial.Icon.cmd_google_maps).color(ContextCompat.getColor(context, R.color.orange)).sizeDp(26));
            }

            Preference refreshInterval = findPreference("refresh_interval");
            if (refreshInterval != null) {
                refreshInterval.setIcon(new IconicsDrawable(context).icon(CommunityMaterial.Icon2.cmd_refresh).color(ContextCompat.getColor(context, R.color.orange)).sizeDp(26));
            }

            Preference notificationInterval = findPreference("notification_interval");
            if (notificationInterval != null) {
                notificationInterval.setIcon(new IconicsDrawable(context).icon(CommunityMaterial.Icon.cmd_bell).color(ContextCompat.getColor(context, R.color.orange)).sizeDp(26));
            }

            Preference enableNotifications = findPreference("enable_notifications");
            if (enableNotifications != null) {
                enableNotifications.setIcon(new IconicsDrawable(context).icon(CommunityMaterial.Icon.cmd_bell_ring).color(ContextCompat.getColor(context, R.color.orange)).sizeDp(26));
            }

            Preference themeChoice = findPreference("theme_choice");
            if (themeChoice != null) {
                themeChoice.setIcon(new IconicsDrawable(context).icon(CommunityMaterial.Icon2.cmd_palette).color(ContextCompat.getColor(context, R.color.orange)).sizeDp(26));
            }

            Preference autoThemeChange = findPreference("auto_theme_change");
            if (autoThemeChange != null) {
                autoThemeChange.setIcon(new IconicsDrawable(context).icon(CommunityMaterial.Icon2.cmd_white_balance_auto).color(ContextCompat.getColor(context, R.color.orange)).sizeDp(26));
            }
            Preference sendFeedback = findPreference("send_feedback");
            if (sendFeedback != null) {
                sendFeedback.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_email)
                        .color(ContextCompat.getColor(context, R.color.orange))
                        .sizeDp(26));
            }

            Preference privacyPolicy = findPreference("privacy_policy");
            if (privacyPolicy != null) {
                privacyPolicy.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon2.cmd_lock_alert)
                        .color(ContextCompat.getColor(context, R.color.orange))
                        .sizeDp(26));
            }

            Preference termsCondition = findPreference("terms");
            if (termsCondition != null) {
                termsCondition.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_file_document)
                        .color(ContextCompat.getColor(context, R.color.orange))
                        .sizeDp(26));
            }

            Preference appVersion = findPreference("app_version");
            if (appVersion != null) {
                appVersion.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_application)
                        .color(ContextCompat.getColor(context, R.color.orange))
                        .sizeDp(26));
            }
            Preference cacheClear = findPreference("clear_cache");
            if (cacheClear != null) {
                cacheClear.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_delete_sweep)
                        .color(ContextCompat.getColor(context, R.color.orange))
                        .sizeDp(26));
            }

            Preference cacheLimit = findPreference("cache_limit");
            if (cacheLimit != null) {
                cacheLimit.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_chart_donut)
                        .color(ContextCompat.getColor(context, R.color.orange))
                        .sizeDp(26));
            }
            Preference languagePref = findPreference("language");
            if (languagePref != null) {
                languagePref.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_google_translate)
                        .color(ContextCompat.getColor(context, R.color.orange))
                        .sizeDp(26));
            }

            Preference favCount = findPreference("fav_count");
            if (favCount != null) {
                favCount.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_counter)
                        .color(ContextCompat.getColor(context, R.color.orange))
                        .sizeDp(26));
            }
            Preference favCount2 = findPreference("trash_delete_days");
            if (favCount2 != null) {
                favCount2.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_delete)
                        .color(ContextCompat.getColor(context, R.color.orange))
                        .sizeDp(26));
            }


            Preference notifyPref = findPreference("enable_notifications");
            if (notifyPref != null) {
                notifyPref.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_bell_ring)
                        .color(ContextCompat.getColor(context, R.color.orange))
                        .sizeDp(26));
            }


            Preference showHiddenPref = findPreference("show_hidden");
            if (showHiddenPref != null) {
                showHiddenPref.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon.cmd_eye_off)
                        .color(ContextCompat.getColor(context, R.color.orange))
                        .sizeDp(26));
            }
            Preference recent = findPreference("recent_count");
            if (recent != null) {
                recent.setIcon(new IconicsDrawable(context)
                        .icon(CommunityMaterial.Icon2.cmd_reload)
                        .color(ContextCompat.getColor(context, R.color.orange))
                        .sizeDp(26));
            }


        }

    }


    private void applyTheme(String themePref) {
        switch (themePref) {
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
    }
    void aboutUs(){
        Preference sendFeedback = findPreference("send_feedback");
        Preference privacyPolicy = findPreference("privacy_policy");
        Preference terms = findPreference("terms");
        Preference appVersion = findPreference("app_version");

        if (sendFeedback != null) {
            sendFeedback.setOnPreferenceClickListener(preference -> {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:support@lufick.com"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Feedback");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Enter your feedback here...");
                try {
                    startActivity(Intent.createChooser(emailIntent, "Send Feedback"));
                } catch (Exception e) {
                    Toast.makeText(getActivity(), "No email app found", Toast.LENGTH_SHORT).show();
                }
                return true;
            });
        }


        if (privacyPolicy != null) {
            privacyPolicy.setOnPreferenceClickListener(preference -> {
                Uri uri = Uri.parse("https://smart-file-manager.lufick.com/privacy-policy/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            });
        }

        if (terms != null) {
            terms.setOnPreferenceClickListener(preference -> {
                Uri uri = Uri.parse("https://smart-file-manager.lufick.com/privacy-policy/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
                return true;
            });
        }

        if (appVersion != null) {
            try {
                String versionName = requireContext()
                        .getPackageManager()
                        .getPackageInfo(requireContext().getPackageName(), 0)
                        .versionName;
                appVersion.setSummary(versionName);
            } catch (PackageManager.NameNotFoundException e) {
                appVersion.setSummary("1.0");
            }
        }
    }


    private void clearAppCache() {
        try {
            Context context = getContext();
            File cacheDir = context.getCacheDir();
            deleteDir(cacheDir);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) return false;
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

void genralSetting(){
    Context context = getContext();

    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

    String selectedLanguage = prefs.getString("language", "en");
    applyLanguage(selectedLanguage);


    boolean notificationsEnabled = prefs.getBoolean("enable_notifications", true);
    if (notificationsEnabled) {

        Log.d("Settings", "Notifications are ON");
    } else {

        Log.d("Settings", "Notifications are OFF");
    }


    boolean showHidden = prefs.getBoolean("show_hidden", false);
    if (showHidden) {

        Log.d("Settings", "Hidden files will be shown");
    } else {

        Log.d("Settings", "Hidden files will be hidden");
    }
}
    private void applyLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

        public static int getFavCount(Context context) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
            String countStr = prefs.getString("fav_count", "10");
            return Integer.parseInt(countStr);
    }

    private void clearAllSharedPreferencesWithConfirmation(Context context) {
        new AlertDialog.Builder(context)
                .setTitle("Clear Preferences")
                .setMessage("Are you sure you want to clear all saved preferences?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.clear();
                    editor.apply();

                    Toast.makeText(context, "Preferences cleared", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

//====================Language===================
@Override
public void onResume() {
    super.onResume();
    prefs.registerOnSharedPreferenceChangeListener(listener);
}

    @Override
    public void onPause() {
        prefs.unregisterOnSharedPreferenceChangeListener(listener);
        super.onPause();
    }

}
