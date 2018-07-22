package com.example.administrator.metacoin.Util;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.administrator.metacoin.Model.Account;

public class PreferenceUtil {
    public static String MY_PREFS_NAME = "my_preferences";
    public static String savedUser = "savedUser";
    public static String name = "name";
    public static String password = "password";
    public static String mnemonic = "mnemonic";

    public static boolean isSavedUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(savedUser, false);
    }

    public static void saveUserData(Context context, Account account) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putString(name, account.name);
        editor.putString(password, account.password);
        editor.putString(mnemonic, account.mnemonic);
        editor.putBoolean(savedUser, true);
        editor.apply();
    }

    public static Account getUserData(Context context) {
        Account account = new Account();
        SharedPreferences preferences = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
        account.name = preferences.getString(name, "");
        account.password = preferences.getString(password, "");
        account.mnemonic = preferences.getString(mnemonic, "");
        return account;
    }

    public static void deleteSavedUser(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE).edit();
        editor.putBoolean(savedUser, false);
        editor.apply();
    }
}
