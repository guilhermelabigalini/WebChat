//var wsUri = "ws://" + document.location.hostname + ":" + document.location.port + document.location.pathname + "chat";

var wsUri = "ws://" + document.location.hostname + ":" + document.location.port + "/chat";

var websocket = null;
var customUrl;
var username;


function join() {
    username = $("#nickname").val();
    customUrl = wsUri + "/" + "1" + "/" + username;
    console.log("connecting to " + customUrl);
    websocket = new WebSocket(customUrl);
    websocket.onclose = function (evt) {
        onClose(evt);
    };
    websocket.onopen = function (evt) {
        onOpen(evt);
    };
    websocket.onmessage = function (evt) {
        onMessage(evt);
    };
    websocket.onerror = function (evt) {
        onError(evt);
    };
}

function send_message() {

    var msg = {
        type: "message",
        body: $("#message").val(),
        destination: "",
        reserved: $("#reserved").is(':checked')
    };

    console.log(msg);
    websocket.send(JSON.stringify(msg));
    $("#textField").val("");
}

function onClose(evt) {
    console.log("Closed ");
    console.log(evt);
}

function onOpen() {
    console.log("Connected to " + customUrl);
    // websocket.send(username + " joined");
}

function onMessage(evt) {
    var msg = JSON.parse(evt.data);

    console.log("onMessage: ");
    console.log(msg);

    if (msg.type == "joined") {
        writeToLog("User <b>" + msg.from + "</b> has joined the room ");
        addUserToList(msg.from);
    } else if (msg.type == "leave") {
        writeToLog("User <b>" + msg.from + "</b> has left the room");
    } else if (msg.type == "message") {
        if (msg.to != undefined && msg.to != "") {
            writeToLog("From <b>" + msg.from + "</b> to <b>" + msg.to + "</b>: " + msg.body);
        } else {
            writeToLog("From <b>" + msg.from + "</b> to everyone: " + msg.body);
        }
    } else if (msg.type == "userlist") {
        var userArray = JSON.parse(msg.body);
        console.log(userArray);
        addUsers(userArray);
    }
}

function addUsers(userArray)
{
    userArray.forEach(function(entry) {
        addUserToList(entry);
    });
}

function addUserToList(userName)
{
    var ul = $("#user-list");
    if (! ul.children('#' + userName).length) 
        ul.append('<a href="#" class="list-group-item" id="' + userName + '">' + userName + '</a>');
}

function writeToLog(message) {
    var d = new Date();
    var n = d.toLocaleTimeString();
    var div = $("#chatlogField");
    div.append(n + " " + message + "<br>");
    div.scrollTop = div.scrollHeight;
}

function onError(evt) {
    console.log(evt);
}
