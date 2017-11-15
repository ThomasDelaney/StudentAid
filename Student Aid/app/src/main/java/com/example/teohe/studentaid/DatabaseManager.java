package com.example.teohe.studentaid;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

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
    private static final String KEY_MARK_SCORE = "moduleScore";

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
            "create table " + DATABASE_PROFILE_TABLE + " (" + KEY_PROFILE_ID + " integer primary key autoincrement, " + KEY_PROFILE_FIRST_NAME + " text not null, "
                    + KEY_PROFILE_LAST_NAME + " text not null, " + KEY_PROFILE_COLLEGE_NAME +" text not null, "
                    + KEY_PROFILE_COURSE_TITLE + " text not null, " + KEY_PROFILE_IMAGE + "text not null);";

    private static final String DATABASE_CREATE_MODULE_TABLE =
            "create table " + DATABASE_MODULE_TABLE + " (" + KEY_MODULE_NAME + " text primary key not null, " + KEY_MODULE_WORTH + " integer not null);";

    private static final String DATABASE_CREATE_NOTIFICATION_TABLE =
            "create table " + DATABASE_NOTIFICATION_TABLE + " (" + KEY_NOTIFICATION_DESCRIPTION + " text primary key not null, " + KEY_NOTIFICATION_DATE + " text not null);";

    private static final String DATABASE_CREATE_MARK_TABLE =
            "create table " + DATABASE_MARK_TABLE + " (" + KEY_MARK_NAME + " text primary key not null, " + KEY_MARK_WORTH + " real not null, "
                    + KEY_MARK_SCORE + " real not null, " + KEY_MODULE_NAME + "text not null, FOREIGN KEY ("+KEY_MODULE_NAME+") REFERENCES " +
                    DATABASE_MODULE_TABLE+" ("+KEY_MODULE_NAME+"));";

    private static final String DATABASE_CREATE_TIMESLOT_TABLE =
            "create table " + DATABASE_TIMESLOT_TABLE + " (" + KEY_TIMESLOT_ID + " integer primary key autoincrement, " + KEY_TIMESLOT_CLASS_TYPE + " text not null, "
                    + KEY_LECTURER_NAME + " text not null, " + KEY_TIMESLOT_ROOM + " text not null, " + KEY_TIMESLOT_DAY_OF_THE_WEEK + " integer not null, " +
                    KEY_TIMESLOT_SLOT + " integer not null, " + KEY_MODULE_NAME + "text not null, FOREIGN KEY ("+KEY_MODULE_NAME+") REFERENCES " +
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

    public SQLiteDatabase open() throws SQLException {
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
    public Profile getUser()
    {
        //since user only created once, we know the id must be 1,  if this was cloud solution it would be found based on a username and password
        Cursor userCursor =  db.query(true, DATABASE_PROFILE_TABLE, new String[]
                        {
                                // this String array is the 2nd paramter to the query method - and is the list of columns you want to return
                                KEY_PROFILE_ID,
                                KEY_PROFILE_FIRST_NAME,
                                KEY_PROFILE_LAST_NAME,
                                KEY_PROFILE_COLLEGE_NAME,
                                KEY_PROFILE_COURSE_TITLE,
                                KEY_PROFILE_IMAGE

                        },
                "_id = 1", null, null, null, null, null);

        Profile profile = null;

        if (userCursor.moveToFirst())
        {
            //create File object from the image path that was stored in the database
            File file = new  File(userCursor.getString(6));
            Bitmap profileImage;

            //if the file exists, create a bitmap from that file, else, use the default image (in case user moves or deletes file)
            if(file.exists())
            {

                profileImage = BitmapFactory.decodeFile(file.getAbsolutePath());

            }
            else
            {
                profileImage = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
            }

            profile = new Profile(userCursor.getString(2), userCursor.getString(3), userCursor.getString(4), userCursor.getString(5), profileImage);
        }

        return profile;
    }

    public long setUser(String firstName, String lastName, String collegeName, String courseTitle, String imagePath)
    {
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_PROFILE_ID, 1);
        initialValues.put(KEY_PROFILE_FIRST_NAME, firstName);
        initialValues.put(KEY_PROFILE_LAST_NAME, lastName);
        initialValues.put(KEY_PROFILE_COLLEGE_NAME, collegeName);
        initialValues.put(KEY_PROFILE_COURSE_TITLE, courseTitle);
        initialValues.put(KEY_PROFILE_IMAGE, imagePath);

        return db.insert(DATABASE_CREATE_PROFILE_TABLE, null, initialValues);
    }
}
