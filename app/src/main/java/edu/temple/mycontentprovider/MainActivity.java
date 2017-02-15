package edu.temple.mycontentprovider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity {
    Uri mUri_insert = Uri.parse("content://" + MyContentProvider.strUri + "/test");
    Uri mUris_query = Uri.parse("content://" + MyContentProvider.strUri + "/test/1");
    Uri mUris_update = Uri.parse("content://" + MyContentProvider.strUri + "/test/1");


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView pu, pr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Storage Permissions
        verifyStoragePermissions(this);

        findViewById(R.id.display).setOnClickListener(onClickListener);
        pu = (TextView) findViewById(R.id.pub_text);
        pr = (TextView) findViewById(R.id.pri_text);
    }

    public void insert(View v) {
        ContentValues value = new ContentValues();
        value.put(SqliteHelper.DB_TABLE_NAME, "test5");
    }

    public void query(View v) {
        ContentResolver cr = this.getContentResolver();
        Cursor c = cr.query(mUris_query, null, null, null, null);
        if (c.moveToFirst() == false) {
            return;
        }
        String out = String.valueOf(c.getCount());
        String dg = String.valueOf(c.getColumnCount());

        Log.d(TAG, dg);

        c.moveToLast();
        int puindex = c.getColumnIndex("PUK");
        int prindex = c.getColumnIndex("PRK");
        String strpu = c.getString(puindex);
        String strpr = c.getString(prindex);
        pu.setText(strpu);
        pr.setText(strpr);
        c.close();
    }

    public void modify(View v) {
        ContentResolver cr = this.getContentResolver();
        ContentValues value = new ContentValues();
        value.put(SqliteHelper.DB_TABLE_NAME, "hehe");
        cr.update(mUris_update, value, null, null);
    }

    public void showcontent(View view) {
        query(view);
    }


    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.display:
                    showcontent(v);
                    break;
            }
        }
    };

    public static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }
}