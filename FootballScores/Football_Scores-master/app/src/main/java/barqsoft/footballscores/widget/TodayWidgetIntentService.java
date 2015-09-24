package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.MatchData;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by LeRoy on 9/19/2015.
 */
public class TodayWidgetIntentService extends IntentService {
    public static final String LOG_TAG = "TodayWidgetIntentServic";
    private String team_id = "";

    public TodayWidgetIntentService() {
        super("TodayWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean bQuickRefresh = false;

        Log.i(LOG_TAG, "OnHandleIntent entry");

        // Retrieve all of the Today widget ids: these are the widgets we need to update
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                TodayWidgetProvider.class));

        // Perform this loop procedure for each Today widget
        for (int appWidgetId : appWidgetIds) {
            // get the team_id for this widget id
            Context context = getApplicationContext();
            SharedPreferences prefs = context.getSharedPreferences(TodayWidgetConfigureActivity.PREFS_NAME, 0);
            String long_team_id = prefs.getString(TodayWidgetConfigureActivity.PREF_PREFIX + appWidgetId, null);
            if (long_team_id != null) {
                team_id = long_team_id.replace(TodayWidgetConfigureActivity.PREF_PREFIX, "");
            }

            if (team_id.equals("")) {
                return;
            }

            int layoutId = R.layout.widget_today_small;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            // Add the data to the RemoteViews
            // get the data we need based on the team id on this widget
            StringBuffer description = new StringBuffer();

            Map<String, MatchData> matches = getMatches(team_id);
            MatchData match = null;
            if (matches.isEmpty()) {
                // display a message that says our team is not active right now
                String ourTeamName = Utilies.getTeamData(team_id).getTeamName();
                views.setTextViewText(R.id.widget_team_name, ourTeamName);
                description.append(ourTeamName + " ");
                views.setTextViewText(R.id.widget_match_day, getString(R.string.widget_is_idle));
                description.append(getString(R.string.widget_is_idle));
            } else {
                Set<String> dateKeys = matches.keySet();
                if (dateKeys.contains(getString(R.string.today))) {
                    match = matches.get(getString(R.string.today));
                } else if (dateKeys.contains(getString(R.string.yesterday))) {
                    match = matches.get(getString(R.string.yesterday));
                } else if (dateKeys.contains(getString(R.string.tomorrow))) {
                    match = matches.get(getString(R.string.tomorrow));
                } else {
                    match = matches.get(dateKeys.toArray()[0]);
                }
                if (match != null) {
                    views.setTextViewText(R.id.widget_team_name, match.getOur_team_name());
                    description.append(match.getOur_team_name() + "\t");
                    String our_score = match.getOur_team_score();
                    String opponents_score = match.getOpponent_team_score();
                    if ((our_score.equals("-1")) || (opponents_score.equals("-1"))) {
                        views.setTextViewText(R.id.widget_team_score, "");
                        views.setTextViewText(R.id.widget_opponent_score, "");
                    } else {
                        views.setTextViewText(R.id.widget_team_score, our_score);
                        description.append(our_score + "\t");
                        views.setTextViewText(R.id.widget_opponent_score, opponents_score);
                        description.append(match.getOpponent_team_name() + "\t");
                        description.append(opponents_score + "\t");
                    }
                    views.setTextViewText(R.id.widget_opponent_name, match.getOpponent_team_name());
                    views.setTextViewText(R.id.widget_match_day, match.getMatch_day());
                    description.append(match.getMatch_day() + "\t");
                    views.setTextViewText(R.id.widget_match_time, match.getMatch_time());
                    description.append(match.getMatch_time());
                    if (match.getMatch_time().equals(getString(R.string.match_in_progress))) {
                        bQuickRefresh = true;
                    }
                }
            }

            // Content Descriptions for RemoteViews were only added in ICS MR1
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                setRemoteContentDescription(views, description.toString());
            }

            // Create an Intent to launch MainActivity
            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

            // set us up to refresh the widget every 15 minutes or quicker if a match is in progress
            long refreshInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
            if (bQuickRefresh) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    refreshInterval = 60 * 1000 * 5; // five minutes
                }
            }
            Intent refreshIntent = new Intent(getApplicationContext(), TodayWidgetProvider.class);
            refreshIntent.setAction(TodayWidgetProvider.ACTION_ALARM_WAKEUP);
            final PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(getApplicationContext(), TodayWidgetProvider.REQUEST_CODE,
                    refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarm.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + refreshInterval, refreshPendingIntent);
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
    private void setRemoteContentDescription(RemoteViews views, String description) {
        views.setContentDescription(R.id.widget_icon, description);
    }

    private Map<String, MatchData> getMatches(String team_id) {
        Map<String, MatchData> map = new HashMap<String, MatchData>();
        map.putAll(getMatchTimeFrame("p2", team_id));
        map.putAll(getMatchTimeFrame("n2", team_id));
        return map;
    }

    private Map<String, MatchData> getMatchTimeFrame(String timeFrame, String team_id) {
        Map<String, MatchData> map = new HashMap<String, MatchData>();

        //Creating fetch URL
        String BASE_URL = "http://api.football-data.org/alpha/teams/" + team_id + "/fixtures"; //Base URL
        final String QUERY_TIME_FRAME = "timeFrame"; //Time Frame parameter to determine days
        //final String QUERY_MATCH_DAY = "matchday";

        Uri fetch_build = Uri.parse(BASE_URL).buildUpon().
                appendQueryParameter(QUERY_TIME_FRAME, timeFrame).build();
        //Log.v(LOG_TAG, "The url we are looking at is: "+fetch_build.toString()); //log spam
        HttpURLConnection m_connection = null;
        BufferedReader reader = null;
        String JSON_data = null;
        //Opening Connection
        try {
            URL fetch = new URL(fetch_build.toString());
            m_connection = (HttpURLConnection) fetch.openConnection();
            m_connection.setRequestMethod("GET");
            m_connection.addRequestProperty("X-Auth-Token",getString(R.string.api_key));
            m_connection.connect();

            // Read the input stream into a String
            InputStream inputStream = m_connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return map;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }
            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return map;
            }
            JSON_data = buffer.toString();
        }
        catch (Exception e)
        {
            Log.e(LOG_TAG, "Exception here " + e.getMessage());
        }
        finally {
            if(m_connection != null)
            {
                m_connection.disconnect();
            }
            if (reader != null)
            {
                try {
                    reader.close();
                }
                catch (IOException e)
                {
                    Log.e(LOG_TAG,"Error Closing Stream");
                }
            }
        }

        if ((JSON_data != null) && (!JSON_data.isEmpty())) {
            try {
                JSONObject wholeJSON = new JSONObject(JSON_data);
                int count = wholeJSON.getInt("count");
                if (count > 0) {
                    JSONArray fixtures = wholeJSON.getJSONArray("fixtures");
                    for (int i=0; i < count; i++) {
                        JSONObject match = fixtures.getJSONObject(i);
                        MatchData matchData = new MatchData(team_id);
                        matchData.initialize(this, match);
                        map.put(matchData.getMatch_day(), matchData);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        return map;
    }

}

