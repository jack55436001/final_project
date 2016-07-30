package softwarestudio.coursers.netdb.sw_final;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by shanglun on 2016/6/22.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.EventViewHolder>{
    private List<Group> mGroupList;
    private Activity mActivity;
    static Context context;

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        static TextView titleView, detailView;
        static ImageView eventImage;
        Group group;
        EventViewHolder(View view){
            super(view);
            view.findViewById(R.id.cv);
            titleView = (TextView)view.findViewById(R.id.event_title);
            detailView = (TextView)view.findViewById(R.id.event_detail);
            eventImage = (ImageView)view.findViewById(R.id.event_photo);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mIntent = new Intent(context, DetailEvent.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    mIntent.putExtra("lat", group.getLat());
                    mIntent.putExtra("lng", group.getLng());
                    mIntent.putExtra("title",group.getTitle());
                    mIntent.putExtra("detail",group.getArticle());
                    mIntent.putExtra("date",group.getDate());
                    mIntent.putExtra("cur_people",group.getCur_people_num());
                    mIntent.putExtra("location",group.getLocation());
                    mIntent.putExtra("tag",group.getTag());
                    mIntent.putExtra("masterid",group.getMasterId());
                    mIntent.putExtra("maxpeople",group.getPeople_max());
                    mIntent.putExtra("minpeople",group.getPeople_min());
                    mIntent.putExtra("time",group.getTime());
                    for(int i=0;i<group.getJoin().size();i++)
                    {
                        mIntent.putExtra("join"+Integer.toString(i),group.getJoin().get(i));
                    }
                    mIntent.putExtra("size",group.getJoin().size());
                    mIntent.putExtra("expire",group.isExpired());
                    mIntent.putExtra("success",group.isSuccess());
                    mIntent.putExtra("image",group.getImage());
                    mIntent.putExtra("group_id",group.getId());
                    //mIntent.putExtra("group", JSON.toJSONString(group));
                    context.startActivity(mIntent);
                }
            });
        }
    }

    RecyclerViewAdapter(Context v,List<Group> grouplist){
        context=v;
        mGroupList = grouplist;
        Log.d("Adapter",Integer.toString(mGroupList.size()));
    }
    RecyclerViewAdapter(){
        mGroupList = new ArrayList<Group>();
    }

    @Override
    public  void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Log.d("Create",Integer.toString(i));
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.event_card, viewGroup, false);

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(lp);

        EventViewHolder pvh = new EventViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(EventViewHolder personViewHolder, int i) {
        EventViewHolder.titleView.setText(mGroupList.get(i).getTitle());
        EventViewHolder.detailView.setText(mGroupList.get(i).getArticle());
        Log.d("Bind",Integer.toString(i));
        try {
            EventViewHolder.eventImage.setImageBitmap(mGroupList.get(i).getImage());
            personViewHolder.group = mGroupList.get(i);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }


        //EventViewHolder.eventImage.setImageResource(mGroupList.get(i).get);

    }

    @Override
    public int getItemCount() {
        Log.d("Adapter",Integer.toString(mGroupList.size()));
        return mGroupList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


}
