package cn.gybyt.plugins;

import cn.gybyt.util.ReflectUtil;
import cn.gybyt.util.SpringUtil;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.*;

/**
 * mapper 刷新插件
 *
 * @classname: MybatisMapperRefresh
 * @author: codetiger
 * @create: 2023/3/16 18:49
 **/
public class GybytMybatisMapperRefreshPlugin {

    /**
     * 日志对象
     */
    private final Logger log = LoggerFactory.getLogger(GybytMybatisMapperRefreshPlugin.class);
    /**
     * sqlSessionFactory 对象
     */
    private final SqlSessionFactory sqlSessionFactory;
    /**
     * mapper文件列表
     */
    private Resource[] mapperLocations;
    /**
     * mapper文件位置
     */
    private final String packageSearchPath;
    /**
     * 刷新延迟
     */
    private final Long refreshInterval;
    /**
     * 存储更改的文件
     */
    private List<String> changeList;
    /**
     * 记录文件是否变化
     */
    private final HashMap<String, Long> fileMapping = new HashMap<>();

    public GybytMybatisMapperRefreshPlugin(String packageSearchPath, Long refreshInterval, SqlSessionFactory sqlSessionFactory) {
        this.sqlSessionFactory = sqlSessionFactory;
        this.packageSearchPath = packageSearchPath;
        this.refreshInterval = refreshInterval;
    }

    /**
     * 初始化方法
     */
    public void init() {
        final GybytMybatisMapperRefreshPlugin ruRefresh = this;
        new Thread(() -> {
            while (true) {
                try {
                    ruRefresh.refreshMapper();
                } catch (Exception e1) {
                    log.error("刷新失败");
                }
                try {
                    Thread.sleep(refreshInterval * 1000);
                } catch (InterruptedException e) {
                    log.error("刷新失败" + e.getMessage());
                }
            }
        }, "Thread-Mybatis-Refresh").start();
    }

    /**
     * 刷新mapper缓存
     */
    private void refreshMapper() {
        try {
            Configuration configuration = this.sqlSessionFactory.getConfiguration();
            try {
                this.scanMapperXml();
            } catch (IOException e) {
                log.error("扫描包路径配置错误");
                return;
            }
            if (isChanged()) {
                for (Resource resource : mapperLocations) {
                    XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(resource.getInputStream(),
                            configuration, resource.toString(), configuration.getSqlFragments());
                    XPathParser xPathParser = new XPathParser(resource.getInputStream(), true, configuration.getVariables(),
                            new XMLMapperEntityResolver());
                    XNode xNode = xPathParser.evalNode("/mapper");
                    // 获取mapper命名空间
                    String namespace = xNode.getStringAttribute("namespace");
                    // 清除缓存的mapper节点
                    cleanMappedStateMents(xNode.evalNodes("*"), configuration, namespace);
                    // 清除缓存的parameter节点
                    cleanParameterMap(xNode.evalNodes("/mapper/parameterMap"), configuration, namespace);
                    // 清除缓存的keyGen节点
                    cleanKeyGenerators(xNode.evalNodes("*"), configuration, namespace);
                    // 清除缓存的SQL语句
                    cleanSqlElement(xNode.evalNodes("/mapper/sql"), configuration, namespace);
                    // 清空缓存的结果集
                    cleanResultMap(xNode.evalNodes("/mapper/resultMap"), configuration, namespace);
                    // 清除已经加载的资源
                    cleanLoadedResource(resource.toString(), configuration);
                    xmlMapperBuilder.parse();
                    if (changeList.contains(resource.getFilename())) {
                        log.info("[" + resource.getFilename() + "] refresh finished");
                    }
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 清除已经加载的资源
     * @param resourceName
     * @param configuration
     */
    private void cleanLoadedResource(String resourceName, Configuration configuration) {
        Set<String> loadedResources = ReflectUtil.getFieldValueByFieldName(configuration, "loadedResources");
        loadedResources.remove(resourceName);
    }

    /**
     * 清空缓存的结果集
     * @param evalNodes
     * @param configuration
     * @param namespace
     */
    private void cleanResultMap(List<XNode> evalNodes, Configuration configuration, String namespace) {
        for (XNode resultMapNode : evalNodes) {
            String id = resultMapNode.getStringAttribute("id", resultMapNode.getValueBasedIdentifier());
            configuration.getResultMapNames().remove(id);
            configuration.getResultMapNames().remove(namespace + "." + id);
            clearResultMap(resultMapNode, configuration, namespace);
        }
    }

    private void clearResultMap(XNode xNode, Configuration configuration, String namespace) {
        for (XNode resultChild : xNode.getChildren()) {
            if ("association".equals(resultChild.getName()) || "collection".equals(resultChild.getName())
                    || "case".equals(resultChild.getName())) {
                if (resultChild.getStringAttribute("select") == null) {
                    configuration.getResultMapNames().remove(
                            resultChild.getStringAttribute("id", resultChild.getValueBasedIdentifier()));
                    configuration.getResultMapNames().remove(
                            namespace + "." + resultChild.getStringAttribute("id", resultChild.getValueBasedIdentifier()));
                    if (resultChild.getChildren() != null && !resultChild.getChildren().isEmpty()) {
                        clearResultMap(resultChild, configuration, namespace);
                    }
                }
            }
        }
    }

    /**
     * 清除缓存的SQL语句
     * @param evalNodes
     * @param configuration
     * @param namespace
     */
    private void cleanSqlElement(List<XNode> evalNodes, Configuration configuration, String namespace) {
        for (XNode context : evalNodes) {
            String id = context.getStringAttribute("id");
            configuration.getSqlFragments().remove(id);
            configuration.getSqlFragments().remove(namespace + "." + id);
        }
    }

    /**
     * 清除缓存的KeyGen节点
     * @param evalNodes
     * @param configuration
     * @param namespace
     */
    private void cleanKeyGenerators(List<XNode> evalNodes, Configuration configuration, String namespace) {
        for (XNode context : evalNodes) {
            String id = context.getStringAttribute("id");
            configuration.getKeyGeneratorNames().remove(id + SelectKeyGenerator.SELECT_KEY_SUFFIX);
            configuration.getKeyGeneratorNames().remove(namespace + "." + id + SelectKeyGenerator.SELECT_KEY_SUFFIX);
        }
    }

    /**
     * 清空mapper节点缓存
     * @param mappedStateMentList
     * @param configuration
     * @param namespace
     */
    private void cleanMappedStateMents(List<XNode> mappedStateMentList, Configuration configuration, String namespace) {
        Map<String, MappedStatement> mappedStatements = ReflectUtil.getFieldValueByFieldName(configuration, "mappedStatements");
        mappedStateMentList.forEach(mappedStateMent -> {
            String id = mappedStateMent.getStringAttribute("id");
            mappedStatements.remove(id);
            mappedStatements.remove(namespace + "." + id);
        });
    }

    /**
     * 扫描xml文件所在的路径
     *
     * @throws IOException
     */
    private void scanMapperXml() throws IOException {
        this.mapperLocations = new PathMatchingResourcePatternResolver().getResources(packageSearchPath);
    }

    /**
     * 判断文件是否发生了变化
     *
     * @return
     * @throws IOException
     */
    private boolean isChanged() throws IOException {
        boolean flag = false;
        changeList = new ArrayList<>();
        // 遍历文件mapper列表
        for (Resource resource : mapperLocations) {
            String resourceName = resource.getFilename();
            // 新增标识
            boolean addFlag = !fileMapping.containsKey(resourceName);
            // 修改文件:判断文件内容是否有变化
            Long compareFrame = fileMapping.get(resourceName);
            long lastFrame = resource.contentLength() + resource.lastModified();
            // 修改标识
            boolean modifyFlag = null != compareFrame && compareFrame != lastFrame;
            // 新增或是修改时,存储文件
            if (addFlag || modifyFlag) {
                // 记录文件内容帧值
                fileMapping.put(resourceName, lastFrame);
                flag = true;
                changeList.add(resource.getFilename());
            }
        }
        return flag;
    }

    /**
     * 清理parameterMap
     *
     * @param list
     * @param namespace
     */
    private void cleanParameterMap(List<XNode> list, Configuration configuration, String namespace) {
        for (XNode parameterMapNode : list) {
            String id = parameterMapNode.getStringAttribute("id");
            configuration.getParameterMaps().remove(namespace + "." + id);
        }
    }

}
