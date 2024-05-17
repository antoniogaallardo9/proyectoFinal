package com.esteban.socialmedia.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.esteban.socialmedia.providers.AuthProvider;
import com.esteban.socialmedia.providers.UserProvider;

import java.util.List;

public class ViewedMessageHelper {

    public static void updateOnline(boolean status, Context context) {
        UserProvider userProvider = new UserProvider();
        AuthProvider authProvider = new AuthProvider();
        if (authProvider.getUid() != null) {
            if(isApplicationSentToBackground(context)){
                userProvider.updateOnline(authProvider.getUid(), status);
            } else if(status) {
                userProvider.updateOnline(authProvider.getUid(), status);
            }
        }
    }
    public static boolean isApplicationSentToBackground(final Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> task = activityManager.getRunningTasks(1);
        if(!task.isEmpty()) {
            ComponentName topActivity = task.get(0).topActivity;
            if(!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}
