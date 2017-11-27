package com.example.baz.studentorganizer.utilities;

/**
 * Created by Baz on 24/11/2017.
 */


import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;


public class Validation extends AppCompatActivity {

    // check for empty field
    public static boolean validateFields(String name){
        if (TextUtils.isEmpty(name)) {
            return false;
        } else {
            return true;
        }
    }


    // check for valid email address
    public static boolean validateEmail(String string) {
        if (TextUtils.isEmpty(string) || !Patterns.EMAIL_ADDRESS.matcher(string).matches()) {
            return false;
        } else {
            return  true;
        }
    }





}
