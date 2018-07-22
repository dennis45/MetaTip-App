package com.example.administrator.metacoin.Activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.administrator.metacoin.API.BlockchainAPI;
import com.example.administrator.metacoin.Adapter.AssetAdapter;
import com.example.administrator.metacoin.Adapter.MainAdapter;
import com.example.administrator.metacoin.Model.Address;
import com.example.administrator.metacoin.Model.Asset;
import com.example.administrator.metacoin.Model.Coin;
import com.example.administrator.metacoin.R;
import com.example.administrator.metacoin.Util.AppData;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.ArrayList;
import java.util.List;

public class ReceiveActivity extends AppCompatActivity implements AssetAdapter.EventListener {

    ImageView img_qrcode;
    TextView txt_asset_name;

    RecyclerView recyclerView;
    AssetAdapter adapter;
    List<Asset> listAssets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bindView();
        setQRCode(AppData.account.addresses.get(0).name);
        setEventListener();
        getAssetBalance();
    }

    private void setQRCode(String name) {
        txt_asset_name.setText(name);
        try {
            QRCodeWriter writer = new QRCodeWriter();
            BitMatrix bitMatrix  = writer.encode(name, BarcodeFormat.QR_CODE, 300, 300);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            if(bmp != null) {
                img_qrcode.setImageBitmap(bmp);
            }
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void setEventListener() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void bindView() {
        img_qrcode = findViewById(R.id.img_qrcode);
        txt_asset_name = findViewById(R.id.txt_asset_name);
        recyclerView = findViewById(R.id.recyclerView);
    }

    private void setRecyclerView() {
        adapter = new AssetAdapter(ReceiveActivity.this, listAssets);
        adapter.setEventListener(ReceiveActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(ReceiveActivity.this));
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void getAssetBalance() {
        listAssets.clear();
        for (int i = 0; i < AppData.account.addresses.size(); i++) {
            Asset asset = new Asset();
            asset.address = AppData.account.addresses.get(i).name;
            listAssets.add(asset);
        }
        setRecyclerView();
        int i = 0;
        for (Address address : AppData.account.addresses) {
            final int index = i++;
            if (AppData.coin.equals("ETP")) {
            } else {
                BlockchainAPI.getAddressAsset(ReceiveActivity.this, address.name, new BlockchainAPI.AssetInterface() {
                    @Override
                    public void onSuccess(List<Asset> assets) {
                        for (Asset asset : assets) {
                            if (asset.symbol.equals(AppData.coin.name)) {
                                listAssets.set(index, asset);
                                setRecyclerView();
                            }
                        }
                    }

                    @Override
                    public void onError(String error) {
                    }
                });
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        setQRCode(listAssets.get(position).address);
    }
}
