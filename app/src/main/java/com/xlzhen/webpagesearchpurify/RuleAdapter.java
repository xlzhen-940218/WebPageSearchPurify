package com.xlzhen.webpagesearchpurify;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RuleAdapter extends SimpleAdapter<Rules> {
    private View convertView;

    public RuleAdapter(Context context, List<Rules> data) {
        super(context, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_normal_button:
                for (Rules rules : getData()) {
                    rules.setSelected(false);
                }
                int position = (int) ((ViewGroup) v.getParent()).getTag();
                getItem(position).setSelected(true);
                DataManager.getInstance().saveDataList("rules", getData(), Rules.class);
                notifyDataSetChanged();
                break;
            case R.id.share_rule_button:
                position = (int) ((ViewGroup) v.getParent()).getTag();
                Rules rules = getItem(position);
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, String.format("domain : \n%s\n\n" +
                        "search rule : \n%s\n\n" +
                        "content rule : \n%s", rules.getDomain(), rules.getSearchRule(), rules.getContentRule()));
                shareIntent.setType("text/plain");
                context.startActivity(Intent.createChooser(shareIntent, "Share it"));
                break;
            case R.id.rule_layout:
                position = (int) v.getTag();
                Intent intent = new Intent(context, SettingsRuleActivity.class);
                intent.putExtra("position", position);
                context.startActivity(intent);
                break;
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_rule, parent, false);
            convertView.findViewById(R.id.setting_normal_button).setOnClickListener(this);
            convertView.findViewById(R.id.share_rule_button).setOnClickListener(this);
            convertView.setOnClickListener(this);
        }
        TextView domainTextView = convertView.findViewById(R.id.domain_text_view);
        domainTextView.setText(String.format("%s%s", getItem(position).getDomain(), getItem(position).getSearchRule()));
        TextView contentRuleTextView = convertView.findViewById(R.id.content_rule_text_view);
        contentRuleTextView.setText(getItem(position).getContentRule());
        convertView.findViewById(R.id.setting_normal_button).setEnabled(!getItem(position).isSelected());
        convertView.setTag(position);
        return convertView;
    }
}
