package com.example.todo_app.ui.company;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.todo_app.MySingleton;
import com.example.todo_app.News;
import com.example.todo_app.NewsItemClicked;
import com.example.todo_app.NewsListAdapter;
import com.example.todo_app.R;
import com.example.todo_app.databinding.FragmentCompanyBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CompanyFragment extends Fragment implements NewsItemClicked {

    private FragmentCompanyBinding binding;
    RecyclerView recyclerView;
    NewsListAdapter mAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(getClass().getName(), "KJH : " + Thread.currentThread().getStackTrace()[2].getMethodName());
        CompanyViewModel companyViewModel = new ViewModelProvider(this).get(CompanyViewModel.class);

        //        final TextView textView = binding.textCompany;
//        companyViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        binding = FragmentCompanyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

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
//        String url = "https://newsapi.org/v2/top-headlines?country=in&category=general&apiKey=f25bd7cf2bf341fa8db0f6f426364335";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray newsJsonArray = response.getJSONArray("articles");
                    ArrayList<News> newsArrayList = new ArrayList<News>();
                    for (int i = 0; i < newsJsonArray.length(); i++) {
                        JSONObject newsJsonObject = newsJsonArray.getJSONObject(i);
                        News news = new News(
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
        Date date = new Date(now - 1);
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