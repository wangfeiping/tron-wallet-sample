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
* 余额查询接口:  
  ```shell
      # curl -XGET http://127.0.0.1:7004/tron/balance/[address]
      curl -XGET http://127.0.0.1:7004/tron/balance/TFWCgEouJUC1bHLMfjdzLQdXPeiCE1byfw
  ```
* 转账交易请求接口:  
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

## 查询TRX 余额

```shell
# request

curl -X POST  https://nile.trongrid.io/wallet/getaccountbalance -d \
    '{
        "account_identifier": {
            "address": "TFWCgEouJUC1bHLMfjdzLQdXPeiCE1byfw"
        },
        "block_identifier": {
            "hash": "00000000032739f625c6e31781a6111d2762b9049cbb618a03f2955872678ee8",
            "number": 52902390
        },
        "visible": true
    }'

# response

{
    "balance": 123000000,
    "block_identifier": {
        "hash": "00000000032739acada418d6955232bdb1ec5bf734a783336e73485973ee9736",
        "number": 52902316
    }
}
```

## 查询TRC20 余额

需要转换地址格式并进行ABI 编码，请参考后面的 [在线地址转换及ABI 编码工具]()  

```shell
# request

# “owner_address” TFWCgEouJUC1bHLMfjdzLQdXPeiCE1byfw
# 测试账户地址（波场格式）
#
# “contract_address” TXYZopYRdj2D9XRtbG411XZZ3kM5VkAeBf
# 测试网（nile）的 USDT 合约地址
#
# “function_selector” balanceOf(address)
# 查询USDT 余额的合约方法
#
# “parameter” 0000000000000000000000003cb540bbfa4f7143f2b8c3e185aebac6a5af9c3e
# 转换为以太坊格式地址并进行ABI 编码的合约方法传入参数 

curl -XPOST https://nile.trongrid.io/wallet/triggerconstantcontract \
    --header 'accept: application/json' \
    --header 'content-type: application/json' -d \
    '{
        "owner_address": "TFWCgEouJUC1bHLMfjdzLQdXPeiCE1byfw",
        "contract_address": "TXYZopYRdj2D9XRtbG411XZZ3kM5VkAeBf",
        "function_selector": "balanceOf(address)",
        "parameter": "0000000000000000000000003cb540bbfa4f7143f2b8c3e185aebac6a5af9c3e",
        "visible": true
    }'

# response

# "constant_result" 000000000000000000000000000000000000000000000000000000000df28e80
# 查询结果，16进制字符串，可转换验证

{
    "result": {
        "result":true
    },
    "energy_used":935,
    "constant_result": [
        "000000000000000000000000000000000000000000000000000000000df28e80"
    ],
    "transaction": {
        "ret":[{}],
        "visible":true,
        "txID":"137391bad5d906e3a4e94a6b8de5e31d208797b969faea55620789743dbed167",
        "raw_data": {
            "contract": [{
                "parameter": {
                    "value": {
                        "data":"70a082310000000000000000000000003cb540bbfa4f7143f2b8c3e185aebac6a5af9c3e",
                        "owner_address":"TFWCgEouJUC1bHLMfjdzLQdXPeiCE1byfw",
                        "contract_address":"TXYZopYRdj2D9XRtbG411XZZ3kM5VkAeBf"
                    },
                    "type_url":"type.googleapis.com/protocol.TriggerSmartContract"
                },
                "type":"TriggerSmartContract"
            }],
            "ref_block_bytes":"3e3c",
            "ref_block_hash":"d8673c35aba05b35",
            "expiration":1734615045000,
            "timestamp":1734614985851
        },"raw_data_hex":"0a023e3c2208d8673c35aba05b354088a7f4f8bd325a8e01081f1289010a31747970652e676f6f676c65617069732e636f6d2f70726f746f636f6c2e54726967676572536d617274436f6e747261637412540a15413cb540bbfa4f7143f2b8c3e185aebac6a5af9c3e121541eca9bc828a3005b9a3b909f2cc5c2a54794de05f222470a082310000000000000000000000003cb540bbfa4f7143f2b8c3e185aebac6a5af9c3e70fbd8f0f8bd32"
    }
}

```


## 提交交易


# 相关资源以及参考资料

## 在线地址转换及ABI 编码工具

调用合约方法需要将地址参数转换为以太坊格式，并且需要进行ABI 编码。  
为了方便测试，使用在线工具直接转换并编码即可。  

在线地址转换  

[https://tronscan.org/#/tools/code-converter/tron-ethereum-address](https://tronscan.org/#/tools/code-converter/tron-ethereum-address)  

波场地址: TFWCgEouJUC1bHLMfjdzLQdXPeiCE1byfw  
转换之后  
以太地址: 0x3CB540BBFA4F7143F2B8C3E185AEBAC6A5AF9C3E  

在线ABI 编码

[https://abi.hashex.org/](https://abi.hashex.org/)  

Function ( your function ) : balanceOf
Argument ( Address ): 0x3CB540BBFA4F7143F2B8C3E185AEBAC6A5AF9C3E
Encoded data: 70a082310000000000000000000000003cb540bbfa4f7143f2b8c3e185aebac6a5af9c3e

截取地址参数编码部分即可: 0000000000000000000000003cb540bbfa4f7143f2b8c3e185aebac6a5af9c3e

## HTTP API 文档

[https://developers.tron.network/reference/full-node-api-overview](https://developers.tron.network/reference/full-node-api-overview)  
  
[https://tronprotocol.github.io/documentation-en/api/http/#fullnode-http-api](https://tronprotocol.github.io/documentation-en/api/http/#fullnode-http-api)  

## Java 版本SDK 代码及文档

[https://github.com/tronprotocol/tronj](https://github.com/tronprotocol/tronj)  

[https://tronjdocument.readthedocs.io/en/latest/mddocs/Quickstart.html](https://tronjdocument.readthedocs.io/en/latest/mddocs/Quickstart.html)  

[https://github.com/tronprotocol/wallet-cli](https://github.com/tronprotocol/wallet-cli)  

