package com.sidoso.profissional.listener;

import com.sidoso.profissional.interfaces.IMessage;
import com.sidoso.profissional.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageListener {
    private Message message;
    private static final List<IMessage> listeners = new ArrayList<IMessage>();

    private void notifyListeners() {
        for (IMessage message : listeners) {
            message.notify(this);
        }
    }
    public void addListener(IMessage message) {
        listeners.add(message);
    }
    public void removeListener(IMessage message) {
        listeners.remove(message);
    }
    public Message getMessage() {
        return message;
    }
    public void setMessage(Message msg) {
        this.message = msg;
        notifyListeners();
    }

}
