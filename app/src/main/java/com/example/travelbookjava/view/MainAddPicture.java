package com.example.travelbookjava.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelbookjava.R;
import com.example.travelbookjava.adapter.CustomAdapter;
import com.example.travelbookjava.model.Place;
import com.google.android.gms.maps.model.LatLng;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class MainAddPicture extends AppCompatActivity {
    public SQLiteDatabase database;
    ListView listView;
    ArrayList<Place> placeList;
    Bitmap selectedImage;
    ImageView imageView;
    TextView editPictureText,textViewP;
    Button button;
    Button button2;
    Button button3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_add_picture);
        imageView=findViewById(R.id.imageView);
        editPictureText=findViewById(R.id.editPictureName);
        textViewP=findViewById(R.id.textViewp);
        button=findViewById(R.id.savedP);
        button2=findViewById(R.id.buttonDP);
        button3=findViewById(R.id.updateP);

        database=this.openOrCreateDatabase("PlacesPN",MODE_PRIVATE,null);
        Intent intent=getIntent();
        String info=intent.getStringExtra("info");
        if(info.matches("new")){
            editPictureText.setText("");
            button2.setVisibility(View.INVISIBLE);
            button3.setVisibility(View.INVISIBLE);
        }else{
            int picId=intent.getIntExtra("picId",1);
            button.setVisibility(View.INVISIBLE);
            editPictureText.setVisibility(View.INVISIBLE);
            try{
                Cursor cursor=database.rawQuery("SELECT * FROM pic WHERE id=?",new String[]{String.valueOf(picId)});

                int picNameIx=cursor.getColumnIndex("placepicname");
                int picImageIx=cursor.getColumnIndex("image");
                while(cursor.moveToNext()){
                    byte[] bytes=cursor.getBlob(picImageIx);
                    Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                    imageView.setImageBitmap(bitmap);
                    textViewP.setText(cursor.getString(picNameIx));
                }
                cursor.close();

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void selectImage(View view){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
        }else{
            Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intentToGallery,2);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if(requestCode==1){
            if(grantResults.length> 0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent intentToGallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentToGallery,2);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode==2 && resultCode==RESULT_OK && data!=null){
            Uri imageData=data.getData();

            try {
                if (Build.VERSION.SDK_INT >= 28){
                    ImageDecoder.Source source=ImageDecoder.createSource(this.getContentResolver(),imageData);
                    selectedImage=ImageDecoder.decodeBitmap(source);
                    imageView.setImageBitmap(selectedImage);
                }else{
                    selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageData);
                    imageView.setImageBitmap(selectedImage);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void savePic(View view){
        String picname=editPictureText.getText().toString();
        Bitmap smallImage=makeSmallerImage(selectedImage,300);
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50, outputStream);
        byte[] byteArray=outputStream.toByteArray();
        try{
            Double userland;
            userland = MainActivity.positions;
            database=this.openOrCreateDatabase("PlacesPN",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS pic (id INTEGER PRIMARY KEY, placeno VARCHAR,placepicname VARCHAR, image BLOB)");
            String sqlString="INSERT INTO pic ( placeno,placepicname, image ) VALUES ( ?, ?, ? )";
            SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,String.valueOf(userland));
            sqLiteStatement.bindString(2,picname);
            sqLiteStatement.bindBlob(3,byteArray);
            sqLiteStatement.execute();
            Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_LONG).show();

        }catch (Exception e){
            e.printStackTrace();
        }
        Intent intentBack=new Intent(MainAddPicture.this,MainActivityPN.class);
        intentBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentBack);
        //finish();
    }

    public Bitmap makeSmallerImage(Bitmap image,int maxSize){
        int width=image.getWidth();
        int height=image.getHeight();
        float bitmapR=(float) width/(float) height;

        if(bitmapR>1){
            width=maxSize;
            height=(int) (width/bitmapR);
        }else{
            height=maxSize;
            width=(int)(height*bitmapR);
        }
        return Bitmap.createScaledBitmap(image,width,height,true);
    }

    public void deletePicture(View view){
        Intent intent=getIntent();
        int deletePicId=intent.getIntExtra("picId",1);
        try{
            SQLiteDatabase database=this.openOrCreateDatabase("PlacesPN",MODE_PRIVATE,null);
            database.execSQL("DELETE FROM pic WHERE id=?",new String[]{String.valueOf(deletePicId)});
            Toast.makeText(getApplicationContext(),"Deleted!",Toast.LENGTH_LONG).show();
            Intent intentBACK=new Intent(MainAddPicture.this,MainActivityPN.class);
            intentBACK.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentBACK);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updatePicture(View view){
        Intent intent=getIntent();
        int updatePicId=intent.getIntExtra("picId",1);
        Bitmap smallImage=makeSmallerImage(selectedImage,300);
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG,50, outputStream);
        byte[] byteArray=outputStream.toByteArray();
        try{
            SQLiteDatabase database=this.openOrCreateDatabase("PlacesPN",MODE_PRIVATE,null);
            String sqlString=("UPDATE pic SET image=? WHERE id=?");
            SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
            sqLiteStatement.bindBlob(1,byteArray);
            sqLiteStatement.bindString(2,String.valueOf(updatePicId));
            sqLiteStatement.execute();
            Toast.makeText(getApplicationContext(),"Update",Toast.LENGTH_LONG).show();
            Intent intentBACK=new Intent(MainAddPicture.this,MainActivityPN.class);
            intentBACK.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentBACK);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}