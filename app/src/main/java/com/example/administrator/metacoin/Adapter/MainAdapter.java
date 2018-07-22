package com.example.administrator.metacoin.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.metacoin.Model.Coin;
import com.example.administrator.metacoin.R;

import java.util.ArrayList;
import java.util.List;

public class MainAdapter extends RecyclerView.Adapter<MainAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater mInflater;
    private EventListener mListener;
    private List<Coin> listData = new ArrayList<>();

    // data is passed into the constructor
    public MainAdapter(Context context, List<Coin> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.listData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_coin, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.img_coin.setImageResource(listData.get(position).image);
        holder.txt_name.setText(listData.get(position).name);
        holder.txt_available.setText(String.format("%.6f", listData.get(position).balance));
        holder.txt_transaction_count.setText("Number of transactions: " + listData.get(position).transaction_count);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return listData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView img_coin;
        TextView txt_name;
        TextView txt_available;
        TextView txt_transaction_count;
        public ViewHolder(View itemView) {
            super(itemView);
            img_coin = itemView.findViewById(R.id.img_coin);
            txt_name = itemView.findViewById(R.id.txt_name);
            txt_available = itemView.findViewById(R.id.txt_available);
            txt_transaction_count = itemView.findViewById(R.id.txt_transaction_count);
            itemView.setOnClickListener(this);
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
