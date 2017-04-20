package cs290final.eventadvisor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import cs290final.eventadvisor.backend.CreateEventActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startButtonAction(View view) {
        System.out.println("Starting Map Activity");
        startActivity(new Intent(this, MapsActivity.class));
        //CreateEventActivity.class
    }
}
