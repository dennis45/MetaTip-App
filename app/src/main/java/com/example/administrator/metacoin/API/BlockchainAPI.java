package com.example.administrator.metacoin.API;

import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.administrator.metacoin.Model.Account;
import com.example.administrator.metacoin.Model.Address;
import com.example.administrator.metacoin.Model.Asset;
import com.example.administrator.metacoin.Model.Attachment;
import com.example.administrator.metacoin.Model.Balance;
import com.example.administrator.metacoin.Model.Input;
import com.example.administrator.metacoin.Model.Output;
import com.example.administrator.metacoin.Model.Transaction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BlockchainAPI {
    private static final String baseUrl                     = "http://127.0.0.1:8820/";
    private static final String apiUrl                      = baseUrl;

    public interface BaseInterface {
        void onSuccess();
        void onError(String error);
    }

    public interface AccountInterface {
        void onSuccess(Account account);
        void onError(String error);
    }

    public interface BalanceInterface {
        void onSuccess(Balance balance);
        void onError(String error);
    }

    public interface NumberInterface {
        void onSuccess(int number);
        void onError(String error);
    }

    public interface AddressInterface {
        void onSuccess(List<Address> addresses);
        void onError(String error);
    }

    public interface AssetInterface {
        void onSuccess(List<Asset> assets);
        void onError(String error);
    }

    public interface TransactionInterface {
        void onSuccess(List<Transaction> transactions);
        void onError(String error);
    }


    public static void importaccount(Context context, String word, String name, final String password, final AccountInterface accountInterface) {
        final KProgressHUD hud = KProgressHUD.create(context);
        hud.show();
        String json = String.format("{\"jsonrpc\":\"2.0\",\"method\":\"importaccount\",\n" +
                "\"params\":[%s, \n" +
                "\t{\"accountname\":\"%s\",\"password\":\"%s\",\"hd_index\":%d,\"language\":\"en\"}\n" +
                "],\"id\":9}", word, name, password, 3);
        try {
            JSONObject object = new JSONObject(json);
            AndroidNetworking.post(apiUrl)
                    .addJSONObjectBody(object)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            hud.dismiss();
                            try {
                                if (response.has("error")) {
                                    JSONObject error = response.getJSONObject("error");
                                    accountInterface.onError(error.getString("message"));
                                } else {
                                    JSONObject result = response.getJSONObject("result");
                                    Account account = new Account();

                                    account.name = result.getString("name");
                                    account.password = password;
                                    account.mnemonic = result.getString("mnemonic");
                                    account.hd_index = result.getInt("hd_index");

                                    JSONArray addresses = result.getJSONArray("addresses");
                                    for (int i = 0; i < addresses.length(); i++) {
                                        Address addressObject = new Address();
                                        addressObject.name = (String)addresses.get(i);
                                        account.addresses.add(addressObject);
                                    }

                                    accountInterface.onSuccess(account);
                                }
                            } catch (Exception e) {
                                accountInterface.onError("Json parse error");
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            hud.dismiss();
                            accountInterface.onError(anError.toString());
                        }
                    });
        } catch (JSONException e) {
            hud.dismiss();
            e.printStackTrace();
            accountInterface.onError("Password incorrect");
        }
    }

    public static void getAddresses(Context context, String name, String password, final AddressInterface addressInterface) {
        String json = String.format("{\"jsonrpc\":\"2.0\",\"method\":\"listaddresses\",\n" +
                "\"params\":[\"%s\", \"%s\"],\"id\":11}", name, password);
        try {
            JSONObject object = new JSONObject(json);
            AndroidNetworking.post(apiUrl)
                    .addJSONObjectBody(object)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.has("error")) {
                                    JSONObject error = response.getJSONObject("error");
                                    addressInterface.onError(error.getString("message"));
                                } else {
                                    JSONObject result = response.getJSONObject("result");
                                    List<Address> addressList = new ArrayList<>();
                                    JSONArray addresses = result.getJSONArray("addresses");
                                    for (int i = 0; i < addresses.length(); i++) {
                                        Address addressObject = new Address();
                                        addressObject.name = (String)addresses.get(i);
                                        addressList.add(addressObject);
                                    }
                                    addressInterface.onSuccess(addressList);
                                }
                            } catch (Exception e) {
                                addressInterface.onError("Json parse error");
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            addressInterface.onError(anError.toString());
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
            addressInterface.onError("Json parse error");
        }
    }

    public static void getAccountAsset(Context context, String name, String password, String symbol, final AssetInterface assetInterface) {
        String json = String.format("{\n" +
                "    \"id\":25,\n" +
                "    \"jsonrpc\":\"2.0\",\n" +
                "    \"method\":\"getaccountasset\",\n" +
                "    \"params\":[\n" +
                "        \"%s\",\n" +
                "        \"%s\",\n" +
                "        \"%s\"\n" +
                "    ]\n" +
                "}", name, password, symbol);
        try {
            JSONObject object = new JSONObject(json);
            AndroidNetworking.post(apiUrl)
                    .addJSONObjectBody(object)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.has("error")) {
                                    JSONObject error = response.getJSONObject("error");
                                    assetInterface.onError(error.getString("message"));
                                } else {
                                    JSONObject result = response.getJSONObject("result");
                                    List<Asset> assetList = new ArrayList<>();
                                    JSONArray assets = result.getJSONArray("assets");
                                    if (assets != null) {
                                        for (int i = 0; i < assets.length(); i++) {
                                            JSONObject assetObject = (JSONObject)assets.get(i);
                                            Asset asset = new Asset();
                                            asset.address = assetObject.getString("address");
                                            asset.decimal_number = assetObject.getInt("decimal_number");
                                            asset.quantity = assetObject.getInt("quantity");
                                            asset.status = assetObject.getString("status");
                                            asset.symbol = assetObject.getString("symbol");
                                            assetList.add(asset);
                                        }
                                    }
                                    assetInterface.onSuccess(assetList);
                                }
                            } catch (Exception e) {
                                assetInterface.onSuccess(new ArrayList<Asset>());
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            assetInterface.onError(anError.toString());
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
            assetInterface.onError("Json parse error");
        }
    }

    public static void getAddressAsset(Context context, String address, final AssetInterface assetInterface) {
        String json = String.format("{\"jsonrpc\":\"2.0\",\"method\":\"getaddressasset\",\n" +
                "\"params\":[\"%s\"],\"id\":38}", address);
        try {
            JSONObject object = new JSONObject(json);
            AndroidNetworking.post(apiUrl)
                    .addJSONObjectBody(object)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.has("error")) {
                                    JSONObject error = response.getJSONObject("error");
                                    assetInterface.onError(error.getString("message"));
                                } else {
                                    JSONObject result = response.getJSONObject("result");
                                    List<Asset> assetList = new ArrayList<>();
                                    JSONArray assets = result.getJSONArray("assets");
                                    if (assets != null) {
                                        for (int i = 0; i < assets.length(); i++) {
                                            JSONObject assetObject = (JSONObject)assets.get(i);
                                            Asset asset = new Asset();
                                            asset.address = assetObject.getString("address");
                                            asset.decimal_number = assetObject.getInt("decimal_number");
                                            asset.quantity = assetObject.getInt("quantity");
                                            asset.status = assetObject.getString("status");
                                            asset.symbol = assetObject.getString("symbol");
                                            assetList.add(asset);
                                        }
                                    }
                                    assetInterface.onSuccess(assetList);
                                }
                            } catch (Exception e) {
                                assetInterface.onSuccess(new ArrayList<Asset>());
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            assetInterface.onError(anError.toString());
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
            assetInterface.onError("Json parse error");
        }
    }

    public static void sendFrom(Context context, String name, String password, String fromAddress, String toAddress, int amount, final BaseInterface baseInterface) {
        String json = String.format("{\n" +
                        "    \"id\":125,\n" +
                        "    \"jsonrpc\":\"2.0\",\n" +
                        "    \"method\":\"sendfrom\",\n" +
                        "    \"params\":[\n" +
                        "        \"%s\",\n" +
                        "        \"%s\",\n" +
                        "        \"%s\",\n" +
                        "        \"%s\",\n" +
                        "        \"%d\"\n" +
                        "    ]\n" +
                        "}",
                name, password, fromAddress, toAddress, amount);
        try {
            JSONObject object = new JSONObject(json);
            AndroidNetworking.post(apiUrl)
                    .addJSONObjectBody(object)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.has("error")) {
                                    JSONObject error = response.getJSONObject("error");
                                    baseInterface.onError(error.getString("message"));
                                } else {
                                    baseInterface.onSuccess();
                                }
                            } catch (Exception e) {
                                baseInterface.onError(e.toString());
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            baseInterface.onError(anError.toString());
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
            baseInterface.onError("Json parse error");
        }
    }

    public static void sendAssetFrom(Context context, String name, String password, String fromAddress, String toAddress, String symbol, int amount, final BaseInterface baseInterface) {
        String json = String.format("{\"jsonrpc\":\"2.0\",\"method\":\"sendassetfrom\",\n" +
                "\"params\":[\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", \"%d\"],\"id\":7}",
                name, password, fromAddress, toAddress, symbol, amount);
        try {
            JSONObject object = new JSONObject(json);
            AndroidNetworking.post(apiUrl)
                    .addJSONObjectBody(object)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.has("error")) {
                                    JSONObject error = response.getJSONObject("error");
                                    baseInterface.onError(error.getString("message"));
                                } else {
                                    baseInterface.onSuccess();
                                }
                            } catch (Exception e) {
                                baseInterface.onError(e.toString());
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            baseInterface.onError(anError.toString());
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
            baseInterface.onError("Json parse error");
        }
    }

    public static void getTransaction(Context context, String name, String password, int height, final TransactionInterface transactionInterface) {
        String json = String.format("{\"jsonrpc\":\"2.0\",\"method\":\"listtxs\",\n" +
                "   \"params\":[\"%s\", \"%s\",{\"height\":\"0:%d\"}],\"id\":31}", name, password, height);
        try {
            JSONObject object = new JSONObject(json);
            AndroidNetworking.post(apiUrl)
                    .addJSONObjectBody(object)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.has("error")) {
                                    JSONObject error = response.getJSONObject("error");
                                    int code = error.getInt("code");
                                    if (code == 2003) {
                                        transactionInterface.onSuccess(new ArrayList<Transaction>());
                                    } else {
                                        transactionInterface.onError(error.getString("message"));
                                    }
                                } else {
                                    JSONObject result = response.getJSONObject("result");
                                    List<Transaction> transactionList = new ArrayList<>();
                                    JSONArray transactions = result.getJSONArray("transactions");

                                    for (int i = 0; i < transactions.length(); i++) {
                                        JSONObject transactionObject = (JSONObject)transactions.get(i);

                                        Transaction transaction = new Transaction();
                                        transaction.direction = transactionObject.getString("direction");
                                        transaction.hash = transactionObject.getString("hash");
                                        transaction.height = transactionObject.getInt("height");

                                        JSONArray inputs = transactionObject.getJSONArray("inputs");
                                        for (int j = 0; j < inputs.length(); j++) {
                                            JSONObject inputObject = (JSONObject)inputs.get(j);
                                            Input input = new Input();
                                            input.address = inputObject.getString("address");
                                            input.script = inputObject.getString("script");
                                            transaction.inputs.add(input);
                                        }

                                        JSONArray outputs = transactionObject.getJSONArray("outputs");
                                        for (int j = 0; j < outputs.length(); j++) {
                                            JSONObject outputObject = (JSONObject)outputs.get(j);
                                            Output output = new Output();
                                            output.address = outputObject.getString("address");

                                            JSONObject attachmentObject = outputObject.getJSONObject("attachment");
                                            Attachment attachment = new Attachment();
                                            attachment.type = attachmentObject.getString("type");
                                            if (!attachment.type.equals("etp")) {
                                                attachment.decimal_number = attachmentObject.getInt("decimal_number");
                                                attachment.quantity = attachmentObject.getInt("quantity");
                                                attachment.symbol = attachmentObject.getString("symbol");
                                            }

                                            output.attachment = attachment;
                                            output.etp_value = outputObject.getInt("etp-value");
                                            output.locked_height_range = outputObject.getInt("locked_height_range");
                                            output.own = outputObject.getBoolean("own");
                                            output.script = outputObject.getString("script");

                                            transaction.outputs.add(output);
                                        }
                                        transaction.timestamp = transactionObject.getInt("timestamp");

                                        transactionList.add(transaction);
                                    }
                                    transactionInterface.onSuccess(transactionList);
                                }
                            } catch (Exception e) {
                                transactionInterface.onError("Json parse error");
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            transactionInterface.onError(anError.toString());
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
            transactionInterface.onError("Json parse error");
        }
    }

    public static void getETPBalance(Context context, String name, final String password, final BalanceInterface balanceInterface) {
        String json = String.format("{\n" +
                "    \"id\":25,\n" +
                "    \"jsonrpc\":\"2.0\",\n" +
                "    \"method\":\"getbalance\",\n" +
                "    \"params\":[\n" +
                "        \"%s\",\n" +
                "        \"%s\"\n" +
                "    ]\n" +
                "}", name, password);
        try {
            JSONObject object = new JSONObject(json);
            AndroidNetworking.post(apiUrl)
                    .addJSONObjectBody(object)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.has("error")) {
                                    JSONObject error = response.getJSONObject("error");
                                    balanceInterface.onError(error.getString("message"));
                                } else {
                                    JSONObject result = response.getJSONObject("result");

                                    Balance balance = new Balance();

                                    balance.total_available = result.getInt("total-available");
                                    balance.total_confirmed = result.getInt("total-confirmed");
                                    balance.total_frozen = result.getInt("total-frozen");
                                    balance.total_received = result.getInt("total-received");
                                    balance.total_unspent = result.getInt("total-unspent");
                                    balanceInterface.onSuccess(balance);
                                }
                            } catch (Exception e) {
                                balanceInterface.onError("Json parse error");
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            balanceInterface.onError(anError.toString());
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
            balanceInterface.onError("Json parse error");
        }
    }

    public static void getHeight(Context context, final NumberInterface numberInterface) {
        String json = String.format("{\n" +
                "    \"id\":25,\n" +
                "    \"jsonrpc\":\"2.0\",\n" +
                "    \"method\":\"getheight\",\n" +
                "    \"params\":[]\n" +
                "}");
        try {
            JSONObject object = new JSONObject(json);
            AndroidNetworking.post(apiUrl)
                    .addJSONObjectBody(object)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                if (response.has("error")) {
                                    JSONObject error = response.getJSONObject("error");
                                    numberInterface.onError(error.getString("message"));
                                } else {
                                    int ret = response.getInt("result");
                                    numberInterface.onSuccess(ret);
                                }
                            } catch (Exception e) {
                                numberInterface.onError("Json parse error");
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            numberInterface.onError(anError.toString());
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
            numberInterface.onError("Json parse error");
        }
    }
}
