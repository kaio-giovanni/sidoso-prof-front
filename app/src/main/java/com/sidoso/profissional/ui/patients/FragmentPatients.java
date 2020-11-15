package com.sidoso.profissional.ui.patients;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.sidoso.profissional.ConversationActivity;
import com.sidoso.profissional.R;
import com.sidoso.profissional.adapter.ChatAdapter;
import com.sidoso.profissional.http.VolleySingleton;
import com.sidoso.profissional.model.Paciente;
import com.sidoso.profissional.utils.RecyclerItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.sidoso.profissional.config.Constants.API_URL;
import static com.sidoso.profissional.config.Constants.FILE_PREFERENCES;

public class FragmentPatients extends Fragment {
    private ProgressBar progressBar;
    private RecyclerView recyclerChat;
    private ChatAdapter chatAdapter;
    private SharedPreferences mUserSaved;

    private static List<Paciente> pacientes = new ArrayList<Paciente>();
    private static boolean requestStarted = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patient, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_patients);
        progressBar.setVisibility(requestStarted == true ? View.VISIBLE : View.GONE);

        recyclerChat = (RecyclerView) view.findViewById(R.id.list_conversation);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerChat.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        recyclerChat.setLayoutManager(layoutManager);
        recyclerChat.setHasFixedSize(true);

        mUserSaved = getContext().getSharedPreferences(FILE_PREFERENCES, MODE_PRIVATE);

        //get data from api
        if (pacientes.isEmpty() && !requestStarted){
            getPacientes(mUserSaved.getInt("userId", 0), mUserSaved.getString("tokenApi", ""));
        }

        chatAdapter = new ChatAdapter(pacientes);
        recyclerChat.setAdapter(chatAdapter);

        recyclerChat.addOnItemTouchListener(new RecyclerItemClickListener(
                getContext(),
                recyclerChat,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Intent intent = new Intent(getActivity(), ConversationActivity.class);
                        Paciente p = pacientes.get(position);
                        intent.putExtra("pacId", p.getId());
                        intent.putExtra("pacName", p.getName());
                        intent.putExtra("pacBirth", p.getDt_birth());
                        intent.putExtra("pacCpf", p.getCpf());
                        intent.putExtra("pacPhoneMain", p.getPhoneMain());
                        intent.putExtra("pacEmail", p.getEmail());

                        intent.putExtra("profId", mUserSaved.getInt("userId", 0));
                        intent.putExtra("profEmail", mUserSaved.getString("userEmail", ""));

                        startActivity(intent);
                    }
                    @Override
                    public void onLongItemClick(View view, int position) {}
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {}
                }
        ));

        return view;
    }

    private void getPacientes(int idUser, final String tokenApi){
        String url = API_URL.concat("profissional/"+idUser+"/pacientes/");

        isLoading(true);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Paciente p;
                isLoading(false);

                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject object = response.getJSONObject(i);
                        p = new Paciente();
                        p.setId(object.getInt("id"));
                        p.setName(object.getString("name"));
                        p.setImage(object.getString("photo"));
                        p.setDt_birth(object.getString("birth"));
                        p.setGenre(object.getString("genre"));
                        p.setPhoneMain(object.getString("phone_main"));
                        p.setEmail(object.getString("email"));

                        pacientes.add(p);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                chatAdapter.notifyDataSetChanged();
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

                        if(networkResponse.statusCode == 403){
                            //refresh token
                            Log.e("RefreshTokenAPI", "Token expired ".concat(response.getString("message")));
                            refreshTokenApi(API_URL.concat("login/profissional/"));
                        }
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

    private void refreshTokenApi(String url){
        Log.i("RefreshingTokenApi", "Update Token API");
        isLoading(true);

        String email = mUserSaved.getString("userEmail", "");
        String password = mUserSaved.getString("userPassword", "");

        JSONObject object;
        try{
            object = new JSONObject();
            object.put("email", email);
            object.put("password", password);
        }catch (JSONException e){
            object = null;
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                isLoading(false);
                SharedPreferences.Editor prefsEditor = mUserSaved.edit();
                try{
                    JSONObject headers = response.getJSONObject("headers");
                    prefsEditor.putString("tokenApi", headers.getString("Authorization"));

                    prefsEditor.commit();

                    getPacientes(mUserSaved.getInt("userId", 0), headers.getString("Authorization"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isLoading(false);
                NetworkResponse networkResponse = error.networkResponse;
                if(networkResponse == null){
                    Log.e("LoginError",error.getClass().toString());
                }else{
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
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try{
                    String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers, PROTOCOL_CHARSET));
                    JSONObject jsonResponse = new JSONObject(jsonString);
                    jsonResponse.put("headers", new JSONObject(response.headers));
                    return Response.success(jsonResponse, HttpHeaderParser.parseCacheHeaders(response));
                }catch (UnsupportedEncodingException e){
                    return Response.error(new ParseError(e));
                }catch(JSONException je){
                    return Response.error(new ParseError(je));
                }
            }
        };

        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(getContext()).addToRequestQueue(request);
    }

    private void isLoading(Boolean y){
        FragmentPatients.requestStarted = y;
        if(y){
            progressBar.setVisibility(View.VISIBLE);
        }else{
            progressBar.setVisibility(View.GONE);
        }
    }

    public static List<Paciente> getArrayPacientes(){
        return pacientes;
    }
}
