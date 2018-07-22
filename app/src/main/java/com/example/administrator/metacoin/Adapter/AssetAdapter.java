package com.example.administrator.metacoin.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.administrator.metacoin.Model.Asset;
import com.example.administrator.metacoin.Model.Coin;
import com.example.administrator.metacoin.R;
import com.example.administrator.metacoin.Util.AppData;

import java.util.ArrayList;
import java.util.List;

public class AssetAdapter extends RecyclerView.Adapter<AssetAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater mInflater;
    private EventListener mListener;
    private List<Asset> listData = new ArrayList<>();

    // data is passed into the constructor
    public AssetAdapter(Context context, List<Asset> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.listData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_assets, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.txt_asset_name.setText(listData.get(position).address);
        holder.txt_asset_balance.setText(AppData.coin.name + " : " + String.format("%.6f", (float)listData.get(position).quantity/Math.pow(10.0f, listData.get(position).decimal_number)));
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return listData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_asset_name;
        TextView txt_asset_balance;
        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            txt_asset_name = itemView.findViewById(R.id.txt_asset_name);
            txt_asset_balance = itemView.findViewById(R.id.txt_asset_balance);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) mListener.onItemClick(view, getAdapterPosition());
        }
    }

    // allows clicks events to be caught
    public void setEventListener(EventListener itemHikeListener) {
        this.mListener = itemHikeListener;
    }

    // parent activity will implement this method to respond to click events
    public interface EventListener {
        void onItemClick(View view, int position);
    }
}
