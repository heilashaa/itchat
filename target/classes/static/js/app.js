'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var stompClient = null;
var username = null;
var colors = [
    '#2196F3', '#32c787', '#ff5652',
    '#ffc107', '#ff85af', '#FF9800'
];

//dalete facebook #_=_, preloader, call connection
$(window).on('load', function(e){
    let preloader = $('.preloader');
    let loader = preloader.find('.prePreloader');
    loader.fadeOut();
    preloader.delay(350).fadeOut('slow');
    if (window.location.hash === '#_=_') {
        window.location.hash = '';
        history.pushState('', document.title, window.location.pathname);
        e.preventDefault();
    }
    if($("#name").val().length > 0) {
        connect(e);
    }
})

//create connection
function connect(event) {
    username = document.querySelector('#name').value.trim();
    usernamePage.classList.add('hidden');
    chatPage.classList.remove('hidden');
    $("#user").append(username);
    $("#message").focus();
    var socket = new SockJS('/ws');
    // var socket = new WebSocket('ws://localhost:8080/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}

// exit
$(function(){
    $("#exit").bind('click', function(e){
        if (stompClient !== null) {
            stompClient.disconnect();
        }
        $(location).attr('href','http://localhost:8080');
    })
})

//on connected handler
function onConnected() {

    stompClient.subscribe('/user/queue/reply', onMessageReceived);
    stompClient.subscribe('/topic/public', onMessageReceived);
    stompClient.subscribe('/topic/delete', onMessageReceivedForDelete);
    stompClient.subscribe('/topic/edit', onMessageReceivedForEdit);
    stompClient.send(
        "/app/chat.addUser",
        {},
        JSON.stringify({sender: username, type: 'JOIN'})
    )
    connectingElement.classList.add('hidden');
}

//error handler
function onError(error) {
    connectingElement.textContent = 'Could not connect to WebSocket server!';
    connectingElement.style.color = 'red';
}

//send message
function sendMessage(event) {
    var messageContent = messageInput.value.trim();
    if(messageContent && stompClient) {
        var chatMessage = {
            sender: username,
            content: messageInput.value,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        messageInput.value = '';
    }
    event.preventDefault();
}

//edit message
function onMessageReceivedForEdit(payload) {
    var message = JSON.parse(payload.body);
    var editMessageId = '#m' + message.id;
    // alert('получение'+ editMessageId);
    document.querySelector(editMessageId).textContent = message.content;
}

//delete message
function onMessageReceivedForDelete(payload) {
    var message = JSON.parse(payload.body);
    var deleteMessageId = '#cm' + message.id;
    var chatPage = document.querySelector(deleteMessageId);
    chatPage.classList.add('hidden');
}

//on message received handler
function onMessageReceived(payload) {
    var message = JSON.parse(payload.body);

    var messageElement = document.createElement('li');

    if(message.type === 'JOIN') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' joined!';
    } else if (message.type === 'LEAVE') {
        messageElement.classList.add('event-message');
        message.content = message.sender + ' left!';
    } else {
        messageElement.classList.add('chat-message');
        messageElement.setAttribute('id' , 'cm' + message.id);

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);

        if(message.sender === username && message.type === 'CHAT'){
            var editButton = document.createElement('button');
            editButton.classList.add('btn', 'btn-default', 'btn-xs', 'editbutton');
            editButton.setAttribute('type' , 'button');
            editButton.setAttribute('id' , 'e' + message.id);
            var editButtonText = document.createTextNode('Edit');
            editButton.append(editButtonText);
            messageElement.append(editButton);

            var deleteButton = document.createElement('button');
            deleteButton.classList.add('btn', 'btn-default', 'btn-xs', 'deletebutton');
            deleteButton.setAttribute('type' , 'button');
            deleteButton.setAttribute('id' , 'd' + message.id);
            var deleteButtonText = document.createTextNode('Delete');
            deleteButton.append(deleteButtonText);
            messageElement.append(deleteButton);


        }

        if(message.sender === username && message.type === 'PICT'){
            var deleteButton = document.createElement('button');
            deleteButton.classList.add('btn', 'btn-default', 'btn-xs', 'deletebutton');
            deleteButton.setAttribute('type' , 'button');
            deleteButton.setAttribute('id' , 'd' + message.id);
            var deleteButtonText = document.createTextNode('Delete');
            deleteButton.append(deleteButtonText);
            messageElement.append(deleteButton);
        }

    }
    var textElement = document.createElement('p');
    textElement.setAttribute('id' , 'm' + message.id);
    if(message.type === 'PICT'){
        var textElement = document.createElement('img');
        textElement.setAttribute('id' , 'm' + message.id);
        textElement.setAttribute('src' , message.content);
    }else{
        var messageText = document.createTextNode(message.content);
        textElement.appendChild(messageText);
    }
    messageElement.appendChild(textElement);
    messageArea.appendChild(messageElement);
    messageArea.scrollTop = messageArea.scrollHeight;
}

//avatar color
function getAvatarColor(messageSender) {
    var hash = 0;
    for (var i = 0; i < messageSender.length; i++) {
        hash = 31 * hash + messageSender.charCodeAt(i);
    }
    var index = Math.abs(hash % colors.length);
    return colors[index];
}

// sendMessage
// messageForm.addEventListener('submit', sendMessage, true)
$(document).on('click', '#sendMessage', function (event) {
    if(this.classList.contains('edit')) {
        event.preventDefault();
    }else{
        sendMessage(event);
    }
})

// facebook button handler
$(function(){$("#facebookbutton").bind('click', function(e){$(location).attr('href','http://localhost:8080/login/facebook');})})

// twitter button handler
$(function(){$("#twitterbutton").bind('click', function(e){$(location).attr('href','http://localhost:8080/login/twitter');})})

//delete button handler
$(document).on('click', '.deletebutton', function (event) {
    var deleteId = event.target.id.substr(1);

    if(stompClient) {
        var chatMessage = {
            id: deleteId,
            sender: username,
            type: 'CHAT'
        };
        stompClient.send("/app/chat.deleteMessage", {}, JSON.stringify(chatMessage));
    }
    event.preventDefault();

})

var editId;
//edit button handler
$(document).on('click', '.editbutton', function (event) {
    var tempButton = document.querySelector('#sendMessage');
    tempButton.classList.add('edit');
    // alert('до' + editId)
    editId = event.target.id.substr(1);
    messageInput.value = document.querySelector('#m' + editId).textContent.trim();
    // alert('после' + editId)
    event.preventDefault();


    $(document).on('click', '#sendMessage', function (e) {
        // alert('начало event' + editId)

        if(this.classList.contains('edit')) {
            if(stompClient) {
                var chatMessage = null;
                chatMessage = {
                    id: editId,
                    sender: username,
                    content: messageInput.value.trim(),
                    type: 'CHAT'
                };
                // alert('отправка' + chatMessage.id)
                stompClient.send("/app/chat.editMessage", {}, JSON.stringify(chatMessage));
                messageInput.value = '';
            }
        }
        tempButton.classList.remove('edit');
        e.preventDefault();
    });
})

/////////////////
//drag and drop//
/////////////////
const cloudName = 'donmgpkp9';
const unsignedUploadPreset = 'xqnn3gbf';
var fileElem = document.getElementById("fileElem"),
    urlSelect = document.getElementById("urlSelect");
var picture = '';
urlSelect.addEventListener("click", function(e) {
    uploadFile('https://res.cloudinary.com/demo/image/upload/sample.jpg')
    e.preventDefault();
}, false);
// ************************ Drag and drop ***************** //
function dragover(e) {
    e.stopPropagation();
    e.preventDefault();
}
var dropbox = document.getElementById("dropbox");
dropbox.addEventListener("dragover", dragover, false);
dropbox.addEventListener("drop", drop, false);
function drop(e) {
    e.stopPropagation();
    e.preventDefault();
    var dt = e.dataTransfer;
    var files = dt.files;
    handleFiles(files);
}
// *********** Upload file to Cloudinary ******************** //
function uploadFile(file) {
    var url = `https://api.cloudinary.com/v1_1/${cloudName}/upload`;
    var xhr = new XMLHttpRequest();
    var fd = new FormData();
    xhr.open('POST', url, true);
    xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
    xhr.onreadystatechange = function(e) {
        if (xhr.readyState == 4 && xhr.status == 200) {
            var response = JSON.parse(xhr.responseText);
            var url = response.secure_url;
            var tokens = url.split('/');
            tokens.splice(-2, 0, 'w_150,c_scale');
            var img = new Image();
            img.src = tokens.join('/');
            img.alt = response.public_id;

            //send on server
            if(stompClient) {
                var chatMessage = {
                    sender: username,
                    content: img.getAttribute('src'),
                    type: 'PICT'
                };
                stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
            }

        }
    };
    fd.append('upload_preset', unsignedUploadPreset);
    fd.append('tags', 'browser_upload');
    fd.append('file', file);
    xhr.send(fd);



}
// *********** Handle selected files ******************** //
var handleFiles = function(files) {
    for (var i = 0; i < files.length; i++) {
        uploadFile(files[i]);

    }
};

