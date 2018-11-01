package id.co.gets.myreport.dbhelper;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "security.db";


    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_NAME + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_USER_VENDOR + " TEXT,"+
                    FeedReaderContract.FeedEntry.COLUMN_USER_DATA + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_FB_TOKEN + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_MAYBANK_DATA + " TEXT)";

    private static final String SQL_CREATE_TABLE_IMAGE =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE2_NAME + " (" +
                    FeedReaderContract.FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_PROFILE_PICT + " BLOB)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_NAME;

    private static final String SQL_DELETE_ENTRIES_IMAGE =
            "DROP TABLE IF EXISTS" + FeedReaderContract.FeedEntry.TABLE2_NAME;

    public DbHelper(Context context){

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
        sqLiteDatabase.execSQL(SQL_CREATE_TABLE_IMAGE);

    }



    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES_IMAGE);
        onCreate(sqLiteDatabase);

    }


}


