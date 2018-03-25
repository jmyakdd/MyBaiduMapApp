package com.jmy.mybaidumapapp.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.BaiduMap
import com.baidu.mapapi.map.LogoPosition
import com.baidu.mapapi.map.MyLocationData
import com.jmy.mybaidumapapp.R
import kotlinx.android.synthetic.main.activity_main.*
import android.location.LocationManager
import android.content.Context.LOCATION_SERVICE
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.MapStatusUpdate
import com.baidu.mapapi.search.core.RouteNode.location
import com.baidu.mapapi.model.LatLng






class MainActivity : AppCompatActivity() {

    private val BAIDU_READ_PHONE_STATE = 100
    val permissions = arrayOf(Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
            )

    lateinit var mBaiduMap:BaiduMap
    lateinit var mLocationClient:LocationClient
    var isFirstLocate = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBaiduMap = mMapView.map
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true)
        mLocationClient = LocationClient(this)
        mLocationClient.registerLocationListener(object :BDAbstractLocationListener(){
            override fun onReceiveLocation(p0: BDLocation?) {
                if (isFirstLocate) {
                    //            /*获取经纬度*/
                    val latLng = LatLng(p0!!.getLatitude(), p0.getLongitude())
                    var update = MapStatusUpdateFactory.newLatLng(latLng)
                    mBaiduMap.animateMapStatus(update)
                    update = MapStatusUpdateFactory.zoomTo(16f)
                    mBaiduMap.animateMapStatus(update)
                    isFirstLocate = false
                }
                mBaiduMap.clear()
                var locationData = MyLocationData.Builder()
                        .accuracy(p0!!.radius)
                        .direction(100f)
                        .latitude(p0.altitude)
                        .longitude(p0.longitude)
                        .build()
                mBaiduMap.setMyLocationData(locationData)
            }
        })
        var option = LocationClientOption()
        option.openGps = true
        option.setCoorType("bd09ll")
        option.setScanSpan(1000)
        option.setIsNeedAddress(true)
        mLocationClient.locOption = option
        val locManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            // 未打开位置开关，可能导致定位失败或定位不准，提示用户或做相应处理
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions,BAIDU_READ_PHONE_STATE)
                    return
                }
            }
        }
        mLocationClient.start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==BAIDU_READ_PHONE_STATE){
            for (i in permissions.indices) {
                if (grantResults.get(i) == PackageManager.PERMISSION_GRANTED) {
                    mLocationClient.start()
                }
            }
        }
    }
}
