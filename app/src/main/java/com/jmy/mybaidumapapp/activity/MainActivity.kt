package com.jmy.mybaidumapapp.activity

import android.location.Location
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


class MainActivity : AppCompatActivity() {
    lateinit var mBaiduMap:BaiduMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBaiduMap = mMapView.map
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true)
        var mLocationClient = LocationClient(this)
        mLocationClient.registerLocationListener(object :BDAbstractLocationListener(){
            override fun onReceiveLocation(p0: BDLocation?) {
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
        mLocationClient.locOption = option
        mLocationClient.start()
    }
}
