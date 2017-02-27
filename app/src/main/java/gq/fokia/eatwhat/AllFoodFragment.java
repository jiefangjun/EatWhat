package gq.fokia.eatwhat;

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

    private List<Food> foodList = new ArrayList<>();
    private FoodDBOpenHelper foodDBOpenHelper;
    private Bitmap bitmap;
    private View view;
    private FoodAdapter adapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        view = inflater.inflate(R.layout.allfood_fragment, container, false);
//        initFoods();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycle_view);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);

        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
/*
                // 开始刷新，设置当前为刷新状态
                //swipeRefreshLayout.setRefreshing(true);

                // 这里是主线程
                // 一些比较耗时的操作，比如联网获取数据，需要放到子线程去执行
                // TODO 获取数据
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        foodList.add(new Food("sb",65161, getImage("/storage/emulated/0/EatWhat/rest.jpg")));
                        adapter.notifyDataSetChanged();

                        Toast.makeText(getContext(), "刷新了一条数据", Toast.LENGTH_SHORT).show();

                        // 加载完数据设置为不刷新状态，将下拉进度收起来
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 1200);

                // System.out.println(Thread.currentThread().getName());

                // 这个不能写在外边，不然会直接收起来
                //swipeRefreshLayout.setRefreshing(false);*/
                onRefreshFragment();
            }
        });
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        Log.d(getClass().toString(), "onCreateView Executed");
        return view;

    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(getClass().toString(), "onStart Executed");
    }

    private void getFoodsData(){

        foodDBOpenHelper = new FoodDBOpenHelper(getContext(),
                "FoodClub.db", null, 1);
        SQLiteDatabase db = foodDBOpenHelper.getWritableDatabase();
        Cursor cursor = db.query("food", null, null, null, null, null, null, null);
        if (cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                foodList.add(new Food(cursor.getString(1),
                        cursor.getDouble(2), getImage(cursor.getString(3))));
                cursor.moveToNext();
            }
            cursor.close();
        }
        adapter = new FoodAdapter(foodList);
//        recyclerView.setAdapter(adapter);
    }

    private Bitmap getImage(String imagePath){
        try {
            BitmapFactory bitmapFactory = new BitmapFactory();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
//            int imageHeight = options.outHeight;
//            int imageWidth = options.outWidth;
            options.inJustDecodeBounds = false;
            options.inSampleSize = 2;
            bitmap = bitmapFactory.decodeFile(imagePath, options);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public void onPause(){
        super.onPause();
        //foodList.clear();
        Log.d(getClass().toString(), "onPause Executed");
    }

    @Override
    public void onResume(){
        super.onResume();
        if(foodList.isEmpty()) {
            getFoodsData();
            recyclerView.setAdapter(adapter);
        }
        //onRefreshFragment();
    }
    public void onRefreshFragment() {

        // 开始刷新，设置当前为刷新状态
        //swipeRefreshLayout.setRefreshing(true);

        // 这里是主线程
        // 一些比较耗时的操作，比如联网获取数据，需要放到子线程去执行
        // TODO 获取数据
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                foodList.add(new Food("sb",65161, getImage("/storage/emulated/0/EatWhat/rest.jpg")));
                adapter.notifyDataSetChanged();

                Toast.makeText(getContext(), "刷新了一条数据", Toast.LENGTH_SHORT).show();

                // 加载完数据设置为不刷新状态，将下拉进度收起来
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1200);

        // System.out.println(Thread.currentThread().getName());

        // 这个不能写在外边，不然会直接收起来
        //swipeRefreshLayout.setRefreshing(false);
    }


}

