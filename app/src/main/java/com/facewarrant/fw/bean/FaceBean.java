package com.facewarrant.fw.bean;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class FaceBean {
    private String mId;
    private String mTopUrl;
    private String mContent;
    private String mLove;
    private String mName;
    private String mFollow;
    private boolean mIsFollow;
    private boolean mIsHasNew;
    private String mNewCount;
    private String mIsAttention;

    private String mGoodUrl;
    private String mGoodId;
    private String mFavour;

    private int mInGroup;

    public int getInGroup() {
        return mInGroup;
    }

    public void setInGroup(int inGroup) {
        mInGroup = inGroup;
    }

    public String getFavour() {
        return mFavour;
    }

    public void setFavour(String favour) {
        mFavour = favour;
    }

    private int mGoodType;

    public int getGoodType() {
        return mGoodType;
    }

    public void setGoodType(int goodType) {
        this.mGoodType = goodType;
    }

    public String getGoodUrl() {
        return mGoodUrl;
    }

    public void setGoodUrl(String goodUrl) {
        mGoodUrl = goodUrl;
    }

    public String getGoodId() {
        return mGoodId;
    }

    public void setGoodId(String goodId) {
        mGoodId = goodId;
    }

    public String getIsAttention() {
        return mIsAttention;
    }

    public void setIsAttention(String isAttention) {
        mIsAttention = isAttention;
    }

    public String getNewCount() {
        return mNewCount;
    }

    public void setNewCount(String newCount) {
        mNewCount = newCount;
    }

    public boolean isHasNew() {
        return mIsHasNew;
    }

    public void setHasNew(boolean hasNew) {
        mIsHasNew = hasNew;
    }

    public String getFollow() {
        return mFollow;
    }

    public void setFollow(String follow) {
        mFollow = follow;
    }

    public String getAllFavour() {
        return mAllFavour;
    }

    public void setAllFavour(String allFavour) {
        mAllFavour = allFavour;
    }

    private String mAllFavour;

    public boolean isFollow() {
        return mIsFollow;
    }

    public void setFollow(boolean follow) {
        mIsFollow = follow;
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

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getLove() {
        return mLove;
    }

    public void setLove(String love) {
        mLove = love;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }
}
