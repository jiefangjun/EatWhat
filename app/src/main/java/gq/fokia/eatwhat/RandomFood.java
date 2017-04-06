package gq.fokia.eatwhat;


import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

import static gq.fokia.eatwhat.MainActivity.db;
import static gq.fokia.eatwhat.MainActivity.lastposition;

/**
 * Created by fokia on 17-3-19.
 */

public class RandomFood extends Fragment {
    private ImageView imageView;
    private TextView name;
    private TextView price;
    private TextView introduce;
    private Cursor cursor;
    private Bitmap bitmap;
    private Food food;
    private static int click;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.random_card_item, container, false);
        imageView = (ImageView) view.findViewById(R.id.random_food_image);
        name = (TextView) view.findViewById(R.id.random_name);
        price = (TextView) view.findViewById(R.id.random_price);
        introduce = (TextView) view.findViewById(R.id.random_introduce);
        getRandomFood();
        return view;
    }

    private Food getRandomFood(){
        cursor = db.query("food", null, null, null, null, null, null, null);
        if(cursor.getCount() == 0){
            food = new Food("hello", 9999, "nothing",
                    getImage(getActivity().getResources(), R.drawable.d0),0);
        }else {
            getRandom();
            food = new Food(cursor.getString(1),
                    cursor.getDouble(2), cursor.getString(4),
                    getImage(cursor.getString(3)),
                    cursor.getInt(5));
            ContentValues contentValues = new ContentValues();
            contentValues.put("recent", System.currentTimeMillis());
            String args[] = {cursor.getString(0)};
            db.update("food", contentValues, "id=?", args);
        }
        return food;
    }

    private Bitmap getImage(String imagePath){
        try {
            BitmapFactory bitmapFactory = new BitmapFactory();
            bitmap = bitmapFactory.decodeFile(imagePath);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    private Bitmap getImage(Resources resource, int id){
        try{
            BitmapFactory bitmapFactory = new BitmapFactory();
            bitmap = bitmapFactory.decodeResource(resource, id);
        }catch (Exception e){
            e.printStackTrace();
        }
        return bitmap;
    }

    private void getRandom(){
        int length, position;
        cursor.moveToFirst();
        int first = cursor.getInt(0);
        cursor.moveToLast();
        int last = cursor.getInt(0);
        if(last == first){
            cursor.moveToFirst();
            Toast.makeText(getActivity(), "就这一个哦", Toast.LENGTH_SHORT).show();
            return;
        }else{
            length = last - first;
        }
        if(length == 1){
            if(click % 2 == 0){
                cursor.moveToLast();
            }else{
                cursor.moveToFirst();
            }
            click++;
            return;
        }
        do {
            Random random = new Random();
            position = random.nextInt(length);
            if(position == lastposition)
                continue;
            if(cursor.moveToPosition(position)){
                cursor.moveToPosition(position);
                lastposition = position;
                break;
            }
        }while (position == lastposition);
    }

    @Override
    public void onResume() {
        super.onResume();
        imageView.setImageBitmap(food.getBitmap());
        name.setText(food.getName());
        price.setText(food.getPrice()+"");
        introduce.setText(food.getIntroduce());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("RandomFood","onDestroy");
        if(bitmap != null)
        bitmap.recycle();
    }
}
