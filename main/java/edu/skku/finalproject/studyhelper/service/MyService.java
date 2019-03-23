package edu.skku.finalproject.studyhelper.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import edu.skku.finalproject.studyhelper.R;
import edu.skku.finalproject.studyhelper.activity.MainActivity;
import edu.skku.finalproject.studyhelper.activity.StartActivity;
import edu.skku.finalproject.studyhelper.dto.Member;
import edu.skku.finalproject.studyhelper.dto.Study;

public class MyService extends Service {
    NotificationManager notificationManager;
    Notification notification;
    ServiceThread thread;
    double lat, lng;
    Study thisweekStudy;
    ArrayList<Member> members;
    ArrayList<Study> studies;
    Member me;
    String sName;
    Geocoder geocoder;
    LocationManager locationManager;
    Boolean isTenminuteNotied = false;
    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) { // location이 변했을 때
            lat=location.getLatitude();
            lng =location.getLongitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public MyService() {
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate() {
        geocoder = new Geocoder(this);
        /* location */
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
        Location lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastLocation != null){
            lat = lastLocation.getLatitude();
            lng = lastLocation.getLongitude();
        }else{
            lat = 37.5609775;
            lng = 126.9664153;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        thisweekStudy = MainActivity.thisWeekStudy;
        members = MainActivity.members;
        me = MainActivity.me;
        sName = MainActivity.sName;
        studies = MainActivity.studies;

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        @SuppressLint("HandlerLeak") Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                Intent intent = new Intent(MyService.this, StartActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
                String notificationMessage = "";
                thisweekStudy = MainActivity.thisWeekStudy;
                members = MainActivity.members;
                me = MainActivity.me;
                sName = MainActivity.sName;
                studies = MainActivity.studies;

                String locationName ="";
                Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.KOREA);
                try {
                    locationName = geocoder.getFromLocation(lat,lng,1).get(0).getAddressLine(0).toString();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                switch (msg.what){
                    // 10분 전 알림일 때
                    case 1:  // 현 위치 갱신
                        Log.d("kwang 위치갱신:",locationName);
                        for (Member m : members){
                            if(m.getName().equals(me.getName())){
                                m.setCurrentLocation(new edu.skku.finalproject.studyhelper.dto.Location(locationName,lat+","+lng));
                            }
                        }
                        for (Study s : studies){
                            if (s.equals(thisweekStudy)){
                                s.setMembers(members);
                            }
                        }

                        MainActivity.updateDB(sName,studies,members);
                        notificationMessage = "스터디 10분 전입니다. 스터디 장소는" + thisweekStudy.getLocation().getName() + "입니다.";
                        if (!isTenminuteNotied){
                            notification = new Notification.Builder(getApplicationContext())
                                    .setContentTitle("스터디 알림")
                                    .setContentText(notificationMessage)
                                    .setSmallIcon(R.drawable.logo)
                                    .setTicker("알림!!!")
                                    .setContentIntent(pendingIntent)
                                    .build();
                            //확인하면 자동으로 알림이 제거 되도록
                            notification.flags = Notification.FLAG_AUTO_CANCEL;

                            notificationManager.notify( 777 , notification);
                        }

                        isTenminuteNotied = true;
                        break;

                    // 스터디 시간 지남
                    case -1: //출석 확인
                        Log.d("kwang 출첵:",locationName);

                        for (Member m : members){
                            if(m.getName().equals(me.getName())){
                                m.setCurrentLocation(new edu.skku.finalproject.studyhelper.dto.Location(locationName,lat+","+lng));
                                if (checkLocation(m)){
                                    m.setAtt("출석");
                                    notificationMessage = "스터디가 시작되었습니다. 출석처리되었습니다.";

                                }else{
                                    m.setAtt("결석");
                                    notificationMessage = "스터디가 시작되었습니다. 스터디 장소에 도착하지않아 결석처리됩니다.";
                                }
                            }else {
                                if (checkLocation(m)){
                                    m.setAtt("출석");
                                }else{
                                    m.setAtt("결석");
                                }
                            }

                        }
                        thisweekStudy.setStatus("완료");
                        thisweekStudy.setMembers(members);

                        for (int idx = 0; idx < studies.size() ; idx++) {
                            if(studies.get(idx).getDate().equals(thisweekStudy.getDate())){
                                studies.set(idx,thisweekStudy);
                            }
                        }

                        MainActivity.updateDB(sName,studies,members);
                        notification = new Notification.Builder(getApplicationContext())
                                .setContentTitle("스터디 알림")
                                .setContentText(notificationMessage)
                                .setSmallIcon(R.drawable.logo)
                                .setTicker("알림!!!")
                                .setContentIntent(pendingIntent)
                                .build();
                        //확인하면 자동으로 알림이 제거 되도록
                        notification.flags = Notification.FLAG_AUTO_CANCEL;

                        notificationManager.notify( 777 , notification);
                        stopService(new Intent(getApplicationContext(),MyService.class));
                        if (MainActivity.pagerAdapter != null){
                            MainActivity.pagerAdapter.notifyDataSetChanged();
                        }
                        break;
                    case 2:
                        Log.d("kwang 위치갱신:",locationName);
                        for (Member m : members){
                            if(m.getName().equals(me.getName())){
                                m.setCurrentLocation(new edu.skku.finalproject.studyhelper.dto.Location(locationName,lat+","+lng));
                            }
                        }
                        for (Study s : studies){
                            if (s.equals(thisweekStudy)){
                                s.setMembers(members);
                            }
                        }

                        MainActivity.updateDB(sName,studies,members);
                        break;

                }



            }
        };
        thread = new ServiceThread(handler);
        thread.start();
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }


    public boolean checkLocation(Member m){
        Location studylocation = new Location("studylocation");
        studylocation.setLatitude(Double.valueOf(thisweekStudy.getLocation().getLatlng().split(",")[0]));
        studylocation.setLongitude(Double.valueOf(thisweekStudy.getLocation().getLatlng().split(",")[1]));
        Log.d("studylocation:",studylocation.getLatitude() + "/" + studylocation.getLongitude());


        Location myLocation = new Location("mylocation");
        myLocation.setLatitude(Double.valueOf(m.getCurrentLocation().getLatlng().split(",")[0]));
        myLocation.setLongitude(Double.valueOf(m.getCurrentLocation().getLatlng().split(",")[1]));
        Log.d("mylocation:",myLocation.getLatitude() + "/" + myLocation.getLongitude());
        double distance = studylocation.distanceTo(myLocation);
        Log.d("distance is:",""+distance);
        if (distance < 50.0){
            return true;
        }else{
            return false;
        }

    }
//    private class MyServiceHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            Intent intent = new Intent(MyService.this, StartActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(MyService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
//            String notificationMessage = "";
//            thisweekStudy = MainActivity.thisWeekStudy;
//            members = MainActivity.members;
//            me = MainActivity.me;
//            sName = MainActivity.sName;
//            studies = MainActivity.studies;
//            switch (msg.what){
//                // 10분 전 알림일 때
//                case 1:  // 현 위치 갱신
//                    String locationName ="";
//                    try {
//                        locationName = geocoder.getFromLocation(lat,lng,1).get(0).getThoroughfare();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//
//                    Log.d("kwang 위치갱신:",locationName);
//                    for (Member m : members){
//                        if(m.getName().equals(me.getName())){
//                            m.setCurrentLocation(new edu.skku.finalproject.studyhelper.dto.Location(locationName,lat+","+lng));
//                        }
//                    }
//                    for (Study s : studies){
//                        if (s.equals(thisweekStudy)){
//                            s.setMembers(members);
//                        }
//                    }
////                    Toast.makeText(MyService.this, "위치갱신", Toast.LENGTH_LONG).show();
//                    MainActivity.updateDB(sName,studies,members);
//                    notificationMessage = "스터디 10분 전입니다. 스터디 장소는" + thisweekStudy.getLocation().getName() + "입니다.";
//                    break;
//
//                // 스터디 시간 지남
//                case -1: //출석 확인
//                    me.setStartLocation();
//                    break;
//            }
//            notification = new Notification.Builder(getApplicationContext())
//                    .setContentTitle("스터디 알림")
//                    .setContentText(notificationMessage)
//                    .setSmallIcon(R.drawable.logo)
//                    .setTicker("알림!!!")
//                    .setContentIntent(pendingIntent)
//                    .build();
//
//
//            //확인하면 자동으로 알림이 제거 되도록
//            notification.flags = Notification.FLAG_AUTO_CANCEL;
//
//            notificationManager.notify( 777 , notification);
//            //토스트 띄우기
//
//        }
//    }
}
