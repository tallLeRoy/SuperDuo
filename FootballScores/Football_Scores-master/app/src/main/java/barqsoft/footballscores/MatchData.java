package barqsoft.footballscores;

import android.content.Context;
import android.text.format.Time;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by LeRoy on 9/21/2015.
 */
public class MatchData {
    private final String TEAM_BASE = "http://api.football-data.org/alpha/teams/";

    String our_team_name;
    String our_team_id;
    String our_team_score;
    String opponent_team_name;
    String opponent_team_id;
    String opponent_team_score;
    String match_day;
    String match_time;
    String status;

    public MatchData(String our_team_id) {
        this.our_team_id = our_team_id;
    }

    public String getMatch_day() {
        return match_day;
    }

    public String getMatch_time() {
        return match_time;
    }

    public String getOpponent_team_id() {
        return opponent_team_id;
    }

    public String getOpponent_team_name() {
        return opponent_team_name;
    }

    public String getOpponent_team_score() {
        return opponent_team_score;
    }

    public String getOur_team_id() {
        return our_team_id;
    }

    public String getOur_team_name() {
        return our_team_name;
    }

    public String getOur_team_score() {
        return our_team_score;
    }

    public String getStatus() {
        return status;
    }

    public void setTeamName(String team_name, String team_id) {
        if (team_id.equals(our_team_id)) {
            our_team_name = team_name;

        } else {
            opponent_team_name = team_name;
        }
    }

    public void setMatch_day(String match_day) {
        this.match_day = match_day;
    }

    public void setMatch_time(String match_time) {
        this.match_time = match_time;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTeamId(String team_id) {
        if (team_id.equals(our_team_id)) {
        } else {
            opponent_team_id = team_id;
        }
    }

    public void setTeamScore(String team_score, String team_id) {
        if (team_id.equals(our_team_id)) {
            our_team_score = team_score;
        } else {
            opponent_team_score = team_score;
        }
    }

    public void initialize(Context context, JSONObject matchJSON) {
        try {
            String date = matchJSON.getString("date");
            date = date.replace("Z", "");
            // convert this to local time right away
            SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH);
            dateFormater.setTimeZone(TimeZone.getTimeZone("GMT"));
            Date gmtDate = dateFormater.parse(date);
            dateFormater.setTimeZone(TimeZone.getDefault());
            dateFormater.applyPattern("yyyy-MM-dd");
            setMatch_day(getDayName(context, dateFormater.format(gmtDate)));
            dateFormater.applyPattern("HH:mm");
            setMatch_time(dateFormater.format(gmtDate));
            setStatus(matchJSON.getString("status"));
            JSONObject resultJSON = matchJSON.getJSONObject("result");
            JSONObject linksJSON = matchJSON.getJSONObject(("_links"));
            JSONObject homeJSON = linksJSON.getJSONObject("homeTeam");
            String home_team_id = homeJSON.getString("href").replace(TEAM_BASE, "");
            setTeamId(home_team_id);
            String home_team_name = matchJSON.getString("homeTeamName");
            setTeamName(home_team_name, home_team_id);
            setTeamScore(resultJSON.getString("goalsHomeTeam"), home_team_id);
            JSONObject awayJSON = linksJSON.getJSONObject("awayTeam");
            String away_team_id = awayJSON.getString("href").replace(TEAM_BASE, "");
            setTeamId(away_team_id);
            String away_team_name = matchJSON.getString("awayTeamName");
            setTeamName(away_team_name, away_team_id);
            setTeamScore(resultJSON.getString("goalsAwayTeam"), away_team_id);
            // fix up the game time
            if (!our_team_score.equals("-1") && !opponent_team_score.equals("-1")) {
                if (status.equals("FINISHED")) {
                    setMatch_time(context.getString(R.string.match_final_score));
                } else {
                    setMatch_time(context.getString(R.string.match_in_progress));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getDayName(Context context, String day) {
        // If the date is today, return the localized version of "Today" instead of the actual
        // day name.

        long dateInMillis = 0;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date dayDate = dateFormat.parse(day);
            dateInMillis = dayDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if ( julianDay == currentJulianDay +1 ) {
            return context.getString(R.string.tomorrow);
        }
        else if ( julianDay == currentJulianDay -1)
        {
            return context.getString(R.string.yesterday);
        }
        else
        {
            Time time = new Time();
            time.setToNow();
            // Otherwise, the format is just the day of the week (e.g "Wednesday".
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }
    }

}
