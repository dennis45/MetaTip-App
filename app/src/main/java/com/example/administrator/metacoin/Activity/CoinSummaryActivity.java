package com.example.administrator.metacoin.Activity;

import android.content.Intent;
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

import com.example.administrator.metacoin.Adapter.MainAdapter;
import com.example.administrator.metacoin.Adapter.TransactionAdapter;
import com.example.administrator.metacoin.Model.Coin;
import com.example.administrator.metacoin.Model.Output;
import com.example.administrator.metacoin.Model.Transaction;
import com.example.administrator.metacoin.R;
import com.example.administrator.metacoin.Util.AppData;
import com.example.administrator.metacoin.Util.PreferenceUtil;

import java.util.ArrayList;
import java.util.List;

public class CoinSummaryActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, TransactionAdapter.EventListener {

    public TextView txt_name;
    public TextView txt_available;
    public TextView txt_transaction_count;
    public ImageView img_coin;

    private ImageView img_coin_nav;
    private TextView txt_name_nav;
    private TextView txt_asset_balance_nav;

    public RecyclerView recyclerView;
    List<Transaction> listTransactions = new ArrayList<>();
    TransactionAdapter adapter;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coinsummary);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        bindView();
    }

    private void bindView() {
        txt_name = findViewById(R.id.txt_name);
        txt_available = findViewById(R.id.txt_available);
        img_coin = findViewById(R.id.img_coin);
        txt_transaction_count = findViewById(R.id.txt_transaction_count);
        recyclerView = findViewById(R.id.recyclerView);

        txt_name.setText(AppData.coin.name);
        txt_available.setText(String.format("%.6f", AppData.coin.balance));
        img_coin.setImageResource(AppData.coin.image);
        txt_transaction_count.setText("Number of transactions: " + AppData.coin.transaction_count);

        View headerLayout = navigationView.getHeaderView(0);
        img_coin_nav = headerLayout.findViewById(R.id.img_coin_nav);
        txt_name_nav = headerLayout.findViewById(R.id.txt_name_nav);
        txt_asset_balance_nav = headerLayout.findViewById(R.id.txt_asset_balance_nav);

        txt_name_nav.setText(AppData.coin.name);
        img_coin_nav.setImageResource(AppData.coin.image);
        txt_asset_balance_nav.setText(String.format("%.6f", AppData.coin.balance));

        findViewById(R.id.btn_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CoinSummaryActivity.this, SendActivity.class));
            }
        });

        findViewById(R.id.btn_receive).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CoinSummaryActivity.this, ReceiveActivity.class));
            }
        });

        getCoinTransaction();
    }

    private void getCoinTransaction() {
        listTransactions.clear();
        for (Transaction transaction : AppData.transactionList) {
            if (transaction.direction.equals("send")) {
                boolean flag = false;
                for (Output output : transaction.outputs) {
                    if (output.own == false) {
                        if (output.attachment.type.equals("etp")) {
                            if (AppData.coin.name.equals("ETP")) flag = true;
                        } else {
                            if (AppData.coin.name.equals(output.attachment.symbol)) flag = true;
                        }
                        break;
                    }
                }
                if (flag) listTransactions.add(transaction);
            } else {
                boolean flag = false;
                for (Output output : transaction.outputs) {
                    if (output.own == true) {
                        if (output.attachment.type.equals("etp")) {
                            if (AppData.coin.name.equals("ETP")) flag = true;
                        } else {
                            if (AppData.coin.name.equals(output.attachment.symbol)) flag = true;
                        }
                        break;
                    }
                }
                if (flag) listTransactions.add(transaction);
            }
        }
        setRecyclerView();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_send) {
            startActivity(new Intent(CoinSummaryActivity.this, SendActivity.class));
        } else if (id == R.id.nav_receive) {
            startActivity(new Intent(CoinSummaryActivity.this, ReceiveActivity.class));
        } else if (id == R.id.nav_transaction) {
            startActivity(new Intent(CoinSummaryActivity.this, TransactionActivity.class));
        } else if (id == R.id.nav_signout) {
            PreferenceUtil.deleteSavedUser(CoinSummaryActivity.this);
            Intent intent = new Intent(CoinSummaryActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setRecyclerView() {
        adapter = new TransactionAdapter(CoinSummaryActivity.this, listTransactions);
        adapter.setEventListener(CoinSummaryActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(CoinSummaryActivity.this));
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
