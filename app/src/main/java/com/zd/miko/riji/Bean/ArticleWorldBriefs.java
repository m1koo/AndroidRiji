package com.zd.miko.riji.Bean;

import java.util.ArrayList;

/**
 * Created by Miko on 2017/8/19.
 */
public class ArticleWorldBriefs {
    private ArrayList<ArticleWorldBrief> articleWorldBriefs;

    @Override
    public String toString() {
        return "ArticleWorldBriefs{" +
                "articleWorldBriefs=" + articleWorldBriefs +
                '}';
    }

    public ArrayList<ArticleWorldBrief> getArticleWorldBriefs() {
        return articleWorldBriefs;
    }

    public void setArticleWorldBriefs(ArrayList<ArticleWorldBrief> articleWorldBriefs) {
        this.articleWorldBriefs = articleWorldBriefs;
    }
}
