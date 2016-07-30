package softwarestudio.coursers.netdb.sw_final;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class SplashActivity extends AppCompatActivity {
    private Intent mLoginIntent;
    private Intent mMainIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mMainIntent = new Intent(this, MainActivity.class);
        mLoginIntent = new Intent(this, LoginActivity.class);

        FacebookSdk.sdkInitialize(getApplicationContext(), new FacebookSdk.InitializeCallback() {
            @Override
            public void onInitialized() {
                checkFbLogin(AccessToken.getCurrentAccessToken());
            }
        });
        AppEventsLogger.activateApp(getApplication());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                SharedPreferences settings = getSharedPreferences(LoginActivity.user,0);
                intent.putExtra(Intent.EXTRA_EMAIL,new String []{settings.getString(LoginActivity.emailField,""),"shanglun.tsai@gmail.com"});

                intent.putExtra(Intent.EXTRA_SUBJECT, "Suggestion");
                intent.putExtra(Intent.EXTRA_TEXT, "");

                startActivity(Intent.createChooser(intent, "Send Email To Developers"));
            }
        });
    }

    private void checkFbLogin(AccessToken currentToken){
        if(currentToken != null){

            startActivity(mMainIntent);
        }else{

            startActivity(mLoginIntent);
        }
        finish();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

}
