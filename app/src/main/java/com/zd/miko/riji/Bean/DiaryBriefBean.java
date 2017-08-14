package com.zd.miko.riji.Bean;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Miko on 2017/7/7.
 */

public class DiaryBriefBean {

    private int type;
    private String articleId;
    private String briefContent;
    private String location;
    private ArrayList<PreviewObj> previewPaths;
    private String month;
    private String year;
    private String day;
    private String week;
    private java.util.Date date;

    public ArrayList<PreviewObj> getPreviewPaths() {
        return previewPaths;
    }

    public void setPreviewPaths(ArrayList<PreviewObj> previewPaths) {
        this.previewPaths = previewPaths;
    }

    public String getBriefContent() {
        return briefContent;
    }

    public void setBriefContent(String briefContent) {
        this.briefContent = briefContent;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
