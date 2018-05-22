package com.vincent.psm.broadcast_helper.service;

import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.vincent.psm.R;
import com.vincent.psm.broadcast_helper.PSMApplication;
import com.vincent.psm.broadcast_helper.manager.FirebaseUserManager;

public class InstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() { //若未被呼叫，可嘗試重新安裝APP
        String currentToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseUserManager.getInstance().setPushToken(currentToken);
        getSharedPreferences(getString(R.string.sp_filename), MODE_PRIVATE)
                .edit()
                .putString(getString(R.string.sp_token), currentToken)
                .apply();
    }
}
