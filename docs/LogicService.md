# LogicService

业务逻辑相关服务

## 执行业务逻辑

POST: /logic/logicName

- logicName：要执行的业务逻辑名
- 内容：JSON格式的业务逻辑参数

## 业务逻辑书写

业务逻辑书写基于expression，系统提供了几个默认对象，用于支持业务逻辑与数据库交互。

- entity: EntityServer的实例对象，可以直接调用EntityServer的方法，例如：

```
entity.save($t_project$, {f_name: $test$}),
```

- sql：SqlServer的实例对象，可以直接调用SqlServer的方法，例如：

```
sql.query($project.sql$, {condition: "f_name='abc'"}),
```
