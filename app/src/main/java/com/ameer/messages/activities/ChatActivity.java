package com.ameer.messages.activities;

import android.Manifest;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Telephony;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.ameer.messages.R;
import com.ameer.messages.SMS;
import com.ameer.messages.adapters.ChatAdapter;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>,
        View.OnClickListener{
    private String contactName;
    private long messageId;
    private RecyclerView chatRecyclerView;
    private List<SMS> listChatData = new ArrayList<>();
    private ImageButton sendMessage;
    private EditText textMessage;
    private ChatAdapter chatAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        chatRecyclerView = findViewById(R.id.chat_recycler);
        sendMessage = findViewById(R.id.send);
        textMessage = findViewById(R.id.editText);
        sendMessage.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();
        contactName = bundle.getString("contact_name");
        messageId = bundle.getLong("message_id");
        setTitle(contactName);
        /*startService(new Intent(this,UpdateSMSService.class)
        .putExtra("contact_name",contactName)
        .putExtra("message_id",messageId));*/
        getLoaderManager().initLoader(2,null,this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d("ameer","oncreate");
        Uri smsURI = Uri.parse("content://sms/");
        String[] selectionArgs = new String[]{contactName};
        return new CursorLoader(ChatActivity.this,
                smsURI,
                null,
                "address = ? ",
                selectionArgs,
                "date DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        while(data.moveToNext()){
            SMS sms = new SMS();
            try{
                sms.setAddress(data.getString(
                        data.getColumnIndex(Telephony.TextBasedSmsColumns.ADDRESS)));
                sms.setId(data.getLong(
                        data.getColumnIndex("_id")));
                sms.setMsg(data.getString(
                        data.getColumnIndex(Telephony.TextBasedSmsColumns.BODY)));
                String folder = data.getString(
                        data.getColumnIndex(Telephony.TextBasedSmsColumns.TYPE));
                if(folder.contains("1")){
                    sms.setFolderName("inbox");
                    sms.setTime(data.getLong(
                            data.getColumnIndex(Telephony.TextBasedSmsColumns.DATE)));
                }else {
                    sms.setFolderName("sent");
                    sms.setTime(data.getLong(
                            data.getColumnIndex(Telephony.TextBasedSmsColumns.DATE_SENT)));
                }
                sms.setReadState(data.getString(
                        data.getColumnIndex(Telephony.TextBasedSmsColumns.READ)));

            }catch (Exception e){

            }finally {
                listChatData.add(sms);
                //data.close();
            }
        }// all message added to chat list
        data.close();
        setRecyclerView(listChatData);

    }

    private void setRecyclerView(List<SMS> chatData){
        chatAdapter = new ChatAdapter(ChatActivity.this,chatData,contactName);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(false);
        chatRecyclerView.setLayoutManager(linearLayoutManager);

        chatRecyclerView.setAdapter(chatAdapter);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    private void requestPermissions() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        1);
            }
        }else{
            sendSMSNow();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendSMSNow();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS failed, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }
    private void sendSMSNow(){
        String message = textMessage.getText().toString();
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(contactName,null,message,null,null);
        SMS newSMS = new SMS();
        newSMS.setAddress(contactName);
        newSMS.setReadState("1");
        newSMS.setTime(System.currentTimeMillis());
        newSMS.setFolderName("sent");
        newSMS.setMsg(message);
        listChatData.add(0,newSMS);
        chatAdapter.notifyDataSetChanged();
        chatRecyclerView.smoothScrollToPosition(0);
        textMessage.setText("");
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.send:{
                requestPermissions();
            }
        }



    }
}
