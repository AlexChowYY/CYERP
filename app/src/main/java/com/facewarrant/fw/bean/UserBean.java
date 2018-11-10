package com.facewarrant.fw.bean;

import java.io.Serializable;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class UserBean implements Serializable {
    private String mId;
    private String mPhone;
    private int mLoginType;
    private String mProvince;
    private String mProvinceId;
    private String mCity;
    private String mCityId;
    private String mHeadUrl;
    private String mName;
    private String mInteragel;

    public String getInteragel() {
        return mInteragel;
    }

    public void setInteragel(String interagel) {
        mInteragel = interagel;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    private String mCountryCode;

    public String getCountryCode() {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode) {
        mCountryCode = countryCode;
    }

    public String getHeadUrl() {
        return mHeadUrl;
    }

    public void setHeadUrl(String headUrl) {
        mHeadUrl = headUrl;
    }

    public static final int TYPE_CODE = 1;
    public static final int TYPE_PWD = 2;
    public static final int TYPE_WECHART = 4;
    public static final int TYPE_ALI_PAY = 5;
    public static final int TYPE_WEIBO = 6;

    public String getId() {
        return mId;
    }

    public String getProvince() {
        return mProvince;
    }

    public void setProvince(String province) {
        mProvince = province;
    }

    public String getProvinceId() {
        return mProvinceId;
    }

    public void setProvinceId(String provinceId) {
        mProvinceId = provinceId;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public String getCityId() {
        return mCityId;
    }

    public void setCityId(String cityId) {
        mCityId = cityId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public int getLoginType() {
        return mLoginType;
    }

    public void setLoginType(int loginType) {
        mLoginType = loginType;
    }
}
