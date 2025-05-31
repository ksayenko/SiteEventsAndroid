package com.honeywell.stevents;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;


import androidx.appcompat.widget.ListPopupWindow;

public class CustomSpinner extends androidx.appcompat.widget.AppCompatSpinner
{
    public CustomSpinner(Context context)
    {
        super(context);
    }

    public CustomSpinner(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }

    public CustomSpinner(Context context, AttributeSet attrs, int defStyle, int mode)
    {
        super(context, attrs, defStyle, mode);
    }

    public CustomSpinner(Context context, int mode)
    {
        super(context, mode);
    }

    @Override
    public boolean performClick()
    {
        boolean bClicked = super.performClick();

        try
        {
            Field mPopupField = androidx.appcompat.widget.AppCompatSpinner.class.getDeclaredField("mPopup");
            mPopupField.setAccessible(true);
            ListPopupWindow pop = (ListPopupWindow) mPopupField.get(this);
            ListView listview = pop.getListView();

            Field mScrollCacheField = View.class.getDeclaredField("mScrollCache");
            mScrollCacheField.setAccessible(true);
            Object mScrollCache = mScrollCacheField.get(listview);
            Field scrollBarField = mScrollCache.getClass().getDeclaredField("scrollBar");
            scrollBarField.setAccessible(true);
            Object scrollBar = scrollBarField.get(mScrollCache);
            Method method = scrollBar.getClass().getDeclaredMethod("setVerticalThumbDrawable", Drawable.class);
            method.setAccessible(true);
            method.invoke(scrollBar, getResources().getDrawable(R.drawable.scrollbar_style));


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return bClicked;
    }
}