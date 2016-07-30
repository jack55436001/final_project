package softwarestudio.coursers.netdb.sw_final;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import android.support.design.widget.FloatingActionButton;

import com.alibaba.fastjson.JSON;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.support.design.widget.CollapsingToolbarLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by shanglun on 2016/6/22.
 */
public class DetailEvent extends AppCompatActivity implements OnMapReadyCallback{
    private GoogleMap mMap;
    private  double lat, lng;
    private String location;
    private int group_id;
    private String masterid;
    private Group groups=new Group();
    private String title;
    private String detail;
    private String date;
    int cur_people;
    private String tag;
    private int maxpeople;
    private int minpeople;
    private  String time;
    private boolean expire;
    private boolean success;
    private List<String> join=new ArrayList<String>();
    private int size;
    @Override
    public void onCreate(Bundle onSavedInstanceState){
        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.detail_event);

        CircleImageView profile=(CircleImageView)findViewById(R.id.event_class_image);
        final FloatingActionButton fab1 = (FloatingActionButton)findViewById(R.id.fab_plus_one);



        lat=getIntent().getDoubleExtra("lat",0);
        lng=getIntent().getDoubleExtra("lng",0);
        title=getIntent().getStringExtra("title");
        detail=getIntent().getStringExtra("detail");
        date=getIntent().getStringExtra("date");
        cur_people=getIntent().getIntExtra("cur_people",0);
        location=getIntent().getStringExtra("location");
        tag=getIntent().getStringExtra("tag");
        Log.d("detail",tag);
        masterid=getIntent().getStringExtra("masterid");
        maxpeople=getIntent().getIntExtra("maxpeople",0);
        minpeople=getIntent().getIntExtra("minpeople",0);
        time=getIntent().getStringExtra("time");
        size=getIntent().getIntExtra("size",0);
        for(int i=0;i<size;i++)
        {
            join.add(getIntent().getStringExtra("join"+Integer.toString(i)));
        }

        expire=getIntent().getBooleanExtra("expire",false);
        success=getIntent().getBooleanExtra("success",false);
        Bitmap image=getIntent().getParcelableExtra("image");
        group_id=getIntent().getIntExtra("group_id",0);

        //jsongroup=getIntent().getStringExtra("group");

        //groups= com.alibaba.fastjson.JSON.toJavaObject(com.alibaba.fastjson.JSON.parseObject(jsongroup),Group.class);
        setUpMapIfNeeded();
       if(tag.equals("吃吃喝喝") ){
            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_restaurant_white_48dp);
            profile.setImageDrawable(myDrawable);
        }else if(tag.equals("旅行") ){
            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_flight_white_48dp);
            profile.setImageDrawable(myDrawable);
        }else if(tag.equals("派對")  ){
            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_local_bar_white_48dp);
            profile.setImageDrawable(myDrawable);
        }else if(tag.equals("運動") ){
            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_directions_bike_white_48dp);
            profile.setImageDrawable(myDrawable);
        }else if(tag.equals("電影") ){
            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_local_movies_white_48dp);
            profile.setImageDrawable(myDrawable);
        }else if(tag.equals("唱歌") ){
            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_music_note_white_48dp);
            profile.setImageDrawable(myDrawable);
        }else{
            Drawable myDrawable = getResources().getDrawable(R.drawable.ic_casino_white_48dp);
            profile.setImageDrawable(myDrawable);
        }

        TextView mDetailTitle = (TextView)findViewById(R.id.detail_event_title_text);
        TextView mDetailDetail = (TextView)findViewById(R.id.detail_detail_text) ;
        TextView mDetailDate = (TextView)findViewById(R.id.detail_date_text);
        TextView mDetailTime = (TextView)findViewById(R.id.detail_time_text);
        TextView mDetailLocation = (TextView)findViewById(R.id.detail_location_text);
        TextView mDetailNumber = (TextView)findViewById(R.id.detail_number_text);
        ImageView mMaster = (ImageView)findViewById(R.id.master);

        mDetailDate.setText(date);
        mDetailDetail.setText(detail);
        mDetailLocation.setText(location);
        mDetailTime.setText(time);
        mDetailTitle.setText(title);
        mMaster.setImageBitmap(image);
        mDetailNumber.setText(getString(R.string.set_number, cur_people, maxpeople));
        SharedPreferences settings = getSharedPreferences(LoginActivity.user,0);
        if(cur_people==maxpeople||settings.getString(LoginActivity.idField,"").equals(masterid)||join.contains(settings.getString(LoginActivity.idField,"")))
        {
            fab1.setVisibility(View.INVISIBLE);
        }
        else
        {
            fab1.setVisibility(View.VISIBLE);
        }

        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AsyncTask<Void,Void,Void>()
                {
                    @Override
                    protected Void doInBackground(Void... params) {
                        try{
                            SharedPreferences settings = getSharedPreferences(LoginActivity.user,0);
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPut httpPut = new HttpPut("http://140.114.221.30:8080/finalserver/group/join?groupId="+Integer.toString(group_id)+"&userId="+settings.getString(LoginActivity.idField,""));
                        HttpResponse httpResponse = httpClient.execute(httpPut);

                        StatusLine statusLine=httpResponse.getStatusLine();
                        int response=statusLine.getStatusCode();

                        Log.d("JoinPut", Integer.toString(response));

                        Log.d("JoinPut","finish");

                            HttpClient httpClient2 = new DefaultHttpClient();
                            HttpPost httpPost = new HttpPost("http://140.114.221.30:8080/finalserver/group/");

                            groups.setCur_people_num(cur_people+1);
                            groups.setArticle(detail);
                            groups.setLng(lng);
                            groups.setLat(lat);
                            groups.setTitle(title);
                            groups.setDate(date);
                            groups.setExpired(expire);
                            join.add(settings.getString(LoginActivity.idField,""));
                            groups.setJoin(join);
                            groups.setId(group_id);
                            groups.setLocation(location);
                            groups.setPeople_max(maxpeople);
                            groups.setPeople_min(minpeople);
                            groups.setSuccess(success);
                            groups.setTime(time);
                            groups.setMasterId(masterid);
                            groups.setTag(tag);
                            StringEntity stringEntity = new StringEntity(JSON.toJSONString(groups),HTTP.UTF_8);

                            stringEntity.setContentType("application/json");
                            stringEntity.setContentEncoding(HTTP.UTF_8);
                            httpPost.setHeader("Content-Type", "application/json; charset=utf-8");
                            httpPost.setEntity(stringEntity);

                            HttpResponse httpResponse2 = httpClient2.execute(httpPost);


                            StatusLine statusLine2=httpResponse2.getStatusLine();
                            int response2=statusLine2.getStatusCode();


                            Log.d("JoinPost", Integer.toString(response2));

                            Log.d("JoinPost","finish");



                        } catch (Exception e) {
                        e.printStackTrace();

                        }
                        return null;
                    }

                }.execute(null, null, null);

                startActivity(new Intent(DetailEvent.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }
        });


    }
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }
    private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        if (mMap == null) {
            return;
        }
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng latLng=new LatLng(lat, lng);

        moveMap(latLng);
        // Initialize map options. For example:
        // mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }
    @Override
    public void onMapReady(GoogleMap map) {
        map.addMarker(new MarkerOptions()
                .position(new LatLng(lat,lng))
                .title(location));
    }
    private void moveMap(LatLng place) {
        // 建立地圖攝影機的位置物件
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(place)
                        .zoom(17)
                        .build();

        // 使用動畫的效果移動地圖
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}

