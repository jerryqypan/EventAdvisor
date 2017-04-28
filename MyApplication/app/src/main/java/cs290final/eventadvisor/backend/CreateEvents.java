package cs290final.eventadvisor.backend;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import cs290final.eventadvisor.CreateEventActivity;
import cs290final.eventadvisor.MapsActivity;

/**
 * Created by jerry on 4/20/2017.
 */

public class CreateEvents extends AsyncTask<String, String, String > {
    private Context context;

    public CreateEvents(Context context){
        this.context=context;

    }
    protected void onPreExecute(){
    }
    @Override
    protected String doInBackground(String... arg0){
        try{
            String title=arg0[0];
            String date=arg0[1];
            String startTime=arg0[2];
            String endTime=arg0[3];
            String description=arg0[4];
            String lat=arg0[5];
            String lon=arg0[6];
            String uid=arg0[7];
            String link="https://users.cs.duke.edu/~qp7/createEvent.php";
            String data =URLEncoder.encode("title", "UTF-8") + "=" +
                    URLEncoder.encode(title, "UTF-8");
            data+="&" + URLEncoder.encode("date", "UTF-8") + "=" +
                    URLEncoder.encode(date, "UTF-8");
            data+="&" + URLEncoder.encode("startTime", "UTF-8") + "=" +
                    URLEncoder.encode(startTime, "UTF-8");
            data+="&" + URLEncoder.encode("endTime", "UTF-8") + "=" +
                    URLEncoder.encode(endTime, "UTF-8");
            data+="&" + URLEncoder.encode("description", "UTF-8") + "=" +
                    URLEncoder.encode(description, "UTF-8");
            data+="&" + URLEncoder.encode("longitude", "UTF-8") + "=" +
                    URLEncoder.encode(lon, "UTF-8");
            data+="&" + URLEncoder.encode("latitude", "UTF-8") + "=" +
                    URLEncoder.encode(lat, "UTF-8");
            data+="&" + URLEncoder.encode("uid", "UTF-8") + "=" +
                    URLEncoder.encode(uid, "UTF-8");
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
        if(result.equals("")){
            Toast.makeText(context, "Success", Toast.LENGTH_LONG).show(); //currently prints success regardless of outcomes
        }else{
            Toast.makeText(context, "Failure "+result, Toast.LENGTH_LONG).show(); //currently prints success regardless of outcomes
        }
        Intent i = new Intent(context,MapsActivity.class);
        CreateEventActivity current = (CreateEventActivity)context;
        i.putExtra("latitude",current.getCoordinates().split(",")[0]);
        i.putExtra("longitude",current.getCoordinates().split(",")[1]);
        context.startActivity(i);

    }
}
