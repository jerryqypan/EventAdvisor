package cs290final.eventadvisor.backend;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Jerry on 4/13/2017.
 */

public class Authentication {
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    public void onCreate(){
        mAuth = FirebaseAuth.getInstance();
    }
}
