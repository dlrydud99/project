package edu.skku.finalproject.studyhelper.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.skku.finalproject.studyhelper.R;
import edu.skku.finalproject.studyhelper.activity.MainActivity;
import static edu.skku.finalproject.studyhelper.activity.MainActivity.thisWeekStudy;

public class StudyListFragment extends Fragment {
    Context mContext;
    TextView textView1, textView2, textView3;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v=inflater.inflate(R.layout.fragment_study_list, container, false);


        textView1=(TextView)  v.findViewById(R.id.text_study_list_1);
        textView2=(TextView)  v.findViewById(R.id.text_study_list_2);
        textView3=(TextView)  v.findViewById(R.id.text_study_list_3);



        if (thisWeekStudy != null) {
            if (MainActivity.studies.indexOf(thisWeekStudy) == -1){
                textView1.setText("");
                textView2.setText("새로운 스터디를\n추가해주세요.");
                textView3.setText("");
            }else{
                textView1.setText(Integer.toString(MainActivity.studies.indexOf(thisWeekStudy) + 1)+"번째 스터디");
                textView2.setText((thisWeekStudy.getDate()));
                textView3.setText((thisWeekStudy.getLocation().getName()));
            }

        }
        else { //스터디 생성해야함
            textView1.setText("");
            textView2.setText("새로운 스터디를\n추가해주세요.");
            textView3.setText("");
        }

        return v;
    }

}
