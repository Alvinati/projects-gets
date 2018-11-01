package id.co.mine.myreportadmin.dbhelper;


import android.provider.BaseColumns;


public final class FeedReaderContract {


    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String TABLE2_NAME = "entry2";
        public static final String COLUMN_MAYBANK_DATA = "maybank";
        public static final String COLUMN_PROFILE_PICT = "profile";
    }


}


