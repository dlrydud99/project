package edu.skku.finalproject.studyhelper.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.skku.finalproject.studyhelper.R;
import edu.skku.finalproject.studyhelper.activity.MainActivity;
import edu.skku.finalproject.studyhelper.dto.Location;
import edu.skku.finalproject.studyhelper.dto.Member;
import edu.skku.finalproject.studyhelper.dto.Study;

import static android.content.Context.MODE_PRIVATE;


public class StudyEnterDialog extends Dialog implements View.OnClickListener{
    EditText studyName, name;
    TextView tvEnter, tvCancel;

    Context context;
    String locationName;
    LatLng startLatLng;
    String latlng;
    static ArrayList<Member> members;
    static ArrayList<Study> studies ;
    FirebaseDatabase database;
    DatabaseReference myRef;
    String sName;
    String myName;

    Geocoder geocoder;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    boolean isMember = false;
    private ProgressDialog progressDialog;

    public StudyEnterDialog(@NonNull Context context, String locationName, LatLng startLatLng) {
        super(context);
        this.locationName = locationName;
        this.context = context;
        this.startLatLng = startLatLng;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_enter);
        studyName = findViewById(R.id.ip_study_name_enter);
        name=findViewById(R.id.ip_name_enter);
        tvEnter = findViewById(R.id.tv_enter);
        tvCancel = findViewById(R.id.tv_cancel_enter);
        tvEnter.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        latlng = Double.toString(startLatLng.latitude) + "," + Double.toString(startLatLng.longitude);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();
        geocoder = new Geocoder(getContext());
        members = new ArrayList<Member>();
        studies = new ArrayList<Study>();

        /* 기존 로그인 정보 가져오기 */
        pref = context.getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_cancel_enter:
                cancel();
                break;
            case R.id.tv_enter:
                sName = studyName.getText().toString();
                myName = name.getText().toString();
                myRef.child(sName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChildren()) {
                            isMember = true;
                            GenericTypeIndicator<ArrayList<Study>> sT = new GenericTypeIndicator<ArrayList<Study>>() {
                            };
                            studies = dataSnapshot.child("/studies").getValue(sT);

                            GenericTypeIndicator<ArrayList<Member>> mT = new GenericTypeIndicator<ArrayList<Member>>() {
                            };
                            members = dataSnapshot.child("/members").getValue(mT);
                        }
                        setStudyRoom();

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });


                break;
        }

    }


    public void setStudyRoom(){

        Location location = new Location(locationName,Double.toString(startLatLng.latitude) + "," + Double.toString(startLatLng.longitude));

        Member m = new Member(name.getText().toString(),"불참", new Location(locationName, latlng), new Location(locationName, latlng));
        boolean isMember = false;
        if (members.size() == 0){
            members.add(m);
            Toast.makeText(getContext(),"스터디가 존재하지않습니다. 스터디를 새로 생성합니다.",Toast.LENGTH_LONG ).show();
        }else{
            for(Member member : members){
                if (member.getName().equals(m.getName())){ // 이미 있는 스터디원일 경우
                    if (locationName != ""){ //출발지를 지정한 경우
                        m.setStartLocation(location);
                        members.set(members.indexOf(member),m);
                        Toast.makeText(getContext(),"출발지를" + locationName +"으로 변경합니다.",Toast.LENGTH_LONG ).show();
                    }else{ // 출발지를 지정하지 않은 경우
                        Toast.makeText(getContext(),"기존 출발 장소인 "+ member.getStartLocation().getName() +"를 출발지로 지정합니다.",Toast.LENGTH_LONG ).show();
                    }
                    isMember = true;
                }
            }
            if (!isMember){ //처음 입장하는 경우
                members.add(m);
            }
        }

        if (studies != null){
            for (Study s : studies){
                if (!s.getStatus().equals("완료")){
                    s.setMembers(members);
                }
            }
            myRef.child(studyName.getText().toString()).child("studies").setValue(studies);
        }
        myRef.child(studyName.getText().toString()).child("members").setValue(members).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                editor.putString("studyName",sName);
                editor.putString("myName",myName);
                editor.commit();
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("studyName",sName);
                intent.putExtra("myName",myName);

                context.startActivity(intent);
                ((Activity)context).finish();
            }
        });
    }


}