package cn.gybyt.satoken;

import cn.dev33.satoken.dao.auto.SaTokenDaoBySessionFollowObject;
import cn.dev33.satoken.util.SaFoxUtil;
import cn.gybyt.util.CacheUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * satoken自定义dao
 *
 * @author: codetiger
 **/
public class GybytTokenDao implements SaTokenDaoBySessionFollowObject {

    @Override
    public String get(String key) {
        return CacheUtil.get("saToken:" + key);
    }

    @Override
    public void set(String key, String value, long timeout) {
        CacheUtil.set("saToken:" + key, value, timeout);
    }

    @Override
    public void update(String key, String value) {
        CacheUtil.set("saToken:" + key, value);
    }

    @Override
    public void delete(String key) {
        CacheUtil.remove("saToken:" + key);
    }

    @Override
    public long getTimeout(String key) {
        return CacheUtil.getExpire("saToken:" + key);
    }

    @Override
    public void updateTimeout(String key, long timeout) {
        CacheUtil.setExpire("saToken:" + key, timeout);
    }

    @Override
    public Object getObject(String key) {
        return CacheUtil.get("saToken:" + key);
    }

    @Override
    public <T> T getObject(String key, Class<T> classType) {
        return CacheUtil.get("saToken:" + key, classType);
    }

    @Override
    public void setObject(String key, Object object, long timeout) {
        CacheUtil.set("saToken:" + key, object, timeout);
    }

    @Override
    public void updateObject(String key, Object object) {
        CacheUtil.set("saToken:" + key, object);
    }

    @Override
    public void deleteObject(String key) {
        CacheUtil.remove("saToken:" + key);
    }

    @Override
    public long getObjectTimeout(String key) {
        return CacheUtil.getExpire("saToken:" + key);
    }

    @Override
    public void updateObjectTimeout(String key, long timeout) {
        CacheUtil.setExpire("saToken:" + key, timeout);
    }

    @Override
    public List<String> searchData(String prefix, String keyword, int start, int size, boolean sortType) {
        Set<String> keys = CacheUtil.keys(prefix + "*" + keyword + "*");
        return SaFoxUtil.searchList(new ArrayList<>(keys), start, size, sortType);
    }

}
