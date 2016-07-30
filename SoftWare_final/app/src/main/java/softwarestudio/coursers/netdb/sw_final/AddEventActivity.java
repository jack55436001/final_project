package softwarestudio.coursers.netdb.sw_final;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.radialtimepicker.RadialTimePickerDialogFragment;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jerry_000 on 2016/6/20.
 */
public class AddEventActivity extends AppCompatActivity
        implements CalendarDatePickerDialogFragment.OnDateSetListener,
        RadialTimePickerDialogFragment.OnTimeSetListener
        {
    protected TextView mDateText, mTimeText, mLocationText, mLatLng;
    protected TextInputEditText mDetailText, mTitleText;
    private Spinner mSpinner;
    private  int mMin = 0, mMax = 0;
    private Intent mPlacePickerIntent;
    private GoogleApiClient mGoogleApiClient;
    private Button createButton;
    private final int PLACE_PICKER_REQUEST = 1;
    private ArrayAdapter<String> classList;
    private SeekBar mMinSeekBar, mMaxSeekBar;
    private double lat, lng;
    private TextView  mMinNumber, mMaxNumber;
    private String[] classify = {"桌遊", "運動", "旅遊", "電影", "唱歌", "派對", "吃吃喝喝"};
    private static final String FRAG_TAG_DATE_PICKER = "fragment_date_picker_name";
    private static final String FRAG_TAG_TIME_PICKER = "timePickerDialogFragment";
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_event);
        mDateText = (TextView)findViewById(R.id.date_text);
        mTimeText = (TextView)findViewById(R.id.time_text);
        mLocationText = (TextView)findViewById(R.id.place_text) ;

        mMinSeekBar = (SeekBar)findViewById(R.id.min_seek_bar);
        mMaxSeekBar = (SeekBar)findViewById(R.id.max_seek_bar);
        mMinNumber = (TextView)findViewById(R.id.min_number);
        mMaxNumber = (TextView)findViewById(R.id.max_number);
        mMinNumber.setText( "最低人數: " + mMinSeekBar.getProgress() );
        mMaxNumber.setText("最高人數: " + mMaxSeekBar.getProgress() );

        mMinSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMin = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMinNumber.setText("最低人數: " + mMin );
            }
        });

        mMaxSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMax = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMaxNumber.setText("最高人數: " + mMax );
            }
        });

        Button dateChooseButton = (Button)findViewById(R.id.set_date_button);
        dateChooseButton.setText(R.string.calendar_date_picker_set);
        dateChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setOnDateSetListener(AddEventActivity.this);
                cdp.show(getSupportFragmentManager(), FRAG_TAG_DATE_PICKER);
            }
        });
        Button timeChooseButton = (Button)findViewById(R.id.set_time_button);
        timeChooseButton.setText(R.string.radial_time_picker);
        timeChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadialTimePickerDialogFragment rtpd = new RadialTimePickerDialogFragment()
                        .setOnTimeSetListener(AddEventActivity.this);
                rtpd.show(getSupportFragmentManager(), FRAG_TAG_TIME_PICKER);
            }
        });
        Button placeChooseButton = (Button)findViewById(R.id.set_place_button);
        placeChooseButton.setText("設定地點");
        placeChooseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    mPlacePickerIntent = builder.build( getApplicationContext());
                    startActivityForResult(mPlacePickerIntent , PLACE_PICKER_REQUEST );
                } catch ( GooglePlayServicesRepairableException e ) {
                    Log.d( "PlacesAPI Demo", "GooglePlayServicesRepairableException thrown" );
                } catch ( GooglePlayServicesNotAvailableException e ) {
                    Log.d( "PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown" );
                }
            }
        });
        mSpinner = (Spinner)findViewById(R.id.class_spinner);
        classList = new ArrayAdapter<String>(AddEventActivity.this, R.layout.spinner_text, classify);
        mSpinner.setAdapter(classList);

        mTitleText = (TextInputEditText)findViewById(R.id.title_text_input);
        mDetailText = (TextInputEditText)findViewById(R.id.detail_text_input);

        createButton = (Button)findViewById(R.id.create_button);
        createButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                createEvent();
            }
        });
    }

    protected void onActivityResult( int requestCode, int resultCode, Intent data ) {
        if( requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK ) {
            displayPlace( PlacePicker.getPlace( data, this ) );
        }
    }

    private void displayPlace( Place place ) {
        if( place == null )
            return;

        String place_name = "";

        if( !TextUtils.isEmpty( place.getName() ) ) {
            place_name +=  place.getName() ;
        }
        if( !TextUtils.isEmpty(place.getLatLng().toString()) ) {
            lat  =  place.getLatLng().latitude ;
            lng = place.getLatLng().longitude;
        }


        mLocationText.setText( place_name );
    }


    @Override
    protected void onStart() {
        super.onStart();
        if( mGoogleApiClient != null )
            mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if( mGoogleApiClient != null && mGoogleApiClient.isConnected() ) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    public void createEvent() {

        if (!validate()) {
            onCreateFailed();
            return;
        }

        Log.d("ADDEVENT",mDateText.getText().toString());
        Log.d("ADDEVENT",mDetailText.getText().toString());
        Log.d("ADDEVENT",mLocationText.getText().toString());
        Log.d("ADDEVENT",mTitleText.getText().toString());
        Log.d("ADDEVENT",mTimeText.getText().toString());
        Log.d("ADDEVENT",mMinNumber.getText().toString());
        Log.d("ADDEVENT",mMaxNumber.getText().toString());
        Log.d("ADDEVENT",mSpinner.getSelectedItem().toString());

        String date=mDateText.getText().toString();

        String [] date_split=date.split(" ");
        String formatdate=date_split[0]+"-"+date_split[2]+"-"+date_split[4];
        Log.d("GROUP",formatdate);
        final SharedPreferences settings = getSharedPreferences(LoginActivity.user,0);

        List<String> join=new ArrayList<String>();
        Group group=new Group();
        group.setArticle(mDetailText.getText().toString());
        group.setCur_people_num(0);
        group.setDate(formatdate);
        group.setExpired(false);
        group.setLat(lat);
        group.setLng(lng);
        group.setLocation(mLocationText.getText().toString());
        group.setMasterId(settings.getString(LoginActivity.idField,""));
        group.setPeople_min(mMin);
        group.setSuccess(false);
        group.setPeople_max(mMax);
        group.setTag(mSpinner.getSelectedItem().toString());
        group.setTime(mTimeText.getText().toString()+":00");
        group.setTitle(mTitleText.getText().toString());

        String groupJSON= JSON.toJSONString(group);
        settings.edit().putString("Group",groupJSON).commit();
        Log.d("ADDEVENT",settings.getString("Group",""));

         new AsyncTask<Void, Void, Void>(){
                                        @Override
                                        protected Void doInBackground(Void... params) {
                                            putGroup(settings.getString("Group",""));
                                            return null;
                                        }
                                    }.execute(null, null, null);

        createButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(AddEventActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();



        // TODO: Implement your own authentication logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        onCreateSuccess();
                        // onLoginFailed();
                        progressDialog.dismiss();

                    }
                }, 3000);

    }
    public void onCreateSuccess(){
        //TODO : put data to server
        createButton.setEnabled(true);
        startActivity(new Intent(AddEventActivity.this,MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
        finish();
    }
    public void onCreateFailed(){
        Toast.makeText(getBaseContext(), "創建失敗", Toast.LENGTH_LONG).show();
        createButton.setEnabled(true);
    }
    public boolean validate() {
        boolean valid = true;

        String mTime = mTimeText.getText().toString();
        String mDate = mDateText.getText().toString();
        String mLocation = mLocationText.getText().toString();
        String mTitle = mTitleText.getText().toString();
        String mDetail = mDetailText.getText().toString();

        if (mTime.isEmpty() ) {
            mTimeText.setError("請設置時間");
            valid = false;
        } else {
            mTimeText.setError(null);
        }
        if (mDate.isEmpty() ) {
            mDateText.setError("請設置時間");
            valid = false;
        } else {
            mDateText.setError(null);
        }
        if(mMin > mMax||mMin==0||mMax==0){
            mMaxNumber.setError("錯誤人數");
            mMinNumber.setError("錯誤人數");
            valid = false;
        }else{
            mMaxNumber.setError(null);
            mMinNumber.setError(null);
        }
        if (mLocation.isEmpty() ) {
            mLocationText.setError("請設置時間");
            valid = false;
        } else {
            mLocationText.setError(null);
        }

        if (mTitle.isEmpty() || mTitle.length() > 30) {
            mTitleText.setError("請取一個30字以內的標題");
            valid = false;
        } else {
            mTitleText.setError(null);
        }

        if (mDetail.isEmpty()) {
            mDetailText.setError("內文不得空白");
            valid = false;
        }else if(mDetail.length() > 250){
            mDetailText.setError("內文不得超過250字");
            valid = false;
        } else {
            mDetailText.setError(null);
        }

        return valid;
    }
    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int year, int monthOfYear, int dayOfMonth) {
        mDateText.setText(getString(R.string.calendar_date_picker_result_values, year, monthOfYear + 1, dayOfMonth));
    }
    @Override
    public void onTimeSet(RadialTimePickerDialogFragment dialog, int hourOfDay, int minute) {
        if((hourOfDay / 10 != 0) && (minute / 10 != 0)){
            mTimeText.setText(getString(R.string.radial_time_picker_result_value,  ((Integer)hourOfDay).toString(), ((Integer)minute)).toString());
        }else if((hourOfDay / 10 != 0) && (minute / 10 == 0)){
            mTimeText.setText(getString(R.string.radial_time_picker_result_value,  ((Integer)hourOfDay).toString(), "0" + ((Integer)minute)).toString());
        }else if((hourOfDay / 10 == 0) && (minute / 10 != 0)){
            mTimeText.setText(getString(R.string.radial_time_picker_result_value,"0"  + ((Integer)hourOfDay).toString(), ((Integer)minute)).toString());
        }else{
            mTimeText.setText(getString(R.string.radial_time_picker_result_value,"0" + ((Integer)hourOfDay).toString(),"0" +  ((Integer)minute)).toString());
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        CalendarDatePickerDialogFragment calendarDatePickerDialogFragment = (CalendarDatePickerDialogFragment) getSupportFragmentManager()
                .findFragmentByTag(FRAG_TAG_DATE_PICKER);
        if (calendarDatePickerDialogFragment != null) {
            calendarDatePickerDialogFragment.setOnDateSetListener(this);
        }
    }
            public void putGroup(String groupJSON)
            {



                try {
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPut httpPut = new HttpPut("http://140.114.221.30:8080/finalserver/group/");

                    StringEntity stringEntity = new StringEntity(groupJSON, HTTP.UTF_8);

                    stringEntity.setContentType("application/json");
                    stringEntity.setContentEncoding(HTTP.UTF_8);
                    httpPut.setHeader("Content-Type", "application/json; charset=utf-8");
                    httpPut.setEntity(stringEntity);


                    HttpResponse httpResponse = httpClient.execute(httpPut);


                    StatusLine statusLine = httpResponse.getStatusLine();
                    int response = statusLine.getStatusCode();


                    Log.d("GROUP", Integer.toString(response));

                    Log.d("GROUP", "finish");
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
}
