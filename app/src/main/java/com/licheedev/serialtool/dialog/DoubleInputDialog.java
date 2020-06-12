package com.licheedev.serialtool.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.licheedev.serialtool.R;


public class DoubleInputDialog extends Dialog {

    private EditText et_account;
    private EditText et_password;

    public DoubleInputDialog(@NonNull Context context) {
        super(context, R.style.Dialog_Base);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_double_confirm);

        et_account = findViewById(R.id.edit_account);
        et_password = findViewById(R.id.edit_password);

        TextView btn_confirm = findViewById(R.id.tv_confirm);
        btn_confirm.setOnClickListener(view -> {
            String account = et_account.getText().toString();
            if (TextUtils.isEmpty(account)) {
                et_account.requestFocus();
                return;
            }

            String password = et_password.getText().toString();
            if (TextUtils.isEmpty(password)) {
                et_password.requestFocus();
                return;
            }

            if (onConfirmClickListener != null) {
                onConfirmClickListener.onConfirm(account, password);
            }
            dismiss();
        });

        findViewById(R.id.tv_cancel).setOnClickListener(v -> dismiss());
    }

    public interface OnConfirmClickListener {
        void onConfirm(String account, String password);
    }

    private OnConfirmClickListener onConfirmClickListener;

    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        onConfirmClickListener = listener;
    }
}
