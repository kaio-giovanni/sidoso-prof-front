package com.sidoso.profissional.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "db_sidoso_msg.sqlite";
    private static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_TABLE = "CREATE TABLE "   + MessageReadContract.MessageEntry.TABLE_NAME + " ( " +
            MessageReadContract.MessageEntry.COLUMN_ID             + " INTEGER PRIMARY KEY AUTOINCREMENT, "      +
            MessageReadContract.MessageEntry.COLUMN_SENDER_ID      + " INTEGER NOT NULL, "                  +
            MessageReadContract.MessageEntry.COLUMN_RECEPTOR_ID    + " INTEGER NOT NULL, "                   +
            MessageReadContract.MessageEntry.COLUMN_RECEPTOR_EMAIL + " VARCHAR(50) NOT NULL, "            +
            MessageReadContract.MessageEntry.COLUMN_MESSAGE   + " VARCHAR(100) NOT NULL, "                   +
            MessageReadContract.MessageEntry.COLUMN_SENDER_AT + " VARCHAR(25) NOT NULL )";
    private static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + MessageReadContract.MessageEntry.TABLE_NAME;
    protected SQLiteDatabase database;

    public DataBase(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DROP_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public SQLiteDatabase getDataBase(){
        if(this.database == null){
            database = getWritableDatabase();
        }
        return this.database;
    }
}

