package id.gets.bookingservice;


import android.provider.BaseColumns;


public final class FeedReaderContract {


    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String COLUMN_USER = "user";
        public static final String COLUMN_PROMO = "promo";
    }


}


