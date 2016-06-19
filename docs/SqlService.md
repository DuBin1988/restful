# SqlService

执行SQL语句

## 获取汇总信息

POST：/sql/sqlName/n

- sqlName：后台配置的sql语句名
- n：指明获取汇总信息
- 内容：sql语句参数, 参数内容在data里，如下：

```
{data: {condition: '1=1'}}
```

说明：
1. sql语句其实是一个字符串表达式，系统将自动带上前缀$。在sql语句里可以使用任何表达式语法。
1. 系统将自动去除sql语句最后的order by部分，以便sum等聚集函数可以通过。

## 获取sql语句执行结果

POST：/sql/{name}?pageNo=pageNo&pageSize=pageSiz

- name: sql语句名
- pageNo: 页号，默认为1，如果小于1，按1算。
- pageSize: 每页行数，小于1或者大于1000，按1000算。
- 内容：sql语句参数, 参数内容在data里，如下：

```
{data: {condition: '1=1'}}
```

说明：
1. 没有提供不带分页的sql语句查询，如果要查询不带分页的内容，不传pageNo及pageSize参即可，这时将默认查询前1000条数据。
2. 系统一次性最多可以查询1000条数据。

## sql语句配置

resources下的sql.xml可以对所有sql语句进行统一配置。内容如下：
```xml
<cfg>
	<sql alias='test' path='test.sql'/>
	<sql alias='查询缴费汇总' path='收费/收费汇总.sql'/>
</cfg>
```

- alias: sql语句名称
- path: sql语句存放路径，所有sql语句都存放在sqls文件夹下，这里的路径为相对路径，开头不能加'/'

## sql语句书写

- 所有sql语句放在resources的sqls目录下，不支持按分目录存放。
- 只支持单一sql语句。
