package com.example.todo_app.ui.map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.todo_app.PermissionSupport;
import com.example.todo_app.R;
import com.example.todo_app.databinding.FragmentMapBinding;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class MapFragment extends Fragment {

    private FragmentMapBinding binding;
    private View root;

    // 권한 클래스를 선언
    private PermissionSupport permissionSupport;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
//        MapViewModel mapViewModel = new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        root = binding.getRoot();

//        final TextView textView = binding.text_map;
//        mapViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // 권한 객체 생성
        permissionSupport = new PermissionSupport(getActivity(), getContext());

        // 권한 체크
        permissionSupport.checkAll();

        // 맵뷰 띄우기
        gotoMap();

        return root;
    }

    @Override
    public void onDestroyView() {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onDestroyView();
        binding = null;
    }

    //맵 뷰를 띄워준다.
    private void gotoMap(){
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        MapView mapView = new MapView(getActivity());

        // 중심점 변경
//        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);
        mapView.setMapCenterPoint(MapPoint.mapPointWithGeoCoord(37.53737528, 127.00557633), true);

        ViewGroup mapViewContainer = root.findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);


        // 줌 버튼
        // 줌 레벨 변경
        mapView.setZoomLevel(7, true);
        // 줌 인
        mapView.zoomIn(true);
        // 줌 아웃
        mapView.zoomOut(true);
    }
}