package cn.gybyt.util;

import lombok.Data;

import java.util.List;

/**
 * 分页
 *
 * @author: sdnnie
 **/
@Data
public class Page <T> {

    /**
     * 分页大小
     */
    private Integer size = 10;

    /**
     * 当前页
     */
    private Integer page = 1;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 记录
     */
    private List<T> records;

    /**
     * 总记录数
     */
    private Integer total;

    public Integer getPages() {
        if (BaseUtil.isEmpty(records)) {
            return 0;
        }
        if (records.size() % size == 0) {
            return records.size() / size;
        }
        return records.size() / size + 1;
    }


}
