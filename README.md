# RocketMQ

- ## 1、MQ介绍

  ### 1.1 为什么要用MQ

  消息队列是一种“先进先出”的数据结构

  ![image.png](https://cdn.nlark.com/yuque/0/2021/png/12759906/1622383247093-9c1dc02c-4f0b-4e20-95fa-bba3d80f4404.png)

  其应用场景主要包含一下三个方面：

  

  - 应用解耦

  系统的耦合性越高，容错就越低。以电商应用为例，用户创建订单后，如果耦合调用库存系统，物流系统，支付系统，任何一个子系统出了故障或因为升级等原因暂时不可用，都会造成下单操作异常，影响用户体验。

  ![image.png](https://cdn.nlark.com/yuque/0/2021/png/12759906/1622383522175-62dea9ea-112f-454b-9e38-bde125a1c77b.png)

  使用消息队列解耦合，系统的耦合性就会提高了。比如物流系统发生故障，需要几分钟才能来修复，在这段时间内，物流系统要处理的数据被缓存到消息队列中，用户的下单操作正常完成。当物流系统回复后，补充处理存在消息队列中的订单消息即可，终端系统感知不到物流系统发生过几分钟故障。

  ![image.png](https://cdn.nlark.com/yuque/0/2021/png/12759906/1622387751220-631766b1-c22e-4dfd-b62a-8b9ffe1f62ff.png)

  - 流量削峰

  ![image.png](https://cdn.nlark.com/yuque/0/2021/png/12759906/1622414485453-74b39ba9-930d-494b-92cd-07fcd357152c.png)

  应用系统如果遇到系统请求流量的瞬间猛增，有可能会将系统压垮。有了消息队列可以将大量请求缓存起来，分散到很长一段时间处理，这样可以大大提到系统的稳定性和用户体验。

  

  ![image.png](https://cdn.nlark.com/yuque/0/2021/png/12759906/1622414528000-a56be787-49ae-4be8-be41-581aefd73684.png)

  一般情况，为了保证系统的稳定性，如果系统负载超过阈值，就会阻止用户请求，这会影响用户体验，而如果使用消息队列将请求缓存起来，等待系统处理完毕后通知用户下单完毕，这样总不能下单体验要好。

  

  处于经济考量目的：

  业务系统正常时段的QPS如果是1000，流量最高峰是10000，为了应对流量高峰配置高性能的服务器显然不划算，这时可以使用消息队列对峰值流量削峰。

  

  - 数据分发

  ![image.png](https://cdn.nlark.com/yuque/0/2021/png/12759906/1622415094265-4f360faf-b099-48e4-b028-2491267ab893.png)

  通过消息队列可以让数据在多个系统更加之间进行流通。数据的产生方不需要关心谁来使用数据，只需要将数据发送到消息队列，数据使用方直接在消息队列中直接获取数据即可。

  ![image.png](https://cdn.nlark.com/yuque/0/2021/png/12759906/1622415118047-cde9824c-7d7e-4c65-8a14-86504d06bd33.png)

  

  ### 1.2 MQ的优选和缺点

  

  优点：解耦、削峰、数据分发

  

  缺点：

  - 系统可用性降低

  系统引入的外部依赖越多，系统稳定性越差。一旦MQ宕机，就会对业务造成影响。

  如何保证MQ的高可用

  

  - 系统复杂度提高

  MQ的加入大大增加了系统的复杂度，以前系统间是同步的远程调用，现在是通过MQ进行异步调用。

  如何保证消息没有被重复消费？怎么处理消息丢失情况？那么保证消息传递的顺序性？

  

  - 一致性问题

  A系统处理完业务，通过MQ给B、C、D三个系统发消息数据，如果B系统，C系统处理成功，D系统处理失败。

  瑞和保证消息数据处理的一致性？

  

  ### 1.3 各种MQ产品的比较

  > 常见的MQ产品包含Kafka、ActiveMQ、RabbitMQ、RocketMQ。

  ![image.png](https://cdn.nlark.com/yuque/0/2021/png/12759906/1622415675975-7832671b-1991-408d-a342-1bc19d94afda.png)



## 2、RocketMQ快速入门



RocketMQ是阿里巴巴2016年开源的MQ中间件，使用Java语言开发，在阿里内部，RocketMQ承接了例如“双11”等高并发场景的消息流转，能够处理万亿级别的消息。



### 2.1 准备工作

#### 2.1.1 下载RocketMQ

https://www.apache.org/dyn/closer.cgi?path=rocketmq/4.5.1/rocketmq-all-4.5.1-bin-release.zip

#### 2.1.2 环境要求

- Java8
- 源码安装需要安装Maven 3.2.x

### 2.2 安装

1. 下载完后，上传到Linux服务器，我这边用xftp上传到`/opt`目录
2. 然后用`unzip xxx`命令解压

![img](https://cdn.nlark.com/yuque/0/2022/png/12759906/1644335074802-3084ff2c-4275-4321-bcf5-d9868f090f04.png)

1. 解压之后，移动到`/usr/local/rocketmq`目录下

![img](https://cdn.nlark.com/yuque/0/2022/png/12759906/1644335114738-dde4e626-05dc-4a74-bacb-aab75c65a0fb.png)

1. 安装完成
2. 目录介绍

- bin：启动脚本，包括shell脚本和CMD脚本
- conf：实例配置文件 ，包括broker配置文件、logback配置文件等

- lib：依赖jar包，包括Netty、commons-lang、FastJSON等



### 2.3 启动RocketMQ



1、启动NameServer

```markdown
# 1.启动NameServer
nohup sh bin/mqnamesrv &
# 2.查看启动日志
tail -f ~/logs/rocketmqlogs/namesrv.log
```

2、启动Broker

```markdown
# 1.启动Broker
nohup sh bin/mqbroker -n localhost:9876 &
# 2.查看启动日志
tail -f ~/logs/rocketmqlogs/broker.log 
```

启动成功可以查看对应日志情况，或者用jps查看进程：

![img](https://cdn.nlark.com/yuque/0/2022/png/12759906/1644500102213-b6ce6fa4-89a6-44bc-bdba-d197f3876ec5.png)

如果启动失败，则需要看下对应得配置文件，runbroker.sh/runserver.sh，里面的内存配置是否大于你服务器/虚拟机的内存；

```
JAVA_OPT="${JAVA_OPT} -server -Xms256m -Xmx256m -Xmn128m -XX:MetaspaceSize=128m  -XX:MaxMetaspaceSize=320m"
```



### 2.4 测试RocketMQ

首先必须进到RocketMQ的安装目录的bin目录下，

发送消息：

```shell
# 1.设置环境变量
export NAMESRV_ADDR=localhost:9876
# 2.使用安装包的Demo发送消息
sh bin/tools.sh org.apache.rocketmq.example.quickstart.Producer
```

消费消息：

```shell
# 1.设置环境变量
export NAMESRV_ADDR=localhost:9876
# 2.接收消息
sh bin/tools.sh org.apache.rocketmq.example.quickstart.Consumer
```

关闭RocketMQ：

```shell
# 1.关闭NameServer
sh bin/mqshutdown namesrv
# 2.关闭Broker
sh bin/mqshutdown broker
```
