package com.example.razvan.edittextinrecyclerview.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
        private static final String TAG = "RateViewHolder";

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
                    publishNewRateValue(editRatePublisher, editable != null ? editable.toString() : null);
                }
            };

            itemView.setOnClickListener(view -> {
                mEtRateAmount.requestFocus();
            });
            mTvRateName.setText(mDisplayedRate.getName());
            mEtRateAmount.setText(String.valueOf(mOnRateListener.getValueForRate(mDisplayedRate.getName())));

            mEtRateAmount.setImeOptions(EditorInfo.IME_ACTION_DONE);
            mEtRateAmount.setOnEditorActionListener((view, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    hideSoftKeyboard(mEtRateAmount);
                    publishNewRateValue(editRatePublisher, mEtRateAmount.getText() != null ? mEtRateAmount.getText().toString() : null);
                    mEtRateAmount.clearFocus();
                    return true;
                }
                return false;
            });
            mEtRateAmount.setOnFocusChangeListener((view, hasFocus) -> {
                if (hasFocus) {
                    showSoftKeyboard(mEtRateAmount);
                    mOnBaseCurrencyChangesListener.onNewBaseCurrency(mDisplayedRate.getName(), mOnRateListener.getValueForRate(mDisplayedRate.getName()));
                    mCardItem.setCardElevation(8.0f);
                    mEtRateAmount.setSelection(mEtRateAmount.getText() != null ? mEtRateAmount.getText().length() : 0);
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

        private void publishNewRateValue(PublishSubject<Rate> editRatePublisher, String rateValueString) {
            if (rateValueString == null || rateValueString.length() == 0) {
                editRatePublisher.onNext(new Rate(mDisplayedRate.getName(), 0));
            } else {
                if (rateValueString.startsWith(".")) {
                    rateValueString = "0".concat(rateValueString);
                }

                Float rateValueFloat = 0.0f;
                try {
                    rateValueFloat = Float.parseFloat(rateValueString);
                } catch (NumberFormatException nfe) {
                    Log.e(TAG, "publishNewRateValue: ", nfe);
                }

                editRatePublisher.onNext(new Rate(mDisplayedRate.getName(), rateValueFloat));
            }
        }

        private void hideSoftKeyboard(View view) {
            InputMethodManager imm = (InputMethodManager) itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        private void showSoftKeyboard(View view) {
            InputMethodManager imm = (InputMethodManager) itemView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, 0);
        }
    }

    public interface OnRateListener {
        float getValueForRate(String rateName);
    }

    public interface OnBaseCurrencyChangesListener {
        void onNewBaseCurrency(String rateName, float currentRateValue);
    }

}
