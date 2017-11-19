package com.example.teohe.studentaid;

/**
 * Created by Thomas on 19/11/2017.
 */

public class Timeslot
{
    private String moduleName;
    private String classType;
    private String lecturerName;
    private String room;
    private int dayOfTheWeek;
    private int slot;

    public Timeslot(String moduleName, String classType, String lecturerName, String room, int dayOfTheWeek, int slot)
    {
        this.moduleName = moduleName;
        this.classType = classType;
        this.lecturerName = lecturerName;
        this.room = room;
        this.dayOfTheWeek = dayOfTheWeek;
        this.slot = slot;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getLecturerName() {
        return lecturerName;
    }

    public void setLecturerName(String lecturerName) {
        this.lecturerName = lecturerName;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getDayOfTheWeek() {
        return dayOfTheWeek;
    }

    public void setDayOfTheWeek(int dayOfTheWeek) {
        this.dayOfTheWeek = dayOfTheWeek;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }
}
