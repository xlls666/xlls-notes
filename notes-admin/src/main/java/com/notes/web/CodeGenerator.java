package com.notes.web;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.fill.Column;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class CodeGenerator {
    private static String url = "jdbc:mysql://14.103.179.26:3306/xlls-notes?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8";
    private static String username = "root";
    private static String password = "lmmqxyx666";
    private static String author = "ldj";
//    private static String outputDir = "notes-system\\src\\main\\java";
    private static String outputDir = "F:\\project\\my-demo\\xlls-notes\\notes-db\\src\\main\\java";
    private static String parentPackage = "com.notes";
    private static String entityPackage = "domain.front.user";
    private static String mapperPackage = "mapper.front.user";
    private static String servicePackage = "service.front.user";
    private static String serviceImplPackage = "service.front.user.impl";
    private static String mapperXmlPackage = "mapper.front.user";

    private static String tableName = "kv_config";

    public static void main(String[] args) {
        // 使用 FastAutoGenerator 快速配置代码生成器
        FastAutoGenerator.create(url, username, password)
            .globalConfig(builder -> {
                builder.author(author) // 设置作者
//                    .enableSwagger()
                    .outputDir(outputDir); // 输出目录
            })
            .packageConfig(builder -> {
                builder.parent(parentPackage) // 设置父包名
//                    .moduleName("notes-system")
                    .entity(entityPackage) // 设置实体类包名
                    .mapper(mapperPackage) // 设置 Mapper 接口包名
                    .service(servicePackage) // 设置 Service 接口包名
                    .serviceImpl(serviceImplPackage) // 设置 Service 实现类包名
                    .xml(mapperXmlPackage); // 设置 Mapper XML 文件包名
//                    .pathInfo(Collections.singletonMap(OutputFile.xml, outputDir + "/resource")); // 设置路径配置信息
            })
            .strategyConfig(builder -> {
                builder.addInclude(tableName) // 设置需要生成的表名
                    .entityBuilder()
//                    .enableLombok() // 启用 Lombok
                    .enableTableFieldAnnotation() // 启用字段注解
                    .logicDeleteColumnName("del")
                    .logicDeletePropertyName("del")
                    .controllerBuilder()
                    .enableHyphenStyle()
                    .enableRestStyle(); // 启用 REST 风格
            })
            .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 模板引擎
            .execute(); // 执行生成
    }
}
