package com.asmita.utils;

import android.content.Context;
import android.support.v7.content.res.AppCompatResources;
import android.util.AttributeSet;
import android.widget.NumberPicker;
import com.asmita.activities.R;
import java.lang.reflect.Field;

public class NumberPickerWithColor extends NumberPicker {

    public NumberPickerWithColor(Context context, AttributeSet attrs) {
        super(context, attrs);

        Class<?> numberPickerClass = null;
        try {
            numberPickerClass = Class.forName("android.widget.NumberPicker");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Field selectionDivider = null;
        try {
            if (numberPickerClass != null)
            {
                selectionDivider = numberPickerClass.getDeclaredField("mSelectionDivider");
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        try {
            if (selectionDivider != null) {
                selectionDivider.setAccessible(true);
                selectionDivider.set(this, AppCompatResources.getDrawable(context, R.drawable.picker_divider_color));
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
