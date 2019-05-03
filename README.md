# data-desensitise
数据脱敏

1. 我自己的代码运行环境：Java version 1.8.0_144，构建项目时使用的是Maven，您可以直接导入Maven工程的方法import本项目，pom文件中包含了所用到的jar包。项目中使用到了数据库，具体的数据表操作（CRUD）在使用时，您可能需要修改成您自己的对应的数据表。
简要说明：
（1）代码入口是src/main/java/com/DesensitiseTest/App.java
（2）DesensitiseTest/db/api/DBApi.java和DesensitiseTest/db/MySQLUtils.java主要封装了访问数据库的一些接口
（3）DesensitiseTest/utils/DesensitiseUtils.java主要是脱敏规则您可根据自己的需求进行修改

2. 这是一份比较简单的数据脱敏项目，主要是理解脱敏流程和基本脱敏规则。对于整个简单的流程，在入口程序中有注释，下面也简单列出：
（1）将数据迁移到新库
（2）创建脱敏字段配置表及脱敏规则说明表
（3）获取待脱敏数据表的所有字段相关信息
（4）将3中获取的字段插入到字段规则配置表fields
（5）人工审核字段，为敏感字段配置脱敏规则（暂时直接在库里配置规则）
（6）读取敏感字段即其相应规则
（7）获取待脱敏数据(假设数据都已经迁移至新库新表，注意，所有字段类型换成string)
（8）根据敏感字段的规则进行脱敏
（9）将脱敏数据入库
