package com.example.terapoop;

import static android.os.Build.ID;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button Add, Delete, Clear, Read, Update;
    EditText Do_it, Date;
    CheckBox Chek;
    DBHelper dbHelper;

    @Override

    public void onClick(View v) {

        String stodo = Do_it.getText().toString();
        String sdate = Date.getText().toString();

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        switch (v.getId()) {
            case R.id.add:
                contentValues.put(DBHelper.KEY_CHECK, Chek.isChecked() ? 1 : 0);
                contentValues.put(DBHelper.KEY_TODO, stodo);
                database.insert(DBHelper.TABLE_PERSONS, null, contentValues);
                break;

            case R.id.read:
                Cursor cursor = database.query(DBHelper.TABLE_PERSONS, null, null, null,
                        null, null, null);

                if (cursor.moveToFirst()) {
                    int idIndex = cursor.getColumnIndex(DBHelper.KEY_CHECK);
                    int nameIndex = cursor.getColumnIndex(DBHelper.KEY_TODO);
                    int emailIndex = cursor.getColumnIndex(DBHelper.KEY_DATE);
                    do {
                        Log.d("mLog", "ID =" + cursor.getInt(idIndex) +
                                ", name = " + cursor.getString(nameIndex) +
                                ", email = " + cursor.getString(emailIndex));

                    } while (cursor.moveToNext());
                } else
                    Log.d("mLog", "0 rows");

                cursor.close(); // освобождение памяти
                break;

            case R.id.clear:
                database.delete(DBHelper.TABLE_PERSONS, null, null);
                break;

            case R.id.delete:
                if (ID.equalsIgnoreCase("")) {
                    break;
                }
                int delCount = database.delete(DBHelper.TABLE_PERSONS, DBHelper.KEY_CHECK + "= " + ID, null);
                Log.d("mLog", "Удалено строк = " + delCount);

            case R.id.update:
                if (ID.equalsIgnoreCase("")) {
                    break;
                }
                contentValues.put(DBHelper.KEY_TODO, stodo);
                contentValues.put(DBHelper.KEY_DATE, sdate);
                int updCount = database.update(DBHelper.TABLE_PERSONS, contentValues, DBHelper.KEY_CHECK + "= ?", new String[]{ID});
                Log.d("mLog", "Обновлено строк = " + updCount);
        }
        dbHelper.close(); // закрываем соединение с БД
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Add = (Button) findViewById(R.id.add);
        Add.setOnClickListener(this);

        Read = (Button) findViewById(R.id.read);
        Read.setOnClickListener(this);

        Clear = (Button) findViewById(R.id.clear);
        Clear.setOnClickListener(this);

        Update = (Button) findViewById(R.id.update);
        Update.setOnClickListener(this);

        Delete = (Button) findViewById(R.id.delete);
        Delete.setOnClickListener(this);

        Do_it = (EditText) findViewById(R.id.do_it);
        Date = (EditText) findViewById(R.id.date);
        Chek = (CheckBox) findViewById(R.id.check);

        dbHelper = new DBHelper(this);
    }

    public class DBHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "myBase";
        public static final String TABLE_PERSONS = "ToDo_List";
        public static final String KEY_ID = "_id";
        public static final String KEY_CHECK = "ischeck";
        public static final String KEY_TODO = "todo";
        public static final String KEY_DATE = "date";

        public DBHelper(@Nullable Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table " + TABLE_PERSONS + "(" + KEY_ID + " integer primary key, " + KEY_CHECK + " integer, " + KEY_TODO + " text, " + KEY_DATE + " text" + ")");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("drop table if exists " + TABLE_PERSONS);
            onCreate(db);
        }
    }
}