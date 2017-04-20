package cs290final.eventadvisor.backend;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import cs290final.eventadvisor.R;

/**
 * Created by Jerry on 4/16/2017.
 */

public class CreateEventActivity extends Activity{

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

    }
    public static void loginPost(View view){

        //new RetrieveEventsActivity().execute("Sending to Server");
    }
    public static void main(String args[]){
//        SendQuery();
    }
}
