package cn.gybyt.dynamic;

/**
 * 动态数据源
 *
 * @program: ApiClumps
 * @classname: GybytDataSourceHolder
 * @author: codetiger
 * @create: 2023/9/2 15:46
 **/
public class GybytDataSourceHolder {

    //线程  本地环境
    private static final ThreadLocal<String> dataSources = new InheritableThreadLocal<>();

    //设置数据源
    public static void setDataSource(String datasource) {
        dataSources.set(datasource);
    }

    //获取数据源
    public static String getDataSource() {
        return dataSources.get();
    }

    //清除数据源
    public static void clearDataSource() {
        dataSources.remove();
    }

}
