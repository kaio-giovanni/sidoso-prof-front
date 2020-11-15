package com.sidoso.profissional.websocket;

import android.content.Context;
import android.util.Log;

import com.sidoso.profissional.dao.MessageDAO;
import com.sidoso.profissional.listener.MessageListener;
import com.sidoso.profissional.model.Message;
import com.sidoso.profissional.model.Profissional;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import static com.sidoso.profissional.config.Constants.API_URL;
import static com.sidoso.profissional.config.Constants.CLIENT_JOIN;
import static com.sidoso.profissional.config.Constants.SEND_PRIVATE_MSG;
import static com.sidoso.profissional.config.Constants.RECEIVE_PRIVATE_MSG;

public class WebSocket {

    private static Socket socket;
    private static MessageListener messageListener;
    private Profissional profissional;
    private MessageDAO messageDAO;

    public WebSocket(Profissional profissional, Context ctx){
        this.profissional = profissional;
        this.messageDAO = new MessageDAO(ctx);

        if(messageListener == null){
            messageListener = new MessageListener();
        }

        try{
            connectSocket(profissional);
        }catch (URISyntaxException e){
            Log.e("SocketIOError", e.getMessage());
        }
    }

    private void connectSocket(final Profissional profissional) throws URISyntaxException {
        socket = IO.socket(API_URL);
        socket.on(io.socket.client.Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                JSONObject object = new JSONObject();
                Boolean join = false;
                try {
                    object.put("sysId", profissional.getId());
                    object.put("email", profissional.getEmail());
                    object.put("name", profissional.getName());

                    join = true;

                }catch(JSONException e){
                    join = false;
                }finally {
                    if(join){
                        socket.emit(CLIENT_JOIN, object);
                    }else {
                        Log.e("SocketIOErrorJoined", "User not joined");
                    }
                }
            }

        }).on(RECEIVE_PRIVATE_MSG, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject object = (JSONObject)args[0];

                try {
                    Message message = new Message(object.getInt("sender"),
                            object.getInt("receptor"),
                            object.getString("rec_email"),
                            object.getString("message"),
                            object.getString("sender_at"));

                    messageListener.setMessage(message);
                    messageDAO.insert(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        socket.connect();
    }

    public static boolean sendMessage(Message msg){
        if(!socket.connected()){
            return false;
        }
        JSONObject object = new JSONObject();
        try {
            object.put("sender", msg.getSenderId());
            object.put("receptor", msg.getReceptorId());
            object.put("rec_email", msg.getReceptorEmail());
            object.put("message", msg.getMessage());
            object.put("sender_at", msg.getSenderAt());

            socket.emit(SEND_PRIVATE_MSG, object);
            return true;
        }catch (JSONException e){
            return false;
        }
    }

    public void disconnectSocket(){
        if(socket.connected()){
            socket.disconnect();
        }
    }
}
