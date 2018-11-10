package com.facewarrant.fw.ui.activity.mine.faceGroup;

import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.FaceGroupBean;
import com.facewarrant.fw.event.UpdateFaceListEvent;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class ManagerActivity extends BaseActivity {
    private static final String TAG = "ManagerActivity";
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.rv_activity_manager)
    RecyclerView mRV;
    @BindView(R.id.tv_layout_top_back_setting)
    TextView mSettingTV;
    private RecyclerCommonAdapter<FaceGroupBean> mAdapter;
    private List<FaceGroupBean> mList = new ArrayList<>();
    private PopupWindow mPromptPopupWindow;
    private PopupWindow mDeletePromptPopupWindow;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_manager;


    }

    @Override
    public void initData() {
        EventBus.getDefault().register(mActivity);

        mTitleTV.setText(R.string.face_mamager);
        mSettingTV.setBackgroundResource(R.drawable.add_black);
//        createData();
//        showRecyclerView();
        getMyGroups();

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
                showPromptPopupwindow();


            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(mActivity);
    }


    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<FaceGroupBean>(mActivity, R.layout.item_face_maanager, mList) {
                @Override
                protected void convert(ViewHolder holder, final FaceGroupBean faceGroupBean, final int position) {
                    holder.setText(R.id.tv_item_face_manager_title, faceGroupBean.getName()
                            + "（" + faceGroupBean.getNumber() + ")");
                    // final SwipeLayout rootSL = holder.getView(R.id.sl_item_face_manager);
                    ImageView editTV = holder.getView(R.id.iv_item_face_manager_edit);
                    ImageView deleteTV = holder.getView(R.id.iv_item_face_manager_delete);
                    if (position == 0 || position == 1) {
                        deleteTV.setVisibility(View.GONE);
                    } else {
                        deleteTV.setVisibility(View.VISIBLE);
                    }

                    editTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // rootSL.close();
                            if (position == 0) {
                                EditGroupActivity.open(mActivity, faceGroupBean.getName(), faceGroupBean.getId(), 1 + "");
                            } else if (position == 1) {
                                EditGroupActivity.open(mActivity, faceGroupBean.getName(), faceGroupBean.getId(), 2 + "");
                            } else {
                                EditGroupActivity.open(mActivity, faceGroupBean.getName(), faceGroupBean.getId(), 3 + "");
                            }
                        }
                    });
                    deleteTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            showDeletePromptPopupwindow(faceGroupBean.getName(), faceGroupBean.getId());
                        }
                    });

                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mRV.setLayoutManager(linearLayoutManager);
            mRV.setAdapter(mAdapter);

        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void getMyGroups() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getMyGroups(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        mList.clear();
                        JSONObject result = data.getJSONObject("result");
                        JSONArray groupsList = result.getJSONArray("groupsList");
                        for (int i = 0; i < groupsList.length(); i++) {
                            JSONObject group = groupsList.getJSONObject(i);
                            FaceGroupBean faceGroupBean = new FaceGroupBean();
                            faceGroupBean.setName(group.getString("groupsName"));
                            faceGroupBean.setId(group.getString("groupsId"));
                            faceGroupBean.setNumber(group.getString("faceNum"));
                            mList.add(faceGroupBean);
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

    private void showPromptPopupwindow() {
        if (mPromptPopupWindow != null && mPromptPopupWindow.isShowing()) {
            mPromptPopupWindow.dismiss();
        } else {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.pop_add_face_group, null);
            final EditText nameET = view.findViewById(R.id.et_pop_add_face_group_name);
            TextView cancelTV = view.findViewById(R.id.tv_pop_add_face_group_cancel);
            TextView sureTV = view.findViewById(R.id.tv_pop_add_face_group_sure);
            cancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPromptPopupWindow.dismiss();
                }
            });
            sureTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String name = nameET.getText().toString().trim();
                    if (!TextUtils.isEmpty(name)) {
                        mPromptPopupWindow.dismiss();
                        createGroup(name);
                    } else {
                        ToastUtil.showShort(mActivity, "请输入群组名！");
                    }


                }
            });
            mPromptPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mPromptPopupWindow.setFocusable(true);
            mPromptPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
        }

    }

    private void showDeletePromptPopupwindow(String name, final String id) {
        if (mDeletePromptPopupWindow != null && mDeletePromptPopupWindow.isShowing()) {
            mDeletePromptPopupWindow.dismiss();
        } else {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.pop_delete_face_froup, null);
            TextView contentTV = view.findViewById(R.id.tv_content);
            contentTV.setText("确定删除" + name + "?");
            TextView cancelTV = view.findViewById(R.id.tv_pop_add_face_group_cancel);
            TextView sureTV = view.findViewById(R.id.tv_pop_add_face_group_sure);
            cancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDeletePromptPopupWindow.dismiss();
                }
            });
            sureTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteGroup(id);
                    mDeletePromptPopupWindow.dismiss();
                }
            });
            mDeletePromptPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mDeletePromptPopupWindow.setFocusable(true);
            mDeletePromptPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
        }
    }

    private void createGroup(String name) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("groupsName", name);
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .createGroup(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                getMyGroups();
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

    private void deleteGroup(String id) {
        final Map<String, String> map = new HashMap<>();
        map.put("groupsId", id);
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .deleteGroup(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                getMyGroups();

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void freshList(UpdateFaceListEvent updateFaceListEvent) {
        getMyGroups();
    }
}
