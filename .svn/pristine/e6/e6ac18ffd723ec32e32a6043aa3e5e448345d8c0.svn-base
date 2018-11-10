package com.facewarrant.fw.ui.activity.mine;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.PinyinBean;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.personal.PersonalActivity;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.makeramen.roundedimageview.RoundedImageView;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;

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
public class MyFollowActivity extends BaseActivity {
    private static final String TAG = "MyFollowActivity";
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.rv_activity_my_follow)
    RecyclerView mRV;
    @BindView(R.id.ib_activity_my_follow)
    IndexBar mIB;
    @BindView(R.id.tvSideBarHint)
    TextView mHintTV;
    @BindView(R.id.tv_activity_my_follow_up)
    TextView mUpTV;

    private RecyclerCommonAdapter<PinyinBean> mAdapter;
    private List<PinyinBean> mDatas = new ArrayList<>();

    private static final String INDEX_STRING_TOP = "↑";

    @Override
    protected int getContentViewId() {
        return R.layout.activity_my_follow;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.my_follow);
        getMyFollowList();
    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mUpTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRV.scrollToPosition(0);
            }
        });
    }


    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<PinyinBean>(mActivity, R.layout.item_my_follow, mDatas) {
                @Override
                protected void convert(ViewHolder holder, final PinyinBean pinyinBean, int position) {
                    RoundedImageView topRIV = holder.getView(R.id.riv_item_my_follow_top);
                    Glide.with(mActivity)
                            .load(pinyinBean.getTopUrl())
                            .into(topRIV);
                    holder.setText(R.id.tv_item_my_follow_name, pinyinBean.getName());
                    holder.setText(R.id.tv_item_my_follow_content, pinyinBean.getContent());
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            PersonalActivity.open(mActivity,pinyinBean.getId());
                        }
                    });
                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mRV.setLayoutManager(linearLayoutManager);
            mRV.setAdapter(mAdapter);
            mRV.addItemDecoration(new SuspensionDecoration(mActivity, mDatas).setColorTitleBg(ContextCompat.getColor(mActivity, R.color.colorBg)));
            mRV.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
            mIB.setmPressedShowTextView(mHintTV)//设置HintTextView
                    .setNeedRealIndex(true)//设置需要真实的索引
                    .setmLayoutManager(linearLayoutManager);
            mIB.setmSourceDatas(mDatas)//设置数据
                    .invalidate();
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }


    private void getMyFollowList() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getMyFollowList(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                JSONObject result = data.getJSONObject("result");
                                Iterator<String> it = result.keys();
                                List<String> keyList = new ArrayList<>();
                                while (it.hasNext()) {
                                    keyList.add(it.next());
                                }
                                for (int i = 0; i < keyList.size(); i++) {
                                    JSONArray itemList = result.getJSONArray(keyList.get(i));
                                    for (int j = 0; j < itemList.length(); j++) {
                                        JSONObject item = itemList.getJSONObject(j);
                                        PinyinBean pinyinBean = new PinyinBean();
                                        pinyinBean.setId(item.getString("faceId"));
                                        pinyinBean.setName(item.getString("faceName"));
                                        pinyinBean.setTopUrl(item.getString("headUrl"));
                                        //  pinyinBean.setContent(item.getString("standing"));
                                        if (Character.isDigit(keyList.get(i).charAt(0))) {
                                            pinyinBean.setTop(true).setBaseIndexTag(INDEX_STRING_TOP);
                                        }
                                        mDatas.add(pinyinBean);
                                    }
                                }
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


}
