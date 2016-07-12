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

### entity

EntityServer的实例对象，可以直接调用EntityServer的方法，例如：

```
entity.save($t_project$, {f_name: $test$}),
```

entity对象提供的方法有：

- save(entityName, data): 保存实体
  * entityName: 要保存对象的实体名称。
  * data: json格式的实体对象内容。
  
- delete(entityName, id): 删除实体
  * entityName: 要删除实体的名称。
  * id: 要删除实体的id号。

- load(entityName, id): 加载实体
  * entityName: 要加载的实体名称。
  * id: 加载的实体的id号
  
### sql

SqlServer的实例对象，可以直接调用SqlServer的方法，例如：

```
sql.query($project.sql$, {condition: $f_name='abc'$}),
```

SqlServer提供的方法有：

- query：执行sql查询，查询结果为一个数组，可以通过数组下标方式进行访问。
  * sqlName：要调用的sql语句名称。
  * params: json格式的参数。

例如：

```
users = sql.query($project.sql$, {}),
user = users[0]
```

- run: 运行update等sql语句。
  * sql: 要执行的sql语句字符串。
  
例如：

```
sql.run($

update t_project set
  f_name = {data.f_name}
where f_userid = {data.f_userid}

$)
```
 
### log
用于调用日志处理过程，例如：

```
log.debug($测试$),
```

### data

传递给业务逻辑的JSON对象内容，采用data获得，例如：

```
a = data.f_name
```

### util

用于辅助处理过程，目前实现的方法有：

- util.error: 抛异常
  * json格式的异常内容，格式为 {status: 异常状态，msg: 异常信息}
  
例如：

```
a < 0:
  util.error( {status: 501, msg: $未找到用户$} )
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

## 业务插件

业务逻辑处理支持用户自定义插件，插件用于提供与业务相关的算法，每个插件包含一批算法处理函数。插件要求如下：

- 一个业务模块，对应一个插件。
- 一个算法，对应一个函数。
- 如果算法参数过多，建议按JSON串格式进行参数传递。
- 如果算法有多个返回值，按JSON串格式返回算法处理结果。
- 请在文档里对插件进行说明。

### 插件注册

resource下的plugins.xml用于对业务逻辑插件进行注册，内容如下：
```
<cfg>
	<plugin alias='util' class='com.aote.util.Util'/>
</cfg>
```

- alias: 业务逻辑表达式中用到的名称。如上，可在业务逻辑中直接访问util对象
```
util.error(501, $test$)
```
- class: 插件对应的类。

## 日期处理

传递给业务逻辑的日期参数，按字符串格式传递。调用entity.save时，自动根据实体类型把日期串转换成日期格式。执行sql.run等方法时，日期按字符串形式处理，不用转换。 例如：
```
entity.save($t_project$, data),

sql.run($

update t_project set
  f_finishtime = '{data.f_finishtime}'
$)
```

data.f_finishtime是字符串形式的日期，在save时，系统会自动转换成日期格式。
