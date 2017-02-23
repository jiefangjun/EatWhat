package gq.fokia.eatwhat;

/**
 * Created by fokia on 17-2-21.
 */

public class Food {

    private String name;
    private int imageId;
    private double price;

    public Food(String name, double price, int imageId){
        this.name = name;
        this.price = price;
        this.imageId = imageId;
    }
    public String getName(){
        return name;
    }
    public int getImageId(){
        return imageId;
    }
    public double getPrice(){
        return price;
    }

}
