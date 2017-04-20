package cs290final.eventadvisor.backend;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Jerry on 4/13/2017.
 */

public class connectSQL {
    private void writeMsg(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("message");
        myRef.setValue("Hello, World!");
    }


}
