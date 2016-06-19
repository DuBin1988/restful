# util

对所有已经存在的类进行静态扩展，以便复用。

## ExpressionHelper

对expression的执行过程进行扩展

- run：带参执行一段程序。

## ResourceHelper

对资源文件的处理进行静态扩展

- getString: 获取资源文件的文本内容。

## SqlHelper

- query: 执行sql查询。
- bulkUpdate：执行sql更新语句。

对sql语句执行进行扩展。

## JsonHelper

对Json处理进行扩展。

- toMap：把json内容转换成map。

## ExceptionHelper

对Exception处理进行扩展

- stackToString：把Exception堆栈信息转换成字符串。
