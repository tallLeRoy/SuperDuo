package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class scoresAdapter extends CursorAdapter
{
    public double detail_match_id = 0;
    public static final int COL_DATE = 1;
    public static final int COL_MATCHTIME = 2;
    public static final int COL_HOME = 3;
    public static final int COL_HOME_TEAM_ID = 4;
    public static final int COL_AWAY = 5;
    public static final int COL_AWAY_TEAM_ID = 6;
    public static final int COL_LEAGUE = 7;
    public static final int COL_HOME_GOALS = 8;
    public static final int COL_AWAY_GOALS = 9;
    public static final int COL_ID = 10;
    public static final int COL_MATCHDAY = 11;
    public static final int COL_STATUS = 12;
    private String FOOTBALL_SCORES_HASHTAG = "#Football_Scores";
    public scoresAdapter(Context context,Cursor cursor,int flags)
    {
        super(context,cursor,flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent)
    {
        View mItem = LayoutInflater.from(context).inflate(R.layout.scores_list_item, parent, false);
        ViewHolder mHolder = new ViewHolder(mItem);
        mItem.setTag(mHolder);
        //Log.v(FetchScoreTask.LOG_TAG,"new View inflated");
        return mItem;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor)
    {
        final ViewHolder mHolder = (ViewHolder) view.getTag();
        String home_name = cursor.getString(COL_HOME);
        mHolder.home_name.setText(home_name);
        String away_name = cursor.getString(COL_AWAY);
        mHolder.away_name.setText(away_name);
        String matchtime = cursor.getString(COL_MATCHTIME);
        int home_goals = cursor.getInt(COL_HOME_GOALS);
        int away_goals = cursor.getInt(COL_AWAY_GOALS);
        mHolder.score.setText(Utilies.getScores(home_goals,away_goals));
        mHolder.match_id = cursor.getDouble(COL_ID);
        mHolder.home_crest.setImageResource(Utilies.getTeamCrestByTeamName(
                cursor.getString(COL_HOME)));
        mHolder.away_crest.setImageResource(Utilies.getTeamCrestByTeamName(
                cursor.getString(COL_AWAY)));

        StringBuilder sb = new StringBuilder();
        if ((home_goals < 0) || (away_goals < 0)) {
            // the game has not started
            sb.append(home_name);
            sb.append(context.getString(R.string.future_match_teams));
            sb.append(away_name);
            sb.append(context.getString(R.string.future_match_time));
            sb.append(matchtime);
        } else {
            // we have a score to report
            sb.append(home_name);
            sb.append(context.getString(R.string.content_desciption_space));
            sb.append(home_goals);
            sb.append(context.getString(R.string.content_desciption_space));
            sb.append(away_name);
            sb.append(context.getString(R.string.content_desciption_space));
            sb.append(away_goals);
            sb.append(context.getString(R.string.content_desciption_space));
            String status = cursor.getString(COL_STATUS);
            if (status.equals("FINISHED")) {
                sb.append(context.getString(R.string.match_final_score));
                matchtime = context.getString(R.string.match_final_score);
            } else {
                sb.append(context.getString(R.string.match_in_progress));
                matchtime = context.getString(R.string.match_in_progress);
            }
        }
        mHolder.date.setText(matchtime);
        mHolder.scores_list_item.setContentDescription(sb.toString());
        
        //Log.v(FetchScoreTask.LOG_TAG,mHolder.home_name.getText() + " Vs. " + mHolder.away_name.getText() +" id " + String.valueOf(mHolder.match_id));
        //Log.v(FetchScoreTask.LOG_TAG,String.valueOf(detail_match_id));
        LayoutInflater vi = (LayoutInflater) context.getApplicationContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.detail_fragment, null);
        ViewGroup container = (ViewGroup) view.findViewById(R.id.details_fragment_container);
        if(mHolder.match_id == detail_match_id)
        {
            //Log.v(FetchScoreTask.LOG_TAG,"will insert extraView");

            container.addView(v, 0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT
                    , ViewGroup.LayoutParams.MATCH_PARENT));
            TextView match_day = (TextView) v.findViewById(R.id.matchday_textview);
            match_day.setText(Utilies.getMatchDay(cursor.getInt(COL_MATCHDAY),
                    cursor.getInt(COL_LEAGUE)));
            TextView league = (TextView) v.findViewById(R.id.league_textview);
            league.setText(Utilies.getLeague(cursor.getInt(COL_LEAGUE)));
            Button share_button = (Button) v.findViewById(R.id.share_button);
            share_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //add Share Action
                    context.startActivity(createShareForecastIntent(mHolder.home_name.getText() + " "
                            + mHolder.score.getText() + " " + mHolder.away_name.getText() + " "));
                }
            });
        }
        else
        {
            container.removeAllViews();
        }

    }
    public Intent createShareForecastIntent(String ShareText) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, ShareText + FOOTBALL_SCORES_HASHTAG);
        return shareIntent;
    }

}
