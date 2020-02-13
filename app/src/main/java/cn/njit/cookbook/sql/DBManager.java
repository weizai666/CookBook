package cn.njit.cookbook.sql;

import android.content.Context;


import cn.njit.cookbook.App;

import org.greenrobot.greendao.database.Database;



public class DBManager
{
    private static DBManager mManager;
    private DaoSession mSession;


    private DBManager()
    {

    }

    public static DBManager getManager()
    {
        if (mManager == null)
        {
            synchronized (DBManager.class)
            {
                if (mManager == null)
                {
                    mManager = new DBManager();
                }
            }
        }
        return mManager;
    }

    public DaoSession init(Context context)
    {
        if (mSession == null)
        {
            String fileName = context.getPackageName();
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, fileName + "-db", null);
            DaoMaster master = new DaoMaster(helper.getWritableDatabase());
            mSession = master.newSession();
        }
        return mSession;
    }




}
