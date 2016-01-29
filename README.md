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
