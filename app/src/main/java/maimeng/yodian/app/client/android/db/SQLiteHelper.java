package maimeng.yodian.app.client.android.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

import maimeng.yodian.app.client.android.model.SkillTemplate;

/**
 * Created by android on 15-7-20.
 */
public class SQLiteHelper extends OrmLiteSqliteOpenHelper {
    private static SQLiteHelper instance;
    private static final String TABLE_NAME = "yodian.db";
    private Dao<SkillTemplate, Integer> dao;

    public SQLiteHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void close() {
        super.close();
        dao=null;
    }

    /**
     * 单例获取该Helper
     *
     * @param context
     * @return
     */
    public static synchronized SQLiteHelper getHelper(Context context) {
        if (instance == null) {
            synchronized (SQLiteHelper.class) {
                if (instance == null)
                    instance = new SQLiteHelper(context);
            }
        }

        return instance;
    }
    @Override
    public void onCreate(SQLiteDatabase database,
                         ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, SkillTemplate.class);
        } catch (java.sql.SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase database,
                          ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, SkillTemplate.class, true);
        } catch (java.sql.SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        onCreate(database, connectionSource);

    }
    public Dao<SkillTemplate, Integer> getDao() throws SQLException
    {
        if (dao == null)
        {
            dao = getDao(SkillTemplate.class);
        }
        return dao;
    }
}
