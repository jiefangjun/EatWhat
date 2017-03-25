package gq.fokia.eatwhat;

import android.graphics.Bitmap;

/**
 * Created by fokia on 17-2-21.
 */

public class Food {

    private String name;
    private double price;
    private Bitmap imageBitmap;
    private String introduce;
    private int isLike;

    public Food(String name, double price, String introduce, Bitmap imageBitmap, int isLike){
        this.name = name;
        this.price = price;
        this.introduce = introduce;
        this.imageBitmap = imageBitmap;
        this.isLike = isLike;
    }
    public String getName(){
        return name;
    }
    public double getPrice(){
        return price;
    }
    public Bitmap getBitmap(){
        return imageBitmap;
    }

    public String getIntroduce(){
        return introduce;
    }

    public int getIsLike(){
        return isLike;
    }

    public void setIsLike(int like){
        isLike = like;
    }

}
