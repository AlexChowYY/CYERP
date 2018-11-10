package com.facewarrant.fw.bean;

import java.util.List;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class WithdrawRecordBean {
    private String mId;
    private String mMonth;
    private List<Record> mList;

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getMonth() {
        return mMonth;
    }

    public void setMonth(String month) {
        mMonth = month;
    }

    public List<Record> getList() {
        return mList;
    }

    public void setList(List<Record> list) {
        mList = list;
    }

    public static  class Record{
        private String mId;
        private String mName;
        private String mTime;
        private String mMoney;
        private int mStatus;

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

        public String getTime() {
            return mTime;
        }

        public void setTime(String time) {
            mTime = time;
        }

        public String getMoney() {
            return mMoney;
        }

        public void setMoney(String money) {
            mMoney = money;
        }

        public int getStatus() {
            return mStatus;
        }

        public void setStatus(int status) {
            mStatus = status;
        }
    }

}
