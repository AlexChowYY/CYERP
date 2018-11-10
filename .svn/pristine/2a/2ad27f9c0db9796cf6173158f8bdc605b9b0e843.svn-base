package com.facewarrant.fw.ui.activity.mine.faceGroup;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.FaceBean;
import com.facewarrant.fw.event.UpdateFaceListEvent;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.ui.activity.addFace.AddFaceActivity;
import com.facewarrant.fw.ui.personal.PersonalActivity;
import com.facewarrant.fw.util.DisplayUtil;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.facewarrant.fw.view.GridSpacingItemDecoration;
import com.makeramen.roundedimageview.RoundedImageView;

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
public class EditGroupActivity extends BaseActivity {
    private static final String TAG = "EditGroupActivity";
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.tv_activity_edit_group_more)
    TextView mMoreTV;
    @BindView(R.id.rv_activity_edit_group)
    RecyclerView mRV;
    @BindView(R.id.tv_activity_edit_group_name)
    TextView mNameTV;
    private String mName;
    private String mId;
    private String mType;

    private List<FaceBean> mList = new ArrayList<>();
    private RecyclerCommonAdapter<FaceBean> mAdapter;
    private PopupWindow mPromptPopupWindow;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_edit_group;
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(mActivity);
        mRV.setNestedScrollingEnabled(false);
        if (getIntent() != null && getIntent().getExtras() != null) {
            mId = getIntent().getExtras().getString(Constant.ID);
            mName = getIntent().getExtras().getString(Constant.NAME);
            mType = getIntent().getExtras().getString(Constant.TYPE);

            mTitleTV.setText(mName);
            mNameTV.setText(mName);
            getFaceMembery();
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
        mMoreTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllMemberActivity.open(mActivity, mId);

            }
        });
        mNameTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mType.equals("3")) {
                    ReviseGroupNameActivity.open(mActivity, mName, mId);
                }

            }
        });

    }

    public static void open(Activity activity, String name, String id, String type) {
        Intent intent = new Intent(activity, EditGroupActivity.class);
        intent.putExtra(Constant.NAME, name);
        intent.putExtra(Constant.TYPE, type);
        intent.putExtra(Constant.ID, id);
        activity.startActivity(intent);

    }

    private void getFaceMembery() {
        final Map<String, String> map = new HashMap<>();
        map.put("groupsId", mId);
        map.put("requestType", 0 + "");
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .getFaceMembery(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);

                            if (data.getInt("resultCode") == 200) {
                                mList.clear();
                                JSONObject result = data.getJSONObject("result");
                                JSONArray faceArray = result.getJSONArray("groupUserList");
                                for (int i = 0; i < faceArray.length(); i++) {
                                    JSONObject faceItem = faceArray.getJSONObject(i);
                                    FaceBean faceBean = new FaceBean();
                                    faceBean.setId(faceItem.getString("faceId"));
                                    faceBean.setTopUrl(faceItem.getString("headUrl"));
                                    faceBean.setName(faceItem.getString("faceName"));
                                    mList.add(faceBean);
                                }
                                if (mType.equals("1")) {
                                    if (result.getInt("groupFacesNum") > 19) {
                                        mMoreTV.setVisibility(View.VISIBLE);
                                    } else {
                                        mMoreTV.setVisibility(View.GONE);
                                    }
                                } else {
                                    if (result.getInt("groupFacesNum") > 18) {
                                        mMoreTV.setVisibility(View.VISIBLE);
                                    } else {
                                        mMoreTV.setVisibility(View.GONE);
                                    }
                                }

                                FaceBean faceBean = new FaceBean();
                                mList.add(faceBean);
                                FaceBean faceBean1 = new FaceBean();
                                mList.add(faceBean1);
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

    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<FaceBean>(mActivity, R.layout.item_group_member, mList) {
                @Override
                protected void convert(final ViewHolder holder, final FaceBean faceBean, final int position) {
                    RoundedImageView topRIV = holder.getView(R.id.riv_item_group_member);
                    if (position == mList.size() - 2) {
                        topRIV.setImageResource(R.drawable.add_member);
                    } else if (position == mList.size() - 1) {
                        topRIV.setImageResource(R.drawable.delete_member);
                    } else {
                        Glide.with(mActivity)
                                .load(faceBean.getTopUrl())
                                .into(topRIV);
                    }
                    holder.setText(R.id.tv_item_group_member_name, faceBean.getName());
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) topRIV.getLayoutParams();
                    layoutParams.width = (DisplayUtil.getScreenWidth(mActivity) - 150) / 5;
                    layoutParams.height = (DisplayUtil.getScreenWidth(mActivity) - 150) / 5;
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (position != mList.size() - 2 && position != mList.size() - 1) {
                                showPromptPopupwindow(faceBean);
                            } else if (position == mList.size() - 1) {
                                DeleteFaceActivity.open(mActivity, mId, mType);
                            } else if (position == mList.size() - 2) {
                                switch (mType) {
                                    case "1"://添加大Face
                                        AddFaceActivity.open(mActivity, mId, 0, mType);
                                        break;
                                    case "2"://添加亲友Face
                                        AddFaceActivity.open(mActivity, mId, 1, mType);
                                        break;
                                    case "3"://添加其他Face
                                        AddFaceActivity.open(mActivity, mId, 2, mType);
                                        break;

                                }

                            }
                        }
                    });
                }

            };
            GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 5);
            mRV.addItemDecoration(new GridSpacingItemDecoration(5, 25, true));
            mRV.setLayoutManager(gridLayoutManager);
            mRV.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }


    private void showPromptPopupwindow(final FaceBean faceBean) {
        if (mPromptPopupWindow != null && mPromptPopupWindow.isShowing()) {
            mPromptPopupWindow.dismiss();
        } else {
            View view = LayoutInflater.from(mActivity).inflate(R.layout.pop_group_member, null);
            RoundedImageView topRIV = view.findViewById(R.id.riv_pop_group_member_top);
            TextView nameTV = view.findViewById(R.id.tv_pop_group_member_name);
            TextView personalPagerTV = view.findViewById(R.id.tv_pop_group_member_home_pager);
            TextView questionTV = view.findViewById(R.id.tv_pop_group_member_question_him);
            nameTV.setText(faceBean.getName());
            Glide.with(mActivity)
                    .load(faceBean.getTopUrl()).into(topRIV);
            questionTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mPromptPopupWindow.dismiss();

                }
            });

            personalPagerTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PersonalActivity.open(mActivity, faceBean.getId());

                    mPromptPopupWindow.dismiss();
                }
            });
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mPromptPopupWindow.dismiss();
                    return false;
                }
            });
            mPromptPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mPromptPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(mActivity);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void freshList(UpdateFaceListEvent updateFaceListEvent) {
        getFaceMembery();
    }
}
