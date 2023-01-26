package id.co.pcsindonesia.ia.diagnostic.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import id.co.pcsindonesia.ia.diagnostic.helper.GlobalHelper;
import id.co.pcsindonesia.ia.diagnostic.model.GeneralConfigModel;
import id.co.pcsindonesia.ia.diagnostic.model.LocationModel;
import id.co.pcsindonesia.ia.diagnostic.util.ENC;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DBConfig extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dcm_config.db";

    private final String GENERAL_HB_CONFIG_TABLE = "payment";
    private final String TABLE_LOCATION = "location";
    private final String TABLE_APPS_STORE = "apps_store";

    private final String LCT_LONGITUDE = "longitude";
    private final String LCT_LATITUDE = "latitude";
    private final String ID = "id";
    private final String GC_GET_CONFIG_TIME = "get_config_time";
    private final String GC_POST_HB_PERIOD = "post_hb_period";
    private final String GC_SECONDARY_HB_PERIOD = "secondary_hb_period";
    private final String GC_STATUS = "status";
    private final String GC_CREATED_DATE = "created_date";

    private final String STR_APP_NAME = "app_name";
    private final String STR_APP_FILENAME = "app_filename";
    private final String STR_APP_URL = "app_url";
    private final String STR_APP_VERSION_NAME = "app_version_name";
    private final String STR_APP_VERSION_CODE = "app_version_code";
    private final String STR_APP_FILE_SIZE = "app_file_size";
    private final String STR_APP_LAST_UPDATE = "app_last_update";
    private final String STR_APP_PACKAGE_NAME = "app_package_name";
    private final String STR_APP_ICON_URL = "app_icon_url";
    private final String STR_APP_STATUS = "status";
    private final String STR_APP_UNINSTALL = "app_uninstall";
    private final String STR_APP_UNINSTALL_USE_PASS = "app_uninstall_use_pass";
    private final String STR_APP_UNINSTALL_PASSWORD = "app_uninstall_password";
    private final String STR_APP_INSTALL = "app_install";
    private final String STR_APP_INSTALL_USE_PASS = "app_install_use_pass";
    private final String STR_APP_INSTALL_PASS = "app_install_pass";

    private Context context;

    public DBConfig(Context context){
        super(context, DATABASE_NAME , null, 7);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table  " + TABLE_APPS_STORE +
                        " (id integer primary key autoincrement , "+STR_APP_NAME+" text,"+STR_APP_FILENAME+" text,"
                        +STR_APP_URL+" text,"+STR_APP_VERSION_NAME+" text,"+STR_APP_VERSION_CODE+" int,"+STR_APP_FILE_SIZE+" int,"
                        +STR_APP_LAST_UPDATE+" text,"+STR_APP_PACKAGE_NAME+" text,"+STR_APP_ICON_URL+" text," +
                        STR_APP_STATUS+" int,"+STR_APP_UNINSTALL+" int,"+STR_APP_UNINSTALL_USE_PASS+" int,"+STR_APP_UNINSTALL_PASSWORD+" int," +
                        STR_APP_INSTALL+" int,"+STR_APP_INSTALL_USE_PASS+" int,"+STR_APP_INSTALL_PASS+" int)"
        );

        db.execSQL(
                "create table  " + GENERAL_HB_CONFIG_TABLE +
                        " (id integer primary key autoincrement , "+GC_GET_CONFIG_TIME+" text,"+GC_POST_HB_PERIOD
                        +" text,"+GC_STATUS+" text, " +GC_SECONDARY_HB_PERIOD+" text,"+ GC_CREATED_DATE+" text)"
        );
        db.execSQL(
                "create table  " + TABLE_LOCATION +
                        " (id integer primary key autoincrement , "+LCT_LATITUDE+" double,"+LCT_LONGITUDE+" double)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+GENERAL_HB_CONFIG_TABLE);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_LOCATION);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_APPS_STORE);
        onCreate(db);
    }

    //======================== INSERT FUNCTION =======================
    public boolean insertGenConfig(GeneralConfigModel data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        if(getGenConfigCount() == 0) {
            contentValues.put(GC_GET_CONFIG_TIME, ENC.encrypt(data.getGet_config_time()));
            contentValues.put(GC_POST_HB_PERIOD, ENC.encrypt(data.getPost_hb_period()));
            contentValues.put(GC_SECONDARY_HB_PERIOD, ENC.encrypt(data.getSecondary_hb_period()));
            contentValues.put(GC_STATUS, ENC.encrypt(data.getStatus()));
            contentValues.put(GC_CREATED_DATE, ENC.encrypt(GlobalHelper.ddMMyyyy.format(new Date())));


            db.insert(GENERAL_HB_CONFIG_TABLE, null, contentValues);
            db.close();
        }else{
            contentValues.put(GC_GET_CONFIG_TIME, ENC.encrypt(data.getGet_config_time()));
            contentValues.put(GC_POST_HB_PERIOD, ENC.encrypt(data.getPost_hb_period()));
            contentValues.put(GC_SECONDARY_HB_PERIOD, ENC.encrypt(data.getSecondary_hb_period()));
            contentValues.put(GC_STATUS, ENC.encrypt(data.getStatus()));
            contentValues.put(GC_CREATED_DATE, ENC.encrypt(GlobalHelper.ddMMyyyy.format(new Date())));


            int vals = db.update(GENERAL_HB_CONFIG_TABLE,  contentValues,
                    ID+" = 1",null);

            db.close();
        }
        return true;
    }

    //======================== INSERT FUNCTION =======================
    public boolean insertLocation(LocationModel data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(getLocationCount() == 0) {
            contentValues.put(LCT_LATITUDE, data.getLATITUDE());
            contentValues.put(LCT_LONGITUDE, data.getLONGITUDE());

            db.insert(TABLE_LOCATION, null, contentValues);
            db.close();
        }else{
            contentValues.put(LCT_LATITUDE, data.getLATITUDE());
            contentValues.put(LCT_LONGITUDE, data.getLONGITUDE());

            int vals = db.update(TABLE_LOCATION,  contentValues,
                    ID+" = 1",null);

            db.close();
        }
        return true;
    }



    public ArrayList<GeneralConfigModel> getAllGenConfig(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<GeneralConfigModel> array_list = new ArrayList<>();

        Cursor res =  null;

        try {
            res =  db.rawQuery( "select get_config_time,post_hb_period,secondary_hb_period,status,created_date  from "+GENERAL_HB_CONFIG_TABLE+" where id =1", null );
            res.moveToFirst();
            while(!res.isAfterLast()){
                GeneralConfigModel model = new GeneralConfigModel();
                model.setGet_config_time(ENC.decrypt(res.getString(res.getColumnIndex(GC_GET_CONFIG_TIME))));
                model.setPost_hb_period(ENC.decrypt(res.getString(res.getColumnIndex(GC_POST_HB_PERIOD))));
                model.setSecondary_hb_period(ENC.decrypt(res.getString(res.getColumnIndex(GC_SECONDARY_HB_PERIOD))));
                model.setStatus(Integer.parseInt(ENC.decrypt(res.getString(res.getColumnIndex(GC_STATUS)))));
                model.setCreated_date(ENC.decrypt(res.getString(res.getColumnIndex(GC_CREATED_DATE))));
                array_list.add(model);
                res.moveToNext();

                res.close();

            }
            res.close();
        } finally {
            if(res != null)
                res.close();
        }
        res.close();
        db.close();
        return array_list;
    }

    public ArrayList<LocationModel> getAllLocation(){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<LocationModel> array_list = new ArrayList<>();


        Cursor res =  null;
        try {
            res =  db.rawQuery( "select * from "+TABLE_LOCATION+" where id =1", null );
            res.moveToFirst();

            while(!res.isAfterLast()){
                LocationModel model = new LocationModel();
                model.setLONGITUDE(res.getDouble(res.getColumnIndex(LCT_LONGITUDE)));
                model.setLATITUDE(res.getDouble(res.getColumnIndex(LCT_LATITUDE)));
                array_list.add(model);
                res.moveToNext();
                res.close();
            }
        } finally {
            res.close();
        }
        res.close();
        db.close();
        return array_list;
    }

    public int getGenConfigCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        int var =  (int) DatabaseUtils.queryNumEntries(db, GENERAL_HB_CONFIG_TABLE);
        return  var;
    }

    public int getLocationCount(){
        SQLiteDatabase db = this.getReadableDatabase();
        int var =  (int) DatabaseUtils.queryNumEntries(db, TABLE_LOCATION);
        return  var;
    }



}
