'use strict';

var usernamePage = document.querySelector('#username-page');
var chatPage = document.querySelector('#chat-page');
var messageForm = document.querySelector('#messageForm');
var messageInput = document.querySelector('#message');
var messageArea = document.querySelector('#messageArea');
var connectingElement = document.querySelector('.connecting');
var stompClient = null;
var username = null;
var colors = ['#2196F3', '#ff5652', '#ffc107'];

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
    stompClient.subscribe('/topic/public', onMessageReceived);
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
        messageElement.classList.add(message.id);

        var avatarElement = document.createElement('i');
        var avatarText = document.createTextNode(message.sender[0]);
        avatarElement.appendChild(avatarText);
        avatarElement.style['background-color'] = getAvatarColor(message.sender);

        messageElement.appendChild(avatarElement);

        var usernameElement = document.createElement('span');
        var usernameText = document.createTextNode(message.sender);
        usernameElement.appendChild(usernameText);
        messageElement.appendChild(usernameElement);
    }


    //todo если пользователь совпадает, то добавить delete и edit

    var textElement = document.createElement('p');
    textElement.classList.add(message.id);
    var messageText = document.createTextNode(message.content);
    textElement.appendChild(messageText);

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

messageForm.addEventListener('submit', sendMessage, true)

// facebook button handler
$(function(){$("#facebookbutton").bind('click', function(e){$(location).attr('href','http://localhost:8080/login/facebook');})})

// twitter button handler
$(function(){$("#twitterbutton").bind('click', function(e){$(location).attr('href','http://localhost:8080/login/twitter');})})
