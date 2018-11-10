package com.facewarrant.fw.ui.personal;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.GoodsBean;
import com.facewarrant.fw.util.DisplayUtil;
import com.facewarrant.fw.view.GridSpacingItemDecoration;
import com.facewarrant.fw.view.SpaceItemDecoration;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * created  by  Alex
 * e-mail:15062859867@163.com
 */
public class BrandDetailActivity extends BaseActivity {
    private static final String TAG = "BrandDetailActivity";
    @BindView(R.id.rv_activity_brand_detail)
    RecyclerView mRV;
    @BindView(R.id.iv_activity_brand_detail_back)
    ImageView mBackIV;
    @BindView(R.id.iv_activity_brand_detail_more)
    ImageView mMoreIV;
    @BindView(R.id.tv_activity_brand_detail_content)
    TextView mContentTV;
    private boolean isExpand = false;
    private RecyclerCommonAdapter<GoodsBean> mAdapter;
    private List<GoodsBean> mList = new ArrayList<>();

    @Override
    protected int getContentViewId() {
        return R.layout.activity_brand_detail;
    }

    @Override
    protected void beforeSetContentView() {
        super.beforeSetContentView();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    @Override
    public void initData() {
        mRV.setNestedScrollingEnabled(false);
        createData();
        showRecyclerView();
        mContentTV.setText("下面的函数就是上面效果展示中展示的例子，通过上面在不绘制UI的前提下获得最大行末尾文字下标，然后让源字符串subString这个下标，在获得的结果上加上“...read more”，然后将添加这一段文字设置点击事件，一个“显示更多”的功能就做好了。");

    }

    @Override
    public void initEvent() {
        mBackIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mMoreIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isExpand = !isExpand;
                if (isExpand) {
                    mContentTV.setEllipsize(null);
                    mContentTV.setSingleLine(false);

                } else {
                    mContentTV.setEllipsize(TextUtils.TruncateAt.END);
                    mContentTV.setLines(2);
                }

            }
        });
    }

    private void createData() {
        for (int i = 0; i < 12; i++) {
            GoodsBean goodsBean = new GoodsBean();
            //goodsBean.setTopUrl("https://ss0.bdstatic.com/-0U0bnSm1A5BphGlnYG/tam-ogel/1891b719eb517d05a7b00f591a4a1d60_222_222.jpg");
            goodsBean.setTopUrl("https://www.bulgari.cn/media/wysiwyg/cms_page/alpencms/campaign_2016/534x566.jpg");
            goodsBean.setTime("2018-12-3");
            goodsBean.setName("欧莱雅");
            goodsBean.setFollow("234");
            goodsBean.setCarNumber("12");
            mList.add(goodsBean);
        }


    }

    private void showRecyclerView() {
        if (mAdapter == null) {
            mAdapter = new RecyclerCommonAdapter<GoodsBean>(mActivity, R.layout.item_my_warrant, mList) {
                @Override
                protected void convert(ViewHolder holder, GoodsBean goodsBean, int position) {
                    RoundedImageView topRIV = holder.getView(R.id.riv_item_my_warrant_top);
                    holder.setText(R.id.tv_item_my_warrant_name, goodsBean.getName());
                    holder.setText(R.id.tv_item_my_warrant_follow, goodsBean.getFollow());
                    holder.setText(R.id.tv_item_my_warrant_time, goodsBean.getTime());
                    holder.setText(R.id.tv_item_my_warrant_car_number, goodsBean.getCarNumber());
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) topRIV.getLayoutParams();
                    params.height = (int) ((DisplayUtil.getScreenWidth(mActivity) - 75) / 2 * (Math.random() + 1));
                    Glide.with(mActivity).load(goodsBean.getTopUrl()).into(topRIV);


                }
            };
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            mRV.setLayoutManager(staggeredGridLayoutManager);
            mRV.addItemDecoration(new SpaceItemDecoration(25, 2));
            mRV.setAdapter(mAdapter);

        } else {
            mAdapter.notifyDataSetChanged();
        }
    }
}
