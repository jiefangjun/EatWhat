package gq.fokia.eatwhat;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


/**
 * Created by fokia on 17-2-21.
 */

public class AllFoodFragment extends Fragment {

    public static List<Food> foodList = new ArrayList<>();
    private FoodDBOpenHelper foodDBOpenHelper;
    public static SQLiteDatabase db;
    public static RecyclerView recyclerView; //为了Itemcallback可以获得此recvclerview--public
    private Bitmap bitmap;
    private View view;
    private FoodAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Cursor cursor;
    private Food foodZero;//栈顶food对象
    private MainActivity mactivity;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.allfood_fragment, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);

        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFragment();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        Log.d(getClass().toString(), "onCreateView Executed");
        return view;
    }

    private void getFoodsData(){
        int dbSize,i = 0, length;
        dbSize = cursor.getCount();
        if(dbSize == 0){
            Toast.makeText(getContext(),"什么也没有哦，请添加数据",Toast.LENGTH_SHORT).show();
            return;
        }else if(dbSize > 5){
            length = 5;
        }else {
            length = dbSize;
        }
            if (cursor.moveToFirst()) {
                while (i < length && !cursor.isAfterLast()) {
                    foodList.add(new Food(cursor.getString(1),
                            cursor.getDouble(2), cursor.getString(4),
                            getImage(cursor.getString(3)),
                            cursor.getInt(5)));
                    cursor.moveToNext();
                    i++;
                }
                adapter = new FoodAdapter(foodList);
            }
    }

    private void getMoreData(){
        int position = cursor.getPosition();
        Log.d("position",position+"");
//        cursor.moveToPosition(position+1);
        int i = 0;
        Log.d("isAfterLast",cursor.isAfterLast()+"");
        while (i < 5 && !cursor.isAfterLast()){
            foodList.add(0, new Food(cursor.getString(1),
                    cursor.getDouble(2), cursor.getString(4), getImage(cursor.getString(3)), cursor.getInt(5)));
            cursor.moveToNext();
            i++;
        }
    }

    private Bitmap getImage(String imagePath){
        try {
            BitmapFactory bitmapFactory = new BitmapFactory();
            /*BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inJustDecodeBounds = false;
            options.inSampleSize = 1;
            bitmap = bitmapFactory.decodeFile(imagePath);*/
            bitmap = bitmapFactory.decodeFile(imagePath);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(foodList.isEmpty()) {
            getFoodsData();
        }if (!foodList.isEmpty()){
            adapter = new FoodAdapter(foodList);
            refreshFragment();
            recyclerView.setAdapter(adapter);

        }/*else {
            //Toast.makeText(getContext(), "什么也没有哦", Toast.LENGTH_LONG).show();
        }*/
        if(adapter != null)
        adapter.setOnItemClickListener(new FoodAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, Food data) {
                if (mactivity == null){
                    mactivity = (MainActivity) getActivity();
                }
                if(data != null)
                mactivity.replaceFragment(new AddFoodFragment(data.getName(), data.getPrice(),
                        data.getIntroduce(), data.getBitmap()));
            }
        });
    }

    @Override
    public void onStart(){
        super.onStart();
        foodDBOpenHelper = new FoodDBOpenHelper(getContext(),
                "FoodClub.db", null, 1);
        db = foodDBOpenHelper.getWritableDatabase();
        cursor = db.query("food", null, null, null, null, null, "id DESC", null);
        final ItemTouchHelper helper = new ItemTouchHelper(new MyCallBack());
        helper.attachToRecyclerView(recyclerView);
    }
    public void refreshFragment() {

        // 开始刷新，设置当前为刷新状态
        swipeRefreshLayout.setRefreshing(true);
        // 获取数据
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(foodList.get(0) != foodZero && foodZero != null){
                    adapter.notifyDataSetChanged();
                    // 加载完数据设置为不刷新状态，将下拉进度收起来
                    swipeRefreshLayout.setRefreshing(false);
                }else if(foodZero == foodList.get(0)){
                    Log.d("db.size",cursor.getCount()+"");
                    if(cursor.getCount() <= 5 || cursor.getPosition() == cursor.getCount()){
                        swipeRefreshLayout.setRefreshing(false);
                        return;
                    }
                    cursor.moveToPosition(foodList.size());
                    getMoreData();
                    adapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }else if(foodZero == null){
                    swipeRefreshLayout.setRefreshing(false);
                }
                foodZero = foodList.get(0);
                Log.d("foodZero", foodZero.toString());
        }}, 1200);
    }



}

