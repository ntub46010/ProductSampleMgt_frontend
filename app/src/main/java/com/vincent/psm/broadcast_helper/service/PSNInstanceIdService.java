package com.vincent.psm.broadcast_helper.service;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.vincent.psm.broadcast_helper.manager.FirebaseUserManager;

public class PSNInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String currentToken = FirebaseInstanceId.getInstance().getToken();
        FirebaseUserManager.getInstance().setPushToken(currentToken);
    }
}
