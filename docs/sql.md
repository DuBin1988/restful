# sql

执行与sql有关的任务，sql执行按spring bean方式处理。

## SqlServer

sql操作的门面。

### 方法

- queryTotal(String name, String str): 获取总数。
  * name：sql语句名称
  * str: JSON格式的sql语句执行参数

- query(String name, int pageNo, int pageSize, String str)：执行sql分页查询。
  * name：sql语句名称
  * pageNo：页号
  * pageSize：每页行数
  * str: JSON格式的sql语句执行参数

- void run(String sql)：执行一条update等sql语句。
  * sql：要执行的sql语句内容。

## SqlMapper

对sql配置文件进行处理。
