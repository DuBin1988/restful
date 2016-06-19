# mapper

把异常映射成http错误。

## 映射内容

- GeneralMapper：
  * 映射：一般Exception
  * 状态：500
  * 内容：服务器内部错误
  * 说明：异常内容，输出在服务器的error.log文件里。
  
## 实现

- com.aote.RestConfig 用来对异常进行注册。
- 所有异常映射都在com.aote.rs.mapper包下。

## 异常处理办法

- 对于不需要处理的异常，在函数头部加 `throws Exception`。
- 在最终服务部分，catch所有异常，并通过log.error，把异常打印到日志里。
- 通过映射，把异常映射成http错误，一般情况下，都是500错误，错误详细信息在日志里看。
- 为增强异常信息量，有事需要捉住异常，添加更多信息后抛出，如下：

```
try {
	List result = q.setFirstResult(page * rows).setMaxResults(rows).list();
	return result;
} catch (SQLGrammarException ex) {
	// 把sql语句添加到异常信息中
	String msg = "sql:\n" + sql + "\n" + ex.getMessage();
	throw new SQLGrammarException(msg, ex.getSQLException());
}
```