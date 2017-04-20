package cs290final.eventadvisor.backend;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by Jerry on 4/16/2017.
 */

public class RetrieveEventsActivity extends AsyncTask<Double,Integer,String> {

    public RetrieveEventsActivity() {

    }

    protected void onPreExecute(){
    }
    @Override
    protected String doInBackground(Double... arg0){
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
    }
}
