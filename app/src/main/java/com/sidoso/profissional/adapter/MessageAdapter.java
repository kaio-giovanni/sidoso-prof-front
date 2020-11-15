package com.sidoso.profissional.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sidoso.profissional.R;
import com.sidoso.profissional.model.Message;
import com.sidoso.profissional.model.Profissional;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages;
    private Profissional p;

    public MessageAdapter(List<Message> messages, Profissional p){
        this.messages = messages;
        this.p = p;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView author;
        TextView message;
        TextView messageDate;

        public MessageViewHolder(@NonNull View itemView){
            super(itemView);

            author = itemView.findViewById(R.id.tv_author_message);
            message = itemView.findViewById(R.id.tv_message);
            messageDate = itemView.findViewById(R.id.tv_message_date);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(isUserLocal(messages.get(position))){
            return 1;
        } else {
            return 2;
        }
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View messageItem;
        if(viewType == 1){
            messageItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_send_adapter_item, parent, false);
        }else{
            messageItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_received_adapter_item, parent, false);
        }

        return new MessageAdapter.MessageViewHolder(messageItem);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.message.setText(message.getMessage());
        holder.messageDate.setText(message.getSenderAt());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private boolean isUserLocal(Message m){
        if(m.getSenderId() == this.p.getId() && m.getReceptorEmail() != this.p.getEmail()){
            return true;
        }
        return false;
    }
}