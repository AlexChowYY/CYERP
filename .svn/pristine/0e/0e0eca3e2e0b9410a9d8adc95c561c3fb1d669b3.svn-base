package com.facewarrant.fw.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ZoomControls;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.facewarrant.fw.R;
import com.facewarrant.fw.adapter.recycler.RecyclerCommonAdapter;
import com.facewarrant.fw.adapter.recycler.base.ViewHolder;
import com.facewarrant.fw.base.BaseActivity;
import com.facewarrant.fw.bean.NearbyStoreBean;
import com.facewarrant.fw.global.Constant;
import com.facewarrant.fw.net.RequestManager;
import com.facewarrant.fw.net.RetrofitCallBack;
import com.facewarrant.fw.net.RetrofitRequestInterface;
import com.facewarrant.fw.util.DisplayUtil;
import com.facewarrant.fw.util.LogUtil;
import com.facewarrant.fw.util.ToastUtil;

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
public class NearbyStoreActivity extends BaseActivity {
    private static final String TAG = "NearbyStoreActivity";
    @BindView(R.id.mv_activity_nearby_store)
    MapView mMapView;
    private BaiduMap mBaiduMap;
    @BindView(R.id.tv_layout_top_back_title)
    TextView mTitleTV;
    @BindView(R.id.iv_layout_top_back)
    ImageView mBackIV;
    @BindView(R.id.rv_bottom_sheet)
    RecyclerView mRV;
    private RecyclerCommonAdapter<NearbyStoreBean> mAdapter;
    private List<NearbyStoreBean> mList = new ArrayList<>();


    LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private int mCurrentDirection = 0;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    boolean isFirstLoc = true; // 是否首次定位
    private MyLocationData locData;
    private MyLocationConfiguration.LocationMode mCurrentMode;

    private boolean isLoctionSuccess = false;

    private BitmapDescriptor mStoreBD = BitmapDescriptorFactory.fromResource(R.drawable.stroe_marker);

    private List<Marker> mMarkerList = new ArrayList<>();


    private int mType;
    /**
     * 附近商店
     */
    public static final int TYPE_STORE = 2;
    /**
     * 附近商场
     */
    public static final int TYPE_MARKET = 1;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_nearby_store;
    }

    @Override
    public void initData() {

        mTitleTV.setText(R.string.nearby_store);
        if (getIntent() != null && getIntent().getExtras() != null) {
            mType = getIntent().getExtras().getInt(Constant.TYPE);

        }
        initBadduMap();
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

    private void initBadduMap() {

        mLocClient = new LocationClient(getApplicationContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(false); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setScanSpan(1000);
        mLocClient.setLocOption(option);
        mLocClient.start();

        mBaiduMap = mMapView.getMap();
        // 隐藏logo
        View child = mMapView.getChildAt(1);
        if (child != null && (child instanceof ImageView || child instanceof ZoomControls)) {
            child.setVisibility(View.INVISIBLE);
        }
        mMapView.showScaleControl(false);
        mMapView.showZoomControls(false);
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
        mBaiduMap.setMyLocationEnabled(true);
        mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                mCurrentMode, true, null));
        MapStatus.Builder builder1 = new MapStatus.Builder();
        builder1.overlook(0);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder1.build()));
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for (int i = 0; i < mMarkerList.size(); i++) {
                    Marker marker1 = mMarkerList.get(i);
                    if (marker == marker1) {
                        mRV.scrollToPosition(i);
                    }
                }
                return true;
            }
        });

    }

    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
        //为系统的方向传感器注册监听器

    }

    @Override
    protected void onStop() {
        //取消注册传感器监听

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        // 退出时销毁定位
        mLocClient.stop();
        // 关闭定位图层
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }

    /**
     * 定位SDK监听函数
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            isLoctionSuccess = true;

            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(18.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

                getNearbyStore();
            }
        }

        public void onReceivePoi(BDLocation poiLocation) {
        }
    }

    private void addMarker() {
        mMarkerList.clear();
        for (int i = 0; i < mList.size(); i++) {
            NearbyStoreBean nearbyStoreBean = mList.get(i);
            LatLng latLng = new LatLng(nearbyStoreBean.getLat(),
                    nearbyStoreBean.getLng());
            MarkerOptions ooA = new MarkerOptions().position(latLng).icon(mStoreBD)
                    .zIndex(9).draggable(false);
            Marker marker = (Marker) mBaiduMap.addOverlay(ooA);
            mMarkerList.add(marker);
        }


    }


    private void getNearbyStore() {
        final Map<String, String> map = new HashMap<>();
        switch (mType) {
            case TYPE_MARKET:
                map.put("storeType", 1 + "");//店铺类型，0 所有类型,1商场，2实体店
                break;
            case TYPE_STORE:
                map.put("storeType", 2 + "");//店铺类型，0 所有类型,1商场，2实体店
                break;
        }

        map.put("lng", mCurrentLon + "");
        map.put("lat", mCurrentLat + "");
        map.put("page", 1 + "");
        map.put("rows", 15 + "");
        LogUtil.e(TAG, map.toString());
        RequestManager.mRetrofitManager.createRequest(RetrofitRequestInterface.class)
                .getNearbyStore(RequestManager.encryptParams(map))
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
                                    NearbyStoreBean nearbyStoreBean = new NearbyStoreBean();
                                    nearbyStoreBean.setId(resultItem.getString("storeId"));
                                    nearbyStoreBean.setAddress(resultItem.getString("storeAddress"));
                                    nearbyStoreBean.setLng((float) resultItem.getDouble("longitude"));
                                    nearbyStoreBean.setLat((float) resultItem.getDouble("latitude"));
                                    nearbyStoreBean.setName(resultItem.getString("storeName"));
                                    nearbyStoreBean.setNavNumber(resultItem.getString("navigationCount"));
                                    nearbyStoreBean.setPhoneNumber(resultItem.getString("phoneCount"));
                                    nearbyStoreBean.setTips(resultItem.getString("storePromotion"));
                                    nearbyStoreBean.setPhone(resultItem.getString("storeMobile"));
                                    mList.add(nearbyStoreBean);
                                }
                                showRecyclerView();
                                addMarker();

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
            mAdapter = new RecyclerCommonAdapter<NearbyStoreBean>(mActivity, R.layout.item_bottom_sheet, mList) {
                @Override
                protected void convert(ViewHolder holder, NearbyStoreBean nearbyStoreBean, int position) {
                    holder.setText(R.id.tv_item_bottom_sheet_address, nearbyStoreBean.getAddress());
                    holder.setText(R.id.tv_item_bottom_sheet_name, nearbyStoreBean.getName());
                    if (!nearbyStoreBean.getTips().equals("null")) {
                        holder.setText(R.id.tv_item_bottom_sheet_promotion, nearbyStoreBean.getTips());
                    } else {
                        holder.setText(R.id.tv_item_bottom_sheet_promotion, "暂无优惠活动");
                    }
                    holder.setText(R.id.tv_item_bottom_sheet_phone_num, nearbyStoreBean.getPhoneNumber() + "次");
                    holder.setText(R.id.tv_item_bottom_sheet_nav_num, nearbyStoreBean.getNavNumber() + "次");
                }
            };
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
            mRV.setLayoutManager(linearLayoutManager);
            mRV.setAdapter(mAdapter);


        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    public static void open(Activity activity, int type) {
        Intent intent = new Intent(activity, NearbyStoreActivity.class);
        intent.putExtra(Constant.TYPE, type);
        activity.startActivity(intent);
    }


}
