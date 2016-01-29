//var WebSocket = require('ws');

var net = require("net");

var PORT = 8080;
var HOST = "127.0.0.1"

var client = new net.Socket();
client.connect(PORT, HOST, function(){
    console.log('connect to ' + HOST + ':' + PORT);
    client.write('hello');


    client.destroy();
});

client.on('close', function(){
    console.log('connetion closed.');
});

/*
var ws = new WebSocket('ws://localhost:8080/chat', function() {
    console.log('client has been started');
});

var mem = {
    'nickname': 'apple',
    'gender': '0'
}

var shakeData = {
    'header': {
        'type': 2
    },
    'content': JSON.stringify(mem)
};

var sendData = {
    'header': {
        'type': 1, // 1:聊天室信息, 4:加入聊天室
        'token': '',
        'memName': '',
        'clientTime': 'client time',
        'targetRoomId': '0'
    },

    'content': 'this is wanghongfei'
}

ws.on('open', function open() {
    console.log("send message:" + JSON.stringify(shakeData));
    ws.send(JSON.stringify(shakeData));
});

var first = true;
var token = null;
var memId = null;
ws.on('message', function(data, flags) {
    console.log('server says: %s', data);

    var resp = JSON.parse(data);

    if (first) {
        token = resp.data.token;
        memId = resp.data.memberId;
        console.log("token = ", token);
        console.log("memberId = ", memId);

        first = false;

        sendData.header.token = token;
        sendData.header.memId = memId;

        ws.send(JSON.stringify(sendData));
    }


});*/
