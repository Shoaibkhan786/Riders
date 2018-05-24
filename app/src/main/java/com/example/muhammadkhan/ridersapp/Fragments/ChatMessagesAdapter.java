package com.example.muhammadkhan.ridersapp.Fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.muhammadkhan.ridersapp.R;
/**
 * Created by Muhammad Khan on 10/05/2018.
 */

public class ChatMessagesAdapter extends RecyclerView.Adapter<ChatMessagesAdapter.MyViewHolder>{
    private Context context;
    private String[] data;
    private LayoutInflater inflater;
    private MessageSelect listener;
    public ChatMessagesAdapter(Context context, String[] data) {
        this.context = context;
        this.data = data;
    }
    @Override
    public ChatMessagesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_message, parent, false);
        ChatMessagesAdapter.MyViewHolder viewHolder = new ChatMessagesAdapter.MyViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(ChatMessagesAdapter.MyViewHolder holder, int position) {
        String text=data[position];
        holder.message.setText(text);

    }
    @Override
    public int getItemCount() {
        return data.length;
    }
    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView message;
        public MyViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            message=itemView.findViewById(R.id.message);
        }

        @Override
        public void onClick(View view) {
            int position=getAdapterPosition();
            String message=data[position];

            listener=(MessageSelect)context;
            listener.onSelectMessage(message);
        }
    }
    public interface MessageSelect{
        void onSelectMessage(String text);
    }
}