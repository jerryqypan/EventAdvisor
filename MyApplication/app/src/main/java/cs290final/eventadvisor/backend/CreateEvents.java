package cs290final.eventadvisor.backend;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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
    private String lat;
    private String lon;

    public CreateEvents(Context context){
        this.context=context;

    }
    protected void onPreExecute(){
    }
    @Override
    protected String doInBackground(String... args){
        try{
            String title=args[0];
            String date=args[1];
            String description=args[2];
            String startTime=args[3];
            String endTime=args[4];
            lat=args[5];
            lon=args[6];
            String place = args[7];
            String uid=args[8];
            String photoPath =args[9];
            String encodedPhoto;
            if(photoPath==null){
                encodedPhoto="";
            }else{
                BitmapFactory.Options options = null;
                options = new BitmapFactory.Options();
                options.inSampleSize = 3;
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath,
                        options);
                float ratio= 800/(float)bitmap.getWidth();
                Bitmap recent = Bitmap.createScaledBitmap(bitmap,800,(int)(bitmap.getHeight()*ratio),true);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                recent.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] byte_arr = stream.toByteArray();
                encodedPhoto = Base64.encodeToString(byte_arr, 0);
            }
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
            data+="&" + URLEncoder.encode("place", "UTF-8") + "=" +
                    URLEncoder.encode(place, "UTF-8");
            data+="&" + URLEncoder.encode("uid", "UTF-8") + "=" +
                    URLEncoder.encode(uid, "UTF-8");
            data+="&" + URLEncoder.encode("photo","UTF-8") + "=" + URLEncoder.encode(encodedPhoto,"UTF-8");
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
        if (context instanceof CreateEventActivity) {
            CreateEventActivity activity = (CreateEventActivity) context;
            activity.createEventsCallBack(lat, lon);
        }
    }
}