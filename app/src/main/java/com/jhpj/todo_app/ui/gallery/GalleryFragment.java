package com.jhpj.todo_app.ui.gallery;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jhpj.todo_app.R;
import com.jhpj.todo_app.databinding.FragmentGalleryBinding;
import com.jhpj.todo_app.ui.company.MySingleton;
import com.jhpj.todo_app.ui.company.News;
import com.jhpj.todo_app.ui.company.NewsItemClicked;
import com.jhpj.todo_app.ui.company.NewsListAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GalleryFragment extends Fragment implements NewsItemClicked {

    private FragmentGalleryBinding binding;
    RecyclerView recyclerView;
    NewsListAdapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

//        final TextView textView = binding.textGallery;
//        galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        recyclerView = root.findViewById(R.id.company_recylerView);
        fetch_date();

        mAdapter = new NewsListAdapter(this);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return root;
    }

    @Override
    public void onDestroyView() {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        super.onDestroyView();
        binding = null;
    }

    private void fetch_date(){
        Log.d(getClass().getName(), "fetch_date()");

        String url = "https://newsapi.org/v2/everything?q=apple&from=" + getDate() + "&to=" + getDate() + "&sortBy=popularity&apiKey=63c99fa5e438467189add97cafe6145f";
//        String url = "https://newsapi.org/v2/everything?q=apple&from=2022-06-27&to=2022-06-27&sortBy=popularity&apiKey=63c99fa5e438467189add97cafe6145f";
//        String url = "https://newsapi.org/v2/top-headlines?country=in&category=general&apiKey=f25bd7cf2bf341fa8db0f6f426364335";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray newsJsonArray = response.getJSONArray("articles");
                    ArrayList<com.jhpj.todo_app.ui.company.News> newsArrayList = new ArrayList<com.jhpj.todo_app.ui.company.News>();
                    for (int i = 0; i < newsJsonArray.length(); i++) {
                        JSONObject newsJsonObject = newsJsonArray.getJSONObject(i);
                        com.jhpj.todo_app.ui.company.News news = new com.jhpj.todo_app.ui.company.News(
                                newsJsonObject.getString("title"),
                                newsJsonObject.getString("author"),
                                newsJsonObject.getString("url"),
                                newsJsonObject.getString("urlToImage")
                        );
//                        Log.d(getClass().getName(), newsJsonObject.getString("author"));
                        newsArrayList.add(news);
                    }
                    mAdapter.updateNews(newsArrayList);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(getClass().getName(), "onErrorResponse() ; " + error);
            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Log.d(getClass().getName(), "getHeaders()");
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("User-Agent", "Mozilla/5.0");
                return headers;
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);
    }

    // 현재 시간을 "yyyy-MM-dd"로 표시하는 메소드
    private String getDate() {
        long now = System.currentTimeMillis();
        Date date = new Date(now - 3);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String getDate = dateFormat.format(date);

        return getDate;
    }

    @Override
    public void onItemClicked(News item) {
        Log.d(getClass().getName(), "onItemClicked()");
        String url = item.url;
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        builder.setToolbarColor(ContextCompat.getColor(getContext(), com.google.android.material.R.color.design_default_color_primary));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(getContext(), Uri.parse(url));
    }
}