package id.co.pcsindonesia.ia.diagnostic.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

import id.co.pcsindonesia.ia.diagnostic.model.CommandModel;
import id.co.pcsindonesia.ia.diagnostic.util.ENC;

import java.util.ArrayList;

public class DBCommands {
    private static final String PUSH_ID = "id";
    public static final String PUSH_PACKAGE_NAME = "package";
    public static final String PUSH_COMMAND_CODE = "command";
    public static final String PUSH_APP_URL = "app_url";
    public static final String PUSH_APP_NAME = "app_name";
    public static final String PUSH_APP_FILENAME = "app_filename";
    public static final String PUSH_ICON_URL = "app_icon_url";
    public static final String PUSH_APP_VERSION = "app_version";

    static Context context;

    public DBCommands(Context context) {
        this.context = context;
    }

    public void insertPushCommands(CommandModel model){
        DBHelper helper = new DBHelper(context);

        SQLiteDatabase db = helper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PUSH_PACKAGE_NAME, ENC.encrypt(model.getPush_app_package()));
        contentValues.put(PUSH_COMMAND_CODE, ENC.encrypt(model.getPush_command_code()));
        contentValues.put(PUSH_APP_URL, ENC.encrypt(model.getApp_url()));
        contentValues.put(PUSH_APP_NAME, ENC.encrypt(model.getApp_name()));
        contentValues.put(PUSH_APP_FILENAME, ENC.encrypt(model.getApp_filename()));
        contentValues.put(PUSH_ICON_URL, ENC.encrypt(model.getApp_icon_url()));
        contentValues.put(PUSH_APP_VERSION, ENC.encrypt(model.getApp_version()));

        db.insert(helper.TABLE_PUSH_COMMANDS, null, contentValues);
        db.close();
    }

    public int getCountCommand(){
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        int result = (int) DatabaseUtils.queryNumEntries(db, helper.TABLE_PUSH_COMMANDS);
        db.close();
        return result;
    }

    public void deleteAllCommand() {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        db.execSQL("delete from "+ helper.TABLE_PUSH_COMMANDS);
    }

    public int deleteByCommandId(int id) {
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getWritableDatabase();
        Cursor cursor = db.query(helper.TABLE_PUSH_COMMANDS, null, null,
                null, null, null, null);


        int i = 0;
        if(cursor.moveToFirst()) {
            i = db.delete(helper.TABLE_PUSH_COMMANDS, "id=?",  new String[]{String.valueOf(id)});
        }
        cursor.close();
        return i ;
    }

    public ArrayList<CommandModel> getAllCommand(){
        DBHelper helper = new DBHelper(context);
        SQLiteDatabase db = helper.getReadableDatabase();
        ArrayList<CommandModel> array_list = new ArrayList<>();

        Cursor res =  db.rawQuery( "select * from "+helper.TABLE_PUSH_COMMANDS, null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            CommandModel model = new CommandModel();
            model.setId(res.getInt(res.getColumnIndex(PUSH_ID)));
            model.setPush_app_package(ENC.decrypt(res.getString(res.getColumnIndex(PUSH_PACKAGE_NAME))));
            model.setPush_command_code(ENC.decrypt(res.getString(res.getColumnIndex(PUSH_COMMAND_CODE))));
            model.setApp_name(ENC.decrypt(res.getString(res.getColumnIndex(PUSH_APP_NAME))));
            model.setApp_filename(ENC.decrypt(res.getString(res.getColumnIndex(PUSH_APP_FILENAME))));
            model.setApp_url(ENC.decrypt(res.getString(res.getColumnIndex(PUSH_APP_URL))));
            model.setApp_version(ENC.decrypt(res.getString(res.getColumnIndex(PUSH_APP_VERSION))));
            array_list.add(model);
            res.moveToNext();
        }
        res.close();
        return array_list;
    }
}
