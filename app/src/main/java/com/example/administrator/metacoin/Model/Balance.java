package com.example.administrator.metacoin.Model;

public class Balance {
    public float total_available;
    public int total_confirmed;
    public int total_frozen;
    public int total_received;
    public int total_unspent;

    public Balance() {
        total_available = 0;
        total_confirmed = 0;
        total_frozen = 0;
        total_received = 0;
        total_unspent = 0;
    }
}
