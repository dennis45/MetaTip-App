package com.example.administrator.metacoin.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.metacoin.API.BlockchainAPI;
import com.example.administrator.metacoin.Adapter.AssetAdapter;
import com.example.administrator.metacoin.Model.Address;
import com.example.administrator.metacoin.Model.Asset;
import com.example.administrator.metacoin.R;
import com.example.administrator.metacoin.Util.AppData;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SendActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_QR_SCAN = 96;

    private EditText edit_address;
    private EditText edit_amount;
    private EditText edit_source_address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ZXingLibrary.initDisplayOpinion(this);
        setContentView(R.layout.activity_send);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bindView();
        setEventListener();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_QR_SCAN && resultCode == RESULT_OK) {
            if(data == null) return;
            Bundle bundle = data.getExtras();
            if (bundle == null) return;
            if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                edit_address.setText(bundle.getString(CodeUtils.RESULT_STRING));
            } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
            }
        }
    }

    private void setEventListener() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        findViewById(R.id.btn_qrcode_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickQRCodeScan();
            }
        });
        findViewById(R.id.btn_next).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickNext();
            }
        });
        findViewById(R.id.btn_select_source).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSelectSource();
            }
        });
    }

    private void onClickSelectSource() {
        final CharSequence[] items = new CharSequence[AppData.account.addresses.size()];
        for (int i = 0; i < AppData.account.addresses.size(); i++) items[i] = AppData.account.addresses.get(i).name;

        new AlertDialog.Builder(SendActivity.this)
                .setSingleChoiceItems(items, 0, null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog)dialog).getListView().getCheckedItemPosition();
                        edit_source_address.setText(items[selectedPosition].toString());
                    }
                })
                .show();
    }

    private void onClickNext() {
        String address = edit_address.getText().toString();
        if (address.isEmpty()) {
            Toast.makeText(SendActivity.this, "Please input address", Toast.LENGTH_SHORT).show();
            return;
        }
        String amount = edit_amount.getText().toString();
        if (amount.isEmpty()) {
            Toast.makeText(SendActivity.this, "Please input amount", Toast.LENGTH_SHORT).show();
            return;
        }
        String source_address = edit_source_address.getText().toString();
        if (source_address.isEmpty()) {
            Toast.makeText(SendActivity.this, "Please input source address", Toast.LENGTH_SHORT).show();
            return;
        }

        int amount_val = 0;
        final KProgressHUD hud = KProgressHUD.create(SendActivity.this);
        hud.show();

        if (AppData.coin.name.equals("ETP")) {
            amount_val = (int)(Float.parseFloat(amount) * Math.pow(10.f, 8));
            BlockchainAPI.sendFrom(SendActivity.this, AppData.account.name, AppData.account.password, source_address, address,
                    amount_val, new BlockchainAPI.BaseInterface() {
                        @Override
                        public void onSuccess() {
                            hud.dismiss();
                            Toast.makeText(SendActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            hud.dismiss();
                            Toast.makeText(SendActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
        } else if (AppData.coin.name.equals("META")) {
            amount_val = (int)(Float.parseFloat(amount));
            BlockchainAPI.sendAssetFrom(SendActivity.this, AppData.account.name, AppData.account.password, source_address, address,
                    AppData.coin.name, amount_val, new BlockchainAPI.BaseInterface() {
                        @Override
                        public void onSuccess() {
                            hud.dismiss();
                            Toast.makeText(SendActivity.this, "Sent", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(String error) {
                            hud.dismiss();
                            Toast.makeText(SendActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void onClickQRCodeScan() {
        Intent intent = new Intent(SendActivity.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE_QR_SCAN);
    }

    private void bindView() {
        edit_address = findViewById(R.id.edit_address);
        edit_amount = findViewById(R.id.edit_amount);
        edit_source_address = findViewById(R.id.edit_source_address);
    }
}
