package com.example.administrator.metacoin.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.administrator.metacoin.Adapter.TransactionAdapter;
import com.example.administrator.metacoin.Model.Output;
import com.example.administrator.metacoin.Model.Transaction;
import com.example.administrator.metacoin.R;
import com.example.administrator.metacoin.Util.AppData;

import java.util.ArrayList;
import java.util.List;

public class TransactionActivity extends AppCompatActivity implements TransactionAdapter.EventListener {

    public RecyclerView recyclerView;
    List<Transaction> listTransactions = new ArrayList<>();
    TransactionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        bindView();
        setEventListener();
    }

    private void setEventListener() {
        findViewById(R.id.btn_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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

    private void setRecyclerView() {
        adapter = new TransactionAdapter(TransactionActivity.this, listTransactions);
        adapter.setEventListener(TransactionActivity.this);
        recyclerView.setLayoutManager(new LinearLayoutManager(TransactionActivity.this));
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
    }

    private void bindView() {
        recyclerView = findViewById(R.id.recyclerView);

    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
