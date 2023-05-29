package cn.gybyt.util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.event.SyncReadListener;
import com.alibaba.excel.read.listener.ReadListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * excel工具类
 *
 * @program: utils
 * @classname: ExcelUtil
 * @author: codetiger
 * @create: 2023/4/19 19:22
 **/
public class ExcelUtil {

    /**
     * 读取excel
     * @param inputStream 输入流
     * @param headClass excel头
     * @param listener excel监听
     * @return 结果列表
     * @param <T> 泛型
     */
    public static <T> List<T> read(InputStream inputStream, Class<T> headClass, ReadListener<T> listener) {
        if (BaseUtil.isNotNull(headClass) && BaseUtil.isNotNull(listener)) {
            return EasyExcel.read(inputStream, headClass, listener).doReadAllSync();
        }
        if (BaseUtil.isNull(headClass) && BaseUtil.isNotNull(listener)) {
            return EasyExcel.read(inputStream, listener).doReadAllSync();
        }
        if (BaseUtil.isNotNull(headClass) && BaseUtil.isNull(listener)) {
            return EasyExcel.read(inputStream, headClass, new SyncReadListener()).doReadAllSync();
        }
        return EasyExcel.read(inputStream, new SyncReadListener()).doReadAllSync();
    }

    /**
     * 读取excel
     * @param inputStream 输入流
     * @param headClass excel头
     * @param listener excel监听
     * @param sheetNo 需要读取的sheet下标
     * @return 结果列表
     * @param <T> 泛型
     */
    public static <T> List<T> read(InputStream inputStream, Class<T> headClass, ReadListener<T> listener, Integer sheetNo) {
        if (BaseUtil.isNotNull(headClass) && BaseUtil.isNotNull(listener)) {
            return EasyExcel.read(inputStream, headClass, listener).sheet(sheetNo).doReadSync();
        }
        if (BaseUtil.isNull(headClass) && BaseUtil.isNotNull(listener)) {
            return EasyExcel.read(inputStream, listener).sheet(sheetNo).doReadSync();
        }
        if (BaseUtil.isNotNull(headClass) && BaseUtil.isNull(listener)) {
            return EasyExcel.read(inputStream, headClass, new SyncReadListener()).sheet(sheetNo).doReadSync();
        }
        return EasyExcel.read(inputStream, new SyncReadListener()).sheet(sheetNo).doReadSync();
    }

    /**
     * 读取excel
     * @param inputStream 输入流
     * @param headClass excel头
     * @param listener excel监听
     * @param sheetName 需要读取的sheet名称
     * @return 结果列表
     * @param <T> 泛型
     */
    public static <T> List<T> read(InputStream inputStream, Class<T> headClass, ReadListener<T> listener, String sheetName) {
        if (BaseUtil.isNotNull(headClass) && BaseUtil.isNotNull(listener)) {
            return EasyExcel.read(inputStream, headClass, listener).sheet(sheetName).doReadSync();
        }
        if (BaseUtil.isNull(headClass) && BaseUtil.isNotNull(listener)) {
            return EasyExcel.read(inputStream, listener).sheet(sheetName).doReadSync();
        }
        if (BaseUtil.isNotNull(headClass) && BaseUtil.isNull(listener)) {
            return EasyExcel.read(inputStream, headClass, new SyncReadListener()).sheet(sheetName).doReadSync();
        }
        return EasyExcel.read(inputStream, new SyncReadListener()).sheet(sheetName).doReadSync();
    }

    /**
     * 写入excel
     * @param outputStream 输出流
     * @param headClass excel头
     * @param data 需要写入的数据
     * @param <T> 泛型
     */
    public static <T> void write(OutputStream outputStream, Class<T> headClass, List<T> data) {
        if (BaseUtil.isNull(headClass)) {
            EasyExcel.write(outputStream).sheet().doWrite(data);
            return;
        }
        EasyExcel.write(outputStream, headClass).sheet().doWrite(data);
    }


}
