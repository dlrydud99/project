package edu.skku.finalproject.studyhelper.dialog;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.util.Calendar;

import edu.skku.finalproject.studyhelper.R;
import edu.skku.finalproject.studyhelper.activity.MainActivity;
import edu.skku.finalproject.studyhelper.service.MyService;

import static edu.skku.finalproject.studyhelper.activity.MainActivity.me;
import static edu.skku.finalproject.studyhelper.activity.MainActivity.pagerAdapter;
import static edu.skku.finalproject.studyhelper.activity.MainActivity.sName;

public class StudyAddDialog extends Dialog implements View.OnClickListener {

    Context context;
    EditText tvDate, tvTime;
    Button btnDate, btnTime, btnOk, btnNo;

    int myear, mmonth, mday, mhour, mminute;

    View.OnClickListener myOnClickListener;

    public int getYear(){
        return myear;
    }

    public int getMonth(){
        return mmonth;
    }

    public int getDay(){
        return mday;
    }

    public int getHour(){
        return mhour;
    }

    public int getMinute(){
        return mminute;
    }

    public StudyAddDialog(@NonNull Context context) {
        super(context);
        this.context=context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_add);
        tvDate=findViewById(R.id.date);
        tvTime=findViewById(R.id.time);
        btnDate=findViewById(R.id.btn_date);
        btnTime=findViewById(R.id.btn_time);
        btnOk=findViewById(R.id.btn_ok);
        btnNo=findViewById(R.id.btn_no);
        btnOk.setOnClickListener(this);
        btnNo.setOnClickListener(this);

        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePicker();
            }
        });

    }

    private void datePicker(){

        final Calendar c = Calendar.getInstance();
        myear=c.get(Calendar.YEAR);
        mmonth=c.get(Calendar.MONTH);
        mday=c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        myear=year;
                        mmonth=month+1;
                        Log.d("month: ", mmonth+"");
                        mday=dayOfMonth;
                        tvDate.setText(year+"년 "+(month+1)+"월 "+dayOfMonth+"일");
                    }
                }, myear, mmonth, mday);
        datePickerDialog.show();
    }

    private void timePicker(){
        final Calendar c = Calendar.getInstance();
        mhour=c.get(Calendar.HOUR_OF_DAY);
        mminute=c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        mhour=hourOfDay;
                        mminute=minute;
                        tvTime.setText(hourOfDay+"시 "+minute+"분");
                    }
                }, mhour, mminute, true);
        timePickerDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_no:
                cancel();
                break;
            case R.id.btn_ok:
                // DB에 저장

                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("studyName",sName);
                intent.putExtra("myName", me.getName());
                context.startActivity(intent);
                ((Activity)context).finish();
                
                if (!isServiceRunning()){
                    context.startService(new Intent(context,MyService.class));
                }
                cancel();
                if(myOnClickListener != null) {
                    myOnClickListener.onClick(v);
                }
                break;
        }
    }

    public void setMyOnClickListener(View.OnClickListener onClickListener) {
        this.myOnClickListener = onClickListener;
    }

    public boolean isServiceRunning()
    {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (MyService.class.getName().equals(service.service.getClassName()))
                return true;
        }
        return false;
    }


}
