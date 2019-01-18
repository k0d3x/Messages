package com.ameer.messages.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ameer.messages.R;
import com.ameer.messages.SMS;

import java.util.List;
//ameer
public class ChatAdapter extends RecyclerView.Adapter {

    private String ID;
    private Context mContext;
    private List<SMS> chatData;
    private final static int LAYOUT_INBOX = 1;
    private final static int LAYOUT_SENT = 0;
    private final static String INBOX = "inbox";
    private final static String SENT = "sent";

    public ChatAdapter(Context context,List<SMS> chatData,String ID) {
        this.mContext = context;
        this.chatData = chatData;
        this.ID = ID;
        Log.d("rahul",""+chatData.size());
    }

    @Override
    public int getItemViewType(int position) {
        if(chatData.get(position).getFolderName().equals(INBOX))
            return LAYOUT_INBOX;
        else
            return LAYOUT_SENT;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(viewType == LAYOUT_INBOX){
            View view = layoutInflater.inflate(
                    R.layout.detailed_incoming_message_layout,viewGroup,false);
            return new InboxViewHolder(view);

        }else{
            View view = layoutInflater.inflate(
                    R.layout.detailed_sent_message_layout,viewGroup,false);
            return new SentViewHolder(view);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        SMS sms = chatData.get(i);
        if(viewHolder instanceof InboxViewHolder){
            ((InboxViewHolder) viewHolder).contactName.setText(sms.getAddress());
            ((InboxViewHolder) viewHolder).avatar.setText(ID.charAt(0)+"");
            ((InboxViewHolder) viewHolder).messageBody.setText(sms.getMsg());
            ((InboxViewHolder) viewHolder).messageTime.setText(
                    AllMessagesAdapter.getDate(sms.getTime()));

        }else if(viewHolder instanceof SentViewHolder){
            ((SentViewHolder) viewHolder).messageBody.setText(sms.getMsg());
        }
    }

    @Override
    public int getItemCount() {
        Log.d("rahul", "getItemCount: "+chatData.size());
        return chatData.size();
    }

    private class InboxViewHolder extends RecyclerView.ViewHolder{

        private TextView avatar;
        private TextView contactName;
        private TextView messageBody;
        private TextView messageTime;
        public InboxViewHolder(@NonNull View itemView) {
            super(itemView);
            this.avatar = itemView.findViewById(R.id.avatar);
            this.contactName = itemView.findViewById(R.id.contact_name);
            this.messageBody = itemView.findViewById(R.id.message_body);
            this.messageTime = itemView.findViewById(R.id.smsTime);
        }
    }
    private class SentViewHolder extends RecyclerView.ViewHolder{

        private TextView messageBody;
        public SentViewHolder(@NonNull View itemView) {
            super(itemView);
            this.messageBody = itemView.findViewById(R.id.message_body);
        }
    }
}
