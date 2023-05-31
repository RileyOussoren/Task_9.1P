package com.rileyoussoren.lostandfoundMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "itemsdb";

    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "items";

    private static final String ID_COL = "id";

    private static final String STATUS_COL = "status";

    private static final String NAME_COL = "name";

    private static final String PHONE_COL = "phone";

    private static final String DESCRIPTION_COL = "description";

    private static final String DATE_COL = "date";

    private static final String LOCATION_COL = "location";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + STATUS_COL + " TEXT,"
                + NAME_COL + " TEXT,"
                + PHONE_COL + " TEXT,"
                + DESCRIPTION_COL + " TEXT,"
                + DATE_COL + " TEXT,"
                +LOCATION_COL + " TEXT)";

        db.execSQL(query);

    }

    public void addItem(String itemStatus, String itemName, String itemPhone, String itemDescription, String itemDate, LatLng itemLocation){

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(STATUS_COL, itemStatus);
        values.put(NAME_COL, itemName);
        values.put(PHONE_COL,itemPhone);
        values.put(DESCRIPTION_COL,itemDescription);
        values.put(DATE_COL,itemDate);
        values.put(LOCATION_COL, String.valueOf(itemLocation));

        db.insert(TABLE_NAME, null, values);

        db.close();

    }

    public ArrayList<ItemModal> readItems(){

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorItems = db.rawQuery("SELECT * FROM " + TABLE_NAME, null );

        ArrayList<ItemModal> itemModalArrayList = new ArrayList<>();

        if (cursorItems.moveToFirst()) {
            do {
                itemModalArrayList.add(new ItemModal(
                        cursorItems.getString(1),
                        cursorItems.getString(2),
                        cursorItems.getString(3),
                        cursorItems.getString(4),
                        cursorItems.getString(5),
                        cursorItems.getString(6)));
            } while (cursorItems.moveToNext());
        }

        cursorItems.close();
        return itemModalArrayList;

    }

    public void deleteItem(String itemName){

        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_NAME, "name=?", new String[]{itemName});
        db.close();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

}
