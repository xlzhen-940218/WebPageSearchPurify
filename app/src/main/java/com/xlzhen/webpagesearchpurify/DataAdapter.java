package com.xlzhen.webpagesearchpurify;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class DataAdapter extends SimpleAdapter<List<Data>> {
    private String domain;
    private int dp10;
    public DataAdapter(Context context, List<List<Data>> data,String domain) {
        super(context, data);
        this.domain = domain;
        dp10 = DensityUtils.dp2px(10);
    }


    @Override
    public void onClick(View v) {
        String url = (String) v.getTag();
        if(!url.startsWith(domain)){
            url=String.format("%s%s",domain,url);
        }
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_data, parent, false);
            for (Data data : getItem(position)) {
                LinearLayout layout = (LinearLayout) convertView;
                switch (data.getType()) {
                    case RuleEnums.TITLE:
                        TextView textView = new TextView(context);
                        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        textView.setTextSize(18);
                        textView.setPadding(dp10,dp10,dp10,0);
                        textView.setTextColor(context.getResources().getColor(R.color.black));
                        layout.addView(textView, data.getIndex()-1);
                        break;
                    case RuleEnums.DESCRIPTION:
                        textView = new TextView(context);
                        textView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        textView.setTextSize(14);
                        textView.setPadding(dp10,dp10,dp10,0);
                        textView.setTextColor(context.getResources().getColor(android.R.color.darker_gray));
                        layout.addView(textView, data.getIndex()-1);
                        break;
                    case RuleEnums.IMG:
                        ImageView imageView = new ImageView(context);
                        imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        imageView.setPadding(dp10,dp10,dp10,0);
                        layout.addView(imageView, data.getIndex()-1);
                        break;
                    case RuleEnums.URL:
                        convertView.setOnClickListener(this);
                        break;
                }
            }
        }
        for (Data data : getItem(position)) {
            LinearLayout layout = (LinearLayout) convertView;
            switch (data.getType()) {
                case RuleEnums.TITLE:
                case RuleEnums.DESCRIPTION:
                    ((TextView) layout.getChildAt(data.getIndex()-1)).setText(data.getValue());
                    break;
                case RuleEnums.IMG:
                    AppApplication.getImageLoader().displayImage(data.getValue(), (ImageView) layout.getChildAt(data.getIndex()-1));
                    break;
                case RuleEnums.URL:
                    convertView.setTag(data.getValue());
                    break;
            }
        }

        return convertView;
    }
}
