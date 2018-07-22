package com.example.administrator.metacoin.Activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.metacoin.API.BlockchainAPI;
import com.example.administrator.metacoin.Model.Account;
import com.example.administrator.metacoin.R;
import com.example.administrator.metacoin.Util.AppData;
import com.example.administrator.metacoin.Util.PreferenceUtil;

import org.apache.commons.ssl.OpenSSL;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;

public class LoginDetailsActivity extends AppCompatActivity {

//    private EditText edit_name;
    private EditText edit_password;

    private String word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_details);

        word = getIntent().getStringExtra("word");

        bindView();
        setEventListener();
    }

    private void bindView() {
//        edit_name       = findViewById(R.id.edit_name);
        edit_password   = findViewById(R.id.edit_password);
    }

    private void setEventListener() {
        findViewById(R.id.btn_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickOpen();
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void onClickOpen() {
        String name = "user_" + System.currentTimeMillis();
//        if (name.isEmpty()) {
//            Toast.makeText(LoginDetailsActivity.this, "Please input account name", Toast.LENGTH_SHORT).show();
//            return;
//        }
        String password = edit_password.getText().toString();
        if (password.isEmpty()) {
            Toast.makeText(LoginDetailsActivity.this, "Please input password", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            byte[] bytes = OpenSSL.decrypt("aes-256-cbc",  password.toCharArray(), word.getBytes(StandardCharsets.UTF_8));
            String mnemonic = new String(bytes, StandardCharsets.UTF_8);

            onLogin(name, password, mnemonic);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }

    private void onLogin(String name, String password, String mnemonic) {
        BlockchainAPI.importaccount(LoginDetailsActivity.this, mnemonic, name, password, new BlockchainAPI.AccountInterface() {
            @Override
            public void onSuccess(Account account) {
                AppData.account = account;
                PreferenceUtil.saveUserData(LoginDetailsActivity.this, account);
                startActivity(new Intent(LoginDetailsActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(LoginDetailsActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
