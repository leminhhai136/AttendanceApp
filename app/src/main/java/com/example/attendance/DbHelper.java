package com.example.attendance;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DbHelper extends SQLiteOpenHelper {
    private static final int VERSION = 4;
    //class table
    private static final String CLASS_TABLE_NAME="CLASS_TABLE";
    public static final String C_ID="_CID";
    public static final String CLASS_NAME_KEY="CLASS_NAME";
    public static final String SUBJECT_NAME_KEY="SUBJECT_NAME";
    public static final String USERS_TABLE_NAME = "Users";
    public static final String ID = "id";
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    private static final String CREATE_USER_TABLE = "CREATE TABLE " +
            USERS_TABLE_NAME + " (" +
            ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            USERNAME + " TEXT, " +
            PASSWORD + " TEXT)";
    private static final String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + USERS_TABLE_NAME;

    private static final String CREATE_CLASS_TABLE="CREATE TABLE " +CLASS_TABLE_NAME+ "(" + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + CLASS_NAME_KEY + " TEXT NOT NULL, " +
            SUBJECT_NAME_KEY+" TEXT NOT NULL, "+
            "UNIQUE (" + CLASS_NAME_KEY +" ," + SUBJECT_NAME_KEY + ")" + ");";


    private static final String DROP_CLASS_TABLE="DROP TABLE IF EXISTS "+CLASS_TABLE_NAME;
    private static final String SELECT_CLASS_TABLE="SELECT * FROM "+CLASS_TABLE_NAME;
    private static final String SELECT_USER_TABLE="SELECT * FROM "+USERS_TABLE_NAME;
    //student table
    private static final String STUDENT_TABLE_NAME="STUDENT_TABLE";
    public static final String S_ID="_SID";
    public static final String STUDENT_NAME_KEY="STUDENT_NAME";
    public static final String STUDENT_ROLL_KEY="ROLL";

    private static final String CREATE_STUDENT_TABLE=
            "CREATE TABLE "+ STUDENT_TABLE_NAME +
                    "( "+
                    S_ID+ " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
                    C_ID+ " INTEGER NOT NULL, "+
                    STUDENT_NAME_KEY+ " TEXT NOT NULL, "+
                    STUDENT_ROLL_KEY+ " INTEGER, "+
                    " FOREIGN KEY ( "+C_ID+ ") REFERENCES "+ CLASS_TABLE_NAME +"(" +C_ID+ ")"+
                    ");";

    private static final  String DROP_STUDENT_TABLE = "DROP TABLE IF EXISTS " + STUDENT_TABLE_NAME;
    private static final  String SELECT_STUDENT_TABLE = "SELECT * FROM " + STUDENT_TABLE_NAME;

    //table status
    private static final String STATUS_TABLE_NAME="STATUS_TABLE";
    public static final String STATUS_ID="STATUS_ID";
    public static final String DATE_KEY="STATUS_DATE";
    public static final String STATUS_KEY="STATUS";


    private static final String CREATE_STATUS_TABLE=
            "CREATE TABLE "+STATUS_TABLE_NAME +
                    "("+
                    STATUS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "+
                    S_ID + " INTEGER NOT NULL, "+
                    C_ID + " INTEGER NOT NULL, "+
                    DATE_KEY+ " DATE NOT NULL, "+
                    STATUS_KEY + " TEXT NOT NULL, "+
                    " UNIQUE ("+ S_ID + " , "+ DATE_KEY+ "),"+
                    " FOREIGN KEY (" + S_ID+ ") REFERENCES "+ STUDENT_TABLE_NAME+ "( "+S_ID+ ")," +
                    " FOREIGN KEY (" + C_ID+ ") REFERENCES "+ CLASS_TABLE_NAME+ "( "+C_ID+ ")"+
                    ");";

    private static final String DROP_STATUS_TABLE = "DROP TABLE IF EXISTS "+ STATUS_TABLE_NAME;
    private static final String SELECT_STATUS_TABLE = " SELECT * FROM "+STATUS_TABLE_NAME;
    public DbHelper(@Nullable Context context) {
        super(context, "Attendance.db", null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CLASS_TABLE);
        db.execSQL(CREATE_STUDENT_TABLE);
        db.execSQL(CREATE_STATUS_TABLE);
        db.execSQL(CREATE_USER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        try {
            db.execSQL(DROP_CLASS_TABLE);
            db.execSQL(DROP_STUDENT_TABLE);
            db.execSQL(DROP_STATUS_TABLE);
            db.execSQL(DROP_USER_TABLE);
            onCreate(db);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public long addUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME, username);
        values.put(PASSWORD, password);
        long userId = db.insert(USERS_TABLE_NAME, null, values);
        db.close();
        return userId;
    }
    public boolean isUsernameExists(String username) {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + USERS_TABLE_NAME + " WHERE " + USERNAME + " = ?";
        Cursor cursor = database.rawQuery(query, new String[]{username});

        if (cursor != null && cursor.moveToFirst()) {
            cursor.close();
            return true;
        } else {
            return false;
        }
    }
    public boolean authenticateUser(String username, String password) {
        SQLiteDatabase database = this.getReadableDatabase();
        String query = "SELECT * FROM " + USERS_TABLE_NAME + " WHERE " + USERNAME + " = ? AND " + PASSWORD + " = ?";
        Cursor cursor = database.rawQuery(query, new String[]{username, password});

        boolean authenticated = cursor.getCount() > 0;
        cursor.close();
        return authenticated;
    }
    public ArrayList<User> getAllUsers() {
        ArrayList<User> userList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + USERS_TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                User user = new User();
                user.setId(cursor.getInt(cursor.getColumnIndex(ID)));
                user.setUsername(cursor.getString(cursor.getColumnIndex(USERNAME)));
                user.setPassword(cursor.getString(cursor.getColumnIndex(PASSWORD)));
                userList.add(user);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return userList;
    }



    public long updateUser(long userId, String newUsername, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(USERNAME, newUsername);
        values.put(PASSWORD, newPassword);
        long result = db.update(USERS_TABLE_NAME, values, ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return result;
    }
    Cursor getUserTable(){
        SQLiteDatabase database= this.getReadableDatabase();
        return database.rawQuery(SELECT_USER_TABLE,null);
    }

    public long deleteUser(long userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        long result = db.delete(USERS_TABLE_NAME, ID + " = ?", new String[]{String.valueOf(userId)});
        db.close();
        return result;
    }
    public boolean checkCurrentPassword(String username, String currentPassword) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT " + DbHelper.PASSWORD + " FROM " + DbHelper.USERS_TABLE_NAME +
                " WHERE " + DbHelper.USERNAME + " = ?";
        Cursor cursor = db.rawQuery(query, new String[]{username});

        if (cursor != null && cursor.moveToFirst()) {
            String storedPassword = cursor.getString(cursor.getColumnIndex(DbHelper.PASSWORD));
            cursor.close();
            db.close();
            return currentPassword.equals(storedPassword);
        } else {
            return false;
        }
    }

    public void updateUserPassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DbHelper.PASSWORD, newPassword);
        String whereClause = DbHelper.USERNAME + " = ?";
        String[] whereArgs = {username};
        int numRowsUpdated = db.update(DbHelper.USERS_TABLE_NAME, values, whereClause, whereArgs);
        db.close();

        if (numRowsUpdated > 0) {

        } else {

        }
    }
    long addClass(String className ,String subjectName){
        SQLiteDatabase database =this.getWritableDatabase();
        ContentValues  values =new ContentValues();
                values.put(CLASS_NAME_KEY,className);
                values.put(SUBJECT_NAME_KEY,subjectName);

        return  database.insert(CLASS_TABLE_NAME,null,values);

    }
    Cursor getClassTable(){
        SQLiteDatabase database= this.getReadableDatabase();
        return database.rawQuery(SELECT_CLASS_TABLE,null);
    }
    int deleteClass(long cid){
        SQLiteDatabase database= this.getReadableDatabase();
        return database.delete(CLASS_TABLE_NAME,C_ID+"=?", new String[]{String.valueOf(cid)});
    }
    long updateClass( long cid, String className ,String subjectName){
        SQLiteDatabase database =this.getWritableDatabase();
        ContentValues  values =new ContentValues();
        values.put(CLASS_NAME_KEY,className);
        values.put(SUBJECT_NAME_KEY,subjectName);

        return  database.update(CLASS_TABLE_NAME,values,C_ID+"=?", new String[]{String.valueOf(cid)});

    }
//    public List<ClassItem> searchClasses(String className) {
//        List<ClassItem> results = new ArrayList<>();
//        String query = "SELECT * FROM " + CLASS_TABLE_NAME +
//                " WHERE " + CLASS_NAME_KEY + " LIKE '%" + className + "%'";
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(query, null);
//        while(cursor.moveToNext()) {
//            long cid = cursor.getLong(cursor.getColumnIndex(C_ID));
//            String name = cursor.getString(cursor.getColumnIndex(CLASS_NAME_KEY));
//            String subject = cursor.getString(cursor.getColumnIndex(SUBJECT_NAME_KEY));
//
//            ClassItem classItem = new ClassItem(cid, name, subject);
//            results.add(classItem);
//        }
//        cursor.close();
//        db.close();
//        return results;
//    }
//    public List<ClassItem> searchSubject(String subjectName) {
//        List<ClassItem> results = new ArrayList<>();
//        String query = "SELECT * FROM " + CLASS_TABLE_NAME +
//                " WHERE " + SUBJECT_NAME_KEY + " LIKE '%" + subjectName + "%'";
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(query, null);
//        while(cursor.moveToNext()) {
//            long cid = cursor.getLong(cursor.getColumnIndex(C_ID));
//            String name = cursor.getString(cursor.getColumnIndex(CLASS_NAME_KEY));
//            String subject = cursor.getString(cursor.getColumnIndex(SUBJECT_NAME_KEY));
//
//            ClassItem classItem = new ClassItem(cid, name, subject);
//            results.add(classItem);
//        }
//        cursor.close();
//        db.close();
//        return results;
//    }
//    public List<StudentItem> searchStudents(String studentName) {
//        List<StudentItem> results = new ArrayList<>();
//        String query = "SELECT * FROM " + STUDENT_TABLE_NAME +
//                " WHERE " + STUDENT_NAME_KEY + " LIKE '%" + studentName + "%'";
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(query, null);
//        while (cursor.moveToNext()) {
//            long studentId = cursor.getLong(cursor.getColumnIndex(S_ID));
//            String name = cursor.getString(cursor.getColumnIndex(STUDENT_NAME_KEY));
//            String roll = cursor.getString(cursor.getColumnIndex(STUDENT_ROLL_KEY));
//            int rollNumber = Integer.parseInt(roll);
//            StudentItem studentItem = new StudentItem(studentId, rollNumber, name);
//            results.add(studentItem);
//        }
//        cursor.close();
//        db.close();
//        return results;
//    }


    long addStudent(long cid,int roll,String name){
        SQLiteDatabase database=this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(C_ID,cid);
        values.put(STUDENT_ROLL_KEY,roll);
        values.put(STUDENT_NAME_KEY,name);
        return database.insert(STUDENT_TABLE_NAME,null,values);


    }
    Cursor getStudentTable(long cid){
        SQLiteDatabase database=this.getWritableDatabase();
        return database.query(STUDENT_TABLE_NAME,null,C_ID+"=?",new String[]{String.valueOf(cid)},null,null,STUDENT_ROLL_KEY);
    }
    int deleteStudent(long sid) {
        SQLiteDatabase database = this.getReadableDatabase();
        return database.delete(STUDENT_TABLE_NAME, S_ID + "=?", new String[]{String.valueOf(sid)});
    }

    long updateStudent (long sid,String name){
        SQLiteDatabase database = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(STUDENT_NAME_KEY,name);
            return  database.update(STUDENT_TABLE_NAME,values,S_ID+"=?", new String[]{String.valueOf(sid)});
    }
    long addStatus(long sid,long cid,String date,String status){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(S_ID,sid);
        values.put(C_ID,cid);
        values.put(DATE_KEY,date);
        values.put(STATUS_KEY,status);
        return  database.insert(STATUS_TABLE_NAME, null,values);

    }
    long updateStatus(long sid,String date,String status){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(STATUS_KEY,status);
        String whereClause = DATE_KEY +"='"+date+"' AND "+S_ID+ " = "+ sid;
        return database.update(STATUS_TABLE_NAME,values,whereClause,null);

    }
    String getStatus (long sid, String date){
        String status=null;
        SQLiteDatabase database = this.getReadableDatabase();
        String whereClause = DATE_KEY +"='"+date+"' AND "+S_ID+ " = "+ sid;
        Cursor cursor = database.query( STATUS_TABLE_NAME,null,whereClause,null,null,null,null);
        if(cursor.moveToFirst())
            status=cursor.getString(cursor.getColumnIndex(STATUS_KEY));
        return status;

    }
    Cursor getDistinctMonths(long cid){
        SQLiteDatabase database= this.getReadableDatabase();
        return  database.query(STATUS_TABLE_NAME,new String[]{DATE_KEY},C_ID+"="+cid,null,"substr(" + DATE_KEY +",4,7)",null,null);//01.04.2023

    }


}
