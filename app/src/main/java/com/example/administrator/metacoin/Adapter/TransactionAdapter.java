package com.example.administrator.metacoin.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.administrator.metacoin.Model.Coin;
import com.example.administrator.metacoin.Model.Output;
import com.example.administrator.metacoin.Model.Transaction;
import com.example.administrator.metacoin.R;
import com.example.administrator.metacoin.Util.AppData;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.ViewHolder> {
    private Context context;
    private LayoutInflater mInflater;
    private EventListener mListener;
    private List<Transaction> listData = new ArrayList<>();

    // data is passed into the constructor
    public TransactionAdapter(Context context, List<Transaction> data) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        this.listData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_transaction, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Transaction transaction = listData.get(position);
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis((long)transaction.timestamp * 1000);
        Date d = c.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat sdf_time = new SimpleDateFormat("HH:mm:ss a");

        holder.txt_date.setText(sdf.format(d));
        if (transaction.direction.equals("send")) {
            holder.txt_description.setText(AppData.coin.name + " sent");
            holder.img_direction.setImageResource(R.mipmap.transaction_send);
            for (Output output : transaction.outputs) {
                if (output.own == false) {
                    if (output.attachment.type.equals("etp")) {
                        holder.txt_amount.setText(String.format("%.6f", (float)output.etp_value/Math.pow(10.0f, 8)));
                        holder.txt_details.setText("ETP transaction, " + sdf_time.format(d));
                    } else {
                        holder.txt_amount.setText(String.format("%.6f", (float)output.attachment.quantity/Math.pow(10.0f, output.attachment.decimal_number)));
                        holder.txt_details.setText(output.attachment.symbol + " transaction, " + sdf_time.format(d));
                    }
                    break;
                }
            }
        } else {
            holder.txt_description.setText(AppData.coin.name + " received");
            holder.img_direction.setImageResource(R.mipmap.transaction_receive);
            for (Output output : transaction.outputs) {
                if (output.own == true) {
                    if (output.attachment.type.equals("etp")) {
                        holder.txt_amount.setText(String.format("%.6f", (float)output.etp_value/Math.pow(10.0f, 8)));
                        holder.txt_details.setText("ETP transaction, " + sdf_time.format(d));
                    } else {
                        holder.txt_amount.setText(String.format("%.6f", (float)output.attachment.quantity/Math.pow(10.0f, output.attachment.decimal_number)));
                        holder.txt_details.setText(output.attachment.symbol + " transaction, " + sdf_time.format(d));
                    }
                    break;
                }
            }
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return listData.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_date;
        ImageView img_direction;
        TextView txt_description;
        TextView txt_amount;
        TextView txt_details;
        public ViewHolder(View itemView) {
            super(itemView);
            txt_date = itemView.findViewById(R.id.txt_date);
            img_direction = itemView.findViewById(R.id.img_direction);
            txt_description = itemView.findViewById(R.id.txt_description);
            txt_amount = itemView.findViewById(R.id.txt_amount);
            txt_details = itemView.findViewById(R.id.txt_details);
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
