package com.facewarrant.fw.bean;

import android.os.Parcel;
import android.os.Parcelable;

import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class PinyinBean extends BaseIndexPinyinBean implements Parcelable {
    private String Name;//名字
    private boolean isTop;//是否是最上面的 不需要被转化成拼音的
    private boolean mIsSelect;
    private String mTopUrl;
    private String mId;
    private String mContent;
    private String mCode;
    private String mPhone;
    private int mIsRegister;

    public int getIsRegister() {
        return mIsRegister;
    }

    public void setIsRegister(int isRegister) {
        mIsRegister = isRegister;
    }

    public String getPhone() {
        return mPhone;
    }

    public void setPhone(String phone) {
        mPhone = phone;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    private String mType;

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
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

    public boolean isSelect() {
        return mIsSelect;
    }

    public void setSelect(boolean select) {
        mIsSelect = select;
    }

    public PinyinBean() {
    }

    public PinyinBean(String Name) {
        this.Name = Name;
    }

    public String getName() {
        return Name;
    }

    public PinyinBean setName(String name) {
        this.Name = name;
        return this;
    }

    public boolean isTop() {
        return isTop;
    }

    public PinyinBean setTop(boolean top) {
        isTop = top;
        return this;
    }

    @Override
    public String getTarget() {
        return Name;
    }

    @Override
    public boolean isNeedToPinyin() {
        return !isTop;
    }


    @Override
    public boolean isShowSuspension() {
        return !isTop;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.Name);
        dest.writeByte(this.isTop ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mIsSelect ? (byte) 1 : (byte) 0);
        dest.writeString(this.mTopUrl);
        dest.writeString(this.mId);
        dest.writeString(this.mContent);
        dest.writeString(this.mCode);
        dest.writeString(this.mPhone);
        dest.writeInt(this.mIsRegister);
        dest.writeString(this.mType);
    }

    protected PinyinBean(Parcel in) {
        this.Name = in.readString();
        this.isTop = in.readByte() != 0;
        this.mIsSelect = in.readByte() != 0;
        this.mTopUrl = in.readString();
        this.mId = in.readString();
        this.mContent = in.readString();
        this.mCode = in.readString();
        this.mPhone = in.readString();
        this.mIsRegister = in.readInt();
        this.mType = in.readString();
    }

    public static final Parcelable.Creator<PinyinBean> CREATOR = new Parcelable.Creator<PinyinBean>() {
        @Override
        public PinyinBean createFromParcel(Parcel source) {
            return new PinyinBean(source);
        }

        @Override
        public PinyinBean[] newArray(int size) {
            return new PinyinBean[size];
        }
    };
}
