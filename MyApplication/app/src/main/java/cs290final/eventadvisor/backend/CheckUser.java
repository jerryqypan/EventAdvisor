package cs290final.eventadvisor.backend;

import android.os.AsyncTask;

import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;


/**
 * Created by Jerry on 4/23/2017.
 */

public class CheckUser extends AsyncTask<String,String,Void> {

    protected void onPreExecute(){

    }
    @Override
    protected Void doInBackground(String... arg0){
        try {
            String uid = arg0[0];
            String name = arg0[1];
            String email = arg0[2];
            String link = "https://users.cs.duke.edu/~qp7/checkUser.php";
            String data = URLEncoder.encode("uid", "UTF-8") + "=" +
                    URLEncoder.encode(uid, "UTF-8");
            data+="&" + URLEncoder.encode("name", "UTF-8") + "=" +
                    URLEncoder.encode(name, "UTF-8");
            data+="&" + URLEncoder.encode("email", "UTF-8") + "=" +
                    URLEncoder.encode(email, "UTF-8");
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write( data );
            wr.flush();
        }
        catch(Exception e){

        }
    return null;
    }
    @Override
    protected void onPostExecute(Void v){


    }

}
