package com.sidoso.profissional.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sidoso.profissional.R;
import com.sidoso.profissional.model.Paciente;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {
    private List<Paciente> pacientes;

    public ChatAdapter(List<Paciente> pacientes){
        this.pacientes = pacientes;
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        ImageView photo;
        TextView name;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.patient_img);
            name = itemView.findViewById(R.id.patient_name);
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View chatItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_adapter_item, parent, false);
        return new ChatViewHolder(chatItem);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Paciente p = pacientes.get(position);
        //holder.photo.setImageBitmap(p.getPhotoUrl());
        holder.name.setText(p.getName());
    }

    @Override
    public int getItemCount() {
        return pacientes.size();
    }
}