package com.facewarrant.fw.bean;

import java.util.List;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class AnswerDetailBean {
    private String mTopUrl;
    private String mName;
    private String mTime;
    private String mContent;
    private List<GoodsBean> mList;

    public String getTopUrl() {
        return mTopUrl;
    }

    public void setTopUrl(String topUrl) {
        mTopUrl = topUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public List<GoodsBean> getList() {
        return mList;
    }

    public void setList(List<GoodsBean> list) {
        mList = list;
    }
}
