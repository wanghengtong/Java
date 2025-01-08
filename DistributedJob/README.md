# Springboot整合分布式任务调度平台XXL-Job实现定时任务

xxl-job将调度行为抽象形成“调度中心"公共平台，而平台自身并不承担业务逻辑，“调度中心"负责发起调度请求。 
将任务抽象成分散的JobHandler，交由“执行器“统一管理 “执行器”负责接收调度请求并执行对应的JobHandler中业务逻辑。
因此，“调度”和“任务"两部分可以相互解耦，提高系统整体稳定性和扩展性。

需要依赖xxl-job控制台进行任务配置及调度：
git clone https://gitee.com/xuxueli0323/xxl-job.git