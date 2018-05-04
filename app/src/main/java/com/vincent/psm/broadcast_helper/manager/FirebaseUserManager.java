package com.vincent.psm.broadcast_helper.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class FirebaseUserManager {
    private static final String PUSH_TOKEN = "push_token";
    private static FirebaseUserManager INSTANCE = null;

    private SharedPreferences noClearSharedPreferences;
    private SharedPreferences.Editor noClearEditor;
    private String pushToken;

    private FirebaseUserManager(Context context) {
        noClearSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        noClearEditor = noClearSharedPreferences.edit();
        pushToken = noClearSharedPreferences.getString(PUSH_TOKEN, null);
    }

    public synchronized static FirebaseUserManager getInstance() {
        return INSTANCE;
    }

    public synchronized static void initialize(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new FirebaseUserManager(context);
        }
    }

    public void setPushToken(String pushToken) {
        this.pushToken = pushToken;
        noClearEditor.putString(PUSH_TOKEN, pushToken);
        noClearEditor.commit();
    }

    public String getPushToken() {
        return pushToken;
    }
}
