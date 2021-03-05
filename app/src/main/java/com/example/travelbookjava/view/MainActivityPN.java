package com.example.travelbookjava.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.travelbookjava.R;

import java.util.ArrayList;

public class MainActivityPN extends AppCompatActivity {
    public SQLiteDatabase database;
    ListView listView2;
    ListView listView3;
    ArrayList<String> notesArray;
    ArrayList<String> pinnateArray;
    ArrayList<Integer> idArray;
    ArrayList<Integer> idArray2;
    ArrayAdapter arrayAdapter;
    ArrayAdapter arrayAdapter2;

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.add_placepicnot,menu);

        return super.onCreateOptionsMenu(menu);
    }
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_picture) {
            Intent intent=new Intent(this, MainAddPicture.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }
        if(item.getItemId() == R.id.add_note) {
            Intent intent=new Intent(this, MainAddNote.class);
            intent.putExtra("info","new");
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_pn);
        database=this.openOrCreateDatabase("PlacesPN",MODE_PRIVATE,null);

        listView2=findViewById(R.id.listView2);
        listView3=findViewById(R.id.listView3);

        notesArray=new ArrayList<String>();
        idArray=new ArrayList<Integer>();
        idArray2=new ArrayList<Integer>();
        pinnateArray =new ArrayList<String>();

        arrayAdapter=new ArrayAdapter(this,android.R.layout.simple_list_item_1,notesArray);
        listView2.setAdapter(arrayAdapter);
        arrayAdapter2=new ArrayAdapter(this,android.R.layout.simple_list_item_1, pinnateArray);
        listView3.setAdapter(arrayAdapter2);

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivityPN.this,MainAddNote.class);
                intent.putExtra("notId",idArray.get(position));
                intent.putExtra("info","old");
                startActivity(intent);
            }
        });

        listView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivityPN.this,MainAddPicture.class);
                intent.putExtra("picId",idArray2.get(position));
                intent.putExtra("info","old");
                startActivity(intent);

            }
        });
        getData();

    }

    public void getData(){
        Double userland = MainActivity.positions;
        int position=(int)userland.intValue();
        try{
        SQLiteDatabase database=this.openOrCreateDatabase("PlacesPN",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS note (id INTEGER PRIMARY KEY,pacino VARCHAR,notes VARCHAR)");
            database.execSQL("CREATE TABLE IF NOT EXISTS pic (id INTEGER PRIMARY KEY, placeno VARCHAR,placepicname VARCHAR, image BLOB)");

            Cursor cursor=database.rawQuery("SELECT * FROM note",null);
            Cursor cursor2=database.rawQuery("SELECT * FROM pic",null);

            int idIx=cursor.getColumnIndex("id");
            int placeIx=cursor.getColumnIndex("pacino");
            int notesIx=cursor.getColumnIndex("notes");
            int id2Ix=cursor2.getColumnIndex("id");
            int pianoIx=cursor2.getColumnIndex("placeno");
            int pinnateIx=cursor2.getColumnIndex("placepicname");

            while(cursor.moveToNext()){
                if(cursor.getInt(placeIx)==position) {
                    notesArray.add(cursor.getString(notesIx));
                    idArray.add(cursor.getInt(idIx));
                }
            }
            while(cursor2.moveToNext()){
                if(cursor2.getInt(pianoIx)==position) {
                    pinnateArray.add(cursor2.getString(pinnateIx));
                    idArray2.add(cursor2.getInt(id2Ix));
                }
            }

        arrayAdapter.notifyDataSetChanged();
        arrayAdapter2.notifyDataSetChanged();
        cursor.close();
        cursor2.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}