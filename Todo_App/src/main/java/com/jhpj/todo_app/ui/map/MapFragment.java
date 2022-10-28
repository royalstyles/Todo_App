package com.jhpj.todo_app.ui.map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jhpj.todo_app.PermissionSupport;
import com.jhpj.todo_app.R;
import com.jhpj.todo_app.databinding.FragmentMapBinding;

import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapReverseGeoCoder;
import net.daum.mf.map.api.MapView;

import java.util.List;
import java.util.Locale;

public class MapFragment extends Fragment implements MapView.MapViewEventListener, MapView.CurrentLocationEventListener, MapReverseGeoCoder.ReverseGeoCodingResultListener, MapView.POIItemEventListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private static final int PERMISSIONS_REQUEST_ACCESS_CALL_PHONE = 2;

    // 1. kakao map api를 이용하기 위해선 keyHash 를 등록해준다.
    private FragmentMapBinding binding;
    private View root;

    // 권한 클래스를 선언
    private PermissionSupport permissionSupport;

    private Activity activity;
    private Context context;
    private LocationManager lm;
    private MapView mapView;

    public double longitude; //경도
    public double latitude; //위도
    public double altitude; //고도
    public float accuracy; //정확도
    public String provider; //위치제공자
    public String currentLocation; // 그래서 최종 위치

    private static final int GPS_ENABLE_REQUEST_CODE = 1000;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        activity = getActivity();
        context = getContext();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
//        MapViewModel mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        root = binding.getRoot();

//        final TextView textView = binding.text_map;
//        mapViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // 권한 객체 생성
        permissionSupport = new PermissionSupport(activity, context);

        // 권한 체크
        permissionSupport.checkAll();

        // 맵뷰 띄우기
        initMap();

        // 위치정보를 얻는다
//        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithHeading);
        getLocation();

        return root;
    }

    @Override
    public void onDestroyView() {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onDestroyView();
        binding = null;
    }

    //맵 뷰를 띄워준다.
    private void initMap(){
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());

        mapView = new MapView(context);

        // 중심점 변경
//        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);

        ViewGroup mapViewContainer = root.findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);

        // Location 제공자에서 정보를 얻어오기(GPS)
        // 1. Location을 사용하기 위한 권한을 얻어와야한다 AndroidManifest.xml
        //     ACCESS_FINE_LOCATION : NETWORK_PROVIDER, GPS_PROVIDER
        //     ACCESS_COARSE_LOCATION : NETWORK_PROVIDER
        // 2. LocationManager 를 통해서 원하는 제공자의 리스너 등록
        // 3. GPS 는 에뮬레이터에서는 기본적으로 동작하지 않는다
        // 4. 실내에서는 GPS_PROVIDER 를 요청해도 응답이 없다.  특별한 처리를 안하면 아무리 시간이 지나도
        //    응답이 없다.
        //    해결방법은
        //     ① 타이머를 설정하여 GPS_PROVIDER 에서 일정시간 응답이 없는 경우 NETWORK_PROVIDER로 전환
        //     ② 혹은, 둘다 한꺼번헤 호출하여 들어오는 값을 사용하는 방식.
        //출처: http://bitsoul.tistory.com/131 [Happy Programmer~]

        // LocationManager 객체를 얻어온다
        // getPermission();

        // GPS 활성화 요청
        if (!checkLocationServiceStatus()) {
            showDialogForLocationServiceSetting();
        }

        lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);

        mapView.setMapViewEventListener(this);
        mapView.setPOIItemEventListener(this);
        mapView.setCurrentLocationEventListener(this);

        mapView.setHDMapTileEnabled(true);
    }

    @Override
    public void onReverseGeoCoderFoundAddress(MapReverseGeoCoder mapReverseGeoCoder, String s) {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        mapReverseGeoCoder.toString();
        Toast.makeText(activity, "Reverse Geo-coding : " + s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReverseGeoCoderFailedToFindAddress(MapReverseGeoCoder mapReverseGeoCoder) {

    }

    @Override
    public void onCurrentLocationUpdate(MapView mapView, MapPoint mapPoint, float v) {
//        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
//        MapPoint.GeoCoordinate mapPointGeo = mapPoint.getMapPointGeoCoord();
//        Log.d(getClass().getName(), String.format("(%f, %f) accuracy (%f)", mapPointGeo.latitude, mapPointGeo.longitude, v));
    }

    @Override
    public void onCurrentLocationDeviceHeadingUpdate(MapView mapView, float v) {

    }

    @Override
    public void onCurrentLocationUpdateFailed(MapView mapView) {

    }

    @Override
    public void onCurrentLocationUpdateCancelled(MapView mapView) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
    }

    private boolean checkLocationServiceStatus() {
        LocationManager locationManager = (LocationManager) activity.getSystemService(context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.title_location_service);
        builder.setMessage(R.string.message_location_service);
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    private void getLocation(){
        try{
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // GPS를 받아오지 못한다면 NETWORK를 통해 위치 정보를 가져오도록 한다(대신 정확도는 떨어짐)
            if (location == null){
                location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }

            longitude = location.getLongitude(); // 경도
            latitude = location.getLatitude(); // 위도
            altitude = location.getAltitude(); //고도
            accuracy = location.getAccuracy(); // 정확도
            provider = location.getProvider(); // 위치제공자

            Log.d(getClass().getName(), "latitude : " + latitude
                    + "\nlongitude : " + longitude
                    + "\naltitude : " + altitude
                    + "\naccuracy : " + accuracy
                    + "\nprovider : " + provider);

            // 중심점 변경  - 즐겨 찾기에서 받아 오거나 자신의 현위치를 받아서 설정하자.
            mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(latitude, longitude), true);

            // 줌 레벨 변경
            mapView.setZoomLevel(1, true);

            // 마커 찍기
            MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
            MapPOIItem mapPOIItem = new MapPOIItem();
//            mapPOIItem.setItemName("시작 위치");
            mapPOIItem.setItemName(getAddressString(context, latitude, longitude));
            mapPOIItem.setTag(0);
            mapPOIItem.setMapPoint(mapPoint);
            mapPOIItem.setMarkerType(MapPOIItem.MarkerType.BluePin);
            mapPOIItem.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);

            mapView.addPOIItem(mapPOIItem);

            // 현재 위치
            // mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);

        }catch (SecurityException ex){

        }
    }

    private String getAddressString(Context context, double Latitude, double Longitude) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.KOREA);
        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(Latitude, Longitude, 20);
            if (addresses != null && addresses.size() > 0) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.d("MyCurrentloctionaddress", strReturnedAddress.toString());
            } else {
                Log.d("MyCurrentloctionaddress", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MyCurrentloctionaddress", "Canont get Address!");
        }

        // "대한민국 " 글자 지워버림
        //strAdd = strAdd.substring(5);

        return strAdd;
    }


    @Override
    public void onMapViewInitialized(MapView mapView) {

    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onPOIItemSelected(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem) {

    }

    @Override
    public void onCalloutBalloonOfPOIItemTouched(MapView mapView, MapPOIItem mapPOIItem, MapPOIItem.CalloutBalloonButtonType calloutBalloonButtonType) {

    }

    @Override
    public void onDraggablePOIItemMoved(MapView mapView, MapPOIItem mapPOIItem, MapPoint mapPoint) {

    }
}