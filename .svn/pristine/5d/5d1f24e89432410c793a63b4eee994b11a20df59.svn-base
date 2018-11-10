package com.facewarrant.fw.ui.activity.mine.faceGroup;

import android.app.Activity;
import android.content.Intent;
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
import com.facewarrant.fw.event.UpdateFaceListEvent;
import com.facewarrant.fw.event.UpdateGroupMemberEvent;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.google.zxing.common.StringUtils;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;

import org.greenrobot.eventbus.EventBus;
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
public class DeleteFaceActivity extends BaseActivity {
    private static final String TAG = "DeleteFaceActivity";
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
    private RecyclerCommonAdapter<PinyinBean> mAdapter;
    private List<PinyinBean> mDatas = new ArrayList<>();
    /**
     * 删除成员的ids
     */
    private List<String> mDeleteIdS = new ArrayList<>();

    /**
     * 添加成员的ids
     */
    private List<String> mAddIdS = new ArrayList<>();


    private String mId;

    private String mType;

    /*
     *是否是删除成员
     */
    private boolean mIsDelete;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_delete_face;
    }

    @Override
    public void initData() {

        mSettingTV.setTextColor(ContextCompat.getColor(mActivity, R.color.colorBlue));
        if (getIntent() != null && getIntent().getExtras() != null) {
            mId = getIntent().getExtras().getString(Constant.ID);
            mType = getIntent().getExtras().getString(Constant.TYPE);
            getFaceMember();
        }

        mTitleTV.setText(R.string.choose_face);
        mSettingTV.setText(R.string.delete);

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
                for (int i = 0; i < mDatas.size(); i++) {
                    PinyinBean pinyinBean = mDatas.get(i);
                    if (pinyinBean.isSelect()) {
                        mDeleteIdS.add(pinyinBean.getId());
                    }
                }
                if (mDeleteIdS.size() == 0) {
                    ToastUtil.showShort(mActivity, "请选择要删除的成员");
                } else {
                    LogUtil.e(TAG, "mDeleteIdS size==" + mDeleteIdS.size());
                    deleteGroupMember();
                }
            }

        });

    }


    private void showRecyclerView() {
        mAdapter = new RecyclerCommonAdapter<PinyinBean>(mActivity, R.layout.item_choose_face, mDatas) {
            @Override
            protected void convert(ViewHolder holder, final PinyinBean pinyinBean, int position) {
                holder.setText(R.id.tv_item_choose_face_name, pinyinBean.getName());
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
                        } else {
                            selectIV.setImageResource(R.drawable.item_choose_red);
                            pinyinBean.setSelect(true);
                        }
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


    }

    public static void open(Activity activity, String id, String type) {
        Intent intent = new Intent(activity, DeleteFaceActivity.class);
        intent.putExtra(Constant.ID, id);
        intent.putExtra(Constant.TYPE, type);
        activity.startActivity(intent);
    }

    private void getFaceMember() {
        final Map<String, String> map = new HashMap<>();
        map.put("groupsId", mId);
        map.put("requestType", 2 + "");
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .getFaceMembery(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                mDatas.clear();
                                JSONObject result = data.getJSONObject("result");
                                Iterator<String> it = result.keys();
                                List<String> keyList = new ArrayList<>();
                                while (it.hasNext()) {
                                    keyList.add(it.next());
                                }
                                for (int i = 0; i < keyList.size(); i++) {
                                    JSONArray itemList = result.getJSONArray(keyList.get(i));
                                    for (int j = 0; j < itemList.length(); j++) {
                                        JSONObject faceItem = itemList.getJSONObject(j);
                                        PinyinBean pinyinBean = new PinyinBean();
                                        pinyinBean.setId(faceItem.getString("faceId"));
                                        pinyinBean.setTopUrl(faceItem.getString("headUrl"));
                                        pinyinBean.setName(faceItem.getString("faceName"));
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

    private void deleteGroupMember() {
        final Map<String, String> map = new HashMap<>();
        map.put("groupsId", mId);
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mDeleteIdS.size(); i++) {
            if (i != mDeleteIdS.size() - 1) {
                stringBuilder.append(mDeleteIdS.get(i)).append(",");
            } else {
                stringBuilder.append(mDeleteIdS.get(i));
            }
        }
        map.put("groupsFaceIds", stringBuilder.toString());
        map.put("groupsType", mType);
        map.put("userId", AccountManager.sUserBean.getId());
        LogUtil.e(TAG, map.toString());

        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .deleteGroupMember(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                finish();
                                EventBus.getDefault().post(new UpdateFaceListEvent());

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
