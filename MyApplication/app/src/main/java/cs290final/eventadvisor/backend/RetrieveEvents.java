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

/**
 * @author Jerry Pan
 */

public class RetrieveEvents extends AsyncTask<String,String,String> {
    /**
     * MapActivity context
     */
    private Context context;

    public RetrieveEvents(Context context) {
        this.context = context;
    }


    @Override
    /**
     * Sends event information to retrieveEvents.php asynchronously
     *
     * @param args arguments passed to post to retrieveEvents.php
     */
    protected String doInBackground(String... args){
        try{
            String lat=args[0];
            String lon=args[1];
            String uid=args[2];
            String distance=args[3];
            String link="https://users.cs.duke.edu/~qp7/retrieveEvents.php";
            String data  = URLEncoder.encode("latitude", "UTF-8") + "=" +
                    URLEncoder.encode(lat, "UTF-8");
            data+="&" + URLEncoder.encode("longitude", "UTF-8") + "=" +
                    URLEncoder.encode(lon, "UTF-8");
            data+="&" + URLEncoder.encode("uid","UTF-8") + "=" +
                    URLEncoder.encode(uid,"UTF-8");
            data+="&" + URLEncoder.encode("diameter","UTF-8") + "=" +
                    URLEncoder.encode(distance,"UTF-8");
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
    /**
     * Calls MapActivity to parse the json into Events
     *
     * @params result json returned from retrieveEvents.php
     */
    protected void onPostExecute(String result){
        if (context instanceof MapsActivity) {
            MapsActivity mapsActivity = (MapsActivity) context;
            mapsActivity.retrieveAndParseJSON(result);
        }
    }
}