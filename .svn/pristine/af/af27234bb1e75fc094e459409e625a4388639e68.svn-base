package com.facewarrant.fw.ui.activity.question;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.PinyinBean;
import com.facewarrant.fw.event.UpdateAiTeList;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.global.EventBusUtils;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class ChooseFaceActivity extends BaseActivity {
    private static final String TAG = "ChooseFaceActivity";
    private static final String INDEX_STRING_TOP = "↑";
    @BindView(R.id.rv_activity_choose_face)
    RecyclerView mRV;
    @BindView(R.id.ib_activity_choose_face)
    IndexBar mIB;
    @BindView(R.id.tvSideBarHint)
    TextView mHintTV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_setting)
    TextView mSettingTV;
    private int mGroupLength;
    public static final String TYPE_GROUP = "TYPE_GROUP";
    public static final String TYPE_FACE = "TYPE_FACE";

    private List<PinyinBean> mGroupList = new ArrayList<>();
    private List<PinyinBean> mFaceList = new ArrayList<>();

    private RecyclerCommonAdapter<PinyinBean> mAdapter;

    private List<PinyinBean> mDataS = new ArrayList<>();

    private List<PinyinBean> mSelectList = new ArrayList<>();
    private List<PinyinBean> mShowSelectList = new ArrayList<>();


    @Override
    protected int getContentViewId() {
        return R.layout.activity_choose_face;


    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.my_follow);
        mSettingTV.setText(R.string.sure);
        getList();
        if (getIntent() != null && getIntent().getExtras() != null) {
            mShowSelectList.clear();
            mShowSelectList.addAll(getIntent().<PinyinBean>getParcelableArrayListExtra(Constant.TYPE));
        }
    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSettingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateAiTeList updateAiTeList = new UpdateAiTeList();
                updateAiTeList.setList(mSelectList);
                EventBus.getDefault().post(updateAiTeList);
                finish();
            }
        });

    }


    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<PinyinBean>(mActivity, R.layout.item_choose_face, mDataS) {
                @Override
                protected void convert(ViewHolder holder, final PinyinBean pinyinBean, final int position) {
                    holder.setText(R.id.tv_item_choose_face_name, pinyinBean.getName());
                    if (position == 0 || position == mGroupLength + 1) {
                        holder.getConvertView().setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorBg));
                        holder.setVisible(R.id.iv_item_choose_face, false);
                    } else {
                        holder.getConvertView().setBackgroundColor(ContextCompat.getColor(mActivity, R.color.colorWhite));
                        holder.setVisible(R.id.iv_item_choose_face, true);
                    }
                    final ImageView selectIV = holder.getView(R.id.iv_item_choose_face);
                    if (pinyinBean.isSelect()) {
                        selectIV.setImageResource(R.drawable.item_choose_red);
                    } else {
                        selectIV.setImageResource(R.drawable.item_choose_gray);
                    }
                    selectIV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (pinyinBean.isSelect()) {
                                selectIV.setImageResource(R.drawable.item_choose_gray);
                                pinyinBean.setSelect(false);
                                mSelectList.remove(pinyinBean);
                            } else {
                                selectIV.setImageResource(R.drawable.item_choose_red);
                                pinyinBean.setSelect(true);
                                mSelectList.add(pinyinBean);
                            }

                        }
                    });

                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mRV.setLayoutManager(linearLayoutManager);
            mRV.setAdapter(mAdapter);
            mRV.addItemDecoration(new SuspensionDecoration(mActivity, mDataS).setColorTitleBg(ContextCompat.getColor(mActivity, R.color.colorBg)));
            mRV.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
            mIB.setmPressedShowTextView(mHintTV)//设置HintTextView
                    .setNeedRealIndex(true)//设置需要真实的索引
                    .setmLayoutManager(linearLayoutManager);
            mIB.setmSourceDatas(mDataS)//设置数据
                    .invalidate();
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void getList() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .getFacesAndGroups(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                JSONObject result = data.getJSONObject("result");
                                JSONArray groupArray = result.getJSONArray("groupsList");
                                mGroupLength = groupArray.length();
                                mGroupList.add((PinyinBean) new PinyinBean().setName("我的群组").setTop(true).setBaseIndexTag(INDEX_STRING_TOP));
                                for (int i = 0; i < groupArray.length(); i++) {
                                    JSONObject groupItem = groupArray.getJSONObject(i);
                                    PinyinBean pinyinBean = new PinyinBean();
                                    pinyinBean.setId(groupItem.getString("groupsId"));
                                    pinyinBean.setName(groupItem.getString("groupsName"));
                                    pinyinBean.setType(TYPE_GROUP);
                                    pinyinBean.setTop(true).setBaseIndexTag(INDEX_STRING_TOP);
                                    mGroupList.add(pinyinBean);
                                }
                                mFaceList.add((PinyinBean) new PinyinBean().setName("我的关注").setTop(true).setBaseIndexTag(INDEX_STRING_TOP));
                                JSONObject faceArray = result.getJSONObject("facesList");
                                Iterator<String> it = faceArray.keys();
                                List<String> keyList = new ArrayList<>();
                                while (it.hasNext()) {
                                    keyList.add(it.next());
                                }
                                for (int i = 0; i < keyList.size(); i++) {
                                    JSONArray itemList = faceArray.getJSONArray(keyList.get(i));
                                    for (int j = 0; j < itemList.length(); j++) {
                                        JSONObject item = itemList.getJSONObject(j);
                                        PinyinBean pinyinBean = new PinyinBean();
                                        pinyinBean.setId(item.getString("faceId"));
                                        pinyinBean.setName(item.getString("faceName"));
                                        pinyinBean.setTopUrl(item.getString("headUrl"));
                                        pinyinBean.setContent(item.getString("standing"));
                                        pinyinBean.setType(TYPE_FACE);
                                        if (Character.isDigit(keyList.get(i).charAt(0))) {
                                            pinyinBean.setTop(true).setBaseIndexTag(INDEX_STRING_TOP);
                                        }
                                        mFaceList.add(pinyinBean);
                                    }
                                }
                                mDataS.addAll(mGroupList);
                                mDataS.addAll(mFaceList);
                                matchSelect();
                                showRecyclerView();

                            } else {
                                ToastUtil.showShort(mActivity, data.getString("resultDesc"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(Throwable t) {

                    }
                });
    }

    private void matchSelect() {
        for (int i = 0; i < mShowSelectList.size(); i++) {
            PinyinBean pinyinBean = mShowSelectList.get(i);
            for (int i1 = 0; i1 < mDataS.size(); i1++) {
                PinyinBean pinyinBean1 = mDataS.get(i1);
                if (pinyinBean.getId().equals(pinyinBean1.getId())) {
                    pinyinBean1.setSelect(true);
                    mSelectList.add(pinyinBean1);
                }
            }
        }
    }


}
