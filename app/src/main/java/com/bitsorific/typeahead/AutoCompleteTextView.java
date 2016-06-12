package com.bitsorific.typeahead;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

/**
 * Created by nischal on 6/11/16.
 */
public class AutoCompleteTextView extends android.support.v7.widget.AppCompatAutoCompleteTextView{
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

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs)
    {
        InputConnection conn = super.onCreateInputConnection(outAttrs);
        outAttrs.imeOptions &= ~EditorInfo.IME_FLAG_NO_ENTER_ACTION;
        return conn;
    }


}
