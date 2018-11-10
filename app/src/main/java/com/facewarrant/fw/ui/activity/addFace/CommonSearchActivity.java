package com.facewarrant.fw.ui.activity.addFace;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aliyun.common.utils.L;
import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.ContactBean;
import com.facewarrant.fw.bean.FaceBean;
import com.facewarrant.fw.bean.PinyinBean;
import com.facewarrant.fw.event.UpdateFaceListEvent;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.makeramen.roundedimageview.RoundedImageView;
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
public class CommonSearchActivity extends BaseActivity {
    private static final String TAG = "CommonSearchActivity";
    @BindView(R.id.rv_activity_common_search)
    RecyclerView mRV;
    @BindView(R.id.et_activity_add_face)
    EditText mSearchET;
    @BindView(R.id.iv_activity_add_face)
    ImageView mSearchIV;

    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;


    @BindView(R.id.ib_activity_search_library)
    IndexBar mIB;
    @BindView(R.id.tvSideBarHint)
    TextView mHintTV;

    private String mGId;
    private String mGType;
    private String mKeywords;
    private String mSearchType;

    private RecyclerCommonAdapter<PinyinBean> mAdapter;
    private List<PinyinBean> mList = new ArrayList<>();

    private List<FaceBean> mResultList = new ArrayList<>();
    private RecyclerCommonAdapter<FaceBean> mResultAdapter;

    private JSONArray mPhoneInfoArray = new JSONArray();


    private String mIso = "CN";

    @Override
    protected int getContentViewId() {
        return R.layout.activity_common_search;
    }

    @Override
    public void initData() {
        mRV.setNestedScrollingEnabled(false);
        mTitleTV.setText(R.string.add_face);
        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                mGId = getIntent().getExtras().getString(Constant.ID);
                mGType = getIntent().getExtras().getString(Constant.TYPE);
                mKeywords = getIntent().getExtras().getString(Constant.KEYWORD);
                mSearchType = getIntent().getExtras().getString(Constant.SEARCH_TYPE);
                if (mSearchType.equals("2")) {
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_CONTACTS)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.READ_CONTACTS}, 200);
                        } else {
                            getAllContacts(mContext);
                        }
                    }
                }
                if (TextUtils.isEmpty(mKeywords)) {
                    getList();//点击更多的页面
                } else {
                    //搜索的页面
                    mSearchET.setText(mKeywords);
                    mSearchET.setSelection(mKeywords.length() - 1);
                    getSearchResult();
                    mIB.setVisibility(View.GONE);
                }
            }
        }
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mIso = telephonyManager.getSimCountryIso().toUpperCase();

    }

    @Override
    public void initEvent() {
        mSearchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mSearchET.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showShort(mActivity, "请输入搜索内容");
                } else {
                    mKeywords = content;
                    getSearchResult();

                }
            }
        });
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 200:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getAllContacts(mActivity);

                } else {
                    ToastUtil.showShort(mActivity, "未开启通讯录权限，请手动开启通讯录权限！");
                }
                break;

        }
    }

    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<PinyinBean>(mActivity, R.layout.item_recommend_follow, mList) {
                @Override
                protected void convert(ViewHolder holder, final PinyinBean pinyinBean, final int position) {
                    holder.setText(R.id.tv_item_recommend_follow_name, pinyinBean.getName());
                    holder.setText(R.id.tv_item_recommend_follow_content, pinyinBean.getContent());
                    RoundedImageView topRIV = holder.getView(R.id.riv_item_recommend_follow_top);
                    final TextView attentionTV = holder.getView(R.id.tv_recommend_follow);
                    if (mSearchType.equals("2")) {
                        attentionTV.setText(R.string.invite);
                    }
//                    Glide.with(mActivity)
//                            .load(pinyinBean.getTopUrl()).into(topRIV);
                    holder.setOnClickListener(R.id.tv_recommend_follow, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!mSearchType.equals("2")) {
                                //添加入群
                                joinFace(pinyinBean.getId(), mGId, attentionTV);
                            } else {
                                //邀请通讯录好友
                                inviteFriend(pinyinBean.getPhone(), pinyinBean.getCode());
                            }

                        }
                    });
                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mRV.setLayoutManager(linearLayoutManager);
            mRV.setAdapter(mAdapter);
            mRV.addItemDecoration(new SuspensionDecoration(this, mList).setColorTitleBg(ContextCompat.getColor(mActivity, R.color.colorBg)));
            mRV.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
            mIB.setmPressedShowTextView(mHintTV)//设置HintTextView
                    .setNeedRealIndex(true)//设置需要真实的索引
                    .setmLayoutManager(linearLayoutManager);
            mIB.setmSourceDatas(mList)//设置数据
                    .invalidate();
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void showResultRecyclerView() {
        mIB.setVisibility(View.GONE);
        if (mResultAdapter == null) {
            mResultAdapter = new RecyclerCommonAdapter<FaceBean>(mActivity, R.layout.item_recommend_follow, mResultList) {
                @Override
                protected void convert(ViewHolder holder, final FaceBean faceBean, int position) {
                    holder.setText(R.id.tv_item_recommend_follow_name, faceBean.getName());
                    holder.setText(R.id.tv_item_recommend_follow_content, faceBean.getContent());
                    RoundedImageView topRIV = holder.getView(R.id.riv_item_recommend_follow_top);
                    final TextView attentionTV = holder.getView(R.id.tv_recommend_follow);
                    Glide.with(mActivity)
                            .load(faceBean.getTopUrl()).into(topRIV);
                    holder.setOnClickListener(R.id.tv_recommend_follow, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            joinFace(faceBean.getId(), mGId, attentionTV);
                        }
                    });
                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mRV.setLayoutManager(linearLayoutManager);
            mRV.setAdapter(mResultAdapter);
        } else {
            mResultAdapter.notifyDataSetChanged();
        }
    }

    public static void open(Activity activity, String id, String type, String keywords, String searchType) {
        Intent intent = new Intent(activity, CommonSearchActivity.class);
        intent.putExtra(Constant.ID, id);
        intent.putExtra(Constant.TYPE, type);
        intent.putExtra(Constant.KEYWORD, keywords);
        intent.putExtra(Constant.SEARCH_TYPE, searchType);
        activity.startActivity(intent);
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


    private void getSearchResult() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("groupsId", mGId);
        switch (mGType) {
            case "0":
                map.put("groupsType", 1 + "");
                break;
            case "1":
                map.put("groupsType", 2 + "");
                break;
            default:
                map.put("groupsType", 3 + "");
                break;
        }
        map.put("searchCondition", mKeywords);
        switch (mSearchType) {
            case "2":
                map.put("searchType", 1 + "");
                break;
            default:
                map.put("searchType", 0 + "");
                break;
        }
        map.put("isoCode", mIso);
        if (mSearchType.equals("2")) {
            map.put("phoneInfo", mPhoneInfoArray.toString());
        }

        LogUtil.e(TAG, map.toString());

        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .searchFaces(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                JSONArray result = data.getJSONArray("result");
                                for (int i = 0; i < result.length(); i++) {
                                    JSONObject resultItem = result.getJSONObject(i);
                                    FaceBean faceBean = new FaceBean();
                                    faceBean.setId(resultItem.getString("faceId"));
                                    faceBean.setTopUrl(resultItem.getString("headUrl"));
                                    faceBean.setContent(resultItem.getString("standing"));
                                    faceBean.setInGroup(resultItem.getInt("isInGroup"));
                                    faceBean.setName(resultItem.getString("faceName"));
                                    mResultList.add(faceBean);
                                }
                                showResultRecyclerView();
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

    /**
     * 拉人
     *
     * @param id
     * @param gId
     */
    private void joinFace(String id, final String gId, final TextView add) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("faceId", id);
        map.put("groupsId", gId);
        switch (mGType) {
            case "0":
                map.put("groupsType", 1 + "");
                break;
            case "1":
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
                                add.setCompoundDrawables(null, null, null, null);
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
        map.put("groupsId", mGId);
        switch (mGType) {
            case "0":
                map.put("groupsType", 1 + "");
                break;
            case "1":
                map.put("groupsType", 2 + "");
                break;
            default:
                map.put("groupsType", 3 + "");
                break;
        }
        map.put("requestType", 1 + "");
        map.put("moreType", mSearchType);
        map.put("isoCode", mIso);
        if (mSearchType.equals("2")) {
            map.put("phoneInfo", mPhoneInfoArray.toString());
        }
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
                        Iterator<String> it = result.keys();
                        List<String> keyList = new ArrayList<>();
                        while (it.hasNext()) {
                            keyList.add(it.next());
                        }
                        mList.clear();
                        if (!mSearchType.equals("2")) {
                            for (int i = 0; i < keyList.size(); i++) {
                                JSONArray itemList = result.getJSONArray(keyList.get(i));
                                for (int j = 0; j < itemList.length(); j++) {
                                    JSONObject item = itemList.getJSONObject(j);
                                    PinyinBean pinyinBean = new PinyinBean();
                                    pinyinBean.setId(item.getString("faceId"));
                                    pinyinBean.setName(item.getString("faceName"));
                                    pinyinBean.setTopUrl(item.getString("headUrl"));
                                    pinyinBean.setContent(item.getString("standing"));
                                    mList.add(pinyinBean);
                                }
                            }
                        } else {//通讯录
                            for (int i = 0; i < keyList.size(); i++) {
                                JSONArray itemList = result.getJSONArray(keyList.get(i));
                                for (int j = 0; j < itemList.length(); j++) {
                                    JSONObject item = itemList.getJSONObject(j);
                                    PinyinBean pinyinBean = new PinyinBean();
                                    pinyinBean.setName(item.getString("contactName"));
                                    pinyinBean.setCode(item.getString("countryCode"));
                                    pinyinBean.setPhone(item.getString("mobile"));
                                    pinyinBean.setIsRegister(item.getInt("isRegistered"));
                                    switch (item.getInt("isRegistered")) {
                                        case 0://未注册
                                            pinyinBean.setContent(item.getString("formatMobile"));
                                            break;
                                        case 1://已注册
                                            pinyinBean.setContent(item.getString("standing"));
                                            break;
                                    }
                                    mList.add(pinyinBean);
                                }
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
    }

}
