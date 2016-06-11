package com.bitsorific.typeahead;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by nischal on 6/11/16.
 */
public class AutoCompleteTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView {
    public AutoCompleteTextView(Context context) {
        super(context);
    }
    public AutoCompleteTextView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public AutoCompleteTextView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    @Override
    public void dismissDropDown() {}

    public void dismiss(){
        super.dismissDropDown();
    }

    @Override
    protected void replaceText(CharSequence text) {
        super.replaceText(text);
    }


}
