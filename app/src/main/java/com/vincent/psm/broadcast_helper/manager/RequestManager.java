package com.vincent.psm.broadcast_helper.manager;

import android.app.Activity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vincent.psm.R;
import com.vincent.psm.broadcast_helper.data.Device;
import com.vincent.psm.broadcast_helper.data.FirebaseUser;
import com.vincent.psm.broadcast_helper.data.KeyData;
import com.vincent.psm.network_helper.MyOkHttp;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import static com.vincent.psm.data.DataHelper.tokens;

public class RequestManager {
    private static RequestManager FIREBASE_D2D_MANAGER = null;
    private DatabaseReference databaseRefUsers;
    private Activity activity;

    private RequestManager(Activity activity) {
        this.activity = activity;
        databaseRefUsers = FirebaseDatabase.getInstance().getReference();
    }

    public synchronized static final RequestManager getInstance(Activity activity) {
        if (FIREBASE_D2D_MANAGER == null) {
            FIREBASE_D2D_MANAGER = new RequestManager(activity);
        }
        return FIREBASE_D2D_MANAGER;
    }

    public void insertUser(final String id) {
        final Device device = new Device(FirebaseUserManager.getInstance().getPushToken());

        databaseRefUsers.child(FirebaseUser.DATABASE_USERS).child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            //當資料更新時，可讓我們讀取最新的資料，這邊先做一些資料的判斷，再透過setValue將該資料結構儲存至Firebase中即可
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FirebaseUser user = dataSnapshot.getValue(FirebaseUser.class);

                if (user == null)
                    user = new FirebaseUser();

                for (Device existingDevice : user.getDeviceList()) {
                    if (existingDevice.getToken().equals(device.getToken())) {
                        //已經註冊過該token
                        return;
                    }
                    /*
                    if (device.getDevice() != null && device.getDevice().equals("android")) //有註冊過相同作業系統
                        return;
                    */
                }

                user.addDevice(device);
                databaseRefUsers.child(FirebaseUser.DATABASE_USERS).child(id).setValue(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void prepareNotification(final String receiverId, final String title, final String message, final String photoUrl) {
        databaseRefUsers.child(FirebaseUser.DATABASE_USERS).child(receiverId).addListenerForSingleValueEvent(new ValueEventListener() {
            //當中的onDataChange()會有個DataSnapshot的物件傳入，我們可以透過接收方的ID，再由該物件取得其註冊的所有Token
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final FirebaseUser user = dataSnapshot.getValue(FirebaseUser.class);

                if (user == null || user.getDeviceList() == null || user.getDeviceList().size() == 0) {
                    return;
                }

                if (user.getDeviceList() != null) {
                    for (Device device : user.getDeviceList()) {
                        if (device.getDevice() != null) { //知道其作業系統(判斷似乎可省略)
                            pushNotification(title, message, photoUrl, device.getToken());
                        }
                    }
                }else {
                    //該使用者未註冊任何裝置

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void pushNotification(String title, String message, String photoUrl, String targetToken) {
        JSONObject reqObj = new JSONObject();
        try {
            reqObj.put(KeyData.TO, targetToken);

            JSONObject obj = new JSONObject();
            obj.put(KeyData.TITLE, title);
            obj.put(KeyData.MESSAGE, message);
            obj.put(KeyData.PHOTO, photoUrl);

            reqObj.put(KeyData.DATA, obj);

            MyOkHttp conn = new MyOkHttp(activity, new MyOkHttp.TaskListener() {
                @Override
                public void onFinished(JSONObject resObj) {

                }
            });

            //加header
            HashMap<String, String> header = new HashMap<>();
            header.put("Authorization", activity.getString(R.string.firebase_server_key));
            header.put("Content-Type", "application/json; charset=utf-8");
            conn.setHeader(header);

            conn.execute(activity.getString(R.string.firebase_cloud_messaging_url), reqObj.toString());
        }catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getTokensById(final String userId) {
        this.databaseRefUsers.child(FirebaseUser.DATABASE_USERS).child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final FirebaseUser user = dataSnapshot.getValue(FirebaseUser.class);

                if (user == null || user.getDeviceList() == null || user.getDeviceList().size() == 0) {
                    tokens = null; //當該user不存在，也設為null作為判斷
                    return;
                }

                if (user.getDeviceList() != null) {
                    tokens = new ArrayList<>();
                    for (Device device : user.getDeviceList()) {
                        if (device.getDevice() == null) {
                            continue;
                        }
                        tokens.add(device.getToken());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
