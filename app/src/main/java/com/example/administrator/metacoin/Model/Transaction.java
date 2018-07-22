package com.example.administrator.metacoin.Model;

import java.util.ArrayList;
import java.util.List;

public class Transaction {
    public String direction;
    public String hash;
    public int height;
    public List<Input> inputs = new ArrayList<>();
    public List<Output> outputs = new ArrayList<>();
    public int timestamp;

    public Transaction() {
        direction = "";
        hash = "";
        height = 0;
        inputs = new ArrayList<>();
        outputs = new ArrayList<>();
        timestamp = 0;
    }
}
