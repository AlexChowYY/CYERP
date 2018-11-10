package com.facewarrant.fw.ui.activity.withdraw;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.PinyinBean;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class ChooseBankActivity extends BaseActivity {
    private static final String TAG = "ChooseBankActivity";
    @BindView(R.id.rv_activity_choose_bank)
    RecyclerView mRV;
    @BindView(R.id.ib_activity_choose_bank)
    IndexBar mIB;
    @BindView(R.id.tvSideBarHint)
    TextView mHintTV;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;

    private List<PinyinBean> mList = new ArrayList<>();
    private RecyclerCommonAdapter<PinyinBean> mAdapter;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_choose_bank;
    }

    @Override
    public void initData() {
        mTitleTV.setText(R.string.choose_bank);
        createData();
        showRecyclerView();

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
        for (int i = 0; i < 10; i++) {
            PinyinBean pinyinBean = new PinyinBean();
            pinyinBean.setName("建设银行");
            mList.add(pinyinBean);
        }
    }

    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<PinyinBean>(mActivity, R.layout.item_choose_bank, mList) {
                @Override
                protected void convert(ViewHolder holder, PinyinBean pinyinBean, int position) {
                    holder.setText(R.id.tv_item_choose_bank_name, pinyinBean.getName());

                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mRV.setLayoutManager(linearLayoutManager);
            mRV.setAdapter(mAdapter);
            mRV.addItemDecoration(new SuspensionDecoration(mActivity, mList).setColorTitleBg(ContextCompat.getColor(mActivity, R.color.colorBg)).setmTitleHeight(10));
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
}
