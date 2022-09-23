// elements
const currentGameIdSpan = document.getElementById('currentGameId')
const betFeedbackContainer = document.getElementById('betFeedback')
const activeGamePlayersList = document.getElementById('activeGamePlayers')
const currentGameCountDownSpan = document.getElementById('currentGameCountDown')
const currentGameInfoItems = document.querySelectorAll('.current-game-info-item')
const activeGamePlayerCountSpan = document.getElementById('activeGamePlayerCount')
const activeGameCorrectNumberSpan = document.getElementById('activeGameCorrectNumber')

// form elements
const betSuccessAlertMessage = document.getElementById('betSuccessAlertMessage')
const betAlertMessage = document.getElementById('betAlertMessage')
const placeBetBtn = document.getElementById('placeBet')
const nicknameEle = document.getElementById('nickname')
const numberEle = document.getElementById('number')
const stakeEle = document.getElementById('stake')

let activeGame = null

// message alert
function messageAlert(show, message = null, element = betAlertMessage) {
    // hide the message alert after 3 seconds
    setTimeout(() => element.style.display = 'none', 3000)
    if (show && message) {
        element.innerHTML = message
        element.style.display = 'block'
        return
    }

    // hide message
    element.innerHTML = ''
    element.style.display = 'none'
}

//// Add socket listeners
// ping response listener
webSocketMessageListeners.push((type, message) => {
    if (type !== 'PING' || !message.data) return
    activeGame = Object.values(message.data).filter(game => game?.active)[0]
    startCountDown()
})

// new game response listener
webSocketMessageListeners.push((type, message) => {
    if (type !== 'NEW_GAME' || !message.data) return
    activeGame = message.data
    startCountDown()
})

// placed bet response listener
webSocketMessageListeners.push((type, message) => {
    if (type !== 'PLACE_BET' || !message.data) return
    activeGame = message.data
    messageAlert(true, "Your bet was placed successfully!", betSuccessAlertMessage)
})

// bet feedback response listener
webSocketMessageListeners.push((type, message) => {
    if (type !== 'BET_FEEDBACK' || !message.data) return
    // add bet feedback to player's bet history
    addBetFeedback(message.data)
})

// end game response listener
webSocketMessageListeners.push((type, message) => {
    if (type !== 'TIMED_OUT' || !message.data) return
    // handle ended game by updating summary
    populateActiveGameSummary(message.data)
})

// error response listener
webSocketMessageListeners.push((type, message) => {
    if (type !== 'ERROR' || !message.message) return
    messageAlert(true, message.message)
})
//// End add socket listeners

// prepare place bet button listener
placeBetBtn.addEventListener('click', function () {
    const nickname = nicknameEle.value
    const number = numberEle.value
    const stake = stakeEle.value

    // clear any current error in preparation for client-level validation
    messageAlert(false)

    // provide some minor frontend validations
    if (!nickname || nickname.trim() === '') {
        messageAlert(true, "Please provide a nickname")
        return
    } else if (!number || number.trim() === '') {
        messageAlert(true, "Please provide a number")
        return
    } else if (!stake || stake.trim() === '') {
        messageAlert(true, "Please provide a stake")
        return
    }

    // place player's bet
    websocket.send(JSON.stringify({
        type: 'PLACE_BET', data: {
            gameId: activeGame.id, nickname, number, stake
        }
    }))
})

// start countdown towards the end of the current game
const startCountDown = () => {
    if (!activeGame) return

    // countdown for the current game
    setInterval(() => {
        const {timeoutAt} = activeGame
        const currentDateInSecs = new Date().getTime()
        const timeDiff = timeoutAt - currentDateInSecs
        const secondCounter = Math.round(timeDiff / 1000)
        if (secondCounter >= 0) {
            currentGameCountDownSpan.innerHTML = secondCounter >= 10
                ? `00:${secondCounter}` : `00:0${secondCounter}`
        }
    }, 1000)
}

// show the summary of the latest ended game
function populateActiveGameSummary(activeGame) {
    if (!activeGame) return

    // deconstruct the game object
    const {players, correctNumber} = activeGame

    // sort the players bets in descending order by time
    const sortedPlayersBet = Object.values(players).sort((a, b) => {
        if (!a || !b) return -1
        return a && b && a['createdAt'] < b['createdAt'] ? 1 : 0
    }) ?? []

    // make all summary elements visible
    currentGameInfoItems.forEach(ele => ele.style.display = 'block')

    // update the view to match current active game
    activeGamePlayersList.innerHTML = ''
    currentGameIdSpan.innerHTML = activeGame?.id
    activeGameCorrectNumberSpan.innerHTML = correctNumber
    activeGamePlayerCountSpan.innerHTML = `${sortedPlayersBet.length}`

    // update view to reflect newly added bets
    sortedPlayersBet.forEach(player => {
        const {
            guessedNumber, stakeAmount, nickname, id, endOfGameBalance, stakeStatus, createdAt
        } = player
        const tr = document.createElement('tr')

        const nicknameEle = document.createElement("td")
        nicknameEle.innerHTML = `${nickname} (${id.slice(0, 6)})`
        tr.append(nicknameEle)

        const numberEle = document.createElement("td")
        numberEle.innerHTML = guessedNumber
        tr.append(numberEle)

        const stakeEle = document.createElement("td")
        stakeEle.innerHTML = stakeAmount
        tr.append(stakeEle)

        const winningsEle = document.createElement("td")
        winningsEle.innerHTML = endOfGameBalance
        tr.append(winningsEle)

        const feedbackEle = document.createElement("td")
        feedbackEle.innerHTML = stakeStatus
        tr.append(feedbackEle)

        const createdAtDate = new Date(createdAt)
        const dateEle = document.createElement("td")
        dateEle.innerHTML = createdAtDate.toLocaleTimeString()
        tr.append(dateEle)
        activeGamePlayersList.append(tr)
    })
}

// add feedback of the player's bet to their bet history
function addBetFeedback(player) {
    if (!player) return

    const {nickname, id, endOfGameBalance, stakeStatus, createdAt} = player
    const createdAtDate = new Date(createdAt)

    // prepare player feedback
    const feedbackEle = document.createElement("p")
    const feedback = `Your bet on (${createdAtDate.toLocaleTimeString()}) : `
    const playerNickname = `${nickname} (${id.slice(0, 6)})`
    if (stakeStatus === 'WIN') {
        feedbackEle.innerHTML = feedback + `<b>${playerNickname}</b> Won ${endOfGameBalance}`
    } else {
        feedbackEle.innerHTML = feedback + `<b>${playerNickname}</b> Lost`
    }

    // add player feedback to bet history
    betFeedbackContainer.prepend(feedbackEle)
}