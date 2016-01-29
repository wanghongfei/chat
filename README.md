## Chat

### Build
```
mvn clean install
```
`master`分支为二进制协议版本, `protocol-websocket`分支为WebSocket版本

## Start
```
java -jar target/char.jar 9000 # ws://localhost:9000/chat
```

## 功能列表
- 一对一聊天 (已通过测试)
- 一对多聊天室聊天 (未测试)

## Chat二进制通讯协议

| 字段名            | 偏移量  | 字段长度     | 数据类型   | 说明                   |
| -------------- | ---- | -------- | ------ | -------------------- |
| sid            | 0    | 10 bytes | string | 会话id                 |
| length         | 10   | 4 bytes  | int    | 数据包总长度               |
| sent time      | 14   | 8 bytes  | long   | 发送时间, 自1970-1-1以来的秒数 |
| type           | 22   | 2 bytes  | string | 数据包的类型               |
| target_user_id | 24   | 4 bytes  | int    | 消息接收者的用户id           |
| target_room_id | 28   | 4 bytes  | int    | 目标聊天室的id             |
| mem_id         | 32   | 4 bytes  | int    | 发送者用户id              |
| body           | 36   |          | string | 消息体                  |

