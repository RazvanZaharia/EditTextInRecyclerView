package com.example.razvan.edittextinrecyclerview.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.razvan.edittextinrecyclerview.R;
import com.example.razvan.edittextinrecyclerview.model.Rate;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class RvAdapterRates extends RecyclerView.Adapter<RvAdapterRates.RateViewHolder> {

    private Context mContext;
    private List<Rate> mDataSet;

    public RvAdapterRates(Context context) {
        mContext = context;
        mDataSet = new ArrayList<>();
    }

    @NonNull
    @Override
    public RateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RateViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_currency_rate, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RateViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }

    public void setDataSet(@NonNull List<Rate> dataSet) {
        mDataSet.clear();
        mDataSet.addAll(dataSet);
    }

    public class RateViewHolder extends RecyclerView.ViewHolder {

        public RateViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(Rate rate) {

        }

    }
}