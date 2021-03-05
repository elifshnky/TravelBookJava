package com.example.travelbookjava.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.travelbookjava.R;
import com.example.travelbookjava.model.Place;

import java.util.ArrayList;

public class MainAddNote extends AppCompatActivity {
    TextView noteText;
    Button button;
    Button button2;
    Button button3;
    ArrayList<Place> placeList;
    public SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_add_note);
        noteText=findViewById(R.id.noteText);
        button=findViewById(R.id.savedN);
        button2=findViewById(R.id.buttonDN);
        button3=findViewById(R.id.updateDN);
        database=this.openOrCreateDatabase("PlacesPN",MODE_PRIVATE,null);
        Intent intent=getIntent();
        String info=intent.getStringExtra("info");
        if(info.matches("new")){
            noteText.setText("");
            button2.setVisibility(View.INVISIBLE);
            button3.setVisibility(View.INVISIBLE);
        }else{
            int notId=intent.getIntExtra("notId",1);
            button.setVisibility(View.INVISIBLE);
            try{
                Cursor cursor=database.rawQuery("SELECT * FROM note WHERE id=?",new String[]{String.valueOf(notId)});

                int notIx=cursor.getColumnIndex("notes");
                while(cursor.moveToNext()){
                    noteText.setText(cursor.getString(notIx));
                }
                cursor.close();

            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    public void saveNote(View view){
        String notes=noteText.getText().toString();
        try{
           /* Intent intent=getIntent();
            Place place=(Place) intent.getSerializableExtra("place");*/
            Double userland;
            userland = MainActivity.positions;
            SQLiteDatabase database=this.openOrCreateDatabase("PlacesPN",MODE_PRIVATE,null);
            database.execSQL("CREATE TABLE IF NOT EXISTS note (id INTEGER PRIMARY KEY,pacino VARCHAR,notes VARCHAR)");
            String sqlString="INSERT INTO note ( pacino, notes) VALUES ( ?, ?)";
            SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,String.valueOf(userland));
            sqLiteStatement.bindString(2,notes);
            sqLiteStatement.execute();
            Toast.makeText(getApplicationContext(),"Saved",Toast.LENGTH_LONG).show();

        }catch (Exception e){
            e.printStackTrace();
        }
        Intent intentBack=new Intent(MainAddNote.this,MainActivityPN.class);
        intentBack.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intentBack);
    }

    public void deleteNote(View view){
        Intent intent=getIntent();
        String info=intent.getStringExtra("info");
        int deleteNoteId=intent.getIntExtra("notId",1);
        try{
            SQLiteDatabase database=this.openOrCreateDatabase("PlacesPN",MODE_PRIVATE,null);
            database.execSQL("DELETE FROM note WHERE id=?",new String[]{String.valueOf(deleteNoteId)});
            Toast.makeText(getApplicationContext(),"Deleted!",Toast.LENGTH_LONG).show();
            Intent intentBACK=new Intent(MainAddNote.this,MainActivityPN.class);
            intentBACK.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentBACK);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateNote(View view){
        String notes=noteText.getText().toString();
        Intent intent=getIntent();
        String info=intent.getStringExtra("info");
        int updateNoteId=intent.getIntExtra("notId",1);
        try{
            SQLiteDatabase database=this.openOrCreateDatabase("PlacesPN",MODE_PRIVATE,null);
            String sqlString=("UPDATE note SET notes=? WHERE id=?");
            SQLiteStatement sqLiteStatement=database.compileStatement(sqlString);
            sqLiteStatement.bindString(1,notes);
            sqLiteStatement.bindString(2,String.valueOf(updateNoteId));
            sqLiteStatement.execute();
            Toast.makeText(getApplicationContext(),"Update",Toast.LENGTH_LONG).show();
            Intent intentBACK=new Intent(MainAddNote.this,MainActivityPN.class);
            intentBACK.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intentBACK);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}