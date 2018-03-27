package com.jmy.mybaidumapapp.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.*
import com.baidu.mapapi.model.LatLng
import com.jmy.mybaidumapapp.R
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val BAIDU_READ_PHONE_STATE = 100
    val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    )

    lateinit var mBaiduMap: BaiduMap
    lateinit var mLocationClient: LocationClient
    var isFirstLocate = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mBaiduMap = mMapView.map
        initMapView()
        initMap()
        initLocation()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (permission in permissions) {
                if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(permissions, BAIDU_READ_PHONE_STATE)
                    return
                }
            }
        }
        mLocationClient.start()
    }

    private fun addPoint(p0: LatLng?) {
        //定义Maker坐标点
        val point = LatLng(p0!!.latitude, p0.longitude)
        //构建Marker图标
        val bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.ic_launcher)
        //构建MarkerOption，用于在地图上添加Marker
        val option = MarkerOptions()
                .position(point)
                .icon(bitmap)
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option)
    }

    private fun initMapView() {
        mMapView.showZoomControls(true)
        mMapView.showScaleControl(true)
    }

    private fun initMap() {
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true)
        mBaiduMap.mapType = BaiduMap.MAP_TYPE_NORMAL
        mBaiduMap.isTrafficEnabled = false
        mBaiduMap.setOnMapClickListener(object :BaiduMap.OnMapClickListener{
            override fun onMapClick(p0: LatLng?) {
                addPoint(p0)
            }

            override fun onMapPoiClick(p0: MapPoi?): Boolean {
                return false
            }
        })
        mBaiduMap.setOnMarkerClickListener(object :BaiduMap.OnMarkerClickListener{
            override fun onMarkerClick(p0: Marker?): Boolean {
                if (p0 != null) {
                    var la = p0.position
                    Toast.makeText(this@MainActivity,la.latitude.toString()+" "+la.longitude,Toast.LENGTH_LONG).show()
                }
                return false
            }
        })
    }

    private fun initLocation() {
        mLocationClient = LocationClient(this)
        mLocationClient.registerLocationListener(object : BDAbstractLocationListener() {
            override fun onReceiveLocation(p0: BDLocation?) {
                if(p0==null){
                    return
                }
                var locType = p0.locType
                if(locType==62){
                    return
                }
                var locationData = MyLocationData.Builder()
                        .accuracy(p0.radius)
                        .direction(p0.radius)
                        .latitude(p0.latitude)
                        .longitude(p0.longitude)
                        .build()
                mBaiduMap.setMyLocationData(locationData)
                if (isFirstLocate) {
                    //            获取经纬度
                    val latLng = LatLng(p0.latitude, p0.longitude)
                    var builder = MapStatus.Builder()
                    builder.target(latLng).zoom(18f)
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()))
                    isFirstLocate = false
                }
            }
        })
        var option = LocationClientOption()
        option.locationMode = LocationClientOption.LocationMode.Hight_Accuracy
        option.openGps = true
        option.setCoorType("bd09ll")
        option.setScanSpan(0)
        option.setIsNeedAddress(true)
        mLocationClient.locOption = option
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == BAIDU_READ_PHONE_STATE) {
            for (i in permissions.indices) {
                if (grantResults.get(i) == PackageManager.PERMISSION_GRANTED) {
                    mLocationClient.start()
                }
            }
        }
    }
}
