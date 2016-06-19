# SqlService

执行SQL语句

## 获取汇总信息

POST：/sql/sqlName/n

- sqlName：后台配置的sql语句名
- n：指明获取汇总信息
- 内容：sql语句参数, 参数内容在data里，求和字段在sums里，如下：

```
{
	data: {condition: '1=1'},
	sums: ['f_persons']
}
```

- 返回：如下JSON对象

```
{"f_persons":null,"n":2,"placeholder":"1"}
```

  * n: 总数据个数。
  * placeholder：固定值1，用于占位，前台可以不管。
  * 其它：求和结果。

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

1. 只支持单一sql语句。
2. sql语句支持模块处理，sql语句可以进行复合，例如：

 基本sql，名称为'基本sql'，有一个汇总参数
```
select {groupName}, count(*) c
from t_project
group by {groupName}
```

 复合sql
```
select * from (
  { sql.call($基本sql$, {groupName: $f_name$}) }
) t
```

调用sql.call函数后，将把汇总部分的sql字符串插入到调用部分，这样就实现了sql语句重用。
