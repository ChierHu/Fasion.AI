# FABS

> Fasion Backend Services

## 项目结构

> ai.fasion.fabs

Name | Port | Description
---|---|---
apollo(阿波罗) | 56100 | 用户交互项目
diana(阿蒂蜜丝) | 56200 | 后台程序
minerva(雅典娜) | 56300 | 调度任务
vesta(荷丝提雅) | 无 | 通用包
vulcan(赫菲斯托斯) | 56500 | API 接口
mercury(荷米斯) | 56400 | 支付项目

## 开发

准备工作

- 配置好 `ssh` 和 `git` 访问
- 安装 `docker` 和 `docker-compose`
- 配置 `docker`，开启 `buildkit` 特性
- 配置好 `ssh-agent`，确保 `ssh-add -l` 正确显示你的私钥

然后构建前端项目的本地镜像：

```shell
$ docker build --ssh default --no-cache -f deploy/Website.Dockerfile -t fasion/website .
```

然后就可以启动开发环境了：
```shell
# 启动 web，db，redis
# 访问 http://localhost:3000
$ docker-compose -f develop.yml up
```
