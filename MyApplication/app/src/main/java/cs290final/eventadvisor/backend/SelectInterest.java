package cs290final.eventadvisor.backend;

import android.os.AsyncTask;
import android.view.View;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * @author Jerry Pan
 */

public class SelectInterest extends AsyncTask<String, String, String> {
    /**
     * The button that is pressed to favorite the event.
     */
    private View view;

    public SelectInterest(View view){
        this.view = view;
    }
    /**
     * Disables the click of the interest button until the database has processed change
     */
    protected void onPreExecute(){
        view.setClickable(false);
    }
    @Override
    /**
     * Sends event information to selectInterest.php asynchronously
     *
     * @param args arguments passed to post to selectInterest.php
     */
    protected String doInBackground(String ... args){
        try{
            String uid = args[0];
            String eid = args[1];
            String addOrDelete = args[2];
            String link = "https://users.cs.duke.edu/~qp7/selectInterest.php";
            String data = URLEncoder.encode("uid","UTF-8") + "=" + URLEncoder.encode(uid, "UTF-8");
            data += "&" + URLEncoder.encode("idEvent","UTF-8") + "=" + URLEncoder.encode(eid,"UTF-8");
            data += "&" + URLEncoder.encode("addOrDelete","UTF-8") + "=" + URLEncoder.encode(addOrDelete,"UTF-8");
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
        }catch(Exception e){
            return e.getMessage();
        }
    }
    /**
     * Reenables the click of the interest button after the database processed the change
     */
    protected void onPostExecute(String result){
        view.setClickable(true);
    }

}
