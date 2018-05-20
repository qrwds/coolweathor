package com.coolweather.android.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Calendar;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;
import com.coolweather.android.CoolweatherAppWidgetReceiver;
import com.coolweather.android.R;

/**
 * 更新小组件事件的服务
 *
 * @author lgl
 *
 */
public class ClockService extends Service {

    // 定时器
    private Timer timer;
    // 日期格式
    private SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日 ");
    private SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
    private Calendar calendar=Calendar.getInstance();
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        timer = new Timer();
        /**
         * 参数：1.事件2.延时事件3.执行间隔事件
         */
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                updateView();
            }
        }, 0, 1000*60);
    }

    /**
     * 更新事件的方法
     */
    private void updateView() {
        // 时间
        String[] weeks={"","周日","周一","周二","周三","星期四","星期五","星期六"};
        String weekkStr=weeks[calendar.get(Calendar.DAY_OF_WEEK)];
        String time = sdf1.format(new Date());
        String date=sdf.format(new Date())+weekkStr;
        /**
         * 参数：1.包名2.小组件布局
         */
        RemoteViews rViews = new RemoteViews(getPackageName(), R.layout.coolweather_widget_layout);
        // 显示当前事件
        rViews.setTextViewText(R.id.widget_time, time);
        rViews.setTextViewText(R.id.widget_date, date);
        // 刷新
        AppWidgetManager manager = AppWidgetManager
                .getInstance(this);
        ComponentName cName = new ComponentName(this,CoolweatherAppWidgetReceiver.class);
        manager.updateAppWidget(cName, rViews);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        timer = null;
    }

}
