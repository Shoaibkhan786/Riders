package com.example.muhammadkhan.ridersapp.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import com.example.muhammadkhan.ridersapp.R;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import java.sql.Driver;
import java.util.Arrays;
import java.util.List;
import static com.firebase.ui.auth.AuthUI.EXTRA_DEFAULT_COUNTRY_CODE;

public class LoginActivity extends AppCompatActivity {
    SharedPreferences preferences;
    private static final int RC_SIGN_IN = 123;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            //method call if user is not already registered
            signInProcess();
    }
    public void signInProcess(){
        // setting local country code
        Bundle params = new Bundle();
        params.putString(EXTRA_DEFAULT_COUNTRY_CODE, "PK");

        List<AuthUI.IdpConfig> phoneConfigWithDefaultNumber =Arrays.asList(
                new AuthUI.IdpConfig.Builder(AuthUI.PHONE_VERIFICATION_PROVIDER)
                        .setParams(params)
                        .build());
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.customeTheme)
                        .setAvailableProviders(phoneConfigWithDefaultNumber)
                        .build(),
                RC_SIGN_IN);
    }
    //handing the sign in information
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                // getting value stored in shared perference
                preferences = PreferenceManager.getDefaultSharedPreferences(this);
                String key = preferences.getString("key", "");
                if(key.equals("AS Driver")){
                    intent = new Intent(this, DriverProfile.class);
                    startActivity(intent);
                }else{
                    intent=new Intent(this,PassengerProfile.class);
                    startActivity(intent);
                }

            } else {
                Toast.makeText(this, "Signing in issues", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

