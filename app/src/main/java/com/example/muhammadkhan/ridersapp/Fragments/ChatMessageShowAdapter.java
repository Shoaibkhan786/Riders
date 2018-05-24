package com.example.muhammadkhan.ridersapp.Fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.muhammadkhan.ridersapp.Models.Chat;
import com.example.muhammadkhan.ridersapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muhammad Khan on 10/05/2018.
 */

public class ChatMessageShowAdapter extends RecyclerView.Adapter<ChatMessageShowAdapter.MyViewHolder> {
    private Context context;
    private List<Chat> data;
    private LayoutInflater inflater;

    public ChatMessageShowAdapter(Context context, List<Chat> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public ChatMessageShowAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_message_in_recyclerview, parent, false);
        ChatMessageShowAdapter.MyViewHolder viewHolder = new ChatMessageShowAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ChatMessageShowAdapter.MyViewHolder holder, int position) {
        Chat chat=(Chat) data.get(position);
        holder.personName.setText(chat.getName());
        holder.message.setText(chat.getMessage());
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView message;
        private TextView personName;

        public MyViewHolder(View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.message);
            personName=itemView.findViewById(R.id.sender_name);
        }
    }
}
