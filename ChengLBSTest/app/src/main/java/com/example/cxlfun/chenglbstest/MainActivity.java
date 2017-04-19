package com.example.cxlfun.chenglbstest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TextView textView;
    private LocationClient locationClient;
    private StringBuilder currentPosition;
    private MapView baiduView;
    private boolean isFirstLocate = true;
    private BaiduMap baiduMap ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(( new MyLocationListener()));
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.textview);
        baiduView = (MapView) findViewById(R.id.main_baidu_mv);
        baiduMap= baiduView.getMap();
        baiduMap.setMyLocationEnabled(true);


        List<String> permissionList = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permissionList.isEmpty()) {
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this, permissions, 1);
        } else {
            requestLocation();
        }


    }


    private void navigateTo(BDLocation location) {
        if (isFirstLocate) {
            Toast.makeText(this, "nav to " + location.getAddrStr(), Toast.LENGTH_SHORT).show();
            LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
            MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
            baiduMap.animateMapStatus(update);
            update = MapStatusUpdateFactory.zoomTo(16f);
            baiduMap.animateMapStatus(update);
            isFirstLocate = false;
        }
        MyLocationData.Builder locationBuilder = new MyLocationData.
                Builder();
        locationBuilder.latitude(location.getLatitude());
        locationBuilder.longitude(location.getLongitude());
        MyLocationData locationData = locationBuilder.build();
        baiduMap.setMyLocationData(locationData);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED) {
                            Toast.makeText(this, "必须同意所有权限才能使用本程序", Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                } else {
                    Toast.makeText(this, "发生未知错误", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
        }
    }

    protected double transfer(double lititue){

            return  0;
    }

    private void requestLocation() {
        initLocation();
        locationClient.start();
    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setScanSpan(5000);
        option.setIsNeedAddress(true);
        locationClient.setLocOption(option);
    }


    @Override
    protected void onResume() {
        super.onResume();
        baiduView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        baiduView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationClient.stop();
        baiduView.onDestroy();
        baiduMap.setMyLocationEnabled(false);
    }

    public class MyLocationListener implements BDLocationListener {


        @Override
        public void onReceiveLocation(BDLocation location) {
            currentPosition = new StringBuilder();
            currentPosition.append("纬度：").append(location.getLatitude());
            currentPosition.append("经线：").append(location.getLongitude());

//            currentPosition.append("纬度：").append(location.getLatitude()).append("\n");
//            currentPosition.append("经线：").append(location.getLongitude()).append("\n");
//            currentPosition.append("国家：").append(location.getCountry()).append("\n");
//            currentPosition.append("省：").append(location.getProvince()).append("\n");
//            currentPosition.append("市：").append(location.getCity()).append("\n");
//            currentPosition.append("区：").append(location.getDistrict()).append("\n");
//            currentPosition.append("街道：").append(location.getStreet()).append("\n");
//            currentPosition.append("定位方式：");
//            if (location.getLocType() == BDLocation.TypeGpsLocation) {
//                currentPosition.append("GPS");
//            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
//                currentPosition.append("网络");
//            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textView.setText(currentPosition);
                }
            });

            if (location.getLocType() == BDLocation.TypeGpsLocation
                    || location.getLocType() == BDLocation.TypeNetWorkLocation) {


                navigateTo(location);
            }



        }



        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }


    }

//   protected void bd_decrypt(double bd_lat, double bd_lon, double &gg_lat, double &gg_lon)
//    {
//        double x = bd_lon - 0.0065, y = bd_lat - 0.006;
//        double z = sqrt(x * x + y * y) - 0.00002 * sin(y * x_pi);
//        double theta = atan2(y, x) - 0.000003 * cos(x * x_pi);
//        gg_lon = z * cos(theta);
//        gg_lat = z * sin(theta);
//    }


}
