package com.fusi.fishingalarm.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.fusi.fishingalarm.R;
import com.fusi.fishingalarm.UILApplication;

/**
 * Created by dev on 2015/11/5.
 */
public class UIUtils {

    private static final String TAG = UIUtils.class.getSimpleName();
    /**
     * dip转换px
     */
    public static int dip2px(int dip) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    /**
     * pxz转换dip
     */
    public static int px2dip(int px) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public interface AlertConfirm {
        public void onConfirm(int tag, String content);

        public void onCancel();
    }

    public static Context getContext() {
        return UILApplication.getApplication();
    }

    public static String getPackageName() {
        return getContext().getPackageName();
    }

    // 隐藏软键盘
    public static void closeBoard(Activity a, Context mcontext) {
        InputMethodManager imm = (InputMethodManager) mcontext
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // imm.hideSoftInputFromWindow(myEditText.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(a.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static void showDialog(final Context context, int resId) {
        showDialog(context, resId, false);
    }


    /**
     * @param context
     * @param resId      dialog布局
     * @param cancelable dialog是否可点击取消
     */
    public static Dialog showDialog(final Context context, int resId, boolean cancelable) {
        Dialog dialog = new Dialog(context, R.style.loading_dialog);
        dialog.setContentView(resId);
        dialog.setCancelable(cancelable);
        dialog.show();
        return dialog;
    }

    // 提示对话框
    public static void showAlterDialog(Context context, String title, String alertContent) {
        try {
            String temp;
            temp = alertContent;
            final AlertDialog alertDialog = new AlertDialog.Builder(context)
                    .create();
            // Alertdialog对话框，设置点击其他位置不消失 ,必须先AlertDialog.Builder.create()之后才能调用
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
//			设置对话框宽度
            WindowManager manager = alertDialog.getWindow().getWindowManager();
            Display d = manager.getDefaultDisplay();
            WindowManager.LayoutParams params = alertDialog.getWindow().getAttributes();
            params.width = (int) (d.getWidth() * 0.85);
//			params.height=(int) (d.getHeight()*0.28);
            alertDialog.getWindow().setAttributes(params);
            Window window = alertDialog.getWindow();
            // *** 主要就是在这里实现这种效果的.
            // 设置窗口的内容页面,alert_dialog_main_title.xml文件中定义view内容
            View dialogView = View.inflate(context,
                    R.layout.alert_dialog_main_title, null);
            window.setContentView(dialogView);
            // 设置对应提示框内容
            TextView alert_dialog_content = (TextView) dialogView
                    .findViewById(R.id.alert_dialog_content);
            alert_dialog_content.setText(temp);

            TextView alert_dialog_title = (TextView) dialogView.findViewById(R.id.alert_dialog_title);
            if (TextUtils.isEmpty(title)) {
                alert_dialog_title.setVisibility(View.INVISIBLE);
            } else {
                alert_dialog_title.setText(title);
            }
            // 为知道了按钮添加事件，执行退出应用操作
            TextView tv_roger = (TextView) dialogView
                    .findViewById(R.id.tv_roger);
            tv_roger.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    alertDialog.cancel();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void createInputDialog(Context context, String title, AlertConfirm Iconfirm) {
        createInputDialog(context, title, null, 1, Iconfirm, -1);
    }

    public static void createInputDialog(Context context, String title, AlertConfirm Iconfirm, int tag) {
        createInputDialog(context, title, null, 1, Iconfirm, tag);
    }

    public static void createInputDialog(Context context, String title, String subtitle, AlertConfirm Iconfirm) {
        createInputDialog(context, title, subtitle, 1, Iconfirm, -1);
    }

    public static void createInputDialog(Context context, String title, String subtitle, int type, AlertConfirm Iconfirm) {
        createInputDialog(context, title, subtitle, type, Iconfirm, -1);
    }


    public static void createInputDialog(Context context, String title, String subtitle, int type, int tag) {
        createInputDialog(context, title, subtitle, type, null, tag);
    }


    //包含输入框的弹出对话框,title和subtitle如果不设置的话，可以为null,type=1，则为默认的情况，type=2，是如果用户输入密码，程序会保存密码
    public static void createInputDialog(final Context context, String title, String subtitle, final int type, final AlertConfirm confirmInterface, int tag) {
        Log.i(TAG, "updateName");
        final int userTag = tag;
        final AlertDialog updateNameDialog = new AlertDialog.Builder(
                context).create();
        // Alertdialog对话框，设置点击其他位置不消失 ,必须先AlertDialog.Builder.create()之后才能调用
        updateNameDialog.setCanceledOnTouchOutside(false);
        updateNameDialog.show();
        // 设置对话框宽度
        WindowManager manager = updateNameDialog.getWindow().getWindowManager();
        Display d = manager.getDefaultDisplay();
        WindowManager.LayoutParams params = updateNameDialog.getWindow()
                .getAttributes();
        params.width = (int) (d.getWidth() * 0.85);
        // params.height=(int) (d.getHeight()*0.28);
        Window window = updateNameDialog.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,alert_dialog_main_title.xml文件中定义view内容
        View dialogView = View.inflate(context,
                R.layout.alert_input_dialog, null);// 就是这个布局弄成之前一直错错错，空指针异常，老方法
        window.setContentView(dialogView);
        // 下面灰常有用，用于使键盘强制弹出
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        TextView titleView = (TextView) dialogView
                .findViewById(R.id.altert_title);
        if (title != null) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(title);
        } else {
            titleView.setVisibility(View.GONE);
        }
        titleView.setText(title);
        TextView subtitleView = (TextView) dialogView.findViewById(R.id.altert_subtitle);
        if (subtitle != null) {
            subtitleView.setVisibility(View.VISIBLE);
            subtitleView.setText(subtitle);
        } else {
            subtitleView.setVisibility(View.GONE);
        }
        // 确定
        TextView tv_ok = (TextView) dialogView.findViewById(R.id.tv_deleteConfig);

        TextView tv_cancel = (TextView) dialogView.findViewById(R.id.tv_deleteCancel);
        // 输入框
        final EditText et_myIbeaconName = (EditText) dialogView
                .findViewById(R.id.alert_input);


        tv_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String content = et_myIbeaconName.getText().toString();
                /*if (type == 2) {
                    if (!TextUtils.isEmpty(content)) {
                        content = String.format("%06d", Integer.valueOf(content));
                        SharedPreferences sp = getContext().getSharedPreferences("product-record-app", Context.MODE_PRIVATE);
                        if (!sp.getString("pollingPwd","").equals(content)) {
                            content = null;
                        }
                    }
                }*/
                Log.i(TAG, "tag=" + content);
                updateNameDialog.cancel();
                if (confirmInterface != null) confirmInterface.onConfirm(userTag, content);
            }
        });

        // 取消
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                updateNameDialog.cancel();
                if (confirmInterface != null) confirmInterface.onCancel();
            }
        });


        et_myIbeaconName.setInputType(InputType.TYPE_CLASS_TEXT
                | InputType.TYPE_TEXT_VARIATION_NORMAL);
        InputMethodManager imm;
        imm = (InputMethodManager) et_myIbeaconName.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }

    //    连接wifi
    //包含输入框的弹出对话框,title和subtitle如果不设置的话，可以为null,type=1，则为默认的情况，type=2，是如果用户输入密码，程序会保存密码
    public static void createInputMoreInfoDialog(final Context context, String dialogTitle,String title, String subtitle, final AlertConfirm confirmInterface, int tag) {
        Log.i(TAG, "createInputMoreInfoDialog");
        final int userTag = tag;
        final AlertDialog inputDialog = new AlertDialog.Builder(
                context).create();
        // Alertdialog对话框，设置点击其他位置不消失 ,必须先AlertDialog.Builder.create()之后才能调用
        inputDialog.setCanceledOnTouchOutside(false);
        inputDialog.show();
        // 设置对话框宽度
        WindowManager manager = inputDialog.getWindow().getWindowManager();
        Display d = manager.getDefaultDisplay();
        WindowManager.LayoutParams params = inputDialog.getWindow()
                .getAttributes();
        params.width = (int) (d.getWidth() * 0.85);
        // params.height=(int) (d.getHeight()*0.28);
        Window window = inputDialog.getWindow();
        // *** 主要就是在这里实现这种效果的.
        // 设置窗口的内容页面,alert_dialog_main_title.xml文件中定义view内容
        View dialogView = View.inflate(context,
                R.layout.dialog_wifi_contact, null);// 就是这个布局弄成之前一直错错错，空指针异常，老方法
        window.setContentView(dialogView);
        // 下面灰常有用，用于使键盘强制弹出
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        //wifi名字
        TextView dialogTitleView=(TextView) dialogView
                .findViewById(R.id.name);
        if (!TextUtils.isEmpty(dialogTitle)){
            dialogTitleView.setText(dialogTitle);
        }

        TextView titleView = (TextView) dialogView
                .findViewById(R.id.scan_strength);
        if (title != null) {
            titleView.setVisibility(View.VISIBLE);
            titleView.setText(title);
        } else {
            titleView.setVisibility(View.GONE);
        }
        titleView.setText(title);
        TextView subtitleView = (TextView) dialogView.findViewById(R.id.security);
        if (subtitle != null) {
            subtitleView.setVisibility(View.VISIBLE);
            subtitleView.setText(subtitle);
        } else {
            subtitleView.setVisibility(View.GONE);
        }
        // 确定
        TextView tv_ok = (TextView) dialogView.findViewById(R.id.ok);

        TextView tv_cancel = (TextView) dialogView.findViewById(R.id.cancel);
        // 输入框
        final EditText et_input = (EditText) dialogView
                .findViewById(R.id.input);


        tv_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                String content = et_input.getText().toString();
                /*if (type == 2) {
                    if (!TextUtils.isEmpty(content)) {
                        content = String.format("%06d", Integer.valueOf(content));
                        SharedPreferences sp = getContext().getSharedPreferences("product-record-app", Context.MODE_PRIVATE);
                        if (!sp.getString("pollingPwd","").equals(content)) {
                            content = null;
                        }
                    }
                }*/
                Log.i(TAG, "tag=" + content);
                inputDialog.cancel();
                if (confirmInterface != null) confirmInterface.onConfirm(userTag, content);
            }
        });

        // 取消
        tv_cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                inputDialog.cancel();
                if (confirmInterface != null) confirmInterface.onCancel();
            }
        });

// et_input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD
//                | InputType.TYPE_TEXT_VARIATION_NORMAL);
        InputMethodManager imm;
        imm = (InputMethodManager) et_input.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.SHOW_FORCED);
    }


    public interface CustomDialogClickListener{
        void leftClick();
        void rightClick();
    }

    /**
     * 自定义弹出对话框
     * @param context
     * @param content 显示内容
     * @param left 左边按钮显示的文字
     * @param right 右边按钮显示的文字
     * @param listener 左右按钮点击后的回调
     */
    public static void showCustomDialog(final Context context,String content,String left,String right, final CustomDialogClickListener listener) {

        try {
            final AlertDialog dialog = new AlertDialog.Builder(context)
                    .create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();
            WindowManager manager=dialog.getWindow().getWindowManager();
            Display d = manager.getDefaultDisplay();
            WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = (int) (d.getWidth() * 0.85);
            Window window = dialog.getWindow();
            window.setAttributes(params);
            View dialogView = View
                    .inflate(context, R.layout.dialog_ok_cancel, null);
            window.setContentView(dialogView);

            TextView contentTv=(TextView)dialogView.findViewById(R.id.content);
            contentTv.setText(content);
            Button left_bn = (Button) dialogView
                    .findViewById(R.id.left_bn);
            left_bn.setText(left);
            left_bn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if(listener!=null){
                        listener.leftClick();
                    }
                    dialog.cancel();
                }
            });

            // 为注册按钮添加事件,执行跳转到注册界面界面
            Button right_bn = (Button) dialogView
                    .findViewById(R.id.right_bn);
            right_bn.setText(right);
            right_bn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    if(listener!=null){
                        listener.rightClick();
                    }
                    dialog.cancel();
                }
            });
        } catch (Exception e) {
            Log.i(TAG, "create dialog fail");
            e.printStackTrace();
        }

    }

}
