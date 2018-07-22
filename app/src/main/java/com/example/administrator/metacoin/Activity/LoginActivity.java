package com.example.administrator.metacoin.Activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.metacoin.R;
import com.example.administrator.metacoin.Util.AppData;
import com.example.administrator.metacoin.Util.PreferenceUtil;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class LoginActivity extends AppCompatActivity {
    private static final int REQUEST_FILE_SELECT = 95;
    private static final int REQUEST_CODE_QR_SCAN = 96;
    private static final int REQUEST_CAMERA_CODE = 97;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ZXingLibrary.initDisplayOpinion(this);
        setContentView(R.layout.activity_login);

        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[] {Manifest.permission.CAMERA}, REQUEST_CAMERA_CODE);
        }

        if (ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.VIBRATE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(LoginActivity.this, new String[] {Manifest.permission.VIBRATE}, REQUEST_CAMERA_CODE);
        }

        if (PreferenceUtil.isSavedUser(LoginActivity.this)) {
            AppData.account = PreferenceUtil.getUserData(LoginActivity.this);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }

        setEventListener();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_FILE_SELECT && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                InputStream is = getContentResolver().openInputStream(uri);
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                String json = new String(buffer, StandardCharsets.UTF_8);
                JSONObject object = new JSONObject(json);
                String mnemonic = object.getString("mnemonic");

                Intent intent = new Intent(LoginActivity.this, LoginDetailsActivity.class);
                intent.putExtra("word", mnemonic);
                startActivity(intent);
                finish();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if(requestCode == REQUEST_CODE_QR_SCAN && resultCode == RESULT_OK) {
            if(data == null) return;
            Bundle bundle = data.getExtras();
            if (bundle == null) return;
            if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                String word = bundle.getString(CodeUtils.RESULT_STRING);

                Intent intent = new Intent(LoginActivity.this, LoginDetailsActivity.class);
                intent.putExtra("word", word);

                startActivity(intent);
                finish();
            } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setMessage("Failed");
                builder.create().show();
            }
        }
    }

    private void setEventListener() {
        findViewById(R.id.btn_open_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickOpenFile();
            }
        });
//        findViewById(R.id.btn_qrcode_scan).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onClickQRCodeScan();
//            }
//        });
    }

    private void onClickQRCodeScan() {
        Intent intent = new Intent(LoginActivity.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_QR_SCAN);
    }

    private void onClickOpenFile() {
        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select a file"), REQUEST_FILE_SELECT);
    }
}
