# tron-wallet-sample

TRON network, wallet sample, SpringBoot

本项目为波场调用示例代码，目的是用一个可编译运行的服务程序演示波场网络相关功能接口的调用。

本文档将主要包含四个部分：
* 1) 对本示例项目功能和代码的简要说明；  
* 2) 对Java 语言实现版本的波场SDK 的简要说明；
* 3) 对Http 接口的说明，以便于更好的理解波场SDK 各个语言版本的实现；
* 4) 网络上搜索到的相关资源以及参考资料；

# 本示例项目

## 项目构建及启动

编译

```shell
mvn install
```

启动

```shell
java -jar ./tron-sample-0.0.0-SNAPSHOT.jar --spring.profiles.active=local
```

测试地址: TFWCgEouJUC1bHLMfjdzLQdXPeiCE1byfw

配置文件: [application-local.yml](https://github.com/wangfeiping/tron-wallet-sample/blob/main/src/main/resources/application-local.yml)

## 功能简介

* 查询最新区块接口:  
  ```shell
      curl -XGET http:/127.0.0.1:7004/tron/height
  ```
* 账户创建接口:  
  ```shell
      # curl -XGET http:/127.0.0.1:7004/tron/address/[request_id]  
      curl -XGET http:/127.0.0.1:7004/tron/address/000000  
  ```
* 转账交易请求接口:  
  ```shell
      curl
  ```
* 余额查询接口:  
  ```shell
      curl
  ```
* 项目将启动一个线程轮询波场的区块数据，监控配置的账户地址充值（入账）交易；  
  [TronWatcher.java](https://github.com/wangfeiping/tron-wallet-sample/blob/main/src/main/java/tron/wallet/component/TronWatcher.java#L65)
* 项目将启动一个独立线程轮询处理转账（出账）交易请求队列，签名并提交执行交易；  

# Java 语言版本波场SDK 简要说明

[public void getNowBlock()](https://github.com/wangfeiping/tron-wallet-sample/blob/main/src/main/java/tron/wallet/service/TronService.java#L49)

# 波场Http 接口简要说明

## 查询最新的区块

```shell
# request

curl -X POST  http://TRON_NETWORK_HTTP_API/wallet/getnowblock

# response

{
    "blockID":"000000000326c641ee5f2819cdaccd67a616195ea214a4c352a4bfa47d61e1c8",
    "block_header":{
        "raw_data":{
            "number":52872769,
            "txTrieRoot":"0000000000000000000000000000000000000000000000000000000000000000",
            "witness_address":"4150d3765e4e670727ebac9d5b598f74b75a3d54a7",
            "parentHash":"000000000326c640639ec37b4f981df3dd768e926305bd0ddb2b91e759fa2fda",
            "version":30,
            "timestamp":1734518928000
        },
        "witness_signature":"1d2d3256479d3c813282e0f92d6b37e50ef9e3382a82d992e50d09cda059b2623a49b5134ba40fc976ee60b63bae8bd940dadff04c132094e7bc12c7e543dc1701"
    }
}
```


# 相关资源以及参考资料

## Java 版本SDK 代码及文档

[https://github.com/tronprotocol/tronj](https://github.com/tronprotocol/tronj)

[https://tronjdocument.readthedocs.io/en/latest/mddocs/Quickstart.html](https://tronjdocument.readthedocs.io/en/latest/mddocs/Quickstart.html)

## HTTP API 文档

[https://tronprotocol.github.io/documentation-en/api/http/#fullnode-http-api](https://tronprotocol.github.io/documentation-en/api/http/#fullnode-http-api)


