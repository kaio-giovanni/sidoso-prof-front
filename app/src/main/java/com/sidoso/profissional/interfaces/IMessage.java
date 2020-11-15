package com.sidoso.profissional.interfaces;

import com.sidoso.profissional.listener.MessageListener;

public interface IMessage {
    public void notify(MessageListener listener);
}
