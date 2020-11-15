package com.sidoso.profissional.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

public abstract class MaskEditText {

    public static final String FORMAT_PHONE = "(###)#####-####";
    public static final String FORMAT_DATE  = "##-##-####";
    public static final String FORMAT_HOUR  = "##:##";
    public static final String FORMAT_CPF   = "###.###.###-##";
    public static final String FORMAT_CEP   = "#####-###";

    public static TextWatcher mask(final EditText editText, final String maskFormat){
        return new TextWatcher() {
            boolean isUpdating;
            String old = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final String text = MaskEditText.unmask(s.toString());
                String mask = "";
                if(isUpdating){
                    old = text;
                    isUpdating = false;
                    return;
                }
                int i = 0;
                for(final char m: maskFormat.toCharArray()){
                    if(m != '#' && text.length() > old.length()){
                        mask += m;
                        continue;
                    }
                    try {
                        mask += text.charAt(i);
                    }catch (final Exception e){
                        break;
                    }
                    i++;
                }
                isUpdating = true;
                editText.setText(mask);
                editText.setSelection(mask.length());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
    }

    public static String unmask(final String str){
        return str.replaceAll("[.]", "").
                replaceAll("[-]", "").
                replaceAll("[/]", "").
                replaceAll("[(]", "").
                replaceAll("[ ]", "").
                replaceAll("[:]", "").
                replaceAll("[)]", "");
    }

}
