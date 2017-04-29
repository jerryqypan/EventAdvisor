package cs290final.eventadvisor.backend;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Created by Jerry on 4/29/2017.
 */

public class SelectInterest extends AsyncTask<String, String, String> {
    private View view;

    public SelectInterest(View view){
        this.view = view;
    }
    protected void onPreExecute(){
        view.setClickable(false);
    }
    @Override
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
            System.out.println("Server Response" + sb.toString());
            return sb.toString();
        }catch(Exception e){
            return e.getMessage();
        }
    }

    protected void onPostExecute(String result){
        view.setClickable(true);
    }

}
