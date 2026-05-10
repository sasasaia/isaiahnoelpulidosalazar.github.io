const ui = {
    level: document.getElementById("level"),
    highScore: document.getElementById("highScore"),
    startBtn: document.getElementById("startBtn"),
    submitBtn: document.getElementById("submitBtn"),
    inputArea: document.getElementById("inputArea"),
    userInput: document.getElementById("userInput"),
    numberDisplay: document.getElementById("numberDisplay"),
    timerContainer: document.getElementById("timerContainer"),
    timerBar: document.getElementById("timerBar"),
    message: document.getElementById("message")
};

let currentNumber = "";
let level = 1;
let score = 0;
let highScore = parseInt(localStorage.getItem("highScore"), 10) || 0;
let displayTimeout;
let timerTimeout;

ui.highScore.textContent = highScore;

function generateNumber(length) {
    let num = "";
    for (let i = 0; i < length; i++) {
        num += Math.floor(Math.random() * 10);
    }
    return num;
}

function startGame() {
    ui.startBtn.classList.add("display-none");
    ui.message.textContent = "";
    ui.message.classList.remove("color-#10b981", "color-#ef4444");
    
    ui.inputArea.classList.remove("display-flex");
    ui.inputArea.classList.add("display-none");
    ui.userInput.value = "";
    
    ui.numberDisplay.classList.remove("color-#10b981", "color-#ef4444");
    ui.level.textContent = level;

    currentNumber = generateNumber(level);
    ui.numberDisplay.textContent = currentNumber;

    const displayTime = 1 + (level * 0.5);

    startTimer(displayTime);

    clearTimeout(displayTimeout);
    displayTimeout = setTimeout(() => {
        ui.numberDisplay.textContent = "?";
        ui.timerContainer.classList.remove("opacity-1");
        ui.timerContainer.classList.add("opacity-0");
        
        ui.inputArea.classList.remove("display-none");
        ui.inputArea.classList.add("display-flex");
        ui.userInput.focus();
    }, displayTime * 1000);
}

function startTimer(seconds) {
    ui.timerContainer.classList.remove("opacity-0");
    ui.timerContainer.classList.add("opacity-1");
    
    ui.timerBar.style.transition = "none";
    ui.timerBar.style.width = "100%";

    clearTimeout(timerTimeout);
    timerTimeout = setTimeout(() => {
        ui.timerBar.style.transition = `width ${seconds}s linear`;
        ui.timerBar.style.width = "0%";
    }, 50);
}

function checkAnswer() {
    const answer = ui.userInput.value.trim();
    
    if (answer === "") return;

    if (answer === currentNumber) {
        score++;
        level++;
        
        ui.message.textContent = "Correct!";
        ui.message.classList.add("color-#10b981");
        ui.message.classList.remove("color-#ef4444");
        
        if (score > highScore) {
            highScore = score;
            localStorage.setItem("highScore", highScore);
            ui.highScore.textContent = highScore;
        }

        ui.inputArea.classList.remove("display-flex");
        ui.inputArea.classList.add("display-none");
        
        ui.numberDisplay.textContent = "Great Job";
        ui.numberDisplay.classList.add("color-#10b981");

        setTimeout(startGame, 1200);
    } else {
        ui.message.textContent = `Incorrect! The correct number was ${currentNumber}`;
        ui.message.classList.add("color-#ef4444");
        ui.message.classList.remove("color-#10b981");
        
        level = 1;
        score = 0;
        
        ui.inputArea.classList.remove("display-flex");
        ui.inputArea.classList.add("display-none");
        
        ui.startBtn.classList.remove("display-none");
        ui.startBtn.textContent = "Try Again";
        
        ui.numberDisplay.textContent = "Game Over";
        ui.numberDisplay.classList.add("color-#ef4444");
    }
}

ui.startBtn.addEventListener("click", startGame);
ui.submitBtn.addEventListener("click", checkAnswer);

ui.userInput.addEventListener("keyup", function(event) {
    if (event.key === "Enter") {
        checkAnswer();
    }
});