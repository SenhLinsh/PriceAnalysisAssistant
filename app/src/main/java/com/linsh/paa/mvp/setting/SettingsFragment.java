package com.linsh.paa.mvp.setting;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import com.linsh.lshutils.utils.Basic.LshStringUtils;
import com.linsh.paa.BuildConfig;
import com.linsh.paa.R;

/**
 * Created by Senh Linsh on 17/5/2.
 */
public class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);

        findPreference("normal_interval_time").setOnPreferenceClickListener(this);
        findPreference("import_items").setOnPreferenceClickListener(this);
        findPreference("import_realm").setOnPreferenceClickListener(this);
        findPreference("export_realm").setOnPreferenceClickListener(this);
        Preference checkUpdate = findPreference("check_update");
        checkUpdate.setOnPreferenceClickListener(this);
        checkUpdate.setTitle(checkUpdate.getTitle() + LshStringUtils.format(" (当前版本: {版本号})", BuildConfig.VERSION_NAME));
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "normal_interval_time":
                ((SettingsActivity)getActivity()).setIntervalTime();
                break;
            case "import_items":
                ((SettingsActivity)getActivity()).importItems();
                break;
            case "import_realm":
                ((SettingsActivity)getActivity()).importRealm();
                break;
            case "export_realm":
                ((SettingsActivity)getActivity()).exportRealm();
                break;
            case "check_update":
                ((SettingsActivity)getActivity()).checkUpdate();
                break;
        }
        return true;
    }
}
