package com.ipb.agrodeo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME ="AssetManagement.db";
    public static final String TABLE_NAME ="AssetSOConfirmation";

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE "+ TABLE_NAME +" (ID INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "UNITID TEXT, KODERUANGAN TEXT, ASSETID TEXT, NAMAASSET TEXT, KODEBARCODE TEXT, KETERANGAN TEXT, STATUS TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropTable = "DROP TABLE IF EXISTS "+TABLE_NAME;
        db.execSQL(dropTable);
    }

    public boolean addData(String unitid, String koderuangan, String assetid, String namaasset, String kodebarcode, String ket, String status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("UNITID", unitid);
        contentValues.put("KODERUANGAN", koderuangan);
        contentValues.put("ASSETID", assetid);
        contentValues.put("NAMAASSET", namaasset);
        contentValues.put("KODEBARCODE", kodebarcode);
        contentValues.put("KETERANGAN", ket);
        contentValues.put("STATUS", status);

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    public Cursor getAssetLists(String unit, String ruangan){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT UNITID, KODERUANGAN, ASSETID, NAMAASSET, KODEBARCODE, "+
                "KETERANGAN, STATUS FROM "+ TABLE_NAME +" WHERE UNITID = '"+ unit +"'"+
                " AND KODERUANGAN = '"+ ruangan +"'", null);
        return data;
     }

    public Cursor getAsset(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT UNITID, KODERUANGAN, ASSETID, NAMAASSET, KODEBARCODE, "+
                "KETERANGAN, STATUS FROM "+ TABLE_NAME, null);
        return data;
    }

    public Cursor getAssetbyBarcode(String barcode, String unit, String ruangan){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM "+ TABLE_NAME +" WHERE (ASSETID = '"+ barcode +"'"+
                " OR KODEBARCODE = '"+ barcode +"') AND UNITID = '"+ unit +"' AND KODERUANGAN = '"+ ruangan +"'", null);
        return data;
    }

     public boolean updateData(String kode, String ket, String status){
         SQLiteDatabase db = this.getWritableDatabase();
         ContentValues contentValues = new ContentValues();
         contentValues.put("KETERANGAN", ket);
         contentValues.put("STATUS", status);

         long result = db.update(TABLE_NAME, contentValues,"ASSETID =? OR KODEBARCODE =?",new String[] {kode, kode});
         return result != -1;
     }

    public boolean deleteData(String unit, String ruangan){
        SQLiteDatabase db = this.getReadableDatabase();

        long result = db.delete(TABLE_NAME,"UNITID =? AND KODERUANGAN =?",new String[] {unit, ruangan});
        return result != -1;
    }
}
