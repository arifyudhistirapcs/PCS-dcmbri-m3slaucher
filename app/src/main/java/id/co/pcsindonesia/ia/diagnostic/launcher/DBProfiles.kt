package id.co.pcsindonesia.pcslauncher

import android.content.ContentValues
import android.content.Context
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import org.jetbrains.anko.db.ManagedSQLiteOpenHelper

class DBProfiles (ctx: Context)   : ManagedSQLiteOpenHelper(ctx, "profiles.db", null, 1) {
    companion object {
        private var instance: DBProfiles? = null
        val TAG: String? = "DBProfiles"
        val PROFILE_TABLE = "profile_table"

        val sn = "serial_number"
        val version = "version"
        val menu = "menu"

        @Synchronized
        fun getInstance(ctx: Context): DBProfiles {
            if (instance == null) {
                instance = DBProfiles(ctx.applicationContext)
            }
            return instance as DBProfiles
        }
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db!!.execSQL(
            "create table  " + PROFILE_TABLE +
                    " (id integer primary key , " + sn + " text, " + version + " text, " + menu + " text)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }

    fun insertProfile(model : ProfileModel){
        val db = this.writableDatabase
        val contentValues = ContentValues()

        contentValues.put(sn, GlobalHelper.getSN())
//        contentValues.put(version, ENC.encrypt(model.version))
//        contentValues.put(menu, ENC.encrypt(model.menu))
        contentValues.put(version,model.version)
        contentValues.put(menu,model.menu)

        if (getProfileCount() == 0) {
            contentValues.put("id", 1)
            val ins = db.insert(
                PROFILE_TABLE,
                null,
                contentValues
            )
            db.close()
            Log.e("DBProfileNew", "new: insertAllProfile: $ins")
        } else {
            val vals = db.update(
                PROFILE_TABLE, contentValues,
                "id = 1", null
            )
            db.close()
            Log.e("DBProfileNew", "update: insertAllProfile: $vals")
        }
    }

    fun getProfileCount(): Int {
        val db = this.readableDatabase
        return DatabaseUtils.queryNumEntries(
            db, PROFILE_TABLE
        )
            .toInt()
    }
}