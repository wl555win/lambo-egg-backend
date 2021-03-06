package com.lambo.rule;

import com.lambo.code.utils.MybatisGeneratorUtil;

/**
 * 代码生成类
 * Created by lambo on 2017/1/10.
 */
public class Generator {
    private static String JDBC_DRIVER = "com.mysql.jdbc.Driver";//数据库驱动
    private static String JDBC_URL = "jdbc:mysql://10.10.10.136:3306/lambo";//数据连接
    private static String JDBC_USERNAME = "root";//数据库用户
    private static String JDBC_PASSWORD = "root";//数据库密码
    private static String DATABASE = "lambo";//数据库名称
    private static String TABLE = "lambo_rule";//表名
    private static String LAST_INSERT_ID_TABLES = "id";//主键
    private static Boolean IS_AUTO_INC = true;//是否自增

    private static String PACKAGE_NAME = "com.lambo.rule";//类的包路径
    private static String AUTHOR = "zxc";//作者名称

    /**
     * 自动代码生成
     * @param args
     */
    public static void main(String[] args) throws Exception {
        MybatisGeneratorUtil.generator(
                JDBC_DRIVER,
                JDBC_URL,
                JDBC_USERNAME,
                JDBC_PASSWORD,
                DATABASE,
                TABLE,
                LAST_INSERT_ID_TABLES,
                IS_AUTO_INC,
                PACKAGE_NAME,
                AUTHOR
        );
    }
}