package com.templateapplication;

import android.app.Activity;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.*;

import com.dayou.dance.aidl.DanceAidl;

public class MainActivity extends Activity {

    TextView log;
    EditText input;
    DanceAidl dance;
    ServiceConnection conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        input = new EditText(this);
        input.setHint("动作编号，例如 1");
        input.setText("1");

        Button bind = new Button(this);
        bind.setText("1) 连接机器人服务");

        Button start = new Button(this);
        start.setText("2) startDance(type)");

        Button stop = new Button(this);
        stop.setText("3) stopDance()");

        log = new TextView(this);
        log.setText("日志：\n");

        root.addView(input);
        root.addView(bind);
        root.addView(start);
        root.addView(stop);
        root.addView(log);

        setContentView(root);

        bind.setOnClickListener(v -> bindRobot());
        start.setOnClickListener(v -> startDance());
        stop.setOnClickListener(v -> stopDance());
    }

    void bindRobot() {
        append("开始绑定服务...");

        Intent intent = new Intent();
        intent.setClassName(
                "com.gdkj.robot.gdrobotmanagerservice",
                "com.gdkj.robot.gdrobotmanagerservice.service.GDRobotMangagerService"
        );

        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                append("绑定成功: " + name);
                dance = DanceAidl.Stub.asInterface(service);
                append(dance == null ? "❌ Binder不是DanceAidl" : "✅ 已获取DanceAidl接口");
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                append("服务断开");
                dance = null;
            }
        };

        boolean ok = false;
        try {
            ok = bindService(intent, conn, BIND_AUTO_CREATE);
        } catch (Throwable t) {
            append("❌ bindService异常: " + t);
        }
        append("bindService 返回: " + ok);
    }

    void startDance() {
        if (dance == null) {
            append("请先连接服务");
            return;
        }
        int type = 1;
        try {
            type = Integer.parseInt(input.getText().toString().trim());
        } catch (Exception ignored) {}

        try {
            append("调用 startDance(" + type + ")");
            dance.startDance(type);
        } catch (Throwable t) {
            append("❌ startDance异常: " + t);
        }
    }

    void stopDance() {
        if (dance == null) {
            append("请先连接服务");
            return;
        }
        try {
            append("调用 stopDance()");
            dance.stopDance();
        } catch (Throwable t) {
            append("❌ stopDance异常: " + t);
        }
    }

    void append(String s) {
        log.append(s + "\n");
    }
}
