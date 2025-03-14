# Leaf

## 快速开始

原版leaf已于2020-04-12停更，我基于自身需求fork后改动，主要改动点如下（不包括雪花算法部分）：

- 精简core模块，单元测试移动到server。
- 增加spring-boot-starter（没有上传中央仓库，需要自行安装并引入）。
- 修改打包脚本，打包到`deploy`目录下，并生成启动脚本`start-server.sh`。
- 修改server启动方式，将属性移动到启动参数，通过`-Dk=v`传入。
- 不再限制号段表表名和字段名，由用户自行实现`IDAllocDao`接口，可以是mybatis 映射，也可以是服务组件`@Service`或`@Component`。
- 增加id长度限制功能，通过`max_number`限制id长度。
- 增加日期变化时重置id功能。

### spring boot starter

将项目通过`mvn install`安装到本地后，在新项目引入，注册一个`IDAllocDao`的实现类，可以是mybatis接口，也可以是服务组件，然后配置
`leaf.segment.enable=true`即可自动配置一个`SegmentIDGenImpl`bean和静态工具类`LeafSegmentGenerator`。

### Leaf Server

我们提供了一个基于spring boot的HTTP服务来获取ID

#### 配置介绍

Leaf 提供两种生成的ID的方式（号段模式和snowflake模式），你可以同时开启两种方式，也可以指定开启某种方式（默认两种方式为关闭状态）。

Leaf Server的配置都在leaf-server/src/main/resources/leaf.properties中

| 配置项                       | 含义                  | 默认值   |
|---------------------------|---------------------|-------|
| leaf.name                 | leaf 服务名            |       |
| leaf.segment.enable       | 是否开启号段模式            | false |
| leaf.jdbc.url             | 数据库库地址              |       |
| leaf.jdbc.username        | 数据库用户名              |       |
| leaf.jdbc.password        | 数据库密码               |       |
| leaf.snowflake.enable     | 是否开启snowflake模式     | false |
| leaf.snowflake.zk.address | snowflake模式下的zk地址   |       |
| leaf.snowflake.port       | snowflake模式下的服务注册端口 |       |

#### 号段模式

如果使用号段模式，需要建立DB表，并配置`leaf.jdbc.url`, `leaf.jdbc.username`, `leaf.jdbc.password`。

如果不想使用该模式配置`leaf.segment.enable=false`即可。

##### 创建数据表

```sql
CREATE
DATABASE leaf
CREATE TABLE `leaf_alloc`
(
    `biz_tag` varchar(128) NOT NULL DEFAULT '',
    `max_id` bigint(20) NOT NULL DEFAULT '1',
    `step` int(11) NOT NULL,
    `description` varchar(256) DEFAULT NULL,
    `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`biz_tag`)
) ENGINE=InnoDB;

insert into leaf_alloc(biz_tag, max_id, step, description)
values ('leaf-segment-test', 1, 2000, 'Test leaf Segment Mode Get Id')
```

##### 配置相关数据项

在`leaf.properties`中配置`leaf.jdbc.url`, `leaf.jdbc.username`, `leaf.jdbc.password`参数

#### Snowflake模式

算法取自twitter开源的snowflake算法。

如果不想使用该模式配置`leaf.snowflake.enable=false`即可。

##### 配置zookeeper地址

在`leaf.properties`中配置`leaf.snowflake.zk.address`，配置leaf 服务监听的端口`leaf.snowflake.port`。

#### 运行Leaf Server

##### 打包服务

```shell
git clone git@github.com:cpupg/leaf.git
//按照上面的号段模式在工程里面配置好
cd leaf
sh deploy.sh
```

##### 运行服务

*注意:首先得先配置好数据库表或者zk地址*

```shell
cd deploy;
# args是上面的配置，通过-Dk=v传入
sh args start-server.sh
```

##### 测试

```shell
#segment
curl http://localhost:8080/api/segment/get/leaf-segment-test
#snowflake
curl http://localhost:8080/api/snowflake/get/test
```

##### 监控页面

号段模式：http://localhost:8080/cache

### Leaf Core

当然，为了追求更高的性能，需要通过RPC Server来部署Leaf 服务，那仅需要引入leaf-core的包，把生成ID的API封装到指定的RPC框架中即可。

### 注意事项

注意现在leaf使用snowflake模式的情况下 其获取ip的逻辑直接取首个网卡ip【特别对于会更换ip的服务要注意】避免浪费workId
