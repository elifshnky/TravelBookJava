package com.example.travelbookjava.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.travelbookjava.R;
import com.example.travelbookjava.adapter.CustomAdapter;
import com.example.travelbookjava.model.Place;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static Double positions;
    public SQLiteDatabase database;
    public SQLiteDatabase database2;

    ArrayList<Place> placeList=new ArrayList<>();
    ListView listView;
    CustomAdapter customAdapter;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.add_place,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_place) {
            Intent intent=new Intent(this, MapsActivity.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }
        if(item.getItemId() == R.id.delete_drop) {
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.this);
            alertDialog.setCancelable(true);
            alertDialog.setTitle("Emin misiniz?");
            alertDialog.setMessage("Bütün adres,not ve resimleriniz silincektir!");
            alertDialog.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try{
                        database=MainActivity.this.openOrCreateDatabase("Places", MODE_PRIVATE,null);
                        database2=MainActivity.this.openOrCreateDatabase("PlacesPN", MODE_PRIVATE,null);
                        database2.execSQL("CREATE TABLE IF NOT EXISTS note (id INTEGER PRIMARY KEY,pacino VARCHAR,notes VARCHAR)");
                        database2.execSQL("CREATE TABLE IF NOT EXISTS pic (id INTEGER PRIMARY KEY, placeno VARCHAR,placepicname VARCHAR, image BLOB)");
                        database.execSQL("DROP TABLE places");
                        database2.execSQL("DROP TABLE note");
                        database2.execSQL("DROP TABLE pic");

                        Intent intent=new Intent(MainActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        Toast.makeText(getApplicationContext(),"Delete veriable",Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            alertDialog.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alertDialog.show();


        }
        if(item.getItemId() == R.id.about_app) {
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.this);
            alertDialog.setCancelable(true);
            alertDialog.setTitle("Travel Book : Seyehat Kitabı");
            alertDialog.setMessage("\n  Seyahat Ajandası olarak telefonunuzda kullanabileceğiniz,gezginlerin gittikleri yerlerin rotalarını , hakkındaki düşünce ve anılarını topladıkları kişisel bir depoya sahip olmaları için geliştirdik." +
                    "\n \n  Bütün seyahat tutkunları; yolculukları boyunca kayıt ettikleri " +
                    "rotalarına, aldıkları notları, çektikleri fotoğrafları saklayabilcekleri bir uygulama Travel book. \n");
            alertDialog.show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listView);
        getData();
    }

    public void getData(){

        customAdapter=new CustomAdapter(this,placeList);
        try {
            database=this.openOrCreateDatabase("Places",MODE_PRIVATE,null);
            Cursor cursor=database.rawQuery("SELECT*FROM places",null);

            int idIx=cursor.getColumnIndex("id");
            int nameIx=cursor.getColumnIndex("name");
            int latitudeIx=cursor.getColumnIndex("latitude");
            int longitudeIx=cursor.getColumnIndex("longitude");


            while(cursor.moveToNext()){

                String nameFromDatabase=cursor.getString(nameIx);
                String latitudeFromDatabase=cursor.getString(latitudeIx);
                String longitudeFromDatabase=cursor.getString(longitudeIx);
                String idFromDatabase=cursor.getString(idIx);

                Double latitude=Double.parseDouble(latitudeFromDatabase);
                Double longitude=Double.parseDouble(longitudeFromDatabase);
                Double idd=Double.parseDouble(idFromDatabase);

                Place place=new Place(nameFromDatabase,latitude,longitude,idd);

                System.out.println(place.id);
                placeList.add(place);
            }
            customAdapter.notifyDataSetChanged();
            cursor.close();

        }catch (Exception e){
            e.printStackTrace();
        }

        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {//önceden kayıt edilmiş adrese menü
                positions=(placeList.get(position).id);
                AlertDialog.Builder alertDialogg=new AlertDialog.Builder(MainActivity.this);
                alertDialogg.setCancelable(true);
                alertDialogg.setTitle("Ne yapmak istiyorsun gezgin?");

                alertDialogg.setPositiveButton("Harita", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(MainActivity.this,MapsActivity.class);
                        intent.putExtra("info","old");
                        intent.putExtra("place",placeList.get(position));
                        startActivity(intent);
                    }
                });
                alertDialogg.setNegativeButton("Resimler & Notlar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent=new Intent(MainActivity.this, MainActivityPN.class);
                        intent.putExtra("info","resim");
                        intent.putExtra("place",placeList.get(position));
                        startActivity(intent);

                    }
                });
                alertDialogg.setNeutralButton("Adresi Sil", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.this);
                        alertDialog.setCancelable(false);
                        alertDialog.setTitle("Adresinizle birlikte bu konuma ait arşiviniz silinecektir! ");
                        alertDialog.setMessage("Bu adres ve bilgiler silinsin mi?");
                        alertDialog.setPositiveButton("Sil", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                try{
                                    database=MainActivity.this.openOrCreateDatabase("Places", MODE_PRIVATE,null);
                                    database2=MainActivity.this.openOrCreateDatabase("PlacesPN", MODE_PRIVATE,null);
                                    database.execSQL("DELETE FROM places WHERE id=?",new String[]{String.valueOf(positions)});

                                    database2.execSQL("CREATE TABLE IF NOT EXISTS note (id INTEGER PRIMARY KEY,pacino VARCHAR,notes VARCHAR)");
                                    database2.execSQL("CREATE TABLE IF NOT EXISTS pic (id INTEGER PRIMARY KEY, placeno VARCHAR,placepicname VARCHAR, image BLOB)");

                                    Cursor cursor=database2.rawQuery("SELECT * FROM note WHERE pacino=?",new String[]{String.valueOf(positions)});
                                    if(cursor!=null){
                                        database2.execSQL("DELETE FROM note WHERE pacino=?",new String[]{String.valueOf(positions)});
                                    }
                                    Cursor cursor2=database2.rawQuery("SELECT * FROM pic WHERE placeno=?",new String[]{String.valueOf(positions)});
                                    if(cursor2!=null) {
                                        database2.execSQL("DELETE FROM pic WHERE placeno=?", new String[]{String.valueOf(positions)});
                                    }
                                    cursor.close();
                                    cursor2.close();

                                    Intent intent=new Intent(MainActivity.this, MainActivity.class);
                                    intent.putExtra("place",placeList.get(position));
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);

                                    Toast.makeText(getApplicationContext(),"Delete place",Toast.LENGTH_LONG).show();

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                        alertDialog.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int i) {
                                Toast.makeText(getApplicationContext(),"canceled!",Toast.LENGTH_LONG).show();
                            }
                        });
                        alertDialog.show();
                    }
                });
                alertDialogg.show();
            }
        });

    }
}