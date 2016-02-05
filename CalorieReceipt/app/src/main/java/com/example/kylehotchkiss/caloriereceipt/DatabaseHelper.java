package com.example.kylehotchkiss.caloriereceipt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kylehotchkiss on 1/30/16.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Receipt.db";
    public static final String TABLE_NAME_1 = "receipt_table";
    public static final String REC_COL_1 = "ID";
    public static final String REC_COL_2 = "NAME";
    public static final String REC_COL_3 = "TOTAL_CALORIES";
    public static final String REC_COL_4 = "CREATED_AT";
    public static final String TABLE_NAME_2 = "activities_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "RECEIPT_ID";
    public static final String COL_3 = "NAME";
    public static final String COL_4 = "IS_REPS";
    public static final String COL_5 = "AMOUNT";
    public static final String COL_6 = "CALORIES";
    public static final String[] activities = {"Select Activity...", "Cycling", "Jogging", "Jumping Jacks", "Leg-Lift", "Plank",
            "Push ups", "Pull ups", "Sit ups", "Squats","Stair-Climbing","Swimming", "Walking"};
    private static final Integer[] is_reps = {1, 1, 1, 0, 0, 0, 1, 0, 0, 0, 0, 0};
    final Map<String, Integer> conversions = new HashMap<String, Integer>();
    final Map<String, Integer> reps = new HashMap<String, Integer>();


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_NAME_1 + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, TOTAL_CALORIES INTEGER, CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
        db.execSQL("create table " + TABLE_NAME_2 + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, NAME TEXT, IS_REPS INTEGER, AMOUNT INTEGER, CALORIES INTEGER, RECEIPT_ID INTEGER)");
        conversions.put(activities[1], 12);
        conversions.put(activities[2], 12);
        conversions.put(activities[3], 10);
        conversions.put(activities[4], 25);
        conversions.put(activities[5], 25);
        conversions.put(activities[6], 350);
        conversions.put(activities[7], 100);
        conversions.put(activities[8], 200);
        conversions.put(activities[9], 225);
        conversions.put(activities[10], 15);
        conversions.put(activities[11], 13);
        conversions.put(activities[12], 20);
        reps.put(activities[1], 0);
        reps.put(activities[2], 0);
        reps.put(activities[3], 0);
        reps.put(activities[4], 0);
        reps.put(activities[5], 0);
        reps.put(activities[6], 1);
        reps.put(activities[7], 1);
        reps.put(activities[8], 1);
        reps.put(activities[9], 1);
        reps.put(activities[10], 0);
        reps.put(activities[11], 0);
        reps.put(activities[12], 0);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_1);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_2);
        onCreate(db);
    }

    public Cursor getReceipts() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select rowid _id, * from " + TABLE_NAME_1, null);
        return res;
    }

    public Cursor getItems(Integer receipt_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME_2 + " s where s." + COL_2 + " =" + receipt_id, null);
        return res;
    }

    public void saveItem(String receipt_name, String activity_name, Integer reps, Integer is_reps, Integer calories) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME_1 + " where " + REC_COL_2 + " ='" + receipt_name + "'", null);
        if (cursor!=null && cursor.getCount()>0) {
            if (cursor.getCount() > 1) {
                cursor.moveToLast();
            } else {
                cursor.moveToFirst();
            }
            Integer id = cursor.getInt(0);
            Integer current_calories = cursor.getInt(cursor.getColumnIndex(REC_COL_3));
            ContentValues row = new ContentValues();
            row.put(COL_2, id);
            row.put(COL_3, activity_name);
            row.put(COL_4, is_reps);
            row.put(COL_5, reps);
            row.put(COL_6, calories);
            db.insert(TABLE_NAME_2, null, row);
            ContentValues data=new ContentValues();
            data.put(REC_COL_3, current_calories + calories);
            db.update(TABLE_NAME_1, data, "ID=" + id, null);
        } else {
            ContentValues data=new ContentValues();
            data.put(REC_COL_2, receipt_name);
            data.put(REC_COL_3, calories);
            db.insert(TABLE_NAME_1, null, data);
            cursor = db.rawQuery("select ID from " + TABLE_NAME_1 + " s where s." + REC_COL_2 + " ='" + receipt_name + "'", null);
            cursor.moveToFirst();
            Integer id = cursor.getInt(0);
            ContentValues row = new ContentValues();
            row.put(COL_2, id);
            row.put(COL_3, activity_name);
            row.put(COL_4, is_reps);
            row.put(COL_5, reps);
            row.put(COL_6, calories);
            db.insert(TABLE_NAME_2, null, row);
        }
    }
}
