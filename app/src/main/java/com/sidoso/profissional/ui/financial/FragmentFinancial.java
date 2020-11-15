package com.sidoso.profissional.ui.financial;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.sidoso.profissional.R;
import com.sidoso.profissional.http.VolleySingleton;
import com.sidoso.profissional.model.Consulta;
import com.sidoso.profissional.model.Paciente;
import com.sidoso.profissional.model.Profissao;
import com.sidoso.profissional.model.Profissional;
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

public class FragmentFinancial extends Fragment {
    private ProgressBar progressBar;
    private BarChart barChart;
    private static  List<BarEntry> entries;
    private static List<Consulta> consultas = new ArrayList<Consulta>();
    private SharedPreferences mUserSaved;
    private static boolean requestStarted = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_financial, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_financial);
        progressBar.setVisibility(requestStarted == true ? View.VISIBLE : View.GONE);

        barChart = (BarChart) view.findViewById(R.id.chart_financial);
        //barChart.setDrawBarShadow(false);
        //barChart.setDrawValueAboveBar(true);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return "Total";
            }
        });

        mUserSaved = getContext().getSharedPreferences(FILE_PREFERENCES, MODE_PRIVATE);

        if(!requestStarted){
            getConsultas(mUserSaved.getInt("userId", 0), mUserSaved.getString("tokenApi", ""));
        }

        entries = new ArrayList<BarEntry>();

        barChart.animateXY(3000, 3000);
        barChart.invalidate();

        return view;
    }

    private void getConsultas(int idUser, final String tokenApi){
        String url = API_URL.concat("profissional/"+idUser+"/consulta/");

        isLoading(true);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                List<Consulta> dados = new ArrayList<Consulta>();
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
                        consulta.setDate(stringToDate(object.getString("date"), "yyyy-MM-dd'T'HH:mm:ss"));
                        consulta.setStatus(object.getString("status"));
                        consulta.setProfissional(profissional);
                        consulta.setPaciente(paciente);
                        consulta.setObs(object.getString("obs"));

                        dados.add(consulta);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } finally {
                        consultas = dados;
                        entries.add(new BarEntry(1, consultas.size(), "N"));
                        BarDataSet barDataSet = new BarDataSet(entries, "Numero de consultas");
                        //barDataSet.setDrawValues(true);
                        BarData barData = new BarData(barDataSet);
                        barChart.setData(barData);
                        barChart.notifyDataSetChanged();
                    }
                }
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

    private void isLoading(Boolean y){
        FragmentFinancial.requestStarted = y;
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
}
