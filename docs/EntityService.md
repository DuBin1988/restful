# EntityService

实体操作相关服务

## 保存单个实体

POST: /entity/entityName

- entityName：要保存的实体名
- 内容：JSON格式的实体内容，格式如下：
```
{data: {f_name: 'abc'}}
```
提交的数据放在data里。

## 删除单个实体

DELETE：/entity/entityName/id

- entityName：要删除的实体名
- id：要删除的实体id
