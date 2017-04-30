package cs290final.eventadvisor.backend;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


/**
 * Created by jerry on 4/30/2017.
 */

public class DeleteEvent extends AsyncTask<String,String,String> {

    protected void onPreExecute(){
    }
    @Override
    protected String doInBackground(String... args){
        try{
            String idEvent=args[0];
            String link="https://users.cs.duke.edu/~qp7/deleteEvent.php";
            String data = URLEncoder.encode("idEvent", "UTF-8") + "=" +
                    URLEncoder.encode(idEvent, "UTF-8");
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
            //System.out.println(sb.toString());
            return sb.toString();
        } catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }
    }
    @Override
    protected void onPostExecute(String result){

    }
}
