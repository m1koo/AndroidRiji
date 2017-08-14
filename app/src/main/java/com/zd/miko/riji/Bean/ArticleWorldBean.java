package com.zd.miko.riji.Bean;

import java.util.ArrayList;

/**
 * Created by Miko on 2017/8/11.
 */

public class ArticleWorldBean {
    private String userName;
    private String userId;
    private String articleId;
    private long editTime;
    private String content;
    private String title;
    private ArrayList<PreviewObj> imagePaths;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "ArticleWorldBean{" +
                "userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", articleId='" + articleId + '\'' +
                ", editTime=" + editTime +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", imagePaths=" + imagePaths +
                '}';
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public long getEditTime() {
        return editTime;
    }

    public void setEditTime(long editTime) {
        this.editTime = editTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public ArrayList<PreviewObj> getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(ArrayList<PreviewObj> imagePaths) {
        this.imagePaths = imagePaths;
    }
}
