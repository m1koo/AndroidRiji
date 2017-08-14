package com.zd.miko.riji.MVP.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

public class LocationService extends Service {
    public LocationService() {
    }


    public interface LocationResultListener {
        void onGetResult(AMapLocation location);
    }

    LocationResultListener listener;

    public void setListener(LocationResultListener listener) {
        this.listener = listener;
    }

    public AMapLocationClient mLocationClient = null;
    public AMapLocationListener mLocationListener = amapLocation -> {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                listener.onGetResult(amapLocation);
            } else {
                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e("AmapError", "location Error, ErrCode:"
                        + amapLocation.getErrorCode() + ", errInfo:"
                        + amapLocation.getErrorInfo());
            }
        }
    };

    public AMapLocationClientOption mLocationOption = null;


    private MyBinder mBinder = new MyBinder();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //返回实例
        return mBinder;
    }


    @Override
    public boolean onUnbind(Intent intent) {
        mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。

        return super.onUnbind(intent);
    }

    public class MyBinder extends Binder {

        public LocationService getService() {
            return LocationService.this;
        }

        public void startLocate() {
            mLocationClient = new AMapLocationClient(getApplicationContext());
            mLocationClient.setLocationListener(mLocationListener);
            //声明AMapLocationClientOption对象
            mLocationOption = new AMapLocationClientOption();
            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
            mLocationOption.setLocationMode(AMapLocationClientOption
                    .AMapLocationMode.Hight_Accuracy);
            mLocationOption.setOnceLocationLatest(true);

            mLocationOption.setNeedAddress(true);

            mLocationClient.setLocationOption(mLocationOption);
            mLocationClient.startLocation();

        }

        public void stopLocate() {
            mLocationClient.onDestroy();//销毁定位客户端，同时销毁本地定位服务。
        }
    }

}
