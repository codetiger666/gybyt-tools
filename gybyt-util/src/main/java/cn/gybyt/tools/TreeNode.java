package cn.gybyt.tools;

import java.util.ArrayList;
import java.util.List;

/**
 * 树节点
 * @program: utils
 * @classname: TreeNode
 * @author: codetiger
 * @create: 2023/3/10 19:46
 **/
public class TreeNode<T extends TreeNode, R> {

    /**
     * 树形节点主键
     */
    private R id;
    /**
     * 父节点
     */
    private R parentId;
    /**
     * 是否有孩子
     */
    private Boolean hasChildren = false;
    /**
     * 孩子列表
     */
    private List<T> children = new ArrayList<>();
    /**
     * 树形节点键
     */
    private String key;
    /**
     * 树形节点值
     */
    private String value;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public R getId() {
        return id;
    }

    public void setId(R id) {
        this.id = id;
    }

    public R getParentId() {
        return parentId;
    }

    public void setParentId(R parentId) {
        this.parentId = parentId;
    }

    public Boolean getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public List<T> getChildren() {
        return children;
    }

    public void setChildren(List<T> children) {
        this.children = children;
    }
}
