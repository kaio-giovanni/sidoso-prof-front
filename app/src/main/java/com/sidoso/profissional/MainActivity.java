package com.sidoso.profissional;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.google.android.material.navigation.NavigationView;

import com.sidoso.profissional.model.Profissional;
import com.sidoso.profissional.websocket.WebSocket;

import static com.sidoso.profissional.config.Constants.FILE_PREFERENCES;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private TextView tvUserName;
    private TextView tvUserEmail;
    private WebSocket webSocket;
    private SharedPreferences mUserSaved;
    private Profissional p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_patients,
                R.id.nav_consultation,
                R.id.nav_financial,
                R.id.nav_settings,
                R.id.nav_about
        ).setDrawerLayout(drawer).build();

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        View headerView = navigationView.getHeaderView(0);

        tvUserName = (TextView) headerView.findViewById(R.id.nav_header_username);
        tvUserEmail = (TextView) headerView.findViewById(R.id.nav_header_email);

        mUserSaved = getSharedPreferences(FILE_PREFERENCES, MODE_PRIVATE);

        if (mUserSaved.contains("userName") && mUserSaved.contains("userEmail")){
            // usuario logado
            int id =  mUserSaved.getInt("userId", 0);
            String nome = mUserSaved.getString("userName", "User invalid!!!");
            String email = mUserSaved.getString("userEmail", "Email invalid!!!");

            tvUserName.setText(nome);
            tvUserEmail.setText(email);

            p = new Profissional();
            p.setId(id);
            p.setName(nome);
            p.setEmail(email);

            webSocket = new WebSocket(p, getApplicationContext());
        }else{
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webSocket.disconnectSocket();
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}