/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package maimeng.yodian.app.client.android.chat.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.easemob.applib.controller.HXSDKHelper;

import maimeng.yodian.app.client.android.R;
import maimeng.yodian.app.client.android.utils.LogUtil;

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 7;
    private static final String LOG_TAG = DbOpenHelper.class.getSimpleName();
    private static DbOpenHelper instance;

    private static final String USERNAME_TABLE_CREATE = "CREATE TABLE "
            + UserDao.TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_NICK + " TEXT, "
            + UserDao.COLUMN_NAME_AVATAR + " TEXT, "
            + UserDao.COLUMN_NAME_WECHAT + " TEXT, "
            + UserDao.COLUMN_NAME_ID + " TEXT PRIMARY KEY);";

    private static final String ROBOT_TABLE_CREATE = "CREATE TABLE "
            + UserDao.ROBOT_TABLE_NAME + " ("
            + UserDao.ROBOT_COLUMN_NAME_ID + " TEXT PRIMARY KEY, "
            + UserDao.ROBOT_COLUMN_NAME_NICK + " TEXT, "
            + UserDao.ROBOT_COLUMN_NAME_WECHAT + " TEXT, "
            + UserDao.ROBOT_COLUMN_NAME_AVATAR + " TEXT);";

    private static final String CREATE_PREF_TABLE = "CREATE TABLE "
            + UserDao.PREF_TABLE_NAME + " ("
            + UserDao.COLUMN_NAME_DISABLED_GROUPS + " TEXT, "
            + UserDao.COLUMN_NAME_DISABLED_IDS + " TEXT);";
    private final Context mContext;

    private DbOpenHelper(Context context) {
        super(context, getUserDatabaseName(), null, DATABASE_VERSION);
        this.mContext = context;

    }

    public static DbOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DbOpenHelper(context);
        }
        return instance;
    }

    private static String getUserDatabaseName() {
        return HXSDKHelper.getInstance().getHXId() + "_demo.db";
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(USERNAME_TABLE_CREATE);
        db.execSQL(CREATE_PREF_TABLE);
        db.execSQL(ROBOT_TABLE_CREATE);
        db.execSQL("ALTER TABLE " + UserDao.TABLE_NAME + " ADD COLUMN " +
                UserDao.COLUMN_NAME_MOBILE + " TEXT ;");
        db.execSQL("ALTER TABLE " + UserDao.TABLE_NAME + " ADD COLUMN " +
                UserDao.COLUMN_NAME_QQ + " TEXT ;");
        db.execSQL("ALTER TABLE " + UserDao.ROBOT_TABLE_NAME + " ADD COLUMN " +
                UserDao.ROBOT_COLUMN_NAME_MOBILE + " TEXT;");
        db.execSQL("ALTER TABLE " + UserDao.ROBOT_TABLE_NAME + " ADD COLUMN " +
                UserDao.ROBOT_COLUMN_NAME_QQ + " TEXT ;");
//        updateVersionTo6(db);
        updateVersionTo7(db);
    }

    private void updateVersionTo6(SQLiteDatabase db) {
        db.beginTransaction();
        LogUtil.i(LOG_TAG, "updateVersionTo6 Begin");
        try {
            String avatar = "android.resource://" + mContext.getPackageName() + "/mipmap/icon_app";
            db.execSQL(String.format("INSERT INTO %s (%s,%s,%s) VALUES('%s','%s','%s')", UserDao.TABLE_NAME, UserDao.COLUMN_NAME_ID, UserDao.COLUMN_NAME_NICK, UserDao.COLUMN_NAME_AVATAR, "hx_admin", "官方君", avatar));
            db.execSQL(String.format("INSERT INTO %s (%s,%s,%s) VALUES('%s','%s','%s')", UserDao.ROBOT_TABLE_NAME, UserDao.ROBOT_COLUMN_NAME_ID, UserDao.ROBOT_COLUMN_NAME_NICK, UserDao.ROBOT_COLUMN_NAME_AVATAR, "hx_admin", "官方君", avatar));
            db.setTransactionSuccessful();
            LogUtil.i(LOG_TAG, "updateVersionTo6 Success");
        } finally {
            LogUtil.i(LOG_TAG, "updateVersionTo6 End");
            db.endTransaction();
        }
    }

    private void updateVersionTo7(SQLiteDatabase db) {
        db.beginTransaction();
        LogUtil.i(LOG_TAG, "updateVersionTo7 Begin");
        try {
            LogUtil.i(LOG_TAG, "updateVersionTo7 old data");
            db.execSQL(String.format("delete from %s where %s = 'hx_admin'", UserDao.TABLE_NAME, UserDao.COLUMN_NAME_ID));
            db.execSQL(String.format("delete from %s where %s = 'hx_admin'", UserDao.ROBOT_TABLE_NAME, UserDao.ROBOT_COLUMN_NAME_ID));
            String avatar = "res://" + mContext.getPackageName() + "/" + R.mipmap.icon_app;
            db.execSQL(String.format("INSERT INTO %s (%s,%s,%s) VALUES('%s','%s','%s')", UserDao.TABLE_NAME, UserDao.COLUMN_NAME_ID, UserDao.COLUMN_NAME_NICK, UserDao.COLUMN_NAME_AVATAR, "hx_admin", "官方君", avatar));
            db.execSQL(String.format("INSERT INTO %s (%s,%s,%s) VALUES('%s','%s','%s')", UserDao.ROBOT_TABLE_NAME, UserDao.ROBOT_COLUMN_NAME_ID, UserDao.ROBOT_COLUMN_NAME_NICK, UserDao.ROBOT_COLUMN_NAME_AVATAR, "hx_admin", "官方君", avatar));
            db.setTransactionSuccessful();
            LogUtil.i(LOG_TAG, "updateVersionTo7 Success");
        } finally {
            LogUtil.i(LOG_TAG, "updateVersionTo7 End");
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + UserDao.TABLE_NAME + " ADD COLUMN " +
                    UserDao.COLUMN_NAME_AVATAR + " TEXT ;");
            oldVersion++;
        }
        if (oldVersion < 3) {
            db.execSQL(CREATE_PREF_TABLE);
            oldVersion++;
        }
        if (oldVersion < 4) {
            oldVersion++;
            db.execSQL(ROBOT_TABLE_CREATE);
        }
        if (oldVersion < 5) {
            LogUtil.i(LOG_TAG, "updateVersionTo5 begin");
            db.execSQL("ALTER TABLE " + UserDao.TABLE_NAME + " ADD COLUMN " +
                    UserDao.COLUMN_NAME_MOBILE + " TEXT ;");
            db.execSQL("ALTER TABLE " + UserDao.TABLE_NAME + " ADD COLUMN " +
                    UserDao.COLUMN_NAME_QQ + " TEXT ;");
            db.execSQL("ALTER TABLE " + UserDao.ROBOT_TABLE_NAME + " ADD COLUMN " +
                    UserDao.ROBOT_COLUMN_NAME_MOBILE + " TEXT;");
            db.execSQL("ALTER TABLE " + UserDao.ROBOT_TABLE_NAME + " ADD COLUMN " +
                    UserDao.ROBOT_COLUMN_NAME_QQ + " TEXT ;");
            LogUtil.i(LOG_TAG, "updateVersionTo5 End");
            oldVersion++;
        }
        if (oldVersion < 6) {
            updateVersionTo6(db);
            oldVersion++;
        }
        if (oldVersion < 7) {
            updateVersionTo7(db);
            oldVersion++;
        }
    }

    public void closeDB() {
        if (instance != null) {
            try {
                SQLiteDatabase db = instance.getWritableDatabase();
                db.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            instance = null;
        }
    }

}
