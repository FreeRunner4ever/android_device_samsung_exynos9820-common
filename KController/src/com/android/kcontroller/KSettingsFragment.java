package com.android.kcontroller;

import com.android.kcontroller.R;
import com.android.kcontroller.utils.FileUtils;
import com.android.kcontroller.utils.Tags;

import android.app.ActionBar;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;
import androidx.preference.PreferenceFragment;
import androidx.preference.PreferenceManager;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreference;

import android.util.Log;

public class KSettingsFragment extends PreferenceFragment {

    private SharedPreferences mSharedPrefs;
    private SwitchPreference mDevfreqBoostPreference;
    private SwitchPreference mChargingLimitPreference;
    private SeekBarPreference mChargingLimitThresholdPreference;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.kcontroller_settings);
        final ActionBar actionBar = getActivity().getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mSharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        mDevfreqBoostPreference = (SwitchPreference) findPreference(Tags.DEVFREQ_BOOST_KEY);
        mChargingLimitPreference = (SwitchPreference) findPreference(Tags.CHARGING_LIMIT_KEY);
        mChargingLimitThresholdPreference = (SeekBarPreference) findPreference(Tags.CHARGING_LIMIT_THRESHOLD_KEY);

        mChargingLimitThresholdPreference.setShowSeekBarValue(true);
        mChargingLimitThresholdPreference.setMin(60);

        if (FileUtils.fileExists(Tags.DEVFREQ_BOOST_NODE)) {
            mDevfreqBoostPreference.setEnabled(true);
            mDevfreqBoostPreference.setSummary(R.string.devfreq_boost_summary);
        } else {
            mDevfreqBoostPreference.setEnabled(false);
            mDevfreqBoostPreference.setSummary(R.string.devfreq_boost_not_supported);
        }

        if (FileUtils.fileExists(Tags.CHARGING_LIMIT_NODE)) {
            mChargingLimitPreference.setEnabled(true);
            mChargingLimitPreference.setSummary(R.string.charging_limit_summary);
        } else {
            mChargingLimitPreference.setEnabled(false);
            mChargingLimitPreference.setSummary(R.string.charging_limit_not_supported);
        }

        mDevfreqBoostPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            if ((boolean)newValue)
                FileUtils.writeLine(Tags.DEVFREQ_BOOST_NODE, "1");
            else
                FileUtils.writeLine(Tags.DEVFREQ_BOOST_NODE, "0");
            return true;
        });

        mChargingLimitPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            if (!(boolean)newValue)
                FileUtils.writeLine(Tags.CHARGING_LIMIT_NODE, "0");
            else
                FileUtils.writeLine(Tags.CHARGING_LIMIT_NODE, String.valueOf(getChargingLimitThresHold()));
            return true;
        });

        mChargingLimitThresholdPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            FileUtils.writeLine(Tags.CHARGING_LIMIT_NODE, String.valueOf(newValue));
            return true;
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = LayoutInflater.from(getContext()).inflate(R.layout.kcontroller,
                container, false);
        ((ViewGroup) view).addView(super.onCreateView(inflater, container, savedInstanceState));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private boolean getDevfreqBoost() {
        return mSharedPrefs.getBoolean(Tags.DEVFREQ_BOOST_KEY, false);
    }

    private boolean getChargingLimit() {
        return mSharedPrefs.getBoolean(Tags.CHARGING_LIMIT_KEY, false);
    }

    private int getChargingLimitThresHold() {
        return mSharedPrefs.getInt(Tags.CHARGING_LIMIT_THRESHOLD_KEY, 85);
    }
}
