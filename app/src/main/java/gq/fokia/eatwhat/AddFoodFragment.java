package gq.fokia.eatwhat;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static gq.fokia.eatwhat.AllFoodFragment.doneRefresh;
import static gq.fokia.eatwhat.AllFoodFragment.foodList;
import static gq.fokia.eatwhat.MainActivity.db;

/**
 * Created by fokia on 17-2-23.
 */

public class AddFoodFragment extends Fragment {

    private EditText editName;
    private EditText editPrice;
    private EditText editIntroduce;
    private ImageView editImage;
    private CheckBox editLike;
    private Button editSaveData;
    private Uri imageUri;
    private String absoluteImagePath;

    private String name;
    private Double price;
    private String introduce;
    private Bitmap bitmap;//初始addFoodfragment时传递的bitmap
    private int like;//传递过来的checkbox的值
    private Bitmap originBitmap;//修改数据时已经存在的bitmap

    public final int CHOOSE_PHOTO = 0;
    public final int TAKE_PHOTO = 1;
    public final int TAKE_CUT = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.add_food_fragment, container, false);
        editName = (EditText) view.findViewById(R.id.edit_name);
        editPrice = (EditText) view.findViewById(R.id.edit_price);
        editImage = (ImageView) view.findViewById(R.id.edit_image);
        editIntroduce = (EditText) view.findViewById(R.id.edit_introduce);
        editLike = (CheckBox) view.findViewById(R.id.edit_like);
        editSaveData = (Button) view.findViewById(R.id.edit_save_data);
        editSaveData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission
                        (getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                }else {
                    addFoodsData();
                }

            }
        });
        return view;
    }

    private void addFoodsData(){
        if(editName.getText().length() == 0 || editPrice.getText().length() == 0){
            Toast.makeText(getContext(), "至少得输入名字和价格哦", Toast.LENGTH_SHORT).show();
            return;
        }else if (bitmap == null){
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.d0);
        }
        absoluteImagePath = savePicture(bitmap, editName.getText().toString());
        ContentValues values = new ContentValues();
        values.put("name", editName.getText().toString());
        values.put("price", editPrice.getText().toString());
        values.put("introduce", editIntroduce.getText().toString());
        values.put("image", absoluteImagePath);
        values.put("like", setEditLike());
        db.insert("food", null, values);
        String ss = editPrice.getText().toString();
        double price;
        if("".equals(ss)){
            price = 0;
        }else
        price = Double.valueOf(ss);
        foodList.add(0, new Food(editName.getText().toString(), price,
                editIntroduce.getText().toString(),
                bitmap, setEditLike()));
        Toast.makeText(getContext(),"添加成功",Toast.LENGTH_SHORT).show();
    }

    private void takePhoto(){
        File outputImage = new File(getContext().getExternalCacheDir(), "output_image.jpg");
        try{
            if (outputImage.exists()){
                outputImage.delete();
            }
            outputImage.createNewFile();
        }catch (IOException e){
            e.printStackTrace();
        }
        if(Build.VERSION.SDK_INT >= 24){
            imageUri = FileProvider.getUriForFile(getContext(), "gq.fokia.eatwhat.fileprovider", outputImage);
        }else {
            imageUri = Uri.fromFile(outputImage);
        }
        //启动相机
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    public Bitmap getBitmap(){
        try {
            BitmapFactory bitmapFactory = new BitmapFactory();
            bitmap = bitmapFactory.decodeStream(getContext().getContentResolver()
            .openInputStream(imageUri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        switch (requestCode){
            case TAKE_PHOTO:
                if(resultCode == RESULT_OK){
                    cutPhoto(4,3,600,500,imageUri,TAKE_CUT);
                }
                break;
            case TAKE_CUT:
                if(resultCode == RESULT_OK) {
                    getBitmap();
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK){
                    handleImageOnKitKat(data);
                }
                break;
            default:
                break;
        }
    }


    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        if (DocumentsContract.isDocumentUri(getActivity(), uri)) {
            // 如果是document类型的Uri，则通过document id处理
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // 解析出数字格式的id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // 如果是content类型的Uri，则使用普通方式处理
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // 如果是file类型的Uri，直接获取图片路径即可
            imagePath = uri.getPath();
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        bitmap = BitmapFactory.decodeFile(imagePath, options);
        Log.i("inSampleSize", "压缩后图片的大小" + (bitmap.getByteCount() / 1024 / 1024)
                + "M宽度为" + bitmap.getWidth() + "高度为" + bitmap.getHeight());
        //茕茕白兔，东走西顾，衣不如新，人不如故
    }

    private String getImagePath(Uri uri, String selection){
        String path = null;
        Cursor cursor = getActivity().getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null){
            if(cursor.moveToFirst()){
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }



    public static String savePicture(Bitmap bitmap, String edit_name) {
        String picturePath = Environment.getExternalStorageDirectory().toString() + "/EatWhat/"
                 + edit_name + ".jpg";
        File dir = new File(Environment.getExternalStorageDirectory().toString() + "/EatWhat/");
        if (!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(Environment.getExternalStorageDirectory().toString() + "/EatWhat/", edit_name + ".jpg");
        FileOutputStream out;
        try {
                out = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.flush();
                out.close();
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        return picturePath;
    }

    //传参数的构造函数，用来初始化编辑界面
    public AddFoodFragment(String name, Double price, String introduce, Bitmap image, int like){
        this.name = name;
        this.price = price;
        this.introduce = introduce;
        this.bitmap = image;
        this.like = like;
    }

    //默认无参构造方法
    public AddFoodFragment(){
        //nothing
    }

    @Override
    public void onResume() {
        super.onResume();
        doneRefresh = true;
        if(name != null && price != null && bitmap != null){
            editName.setText(name);
            editPrice.setText(price+"");
            editIntroduce.setText(introduce);
            editImage.setImageBitmap(bitmap);
            editLike.setChecked(like == 1);
            originBitmap = bitmap;
            editSaveData.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    updateData(name);
                }
            });

        }else if(bitmap != null){
            editImage.setImageBitmap(bitmap);
            originBitmap = bitmap;
        }
        //存储初始bitmap

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                        dialog.setItems(new String[]{"拍照", "从相册选择"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case 0:
                                    takePhoto();
                                    break;
                                case 1:
                                    openAlbum();
                                    break;
                            }
                        }
                    });
                        dialog.show();
            }
        });
    }

    private void updateData(String name){

        File file = new File(Environment.getExternalStorageDirectory().toString() + "/EatWhat/" + name + ".jpg");

        if(bitmap != originBitmap){
            absoluteImagePath = savePicture(bitmap, editName.getText().toString());
        }else {
            //重命名原来图片
            File new_file = new File(Environment.getExternalStorageDirectory().toString() +
                    "/EatWhat/", editName.getText().toString() + ".jpg");
            file.renameTo(new_file);
            absoluteImagePath = Environment.getExternalStorageDirectory().toString() + "/EatWhat/" +
                    editName.getText().toString() + ".jpg";
        }
        file.delete();
        ContentValues values = new ContentValues();
        values.put("name", editName.getText().toString());
        values.put("price", editPrice.getText().toString());
        values.put("introduce", editIntroduce.getText().toString());
        values.put("image", absoluteImagePath);
        values.put("like", setEditLike());
        String args[] = {name};
        db.update("food", values, "name=?" , args);
        foodList.clear();
        Toast.makeText(getContext(),"数据更新成功",Toast.LENGTH_SHORT).show();
        savePicture(bitmap, editName.getText().toString());
    }

    private void cutPhoto(int aspectX,int aspectY,int outputX,int outputY,Uri uri,int requestCode) {
        // 裁剪图片意图
        Intent in = new Intent("com.android.camera.action.CROP");
        in.setDataAndType(uri, "image/*");
        in.putExtra("crop", "true");
        // 裁剪框的比例
        in.putExtra("aspectX", aspectX);
        in.putExtra("aspectY", aspectY);
        // 裁剪后输出图片的尺寸大小
        in.putExtra("outputX", outputX);
        in.putExtra("outputY", outputY);
        //是否保留比例
        in.putExtra("scale", true);
        in.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        in.putExtra("return-data", false);
        in.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());// 图片格式
        in.putExtra("noFaceDetection", true);// 取消人脸识别
        startActivityForResult(in, requestCode);// 开启一个带有返回值的Activity
    }

    public int setEditLike(){
        if (editLike.isChecked())
            return 1;
        else
            return 0;
    }

    private void openAlbum(){
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 2:
                if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    addFoodsData();
                }else {
                    Toast.makeText(getContext(), "权限未被授予", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }
}
