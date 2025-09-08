document.addEventListener("DOMContentLoaded", () => {
    const bell = document.getElementById("bell");
    const dropdown = document.getElementById("notification-dropdown");
    const list = document.getElementById("notification-list");
    const unreadCount = document.getElementById("unread-count");

    const socket = new SockJS("/ws");
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        console.log("✅ WebSocket 연결 성공");

        stompClient.subscribe("/user/queue/notifications", (message) => {
            const notification = JSON.parse(message.body);
            addNotification(notification);
        });
    });

    bell.addEventListener("click", () => {
        dropdown.classList.toggle("hidden");
    });

    function addNotification(notification) {
        const li = document.createElement("li");

        li.textContent =
            `[${notification.notificationType}] ${notification.sender}: ${notification.preview} (${notification.createdAt})`;

        list.appendChild(li);

        let count = parseInt(unreadCount.textContent);
        if (isNaN(count)) count = 0;
        unreadCount.textContent = count + 1;
        unreadCount.style.display = "inline-block";

        dropdown.querySelector(".empty").style.display = "none";
    }
});
