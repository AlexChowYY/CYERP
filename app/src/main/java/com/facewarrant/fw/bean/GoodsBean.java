package com.facewarrant.fw.bean;

import com.facewarrant.fw.ui.activity.mine.faceValue.OrderIncomeActivity;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 * <p>
 * 碑它的商品
 */
public class GoodsBean {
    private String mId;
    private String mName;
    private String mTopUrl;
    /**
     * 图片的宽高
     */
    private int mWidth;
    private int mHeight;
    private String mFaceNumber;
    private String mTime;
    private String mFollow;
    private String mCarNumber;
    private String mContent;

    private String mVId;
    private int mIsNew;

    public int getNew() {
        return mIsNew;
    }

    public void setNew(int aNew) {
        mIsNew = aNew;
    }

    public String getVId() {
        return mVId;
    }

    public void setVId(String VId) {
        mVId = VId;
    }

    private boolean mSelect;
    private int mType;
    public static final int TYPE_VIDEO = 1;
    public static final int TYPE_PIC = 0;

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public boolean isSelect() {
        return mSelect;
    }

    public void setSelect(boolean select) {
        mSelect = select;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getCarNumber() {
        return mCarNumber;
    }

    public void setCarNumber(String carNumber) {
        mCarNumber = carNumber;
    }

    public String getFollow() {
        return mFollow;
    }

    public void setFollow(String follow) {
        mFollow = follow;
    }

    public String getFaceNumber() {
        return mFaceNumber;
    }

    public void setFaceNumber(String faceNumber) {
        mFaceNumber = faceNumber;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(int height) {
        mHeight = height;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getTopUrl() {
        return mTopUrl;
    }

    public void setTopUrl(String topUrl) {
        mTopUrl = topUrl;
    }
}
