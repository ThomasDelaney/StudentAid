package com.example.teohe.studentaid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by Thomas on 15/11/2017.
 */

public class DatabaseManager
{
    //profile table column strings
    private static final String KEY_PROFILE_ID = "_id";
    private static final String KEY_PROFILE_FIRST_NAME = "firstName";
    private static final String KEY_PROFILE_LAST_NAME = "lastName";
    private static final String KEY_PROFILE_COLLEGE_NAME = "collegeName";
    private static final String KEY_PROFILE_COURSE_TITLE = "courseTitle";
    private static final String KEY_PROFILE_IMAGE = "image";

    //module table column strings
    private static final String KEY_MODULE_NAME = "moduleName";
    private static final String KEY_MODULE_WORTH = "moduleWorth";

    //notification table column strings
    private static final String KEY_NOTIFICATION_DESCRIPTION = "notificationDescription";
    private static final String KEY_NOTIFICATION_DATE = "notificationDate";

    //mark table column strings
    private static final String KEY_MARK_NAME = "markName";
    private static final String KEY_MARK_WORTH = "markWorth";
    private static final String KEY_MARK_SCORE = "markScore";

    //timeslot table column strings
    private static final String KEY_TIMESLOT_ID = "_id";
    private static final String KEY_TIMESLOT_CLASS_TYPE = "classType";
    private static final String KEY_LECTURER_NAME = "lecturerName";
    private static final String KEY_TIMESLOT_ROOM = "room";
    private static final String KEY_TIMESLOT_DAY_OF_THE_WEEK = "dayOfTheWeek";
    private static final String KEY_TIMESLOT_SLOT = "slot";

    //table name strings
    private static final String DATABASE_PROFILE_TABLE = "Profile";
    private static final String DATABASE_MODULE_TABLE = "Module";
    private static final String DATABASE_MARK_TABLE = "Mark";
    private static final String DATABASE_NOTIFICATION_TABLE = "Notification";
    private static final String DATABASE_TIMESLOT_TABLE = "Timeslot";

    private static final String DATABASE_NAME = "StudentAid";
    private static final int DATABASE_VERSION = 1; // since it is the first version of the dB


    // SQL statements to create the database
    private static final String DATABASE_CREATE_PROFILE_TABLE =
            "create table " + DATABASE_PROFILE_TABLE + " (" + KEY_PROFILE_ID + " integer primary key, " + KEY_PROFILE_FIRST_NAME + " text not null, "
                    + KEY_PROFILE_LAST_NAME + " text not null, " + KEY_PROFILE_COLLEGE_NAME +" text not null, "
                    + KEY_PROFILE_COURSE_TITLE + " text not null, " + KEY_PROFILE_IMAGE + " text not null);";

    private static final String DATABASE_CREATE_MODULE_TABLE =
            "create table " + DATABASE_MODULE_TABLE + " (" + KEY_MODULE_NAME + " text primary key not null, " + KEY_MODULE_WORTH + " integer not null);";

    private static final String DATABASE_CREATE_NOTIFICATION_TABLE =
            "create table " + DATABASE_NOTIFICATION_TABLE + " (" + KEY_NOTIFICATION_DESCRIPTION + " text primary key not null, " + KEY_NOTIFICATION_DATE + " text not null);";

    private static final String DATABASE_CREATE_MARK_TABLE =
            "create table " + DATABASE_MARK_TABLE + " (" + KEY_MARK_NAME + " text primary key not null, " + KEY_MARK_WORTH + " real not null, "
                    + KEY_MARK_SCORE + " real not null, " + KEY_MODULE_NAME + " text not null, FOREIGN KEY ("+KEY_MODULE_NAME+") REFERENCES " +
                    DATABASE_MODULE_TABLE+" ("+KEY_MODULE_NAME+"));";

    private static final String DATABASE_CREATE_TIMESLOT_TABLE =
            "create table " + DATABASE_TIMESLOT_TABLE + " (" + KEY_TIMESLOT_ID + " integer primary key autoincrement, " + KEY_TIMESLOT_CLASS_TYPE + " text not null, "
                    + KEY_LECTURER_NAME + " text not null, " + KEY_TIMESLOT_ROOM + " text not null, " + KEY_TIMESLOT_DAY_OF_THE_WEEK + " integer not null, " +
                    KEY_TIMESLOT_SLOT + " integer not null, " + KEY_MODULE_NAME + " text not null, FOREIGN KEY ("+KEY_MODULE_NAME+") REFERENCES " +
                    DATABASE_MODULE_TABLE+" ("+KEY_MODULE_NAME+"));";


    private final Context context;
    private DatabaseHelper DBHelper;
    private SQLiteDatabase db;

    public DatabaseManager(Context ctx)
    {
        //
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    public SQLiteDatabase open() throws SQLException
    {
        db = DBHelper.getWritableDatabase();
        return db;
    }

    // nested dB helper class
    private static class DatabaseHelper extends SQLiteOpenHelper {
        //
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        //
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(DATABASE_CREATE_PROFILE_TABLE);
            db.execSQL(DATABASE_CREATE_MODULE_TABLE);
            db.execSQL(DATABASE_CREATE_NOTIFICATION_TABLE);
            db.execSQL(DATABASE_CREATE_MARK_TABLE);
            db.execSQL(DATABASE_CREATE_TIMESLOT_TABLE);
        }

        @Override
        //
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            // dB structure  change..

        }
    }   // end nested class

    // remainder of the Database Example methods to "use" the database
    public void close()
    {
        DBHelper.close();

    }

    //get user that is logged in, if not logged in return null Profile
    public Profile getProfile() throws SQLException
    {
        Profile profile = null;

        try
        {
            //since user only created once, we know the id must be 1,  if this was cloud solution it would be found based on a username and password
            Cursor userCursor = db.query(true, DATABASE_PROFILE_TABLE, new String[]
                            {
                                    // this String array is the 2nd parameter to the query method - and is the list of columns you want to return
                                    KEY_PROFILE_FIRST_NAME,
                                    KEY_PROFILE_LAST_NAME,
                                    KEY_PROFILE_COLLEGE_NAME,
                                    KEY_PROFILE_COURSE_TITLE,
                                    KEY_PROFILE_IMAGE

                            },
                    "_id = 1", null, null, null, null, null);

            if (userCursor.moveToFirst())
            {

                //create File object from the image path that was stored in the database
                Bitmap profileImage;
                String uriString = userCursor.getString(4);

                Log.e("help", uriString);

                Uri imageUri = Uri.parse(uriString);

                InputStream input = null;
                try
                {
                    input = context.getContentResolver().openInputStream(imageUri);
                    Log.e("Real shit?", input.toString());
                }
                catch (FileNotFoundException e)
                {
                    Log.e("hmm", "HMMMMMMMMMMMM");
                }

                profileImage = BitmapFactory.decodeStream(input);
                /*File imageFile = new File(getRealPathFromURI(context, imageUri));

                if (imageFile.exists())
                {
                    profileImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
                }
                else
                {
                    profileImage = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
                }*/

                profile = new Profile(userCursor.getString(0), userCursor.getString(1), userCursor.getString(2), userCursor.getString(3), profileImage);
            }
            userCursor.close();
        }
        catch (SQLException e)
        {
            Log.e("SQLException", e.getMessage());
        }

        return profile;
    }

    public long setProfile(String firstName, String lastName, String collegeName, String courseTitle, String imagePath) throws SQLException
    {
        long returnValue = -1;

        try
        {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_PROFILE_ID, 1);
            initialValues.put(KEY_PROFILE_FIRST_NAME, firstName);
            initialValues.put(KEY_PROFILE_LAST_NAME, lastName);
            initialValues.put(KEY_PROFILE_COLLEGE_NAME, collegeName);
            initialValues.put(KEY_PROFILE_COURSE_TITLE, courseTitle);
            initialValues.put(KEY_PROFILE_IMAGE, imagePath);

            returnValue = db.insert(DATABASE_PROFILE_TABLE, null, initialValues);
        }
        catch (SQLException e)
        {
            Log.e("SQLException", e.getMessage());
        }

        return returnValue;
    }

    public long insertModule(String moduleName, int worth) throws SQLException
    {
        long returnValue = -1;

        try
        {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_MODULE_NAME, moduleName);
            initialValues.put(KEY_MODULE_WORTH, worth);

            returnValue = db.insert(DATABASE_MODULE_TABLE, null, initialValues);
        }
        catch (SQLException e)
        {
            Log.e("SQLException", e.getMessage());
        }

        return returnValue;
    }

    public long updateModule(String originalModuleName, String newModuleName, int worth) throws SQLException
    {
        long returnValue = -1;
        String moduleUpdate = "moduleName = "+"'"+originalModuleName+"'";
        try
        {
            ContentValues initialValues = new ContentValues();
            initialValues.put(KEY_MODULE_NAME, newModuleName);
            initialValues.put(KEY_MODULE_WORTH, worth);

            returnValue = db.update(DATABASE_MODULE_TABLE, initialValues, moduleUpdate, null);
        }
        catch (SQLException e)
        {
            Log.e("SQLException", e.getMessage());
        }

        return returnValue;
    }

    public long deleteModule(String moduleName) throws SQLException
    {
        long returnValue = -1;
        String moduleDelete = "moduleName = "+"'"+moduleName+"'";

        try
        {
            returnValue = db.delete(DATABASE_MODULE_TABLE, moduleDelete, new String[]{});
        }
        catch (SQLException e)
        {
            Log.e("SQLException", e.getMessage());
        }

        return returnValue;
    }

    public Cursor getModules()
    {
        return db.query(true, DATABASE_MODULE_TABLE, new String[]
                        {
                                KEY_MODULE_NAME,
                                KEY_MODULE_WORTH
                        },
                 null,  null, null, null, null, null);
    }

    public Cursor getModule(String moduleName)
    {
        String moduleSelect = "moduleName = "+"'"+moduleName+"'";

        return db.query(true, DATABASE_MODULE_TABLE, new String[]
                        {
                                KEY_MODULE_NAME,
                                KEY_MODULE_WORTH
                        },
                moduleSelect,  null, null, null, null, null);
    }

    // Reference: algorithm for getting file path from URI
    //https://stackoverflow.com/questions/3401579/get-filename-and-path-from-uri-from-mediastore
    public String getRealPathFromURI(Context context, Uri contentUri)
    {
        Cursor cursor = null;
        try
        {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        finally
        {
            if (cursor != null)
            {
                cursor.close();
            }
        }
    }
    // Reference complete
}
