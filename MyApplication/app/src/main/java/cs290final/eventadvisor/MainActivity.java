package cs290final.eventadvisor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startButtonAction(View view) {
        System.out.println("Starting Map Activity");
        startActivity(new Intent(this, MapsActivity.class));
    }
    public void createEventAction(View view){
        System.out.println("Create Event Activity");
        startActivity(new Intent(this, CreateEventActivity.class));
    }
}
