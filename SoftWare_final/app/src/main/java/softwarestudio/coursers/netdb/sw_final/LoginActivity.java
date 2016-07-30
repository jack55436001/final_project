package softwarestudio.coursers.netdb.sw_final;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LoginActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    private AccessToken accessToken;
    //public static UserInfo user=new UserInfo();


    public static final String user = "USER";
    public static final String nameField = "NAME";
    public static final String genderField = "GENDER";
    public static final String birthdayField = "BIRTHDAY";
    public static final String emailField = "EMAIL";
    public static final String friendsField = "FRIENDS";
    public static final String idField = "ID";
    public static final String pictureField = "PICTURE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!FacebookSdk.isInitialized()){
            FacebookSdk.sdkInitialize(getApplicationContext());
            AppEventsLogger.activateApp(getApplication());
        }

        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        callbackManager = CallbackManager.Factory.create();
        final LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        //final Context mc=this.getApplicationContext();

        loginButton.setReadPermissions(Arrays.asList(
                "user_birthday","user_about_me","email","user_friends"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            //loginButton.setVisibility(View.INVISIBLE);
            //登入成功

            @Override
            public void onSuccess(LoginResult loginResult) {

                //accessToken之後或許還會用到 先存起來
                loginButton.setVisibility(View.INVISIBLE);
                accessToken = loginResult.getAccessToken();

                Log.d("FB","access token got.");

                //send request and call graph api

                GraphRequest request = GraphRequest.newMeRequest(
                        accessToken,
                        new GraphRequest.GraphJSONObjectCallback() {

                            //當RESPONSE回來的時候
                            //JSONArray putData=new JSONArray();
                            //JSONObject ob=new JSONObject();
                            JSONArray data=new JSONArray();
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {

                                //讀出姓名 ID FB個人頁面連結

                                Log.d("FB","complete");
                                try{
                                    Log.d("FB",object.optString("name"));
                                    Log.d("FB",object.optString("link"));
                                    Log.d("FB",object.optString("id"));
                                    Log.d("FB",object.optString("gender"));
                                    Log.d("FB",object.optString("birthday"));
                                    Log.d("FB",object.optString("email"));
                                    Log.d("FB",object.optString("friends"));

                                    JSONObject friend=object.getJSONObject("friends");
                                    data=friend.getJSONArray("data");


                                    String birth=object.optString("birthday");
                                    String [] births=birth.split("/");
                                    String birthday=births[2]+"-"+births[0]+"-"+births[1];


                                    SharedPreferences settings = getSharedPreferences(user,0);
                                    settings.edit()
                                            .putString(nameField, object.optString("name"))
                                            .putString(idField, object.optString("id"))
                                            .putString(emailField, object.optString("email"))
                                            .putString(birthdayField, birthday)
                                            .putString(genderField, object.optString("gender"))
                                            .putString(friendsField,data.toString())
                                            .putString(pictureField, object.getJSONObject("picture").getJSONObject("data").getString("url"))
                                            .commit();

                                    new AsyncTask<Void, Void, Void>(){
                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            try {
                                                //建立要傳送的JSON物件
                                                Log.d("UPLOAD","begin");
                                                JSONObject json = new JSONObject();
                                                SharedPreferences settings = getSharedPreferences(LoginActivity.user,0);

                                                json.put("id",settings.getString(idField,"") );
                                                json.put("name",settings.getString(nameField,""));
                                                json.put("gender",settings.getString(genderField,"") );
                                                json.put("birthday",settings.getString(birthdayField,"") );
                                                json.put("email",settings.getString(emailField,"") );
                                                json.put("friends",data);
                                                Log.d("FRIEND",json.getString("friends"));
                                                Log.d("PUT",json.toString());

                                                String s=json.toString();



                                                //建立POST Request
                                                HttpClient httpClient = new DefaultHttpClient();
                                                HttpPut httpPut = new HttpPut("http://140.114.221.30:8080/finalserver/user/");

                                                //JSON物件放到POST Request
                                                Log.d("PUT",s);
                                                StringEntity stringEntity = new StringEntity(s,HTTP.UTF_8);

                                                stringEntity.setContentType("application/json");
                                                stringEntity.setContentEncoding(HTTP.UTF_8);
                                                httpPut.setHeader("Content-Type", "application/json; charset=utf-8");
                                                httpPut.setEntity(stringEntity);

                                                //執行POST Request
                                                HttpResponse httpResponse = httpClient.execute(httpPut);

                                                //取得回傳的內容
                                                //HttpEntity httpEntity = httpResponse.getEntity();
                                                StatusLine statusLine=httpResponse.getStatusLine();
                                                int response=statusLine.getStatusCode();
                                                //String responseString = EntityUtils.toString(httpEntity, "UTF-8");
                                                //回傳的內容轉存為JSON物件
                                                //JSONObject responseJSON = new JSONObject(responseString);
                                                //取得Message的屬性
                                                //String Message = responseJSON.getString("message");

                                                Log.d("UPLOAD", Integer.toString(response));

                                                Log.d("UPLOAD","finish");


                                            } catch (Exception e) {
                                                e.printStackTrace();

                                            }
                                            return null;
                                        }
                                    }.execute(null, null, null);



                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                                    startActivity(intent);
                                    finish();
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }

                            }
                        });

                //包入你想要得到的資料 送出request

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,link,gender,birthday,email,friends,picture.type(large)");
                request.setParameters(parameters);
                request.executeAsync();
            }

            //登入取消

            @Override
            public void onCancel() {

                Log.d("FB","CANCEL");
            }

            //登入失敗

            @Override
            public void onError(FacebookException exception) {

                Log.d("FB",exception.toString());
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                SharedPreferences settings = getSharedPreferences(user,0);
                intent.putExtra(Intent.EXTRA_EMAIL,new String []{settings.getString(emailField, ""),"shanglun.tsai@gmail.com"});

                intent.putExtra(Intent.EXTRA_SUBJECT, "Suggestion");
                intent.putExtra(Intent.EXTRA_TEXT, "");

                startActivity(Intent.createChooser(intent, "Send Email To Developers"));
            }
        });
    }



    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);


    }




    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }
}
