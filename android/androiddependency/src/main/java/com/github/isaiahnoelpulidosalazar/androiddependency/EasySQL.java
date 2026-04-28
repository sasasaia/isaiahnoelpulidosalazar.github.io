package com.github.isaiahnoelpulidosalazar.androiddependency;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class EasySQL {
    Context context;

    public EasySQL(Context context){
        this.context = context;
    }

    SQLiteDatabase database(String databaseName){
        try {
            SQLiteOpenHelper sqLiteOpenHelper = new SQLiteOpenHelper(context, databaseName, null, 1) {
                @Override
                public void onCreate(SQLiteDatabase db) {
                }
                @Override
                public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                }
            };
            return sqLiteOpenHelper.getWritableDatabase();
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return null;
    }

    public void createTable(String databaseName, String tableName, String[] columns){
        try {
            SQLiteDatabase db = database(databaseName);
            String query = "CREATE TABLE " + tableName + "(";
            for (int a = 0; a < columns.length; a++){
                if (a != columns.length - 1){
                    query += columns[a].split(":")[0] + " " + columns[a].split(":")[1] + ",";
                } else {
                    query += columns[a].split(":")[0] + " " + columns[a].split(":")[1];
                }
            }
            query += ")";
            if (!doesTableExist(databaseName, tableName)){
                db.execSQL(query);
            }
            db.close();
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public boolean doesTableExist(String databaseName, String tableName){
        try {
            SQLiteDatabase db = database(databaseName);
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);
            cursor.close();
            db.close();
            return true;
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return false;
    }

    public void deleteTable(String databaseName, String tableName){
        try {
            SQLiteDatabase db = database(databaseName);
            if (doesTableExist(databaseName, tableName)){
                db.execSQL("DROP TABLE " + tableName);
            }
            db.close();
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public void clearTable(String databaseName, String tableName){
        try {
            SQLiteDatabase db = database(databaseName);
            if (doesTableExist(databaseName, tableName)){
                db.execSQL("DELETE FROM " + tableName);
            }
            db.close();
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public void insertToTable(String databaseName, String tableName, String[] values){
        try {
            SQLiteDatabase db = database(databaseName);
            if (doesTableExist(databaseName, tableName)){
                ContentValues contentValues = new ContentValues();
                for (String value : values){
                    contentValues.put(value.split(":")[0], value.substring(value.split(":")[0].length() + 1));
                }
                db.insert(tableName, null, contentValues);
            }
            db.close();
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public void deleteFromTable(String databaseName, String tableName, String columnValuePair){
        try {
            SQLiteDatabase db = database(databaseName);
            if (doesTableExist(databaseName, tableName)){
                Cursor cursor = db.rawQuery("SELECT " + columnValuePair.split(":")[0] + " FROM " + tableName, null);
                cursor.moveToNext();
                int columnType = cursor.getType(0);
                cursor.close();
                if (columnType == Cursor.FIELD_TYPE_INTEGER || columnType == Cursor.FIELD_TYPE_FLOAT){
                    db.execSQL("DELETE FROM " + tableName + " WHERE " + columnValuePair.split(":")[0] + "=" + columnValuePair.split(":")[1]);
                } else {
                    db.execSQL("DELETE FROM " + tableName + " WHERE " + columnValuePair.split(":")[0] + "='" + columnValuePair.split(":")[1] + "'");
                }
            }
            db.close();
        } catch (Exception exception){
            exception.printStackTrace();
        }
    }

    public List<String> getTableValues(String databaseName, String tableName){
        List<String> temp = new ArrayList<>();
        try {
            SQLiteDatabase db = database(databaseName);
            if (doesTableExist(databaseName, tableName)){
                Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);
                while (cursor.moveToNext()){
                    for (int a = 0; a < cursor.getColumnCount(); a++){
                        int columnType = cursor.getType(a);
                        if (columnType == Cursor.FIELD_TYPE_INTEGER || columnType == Cursor.FIELD_TYPE_FLOAT){
                            temp.add(cursor.getColumnName(a) + ":" + cursor.getString(a));
                        } else {
                            temp.add(cursor.getColumnName(a) + ":'" + cursor.getString(a) + "'");
                        }
                    }
                }
                cursor.close();
            }
            db.close();
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return temp;
    }

    public List<String[]> getTableValuesAsArray(String databaseName, String tableName){
        List<String[]> temp = new ArrayList<>();
        try {
            SQLiteDatabase db = database(databaseName);
            if (doesTableExist(databaseName, tableName)){
                Cursor cursor = db.rawQuery("SELECT * FROM " + tableName, null);
                while (cursor.moveToNext()){
                    String[] raw = new String[cursor.getColumnCount()];
                    for (int a = 0; a < cursor.getColumnCount(); a++){
                        int columnType = cursor.getType(a);
                        if (columnType == Cursor.FIELD_TYPE_INTEGER || columnType == Cursor.FIELD_TYPE_FLOAT){
                            raw[a] = cursor.getColumnName(a) + ":" + cursor.getString(a);
                        } else {
                            raw[a] = cursor.getColumnName(a) + ":'" + cursor.getString(a) + "'";
                        }
                    }
                    temp.add(raw);
                }
                cursor.close();
            }
            db.close();
        } catch (Exception exception){
            exception.printStackTrace();
        }
        return temp;
    }
}
