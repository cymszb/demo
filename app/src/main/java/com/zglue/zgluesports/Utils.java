package com.zglue.zgluesports;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

/**
 * Created by yuancui on 12/4/17.
 */

public class Utils {
    //start activity with a safe schedule
    public static  boolean startActivitySafely(Context context, Intent intent){
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
