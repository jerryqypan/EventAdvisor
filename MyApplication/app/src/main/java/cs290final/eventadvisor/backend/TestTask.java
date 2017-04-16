package cs290final.eventadvisor.backend;

import android.os.AsyncTask;

/**
 * Created by Jerry on 4/16/2017.
 */

public class TestTask extends AsyncTask<String,Integer,Boolean> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
    }

    @Override
    protected Boolean doInBackground(String... params){
        return null;
    }
}