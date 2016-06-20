# restful

提供基础服务，包括：

- [实体操作](docs/EntityService.md)
- [sql查询](docs/SqlService.md)
- [业务逻辑](docs/LogicService.md)

## Maven仓库

```
http://125.76.225.203:3002/nexus/content/repositories/thirdparty/
```

以如下方式引入

```
"af:restful:1.0.0",
```

## 目录结构

- 源程序：`src/main/java/com/aote`
  * rs：所有服务均放在该目录下，服务采取门面模式，实际操作放在专门目录下。
  * [rs/mapper](docs/mapper.md)：把异常映射成错误内容。
  * [sql](docs/sql.md)：执行sql相关操作。
  * [entity](doc/entity.md)：执行实体相关操作。
  * [logic](doc/logic.md)：业务逻辑相关操作。
  * [util](doc/util.md)：对已经存在的类进行静态扩展。

- 资源内容: src/main/resources
  * hibernate.cfg.xml：hibernate的主配置文件
  * hibernate目录：用于测试的数据库表
  * sql.xml: sql语句的主配置文件
  *　sqls目录：用于测试的sql查询，可以按目录存放
  * logic.xml：业务逻辑的主配置文件
  * logics目录：用于测试的业务逻辑，可以按目录存放
  
- 测试环境
  * 单元测试用例在 `src/test/java/com/aote` 下。
  * 单元测试对每个类进行测试，重点测试服务。

## 版本发布

发布前，修改build.gradle中版本号，用下列命令发布版本：
```
gradle release
```
