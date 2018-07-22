package com.example.administrator.metacoin.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.example.administrator.metacoin.API.BlockchainAPI;
import com.example.administrator.metacoin.Adapter.MainAdapter;
import com.example.administrator.metacoin.Model.Address;
import com.example.administrator.metacoin.Model.Asset;
import com.example.administrator.metacoin.Model.Attachment;
import com.example.administrator.metacoin.Model.Balance;
import com.example.administrator.metacoin.Model.Coin;
import com.example.administrator.metacoin.Model.Output;
import com.example.administrator.metacoin.Model.Transaction;
import com.example.administrator.metacoin.R;
import com.example.administrator.metacoin.Util.AppData;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  MainAdapter.EventListener {

    public RecyclerView recyclerView;
    List<Coin> listCoins = new ArrayList<>();
    MainAdapter adapter;

    // ETP info
    Balance etpBalance = new Balance();
    int etpTransactionCount;

    // MEAT info
    List<Asset> metaAssets = new ArrayList<>();
    Balance metaBalance = new Balance();
    int metaTransactionCount;

    // BTC info
    List<Asset> btcAssets = new ArrayList<>();
    Balance btcBalance = new Balance();
    int btcTransactionCount;

    KProgressHUD hud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        hud = KProgressHUD.create(MainActivity.this);

        recyclerView = findViewById(R.id.recyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        hud.show();
        getAddresses();
    }

    private void getTransactionCount() {
        etpTransactionCount = 0;
        metaTransactionCount = 0;
        btcTransactionCount = 0;
        for (Transaction transaction : AppData.transactionList) {
            if (transaction.direction.equals("send")) {
                for (Output output : transaction.outputs) {
                    if (output.own == false) {
                        if (output.attachment.type.equals("etp")) {
                            etpTransactionCount++;
                        } else {
                            if (output.attachment.symbol.equals("META")) {
                                metaTransactionCount++;
                            } else if (output.attachment.symbol.equals("BTC")) {
                                btcTransactionCount++;
                            }
                        }
                        break;
                    }
                }
            } else {
                for (Output output : transaction.outputs) {
                    if (output.own == true) {
                        if (output.attachment.type.equals("etp")) {
                            etpTransactionCount++;
                        } else {
                            if (output.attachment.symbol.equals("META")) {
                                metaTransactionCount++;
                            } else if (output.attachment.symbol.equals("BTC")) {
                                btcTransactionCount++;
                            }
                        }
                    }
                }
            }
        }
    }

    private void getAddresses() {
        BlockchainAPI.getAddresses(MainActivity.this, AppData.account.name, AppData.account.password, new BlockchainAPI.AddressInterface() {
            @Override
            public void onSuccess(List<Address> addresses) {
                AppData.account.addresses = addresses;
                getHeight();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                hud.dismiss();
            }
        });
    }

    private void getTransaction() {
        BlockchainAPI.getTransaction(MainActivity.this, AppData.account.name, AppData.account.password, AppData.height, new BlockchainAPI.TransactionInterface() {
            @Override
            public void onSuccess(List<Transaction> transactions) {
                AppData.transactionList = transactions;
                getTransactionCount();
                getETPBalance();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                hud.dismiss();
            }
        });
    }

    private void getHeight() {
        BlockchainAPI.getHeight(MainActivity.this, new BlockchainAPI.NumberInterface() {
            @Override
            public void onSuccess(int number) {
                AppData.height = number;
                getTransaction();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                hud.dismiss();
            }
        });
    }

    void getETPBalance() {
        BlockchainAPI.getETPBalance(MainActivity.this, AppData.account.name, AppData.account.password, new BlockchainAPI.BalanceInterface() {
            @Override
            public void onSuccess(Balance balance) {
                etpBalance = balance;
                etpBalance.total_available /= Math.pow(10.0f, 8);
                getMETAAsset();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                hud.dismiss();
            }
        });
    }

    void getMETAAsset() {
        BlockchainAPI.getAccountAsset(MainActivity.this, AppData.account.name, AppData.account.password, "META", new BlockchainAPI.AssetInterface() {
            @Override
            public void onSuccess(List<Asset> assets) {
                metaAssets = assets;
                metaBalance = new Balance();
                for (Asset asset : assets) {
                    metaBalance.total_available += (float)asset.quantity / Math.pow(10.0f, asset.decimal_number);
                }
                getBTCAsset();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                hud.dismiss();
            }
        });
    }

    void getBTCAsset() {
        BlockchainAPI.getAccountAsset(MainActivity.this, AppData.account.name, AppData.account.password, "BTC", new BlockchainAPI.AssetInterface() {
            @Override
            public void onSuccess(List<Asset> assets) {
                btcAssets = assets;
                btcBalance = new Balance();
                for (Asset asset : assets) {
                    btcBalance.total_available += (float)asset.quantity / Math.pow(10.0f, asset.decimal_number);
                }

                setRecyclerView();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                hud.dismiss();
            }
        });
    }

    private void setRecyclerView() {
        hud.dismiss();

        listCoins.clear();
        Coin coin_meta = new Coin();
        coin_meta.name = "META";
        coin_meta.image = R.mipmap.meta;
        coin_meta.balance = metaBalance.total_available;
        coin_meta.transaction_count = metaTransactionCount;
        listCoins.add(coin_meta);

        Coin coin_etp = new Coin();
        coin_etp.name = "ETP";
        coin_etp.image = R.mipmap.etp;
        coin_etp.balance = etpBalance.total_available;
        coin_etp.transaction_count = etpTransactionCount;
        listCoins.add(coin_etp);

        Coin coin_btc = new Coin();
        coin_btc.name = "BTC";
        coin_btc.image = R.mipmap.bitcoin;
        coin_btc.balance = btcBalance.total_available;
        coin_btc.transaction_count = btcTransactionCount;
        listCoins.add(coin_btc);

        adapter = new MainAdapter(MainActivity.this, listCoins);
        adapter.setEventListener(MainActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        AppData.coin = listCoins.get(position);
        startActivity(new Intent(MainActivity.this, CoinSummaryActivity.class));
    }
}
