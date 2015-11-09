package maimeng.yodian.app.client.android.chat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import com.easemob.util.HanziToPinyin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import maimeng.yodian.app.client.android.BuildConfig;
import maimeng.yodian.app.client.android.chat.Constant;
import maimeng.yodian.app.client.android.chat.domain.RobotUser;
import maimeng.yodian.app.client.android.chat.domain.User;

public class DemoDBManager {
    static private DemoDBManager dbMgr = new DemoDBManager();
    private DbOpenHelper dbHelper;

    void onInit(Context context) {
        dbHelper = DbOpenHelper.getInstance(context);
    }

    public static synchronized DemoDBManager getInstance() {
        return dbMgr;
    }

    /**
     * 获取好友list
     *
     * @return
     */
    synchronized public Map<String, User> getContactList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, User> users = new HashMap<String, User>();
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.TABLE_NAME /* + " desc" */, null);
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_ID));
                String nick = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_AVATAR));
                String wechat = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_WECHAT));
                String mobile = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_MOBILE));
                String qq = cursor.getString(cursor.getColumnIndex(UserDao.COLUMN_NAME_QQ));
                User user = new User();
                user.setUsername(username);
                user.setNick(nick);
                user.setAvatar(avatar);
                user.setWechat(wechat);
                user.setMobile(mobile);
                user.setQq(qq);
                String headerName = null;
                if (!TextUtils.isEmpty(user.getNick())) {
                    headerName = user.getNick();
                } else {
                    headerName = user.getUsername();
                }

                if (username.equals(Constant.NEW_FRIENDS_USERNAME) || username.equals(Constant.GROUP_USERNAME)
                        || username.equals(Constant.CHAT_ROOM) || username.equals(Constant.CHAT_ROBOT)) {
                    user.setHeader("");
                } else if (headerName.length() > 0 && Character.isDigit(headerName.charAt(0))) {
                    user.setHeader("#");
                } else {
                    if (headerName.length() > 0) {
                        ArrayList<HanziToPinyin.Token> lists = HanziToPinyin.getInstance().get(headerName.substring(0, 1));
                        if (lists.size() > 0) {
                            user.setHeader(lists.get(0).target.substring(0, 1).toUpperCase());
                        }

                        char header = user.getHeader().toLowerCase().charAt(0);
                        if (header < 'a' || header > 'z' || lists.size() == 0) {
                            user.setHeader("#");
                        }
                    }
                }
                users.put(username, user);
            }
            cursor.close();
        }
        return users;
    }

    /**
     * 删除一个联系人
     *
     * @param username
     */
    synchronized public void deleteContact(String username) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            db.delete(UserDao.TABLE_NAME, UserDao.COLUMN_NAME_ID + " = ?", new String[]{username});
        }
    }

    /**
     * 保存一个联系人
     *
     * @param user
     */
    synchronized public void saveOrUpdate(User user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(UserDao.TABLE_NAME, null, UserDao.COLUMN_NAME_ID + "= ?", new String[]{user.getUsername()}, null, null, null);
        ContentValues values = new ContentValues();
        if (cursor.moveToFirst()) {
            if (BuildConfig.DEBUG) Log.i("DBManager", "update Contact by " + user.getNick());
            cursor.close();
            if (user.getNick() != null)
                values.put(UserDao.COLUMN_NAME_NICK, user.getNick());
            if (user.getAvatar() != null)
                values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
            if (user.getWechat() != null)
                values.put(UserDao.COLUMN_NAME_WECHAT, user.getWechat());
            if (user.getMobile() != null)
                values.put(UserDao.ROBOT_COLUMN_NAME_MOBILE, user.getMobile());
            if (user.getQq() != null)
                values.put(UserDao.ROBOT_COLUMN_NAME_QQ, user.getQq());
            if (db.isOpen()) {
                db.update(UserDao.TABLE_NAME, values, UserDao.COLUMN_NAME_ID + "= ?", new String[]{user.getUsername()});
            }
        } else {
            if (BuildConfig.DEBUG) Log.i("DBManager", "add Contact by " + user.getNick());
            cursor.close();
            values.put(UserDao.COLUMN_NAME_ID, user.getUsername());
            if (user.getNick() != null)
                values.put(UserDao.COLUMN_NAME_NICK, user.getNick());
            if (user.getAvatar() != null)
                values.put(UserDao.COLUMN_NAME_AVATAR, user.getAvatar());
            if (user.getWechat() != null)
                values.put(UserDao.COLUMN_NAME_WECHAT, user.getWechat());
            if (user.getMobile() != null)
                values.put(UserDao.ROBOT_COLUMN_NAME_MOBILE, user.getMobile());
            if (user.getQq() != null)
                values.put(UserDao.ROBOT_COLUMN_NAME_QQ, user.getQq());
            if (db.isOpen()) {
                db.insert(UserDao.TABLE_NAME, null, values);
            }
        }
    }

    public void setDisabledGroups(List<String> groups) {
        setList(UserDao.COLUMN_NAME_DISABLED_GROUPS, groups);
    }

    public List<String> getDisabledGroups() {
        return getList(UserDao.COLUMN_NAME_DISABLED_GROUPS);
    }

    public void setDisabledIds(List<String> ids) {
        setList(UserDao.COLUMN_NAME_DISABLED_IDS, ids);
    }

    public List<String> getDisabledIds() {
        return getList(UserDao.COLUMN_NAME_DISABLED_IDS);
    }

    synchronized private void setList(String column, List<String> strList) {
        StringBuilder strBuilder = new StringBuilder();

        for (String hxid : strList) {
            strBuilder.append(hxid).append("$");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if (db.isOpen()) {
            ContentValues values = new ContentValues();
            values.put(column, strBuilder.toString());

            db.update(UserDao.PREF_TABLE_NAME, values, null, null);
        }
    }

    synchronized private List<String> getList(String column) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select " + column + " from " + UserDao.PREF_TABLE_NAME, null);
        if (!cursor.moveToFirst()) {
            cursor.close();
            return null;
        }

        String strVal = cursor.getString(0);
        if (strVal == null || strVal.equals("")) {
            return null;
        }

        cursor.close();

        String[] array = strVal.split("$");

        if (array != null && array.length > 0) {
            List<String> list = new ArrayList<String>();
            for (String str : array) {
                list.add(str);
            }

            return list;
        }

        return null;
    }


    synchronized public void closeDB() {
        if (dbHelper != null) {
            dbHelper.closeDB();
        }
    }

    /**
     * start robot list
     */
    synchronized public Map<String, RobotUser> getRobotList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Map<String, RobotUser> users = null;
        if (db.isOpen()) {
            Cursor cursor = db.rawQuery("select * from " + UserDao.ROBOT_TABLE_NAME, null);
            if (cursor.getCount() > 0) {
                users = new HashMap<String, RobotUser>();
            }
            ;
            while (cursor.moveToNext()) {
                String username = cursor.getString(cursor.getColumnIndex(UserDao.ROBOT_COLUMN_NAME_ID));
                String nick = cursor.getString(cursor.getColumnIndex(UserDao.ROBOT_COLUMN_NAME_NICK));
                String avatar = cursor.getString(cursor.getColumnIndex(UserDao.ROBOT_COLUMN_NAME_AVATAR));
                String wechat = cursor.getString(cursor.getColumnIndex(UserDao.ROBOT_COLUMN_NAME_WECHAT));
                String mobile = cursor.getString(cursor.getColumnIndex(UserDao.ROBOT_COLUMN_NAME_MOBILE));
                String qq = cursor.getString(cursor.getColumnIndex(UserDao.ROBOT_COLUMN_NAME_QQ));
                RobotUser user = new RobotUser();
                user.setUsername(username);
                user.setNick(nick);
                user.setAvatar(avatar);
                user.setWechat(wechat);
                user.setMobile(mobile);
                user.setQq(qq);
                String headerName = null;
                if (!TextUtils.isEmpty(user.getNick())) {
                    headerName = user.getNick();
                } else {
                    headerName = user.getUsername();
                }
                if (Character.isDigit(headerName.charAt(0))) {
                    user.setHeader("#");
                } else {
                    user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target
                            .substring(0, 1).toUpperCase());
                    char header = user.getHeader().toLowerCase().charAt(0);
                    if (header < 'a' || header > 'z') {
                        user.setHeader("#");
                    }
                }

                users.put(username, user);
            }
            cursor.close();
        }
        return users;
    }


    synchronized public void saveOrUpdate(RobotUser user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor cursor = db.query(UserDao.ROBOT_TABLE_NAME, null, UserDao.ROBOT_COLUMN_NAME_ID + "=  ?", new String[]{user.getUsername()}, null, null, null);
        if (cursor.moveToFirst()) {
            if (BuildConfig.DEBUG) Log.i("DBManager", "update RobotUser by " + user.getNick());
            cursor.close();
            ContentValues values = new ContentValues();
            if (user.getNick() != null)
                values.put(UserDao.ROBOT_COLUMN_NAME_NICK, user.getNick());
            if (user.getAvatar() != null)
                values.put(UserDao.ROBOT_COLUMN_NAME_AVATAR, user.getAvatar());
            if (user.getWechat() != null)
                values.put(UserDao.ROBOT_COLUMN_NAME_WECHAT, user.getWechat());
            if (user.getMobile() != null)
                values.put(UserDao.ROBOT_COLUMN_NAME_MOBILE, user.getMobile());
            if (user.getQq() != null)
                values.put(UserDao.ROBOT_COLUMN_NAME_QQ, user.getQq());
            if (db.isOpen()) {
                db.update(UserDao.ROBOT_TABLE_NAME, values, UserDao.ROBOT_COLUMN_NAME_ID + "=  ?", new String[]{user.getUsername()});
            }
        } else {
            if (BuildConfig.DEBUG) Log.i("DBManager", "add RobotUser by " + user.getNick());
            cursor.close();
            ContentValues values = new ContentValues();
            values.put(UserDao.ROBOT_COLUMN_NAME_ID, user.getUsername());
            if (user.getNick() != null)
                values.put(UserDao.ROBOT_COLUMN_NAME_NICK, user.getNick());
            if (user.getAvatar() != null)
                values.put(UserDao.ROBOT_COLUMN_NAME_AVATAR, user.getAvatar());
            if (user.getWechat() != null)
                values.put(UserDao.ROBOT_COLUMN_NAME_WECHAT, user.getWechat());
            if (user.getMobile() != null)
                values.put(UserDao.ROBOT_COLUMN_NAME_MOBILE, user.getMobile());
            if (user.getQq() != null)
                values.put(UserDao.ROBOT_COLUMN_NAME_QQ, user.getQq());
            if (db.isOpen()) {
                db.insert(UserDao.ROBOT_TABLE_NAME, null, values);
            }
        }
    }
}