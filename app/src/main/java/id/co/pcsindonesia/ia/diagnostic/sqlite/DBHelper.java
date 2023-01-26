package id.co.pcsindonesia.ia.diagnostic.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import id.co.pcsindonesia.ia.diagnostic.model.ConfigModel;
import id.co.pcsindonesia.ia.diagnostic.model.DataModel;
import id.co.pcsindonesia.ia.diagnostic.util.ENC;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dcm.db";
    //---------------- TABLE NAME -------------------------------
    private static final String TRANSACTION_TABLE_NAME = "trx";
    private static final String CONFIG_TABLE_NAME = "conf";
    public static final String TABLE_PUSH_COMMANDS = "commands";


    //---------------- TRANSACTION TABLE ------------------------
    private static final String TRX_ISPAID = "isPaid";
    private static final String TRX_NUMBER = "trxNumber";
    private static final String TRX_PRODUCT_AMOUNT = "productAmount";
    private static final String TRX_MERCHANT_CODE = "merchantCode";
    private static final String TRX_CURRENT_VOLUME = "currentVolume";
    private static final String TRX_CURRENT_AMOUNT = "currentAmount";
    private static final String TRX_PRODUCT_NUMBER = "productNumber";
    private static final String TRX_SUBMERCHANT_CODE = "submerchantCode";
    private static final String TRX_PRODUCT_NAME = "productName";
    private static final String TRX_MERCHANT_NAME = "merchantName";
    private static final String TRX_PAYMENT_TYPE = "paymentType";
    private static final String TRX_DATE = "date";
    private static final String TRX_APPROVAL_CODE = "approvalCode";
    private static final String TRX_MASKEDPAN = "maskedPAN";
    private static final String TRX_MID = "mid";
    private static final String TRX_TID = "tid";
    private static final String TRX_ROUTING = "routing";
    private static final String TRX_ACQUIRING = "acquiring";
    private static final String TRX_PUMP_NUMBER = "pumpNumber";
    private static final String TRX_OPERATOR_NAME = "operatorName";
    private static final String TRX_REFUND = "refund";

    //---------------- CONFIG TABLE ---------------------
    private static final String CNF_hbid = "hb_id";
    private static final String CNF_MERCHANT_ID = "merchant_id";
    private static final String CNF_SUBMERCHANT_ID = "submerchant_id";
    private static final String CNF_HB_TIME_PERIODE = "hb_time_periode";
    private static final String CNF_HB_DAY_SCHEDULE = "hb_day_schedule";
    private static final String CNF_HB_TIME_GET_PARAM = "hb_time_get_param";
    private static final String CNF_HB_DAY_GET_PARAM = "hb_day_get_param";
    private static final String CNF_HB_TIME_GET_MERCHANT = "hb_time_get_merchant";
    private static final String CNF_MAX_HB_LOCAL = "max_hb_local";
    private static final String CNF_STATUS = "status";
    private static final String CNF_MAX_LISTVIEW_TRX = "max_listview_trx";
    private static final String CNF_DATE_REQUEST = "date_request";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table  " + TRANSACTION_TABLE_NAME +
                        " (id integer primary key autoincrement , "+TRX_ISPAID+" text,"+TRX_NUMBER+" text,"+TRX_PRODUCT_AMOUNT+" text, " +
                        TRX_MERCHANT_CODE+" text,"+TRX_CURRENT_VOLUME+" text,"+TRX_CURRENT_AMOUNT+" text,"+TRX_PRODUCT_NUMBER+" text" +
                        ", "+TRX_SUBMERCHANT_CODE+" text, "+TRX_PRODUCT_NAME+" text, "+TRX_MERCHANT_NAME+" text,"+TRX_PAYMENT_TYPE+" text," +
                        TRX_DATE+" text,"+TRX_APPROVAL_CODE+" text,"+TRX_MASKEDPAN+" text,"+TRX_MID+" text,"+TRX_TID+" text, " +
                        TRX_ROUTING+" text,"+TRX_ACQUIRING+" text,"+TRX_PUMP_NUMBER+" text,"+TRX_OPERATOR_NAME+" text,"+TRX_REFUND+" text)"
        );

        db.execSQL(
                "create table  " + CONFIG_TABLE_NAME +
                        " (id integer primary key autoincrement , "+CNF_hbid+" text,"+CNF_HB_DAY_GET_PARAM+" text,"+CNF_HB_DAY_SCHEDULE+" text, " +
                        CNF_HB_TIME_GET_MERCHANT+" text,"+CNF_HB_TIME_GET_PARAM+" text,"+CNF_HB_TIME_PERIODE+" text," +
                        CNF_MAX_HB_LOCAL+" int, "+CNF_MAX_LISTVIEW_TRX+" int, "+CNF_MERCHANT_ID+" text,"+CNF_STATUS+" text," +
                        CNF_SUBMERCHANT_ID+" text,"+CNF_DATE_REQUEST+" text)"
        );

        db.execSQL(
                "create table "+ TABLE_PUSH_COMMANDS +
                        " (id integer primary key autoincrement, "+DBCommands.PUSH_COMMAND_CODE+" text,"+
                        DBCommands.PUSH_PACKAGE_NAME+" text,"+DBCommands.PUSH_APP_FILENAME+" text,"+
                        DBCommands.PUSH_APP_NAME+" text,"+DBCommands.PUSH_APP_URL+" text, "+
                        DBCommands.PUSH_ICON_URL+" text,"+DBCommands.PUSH_APP_VERSION+" text)"
        );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS "+TRANSACTION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+CONFIG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PUSH_COMMANDS);
        onCreate(db);
    }

    public void truncateTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+TRANSACTION_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+CONFIG_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_PUSH_COMMANDS);
        onCreate(db);
    }

    public boolean insertTransaction(DataModel data){
        checkRowPump(data.getPumpNumber());

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRX_ACQUIRING, ENC.encrypt(data.getAcquiring()));
        contentValues.put(TRX_APPROVAL_CODE, ENC.encrypt(data.getApprovalCode()));
        contentValues.put(TRX_CURRENT_AMOUNT, ENC.encrypt(data.getCurrentAmount()));
        contentValues.put(TRX_CURRENT_VOLUME, ENC.encrypt(data.getCurrentVolume()));
        contentValues.put(TRX_DATE, ENC.encrypt(data.getDate()));
        contentValues.put(TRX_MASKEDPAN, ENC.encrypt(data.getMaskedPAN()));
        contentValues.put(TRX_MERCHANT_CODE, ENC.encrypt(data.getMerchantCode()));
        contentValues.put(TRX_MERCHANT_NAME, ENC.encrypt(data.getMerchantName()));
        contentValues.put(TRX_MID, ENC.encrypt(data.getMid()));
        contentValues.put(TRX_NUMBER, ENC.encrypt(data.getTrxNumber()));
        contentValues.put(TRX_OPERATOR_NAME, ENC.encrypt(data.getOperatorName()));
        contentValues.put(TRX_PAYMENT_TYPE, ENC.encrypt(data.getPaymentType()));
        contentValues.put(TRX_PRODUCT_AMOUNT, ENC.encrypt(data.getProductAmount()));
        contentValues.put(TRX_PRODUCT_NAME, ENC.encrypt(data.getProductName()));
        contentValues.put(TRX_PRODUCT_NUMBER, ENC.encrypt(data.getProductNumber()));
        contentValues.put(TRX_PUMP_NUMBER, ENC.encrypt(data.getPumpNumber()));
        contentValues.put(TRX_REFUND, ENC.encrypt(data.getRefund()));
        contentValues.put(TRX_ROUTING, ENC.encrypt(data.getRouting()));
        contentValues.put(TRX_SUBMERCHANT_CODE, ENC.encrypt(data.getSubmerchantCode()));
        contentValues.put(TRX_TID, ENC.encrypt(data.getTid()));

        db.insert(TRANSACTION_TABLE_NAME, null, contentValues);
        return true;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TRANSACTION_TABLE_NAME);
    }

    public void deleteAllTrx() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TRANSACTION_TABLE_NAME, null, null, null, null, null, null);

        if(cursor.getCount() > 0) {
            db.execSQL("delete from "+ TRANSACTION_TABLE_NAME);
        }
        cursor.close();
    }

    public void deleteTrxById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TRANSACTION_TABLE_NAME, null, null, null, null, null, null);

        if(cursor.moveToFirst()) {
            db.delete(TRANSACTION_TABLE_NAME, "id=?",  new String[]{String.valueOf(id)});
        }
        cursor.close();
    }

    private void checkRowPump(String pump){
        int maxRow = 3;
        ConfigModel dparam = getHBConfig();
        if(dparam.getMax_listview_trx() > 0){
            maxRow = dparam.getMax_listview_trx();
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TRANSACTION_TABLE_NAME+" WHERE "+TRX_PUMP_NUMBER+" = '" +
                pump+"' ORDER BY id ASC", null );
        if(res.getCount() >= maxRow){
            if(res.moveToFirst()) {
                String rowId = res.getString(res.getColumnIndex("id"));

                db.delete(TRANSACTION_TABLE_NAME, "id=?",  new String[]{rowId});
            }
        }
        res.close();
    }

    public ArrayList<DataModel> getAllTransaction() {
        ArrayList<DataModel> array_list = new ArrayList<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+TRANSACTION_TABLE_NAME+" ORDER BY "+TRX_PUMP_NUMBER+" ASC,id DESC", null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            DataModel model = new DataModel();
            model.setAcquiring(ENC.decrypt(res.getString(res.getColumnIndex(TRX_ACQUIRING))));
            model.setApprovalCode(ENC.decrypt(res.getString(res.getColumnIndex(TRX_APPROVAL_CODE))));
            model.setCurrentAmount(Double.parseDouble(ENC.decrypt(res.getString(res.getColumnIndex(TRX_CURRENT_AMOUNT)))));
            model.setProductAmount(Double.parseDouble(ENC.decrypt(res.getString(res.getColumnIndex(TRX_PRODUCT_AMOUNT)))));
            model.setCurrentVolume(Double.parseDouble(ENC.decrypt(res.getString(res.getColumnIndex(TRX_CURRENT_VOLUME)))));
            model.setDate(ENC.decrypt(res.getString(res.getColumnIndex(TRX_DATE))));
            model.setTrxNumber(ENC.decrypt(res.getString(res.getColumnIndex(TRX_NUMBER))));
            model.setProductName(ENC.decrypt(res.getString(res.getColumnIndex(TRX_PRODUCT_NAME))));
            model.setProductNumber(Double.parseDouble(ENC.decrypt(res.getString(res.getColumnIndex(TRX_PRODUCT_NUMBER)))));
            model.setPaymentType(ENC.decrypt(res.getString(res.getColumnIndex(TRX_PAYMENT_TYPE))));
            model.setMid(ENC.decrypt(res.getString(res.getColumnIndex(TRX_MID))));
            model.setTid(ENC.decrypt(res.getString(res.getColumnIndex(TRX_TID))));
            model.setRouting(ENC.decrypt(res.getString(res.getColumnIndex(TRX_ROUTING))));
            model.setMaskedPAN(ENC.decrypt(res.getString(res.getColumnIndex(TRX_MASKEDPAN))));
            model.setMerchantCode(ENC.decrypt(res.getString(res.getColumnIndex(TRX_MERCHANT_CODE))));
            model.setSubmerchantCode(ENC.decrypt(res.getString(res.getColumnIndex(TRX_SUBMERCHANT_CODE))));
            model.setOperatorName(ENC.decrypt(res.getString(res.getColumnIndex(TRX_OPERATOR_NAME))));
            model.setPumpNumber(ENC.decrypt(res.getString(res.getColumnIndex(TRX_PUMP_NUMBER))));
            model.setRefund(Double.parseDouble(ENC.decrypt(res.getString(res.getColumnIndex(TRX_REFUND)))));
            model.setDbId(res.getInt(res.getColumnIndex("id")));
            array_list.add(model);
            res.moveToNext();
        }
        res.close();
        return array_list;
    }

    public int rowsConfig(){
        SQLiteDatabase db = this.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, CONFIG_TABLE_NAME);
    }

    public void deleteAllConfig() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(CONFIG_TABLE_NAME, null, null, null,
                null, null, null);

        if(cursor.getCount() > 0) {
            db.execSQL("delete from "+ CONFIG_TABLE_NAME);
        }
        cursor.close();
    }

    public void updatingConfig(ConfigModel model){
        if(rowsConfig() == 0){
            insertConfig(model);
        }else{
            deleteAllConfig();
            insertConfig(model);
        }
    }

    public boolean insertConfig(ConfigModel data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CNF_HB_DAY_GET_PARAM, ENC.encrypt(data.getHb_day_get_param()));
        contentValues.put(CNF_HB_DAY_SCHEDULE, ENC.encrypt(data.getHb_day_schedule()));
        contentValues.put(CNF_HB_TIME_GET_MERCHANT, ENC.encrypt(data.getHb_time_get_merchant()));
        contentValues.put(CNF_HB_TIME_GET_PARAM, ENC.encrypt(data.getHb_time_get_param()));
        contentValues.put(CNF_HB_TIME_PERIODE, ENC.encrypt(data.getHb_time_periode()));
        contentValues.put(CNF_hbid, ENC.encrypt(data.getHb_id()));
        contentValues.put(CNF_MAX_HB_LOCAL, ENC.encrypt(data.getMax_hb_local()));
        contentValues.put(CNF_MAX_LISTVIEW_TRX, ENC.encrypt(data.getMax_listview_trx()));
        contentValues.put(CNF_MERCHANT_ID, ENC.encrypt(data.getMerchant_id()));
        contentValues.put(CNF_STATUS,ENC.encrypt( data.getStatus()));
        contentValues.put(CNF_SUBMERCHANT_ID, ENC.encrypt(data.getSubmerchant_id()));
        contentValues.put(CNF_DATE_REQUEST, ENC.encrypt(data.getDate_request()));

        db.insert(CONFIG_TABLE_NAME, null, contentValues);
        return true;
    }

    public ConfigModel getHBConfig() {
        ConfigModel model = new ConfigModel();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONFIG_TABLE_NAME, null );
        res.moveToFirst();

        while(res.isFirst()){
            model.setDb_id(res.getInt(res.getColumnIndex("id")));
            model.setHb_day_get_param(ENC.decrypt(res.getString(res.getColumnIndex(CNF_HB_DAY_GET_PARAM))));
            model.setHb_day_schedule(ENC.decrypt(res.getString(res.getColumnIndex(CNF_HB_DAY_SCHEDULE))));
            model.setHb_time_get_merchant(ENC.decrypt(res.getString(res.getColumnIndex(CNF_HB_TIME_GET_MERCHANT))));
            model.setHb_time_periode(Integer.parseInt(ENC.decrypt(res.getString(res.getColumnIndex(CNF_HB_TIME_PERIODE)))));
            model.setStatus(Integer.parseInt(ENC.decrypt(res.getString(res.getColumnIndex(CNF_STATUS)))));
            model.setMax_hb_local(Integer.parseInt(ENC.decrypt(res.getString(res.getColumnIndex(CNF_MAX_HB_LOCAL)))));
            model.setMax_listview_trx(Integer.parseInt(ENC.decrypt(res.getString(res.getColumnIndex(CNF_MAX_LISTVIEW_TRX)))));
            model.setSubmerchant_id(ENC.decrypt(res.getString(res.getColumnIndex(CNF_SUBMERCHANT_ID))));
            model.setHb_time_get_param(ENC.decrypt(res.getString(res.getColumnIndex(CNF_HB_TIME_GET_PARAM))));
            model.setMerchant_id(ENC.decrypt(res.getString(res.getColumnIndex(CNF_MERCHANT_ID))));
            model.setDate_request(ENC.decrypt(res.getString(res.getColumnIndex(CNF_DATE_REQUEST))));
            res.moveToNext();
        }
        res.close();
        return model;
    }

    public ArrayList<ConfigModel> getAllHBConfig() {
        ArrayList<ConfigModel> array_list = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+CONFIG_TABLE_NAME, null );
        res.moveToFirst();

        while(!res.isAfterLast()){
            ConfigModel model = new ConfigModel();
            model.setDb_id(res.getInt(res.getColumnIndex("id")));
            model.setHb_day_get_param(ENC.decrypt(res.getString(res.getColumnIndex(CNF_HB_DAY_GET_PARAM))));
            model.setHb_day_schedule(ENC.decrypt(res.getString(res.getColumnIndex(CNF_HB_DAY_SCHEDULE))));
            model.setHb_time_get_merchant(ENC.decrypt(res.getString(res.getColumnIndex(CNF_HB_TIME_GET_MERCHANT))));
            model.setHb_time_periode(Integer.parseInt(ENC.decrypt(res.getString(res.getColumnIndex(CNF_HB_TIME_PERIODE)))));
            model.setStatus(Integer.parseInt(ENC.decrypt(res.getString(res.getColumnIndex(CNF_STATUS)))));
            model.setMax_hb_local(Integer.parseInt(ENC.decrypt(res.getString(res.getColumnIndex(CNF_MAX_HB_LOCAL)))));
            model.setMax_listview_trx(Integer.parseInt(ENC.decrypt(res.getString(res.getColumnIndex(CNF_MAX_LISTVIEW_TRX)))));
            model.setSubmerchant_id(ENC.decrypt(res.getString(res.getColumnIndex(CNF_SUBMERCHANT_ID))));
            model.setHb_time_get_param(ENC.decrypt(res.getString(res.getColumnIndex(CNF_HB_TIME_GET_PARAM))));
            model.setMerchant_id(ENC.decrypt(res.getString(res.getColumnIndex(CNF_MERCHANT_ID))));
            model.setDate_request(ENC.decrypt(res.getString(res.getColumnIndex(CNF_DATE_REQUEST))));
            res.moveToNext();
            array_list.add(model);
        }
        res.close();
        return array_list;
    }
}

