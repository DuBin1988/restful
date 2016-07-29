# FileService

文件操作相关服务
文件路径要经过两侧 encode

## 读取相关文件类型根目录

GET: /file/getpath/fileType

- fileType：文件类型，在web.xml中配置该种文件的物理路径，如下
```
 <context-param>
	<param-name>checkplanroot</param-name>
	<param-value>D:\1\2\</param-value>
 </context-param>
```

- 返回：具体物理路径，字符串

## 读取文件内容

GET: /file/readfile/filePath

- filePath：文件物理路径

- 返回：文件内容，byte[]

## 读取文件夹目录结构

GET: /file/readfolder/path/readChild

- path：文件夹物理路径
- readChild：是否读取子目录
- 返回：如下JSON对象

```
{"2":{"t1.bin":{"path":"D:\\1\\2\\t1.bin","size":226528},".":{"path":"D:\\1\\2","size":1}},"123.txt":{"path":"D:\\1\\123.txt","size":2},".":{"path":"D:\\1","size":2}}
```

  * .: 本级目录。
  * "t1.bin"：文件名。
  * path：物理路径。
  * size：文件大小或目录中文件个数。
  
## 写入文件内容

POST: /file/write/filePath

- filePath：文件物理路径

- 内容：ByteArrayEntity实体内容

- 返回：写成功返回"OK"

## 追加文件内容

POST: /file/append/filePath

- filePath：文件物理路径

- 内容：ByteArrayEntity 实体内容

- 返回：追加成功返回"OK"

## 删除文件或目录

POST: /file/delete/path

- path：文件物理路径

- 返回：删除成功返回"OK"

## 在目录中查找文件，路径中传入文件名

GET: /file/findfile/path/fileName

- path：文件夹物理路径
- fileName：文件名，可以为部分
- 返回：如下JSON对象

```
{"123.txt":{"path":"D:\\1\\123.txt","size":2},"2.bin":{"path":"D:\\1\\2\\2.bin","size":226528}}
```

  * "123.txt"：文件名。
  * path：物理路径。
  * size：文件大小。
  
## 在目录中查找文件，路径中传入检测接口全名

GET: /file/find/path/checkName

- path：文件夹物理路径
- checkName：检测文件接口全名
- 返回：如下JSON对象

```
{"123.txt":{"path":"D:\\1\\123.txt","size":2},"2.bin":{"path":"D:\\1\\2\\2.bin","size":226528}}
```

  * "123.txt"：文件名。
  * path：物理路径。
  * size：文件大小。
  
## 上传文件

POST: /file/upload/fileType/fileName

- fileType：文件类型，在web.xml中配置该种文件的物理路径，如下
```
 <context-param>
	<param-name>checkplanroot</param-name>
	<param-value>D:\1\2\</param-value>
 </context-param>
```

  * checkplanroot：fileType传入。
  * D:\1\2\：具体物理路径。

- fileName：文件名

- 内容：InputStream 实体内容

- 返回：上传功返回"OK"

## 在目录中查找文件，路径中传入检测接口全名

GET: /file/download/fileType/fileName

- fileType：文件类型，在web.xml中配置该种文件的物理路径，如下
```
 <context-param>
	<param-name>checkplanroot</param-name>
	<param-value>D:\1\2\</param-value>
 </context-param>
```

  * checkplanroot：fileType传入。
  * D:\1\2\：具体物理路径。

- fileName：文件名

- 返回：Response对象