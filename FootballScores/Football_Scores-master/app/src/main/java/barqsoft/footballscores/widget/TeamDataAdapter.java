package barqsoft.footballscores.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import barqsoft.footballscores.R;
import barqsoft.footballscores.TeamData;

/**
 * Created by LeRoy on 9/23/2015.
 */
public class TeamDataAdapter extends ArrayAdapter<TeamData> {
    final static String LOG_TAG = "TeamDataAdapter";

    List<TeamData> listTeamData;

    public TeamDataAdapter(Context context, int resource, List<TeamData> objects) {
        super(context, resource, objects);
        listTeamData = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.team_data_row, null);
        }
        TeamData teamData = listTeamData.get(position);
        if (teamData != null) {
            v.setTag(teamData);
            TextView teamName = (TextView)v.findViewById(R.id.team_data_row_team_name);
            if (teamName != null) {
                teamName.setText(teamData.getTeamName());
                teamName.setContentDescription(teamData.getTeamName());
            }
        }
        return v;
    }
}
