package com.sidoso.profissional.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.sidoso.profissional.database.DataBase;
import com.sidoso.profissional.database.MessageReadContract;
import com.sidoso.profissional.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageDAO extends DataBase {

    public MessageDAO(Context context) {
        super(context);
    }

    public void insert(Message msg) throws Exception {
        ContentValues values = new ContentValues();

        values.put(MessageReadContract.MessageEntry.COLUMN_SENDER_ID, msg.getSenderId());
        values.put(MessageReadContract.MessageEntry.COLUMN_RECEPTOR_ID, msg.getReceptorId());
        values.put(MessageReadContract.MessageEntry.COLUMN_RECEPTOR_EMAIL, msg.getReceptorEmail());
        values.put(MessageReadContract.MessageEntry.COLUMN_MESSAGE, msg.getMessage());
        values.put(MessageReadContract.MessageEntry.COLUMN_SENDER_AT, msg.getSenderAt());

        getDataBase().insert(MessageReadContract.MessageEntry.TABLE_NAME, null, values);
    }

    public List<Message> findMessages(Integer receptorId) throws Exception {
        String sql = "SELECT * FROM " + MessageReadContract.MessageEntry.TABLE_NAME + " WHERE " +
                MessageReadContract.MessageEntry.COLUMN_SENDER_ID + " = ?" +
                " OR " + MessageReadContract.MessageEntry.COLUMN_RECEPTOR_ID + " = ?";
        String[] selectionArgs = new String[]{"" + receptorId, "" + receptorId};
        Cursor cursor = getDataBase().rawQuery(sql, selectionArgs);
        cursor.moveToFirst();

        return getMessages(cursor);
    }

    private List<Message> getMessages(Cursor cursor) throws Exception {
        List<Message> messages = new ArrayList<Message>();
        Message message;

        while(!cursor.isAfterLast()){
            // Integer id           = cursor.getInt(cursor.getColumnIndex(MessageReadContract.MessageEntry.COLUMN_ID));
            Integer senderId     = cursor.getInt(cursor.getColumnIndex(MessageReadContract.MessageEntry.COLUMN_SENDER_ID));
            Integer receptorId   = cursor.getInt(cursor.getColumnIndex(MessageReadContract.MessageEntry.COLUMN_RECEPTOR_ID));
            String receptorEmail = cursor.getString(cursor.getColumnIndex(MessageReadContract.MessageEntry.COLUMN_RECEPTOR_EMAIL));
            String msgs          = cursor.getString(cursor.getColumnIndex(MessageReadContract.MessageEntry.COLUMN_MESSAGE));
            String senderAt      = cursor.getString(cursor.getColumnIndex(MessageReadContract.MessageEntry.COLUMN_SENDER_AT));

            message = new Message(senderId, receptorId, receptorEmail, msgs, senderAt);

            messages.add(message);
            cursor.moveToNext();
        }

        return messages;
    }

    public void deleteAllMessages(Integer receptorId) throws  Exception {
        getDataBase().delete(MessageReadContract.MessageEntry.TABLE_NAME,
                MessageReadContract.MessageEntry.COLUMN_SENDER_ID + " = ? " +
                        " OR " +
                        MessageReadContract.MessageEntry.COLUMN_RECEPTOR_ID + " = ? "
                , new String[]{receptorId+"", receptorId+""});
    }
}

