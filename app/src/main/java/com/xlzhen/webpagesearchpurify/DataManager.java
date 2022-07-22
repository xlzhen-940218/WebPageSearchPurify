package com.xlzhen.webpagesearchpurify;

import android.os.Environment;

import com.alibaba.fastjson.JSON;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataManager {
    private static volatile DataManager instance;
    private Map<String, Object> dataMaps;

    public static final String FILE_TEMPLATE="%s/Android/data/%s/data/%s.data";

    public static DataManager getInstance() {
        if (instance == null) {
            synchronized (DataManager.class) {
                if (instance == null)
                    instance = new DataManager();
            }
        }
        return instance;
    }

    public DataManager() {
        this.dataMaps = new HashMap<>();
    }

    public <T> void saveData(int arg, T bean, Class<T> cls) {
        saveData(arg + "", bean, cls);
    }

    public <T> void saveData(String key, T bean, Class<T> cls) {

        dataMaps.put(key + cls.getSimpleName(), bean);//先存储到内存

        String data = JSON.toJSONString(bean);
        File file = new File(String.format(FILE_TEMPLATE
                , Environment.getExternalStorageDirectory().getAbsolutePath()
                , AppApplication.getContext().getPackageName(), key + cls.getSimpleName()));
        FileUtils.saveFile(file.getAbsolutePath(), data);//再存储到硬盘
    }

    public <T> T getData(int arg, Class<T> cls) {
        return getData(arg + "", cls);
    }

    public <T> T getData(String key, Class<T> cls) {

        if (dataMaps.containsKey(key + cls.getSimpleName()))
            return (T) dataMaps.get(key + cls.getSimpleName());//先从内存取

        String data = null;
        File file = new File(String.format(FILE_TEMPLATE
                , Environment.getExternalStorageDirectory().getAbsolutePath()
                , AppApplication.getContext().getPackageName(), key + cls.getSimpleName()));
        if (file.exists()) {
            data = new String(FileUtils.readFile(file.getAbsolutePath()), StandardCharsets.UTF_8);
            dataMaps.put(key+cls.getSimpleName(),(T) JSON.parseObject(data, cls));
            return (T) dataMaps.get(key + cls.getSimpleName());//内存没有从硬盘取
        }
        return null;
    }

    public <T> void saveDataList(int arg, List<T> beanList, Class<T> cls) {
        saveDataList(arg + "", beanList, cls);
    }

    public <T> void saveDataList(String key, List<T> beanList, Class<T> cls) {

        dataMaps.put(key + cls.getSimpleName(), beanList);//先存储到内存

        String data = JSON.toJSONString(beanList);
        File file = new File(String.format(FILE_TEMPLATE
                , Environment.getExternalStorageDirectory().getAbsolutePath()
                , AppApplication.getContext().getPackageName(), key + cls.getSimpleName()));
        FileUtils.saveFile(file.getAbsolutePath(), data);//再存储到硬盘
    }

    public <T> List<T> getDataList(int arg, Class<T> cls) {
        return getDataList(arg + "", cls);
    }

    public <T> List<T> getDataList(String key, Class<T> cls) {

        if (dataMaps.containsKey(key + cls.getSimpleName()))
            return (List<T>) dataMaps.get(key + cls.getSimpleName());//先从内存取

        String data = null;
        File file = new File(String.format(FILE_TEMPLATE
                , Environment.getExternalStorageDirectory().getAbsolutePath()
                , AppApplication.getContext().getPackageName(), key + cls.getSimpleName()));
        if (file.exists()) {
            data = new String(FileUtils.readFile(file.getAbsolutePath()), StandardCharsets.UTF_8);
            dataMaps.put(key+cls.getSimpleName(),(List<T>) JSON.parseArray(data, cls));
            return (List<T>) dataMaps.get(key + cls.getSimpleName());//内存没有从硬盘取
        }
        return null;
    }
}
