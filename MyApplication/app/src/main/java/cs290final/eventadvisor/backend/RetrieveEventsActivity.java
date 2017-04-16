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

/**
 * Created by Jerry on 4/16/2017.
 */

public class RetrieveEventsActivity extends AsyncTask<String,String,String> {
    private TextView statusField,roleField;
    private Context context;

    //flag 0 means get and 1 means post.(By default it is get.)
    public RetrieveEventsActivity() {
//        this.context = context;
//        this.statusField = statusField;
//        this.roleField = roleField;
    }

    protected void onPreExecute(){
    }
    @Override
    protected String doInBackground(String[] arg0){
        try{
            System.out.println("testing");
            String text = (String)arg0[0];


            String link="https://users.cs.duke.edu/~qp7/index.php";
            String data  = URLEncoder.encode("text", "UTF-8") + "=" +
                    URLEncoder.encode(text, "UTF-8");
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
