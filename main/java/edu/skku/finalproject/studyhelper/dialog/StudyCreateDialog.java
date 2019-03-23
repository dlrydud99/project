package edu.skku.finalproject.studyhelper.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.skku.finalproject.studyhelper.R;
import edu.skku.finalproject.studyhelper.activity.MainActivity;
import edu.skku.finalproject.studyhelper.dto.Location;
import edu.skku.finalproject.studyhelper.dto.Member;
import edu.skku.finalproject.studyhelper.dto.Study;

import static android.content.Context.MODE_PRIVATE;

public class StudyCreateDialog extends Dialog implements View.OnClickListener{
    EditText studyName, nickName;
    TextView tvCreate, tvCancel;
    Context context;
    String locationName;
    LatLng startLatLng;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String sName;
    String myName;

    public StudyCreateDialog(@NonNull Context context, String locationName, LatLng startLatLng) {
        super(context);
        this.locationName = locationName;
        this.context = context;
        this.startLatLng = startLatLng;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_create);
        studyName = findViewById(R.id.ip_study_name_create);
        nickName = findViewById(R.id.ip_name_create);
        tvCreate = findViewById(R.id.tv_create);
        tvCancel = findViewById(R.id.tv_cancel_create);
        tvCreate.setOnClickListener(this);
        tvCancel.setOnClickListener(this);

        /* 기존 로그인 정보 가져오기 */
        pref = context.getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_cancel_create:
                cancel();
                break;
            case R.id.tv_create:
                sName = studyName.getText().toString();
                myName = nickName.getText().toString();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference myRef = database.getReference();
                final ArrayList<Member> members = new ArrayList<Member>();
                String latlng = Double.toString(startLatLng.latitude) + "," + Double.toString(startLatLng.longitude);
                Member member = new Member(myName,"불참", new Location(locationName, latlng), new Location(locationName, latlng));
                members.add(member);


                myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(sName)){
                            Toast.makeText(getContext(),"스터디가 이미 존재합니다. 다른 이름을 입력하세요.", Toast.LENGTH_LONG ).show();
                        }else{
                            myRef.child(sName).child("members").setValue(members)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            editor.putString("studyName",sName);
                                            editor.putString("myName",myName);
                                            editor.commit();
                                            Intent intent = new Intent(context, MainActivity.class);
                                            intent.putExtra("studyName",sName);
                                            intent.putExtra("myName",myName);
                                            Toast.makeText(getContext(),"스터디 생성 완료.", Toast.LENGTH_LONG ).show();
                                            context.startActivity(intent);
                                            ((Activity)context).finish();
                                        }
                                    });
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
                }


    }

    public static void setStudyRoom(String studyName, String memberName, String locationName, String latlng){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();
        ArrayList<Member> members = new ArrayList<>();
        ArrayList<Study> studies = new ArrayList<>();
        Member member = new Member(memberName,"불참", new Location(locationName,latlng), new Location(locationName, latlng));
        members.add(member);

    }
}