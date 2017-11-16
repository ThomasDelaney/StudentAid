package com.example.teohe.studentaid;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by TeoHe on 15/11/2017.
 */

public class UserChecker
{
    //using shared preferences to see if user has already set up profile, this will be quicker then checking database
    //for user profile
    private SharedPreferences currentPreferences;

    public int getUser(Context context)
    {
        currentPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return currentPreferences.getInt("logged", 0);
    }

    public void setUser(Context context)
    {
        currentPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = currentPreferences.edit();
        editor.putInt("logged", 1);
        editor.apply();
    }
}
