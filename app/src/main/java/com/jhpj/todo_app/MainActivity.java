package com.jhpj.todo_app;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.jhpj.todo_app.databinding.ActivityMainBinding;
import com.jhpj.todo_app.ui.login.LoginActivity;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;

    // 마지막으로 뒤로가기 버튼을 눌렀던 시간 저장
    private long backKeyPressedTime = 0;
    // 첫 번째 뒤로가기 버튼을 누를때 표시
    private Toast toast;
    // 권한 클래스를 선언
    private PermissionSupport permissionSupport;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");

                // String으로 받아서 넣기
                String sendMessage = "이렇게 스트링으로 만들어서 넣어주면 됩니다.";
                intent.putExtra(Intent.EXTRA_TEXT, sendMessage);

                Intent shareIntent = Intent.createChooser(intent, "share");
                startActivity(shareIntent);

//                try {
//                    KakaoLink kakaoLink = KakaoLink.getKakaoLink(getApplicationContext());
//                    KakaoTalkLinkMessageBuilder messageBuilder = kakaoLink.createKakaoTalkLinkMessageBuilder();
//
//                    messageBuilder.addText("카카오톡으로 공유해요.");
//                    messageBuilder.addAppButton("앱 실행하기");
//                    kakaoLink.sendMessage(messageBuilder,getApplicationContext());
//                } catch (KakaoParameterException e) {
//                    e.printStackTrace();
//                }
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView mNavigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_company, R.id.nav_gallery, R.id.nav_map, R.id.nav_movie)
                .setOpenableLayout(drawer)
                .build();
        NavController mNavController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(mNavigationView, mNavController);

        // 네비게이션뷰 설정
        if (mNavigationView != null) {
//            mNavigationView.setNavigationItemSelectedListener(this);
        }

        SplashActivity.flag = true;

        getHashKey();

        // Kakao SDK 초기화
//        KakaoSdk.init(this, "{NATIVE_APP_KEY}");

        // 권한 객체 생성
        permissionSupport = new PermissionSupport(this, this);
        // 권한 체크
        permissionSupport.checkAll();

        View headerView = mNavigationView.getHeaderView(0);

        ImageView img_nav_header = headerView.findViewById(R.id.img_nav_header);
        img_nav_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        // 파이어페이스 토큰 가져오기
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {

                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Log and toast
                        Log.d("MessageToken : ", token);
//                        Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onResume();
    }

    @Override
    protected void onRestart() {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onRestart();
    }

    @Nullable
    @Override
    public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.nav_company) {
            Toast.makeText(MainActivity.this, "회사", Toast.LENGTH_SHORT).show();
            binding.appBarMain.fab.setVisibility(View.GONE);
        } else if (id == R.id.nav_movie) {
            Toast.makeText(MainActivity.this, "무비", Toast.LENGTH_SHORT).show();
            binding.appBarMain.fab.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_gallery) {
            Toast.makeText(MainActivity.this, "갤러리", Toast.LENGTH_SHORT).show();
            binding.appBarMain.fab.setVisibility(View.VISIBLE);
        } else if (id == R.id.nav_map) {
            Toast.makeText(MainActivity.this, "지도", Toast.LENGTH_SHORT).show();
            binding.appBarMain.fab.setVisibility(View.VISIBLE);
        } else if (id == R.id.sub_menu_item1) {
            Toast.makeText(MainActivity.this, "서브01", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.sub_menu_item2) {
            Toast.makeText(MainActivity.this, "서브02", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.sub_menu_item3) {
            Toast.makeText(MainActivity.this, "서브03", Toast.LENGTH_SHORT).show();
        }

        return false;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        DrawerLayout drawer = binding.drawerLayout;
        // 네비게이션 뷰가 열려있으면 닫는다.
        if (drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawers();
        }else {
//             기존 뒤로가기 버튼의 기능을 막기위해 주석처리 또는 삭제
//             super.onBackPressed();
//
//             마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
//             마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지났으면 Toast Show
//             2000 milliseconds = 2 seconds

            @SuppressLint("SimpleDateFormat") SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Log.d(getClass().getName(),
                    "KJH : " + "System.currentTimeMillis() : " + timeformat.format(new Date(System.currentTimeMillis())) + "\n"
                            + "backKeyPressedTime : " + timeformat.format(new Date(backKeyPressedTime)));

            if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
                backKeyPressedTime = System.currentTimeMillis();
                toast = Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
                toast.show();
            }
            // 마지막으로 뒤로가기 버튼을 눌렀던 시간에 2초를 더해 현재시간과 비교 후
            // 마지막으로 뒤로가기 버튼을 눌렀던 시간이 2초가 지나지 않았으면 종료
            // 현재 표시된 Toast 취소
//            if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            else{
                finish();
                toast.cancel();
                moveTaskToBack(true);
            }
        }
    }

    @SuppressLint("PackageManagerGetSignatures")
    private void getHashKey(){
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo == null)
            Log.e("KeyHash", "KeyHash:null");

        for (Signature signature : packageInfo.signatures) {
            try {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            } catch (NoSuchAlgorithmException e) {
                Log.e("KeyHash", "Unable to get MessageDigest. signature=" + signature, e);
            }
        }
    }

    // Request Permission에 대한 결과 값을 받는다.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d(getClass().getName(), "requestCode : " + requestCode + "\n"
                + "permissions.length  : " + permissions.length + "\n"
                + "permissions : " + permissions[0] + "\n"
                + "grantResults.length : " + grantResults.length + "\n"
                + "grantResults : " + grantResults[0] + "\n");

        if (!permissionSupport.PermissionsResult(requestCode, permissions, grantResults)){
            permissionSupport.checkPermissionRequest();
        }
    }
}