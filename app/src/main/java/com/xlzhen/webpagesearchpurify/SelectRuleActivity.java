package com.xlzhen.webpagesearchpurify;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.xlzhen.webpagesearchpurify.databinding.ActivitySelectRuleBinding;

import java.util.List;

public class SelectRuleActivity extends AppCompatActivity {
    private ActivitySelectRuleBinding binding;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySelectRuleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Rules> rules = DataManager.getInstance().getDataList("rules",Rules.class);
        if(rules==null||rules.size()==0) {
            finish();
            return;
        }
        binding.listView.setAdapter(new RuleAdapter(this,rules));
    }
}
