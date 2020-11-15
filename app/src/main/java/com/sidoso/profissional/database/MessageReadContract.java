package com.sidoso.profissional.database;

import android.provider.BaseColumns;

public class MessageReadContract {

    // Para previnir que a classe de contrato seja instanciada acidentalmente
    // o construtor deve ser mantido privado
    private MessageReadContract(){}

    public static class MessageEntry implements BaseColumns {
        public static final String TABLE_NAME            = "messages";
        public static final String COLUMN_ID             = "id";
        public static final String COLUMN_SENDER_ID      = "sender_id";
        public static final String COLUMN_RECEPTOR_ID    = "receptor_id";
        public static final String COLUMN_RECEPTOR_EMAIL = "receptor_email";
        public static final String COLUMN_MESSAGE        = "message";
        public static final String COLUMN_SENDER_AT      = "sender_at";
    }
}

