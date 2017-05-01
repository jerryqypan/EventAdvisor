package cs290final.eventadvisor.backend;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import cs290final.eventadvisor.MapsActivity;
import cs290final.eventadvisor.TabbedActivity;

/**
 * @author Jerry Pan
 */

public class RetrieveUserEvents extends AsyncTask<String,String,String>{
    private Context context;

    public RetrieveUserEvents(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... args){
        try{
            String uid=args[0];
            String link="https://users.cs.duke.edu/~qp7/retrieveUserEvents.php";
            String data  = URLEncoder.encode("uid", "UTF-8") + "=" +
                    URLEncoder.encode(uid, "UTF-8");
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( data );
            wr.flush();
            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }
            return sb.toString();
        } catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }
    }
    @Override
    protected void onPostExecute(String result){
        if (context instanceof MapsActivity) {
            MapsActivity mapsActivity = (MapsActivity) context;
            mapsActivity.retrieveUserEvents(result);
        }
        if (context instanceof TabbedActivity) {
            TabbedActivity activity = (TabbedActivity) context;
            activity.retrieveAndParseJSON(result, 2);
        }
    }
}
