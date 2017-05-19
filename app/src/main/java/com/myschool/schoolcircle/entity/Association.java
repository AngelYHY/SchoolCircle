package com.myschool.schoolcircle.entity;

/**
 * Created by Mr.R on 2016/7/12.
 */
public class Association {
    private String name;
    private String information;
    private int associationHeadId;

    public Association(String name, String information, int associationHeadId) {
        this.name = name;
        this.information = information;
        this.associationHeadId = associationHeadId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public int getAssociationHeadId() {
        return associationHeadId;
    }

    public void setAssociationHeadId(int associationHeadId) {
        this.associationHeadId = associationHeadId;
    }
}
