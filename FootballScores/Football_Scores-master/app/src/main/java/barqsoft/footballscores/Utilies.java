package barqsoft.footballscores;

import android.os.Build;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yehya khaled on 3/3/2015.
 */
public class Utilies
{
    public static final int SERIE_A_14 = 357;
    public static final int PREMIER_LEAGUE_14 = 354;
    public static final int CHAMPIONS_LEAGUE_14 = 362;
    public static final int PRIMERA_DIVISION_14 = 358;
    public static final int BUNDESLIGA1_14 = 351;
    public static final int BUNDESLIGA1_15 = 394;
    public static final int BUNDESLIGA2_15 = 395;
    public static final int BUNDESLIGA3_15 = 403;
    public static final int LIGUE1_15 = 396;
    public static final int LIGUE2_15 = 397;
    public static final int PREMIER_LEAGUE_15 = 398;
    public static final int PRIMERA_DIVISION_15 = 399;
    public static final int SEGUNDA_DIVISION_15 = 400;
    public static final int SERIE_A_15 = 401;
    public static final int PRIMEIRA_LIGA_15 = 402;
    public static final int EREDIVISIE_15 = 404;

    static final Map<String, TeamData> mapTeamData = new HashMap<String, TeamData>(40);

    public static String getLeague(int league_num)
    {
        switch (league_num)
        {
            case SERIE_A_14 : return "Seria A 2014/15";
            case SERIE_A_15 : return "Seria A 2015/16";
            case PREMIER_LEAGUE_14 : return "Premier League 2014/15";
            case PREMIER_LEAGUE_15 : return "Premier League 2015/16";
            case CHAMPIONS_LEAGUE_14 : return "UEFA Champions League 2014/15";
            case PRIMERA_DIVISION_14 : return "Primera Division 2014/15";
            case PRIMERA_DIVISION_15 : return "Primera Division 2015/16";
            case BUNDESLIGA1_14 : return "1. Bundesliga 2014/15";
            case BUNDESLIGA1_15 : return "1. Bundesliga 2015/16";
            case BUNDESLIGA2_15 : return "2. Bundesliga 2015/16";
            case BUNDESLIGA3_15 : return "3. Bundesliga 2015/16";
            case LIGUE1_15 : return "Ligue 1 2015/16";
            case LIGUE2_15 : return "Ligue 2 2015/16";
            case SEGUNDA_DIVISION_15 : return "Segunda Division 2015/16";
            case PRIMEIRA_LIGA_15 : return "Primeira Liga 2015/16";
            case EREDIVISIE_15 : return "Eredivisie 2015/16";
           default: return "Not known League Please report";
        }
    }
    public static String getMatchDay(int match_day,int league_num)
    {
        if(league_num == CHAMPIONS_LEAGUE_14)
        {
            if (match_day <= 6)
            {
                return "Group Stages, Matchday : 6";
            }
            else if(match_day == 7 || match_day == 8)
            {
                return "First Knockout round";
            }
            else if(match_day == 9 || match_day == 10)
            {
                return "QuarterFinal";
            }
            else if(match_day == 11 || match_day == 12)
            {
                return "SemiFinal";
            }
            else
            {
                return "Final";
            }
        }
        else
        {
            return "Matchday : " + String.valueOf(match_day);
        }
    }

    public static String getScores(int home_goals,int awaygoals)
    {
        if(home_goals < 0 || awaygoals < 0)
        {
            return " - ";
        }
        else
        {
            return String.valueOf(home_goals) + " - " + String.valueOf(awaygoals);
        }
    }

    public static int getTeamCrestByTeamName (String teamname)
    {
        if (teamname==null){return R.drawable.no_icon;}
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int resource = getVectorTeamCrestByTeamName(teamname);
            if (resource != R.drawable.no_icon) {
                return resource;
            }
        }
        switch (teamname)
        { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
            case "Arsenal London FC" : return R.drawable.arsenal;
            case "Manchester United FC" : return R.drawable.manchester_united;
            case "Swansea City" : return R.drawable.swansea_city_afc;
            case "Leicester City" : return R.drawable.leicester_city_fc_hd_logo;
            case "Everton FC" : return R.drawable.everton_fc_logo1;
            case "West Ham United FC" : return R.drawable.west_ham;
            case "Tottenham Hotspur FC" : return R.drawable.tottenham_hotspur;
            case "West Bromwich Albion" : return R.drawable.west_bromwich_albion_hd_logo;
            case "Sunderland AFC" : return R.drawable.sunderland;
            case "Stoke City FC" : return R.drawable.stoke_city;
            default: return R.drawable.no_icon;
        }
    }

    public static int getVectorTeamCrestByTeamName (String teamname)
    {
        if (teamname==null){return R.drawable.no_icon;}
        switch (teamname)
        { //This is the set of icons that are currently in the app. Feel free to find and add more
            //as you go.
//            case "SC Freiburg" : return R.drawable.sc_freiburg;
            default: return R.drawable.no_icon;
        }
    }


    public static void setTeamId ( String team_id, String team_name ) {
        if (!mapTeamData.containsKey(team_id)) {
            mapTeamData.put(team_id, new TeamData(team_id, team_name));
        }
    }

    public static TeamData getTeamData( String team_id ) {
        if (mapTeamData.containsKey(team_id)) {
            return mapTeamData.get(team_id);
        }
        return null;
    }

    public static URL getTeamCrestUrl( String team_id ) {
        if (mapTeamData.containsKey(team_id)) {
            String crestUrl = mapTeamData.get(team_id).getCrestUrl();
            if (crestUrl != null) {
                try {
                    return new URL(crestUrl);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static List<TeamData> getListTeamData() {
        List<TeamData> list;
        Collection<TeamData> coll = mapTeamData.values();
        if (coll instanceof List) {
            list = (List<TeamData>)coll;
        } else {
            list = new ArrayList<TeamData>(coll);
        }
        Collections.sort(list);
        return list;
    }
}
