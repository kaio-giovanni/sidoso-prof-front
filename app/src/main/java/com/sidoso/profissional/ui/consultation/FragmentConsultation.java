package com.sidoso.profissional.ui.consultation;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sidoso.profissional.R;
import com.sidoso.profissional.adapter.ConsultaAdapter;
import com.sidoso.profissional.http.VolleySingleton;
import com.sidoso.profissional.model.Consulta;
import com.sidoso.profissional.model.Paciente;
import com.sidoso.profissional.model.Profissao;
import com.sidoso.profissional.model.Profissional;
import com.sidoso.profissional.ui.patients.FragmentPatients;
import com.sidoso.profissional.utils.MaskEditText;
import com.sidoso.profissional.utils.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static android.content.Context.MODE_PRIVATE;
import static com.sidoso.profissional.config.Constants.API_URL;
import static com.sidoso.profissional.config.Constants.FILE_PREFERENCES;

public class FragmentConsultation extends Fragment {
    private ProgressBar progressBar;
    private RecyclerView recyclerConsultation;
    private FloatingActionButton faButton;
    private TextView tv_empty;
    private ConsultaAdapter consultaAdapter;
    private SharedPreferences mUserSaved;

    private static List<Consulta> consultas = new ArrayList<Consulta>();
    private static boolean requestStarted = false;
    private String tokenApi;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consultation, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_consultation);
        progressBar.setVisibility(requestStarted == true ? View.VISIBLE : View.GONE);

        recyclerConsultation = (RecyclerView) view.findViewById(R.id.list_consultation);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerConsultation.setLayoutManager(layoutManager);
        recyclerConsultation.setHasFixedSize(true);

        tv_empty = (TextView) view.findViewById(R.id.tv_empty_consultation);

        faButton = (FloatingActionButton) view.findViewById(R.id.fa_new_consulta);
        faButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAlertDialog();
            }
        });

        mUserSaved = getContext().getSharedPreferences(FILE_PREFERENCES, MODE_PRIVATE);

        if(consultas.isEmpty() && !requestStarted){
            recyclerConsultation.setVisibility(View.GONE);
            tv_empty.setVisibility(View.VISIBLE);
            getConsultas(mUserSaved.getInt("userId", 0), mUserSaved.getString("tokenApi", ""));
        }

        consultaAdapter = new ConsultaAdapter(consultas);
        recyclerConsultation.setAdapter(consultaAdapter);

        recyclerConsultation.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(),
                recyclerConsultation,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {}
                    @Override
                    public void onLongItemClick(View view, int position) {
                        openOptionMenu(view, position);
                    }
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {}
                }
        ));

        return view;
    }

    private void isLoading(Boolean y){
        FragmentConsultation.requestStarted = y;
        if(y){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
        }
    }

    private String stringToDate(String aDate, String aFormat){
        if (aDate == null) return "0000-00-00";
        SimpleDateFormat df = new SimpleDateFormat(aFormat);
        df.setLenient(false);
        df.setTimeZone(TimeZone.getTimeZone("America/Fortaleza"));

        try {
            Date date = df.parse(aDate);
            return df.format(date);
        }catch(ParseException e){
            e.printStackTrace();
            return null;
        }
    }

    private void createAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        List<Paciente> pacientes = FragmentPatients.getArrayPacientes();
        int list_size = pacientes.size();
        final String[] nomes = new String[list_size];
        final int[] ids = new int[list_size];
        for(int i = 0; i < list_size; i++){
            nomes[i] = pacientes.get(i).getName();
            ids[i] = pacientes.get(i).getId();
        }

        final Paciente paciente = new Paciente();

        LayoutInflater layoutInflater = getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.create_consulta, null);
        builder.setView(v);

        final EditText title = v.findViewById(R.id.et_title_consulta);
        final EditText date = v.findViewById(R.id.et_date_consulta);
        final EditText hour = v.findViewById(R.id.et_hour_consulta);
        final EditText obs = v.findViewById(R.id.et_obs_consulta);
        final Spinner spinner = v.findViewById(R.id.spinner_pacientes);

        date.addTextChangedListener(MaskEditText.mask(date, MaskEditText.FORMAT_DATE));
        hour.addTextChangedListener(MaskEditText.mask(hour, MaskEditText.FORMAT_HOUR));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, nomes);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                paciente.setId(ids[i]);
                paciente.setName(nomes[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Consulta c = new Consulta();
                Profissional profissional = new Profissional();
                profissional.setId(mUserSaved.getInt("userId", 0));
                c.setTitle(title.getText().toString());
                c.setDate(date.getText().toString() + " " + hour.getText().toString());
                c.setLat(-37.0000);
                c.setLng(-37.0000);
                c.setObs(obs.getText().toString());
                c.setPaciente(paciente);
                c.setProfissional(profissional);

                createConsulta(c);
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void openOptionMenu(View v,final int position){
        PopupMenu popup = new PopupMenu(v.getContext(), v);
        popup.getMenuInflater().inflate(R.menu.menu_consulta, popup.getMenu());
        popup.setGravity(Gravity.RIGHT);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Consulta c = consultas.get(position);
                switch (item.getItemId()){
                    case R.id.consulta_status_concluida:
                        changeStatusConsulta(c, "002");
                        return true;
                    case R.id.consulta_status_cancelada:
                        changeStatusConsulta(c, "003");
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    private void getConsultas(int idUser, final String tokenApi){
        String url = API_URL.concat("profissional/"+idUser+"/consulta/");
        this.tokenApi = tokenApi;

        isLoading(true);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Consulta consulta;
                Profissional profissional = new Profissional();
                Profissao profissao;
                Paciente paciente = new Paciente();
                isLoading(false);

                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        JSONObject profissionalJson = object.getJSONObject("profissional");
                        JSONObject profissaoJson = profissionalJson.getJSONObject("profissao");
                        JSONObject pacienteJson = object.getJSONObject("paciente");

                        consulta = new Consulta();

                        profissao = new Profissao(profissaoJson.getInt("id"), profissaoJson.getString("name"));

                        profissional.setId(profissionalJson.getInt("id"));
                        profissional.setName(profissionalJson.getString("name"));
                        profissional.setProfissao(profissao);

                        paciente.setId(pacienteJson.getInt("id"));
                        paciente.setName(pacienteJson.getString("name"));
                        paciente.setDt_birth(pacienteJson.getString("birth"));
                        paciente.setGenre(pacienteJson.getString("genre"));
                        paciente.setPhoneMain(pacienteJson.getString("phone_main"));
                        paciente.setEmail(pacienteJson.getString("email"));

                        consulta.setId(object.getInt("id"));
                        consulta.setTitle(object.getString("title"));
                        consulta.setDate(stringToDate(object.getString("date"), "yyyy-MM-dd'T'HH:mm").replace("T", " "));
                        consulta.setStatus(object.getString("status"));
                        consulta.setProfissional(profissional);
                        consulta.setPaciente(paciente);
                        consulta.setObs(object.getString("obs"));

                        consultas.add(consulta);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    tv_empty.setVisibility(View.GONE);
                    recyclerConsultation.setVisibility(View.VISIBLE);
                }
                consultaAdapter.notifyDataSetChanged();
                ;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                isLoading(false);

                if(networkResponse == null){
                    Log.e("ErrorHttpResponse", error.getClass().toString());
                }else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        Toast.makeText(getContext(), response.getString("error"), Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", tokenApi);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void createConsulta(Consulta c){
        String url = API_URL.concat("profissional/"+c.getProfissional().getId()+"/consulta/marcar");

        isLoading(true);

        JSONObject object = new JSONObject();
        try{
            object.put("title", c.getTitle());
            object.put("profissionalId", c.getProfissional().getId());
            object.put("pacienteId", c.getPaciente().getId());
            object.put("date", c.getDate());
            object.put("latitude", c.getLat());
            object.put("longitude", c.getLng());
            object.put("obs", c.getObs());
        }catch (JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                isLoading(false);
                Toast.makeText(getContext(), "Consulta marcada com sucesso", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                isLoading(false);

                if(networkResponse == null){
                    Log.e("ErrorHttpResponse", error.getClass().toString());
                }else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        Toast.makeText(getContext(), response.getString("error"), Toast.LENGTH_SHORT).show();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", tokenApi);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void changeStatusConsulta(Consulta c, String status){
        String url = API_URL.concat("profissional/" + c.getProfissional().getId() + "/consulta/editar");

        isLoading(true);

        JSONObject object = new JSONObject();
        try{
            object.put("consultaId", c.getId());
            object.put("newStatus", status);
        }catch (JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PATCH, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                isLoading(false);
                Toast.makeText(getContext(), "Status da consulta alterado", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading(false);
                Toast.makeText(getContext(), "Ocorreu um erro", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/json");
                params.put("Authorization", tokenApi);
                return params;
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);

    }
}
