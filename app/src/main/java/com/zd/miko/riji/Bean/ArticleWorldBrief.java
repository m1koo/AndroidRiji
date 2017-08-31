package com.zd.miko.riji.Bean;

/**
 * Created by Miko on 2017/8/19.
 */
public class ArticleWorldBrief {
    private String userName;
    private String userId;
    private String articleId;
    private long shareTime;
    private String content;
    private String title;
    private String previewPaths;

    @Override
    public String toString() {
        return "ArticleWorldBrief{" +
                "userName='" + userName + '\'' +
                ", userId='" + userId + '\'' +
                ", articleId='" + articleId + '\'' +
                ", shareTime=" + shareTime +
                ", content='" + content + '\'' +
                ", title='" + title + '\'' +
                ", previewPaths='" + previewPaths + '\'' +
                '}';
    }

    public String getPreviewPaths() {
        return previewPaths;
    }

    public void setPreviewPaths(String previewPaths) {
        this.previewPaths = previewPaths;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
