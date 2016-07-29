# file

执行与文件相关的业务。

## FileServer

文件操作的门面。

### 方法

- setChecker(IFile check): 设置文件操作相关接口
  * check: 接口对象。
  
- setChecker(String checkName): 设置文件操作相关接口
  * checkName: 接口对象全名。

- read(String filePath): 读取文件内容
  * filePath: 文件路径。

- read(String filePath, boolean readChild)：读取文件夹目录
  * filePath: 文件夹路径。
  * readChild：是否读取子目录。
  
- write(byte[] buffer, int length, String filePath, boolean append)：写入文件内容
  * buffer: 要写入的内容。
  * length：内容长度。
  * filePath：文件路径。
  * append：是否为追加。
  
- delete(String filePath)：删除文件或目录
  * filePath：文件路径。
  
- find(String filePath, boolean findChild, IFile check)：查找文件
  * filePath：文件路径。
  * findChild：是否查找子目录。
  * check：检测接口对象。
