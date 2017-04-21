package cs290final.eventadvisor.backend;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import cs290final.eventadvisor.MapsActivity;

/**
 * Created by jerry on 4/20/2017.
 */

public class CreateEvents extends AsyncTask<String, String, String > {
    protected void onPreExecute(){
    }
    @Override
    protected String doInBackground(String... arg0){
        try{
            System.out.println("testing");
            String lat=arg0[0];
            String lon=arg0[1];
            String link="https://users.cs.duke.edu/~qp7/createEvents.php";
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

    }
}
