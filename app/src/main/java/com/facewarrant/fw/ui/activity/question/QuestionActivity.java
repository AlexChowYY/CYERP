package com.facewarrant.fw.ui.activity.question;

import android.content.Intent;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.PinyinBean;
import com.facewarrant.fw.event.UpdateAiTeList;
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
public class QuestionActivity extends BaseActivity {
    private static final String TAG = "QuestionActivity";
    @BindView(R.id.tfl_activity_question)
    TagFlowLayout mQuestionTFL;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_setting)
    TextView mSettingTV;

    @BindView(R.id.iv_activity_question_topic)
    ImageView mTopicIV;
    @BindView(R.id.iv_activity_question_voice)
    ImageView mVoiceIV;
    @BindView(R.id.iv_activity_question_face)
    ImageView mFaceIV;
    @BindView(R.id.et_activity_question_content)
    EditText mContentET;
    @BindView(R.id.tv_activity_question_ai_te)
    TextView mAiTeTV;


    public static final int REQUEST_LABEL_CODE = 100;


    private List<PinyinBean> mAiTeList = new ArrayList<>();


    private List<String> mLabelStringList = new ArrayList<>();
    private String mGIds;
    private String mFIds;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_question;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.question);
        mSettingTV.setText(R.string.release);
        EventBus.getDefault().register(this);

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
        mQuestionTFL.setOnTagClickListener(new TagFlowLayout.OnTagClickListener() {
            @Override
            public boolean onTagClick(View view, int position, FlowLayout parent) {
                ToastUtil.showShort(mActivity, mLabelStringList.get(position));
                String content = mContentET.getText().toString();
                StringBuilder stringBuilder = new StringBuilder(content);
                stringBuilder.append(mLabelStringList.get(position));
                mContentET.setText(stringBuilder.toString() + " ");
                mContentET.setSelection(stringBuilder.length() + 1);
                return true;
            }
        });
        mTopicIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(mActivity, ChooseLabelActivity.class), REQUEST_LABEL_CODE);
            }
        });
        mFaceIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, ChooseFaceActivity.class);
                intent.putParcelableArrayListExtra(Constant.TYPE, (ArrayList<? extends Parcelable>) mAiTeList);
                startActivity(intent);
            }
        });
        mContentET.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {//这里我们对edit进行按键监听
                if (!(mContentET.getSelectionEnd() - mContentET.getSelectionStart() > 0)) {
                    if (keyCode == KeyEvent.KEYCODE_DEL
                            && event.getAction() == KeyEvent.ACTION_DOWN) {//判断是否为按下并且是键盘的删除键
                        String s = mContentET.getText().toString();//获取edit的内容
                        int length = s.length();//获取文本的长度
                        int select = mContentET.getSelectionEnd();//获取我们选择的位置
                        LogUtil.e(TAG, "select=" + select + "");
                        if (length > 0 && select != 0) {//判断文本长度是否大于0并且判断光标位置是否为0
                            if (" ".equals(s.substring(select - 1, select))) {//判断光标所处的位置的前一个是否是空格
                                String reg = ".*#.*";
                                if (s.substring(0, select).matches(reg)) {//判断光标所处的位置到开头还有没有@符号
                                    //有的话获取到最后一个@符号的位置
                                    int i = s.substring(0, s.lastIndexOf("#")).lastIndexOf("#");
                                    LogUtil.e(TAG, "i=" + i + "");
                                    String reg2 = ".* .*";
                                    if (!s.substring(i, select - 1).matches(reg2)) {//判断字符串中@符号位置到光标所处位置是否含有空格
                                        mContentET.setSelection(i, select);//有的话就选中，说明这一段是@的人了
                                        return true;//返回true，不返回的话会立马删除
                                    }
                                }
                            }
                            //获取光标位置
                        }
                    }
                }

                return false;
            }
        });
        mSettingTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = mContentET.getText().toString();
                if (TextUtils.isEmpty(content)) {
                    ToastUtil.showShort(mActivity, "请填写提问内容！");
                } else if (TextUtils.isEmpty(mGIds) && TextUtils.isEmpty(mFIds)) {
                    ToastUtil.showShort(mActivity, "请选择一个@的群或Face！");
                } else {
                    question(content);
                }

            }
        });

    }


    private void getTags() {
        final Map<String, String> map = new HashMap<>();
        map.put("flag", 0 + "");
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
                                    mLabelStringList.add("#" + result.getString(i) + "#");
                                }
                                showTag();
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

    private void showTag() {
        mQuestionTFL.setAdapter(new TagAdapter<String>(mLabelStringList) {
            @Override
            public View getView(FlowLayout parent, int position, String s) {
                TextView textView = (TextView) mInflater.inflate(R.layout.item_label, null);
                textView.setText(s);
                return textView;
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_LABEL_CODE && data != null) {
            String label = data.getExtras().getString(Constant.LABEL);
            String content = mContentET.getText().toString();
            mContentET.setText(content + "#" + label + "# ");
            String content1 = mContentET.getText().toString();
            mContentET.setSelection(content1.length());
        }
    }

    private void question(String content) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("questionContent", content);
        if (!TextUtils.isEmpty(mGIds)) {
            map.put("groupsIds", mGIds.substring(0, mGIds.length() - 1));
        }
        if (!TextUtils.isEmpty(mFIds)) {
            map.put("faceIds", mFIds.substring(0, mFIds.length() - 1));
        }
        LogUtil.e(TAG, map.toString());

        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .question(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                ToastUtil.showShort(mActivity, "提问成功！");
                                finish();

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
    public void fresh(UpdateAiTeList updateAiTeList) {
        mAiTeList.clear();
        mAiTeList.addAll(updateAiTeList.getList());
        StringBuilder stringBuilder = new StringBuilder();
        mGIds = "";
        mFIds = "";
        for (int i = 0; i < mAiTeList.size(); i++) {
            stringBuilder.append("@" + mAiTeList.get(i).getName() + " ");
            PinyinBean pinyinBean = mAiTeList.get(i);
            if (pinyinBean.getType().equals(ChooseFaceActivity.TYPE_FACE)) {
                mFIds = mFIds + pinyinBean.getId() + ",";
            } else if (pinyinBean.getType().equals(ChooseFaceActivity.TYPE_GROUP)) {
                mGIds = mGIds + pinyinBean.getId() + ",";
            }
        }
        LogUtil.e(TAG, "mfid= " + mFIds + " mGIds=" + mGIds);
        mAiTeTV.setText(stringBuilder.toString());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


}
