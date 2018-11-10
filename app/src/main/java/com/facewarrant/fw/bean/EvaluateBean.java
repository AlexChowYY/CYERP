package com.facewarrant.fw.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class EvaluateBean implements Parcelable {
    private String mId;
    private String mToUrl;
    private String mName;
    private String mContent;
    private String mTime;
    private String mEvaluateNumber;
    private String mZanNumber;
    private String mUserId;
    private int isLike;
    private int mReplyCount;

    public int getReplyCount() {
        return mReplyCount;
    }

    public void setReplyCount(int replyCount) {
        mReplyCount = replyCount;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public String getUserId() {
        return mUserId;
    }

    public void setUserId(String userId) {
        mUserId = userId;
    }

    private List<EvaluateEvaluate> mList;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getToUrl() {
        return mToUrl;
    }

    public void setToUrl(String toUrl) {
        mToUrl = toUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public String getEvaluateNumber() {
        return mEvaluateNumber;
    }

    public void setEvaluateNumber(String evaluateNumber) {
        mEvaluateNumber = evaluateNumber;
    }

    public String getZanNumber() {
        return mZanNumber;
    }

    public void setZanNumber(String zanNumber) {
        mZanNumber = zanNumber;
    }

    public List<EvaluateEvaluate> getList() {
        return mList;
    }

    public void setList(List<EvaluateEvaluate> list) {
        mList = list;
    }

    public static class EvaluateEvaluate implements Parcelable {

        private String mId;
        private String mFromId;
        private String mToId;
        private String mFromNAME;
        private String mToName;
        private String mEvaluateNumber;
        private String mZanNumber;
        private String mContent;
        private String mTime;
        private int isLike;
        private int mReplyCount;

        public int getReplyCount() {
            return mReplyCount;
        }

        public void setReplyCount(int replyCount) {
            mReplyCount = replyCount;
        }

        public int getIsLike() {
            return isLike;
        }

        public void setIsLike(int isLike) {
            this.isLike = isLike;
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

        public String getEvaluateNumber() {
            return mEvaluateNumber;
        }

        public void setEvaluateNumber(String evaluateNumber) {
            mEvaluateNumber = evaluateNumber;
        }

        public String getZanNumber() {
            return mZanNumber;
        }

        public void setZanNumber(String zanNumber) {
            mZanNumber = zanNumber;
        }

        public String getId() {
            return mId;
        }

        public void setId(String id) {
            mId = id;
        }

        public String getFromId() {
            return mFromId;
        }

        public void setFromId(String fromId) {
            mFromId = fromId;
        }

        public String getToId() {
            return mToId;
        }

        public void setToId(String toId) {
            mToId = toId;
        }

        public String getFromNAME() {
            return mFromNAME;
        }

        public void setFromNAME(String fromNAME) {
            mFromNAME = fromNAME;
        }

        public String getToName() {
            return mToName;
        }

        public void setToName(String toName) {
            mToName = toName;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mId);
            dest.writeString(this.mFromId);
            dest.writeString(this.mToId);
            dest.writeString(this.mFromNAME);
            dest.writeString(this.mToName);
            dest.writeString(this.mEvaluateNumber);
            dest.writeString(this.mZanNumber);
            dest.writeString(this.mContent);
            dest.writeString(this.mTime);
            dest.writeInt(this.isLike);
            dest.writeInt(this.mReplyCount);
        }

        public EvaluateEvaluate() {
        }

        protected EvaluateEvaluate(Parcel in) {
            this.mId = in.readString();
            this.mFromId = in.readString();
            this.mToId = in.readString();
            this.mFromNAME = in.readString();
            this.mToName = in.readString();
            this.mEvaluateNumber = in.readString();
            this.mZanNumber = in.readString();
            this.mContent = in.readString();
            this.mTime = in.readString();
            this.isLike = in.readInt();
            this.mReplyCount = in.readInt();
        }

        public static final Creator<EvaluateEvaluate> CREATOR = new Creator<EvaluateEvaluate>() {
            @Override
            public EvaluateEvaluate createFromParcel(Parcel source) {
                return new EvaluateEvaluate(source);
            }

            @Override
            public EvaluateEvaluate[] newArray(int size) {
                return new EvaluateEvaluate[size];
            }
        };
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mToUrl);
        dest.writeString(this.mName);
        dest.writeString(this.mContent);
        dest.writeString(this.mTime);
        dest.writeString(this.mEvaluateNumber);
        dest.writeString(this.mZanNumber);
        dest.writeString(this.mUserId);
        dest.writeInt(this.isLike);
        dest.writeInt(this.mReplyCount);
        dest.writeList(this.mList);
    }

    public EvaluateBean() {
    }

    protected EvaluateBean(Parcel in) {
        this.mId = in.readString();
        this.mToUrl = in.readString();
        this.mName = in.readString();
        this.mContent = in.readString();
        this.mTime = in.readString();
        this.mEvaluateNumber = in.readString();
        this.mZanNumber = in.readString();
        this.mUserId = in.readString();
        this.isLike = in.readInt();
        this.mReplyCount = in.readInt();
        this.mList = new ArrayList<EvaluateEvaluate>();
        in.readList(this.mList, EvaluateEvaluate.class.getClassLoader());
    }

    public static final Parcelable.Creator<EvaluateBean> CREATOR = new Parcelable.Creator<EvaluateBean>() {
        @Override
        public EvaluateBean createFromParcel(Parcel source) {
            return new EvaluateBean(source);
        }

        @Override
        public EvaluateBean[] newArray(int size) {
            return new EvaluateBean[size];
        }
    };
}
