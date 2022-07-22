package com.xlzhen.webpagesearchpurify;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class SimpleAdapter<T> extends BaseAdapter implements View.OnClickListener {
    protected Context context;
    private List<T> data;

    public SimpleAdapter(Context context, List<T> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public T getItem(int position) {
        return data.get(position);
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
        notifyDataSetInvalidated();
    }

    public void addData(T bean) {
        data.add(bean);
        notifyDataSetChanged();
    }

    public void addDataList(List<T> dataList) {
        data.addAll(dataList);
        notifyDataSetChanged();
    }
}
