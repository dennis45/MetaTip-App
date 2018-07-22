package com.example.administrator.metacoin.Model;

public class Output {
    public String address;
    public int etp_value;
    public Attachment attachment;
    public int locked_height_range;
    public boolean own;
    public String script;

    public Output() {
        address = "";
        etp_value = 0;
        attachment = new Attachment();
        locked_height_range = 0;
        own = false;
        script = "";
    }
}
