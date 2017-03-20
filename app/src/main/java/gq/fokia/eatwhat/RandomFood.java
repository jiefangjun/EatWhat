package gq.fokia.eatwhat;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import static gq.fokia.eatwhat.MainActivity.db;

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
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.random_card_item, container, false);
        imageView = (ImageView) view.findViewById(R.id.random_food_image);
        name = (TextView) view.findViewById(R.id.random_name);
        price = (TextView) view.findViewById(R.id.random_price);
        introduce = (TextView) view.findViewById(R.id.random_introduce);
        return view;
    }

    public RandomFood (){
        getRandomFood();
    }

    private Food getRandomFood(){
        Random random = new Random();
        cursor = db.query("food", null, null, null, null, null, null, null);
        if(cursor.getCount() == 0){
            food = new Food("hello", 9999, "nothing",getImage("/res/drawable/d0.jpg"),0);
        }else {
            cursor.moveToFirst();
            int first = cursor.getInt(0);
            cursor.moveToLast();
            int last = cursor.getInt(0);
            int position = random.nextInt(last - first);
            cursor.moveToPosition(position);
            food = new Food(cursor.getString(1),
                    cursor.getDouble(2), cursor.getString(4),
                    getImage(cursor.getString(3)),
                    cursor.getInt(5));
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

    @Override
    public void onResume() {
        super.onResume();
        imageView.setImageBitmap(food.getBitmap());
        name.setText(food.getName());
        price.setText(food.getPrice()+"");
        introduce.setText(food.getIntroduce());
    }
}
