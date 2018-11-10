package com.facewarrant.fw.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.event.UpdateFaceListEvent;
import com.facewarrant.fw.event.UpdateSearchHistoryEvent;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

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
public class SearchActivity extends BaseActivity {
    private static final String TAG = "SearchActivity";
    @BindView(R.id.tfl_activity_search)
    TagFlowLayout mHistoryTFL;
    @BindView(R.id.tfl_activity_search_hot)
    TagFlowLayout mHotTFL;
    @BindView(R.id.tv_activity_search_cancel)
    TextView mCancelTV;
    @BindView(R.id.iv_activity_search_search)
    ImageView mSearchIV;
    @BindView(R.id.et_activity_search)
    EditText mSearchET;
    @BindView(R.id.iv_activity_search_clear)
    ImageView mClearIV;

    private List<String> mHistoryList = new ArrayList<>();
    private List<String> mHotList = new ArrayList<>();


    public static final int TYPE_TEXT = 1;
    public static final int TYPE_VOICE = 2;
    /**
     * 是否从face库那边跳转过来
     */
    private boolean mIsLibrary = false;


    private int mType;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_search;
    }

    @Override
    public void initData() {
        EventBus.getDefault().register(mActivity);
        getHistory();

        if (getIntent() != null) {
            if (getIntent().getExtras() != null) {
                mType = getIntent().getExtras().getInt(Constant.TYPE);
                mIsLibrary = getIntent().getExtras().getBoolean(Constant.LIBRARY);
            }
        }
        switch (mType) {
            case TYPE_TEXT:
                ToastUtil.showShort(mActivity, "文本搜索");
                break;
            case TYPE_VOICE:
                ToastUtil.showShort(mActivity, "语音搜索");
                break;
        }


    }

    @Override
    public void initEvent() {
        mCancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSearchIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keyword = mSearchET.getText().toString().trim();
                if (TextUtils.isEmpty(keyword)) {
                    ToastUtil.showShort(mActivity, "请填写搜索关键字！");
                } else {
                    if (!mIsLibrary) {
                        SearchResultActivity.open(mActivity, keyword);
                    } else {
                        SearchLibraryActivity.open(mActivity, keyword);
                    }

                }

            }
        });
        mSearchET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String keyword = mSearchET.getText().toString().toString().trim();

                    if (TextUtils.isEmpty(keyword)) {
                        ToastUtil.showShort(mActivity, "请填写关键字！");
                    } else {
                        if (!mIsLibrary) {
                            SearchResultActivity.open(mActivity, keyword);
                        } else {
                            SearchLibraryActivity.open(mActivity, keyword);
                        }
                    }
                    return true;
                }
                return false;
            }
        });
        mClearIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearHistory();
            }
        });

    }


    public static void open(Activity activity, int type, boolean isLibrary) {
        Intent intent = new Intent(activity, SearchActivity.class);
        intent.putExtra(Constant.TYPE, type);
        intent.putExtra(Constant.LIBRARY, isLibrary);
        activity.startActivity(intent);
    }

    private void clearHistory() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .clearSearchHistory(RequestManager.encryptParams(map)).
                enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                mHistoryList.clear();
                                mHistoryTFL.onChanged();
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

    private void getHistory() {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .getSearchHistory(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                mHistoryList.clear();
                                mHotList.clear();
                                JSONObject result = data.getJSONObject("result");
                                JSONArray hotArray = result.getJSONArray("hotSearchList");
                                for (int i = 0; i < hotArray.length(); i++) {
                                    mHotList.add(hotArray.getJSONObject(i).getString("searchCondition"));
                                }
                                JSONArray historyArray = result.getJSONArray("historySearchList");
                                for (int i = 0; i < historyArray.length(); i++) {
                                    mHistoryList.add(historyArray.getJSONObject(i).getString("searchContent"));
                                }
                                initHistoryAndHot();
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

    private void initHistoryAndHot() {
        mHistoryTFL.setAdapter(new TagAdapter<String>(mHistoryList) {
            @Override
            public View getView(FlowLayout parent, int position, final String s) {
                TextView textView = (TextView) mInflater.inflate(R.layout.item_search, null);
                textView.setText(s);
                return textView;
            }
        });
        mHistoryTFL.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                if (mIsLibrary) {
                    SearchLibraryActivity.open(mActivity, mHistoryList.get(position));
                } else {
                    SearchResultActivity.open(mActivity, mHistoryList.get(position));
                }

                return true;
            }
        });
        mHotTFL.setAdapter(new TagAdapter<String>(mHotList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView textView = (TextView) mInflater.inflate(R.layout.item_search, null);
                textView.setText(s);
                return textView;
            }
        });
        mHotTFL.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                if (mIsLibrary) {
                    SearchLibraryActivity.open(mActivity, mHotList.get(position));
                } else {
                    SearchResultActivity.open(mActivity, mHotList.get(position));
                }
                return true;
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void freshList(UpdateSearchHistoryEvent updateSearchHistoryEvent) {
        getHistory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(mActivity);
    }
}
