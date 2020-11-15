package com.sidoso.profissional;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.sidoso.profissional.adapter.MessageAdapter;
import com.sidoso.profissional.dao.MessageDAO;
import com.sidoso.profissional.interfaces.IMessage;
import com.sidoso.profissional.listener.MessageListener;
import com.sidoso.profissional.model.Message;
import com.sidoso.profissional.model.Paciente;
import com.sidoso.profissional.model.Profissional;
import com.sidoso.profissional.websocket.WebSocket;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ConversationActivity extends AppCompatActivity implements IMessage {
    private Toolbar toolbar;
    private ImageButton btnSendMsg;
    private EditText etMsg;
    private RecyclerView recyclerMessage;
    private Paciente paciente;
    private MessageListener messageListener;
    private MessageAdapter messageAdapter;
    private MessageDAO messageDAO;
    private Profissional user;
    private static List<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        Intent intent = getIntent();

        paciente = new Paciente();
        paciente.setId(intent.getIntExtra("pacId", 0));
        paciente.setName(intent.getStringExtra("pacName"));
        paciente.setEmail(intent.getStringExtra("pacEmail"));
        paciente.setDt_birth(intent.getStringExtra("pacBirth"));
        paciente.setPhoneMain(intent.getStringExtra("pacPhoneMain"));
        paciente.setCpf(intent.getStringExtra("pacCpf"));

        user = new Profissional();
        user.setId(intent.getIntExtra("profId", 0));
        user.setEmail(intent.getStringExtra("profEmail"));

        toolbar = (Toolbar) findViewById(R.id.tollbarConversation);
        toolbar.setTitle(paciente.getName());
        toolbar.setSubtitle(R.string.patient);
        toolbar.setNavigationIcon(R.drawable.ic_action_arrow_left);
        setSupportActionBar(toolbar);

        etMsg = (EditText) findViewById(R.id.et_conversation);

        btnSendMsg = (ImageButton) findViewById(R.id.btn_conversation);

        recyclerMessage = (RecyclerView) findViewById(R.id.rv_conversation);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerMessage.setLayoutManager(layoutManager);

        messageDAO = new MessageDAO(getApplicationContext());

        messages = getMessagesByReceptorId(paciente.getId());

        messageAdapter = new MessageAdapter(messages, user);

        recyclerMessage.setAdapter(messageAdapter);

        messageListener = new MessageListener();
        messageListener.addListener(this);

        btnSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = etMsg.getText().toString();
                Calendar currentDate = Calendar.getInstance();
                if(!(msg == "" || msg == null)){
                    Message txtMsg = new Message(user.getId(),
                            paciente.getId(),
                            paciente.getEmail(),
                            msg,
                            currentDate.get(Calendar.HOUR_OF_DAY) + ":" +
                                    currentDate.get(Calendar.MINUTE));

                    WebSocket.sendMessage(txtMsg);
                    saveMessage(txtMsg);
                    try{
                        messageDAO.insert(txtMsg);
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    etMsg.setText("");

                    scrollingRecyclerView();
                }
            }
        });
        scrollingRecyclerView();

    }

    private void scrollingRecyclerView(){
        recyclerMessage.scrollToPosition(messages.size() - 1);
    }

    @Override
    public void notify(MessageListener listener) {
        Message message = listener.getMessage();
        saveMessage(message);
    }

    private void refreshMessageAdapter(){
        // execute in main thread (UI)
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                messageAdapter.notifyDataSetChanged();
            }
        });
    }

    private List<Message> getMessagesByReceptorId(int receptorId){
        try {
            return messageDAO.findMessages(receptorId);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<Message>();
        }
    }

    private void saveMessage(Message msg){
        messages.add(msg);
        new Handler((Looper.getMainLooper())).post(new Runnable() {
            @Override
            public void run() {
                messageAdapter.notifyDataSetChanged();
                scrollingRecyclerView();
            }
        });
    }
}
