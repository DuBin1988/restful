# restful

提供基础服务，包括：

- [实体操作](docs/EntityService.md)
- [sql查询](docs/SqlService.md)
- [业务逻辑](docs/LogicService.md)

## 目录结构

- 源程序：`src/main/java/com/aote`
  * rs：所有服务均放在该目录下，服务采取门面模式，实际操作放在专门目录下。
  * face：业务逻辑中调用的java代码门面，实际操作在专门目录下。
  * [sql](docs/sql.md)：执行sql相关操作。
  * [entity](doc/entity.md)：执行实体相关操作。
  * [logic](doc/logic.md)：业务逻辑相关操作。

- 测试环境
  * 单元测试用例在 `src/test/java/com/aote` 下。
  * 单元测试对每个类进行测试，重点测试服务。
  * 配合单元测试的相关资源在test的resources下，内容有：
    - hibernate.cfg.xml：hibernate的主配置文件
    - hibernate目录：用于测试的数据库表
    - sqls目录：用于测试的sql查询
    - logics目录：用于测试的业务逻辑
