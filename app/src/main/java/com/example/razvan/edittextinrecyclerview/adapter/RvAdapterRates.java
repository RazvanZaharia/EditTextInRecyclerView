package com.example.razvan.edittextinrecyclerview.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.razvan.edittextinrecyclerview.R;
import com.example.razvan.edittextinrecyclerview.model.Rate;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import rx.subscriptions.CompositeSubscription;

public class RvAdapterRates extends RecyclerView.Adapter<RvAdapterRates.RateViewHolder> {

    private Context mContext;
    private List<Rate> mDataSet;
    private PublishSubject<Rate> mEditRatePublisher;
    private PublishSubject<Void> mRatesValuesChange;
    private PublishSubject<Float> mBaseValueChangesPublisher;
    private OnRateListener mOnRateListener;
    private OnBaseCurrencyChangesListener mOnBaseCurrencyChangesListener;

    public RvAdapterRates(Context context,
                          @NonNull OnRateListener onRateListener,
                          @NonNull OnBaseCurrencyChangesListener onBaseCurrencyChangesListener) {
        mContext = context;
        mOnRateListener = onRateListener;
        mOnBaseCurrencyChangesListener = onBaseCurrencyChangesListener;
        mDataSet = new ArrayList<>();
    }

    @NonNull
    @Override
    public RateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RateViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_currency_rate, parent, false),
                mOnRateListener,
                mOnBaseCurrencyChangesListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RateViewHolder holder, int position) {
        holder.bind(mDataSet.get(position), mEditRatePublisher, mBaseValueChangesPublisher, mRatesValuesChange);
    }

    @Override
    public int getItemCount() {
        return mDataSet != null ? mDataSet.size() : 0;
    }

    public void setDataSet(@NonNull List<Rate> dataSet) {
        mDataSet.clear();
        mDataSet.addAll(dataSet);
    }

    public List<Rate> getDataSet() {
        return mDataSet;
    }

    public void setEditRatePublisher(PublishSubject<Rate> editRatePublisher) {
        mEditRatePublisher = editRatePublisher;
    }

    public void setBaseValuePublisher(PublishSubject<Float> baseValueChangesPublisher) {
        mBaseValueChangesPublisher = baseValueChangesPublisher;
    }

    public void setRatesValuesChange(PublishSubject<Void> ratesValuesChange) {
        mRatesValuesChange = ratesValuesChange;
    }

    class RateViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_icon)
        ImageView mIvIcon;

        @BindView(R.id.tv_rate_name)
        TextView mTvRateName;

        @BindView(R.id.et_rate_amount)
        EditText mEtRateAmount;

        @BindView(R.id.card_item)
        CardView mCardItem;

        private Rate mDisplayedRate;
        private CompositeSubscription mSubscription;
        private OnRateListener mOnRateListener;
        private OnBaseCurrencyChangesListener mOnBaseCurrencyChangesListener;

        RateViewHolder(View itemView,
                       @NonNull OnRateListener onRateListener,
                       @NonNull OnBaseCurrencyChangesListener onBaseCurrencyChangesListener) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mOnRateListener = onRateListener;
            mOnBaseCurrencyChangesListener = onBaseCurrencyChangesListener;

        }

        void bind(final Rate rate,
                  PublishSubject<Rate> editRatePublisher,
                  PublishSubject<Float> baseValueChangesPublisher,
                  PublishSubject<Void> ratesValuesChanges) {

            mDisplayedRate = rate;

            if (mSubscription == null) {
                mSubscription = new CompositeSubscription();
            } else {
                mSubscription.clear();
            }

            TextWatcher mFocusedEditTextChangesListener = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable == null || editable.length() == 0) {
                        editRatePublisher.onNext(new Rate(mDisplayedRate.getName(), 0));
                    } else {
                        editRatePublisher.onNext(new Rate(mDisplayedRate.getName(), Float.parseFloat(editable.toString())));
                    }
                }
            };

            mTvRateName.setText(mDisplayedRate.getName());
            mEtRateAmount.setText(String.valueOf(mOnRateListener.getValueForRate(mDisplayedRate.getName())));

            mEtRateAmount.setImeOptions(EditorInfo.IME_ACTION_DONE);
            mEtRateAmount.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    editRatePublisher.onNext(new Rate(mDisplayedRate.getName(), Float.parseFloat(mEtRateAmount.getText().toString())));
                    return true;
                }
                return false;
            });
            mEtRateAmount.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    mOnBaseCurrencyChangesListener.onNewBaseCurrency(mDisplayedRate.getName(), mOnRateListener.getValueForRate(mDisplayedRate.getName()));
                    mCardItem.setCardElevation(8.0f);
                    mEtRateAmount.addTextChangedListener(mFocusedEditTextChangesListener);

                } else {
                    mEtRateAmount.removeTextChangedListener(mFocusedEditTextChangesListener);
                    mCardItem.setCardElevation(0.0f);
                }
            });

            mSubscription.add(baseValueChangesPublisher
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(baseValue -> {
                        if (!mEtRateAmount.hasFocus()) {
                            mEtRateAmount.setText(String.valueOf(mOnRateListener.getValueForRate(mDisplayedRate.getName())));
                        }
                    }));

            mSubscription.add(ratesValuesChanges
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(baseValue -> {
                        if (!mEtRateAmount.hasFocus()) {
                            mEtRateAmount.setText(String.valueOf(mOnRateListener.getValueForRate(mDisplayedRate.getName())));
                        }
                    }));
        }
    }

    public interface OnRateListener {
        float getValueForRate(String rateName);
    }

    public interface OnBaseCurrencyChangesListener {
        void onNewBaseCurrency(String rateName, float currentRateValue);
    }

}
