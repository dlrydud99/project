package edu.skku.finalproject.studyhelper.dto;

import java.util.Comparator;
import java.util.List;

public class Study {
    String date = "";
    Location location = null;
    List<Member> members = null;
    String status = "";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Study(String date, Location location, List<Member> member, String status ) {
        this.date = date;
        this.location = location;
        this.members = member;
        this.status = status;
    }

    public Study() {
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public static Comparator<Study> StuDateComparator = new Comparator<Study>() {
        public int compare (Study s1, Study s2){
            String date1=s1.getDate();
            String date2=s2.getDate();
            return date1.compareTo(date2);
        }
    };

}
