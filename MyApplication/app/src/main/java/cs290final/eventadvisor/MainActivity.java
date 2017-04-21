package cs290final.eventadvisor;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.ResultCodes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int RC_SIGN_IN = 9001;

    private View mRootView;
    private View mInitView;

    private final Runnable revealLogoAnimation = new Runnable() {
        @Override
        public void run() {
            int cx = mInitView.getWidth() / 2;
            int cy = mInitView.getHeight() / 2;
            float finalRadius = (float) Math.hypot(cx, cy);
            // Get the final radius for the clipping circle
            // Create the animator for this view (the start radius is zero)
            Animator anim = ViewAnimationUtils.createCircularReveal(mInitView, cx, cy, 0, finalRadius);
            anim.setDuration(1000);
            anim.start();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRootView = (View) findViewById(android.R.id.content);
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is already signed in, take them to the map activity directly
            startActivity(new Intent(this, MapsActivity.class));
        } else {
            initiateSignIn();
        }
    }

    private void initiateSignIn() {
        // Get the previously invisity logo view
        mInitView = (RelativeLayout) findViewById(R.id.init_view);
        // make the view visible and start the animation

        mInitView.post(revealLogoAnimation);
        mInitView.setVisibility(View.VISIBLE);
    }

    public void onStartClick(View view) {
        Log.d(TAG, "onStartClick: Clicked on 'Get Started' Button");
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG)
                        .setProviders(Arrays.asList(new AuthUI.IdpConfig.Builder(AuthUI.EMAIL_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build(),
                                new AuthUI.IdpConfig.Builder(AuthUI.FACEBOOK_PROVIDER).build()))
                        .setTheme(R.style.SplashTheme)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            handleSignInResponse(resultCode, data);
            return;
        }
        showSnackbar(R.string.unknown_response);
    }

    private void handleSignInResponse(int resultCode, Intent data) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        // Successfully signed in
        if (resultCode == ResultCodes.OK) {
            startActivity(MapsActivity.createIntent(this, response));
            finish();
            return;
        } else {
            // Sign in Failed
            if (response == null) {
                // User pressed back button
                showSnackbar(R.string.sign_in_cancelled);
                return;
            }

            if (response.getErrorCode() == ErrorCodes.NO_NETWORK) {
                showSnackbar(R.string.no_internet_connection);
                return;
            }

            if (response.getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                showSnackbar(R.string.unknown_error);
                return;
            }
        }

        showSnackbar(R.string.unknown_sign_in_response);
    }

    private void showSnackbar(@StringRes int errorMessageRes) {
        Snackbar.make(mRootView, errorMessageRes, Snackbar.LENGTH_LONG).show();
    }

    public static Intent createIntent(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, MainActivity.class);
        return intent;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    public void createEventAction(View view){
        System.out.println("Create Event Activity");
        startActivity(new Intent(this, CreateEventActivity.class));
    }
}
