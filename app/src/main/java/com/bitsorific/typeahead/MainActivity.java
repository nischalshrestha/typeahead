package com.bitsorific.typeahead;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.bitsorific.typeahead.databinding.ActivityMainBinding;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    CompositeSubscription subscription;
    ActivityMainBinding binding;
    private String previousText;
    private int currentCursor;
    private int previousCursor;
    private Filter.FilterListener filterListener;
    private boolean match = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        subscription = new CompositeSubscription();

        List<String> sampleData = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            sampleData.add("sample" + i);
        }

        CustomArrayAdapter<String> suggestions = new CustomArrayAdapter<>(getApplicationContext(), R.layout.dropdown);
        suggestions.addAll(sampleData);
        binding.searchEdit.setAdapter(suggestions);
        binding.searchEdit.setThreshold(1);
        filterListener = new Filter.FilterListener() {
            @Override
            public void onFilterComplete(int count) {
                if (count > 0) {
                    match = true;
                } else {
                    match = false;
                }
            }
        };
        // Listen for events from search
        addSubscription(RxTextView.textChangeEvents(binding.searchEdit)
                .debounce(200, TimeUnit.MILLISECONDS)
                .compose(mainThread())
                .subscribe(this::handleTextChanges, error -> Timber.e("Search" + error.getMessage())));
    }

    @Override
    protected void onDestroy() {
        subscription.unsubscribe();
        super.onDestroy();
    }

    /**
     * Custom ArrayAdapter to set span style on text as user types for the typeahead search
     * @param <String>
     */
    private class CustomArrayAdapter<String> extends ArrayAdapter<String> implements Filterable {

        public CustomArrayAdapter(Context context, int resource) {
            super(context, resource);
        }

        class ViewHolder{
            TextView text;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            ViewHolder holder;
            if(row == null) {
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                row = inflater.inflate(R.layout.dropdown, parent, false);

                holder = new ViewHolder();
                holder.text = (TextView) row.findViewById(R.id.text);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            String text = getItem(position);
            if(match && previousCursor <= text.toString().length()) {
                holder.text.setText(setTypeAhead(text.toString(), 0, previousCursor));
            } else {
                holder.text.setText(text.toString());
            }

            return row;
        }
    }

    /**
     * Sets a Bold Typeface on the selection portion of the given string
     * @param s
     * @param start
     * @param end
     * @return
     */
    public SpannableString setTypeAhead(CharSequence s, int start, int end) {
        SpannableString string = new SpannableString(s);
        string.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return string;
    }

    /**
     * Handles the text changes detected via RxTextView
     * @param event
     */
    public void handleTextChanges(TextViewTextChangeEvent event){
        String text = event.text().toString();
        if (text.equals(previousText)) {
            return;
        }
        currentCursor = event.view().getSelectionEnd();
        Timber.d("current: "+currentCursor);
        if(text == null || text.length() == 0){
//            Timber.d("Text dismissing suggestions");
            binding.searchEdit.dismiss();
        } else {
            ((CustomArrayAdapter) binding.searchEdit.getAdapter()).getFilter().filter(text, filterListener);
        }
        previousCursor = currentCursor;
        previousText = text;
    }

    private void addSubscription(Subscription s) {
        subscription.add(s);
    }

    private static <T> Observable.Transformer<T, T> mainThread(){
        return observable ->
                observable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(AndroidSchedulers.mainThread());
    }
}
