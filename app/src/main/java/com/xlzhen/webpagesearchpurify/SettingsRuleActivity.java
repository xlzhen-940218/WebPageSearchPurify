package com.xlzhen.webpagesearchpurify;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;
import com.xlzhen.webpagesearchpurify.databinding.ActivitySettingsRuleBinding;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class SettingsRuleActivity extends AppCompatActivity {
    private ActivitySettingsRuleBinding binding;
    private List<Rules> rulesList;
    private Rules selectRules;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsRuleBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        rulesList = DataManager.getInstance().getDataList("rules", Rules.class);

        if (rulesList == null) {
            rulesList = new ArrayList<>();
        }

        if (getIntent().getIntExtra("position", -1) != -1) {
            int position = getIntent().getIntExtra("position", -1);

            selectRules = rulesList.get(position);
            binding.domainEditText.setText(selectRules.getDomain());
            binding.searchRegexEditText.setText(selectRules.getSearchRule());
            binding.contentRuleEditText.setText(selectRules.getContentRule());
        } else {
            String contentRule = "<a.*?class=\"a\".*?href=\"(data_url)\">\n" +
                    "<img.*?src=\"(data_img)\".*?>";
            binding.domainEditText.setText("https://www.duitang.com");
            binding.searchRegexEditText.setText("/search/?kw=" + getString(R.string.search_rule_content_regex) + "&type=feed");
            binding.contentRuleEditText.setText(contentRule);
        }

        binding.addRulesButton.setOnClickListener(v -> {
            if (selectRules == null) {
                Rules rules = new Rules();
                rules.setDomain(binding.domainEditText.getText().toString());
                rules.setSearchRule(binding.searchRegexEditText.getText().toString());
                rules.setContentRule(binding.contentRuleEditText.getText().toString());

                rulesList.add(rules);
            } else {
                selectRules.setDomain(binding.domainEditText.getText().toString());
                selectRules.setSearchRule(binding.searchRegexEditText.getText().toString());
                selectRules.setContentRule(binding.contentRuleEditText.getText().toString());
            }
            DataManager.getInstance().saveDataList("rules", rulesList, Rules.class);
            Toast.makeText(SettingsRuleActivity.this, R.string.setting_success, Toast.LENGTH_SHORT).show();
            finish();
        });

        binding.importRuleFileButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("other/rule");
            intent.addCategory(Intent.CATEGORY_OPENABLE);

            activityResultContract.launch(intent);
        });
        binding.importRuleTextButton.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            //无数据时直接返回
            if (clipboard == null || !clipboard.hasPrimaryClip()) {
                return;
            }
            //如果是文本信息
            if (clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                ClipData cdText = clipboard.getPrimaryClip();
                ClipData.Item item = cdText.getItemAt(0);
                //此处是TEXT文本信息
                if (item.getText() != null) {
                    //item为剪贴板的内容，你可以取到这个字符串，然后再根据规则去进行剪贴拼接
                    String content = item.getText().toString();
                    if (!TextUtils.isEmpty(content)) {
                        String[] contents = content.split("\n");
                        if(contents.length<7)
                            return;
                        binding.domainEditText.setText(contents[1]);
                        binding.searchRegexEditText.setText(contents[4]);
                        binding.contentRuleEditText.setText(contents[7]);
                    }
                }
            }
        });
    }

    private final ActivityResultLauncher<Intent> activityResultContract = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getData() != null && result.getResultCode() == Activity.RESULT_OK) {
            Uri uri = result.getData().getData();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Rules rules = null;
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        byte[] data = new byte[inputStream.available()];
                        inputStream.read(data);
                        String s = new String(data, StandardCharsets.UTF_8);
                        rules = JSON.parseObject(s, Rules.class);

                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Rules finalRules = rules;
                    runOnUiThread(() -> {
                        if (finalRules == null)
                            return;

                        binding.domainEditText.setText(finalRules.getDomain());
                        binding.searchRegexEditText.setText(finalRules.getSearchRule());
                        binding.contentRuleEditText.setText(finalRules.getContentRule());
                        //binding.addRulesButton.performClick();
                    });
                }
            }).start();

        } else {
            Toast.makeText(getApplicationContext(), R.string.dont_select_file, Toast.LENGTH_LONG).show();
        }
    });
}
