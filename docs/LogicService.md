# LogicService

业务逻辑相关服务

## 执行业务逻辑

POST: /logic/logicName

- logicName：要执行的业务逻辑名
- 内容：JSON格式的业务逻辑参数，数据内容在data里，如下：
```
{data: {f_name: 'abc'}}
```

## 业务逻辑书写

业务逻辑书写基于expression，系统提供了几个默认对象，用于支持业务逻辑与数据库交互。

- entity: EntityServer的实例对象，可以直接调用EntityServer的方法，例如：

```
entity.save($t_project$, {f_name: $test$}),
```

- sql：SqlServer的实例对象，可以直接调用SqlServer的方法，例如：

```
sql.query($project.sql$, {condition: $f_name='abc'$}),
```

- log: 用于调用日志处理过程，例如：

```
log.debug($测试$),
```

传递给业务逻辑的JSON对象内容，采用data获得，例如：

```
a = data.f_name
```

## 业务逻辑配置

resources下的logic.xml可以对所有业务逻辑进行统一配置。内容如下：
```xml
<cfg>
	<logic alias='test' path='test.sql'/>
	<logic alias='查询缴费汇总' path='收费/收费汇总.sql'/>
</cfg>
```

- alias: 业务逻辑名称
- path: 业务逻辑存放路径，所有业务逻辑都存放在logics文件夹下，这里的路径为相对路径，开头不能加'/'

## 日期处理

传递给业务逻辑的日期参数，按字符串格式传递。调用entity.save时，自动根据实体类型把日期串转换成日期格式。执行sql.run等方法时，日期串按字符串形式处理，不用转换。 例如：
```
entity.save($t_project$, data),

sql.run($

update t_project set
  f_finishtime = '{data.f_finishtime}'
$)
```

data.f_finishtime是字符串形式的日期，在save时，系统会自动转换成日期格式。
