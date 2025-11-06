package com.blog.util;

import cn.hutool.core.util.StrUtil;

import java.io.File;
import java.net.URLDecoder;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * 获取项目路径位置
 */
public class PathUtil {

    /**
     * 判断是否开发环境
     * 必须是maven标准项目路径
     */
    private final static String DEV_KEYWORD = StrUtil.format("target{}classes", File.separator);
    /**
     * 标准目录结构部署的配置目录
     */
    private static final String CONFIG_DIR = "config";
    /**
     * 标准目录结构部署的lib目录
     */
    private static final String LIB_DIR = "lib";
    /**
     * jar包内资源路径会带感叹号
     */
    private static final String JAR_DIR_SIGN = "!";
    private static String basePath;
    private static String rootClassPath;
    private static boolean isDev;

    /**
     * 获取项目路径
     * jar包部署路径为jar所在路径
     *
     * @return
     */
    public static String getBasePath() {
        if (basePath == null) {
            String rootClassPath = getRootClassPath();
            if (rootClassPath.contains(DEV_KEYWORD)) {
                //开发环境
                basePath = Paths.get(rootClassPath).getParent().getParent().toString();
                isDev = true;
            } else {
                //服务器部署环境
                if (rootClassPath.contains(CONFIG_DIR) || rootClassPath.contains(LIB_DIR)) {
                    //标准目录结构部署
                    basePath = Paths.get(rootClassPath).getParent().toString();
                } else {
                    //fatjar部署
                    basePath = rootClassPath;
                }
                isDev = false;
            }
        }

        try {
            return URLDecoder.decode(basePath,"utf-8");
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return  basePath;

    }

    /**
     * 获取classpath路径
     *
     * @return
     */
    public static String getRootClassPath() {
        if (rootClassPath == null) {
            String path = Objects.requireNonNull(getClassLoader().getResource(""), "classPath获取异常!").getPath().replace("file:", "");
            //处理linux系统和windows系统的差异,window进入异常代码进行处理
            try {
                Paths.get(path);
            } catch (Exception e) {
                path = path.substring(1);
            }
            //处理jar包部署路径问题
            while (path.contains(JAR_DIR_SIGN)) {
                path = Paths.get(path).getParent().toString();
            }
            rootClassPath = new File(path).getAbsolutePath();
        }

        try {
            return URLDecoder.decode(rootClassPath,"utf-8");
        }catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return  rootClassPath;
    }

    private static ClassLoader getClassLoader() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader != null ? classLoader : PathUtil.class.getClassLoader();
    }

    /**
     * 判断是否开发环境
     *
     * @return
     */
    public static boolean isDev() {
        return isDev;
    }
}
