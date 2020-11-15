package com.sidoso.profissional.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sidoso.profissional.R;
import com.sidoso.profissional.model.Consulta;

import java.util.List;

public class ConsultaAdapter extends RecyclerView.Adapter<ConsultaAdapter.ConsultaViewHolder> {
    List<Consulta> consultas;

    public ConsultaAdapter(List<Consulta> consultas){
        this.consultas = consultas;
    }

    public class ConsultaViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView date;
        TextView status;
        TextView profissional;
        TextView paciente;
        TextView obs;

        public ConsultaViewHolder(@NonNull View itemView){
            super(itemView);

            title = itemView.findViewById(R.id.tv_consulta_title);
            date = itemView.findViewById(R.id.tv_consulta_date);
            status = itemView.findViewById(R.id.tv_consulta_status);
            profissional = itemView.findViewById(R.id.tv_consulta_prof);
            paciente = itemView.findViewById(R.id.tv_consulta_pac);
            obs = itemView.findViewById(R.id.tv_consulta_obs);
        }
    }

    @NonNull
    @Override
    public ConsultaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View consultaView = LayoutInflater.from(parent.getContext()).inflate(R.layout.consulta_adapter_item, parent, false);
        return new ConsultaViewHolder(consultaView);
    }

    @Override
    public void onBindViewHolder(@NonNull ConsultaViewHolder holder, int position) {
        Consulta consulta = consultas.get(position);

        holder.title.setText(consulta.getTitle());
        holder.date.setText("Data e hora: " + consulta.getDate());
        holder.status.setText(consulta.getStatus());
        holder.status.setBackgroundResource(changeStatusColor(consulta.getStatus()));
        holder.profissional.setText(consulta.getProfissional().toString());
        holder.paciente.setText(consulta.getPaciente().toString());
        holder.obs.setText(consulta.getObs());
    }

    @Override
    public int getItemCount() {
        return consultas.size();
    }

    private int changeStatusColor(String status){
        if(status == "MARCADA"){
            return R.color.colorGreenLight;
        } else if(status == "CONCLUIDA"){
            return R.color.colorPrimaryDark;
        } else if(status == "CANCELADA"){
            return R.color.colorRed;
        }else {
            return R.color.colorGray;
        }
    }

}