package com.example.teohe.studentaid;

import android.graphics.Bitmap;

/**
 * Created by TeoHe on 15/11/2017.
 */

public class Profile
{
    private String firstName;
    private String lastName;
    private String collegeName;
    private String courseTitle;
    private Bitmap image;

    public Profile(String firstName, String lastName, String collegeName, String courseTitle, Bitmap image)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.collegeName = collegeName;
        this.courseTitle = courseTitle;
        this.image = image;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }
}
