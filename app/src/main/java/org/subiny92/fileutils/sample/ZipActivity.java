package org.subiny92.fileutils.sample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;


import org.subiny92.fileutils.R;
import org.subiny92.fileutils.utils.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ZipActivity extends AppCompatActivity {

    private static final String SAMPLE_DIRECTORY = "sample";
    private static final String SAMPLE_FILE = "sample.txt";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zip);

        findViewById(R.id.btn_create_file).setOnClickListener(onCreateListener);
        findViewById(R.id.btn_create_zip).setOnClickListener(onCreateListener);
    }

    View.OnClickListener onCreateListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            int permission = ActivityCompat.checkSelfPermission(ZipActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(ZipActivity.this, new String [] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x001);
            }

            if (id == R.id.btn_create_file) {
                createFile();
            } else if (id == R.id.btn_create_zip){
                createZip();
            }
        }
    };

    private void createFile() {
        FileUtils.getInstance().setFolderName(SAMPLE_DIRECTORY);
        FileUtils.getInstance().createFileDirectory();

        try {
            if (FileUtils.getInstance().isDirectory()) {
                File file = new File(FileUtils.getInstance().getFileAbsolutePath(), SAMPLE_FILE);
                FileOutputStream fos = new FileOutputStream(file);
                String msg = "This file is a sample code message.";
                fos.write(msg.getBytes());
                fos.close();
            }

            Toast.makeText(this, "The Sample folder has been created.", Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createZip() {
        try {
            String folderPath = FileUtils.getInstance().getFileAbsolutePath(); /// sample Folder
            String output = folderPath + ".zip";
            FileUtils.getInstance().zip(folderPath, output);

            Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +SAMPLE_FILE);
            openFile(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this, "Please press create File first.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openFile(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/*");
        startActivity(Intent.createChooser(intent, "open"));
    }


}
