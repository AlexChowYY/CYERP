package com.facewarrant.fw.ui.activity.mine;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.global.AccountManager;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class FeedbackActivity extends BaseActivity {
    private static final String TAG = "FeedbackActivity";
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.et_activity_feedback)
    EditText mFeedbackET;
    @BindView(R.id.tv_activity_feedback_commit)
    TextView mCommitTV;
    @BindView(R.id.tv_activity_feedback_number_count)
    TextView mNumberCountTV;

    private PopupWindow mPopupWindow;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_feedback;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.feedback);
    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (TextUtils.isEmpty(mFeedbackET.getText().toString())) {
            mNumberCountTV.setText("0" + "/200");
        } else {
            mNumberCountTV.setText(mFeedbackET.getText().length() + "/20");
        }

        showCharNumber(200);
        mCommitTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String feedback = mFeedbackET.getText().toString().trim();
                if (TextUtils.isEmpty(feedback)) {
                    ToastUtil.showShort(mActivity, "请输入意见！");
                } else {
                    feedback(feedback);
                }
            }
        });

    }

    private void showCharNumber(final int maxNumber) {
        mFeedbackET.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;
            private int selectionStart;
            private int selectionEnd;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int number = s.length();
                mNumberCountTV.setText(number + "/" + maxNumber);
                selectionStart = mFeedbackET.getSelectionStart();
                selectionEnd = mFeedbackET.getSelectionEnd();
                //System.out.println("start="+selectionStart+",end="+selectionEnd);
                if (temp.length() > maxNumber) {
                    s.delete(selectionStart - 1, selectionEnd);
                    int tempSelection = selectionStart;
                    mFeedbackET.setText(s);
                    mFeedbackET.setSelection(tempSelection);
                }
            }
        });
    }

    private void feedback(String feedback) {
        final Map<String, String> map = new HashMap<>();
        map.put("userId", AccountManager.sUserBean.getId());
        map.put("suggestion", feedback);
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .feedback(RequestManager.encryptParams(map))
                .enqueue(new RetrofitCallBack() {
                    @Override
                    public void onSuccess(String response) {
                        LogUtil.e(TAG, response.toString());
                        try {
                            JSONObject data = new JSONObject(response);
                            if (data.getInt("resultCode") == 200) {
                                showPopupWindow();
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

    private void showPopupWindow() {
        if (mPopupWindow != null && mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        } else {
            View view = mInflater.inflate(R.layout.pop_feedback_success, null);
            TextView cancelTV = view.findViewById(R.id.tv_cancel);
            cancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mPopupWindow != null && mPopupWindow.isShowing()) {
                        mPopupWindow.dismiss();
                    }
                    finish();
                }
            });
            mPopupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, 0, 0);

        }
    }
}
