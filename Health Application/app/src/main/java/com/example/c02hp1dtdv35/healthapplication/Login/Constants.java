package com.example.c02hp1dtdv35.healthapplication.Login;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.c02hp1dtdv35.healthapplication.R;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class Constants {
    static File cacheDir;
    public static final String BASE_URL = "http://ec2-13-57-182-205.us-west-1.compute.amazonaws.com:3000/api/";
    public static final String Image_URL = "http://";
    public static final String User_Profile_URL = "http://";
    public static final String MyPREFERENCES = "MyPrefs" ;
    public static String api_type = "android";
    public static String api_devicetoken = "cxzczxczxcxz";
    public static String Version = "1";

    public static String emailPattern = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
            + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
            + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
            + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
            + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";
    public static int isLegalPassword(String pass) {


        if (!pass.matches(".*\\d.*")) return 3;

        if (!pass.matches(".*[~!.......].*")) return 4;

        return 0;
    }

    public static boolean validatePassword(String password) {
        Pattern pattern;
        Matcher matcher;
        String PASSWORD_PATTERN = "^(?=.*?[0-9])(?=.*?[#?!@$%^&*-]).{7,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }
    public static String PASSWORD_PATTERN =
            "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%]).{6,15})";

    public static boolean checkEmail(String email) {
        return Constants.EMAIL_ADDRESS_PATTERN.matcher(email).matches();
    }
    public static Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );
    public static String DEVICE_TOKEN="";

    public static void hidekeyboard(Activity activity, View v) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

    }

    public static void hideSoftKeyBoardOnTabClicked(Context context,View v) {
        if (v != null && context != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public  static Dialog get_dialog(Context context, int layoutid)
    {
        Dialog dialog = new  Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layoutid);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT);
        // dialog.getWindow().setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(R.color.transparent)));
        return dialog;
    }
    public static void snackbar(Context context, LinearLayout layout, String msg)
    {
        Snackbar snackbar = Snackbar
                .make(layout, ""+msg, Snackbar.LENGTH_LONG);
        snackbar.getView().setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        View sbView = snackbar.getView();
        //sbView.setBackgroundColor(context.getResources().getColor(R.color.light_blue));
        TextView tv = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        //  tv.setTextColor(context.getResources().getColor(R.color.white));
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
       /* LinearLayout.LayoutParams layoutParams= new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        sbView.setLayoutParams(layoutParams);*/
        snackbar.show();

    }

    public static void SpacialCharacterNotAllow(EditText editText) {
        if (editText.getText().toString().length() > 0) {

            char x;
            int[] t = new int[editText.getText().toString()
                    .length()];

            for (int i = 0; i < editText.getText().toString()
                    .length(); i++) {
                x = editText.getText().toString().charAt(i);
                int z = (int) x;
                t[i] = z;

                if ((z > 47 && z < 58) || (z >= 64 && z < 91)
                        || (z > 96 && z < 123) || z==33 || z==35) {

                } else {

                    editText.setText(editText
                            .getText()
                            .toString()
                            .substring(
                                    0,
                                    (editText.getText()
                                            .toString().length()) - 1));
                    editText.setSelection(editText
                            .getText().length());
                }


            }
        }
    }

    public static String convert_object_string(Object obj){
        Gson gson = new Gson();
        String json = gson.toJson(obj); // myObject - instance of MyObject
        return json;
    }

    public static boolean isInternetAvailable(Context ctx) {
        ConnectivityManager check = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (check != null) {
            NetworkInfo[] info = check.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                        //Toast.makeText(context, "Internet is connected", Toast.LENGTH_SHORT).show();
                    }
        }
        return false;
    }

    public static String formatDate(String date, String initDateFormat, String endDateFormat) throws ParseException {
        Date initDate = new SimpleDateFormat(initDateFormat).parse(date.trim());
        SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
        String parsedDate = formatter.format(initDate);
        return parsedDate;
    }

    public static void executeLogcat(Context context){


        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"HappyRoads");
        else
            cacheDir=context.getCacheDir();

        if(!cacheDir.exists())
            cacheDir.mkdirs();

        File logFile = new File(cacheDir, "logoutput.log"); // log file name
        int sizePerFile = 60; // size in kilobytes
        int rotationCount = 10; // file rotation count
        String filter = "D"; // Debug priority

        String[] args = new String[] { "logcat",
                "-v", "time",
                "-f",logFile.getAbsolutePath(),
                "-r", Integer.toString(sizePerFile),
                "-n", Integer.toString(rotationCount),
                "*:" + filter };

        try {
            Runtime.getRuntime().exec(args);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

