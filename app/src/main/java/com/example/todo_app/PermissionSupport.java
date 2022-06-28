package com.example.todo_app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionSupport {
    private Context context;
    private Activity activity;

    // Manifest에 권한을 작성 후
    // 요청할 권한을 배열로 저장
    private String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
    };

    // 권한 요청을 할 때 발생하는 창에 대한 결과값
    private List<Object> permissionList;

    // 정확한 위치 설정 권한 리턴 결과값
    private final int PERMISSIONS_ACCESS_FINE_LOCATION = 2022;

    public PermissionSupport(Activity _activity, Context _context){
        this.activity = _activity;
        this.context = _context;
    }

    public boolean checkAll(){
        boolean isCheck = false;
        if (!checkGPSService()){
            Toast.makeText(activity, R.string.checkGPSService_ON, Toast.LENGTH_SHORT).show();
        }else{
            if (!checkNetworkService()){
                Toast.makeText(activity, R.string.checkNetworkService_ON, Toast.LENGTH_SHORT).show();
            }else{
                if (!checkPermission()){
                    checkPermissionRequest();
                }else{
                    isCheck = true;
                }
            }
        }
        return isCheck;
    }

    //권한 허용 체크 리스트
    public boolean checkPermission(){
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        int result;
        permissionList = new ArrayList<>();

        // 배열로 저장한 권한 중 허용되지 않은 권한이 있는지 체크
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(context, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        return permissionList.isEmpty();
    }

    // 권한 허용 요청
    public void checkPermissionRequest(){
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());

        // Android 6.0 이하 버전에서는 permission이 필요하지 않음
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Toast.makeText(activity, R.string.checkPermission_ON, Toast.LENGTH_SHORT).show();
//            activity.requestPermissions(new String[permissionList.size()], MULTIPLE_PERMISSIONS);
            activity.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_ACCESS_FINE_LOCATION);
        }
    }

    // 권한 요청에 대한 결과 처리
    public boolean PermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());

        if (requestCode == PERMISSIONS_ACCESS_FINE_LOCATION && (grantResults.length > 0)){
            for(int j = 0 ; j < grantResults.length ; j++){

                // grantResults == 0 사용자가 허용한 것
                // grantResults == 1 사용자가 거부한 것
                if (grantResults[j] == -1){
                    new AlertDialog.Builder(context).setTitle(R.string.Alert_Title_Notice).setMessage(R.string.checkPermissionLOCATION_ON)
                            .setPositiveButton(R.string.Alert_Button_Positive, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + activity.getPackageName()));
                                    intent.addCategory(Intent.CATEGORY_DEFAULT);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    activity.startActivity(intent);
                                }
                            })
                            .setNegativeButton(R.string.Alert_Button_Negative, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .create()
                            .show();
                }
            }
        }
        return true;
    }

    // GPS가 켜져있는지 확인
    public boolean checkGPSService() {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        boolean isGPS = false;
        LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            isGPS = true;
        }
        return isGPS;
    }

    // 네트워크 연결 상태인지 확인
    public boolean checkNetworkService(){
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        boolean isOnline = false;
        try {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo.State mobile =  manager.getNetworkInfo(0).getState();
            if (mobile == NetworkInfo.State.CONNECTED) {
                isOnline = true;
            }

            NetworkInfo.State wifi =  manager.getNetworkInfo(1).getState();
            if (wifi == NetworkInfo.State.CONNECTED) {
                isOnline = true;
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return isOnline;
    }


}