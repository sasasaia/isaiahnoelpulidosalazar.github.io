package com.github.isaiahnoelpulidosalazar.androiddependency;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

/**
 * A simplified wrapper around Android's {@link SQLiteDatabase} API for performing
 * common database operations without boilerplate.
 *
 * <p>Column definitions are expressed as colon-separated {@code "name:TYPE"} strings
 * (e.g., {@code "id:INTEGER"}, {@code "name:TEXT"}). Value pairs for insert/delete
 * follow the same {@code "column:value"} convention.</p>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * EasySQL db = new EasySQL(context);
 * db.createTable("mydb", "users", new String[]{"id:INTEGER", "name:TEXT"});
 * db.insertToTable("mydb", "users", new String[]{"id:1", "name:Alice"});
 * List<String> rows = db.getTableValues("mydb", "users");
 * }</pre>
 */
public class EasySQL {
    Context context;

    /**
     * Constructs a new {@code EasySQL} instance bound to the given context.
     *
     * @param context the Android {@link Context} used to open or create databases
     */
    public EasySQL(Context context){
        this.context = context;
    }

    /**
     * Opens (or creates) the SQLite database with the given name and returns a writable handle.
     *
     * @param databaseName the file name of the SQLite database
     * @return a writable {@link SQLiteDatabase}, or {@code null} if an error occurs
     */
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

    /**
     * Creates a new table in the specified database if it does not already exist.
     *
     * <p>Column definitions must be colon-separated strings of the form {@code "columnName:TYPE"},
     * e.g., {@code "id:INTEGER PRIMARY KEY"} or {@code "username:TEXT"}.</p>
     *
     * @param databaseName the name of the database file
     * @param tableName    the name of the table to create
     * @param columns      an array of column definitions in {@code "name:TYPE"} format
     */
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

    /**
     * Checks whether a table with the given name exists in the specified database.
     *
     * @param databaseName the name of the database file
     * @param tableName    the name of the table to check for
     * @return {@code true} if the table exists; {@code false} otherwise
     */
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

    /**
     * Drops (permanently deletes) the specified table from the database, if it exists.
     *
     * @param databaseName the name of the database file
     * @param tableName    the name of the table to delete
     */
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

    /**
     * Removes all rows from the specified table without dropping the table itself.
     *
     * @param databaseName the name of the database file
     * @param tableName    the name of the table to clear
     */
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

    /**
     * Inserts a new row into the specified table.
     *
     * <p>Each element in {@code values} must be a colon-separated {@code "column:value"} string.
     * The column name is everything before the first colon; the value is everything after it,
     * allowing values that themselves contain colons.</p>
     *
     * <p>Example: {@code new String[]{"id:1", "name:Alice"}}</p>
     *
     * @param databaseName the name of the database file
     * @param tableName    the name of the table to insert into
     * @param values       an array of {@code "column:value"} pairs defining the row to insert
     */
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

    /**
     * Deletes rows from the specified table where a column matches a given value.
     *
     * <p>The {@code columnValuePair} must be in {@code "column:value"} format
     * (e.g., {@code "id:1"} or {@code "name:Alice"}). The method automatically
     * detects whether the column is numeric or text and formats the SQL accordingly.</p>
     *
     * @param databaseName     the name of the database file
     * @param tableName        the name of the table to delete from
     * @param columnValuePair  a {@code "column:value"} string identifying rows to delete
     */
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

    /**
     * Retrieves all values from the specified table as a flat list of {@code "column:value"} strings.
     *
     * <p>Numeric values are formatted as {@code "column:value"};
     * text values are formatted as {@code "column:'value'"}.</p>
     *
     * @param databaseName the name of the database file
     * @param tableName    the name of the table to read from
     * @return a {@link List} of {@code "column:value"} strings for every cell in the table;
     *         an empty list if the table is empty or does not exist
     */
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

    /**
     * Retrieves all rows from the specified table, where each row is returned as
     * a {@code String[]} of {@code "column:value"} pairs.
     *
     * <p>Each element of the returned list corresponds to one row, and each element
     * of its inner array corresponds to one column in that row. Numeric values are
     * formatted as {@code "column:value"}; text values as {@code "column:'value'"}.</p>
     *
     * @param databaseName the name of the database file
     * @param tableName    the name of the table to read from
     * @return a {@link List} of {@code String[]} arrays representing each row;
     *         an empty list if the table is empty or does not exist
     */
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