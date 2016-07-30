package softwarestudio.coursers.netdb.sw_final;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shanglun on 2016/6/22.
 */
public class PageFragment extends Fragment {
    private RecyclerViewAdapter mGroupAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    public static final String ARG_PAGE = "ARG_PAGE";
    public static List<Group> groups;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    public static PageFragment newInstance(int page,List<Group> g) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        PageFragment fragment = new PageFragment();
        fragment.setArguments(args);
        groups=g;
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){


        View rootView = inflater.inflate(R.layout.page_fragment, container, false);
        RecyclerView mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_view);
        try{
            int pos=this.getArguments().getInt("ARG_PAGE");
            List<Group> group=new ArrayList<Group>();
            SharedPreferences setting= this.getActivity().getSharedPreferences(LoginActivity.user,0);
            Log.d("Fragment",Integer.toString(pos));
            if(pos == 1){
                Log.d("Filter",Integer.toString(groups.size()));
                for(int i = 0; i < groups.size();i++){
                    group.add(groups.get(i));
                }
            }else if(pos == 2){
                for(int i = 0; i < groups.size(); i++){
                    if(groups.get(i).getJoin().contains(setting.getString(LoginActivity.idField,"")))
                    {
                        group.add(groups.get(i));
                    }
                }

            }else{
                for(int i = 0; i < groups.size(); ++i){
                    if(groups.get(i).getMasterId().equals(setting.getString(LoginActivity.idField,"")))
                    {
                        group.add(groups.get(i));
                    }
                }
            }
            mGroupAdapter = new RecyclerViewAdapter(getActivity(),group);
        }catch(Exception e) {
            e.printStackTrace();
        }
        mLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mGroupAdapter);
        // Get a reference to the ListView, and attach this adapter to it.



        return rootView;

    }


}
