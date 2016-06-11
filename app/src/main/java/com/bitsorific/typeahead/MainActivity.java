package com.bitsorific.typeahead;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.bitsorific.typeahead.databinding.ActivityMainBinding;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;
import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

    CompositeSubscription subscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        subscription = new CompositeSubscription();

        // Listen for events from search
        addSubscription(RxTextView.textChangeEvents(binding.searchEdit)
                .debounce(500, TimeUnit.MILLISECONDS)
                .compose(mainThread())
                .subscribe(this::handleTextChanges, error -> Timber.e(error.getMessage())));
    }

    private class CustomArrayAdapter extends ArrayAdapter<String> implements Filterable{

        private List<String> listResult;

        public CustomArrayAdapter(Context context, int resource) {
            super(context, resource);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    filterResults.values = listResult;
                    filterResults.count = listResult.size();
                    if (constraint != null) {
                        // Assign the data to the FilterResults
                        filterResults.values = listResult;
                        filterResults.count = listResult.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results.values != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else if(results == null){ // prevents dropdown from being cleared
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }

    }

    public void handleTextChanges(TextViewTextChangeEvent event){
        Timber.d("Text changing " + event.text().toString());

    }

    private void addSubscription(Subscription s) {
        subscription.add(s);
    }

    private static <T> Observable.Transformer<T, T> mainThread(){
        return observable ->
                observable.observeOn(AndroidSchedulers.mainThread()).subscribeOn(AndroidSchedulers.mainThread());
    }
}
