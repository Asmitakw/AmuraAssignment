package com.asmita.utils;


import android.content.Context;
import android.view.inputmethod.InputMethodManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Utility
{
    public static void hideKeyboard(Context context)
    {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    // get current date and return in String format
    public static String getTodaysDate()
    {
        Calendar c = Calendar.getInstance();

        String datePattern = "dd-MMM-yyyy";

        SimpleDateFormat df = new SimpleDateFormat(datePattern, Locale.US);

        return df.format(c.getTime());
    }

    // get selected date and convert into format and return in String format

    public static String convertDateFormat(String strDate)
    {
        String actualDateFormat = "dd/MM/yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(actualDateFormat,Locale.US);

        String datePattern = "dd-MMM-yyyy";
        SimpleDateFormat formatterOut = new SimpleDateFormat(datePattern,Locale.US);

        try
        {
            Date date = formatter.parse(strDate);

            return formatterOut.format(date);

        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return "";
    }
}
