package softwarestudio.coursers.netdb.sw_final;

import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.SeekBar;
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
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.github.clans.fab.FloatingActionButton;
import com.alibaba.fastjson.JSON;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private DrawerLayout mDrawerLayout;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ProgressDialog progressDialog;
    private Intent mAddEventIntent;
    private boolean tab_count=true;
    public static List<Group> groups=new ArrayList<Group>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);


        mAddEventIntent = new Intent(this, AddEventActivity. class);
        FloatingActionButton fab1 = (FloatingActionButton)findViewById(R.id.fab_add_event);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mAddEventIntent);
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton)findViewById(R.id.fab_add_group);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                SharedPreferences settings = getSharedPreferences(LoginActivity.user,0);
                intent.putExtra(Intent.EXTRA_EMAIL,new String []{settings.getString(LoginActivity.emailField,""),"shanglun.tsai@gmail.com"});

                intent.putExtra(Intent.EXTRA_SUBJECT, "Suggestion");
                intent.putExtra(Intent.EXTRA_TEXT, "");

                startActivity(Intent.createChooser(intent, "Send Email To Developers"));

            }
        });

        SharedPreferences settings = getSharedPreferences(LoginActivity.user,0);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();



        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        initViewPager();
        //initSwipeRefresh();
        CircleImageView profile=(CircleImageView)headerLayout.findViewById(R.id.profile_image);
        try {
            URL facebookProfileURL = new URL(settings.getString(LoginActivity.pictureField," "));
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setTitle("使用者匯入");
            progressDialog.setMessage("匯入中....請稍後");
            progressDialog.setCancelable(false);
            Bitmap bitmap = new myTask().execute(facebookProfileURL).get();
            profile.setImageBitmap(bitmap);
            TextView name=(TextView)headerLayout.findViewById(R.id.name);
            name.setText(settings.getString(LoginActivity.nameField,""));
            TextView email=(TextView)headerLayout.findViewById(R.id.email);
            email.setText(settings.getString(LoginActivity.emailField,""));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userDataRefresh();
                startActivity(new Intent(MainActivity.this,MainActivity.class));
                finish();
            }
        });



    }
    private void initSwipeRefresh(){
        //mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener(){
                @Override
                public void onRefresh(){
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        );
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    private void initViewPager(){
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        MainPagerAdapter mPagerAdapter=new MainPagerAdapter(getSupportFragmentManager(), MainActivity.this);
        if(tab_count)
        {
            mTabLayout.addTab(mTabLayout.newTab().setText("Current event"));
            mTabLayout.addTab(mTabLayout.newTab().setText("Recent event"));
            mTabLayout.addTab(mTabLayout.newTab().setText("Hot event"));
            tab_count=false;
        }
        try{
            groups=new groupTask().execute().get();
            mViewPager = (ViewPager) findViewById(R.id.viewpager);

            mViewPager.setAdapter( mPagerAdapter);
            mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));
            mTabLayout.setupWithViewPager(mViewPager);

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private class MainPagerAdapter extends FragmentPagerAdapter {
        final int PAGE_COUNT = 3;
        private Context context;
        private String tabTitles[] = new String[] { "最新揪團", "加入的揪團", "我的揪團" };
        public MainPagerAdapter(FragmentManager fm, Context context){
            super(fm);
            this.context = context;
        }

        @Override
        public int getCount(){
            return PAGE_COUNT;
        }


        @Override
        public Fragment getItem(int position){

            return PageFragment.newInstance(position + 1,groups);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            // Generate title based on item position
            return tabTitles[position];
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem menuSearchItem = menu.findItem(R.id.my_search);
        SearchManager searchManager = (SearchManager)getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menuSearchItem.getActionView();

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_game) {
            // Handle the camera action
        } else if(id == R.id.nav_all){

        }
        else if (id == R.id.nav_travel) {

        } else if (id == R.id.nav_sport) {

        } else if (id == R.id.nav_party) {

        } else if(id == R.id.nav_eat){

        }else if(id == R.id.nav_singing){

        }else if(id == R.id.nav_movie){

        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(this, SettingsActivity.class));
        } else if (id == R.id.nav_logout) {
            startActivity(new Intent(this,Logout.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private class groupTask extends AsyncTask<Void,Void,List<Group>>{
        SharedPreferences settings = getSharedPreferences(LoginActivity.user,0);

        protected List<Group> doInBackground(Void... params) {

            return getGroup(settings.getString(LoginActivity.idField,""));
        }
        protected void onPostExecute(List<Group> group) {
        }
    }
    public List<Group> getGroup(String id)
    {
        try{
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("http://140.114.221.30:8080/finalserver/group/all?userId="+id);

            HttpResponse response = client.execute(httpGet);

            HttpEntity resEntity = response.getEntity();

            String result = EntityUtils.toString(resEntity, HTTP.UTF_8);
            //Log.d("GETGROUP",result);
            com.alibaba.fastjson.JSONArray group=com.alibaba.fastjson.JSONArray.parseArray(result);
            Group [] groups;
            groups= com.alibaba.fastjson.JSONArray.toJavaObject(group,Group[].class);

            //Log.d("GETGROUP",Integer.toString(group.size()));
            //groups=group.toArray(groups);
            List<Group> groupList=new ArrayList<Group>();
            for(int i=0;i<group.size();i++){
                String masterId = groups[i].getMasterId();
                URL url = new URL("https://graph.facebook.com/" + masterId + "/picture?type=large");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setFollowRedirects(HttpURLConnection.getFollowRedirects());
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap image = BitmapFactory.decodeStream(input);
                groups[i].setImage(image);
                //Log.d("Recycle",groups[i].getTitle());
                groupList.add(groups[i]);
            }
            return groupList;

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private class myTask extends AsyncTask<URL,Void,Bitmap>{


        protected Bitmap doInBackground(URL... params) {
            Bitmap bitmap;
            try {
                //userDataRefresh();
                bitmap= BitmapFactory.decodeStream(params[0].openConnection().getInputStream());
                return bitmap;
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return null;
            }

        }
        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            progressDialog.show();
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPreExecute();
            progressDialog.dismiss();

        }
    }
    public void putUser()
    {
        try {

            JSONObject json = new JSONObject();
            SharedPreferences settings = getSharedPreferences(LoginActivity.user,0);

            json.put("id",settings.getString(LoginActivity.idField,"") );
            json.put("name",settings.getString(LoginActivity.nameField,""));
            json.put("gender",settings.getString(LoginActivity.genderField,"") );
            json.put("birthday",settings.getString(LoginActivity.birthdayField,"") );
            json.put("email",settings.getString(LoginActivity.emailField,"") );
            json.put("friends",settings.getString(LoginActivity.friendsField,"") );

            Log.d("USER",json.getString("friends"));
            Log.d("USER",json.toString());

            String s=json.toString();




            HttpClient httpClient = new DefaultHttpClient();
            HttpPut httpPut = new HttpPut("http://140.114.221.30:8080/finalserver/user/");


            StringEntity stringEntity = new StringEntity(s,HTTP.UTF_8);

            stringEntity.setContentType("application/json");
            stringEntity.setContentEncoding(HTTP.UTF_8);
            httpPut.setHeader("Content-Type", "application/json; charset=utf-8");
            httpPut.setEntity(stringEntity);


            HttpResponse httpResponse = httpClient.execute(httpPut);


            StatusLine statusLine=httpResponse.getStatusLine();
            int response=statusLine.getStatusCode();


            Log.d("USER", Integer.toString(response));

            Log.d("USER","finish");


        } catch (Exception e) {
            e.printStackTrace();

        }

    }
    public void getUser(String id)
    {
        try{
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("http://140.114.221.30:8080/finalserver/user?userId="+id);

            HttpResponse response = client.execute(httpGet);

            HttpEntity resEntity = response.getEntity();
            String result = EntityUtils.toString(resEntity, HTTP.UTF_8);
            Log.d("USER",result);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void getMyGroup(String id)
    {
        try{
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet("http://140.114.221.30:8080/finalserver/group/me?userId="+id);

            HttpResponse response = client.execute(httpGet);

            HttpEntity resEntity = response.getEntity();
            String result = EntityUtils.toString(resEntity, HTTP.UTF_8);
            Log.d("MYGROUP",result);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void userDataRefresh() {

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    JSONArray data = new JSONArray();

                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {

                            JSONObject friend = object.getJSONObject("friends");
                            data = friend.getJSONArray("data");


                            String birth = object.optString("birthday");
                            String[] births = birth.split("/");
                            String birthday = births[2] + "-" + births[0] + "-" + births[1];


                            SharedPreferences settings = getSharedPreferences(LoginActivity.user, 0);
                            settings.edit()
                                    .putString(LoginActivity.nameField, object.optString("name"))
                                    .putString(LoginActivity.idField, object.optString("id"))
                                    .putString(LoginActivity.emailField, object.optString("email"))
                                    .putString(LoginActivity.birthdayField, birthday)
                                    .putString(LoginActivity.genderField, object.optString("gender"))
                                    .putString(LoginActivity.friendsField, data.toString())
                                    .putString(LoginActivity.pictureField, object.getJSONObject("picture").getJSONObject("data").getString("url"))
                                    .commit();
                            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                            View headerLayout = navigationView.getHeaderView(0);
                            navigationView.setNavigationItemSelectedListener(MainActivity.this);
                            initViewPager();
                            //initSwipeRefresh();
                            CircleImageView profile = (CircleImageView) headerLayout.findViewById(R.id.profile_image);
                            URL facebookProfileURL = new URL(settings.getString(LoginActivity.pictureField, " "));
                            progressDialog = new ProgressDialog(MainActivity.this);
                            progressDialog.setTitle("使用者匯入");
                            progressDialog.setMessage("匯入中....請稍後");
                            progressDialog.setCancelable(false);
                            Bitmap bitmap = new myTask().execute(facebookProfileURL).get();
                            profile.setImageBitmap(bitmap);

                            Log.d("REFRESH",object.optString("name"));
                            Log.d("REFRESH",object.optString("link"));
                            Log.d("REFRESH",object.optString("id"));
                            Log.d("REFRESH",object.optString("gender"));
                            Log.d("REFRESH",object.optString("birthday"));
                            Log.d("REFRESH",object.optString("email"));
                            Log.d("REFRESH",object.optString("friends"));
                            new AsyncTask<Void, Void, Void>(){
                                @Override
                                protected Void doInBackground(Void... params) {
                                    try {
                                        //建立要傳送的JSON物件
                                        Log.d("UPLOAD","begin");
                                        JSONObject json = new JSONObject();
                                        SharedPreferences settings = getSharedPreferences(LoginActivity.user,0);

                                        json.put("id",settings.getString(LoginActivity.idField,"") );
                                        json.put("name",settings.getString(LoginActivity.nameField,""));
                                        json.put("gender",settings.getString(LoginActivity.genderField,"") );
                                        json.put("birthday",settings.getString(LoginActivity.birthdayField,"") );
                                        json.put("email",settings.getString(LoginActivity.emailField,"") );
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

                            Toast.makeText(MainActivity.this, "更新個人資訊完成", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,gender,birthday,email,friends,picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
