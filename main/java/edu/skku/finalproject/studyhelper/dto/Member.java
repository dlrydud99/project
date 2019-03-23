package edu.skku.finalproject.studyhelper.dto;

import java.util.Comparator;

public class Member {

    String name = "";
    String att = "";
    Location startLocation = null;
    Location currentLocation=null;
    public void setCurrentLocation(Location currentLocation){this.currentLocation = currentLocation;}
    public Location getCurrentLocation(){return currentLocation;}
    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Member() { }

    public Member( String name, String att, Location loc, Location currentLocation) {

        this.name = name;
        this.att = att;
        this.startLocation = loc;
        this.currentLocation = currentLocation;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAtt() {
        return att;
    }

    public void setAtt(String att) {
        this.att = att;
    }

    @Override
    public String toString() {
        return "Member{" +
                "name='" + name + '\'' +
                ", att='" + att + '\'' +
                ", startLocation=" + startLocation +
                ", currentLocation=" + currentLocation +
                '}';
    }

    public static Comparator<Member> StuNameComparator = new Comparator<Member>() {
        public int compare (Member m1, Member m2){
            String name1=m1.getName();
            String name2=m2.getName();
            return name1.compareTo(name2);
        }
    };
}

