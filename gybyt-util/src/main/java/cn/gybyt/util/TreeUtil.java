package cn.gybyt.util;

import cn.gybyt.tools.TreeNode;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 树型结构工具类
 *
 * @program: utils
 * @classname: TreeUtil
 * @author: codetiger
 * @create: 2023/3/10 19:55
 **/
public class TreeUtil {

    /**
     * 生成树型结构
     * @param nodeList
     * @return
     */
    public static <T extends TreeNode<T, R>, R> List<T> merge(List<T> nodeList) {
        // 存储所有顶级节点
        List<T> parentNodeList = new ArrayList<>();
        // 用来存储顶级节点
        HashSet<R> parentIdSet = new HashSet<>();
        // 为空返回空集合
        if (BaseUtil.isEmpty(nodeList)) {
            return new ArrayList<>();
        }
        // 根据主键生成map
        Map<R, T> nodeMap = nodeList.stream().collect(Collectors.toMap(TreeNode::getId, Function.identity(), (key1, key2) -> key2));
        nodeList.forEach(node -> {
            // 查找当前节点父节点
            TreeNode<T, R> parentNode = nodeMap.get(node.getParentId());
            // 如果父节点不存在，则当前节点为顶级节点
            if (BaseUtil.isEmpty(parentNode)) {
                parentIdSet.add((R) node.getId());
            } else {
                parentNode.setHasChildren(true);
                parentNode.getChildren().add(node);
            }
        });
        // 顶级节点主键迭代器
        // 拼装需返回的树状结构
        for (R id : parentIdSet) {
            parentNodeList.add(nodeMap.get(id));
        }
        return parentNodeList;
    }

}
