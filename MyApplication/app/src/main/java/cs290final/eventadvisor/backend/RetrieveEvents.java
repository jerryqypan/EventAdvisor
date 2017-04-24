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
 * Created by Jerry on 4/16/2017.
 */

public class RetrieveEvents extends AsyncTask<Double,Integer,String> {
    private Context context;

    public RetrieveEvents(Context context) {
        this.context = context;
    }

    protected void onPreExecute(){
    }
    @Override
    protected String doInBackground(Double... arg0){
        System.out.println("doinbackgrougn" + Thread.currentThread());
        try{
            System.out.println("testing");
            Double lat=arg0[0];
            Double lon=arg0[1];
            String link="https://users.cs.duke.edu/~qp7/retrieveEvents.php";
            String data  = URLEncoder.encode("latitude", "UTF-8") + "=" +
                    URLEncoder.encode(lat.toString(), "UTF-8");
            data+="&" + URLEncoder.encode("longitude", "UTF-8") + "=" +
                    URLEncoder.encode(lon.toString(), "UTF-8");
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( data );
            wr.flush();
            System.out.println("after read");
            BufferedReader reader = new BufferedReader(new
                    InputStreamReader(conn.getInputStream()));

            StringBuilder sb = new StringBuilder();
            String line = null;

            // Read Server Response
            while((line = reader.readLine()) != null) {
                sb.append(line);
                break;
            }
            System.out.println(sb.toString());
            return sb.toString();
        } catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }
    }
    @Override
    protected void onPostExecute(String result){
//        this.statusField.setText("Login Successful");
//        this.roleField.setText(result);
        if (context instanceof MapsActivity) {
            MapsActivity mapsActivity = (MapsActivity) context;
            mapsActivity.retrieveAndParseJSON(result);
            System.out.println("onpostexecute" + Thread.currentThread());
        }
    }
}
