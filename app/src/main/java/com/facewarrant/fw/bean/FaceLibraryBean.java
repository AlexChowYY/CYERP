package com.facewarrant.fw.bean;

import java.util.List;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class FaceLibraryBean {
    private String mId;
    private String mTopUrl;
    private String mName;
    private String mFans;
    private String mWarrantIt;
    private int mIndex;
    private int mCount;

    public int getCount() {
        return mCount;
    }

    public void setCount(int count) {
        mCount = count;
    }

    public int getIndex() {
        return mIndex;
    }

    public void setIndex(int index) {
        mIndex = index;
    }

    private List<Good> mGoodList;

    public List<Good> getGoodList() {
        return mGoodList;
    }

    public void setGoodList(List<Good> goodList) {
        mGoodList = goodList;
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

    public String getFans() {
        return mFans;
    }

    public void setFans(String fans) {
        mFans = fans;
    }

    public String getWarrantIt() {
        return mWarrantIt;
    }

    public void setWarrantIt(String warrantIt) {
        mWarrantIt = warrantIt;
    }

    public static class Good {
        private String mId;
        private String mTopUrl;

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
    }
}
