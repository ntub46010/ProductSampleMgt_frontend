package com.vincent.psm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.vincent.psm.data.AlbumImageProvider;
import com.vincent.psm.data.ImageChild;
import com.vincent.psm.structure.ImageUploadQueue;

import static com.vincent.psm.data.AlbumImageProvider.REQUEST_ALBUM;
import static com.vincent.psm.data.AlbumImageProvider.REQUEST_CROP;

public class ImageUploadActivity extends AppCompatActivity {
    private Context context;
    private ImageView imageView;
    private Button btnUpload;
    private AlbumImageProvider provider;
    private ImageUploadQueue queue;
    private ProgressBar prgBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);
        context = this;

        imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provider = new AlbumImageProvider(ImageUploadActivity.this, 5, 6, 350, 420, new AlbumImageProvider.TaskListener() {
                    @Override
                    public void onFinished(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                });
                provider.select();
            }
        });

        btnUpload = findViewById(R.id.btnUpload);
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prgBar.setVisibility(View.VISIBLE);
                String[] fileNames = new String[1];
                fileNames[0] = "";
                queue = new ImageUploadQueue(getResources(), context, getString(R.string.link_upload_avatar));
                queue.enqueueFromRear(new ImageChild(provider.getImage(), true));
                queue.startUpload(fileNames, null, null, new ImageUploadQueue.TaskListener() {
                    @Override
                    public void onFinished(String[] fileNames) {
                        Toast.makeText(context, "上傳完成", Toast.LENGTH_LONG).show();
                        prgBar.setVisibility(View.GONE);
                    }
                });
            }
        });

        prgBar = findViewById(R.id.prgBar);
        prgBar.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ALBUM || requestCode == REQUEST_CROP)
            provider.onActivityResult(requestCode, resultCode, data);
        else
            super.onActivityResult(requestCode, resultCode, data);
    }
}
