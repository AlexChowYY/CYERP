package com.facewarrant.fw.ui.activity.question;

import android.content.Intent;
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
import com.facewarrant.fw.bean.ClassifyBean;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;
import com.facewarrant.fw.view.SpacesItemDecoration;

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
public class ChooseLabelActivity extends BaseActivity {
    private static final String TAG = "ChooseLabelActivity";
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.rv_activity_choose_label_recommend)
    RecyclerView mRV;


    private RecyclerCommonAdapter<ClassifyBean> mAdapter;
    private List<ClassifyBean> mTopicList = new ArrayList<>();


    @Override
    protected int getContentViewId() {
        return R.layout.activity_choose_label;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.choose_label);
        mRV.setNestedScrollingEnabled(false);
        getTags();

    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void createData() {
        for (int i = 0; i < 15; i++) {
            ClassifyBean classifyBean = new ClassifyBean();
            classifyBean.setName("世界杯");
            mTopicList.add(classifyBean);
        }
    }

    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<ClassifyBean>(mActivity, R.layout.item_choose_topic, mTopicList) {
                @Override
                protected void convert(ViewHolder holder, final ClassifyBean classifyBean, int position) {
                    holder.setText(R.id.tv_item_choose_label_label, classifyBean.getName());
                    holder.getConvertView().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra(Constant.LABEL, classifyBean.getName());
                            setResult(100, intent);
                            finish();

                        }
                    });

                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mRV.setLayoutManager(linearLayoutManager);
            mRV.addItemDecoration(new DividerItemDecoration(mActivity, DividerItemDecoration.VERTICAL));
            mRV.setAdapter(mAdapter);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void getTags() {
        final Map<String, String> map = new HashMap<>();
        map.put("flag", 1 + "");
        RequestManager.mRetrofitManager
                .createRequest(RetrofitRequestInterface.class)
                .getRecommendTags(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                JSONArray result = data.getJSONArray("result");
                                for (int i = 0; i < result.length(); i++) {
                                    ClassifyBean classifyBean = new ClassifyBean();
                                    classifyBean.setName(result.getString(i));
                                    mTopicList.add(classifyBean);
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
