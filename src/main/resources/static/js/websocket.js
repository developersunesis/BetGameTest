// initiate websocket instance
const websocket = new WebSocket("ws://localhost:8080/guessTheNumberGame")
// creates an array object to hold the set of listeners
const webSocketMessageListeners = []

// prepare message function and add message listeners
websocket.onmessage = ({data}) => {
    try {
        const message = JSON.parse(data);
        webSocketMessageListeners.forEach(listener => listener(message.type, message))
    } catch (e) {
        console.error("An error occurred : " + e)
    }
}

// send first message after establishing connection
websocket.onopen = () => {
    websocket.send(JSON.stringify({type: 'PING'}))
}

// when there's a connection error set a timeout and reload the page
// this might not provide a good UX, but it can be used for the time being to
// trigger a reconnection to the socket
websocket.onerror = (e) => {
    console.log("An error occurred : ", e)
    setTimeout(() => document.location.reload(), 10000)
}