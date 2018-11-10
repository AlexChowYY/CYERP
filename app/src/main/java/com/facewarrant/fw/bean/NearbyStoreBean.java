package com.facewarrant.fw.bean;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class NearbyStoreBean {
    private String mId;
    private float mLat;
    private float mLng;
    private String mName;
    private String mAddress;
    private String mTips;
    private String mPhoneNumber;
    private String mNavNumber;
    private int mType;
    private String mPhone;
    public static final int TYPE_STORE = 1;
    public static final int TYPE_MARKET = 2;

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public float getLat() {
        return mLat;
    }

    public void setLat(float lat) {
        mLat = lat;
    }

    public float getLng() {
        return mLng;
    }

    public void setLng(float lng) {
        mLng = lng;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public String getTips() {
        return mTips;
    }

    public void setTips(String tips) {
        mTips = tips;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        mPhoneNumber = phoneNumber;
    }

    public String getNavNumber() {
        return mNavNumber;
    }

    public void setNavNumber(String navNumber) {
        mNavNumber = navNumber;
    }
}
