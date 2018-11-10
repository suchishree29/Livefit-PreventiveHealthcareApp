package com.example.c02hp1dtdv35.healthapplication.Login;

import android.util.Log;


public class CustomLog {

    public static final boolean ENABLE_LOG =  true;




    public static void d(String tag, String msg) {

        if(ENABLE_LOG)
            Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {

        if(ENABLE_LOG)
            Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {

        if(ENABLE_LOG)
            Log.w(tag, msg);
    }



}