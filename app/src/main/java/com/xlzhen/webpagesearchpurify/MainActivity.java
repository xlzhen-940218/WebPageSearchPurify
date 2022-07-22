package com.xlzhen.webpagesearchpurify;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ConsoleMessage;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.xlzhen.webpagesearchpurify.databinding.ActivityMainBinding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private String dataRules = "\\(data_(.*?)\\)";
    private List<Data> ruleList;
    private List<List<Data>> dataList;
    private DataAdapter dataAdapter;

    private List<Rules> rulesList = new ArrayList<>();

    private Rules selectedRules;
    private boolean loading;
    private String queryText;
    private String contentRule;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                queryText = query;
                loadWeb();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (loading)
            return;
        rulesList = DataManager.getInstance().getDataList("rules", Rules.class);
        if (rulesList == null || rulesList.size() == 0) {
            return;
        }

        for (Rules rules : rulesList) {
            if (rules.isSelected()) {
                selectedRules = rules;
                break;
            }
        }
        if (selectedRules == null) {
            selectedRules = rulesList.get(0);
        }

        dataAdapter = new DataAdapter(MainActivity.this, new ArrayList<>(), selectedRules.getDomain());
        binding.listView.setAdapter(dataAdapter);

        ruleMatcher();
        loadWeb();
    }

    private void ruleMatcher() {
        ruleList = new ArrayList<>();
        contentRule = selectedRules.getContentRule();
        Pattern pattern = Pattern.compile(dataRules);
        Matcher matcher = pattern.matcher(selectedRules.getContentRule());
        while (matcher.find()) {
            ruleList.add(new Data());
            ruleList.get(ruleList.size() - 1).setType(matcher.group(1));
            ruleList.get(ruleList.size() - 1).setIndex(ruleList.size() - 1);
            contentRule=contentRule.replaceAll(matcher.group(), ".*?");
        }


    }

    private void loadWeb() {
        if (queryText == null)
            return;

        loading = true;

         new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    dataList = new ArrayList<>();
                    String searchRule = selectedRules.getSearchRule().replace(getString(R.string.search_rule_content_regex),URLEncoder.encode(queryText,"UTF-8"));
                    URL url = new URL(String.format("%s%s",selectedRules.getDomain(),searchRule));
                    BufferedReader reader = new BufferedReader(new InputStreamReader(
                            url.openStream(), StandardCharsets.UTF_8));
                    StringBuffer html = new StringBuffer();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        html.append(line).append("\n");
                    }
                    dataList = new ArrayList<>();
                    Pattern pattern = Pattern.compile(contentRule);
                    Matcher matcher = pattern.matcher(html);
                    while (matcher.find()) {
                        dataList.add(new ArrayList<>());
                        for (int i = 0; i < ruleList.size(); i++) {
                            dataList.get(dataList.size() - 1).add(new Data());
                            dataList.get(dataList.size() - 1).get(dataList.get(dataList.size() - 1).size() - 1).setIndex(ruleList.get(i).getIndex());
                            dataList.get(dataList.size() - 1).get(dataList.get(dataList.size() - 1).size() - 1).setValue(matcher.group(i + 1));
                            dataList.get(dataList.size() - 1).get(dataList.get(dataList.size() - 1).size() - 1).setType(ruleList.get(i).getType());
                        }

                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                runOnUiThread(()->{
                    binding.listView.setAdapter(new DataAdapter(MainActivity.this,dataList,selectedRules.getDomain()));
                });
            }
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, R.string.settings_rule);
        menu.add(0, 2, 0, R.string.select_search_rule);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == 1) {
            startActivity(new Intent(this, SettingsRuleActivity.class));
        }
        if (item.getItemId() == 2) {
            startActivity(new Intent(this, SelectRuleActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}