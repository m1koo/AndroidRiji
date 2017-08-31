package com.zd.miko.riji.Bean.RealmBean;

import io.realm.RealmObject;

/**
 * Created by Miko on 2017/8/11.
 */

public class RealmArticleWorld extends RealmObject {
    private String title;
    private String userName;
    private String userId;
    private String articleId;
    private long shareTime;
    private String content;
    private String imagePaths;
    private boolean hadRead;
    private String readUserId;

    public String getReadUserId() {
        return readUserId;
    }

    public void setReadUserId(String readUserId) {
        this.readUserId = readUserId;
    }

    public boolean isHadRead() {
        return hadRead;
    }

    public void setHadRead(boolean hadRead) {
        this.hadRead = hadRead;
    }

    @Override
    public String toString() {
        return "RealmArticleWorld{" +
                "title='" + title + '\'' +
                ", userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", articleId='" + articleId + '\'' +
                ", shareTime=" + shareTime +
                ", content='" + content + '\'' +
                ", imagePaths='" + imagePaths + '\'' +
                '}';
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public long getShareTime() {
        return shareTime;
    }

    public void setShareTime(long shareTime) {
        this.shareTime = shareTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImagePaths() {
        return imagePaths;
    }

    public void setImagePaths(String imagePaths) {
        this.imagePaths = imagePaths;
    }
}
