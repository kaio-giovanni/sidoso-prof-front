package com.sidoso.profissional.ui.settings;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.sidoso.profissional.R;
import com.sidoso.profissional.dao.MessageDAO;

import static android.content.Context.MODE_PRIVATE;
import static com.sidoso.profissional.config.Constants.FILE_PREFERENCES;

public class FragmentSettings extends Fragment {
    private ProgressBar progressBar;
    private ListView lt_settings;
    private MessageDAO messageDAO;
    private SharedPreferences mUserSaved;
    private ArrayAdapter<CharSequence> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        lt_settings = (ListView) view.findViewById(R.id.lv_settings);

        adapter = ArrayAdapter.createFromResource(getContext(), R.array.list_settings_fragment, android.R.layout.simple_list_item_1);
        lt_settings.setAdapter(adapter);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar_settings);

        mUserSaved = getContext().getSharedPreferences(FILE_PREFERENCES, MODE_PRIVATE);

        lt_settings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                alertDialogConfirmation((String) adapter.getItem(i), i);
            }
        });
        return view;
    }

    public void alertDialogConfirmation(String msg, final int option){
        final AsyncSettings aSettings = new AsyncSettings();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setMessage("Tem certeza que deseja " + msg.toLowerCase() + "?")
                .setTitle(msg);
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                aSettings.execute(option);
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

    private void deleteMessages(){
        messageDAO = new MessageDAO(getContext());
        int userId = mUserSaved.getInt("userId", 0);
        try {
            messageDAO.deleteAllMessages(userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void logout(){
        mUserSaved.edit().clear().commit();
        getActivity().finish();
    }

    private class AsyncSettings extends AsyncTask<Integer, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Integer... integers) {
            if (integers[0] == 0){
                deleteMessages();
            }else if(integers[0] == 1){
                logout();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
        }
    }
}
