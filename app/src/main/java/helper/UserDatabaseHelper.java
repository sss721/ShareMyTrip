package helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import model.User;

/**
 * Created by Shweta on 4/7/2016.
 */
public class UserDatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "SHARERIDE_APP_DB";

    private static final String USER_DATABASE_NAME = "USER";
    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_USER_FIRSTNAME = "uName";
    public static final String COLUMN_USER_USERNAME = "uUserName";
    public static final String COLUMN_USER_PASSWORD = "uPass";
    public static final String COLUMN_USER_ADDRESS = "uAddress";
    public static final String COLUMN_USER_SEX = "uSex";
    public static final String COLUMN_USER_LASTNAME = "uLast";
    public static final String COLUMN_USER_PHONE = "uPhone";
    public static final String COLUMN_USER_ABOUTME = "uAboutMe";

    private static final String DROP_TABLE_QUERY = "DROP TABLE IF EXISTS ";

    private static final String USER_DATABASE_CREATE =
            "CREATE TABLE " + USER_DATABASE_NAME + " ( " +
                    COLUMN_USER_ID + " integer primary key autoincrement, " +
                    COLUMN_USER_FIRSTNAME + " varchar(25), " +
                    COLUMN_USER_LASTNAME + " varchar(25), " +
                    COLUMN_USER_USERNAME + " text, " +
                    COLUMN_USER_PASSWORD+ " varchar(25), " +
                    COLUMN_USER_ADDRESS+ " date, " +
                    COLUMN_USER_SEX + " varchar(25), " +
                    COLUMN_USER_PHONE + " varchar(25) " +
                    COLUMN_USER_ABOUTME + " varchar(25), " +" )";

    private static final String FETCH_USERS_QUERY = "SELECT * FROM " + USER_DATABASE_NAME;


    public UserDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context,DATABASE_NAME , factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(DROP_TABLE_QUERY + USER_DATABASE_NAME);
        onCreate(sqLiteDatabase);

    }

    public long insertUser(User user) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_FIRSTNAME, user.getFirstName());
        values.put(COLUMN_USER_LASTNAME, user.getLastName());
        Log.d("User Name: ", user.getFirstName());
        values.put(COLUMN_USER_USERNAME, user.getUserName());
        values.put(COLUMN_USER_PASSWORD, user.getUserPassword());
        values.put(COLUMN_USER_ADDRESS, user.getUserAddress());
        values.put(COLUMN_USER_SEX, user.getSex());
        values.put(COLUMN_USER_PHONE, user.getPhoneNumber());
        values.put(COLUMN_USER_ABOUTME, user.getAboutMe());

        return getWritableDatabase().insert(USER_DATABASE_NAME, null, values);
    }

}
