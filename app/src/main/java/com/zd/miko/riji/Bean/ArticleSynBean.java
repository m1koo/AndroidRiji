package com.zd.miko.riji.Bean;

import java.util.List;

/**
 * Created by Miko on 2017/8/26.
 */
public class ArticleSynBean {
    List<String> prepareDownloadArticleIds;
    List<String> prepareUploadArticleIds;

    public List<String> getPrepareDownloadArticleIds() {
        return prepareDownloadArticleIds;
    }

    public void setPrepareDownloadArticleIds(List<String> prepareDownloadArticleIds) {
        this.prepareDownloadArticleIds = prepareDownloadArticleIds;
    }

    public List<String> getPrepareUploadArticleIds() {
        return prepareUploadArticleIds;
    }

    public void setPrepareUploadArticleIds(List<String> prepareUploadArticleIds) {
        this.prepareUploadArticleIds = prepareUploadArticleIds;
    }
}
