package com.facewarrant.fw.ui.activity.addFace;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.ContactBean;
import com.facewarrant.fw.bean.FaceBean;
import com.facewarrant.fw.event.UpdateFaceListEvent;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.makeramen.roundedimageview.RoundedImageView;

import org.greenrobot.eventbus.EventBus;
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
public class AddFaceActivity extends BaseActivity {
    private static final String TAG = "AddFaceActivity";
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.rv_activity_add_face)
    RecyclerView mRV;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.nsv_activity_add_face)
    NestedScrollView mTopNSV;
    @BindView(R.id.et_activity_add_face)
    EditText mSearchET;
    @BindView(R.id.iv_activity_add_face)
    ImageView mSearchIV;
    @BindView(R.id.tv_activity_add_face_top_text)
    TextView mTopTextTV;
    @BindView(R.id.tv_activity_add_face_bottom_text)
    TextView mBottomTextTV;
    @BindView(R.id.rv_activity_add_face_bottom)
    RecyclerView mBottomRV;
    @BindView(R.id.tv_activity_add_face_top_more)
    TextView mTopMoreTV;
    @BindView(R.id.tv_activity_add_face_bottom_more)
    TextView mBottomMoreTV;


    private List<ContactBean> mContactList = new ArrayList<>();
    private RecyclerCommonAdapter<ContactBean> mBottomAdapter;


    private String mId;

    private int mPosition;


    private Drawable isAttention;
    private JSONArray mPhoneInfoArray = new JSONArray();


    private RecyclerCommonAdapter<FaceBean> mAdapter;
    private List<FaceBean> mList = new ArrayList<>();


    private RecyclerCommonAdapter<FaceBean> mRecommendAdapter;
    private List<FaceBean> mRecommendList = new ArrayList<>();

    private String mIso = "CN";


    @Override
    protected int getContentViewId() {
        return R.layout.activity_add_face;
    }

    @Override
    public void initData() {
        mRV.setNestedScrollingEnabled(false);
        mBottomRV.setNestedScrollingEnabled(false);
        mTitleTV.setText(R.string.add_face);
        if (getIntent() != null && getIntent().getExtras() != null) {
            mId = getIntent().getExtras().getString(Constant.ID);
            mPosition = getIntent().getExtras().getInt(Constant.POSITION);
            switch (mPosition) {
                case 0://添加大Face
                    mBottomTextTV.setVisibility(View.GONE);
                    mBottomRV.setVisibility(View.GONE);
                    mBottomMoreTV.setVisibility(View.GONE);
                    break;
                case 1://添加亲友Face
                    mTopTextTV.setText(R.string.my_follow);
                    mBottomTextTV.setText(R.string.my_phone_list);
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CONTACTS)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_CONTACTS}, 200);
                        } else {
                            getAllContacts(mContext);
                        }
                    }


                    break;
                default://添加其他Face
                    mTopTextTV.setText(R.string.my_follow);
                    mBottomTextTV.setText(R.string.recommend);
                    break;
            }

            getList();
        }
        isAttention = ContextCompat.getDrawable(mActivity, R.drawable.add_follow);
        isAttention.setBounds(0, 0, isAttention.getMinimumWidth(), isAttention.getMinimumHeight());

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mIso = telephonyManager.getSimCountryIso().toUpperCase();

    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSearchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mSearchET.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showShort(mActivity, "请输入搜索内容");
                } else {
                    CommonSearchActivity.open(mActivity, mId, mPosition + "", content, 0 + "");

                }
            }
        });
        mTopMoreTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mPosition) {
                    case 0://大Face推荐
                        CommonSearchActivity.open(mActivity, mId, mPosition + "", "", 0 + "");
                        break;
                    case 1://我的关注
                        CommonSearchActivity.open(mActivity, mId, mPosition + "", "", 1 + "");
                        break;
                    default://我的关注
                        CommonSearchActivity.open(mActivity, mId, mPosition + "", "", 1 + "");
                        break;

                }

            }
        });
        mBottomMoreTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (mPosition) {
                    case 1:
                        //通讯录好友
                        CommonSearchActivity.open(mActivity, mId, mPosition + "", "", 2 + "");
                        break;
                    default:
                        //推荐
                        CommonSearchActivity.open(mActivity, mId, mPosition + "", "", 3 + "");
                        break;

                }

            }
        });
    }


    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<FaceBean>(mActivity, R.layout.item_recommend_follow, mList) {
                @Override
                protected void convert(ViewHolder holder, final FaceBean faceBean, final int position) {
                    holder.setText(R.id.tv_item_recommend_follow_name, faceBean.getName());
                    holder.setText(R.id.tv_item_recommend_follow_content, faceBean.getContent());
                    RoundedImageView topRIV = holder.getView(R.id.riv_item_recommend_follow_top);
                    final TextView attentionTV = holder.getView(R.id.tv_recommend_follow);
                    Glide.with(mActivity)
                            .load(faceBean.getTopUrl()).into(topRIV);
                    holder.setOnClickListener(R.id.tv_recommend_follow, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            joinFace(faceBean.getId(), mId, attentionTV, position);
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

    /**
     * 拉人
     *
     * @param id
     * @param gId
     * @param position
     */
    private void joinFace(String id, final String gId, final TextView add, final int position) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("faceId", id);
        map.put("groupsId", gId);
        switch (mPosition) {
            case 0:
                map.put("groupsType", 1 + "");
                break;
            case 1:
                map.put("groupsType", 2 + "");
                break;
            default:
                map.put("groupsType", 3 + "");
                break;
        }
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                
                .createRequest(RetrofitRequestInterface.class)
                .joinFace(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                add.setCompoundDrawables(null, null, null, null);
                                add.setText("已添加");
                                add.setTextColor(ContextCompat.getColor(mActivity, R.color.colorFontHint));
                                add.setEnabled(false);
                                ToastUtil.showShort(mActivity, "拉人成功！");
                                EventBus.getDefault().post(new UpdateFaceListEvent());
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

    private void getList() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("groupsId", mId);
        switch (mPosition) {
            case 0:
                map.put("groupsType", 1 + "");
                break;
            case 1:
                map.put("groupsType", 2 + "");
                map.put("phoneInfo", mPhoneInfoArray.toString());
                break;
            default:
                map.put("groupsType", 3 + "");
                break;
        }
        map.put("requestType", 0 + "");
        map.put("isoCode", mIso);
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getFaceRecommendList(RequestManager.encryptParams(map)).enqueue(new RetrofitCallBack() {
            @Override
            public void onSuccess(String response) {
                LogUtil.e(TAG, response.toString());
                try {
                    JSONObject data = new JSONObject(response);
                    if (data.getInt("resultCode") == 200) {
                        JSONObject result = data.getJSONObject("result");
                        JSONArray faceList = result.getJSONArray("facesList");
                        mList.clear();
                        for (int i = 0; i < faceList.length(); i++) {
                            JSONObject faceItem = faceList.getJSONObject(i);
                            FaceBean faceBean = new FaceBean();
                            faceBean.setId(faceItem.getString("faceId"));
                            faceBean.setTopUrl(faceItem.getString("headUrl"));
                            faceBean.setName(faceItem.getString("faceName"));
                            faceBean.setContent(faceItem.getString("standing"));
                            mList.add(faceBean);
                        }
                        showRecyclerView();
                        if (mPosition == 1) {
                            JSONArray contactArray = result.getJSONArray("contactsFaceList");
                            mContactList.clear();
                            for (int i = 0; i < contactArray.length(); i++) {
                                JSONObject contactItem = contactArray.getJSONObject(i);
                                ContactBean contactBean = new ContactBean();
                                contactBean.setName(contactItem.getString("contactName"));
                                contactBean.setPhone1(contactItem.getString("mobile"));
                                contactBean.setCode(contactItem.getString("countryCode"));
                                contactBean.setIsRegister(contactItem.getInt("isRegistered"));
                                switch (contactItem.getInt("isRegistered")) {
                                    case 0://未注册
                                        contactBean.setPhone(contactItem.getString("formatMobile"));
                                        break;
                                    case 1://已注册
                                        contactBean.setPhone(contactItem.getString("standing"));
                                        break;
                                }
                                mContactList.add(contactBean);
                            }
                            showBottomRecyclerView();
                        } else if (mPosition >= 2) {
                            JSONArray contactArray = result.getJSONArray("recommendFaceList");
                            mRecommendList.clear();
                            for (int i = 0; i < contactArray.length(); i++) {
                                JSONObject faceItem = contactArray.getJSONObject(i);
                                FaceBean faceBean = new FaceBean();
                                faceBean.setId(faceItem.getString("faceId"));
                                faceBean.setTopUrl(faceItem.getString("headUrl"));
                                faceBean.setName(faceItem.getString("faceName"));
                                faceBean.setContent(faceItem.getString("standing"));
                                mRecommendList.add(faceBean);
                            }
                            showRecommendRecyclerView();
                        }
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

    public static void open(Activity activity, String id, int position, String type) {
        Intent intent = new Intent(activity, AddFaceActivity.class);
        intent.putExtra(Constant.ID, id);
        intent.putExtra(Constant.POSITION, position);
        intent.putExtra(Constant.TYPE, type);
        activity.startActivity(intent);
    }

    /**
     * 获取联系人信息
     *
     * @param context
     * @return
     */

    private void getAllContacts(Context context) {
        Cursor cursor = context.getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String contactId = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            Cursor phones = context.getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId,
                    null, null);
            JSONArray phoneArray = new JSONArray();
            while (phones.moveToNext()) {
                ContactBean.phone phone = new ContactBean.phone();
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneArray.put(phoneNumber);
                phone.setPhone(phoneNumber);
            }
            phones.close();
            JSONObject phoneItem = new JSONObject();
            try {
                phoneItem.put("name", name);
                phoneItem.put("phoneNumber", phoneArray);
                mPhoneInfoArray.put(phoneItem);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        cursor.close();
        getList();
    }

    private void showBottomRecyclerView() {
        if (mBottomAdapter == null) {
            mBottomAdapter = new RecyclerCommonAdapter<ContactBean>(mActivity, R.layout.item_recommend_follow, mContactList) {
                @Override
                protected void convert(ViewHolder holder, final ContactBean contactBean, int position) {
                    RoundedImageView topRIV = holder.getView(R.id.riv_item_recommend_follow_top);
                    LogUtil.e(TAG, contactBean.getTopImage() + "");
                    holder.setText(R.id.tv_item_recommend_follow_name,
                            contactBean.getName());
                    TextView inviteTV = holder.getView(R.id.tv_recommend_follow);
                    inviteTV.setCompoundDrawables(null, null, null, null);
                    inviteTV.setText(R.string.invite);
                    holder.setText(R.id.tv_item_recommend_follow_content, contactBean.getPhone());
                    inviteTV.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            inviteFriend(contactBean.getPhone1(), contactBean.getCode());
                        }
                    });
                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mBottomRV.setLayoutManager(linearLayoutManager);
            mBottomRV.setAdapter(mBottomAdapter);
        } else {
            mBottomAdapter.notifyDataSetChanged();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAllContacts(mActivity);
                    showBottomRecyclerView();
                } else {
                    ToastUtil.showShort(mActivity, "未开启通讯录权限，请手动开启通讯录权限！");
                }
                break;

        }
    }


    private void inviteFriend(String phone, String code) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("contactPhone", phone);
        map.put("countryCode", code);
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .inviteContact(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                ToastUtil.showShort(mActivity, "邀请成功！");
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

    private void showRecommendRecyclerView() {
        if (mRecommendAdapter == null) {
            mRecommendAdapter = new RecyclerCommonAdapter<FaceBean>(mActivity, R.layout.item_recommend_follow, mRecommendList) {
                @Override
                protected void convert(ViewHolder holder, final FaceBean faceBean, final int position) {
                    holder.setText(R.id.tv_item_recommend_follow_name, faceBean.getName());
                    holder.setText(R.id.tv_item_recommend_follow_content, faceBean.getContent());
                    RoundedImageView topRIV = holder.getView(R.id.riv_item_recommend_follow_top);
                    final TextView attentionTV = holder.getView(R.id.tv_recommend_follow);
                    Glide.with(mActivity)
                            .load(faceBean.getTopUrl()).into(topRIV);
                    holder.setOnClickListener(R.id.tv_recommend_follow, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            joinFace(faceBean.getId(), mId, attentionTV, position);
                        }
                    });
                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mBottomRV.setLayoutManager(linearLayoutManager);
            mBottomRV.setAdapter(mAdapter);
        } else {
            mRecommendAdapter.notifyDataSetChanged();
        }
    }
}
