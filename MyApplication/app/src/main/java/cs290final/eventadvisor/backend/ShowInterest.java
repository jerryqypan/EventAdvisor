package cs290final.eventadvisor.backend;

import android.os.AsyncTask;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * @author Jerry Pan
 */

public class ShowInterest extends AsyncTask<String, String, String> {

    @Override
    /**
     * Sends event information to showInterest.php asynchronously
     *
     * @param args argument passed to post to showInterest.php
     */
    protected String doInBackground(String ... args){
        try{
            String uid = args[0];
            String link = "https://users.cs.duke.edu/~qp7/showInterest.php";
            String data = URLEncoder.encode("uid","UTF-8") + "=" + URLEncoder.encode(uid, "UTF-8");
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( data );
            wr.flush();
            return "";
        }catch(Exception e){
            return e.getMessage();
        }
    }

}
