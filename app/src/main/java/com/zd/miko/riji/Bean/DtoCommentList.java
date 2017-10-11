package com.zd.miko.riji.Bean;

import java.util.List;

/**
 * Created by Miko on 2017/9/15.
 */
public class DtoCommentList {
    List<DtoCommentBean> commentList;

    @Override
    public String toString() {
        return "DtoCommentList{" +
                "commentList=" + commentList +
                '}';
    }

    public List<DtoCommentBean> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<DtoCommentBean> commentList) {
        this.commentList = commentList;
    }
}
