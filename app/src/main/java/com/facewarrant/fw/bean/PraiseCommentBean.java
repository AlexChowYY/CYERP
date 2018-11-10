package com.facewarrant.fw.bean;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class PraiseCommentBean {
    private String mId;
    private String mTopUrl;
    private  String mName;
    private String mTime;
    private String mContent;
    private String mPic;
    private int mStatus;
    private int mAttention;
    private int mAttentionVisible;
    private String mFromUserId;
    private String mGoodId;
    private String mVId;

    public String getFromUserId() {
        return mFromUserId;
    }

    public void setFromUserId(String fromUserId) {
        mFromUserId = fromUserId;
    }

    public String getGoodId() {
        return mGoodId;
    }

    public void setGoodId(String goodId) {
        mGoodId = goodId;
    }

    public String getVId() {
        return mVId;
    }

    public void setVId(String VId) {
        mVId = VId;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public int getAttention() {
        return mAttention;
    }

    public void setAttention(int attention) {
        mAttention = attention;
    }

    public int getAttentionVisible() {
        return mAttentionVisible;
    }

    public void setAttentionVisible(int attentionVisible) {
        mAttentionVisible = attentionVisible;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

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

    public String getPic() {
        return mPic;
    }

    public void setPic(String pic) {
        mPic = pic;
    }
}
