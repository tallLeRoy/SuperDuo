package barqsoft.footballscores.widget;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import barqsoft.footballscores.R;
import barqsoft.footballscores.TeamData;
import barqsoft.footballscores.Utilies;

public class TodayWidgetConfigureActivity extends ListActivity implements LoaderManager.LoaderCallbacks {
    final static String LOG_TAG = "TodayWidgetConfigureActivity";
    public final static String PREF_PREFIX = "WIDGET_TEAM_ID_";
    public final static String PREFS_NAME = "barqsoft.footballscores.widget.TodayWidgetProvider";

    int mWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    TeamDataAdapter mTeamDataAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(RESULT_CANCELED);

        setContentView(R.layout.activity_today_widget_configure);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        mTeamDataAdapter = new TeamDataAdapter(this, R.layout.team_data_row, Utilies.getListTeamData());
        getListView().setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        TeamData teamData = (TeamData) view.getTag();
                        String team_id = teamData.getTeamId();

                        // hand the team_id selected to our widget
                        Context context = TodayWidgetConfigureActivity.this;
                        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
                        prefs.putString(PREF_PREFIX + mWidgetId, team_id);
                        prefs.commit();

                        // now that we have a team_id, start the service
                        context.startService(new Intent(context, TodayWidgetIntentService.class));

                        // now tell android the configuration is done, and exit
                        Intent resultValue = new Intent();
                        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mWidgetId);
                        setResult(RESULT_OK, resultValue);
                        finish();
                    }
                }
        );
        setListAdapter(mTeamDataAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_today_widget_configure, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }
}
