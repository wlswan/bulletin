document.addEventListener("DOMContentLoaded", () => {
    const list = document.getElementById("notification-list");
    const unreadCount = document.getElementById("unread-count");
    const dropdown = document.getElementById("notification-dropdown");

    // WebSocket 연결
    const socket = new SockJS("/ws");
    const stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        console.log("✅ WebSocket 연결 성공");

        stompClient.subscribe("/user/queue/notifications", (message) => {
            const notification = JSON.parse(message.body);
            addNotification(notification);
        });
    });

    // 알림 추가
    function addNotification(notification) {
        const li = document.createElement("li");

        li.textContent =
            `[${notification.notificationType}] ${notification.sender}: ${notification.preview} (${notification.createdAt})`;

        // 👉 클릭 시 해당 알림만 삭제
        li.addEventListener("click", () => {
            li.remove();
            updateUnreadCount(-1);
        });

        list.appendChild(li);
        updateUnreadCount(1);
        dropdown.querySelector(".empty").style.display = "none";
    }

    function updateUnreadCount(delta) {
        let count = parseInt(unreadCount.textContent);
        if (isNaN(count)) count = 0;
        count += delta;
        unreadCount.textContent = count;

        if (count <= 0) {
            unreadCount.style.display = "none";
            dropdown.querySelector(".empty").style.display = "block";
        } else {
            unreadCount.style.display = "inline-block";
        }
    }
});
