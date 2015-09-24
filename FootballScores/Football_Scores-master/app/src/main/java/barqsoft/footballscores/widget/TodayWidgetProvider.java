package barqsoft.footballscores.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by LeRoy on 9/12/2015.
 */
public class TodayWidgetProvider extends AppWidgetProvider {
    public static final String LOG_TAG = "TodayWidgetProvider";
    public final static String ACTION_ALARM_WAKEUP =
            "barqsoft.footballscores.ACTION_ALARM_WAKEUP";
    public final static int REQUEST_CODE = 753711;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, TodayWidgetIntentService.class));
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, TodayWidgetIntentService.class));
    }

    @Override
    public void onReceive(@NonNull Context context, @NonNull Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (action == null) {
            action = "getAction() returned null";
        }
        Log.i(LOG_TAG, action);
        if (TodayWidgetProvider.ACTION_ALARM_WAKEUP.equals(action)) {
            context.startService(new Intent(context, TodayWidgetIntentService.class));
        } else if ( "android.intent.action.BOOT_COMPLETED".equals(action)) {
            context.startService(new Intent(context, TodayWidgetIntentService.class));
        }
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        // get rid of the refresh timer
        Intent refreshIntent = new Intent(context, TodayWidgetProvider.class);
        refreshIntent.setAction(ACTION_ALARM_WAKEUP);
        final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context, REQUEST_CODE,
                refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(refreshPendingIntent);
    }
}

