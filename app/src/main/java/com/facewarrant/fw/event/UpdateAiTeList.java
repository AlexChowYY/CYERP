package com.facewarrant.fw.event;

import com.facewarrant.fw.bean.PinyinBean;

import java.util.List;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class UpdateAiTeList {
    private String mId;
    private String mName;
    private List<PinyinBean> mList;

    public List<PinyinBean> getList() {
        return mList;
    }

    public void setList(List<PinyinBean> list) {
        mList = list;
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
}
