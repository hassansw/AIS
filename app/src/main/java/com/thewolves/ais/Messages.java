package com.thewolves.ais;

import android.app.Application;
import android.support.design.widget.Snackbar;
import android.widget.Toast;

/**
 * Created by Hassan on 11/12/2016.
 */

public class Messages {

    public static void showToast(Application app, String msg){
        Toast.makeText(app.getApplicationContext(), msg , Toast.LENGTH_SHORT).show();
    }

    public static void showSnackBar(Application app, String msg){
        Snackbar.make( null, msg, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }
}
