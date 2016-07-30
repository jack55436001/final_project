package softwarestudio.coursers.netdb.sw_final;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;

public class Logout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LoginButton loginButton=(LoginButton)findViewById(R.id.logout_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
                     public void onClick(View v) {
                            LoginManager.getInstance().logOut();
                            goodbye();
                        }
               });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                SharedPreferences settings = getSharedPreferences(LoginActivity.user,0);
                intent.putExtra(Intent.EXTRA_EMAIL,new String []{settings.getString(LoginActivity.emailField, ""),"shanglun.tsai@gmail.com"});

                intent.putExtra(Intent.EXTRA_SUBJECT, "Suggestion");
                intent.putExtra(Intent.EXTRA_TEXT, "");

                startActivity(Intent.createChooser(intent, "Send Email To Developers"));
            }
        });
    }
    public void goodbye()
    {
        Intent intent=new Intent(this,LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
