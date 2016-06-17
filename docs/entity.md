# entity

执行与实体有关的任务。

## EntityServer

实体操作的门面。

### 方法

- save(String entityName, String values): 保存实体
  * entityName: 实体名。
  * values: JSON格式的实体内容。

- save(String entityName, HashMap<String, Object> map): 保存实体
  * entityName: 实体名。
  * map：转换成map的实体内容。

- delete(String entityName, int id)：删除实体
  * entityName: 实体名。
  * id：要删除的实体id号。
