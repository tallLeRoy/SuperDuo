package barqsoft.footballscores;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by LeRoy on 9/8/2015.
 */
public class TeamData implements Comparable<TeamData> {
    String teamId = null;
    String teamName = null;
    String crestUrl = null;

    public TeamData(String team_id, String team_name) {
        this.teamId = team_id;
        this.teamName = team_name;
//        new ObtainTeamData().execute(team_id);
    }

    public String getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public String getCrestUrl() {
        return crestUrl;
    }

    @Override
    public int compareTo(TeamData another) {
        return teamName.compareTo(another.getTeamName());
    }

    class ObtainTeamData extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            return grabCrestUrl(params[0]);
        }

        String grabCrestUrl(String team_id) {
            HttpURLConnection urlConnection = null;
            Bitmap bitmap = null;
            BufferedReader reader = null;
            try {
                // Create the request to themoviedb, and open the connection
                URL url = new URL("http://api.football-data.org/alpha/teams/" + team_id);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.addRequestProperty("X-Auth-Token", MainActivity.applicationContext.getString(R.string.api_key));
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
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
                    return null;
                }
                processJSON(buffer.toString());

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                // kind of normal for this database
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
            }
            return crestUrl;
        }

        void processJSON(String raw) {
            try {
                JSONObject jsonWhole = new JSONObject(raw);
                crestUrl = jsonWhole.getString("crestUrl");
                teamName = jsonWhole.getString("name");
                Log.i("TeamData.processJSON", teamId + " : " + teamName + " : " + crestUrl);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
