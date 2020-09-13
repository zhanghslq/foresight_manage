package com.zhs;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.baomidou.mybatisplus.generator.config.PackageConfig;
import com.baomidou.mybatisplus.generator.config.StrategyConfig;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.ArrayList;

/**
 * @author: zhs
 * @date: 2020/7/6 16:33
 */
public class MyCode {
    public static void main(String[] args) {
        AutoGenerator mpg  = new AutoGenerator();

        //配置策略
        GlobalConfig gc = new GlobalConfig();
        String projectPath = System.getProperty("user.dir");
        gc.setOutputDir(projectPath+"/back_manage_b/src/main/java");
        gc.setAuthor("zhs");
        gc.setOpen(false);
        gc.setFileOverride(false);// 是否覆盖
        gc.setServiceName("%sService");
        gc.setIdType(IdType.AUTO);
        gc.setDateType(DateType.ONLY_DATE);
        gc.setSwagger2(true);
        gc.setFileOverride(true);
        mpg.setGlobalConfig(gc);

        //2. 设置数据源
        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl("jdbc:mysql://39.106.39.48:3306/manage?useSSL=false&useUnicode=true&characterEncoding=utf-8&serverTimezone=GMT%2B8");
        dsc.setDriverName("com.mysql.cj.jdbc.Driver");
        dsc.setUsername("root");
        dsc.setPassword("yj@20200630!YJ");

        mpg.setDataSource(dsc);

        //3.包的配置

        PackageConfig pc = new PackageConfig();
        pc.setModuleName("");
        pc.setParent("com.zhs.backmanageb");
        pc.setEntity("entity");
        pc.setMapper("mapper");
        pc.setService("service");
        pc.setController("controller");
        pc.setServiceImpl("service.impl");
        mpg.setPackageInfo(pc);

        //4.策略配置
        // 策略配置
        StrategyConfig strategy = new StrategyConfig();
        //strategy.setInclude("resume","leader","organization","common_data","company","concat_record","contacts","experience_record","expert");// 设置要映射的表名
        strategy.setInclude("drop_down_box_type");// 设置要映射的表名
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        strategy.setEntityLombokModel(true);
        strategy.setRestControllerStyle(true);

        strategy.setLogicDeleteFieldName("deleted");

        //自动填充配置
        TableFill createTime = new TableFill("create_time", FieldFill.INSERT);
        TableFill updateTime = new TableFill("update_time", FieldFill.INSERT_UPDATE);
        ArrayList<TableFill> tableFills = new ArrayList<>();
        tableFills.add(createTime);
        tableFills.add(updateTime);
        strategy.setTableFillList(tableFills);

        //乐观锁
//        strategy.setVersionFieldName("version");
//        strategy.setRestControllerStyle(true);
//        strategy.setControllerMappingHyphenStyle(true);// url 下划线
        mpg.setStrategy(strategy);

        mpg.execute();

    }
}
