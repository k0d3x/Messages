package com.ameer.messages.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.ameer.messages.R;
import com.ameer.messages.SMS;
import com.ameer.messages.adapters.AllMessagesAdapter;
import com.ameer.messages.adapters.InboxItemClickListener;
import com.ameer.messages.services.NotificationService;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        InboxItemClickListener {

    private RecyclerView inboxRecyclerView;
    private final int READ_SMS_REQUEST_CODE = 1;
    private AllMessagesAdapter adapter;
    private List<SMS> smsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_SMS)== PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this
                ,Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(MainActivity.this,"Permission already Granted",
                    Toast.LENGTH_SHORT).show();
            init();
        }else {
            requestForPermission();
        }
    }
    private void requestForPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,
                Manifest.permission.READ_SMS)){
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.permission_title)
                    .setMessage(R.string.permission_message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.READ_SMS},READ_SMS_REQUEST_CODE);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();
        }else{
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_SMS,Manifest.permission.RECEIVE_SMS},READ_SMS_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == READ_SMS_REQUEST_CODE){
            if(grantResults.length > 0 && grantResults[0] == (PackageManager.PERMISSION_GRANTED)){
                Toast.makeText(this,"Permission Granted!",Toast.LENGTH_SHORT).show();
                init();
                Log.d("amir","init started");
            }else{
                Toast.makeText(this,"Permission Denied!",Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void init(){
        Log.d("amir","init");
        inboxRecyclerView = findViewById(R.id.mainRecyclerView);
        getLoaderManager().initLoader(1,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("amir","oncreateloader");
        Uri smsURI = Uri.parse("content://sms/");
        return new CursorLoader(this,
                smsURI,
                null,
                null,
                null,
                "date DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        getAllSmsToFile(data);
        adapter = new AllMessagesAdapter(MainActivity.this,smsList);
        adapter.setItemClickListener(this);
        inboxRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        inboxRecyclerView.setAdapter(adapter);
        inboxRecyclerView.setNestedScrollingEnabled(false);

    }

    public void getAllSmsToFile(Cursor c) {
        Log.d("amir","getAllSmsToFile");
        List<SMS> lstSms = new ArrayList<>();
        SMS objSMS = null;
        int totalSMS = c.getCount();
        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {
                try {
                    objSMS = new SMS();
                    objSMS.setId(c.getLong(c.getColumnIndexOrThrow("_id")));
                    String num=c.getString(c.getColumnIndexOrThrow("address"));
                    objSMS.setAddress(num);
                    objSMS.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                    objSMS.setReadState(c.getString(c.getColumnIndex("read")));
                    objSMS.setTime(c.getLong(c.getColumnIndexOrThrow("date")));
                    if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                        objSMS.setFolderName("inbox");
                    } else {
                        objSMS.setFolderName("sent");
                    }

                } catch (Exception e) {

                } finally {
                    lstSms.add(objSMS);
                    c.moveToNext();
                }
            }
        }
        //c.close();
        Log.d("rahulkumar", "getAllSmsToFile: "+lstSms.size());
        Set<SMS> setOfSMSAsPerAddress = new LinkedHashSet<>(lstSms);
        smsList = new ArrayList<>();
        smsList.addAll(setOfSMSAsPerAddress);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //adapter.notifyDataSetChanged();
    }

    @Override
    public void OnInboxItemClickListener(String contact,long messageId) {
        Intent intent =new Intent(this,ChatActivity.class);
        intent.putExtra("contact_name",contact);
        intent.putExtra("message_id",messageId);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter("SaveSMS");
        this.registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("avi", "onReceive: "+intent.getStringExtra("address")+ " : "+
                intent.getStringExtra("message_body"));
                getLoaderManager().restartLoader(1,null,MainActivity.this);
            }
        },intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}