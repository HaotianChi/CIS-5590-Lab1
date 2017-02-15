package edu.temple.mycontentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.ContentUris;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.security.KeyPair;
import java.security.KeyStoreException;

public class MyContentProvider extends ContentProvider {
    SQLiteDatabase db;
    RSA kg;
    KeyPair p;
    private SqliteHelper mHelper;
    public static final String strUri = "PKI";
    private static final int TEST=101;
    private static final int TESTS=102;
    private static final  UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(strUri, "test", TESTS);
        uriMatcher.addURI(strUri, "test/#", TEST);
    }




    public MyContentProvider() throws KeyStoreException {
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues initvalues) {
        db = mHelper.getWritableDatabase();
        ContentValues values;
        if(initvalues != null)
        {
            values = new ContentValues(initvalues);
        }else
        {
            values = new ContentValues();
        }
        p = kg.generateRSAKeyPair(1024);
        values.clear();

        values.put("PUK",p.getPublic().toString());
        values.put("PRK",p.getPrivate().toString());


        switch( uriMatcher.match(uri))
        {
            case TEST:
                break;
            case TESTS:
                if(values.containsKey(SqliteHelper.DB_TABLE_NAME) == false)
                {
                    values.put(SqliteHelper.DB_TABLE_NAME, "FCL");
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        long rowid = db.insert(SqliteHelper.DB_TABLE, null, values);
        if(rowid > 0)
        {
            Uri noteuri = ContentUris.withAppendedId(uri, rowid);
            return noteuri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {
        mHelper = new SqliteHelper(getContext());


        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        db = mHelper.getReadableDatabase();

        switch(uriMatcher.match(uri)) {
            case TEST:
                long id = ContentUris.parseId(uri);
                String where = "id=" + id;
                where += !TextUtils.isEmpty(selection) ? " and (" + selection + ")" : "";
                return db.query(SqliteHelper.DB_TABLE, projection, where, selectionArgs, null, null, sortOrder);
            case TESTS:
                return db.query(SqliteHelper.DB_TABLE, projection, selection, selectionArgs, null, null, sortOrder);
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        db = mHelper.getWritableDatabase();
        p = kg.generateRSAKeyPair(1024);
        values.clear();

        values.put("PUK",p.getPublic().toString());
        values.put("PRK",p.getPrivate().toString());
        values.put("name","ceron");
        int count = 0;
        switch( uriMatcher.match(uri))
        {
            case TEST:
                long id = ContentUris.parseId(uri);
                String where = "id="+id;
                where += !TextUtils.isEmpty(selection)?" and ("+selection+")" : "";
                count = db.update(SqliteHelper.DB_TABLE,values, where, selectionArgs);
                break;
            case TESTS:
                db.update(SqliteHelper.DB_TABLE, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        db.close();
        return count;
    }

}



class SqliteHelper extends SQLiteOpenHelper {


    public final static String DB_NAME = android.os.Environment.getExternalStorageDirectory().getAbsolutePath()+"/test.db";
    public final static int DB_VERSION = 1;

    public final static String DB_ID = "id";
    public final static String DB_TABLE = "test";
    public final static String DB_TABLE_NAME = "name";



    public SqliteHelper(Context context)
    {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + DB_TABLE + " ("
                + DB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                +"PUK BOLB, "
                +"PRK BOLB, "
                + DB_TABLE_NAME + " TEXT"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DB_TABLE);
        onCreate(db);
    }

}

