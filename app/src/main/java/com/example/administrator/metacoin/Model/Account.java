package com.example.administrator.metacoin.Model;

import java.util.ArrayList;
import java.util.List;

public class Account {
    public String name;
    public String password;
    public String mnemonic;
    public int hd_index;
    public List<Address> addresses = new ArrayList<>();

    public Account() {
        name = "";
        password = "";
        mnemonic = "";
        hd_index = 0;
        addresses = new ArrayList<>();
    }
}
