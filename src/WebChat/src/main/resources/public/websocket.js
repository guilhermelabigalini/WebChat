//var wsUri = "ws://" + document.location.hostname + ":" + document.location.port + document.location.pathname + "chat";

var wsUri = "ws://" + document.location.hostname + ":" + document.location.port + "/roomsocket";

var websocket = null;
var customUrl;
var username;
var toUserName = null;

function join(roomId) {
    
    toUserName = null;

    username = $("#nickname").val();
    //customUrl = wsUri + "/" + roomId + "/" + username;
    
    customUrl = wsUri + "?roomId=" + roomId + "&displayName=" + username;
    
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
        reserved: $("#reserved").is(':checked'),
        to: toUserName
    };

    console.log(msg);
    websocket.send(JSON.stringify(msg));
    $("#message").val("");
}

function onClose(evt) {
    console.log("Closed ");
    console.log(evt);
    
    displayDisconnectedUI();
    writeToLog("Connection closed");
}

function onOpen() {
    console.log("Connected to " + customUrl);
    
    displayConnectedUI();
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
        removeUserFromList(msg.from);
    } else if (msg.type == "message") {
        if (msg.to != undefined && msg.to != "") {
            if (!msg.reserved) {
                writeToLog("From <b>" + msg.from + "</b> to <b>" + msg.to + "</b>: " + msg.body);
            } else {
                writeToLog("From <b>" + msg.from + "</b> reservedly to <b>" + msg.to + "</b>: " + msg.body);
            }
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
    userArray.forEach(function (entry) {
        addUserToList(entry);
    });
}

function selectcontact(e, contact)
{
    $that = $(e);

    $that.parent().find('a').removeClass('active');
    $that.addClass('active');

    console.log('selected contact: ' + contact);

    if (contact)
        toUserName = contact;
    else
        toUserName = null;
}

function removeUserFromList(userName)
{
    var ul = $("#user-list");
    ul.children('#' + userName).remove();
    
    if (userName == toUserName)
        toUserName = null;
}

function addUserToList(userName)
{
    var ul = $("#user-list");
    if (!ul.children('#' + userName).length) {
        var newLink = $('<a href="#" class="list-group-item" id="' + userName + '">' + userName + '</a>');
        newLink.click(function (evt) {
            selectcontact(this, userName);
        });
        ul.append(newLink);
    }
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

function displayConnectedUI() {
    $("#div-login-form").css('display','none');
    $("#div-message-form").css('display','');
}

function displayDisconnectedUI() {
    $("#div-login-form").css('display','');
    $("#div-message-form").css('display','none');
}