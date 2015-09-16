var WebSocket = require('ws');

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
        'type': 0,
        'token': '',
        'memName': '',
        'clientTime': 'client time',
        'targetMemId': ''
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
    }

    sendData.header.token = token;
    sendData.header.targetMemId = 12;
    sendData.header.memId = memId;

    ws.send(JSON.stringify(sendData));
});