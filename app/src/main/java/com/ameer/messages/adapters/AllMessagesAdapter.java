package com.ameer.messages.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
//import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ameer.messages.R;
import com.ameer.messages.SMS;
import com.ameer.messages.utils.ColorGeneratorModified;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class AllMessagesAdapter extends RecyclerView.Adapter<AllMessagesAdapter.InboxViewHolder> {
    private Context context;
    private List<SMS> data;
    ColorGeneratorModified generator = ColorGeneratorModified.MATERIAL;
    private InboxItemClickListener mInboxItemClickListener;

    public AllMessagesAdapter(Context context, List<SMS> data){
        Log.d("amir","AllMessagesAdapter created");
        this.context = context;
        this.data = data;
    }

    @Override
    public InboxViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Log.d("amir","oncreateviewholder");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.inbox_item_layout,viewGroup,false);
        return new InboxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InboxViewHolder holder, int i) {
        Log.d("rahul","onbinddata"+i);
        final SMS sms = data.get(i);
        holder.senderContact.setText(sms.getAddress());
        holder.message.setText(sms.getMsg());
        holder.time.setText(getDate(sms.getTime()));
        holder.senderImage.setText(sms.getAddress().charAt(0)+"");
    }

    public void setItemClickListener(InboxItemClickListener mInboxItemClickListener){
        this.mInboxItemClickListener = mInboxItemClickListener;
    }
    @Override
    public int getItemCount() {
        return data.size();
    }

    public static String getDate(long milliSeconds)
    {
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }

    public class InboxViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        private TextView senderImage;
        private TextView senderContact;
        private TextView message;
        private TextView time;
        private RelativeLayout itemRelativeLayout;
        public InboxViewHolder(@NonNull View itemView) {
            super(itemView);

            senderImage = itemView.findViewById(R.id.contactImage);
            senderContact = itemView.findViewById(R.id.contactName);
            message = itemView.findViewById(R.id.smsContent);
            time = itemView.findViewById(R.id.smsTime);
            itemRelativeLayout = itemView.findViewById(R.id.inbox_item);
            itemRelativeLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            SMS clickedSms = data.get(getAdapterPosition());
            mInboxItemClickListener.OnInboxItemClickListener(clickedSms.getAddress(),
                    clickedSms.getId());
            Log.d("ameer","onclick item");
        }
    }
}
