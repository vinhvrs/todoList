var localStorage = window.localStorage;

function loginUser() {
    const username = document.getElementById("username").value;
    const password = document.getElementById("password").value;
    const errorMessage = document.getElementById("error-message");

    fetch("http://127.0.0.1:1111/api/user/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ username, password }),
        credentials: "include"
    })
    .then(response => {
        if (!response.ok) {
            throw new Error("Invalid username or password");
        }
        return response.json();
    })
    .then(data => {
        localStorage.setItem("userID", data.id);
        localStorage.setItem("username", data.username);
        alert("Login successful!");
        window.location.href = "index.html"; // Redirect to home page
    })
    .catch(error => {
        errorMessage.textContent = error;
    });
}

async function checkSession() {
    try {
        const response = await fetch("http://127.0.0.1:1111/api/user/getSession", {
            method: "GET",
            credentials: "include", // ✅ Ensures session cookie is sent
        });

        if (response.status === 201) {
            throw new Error("No active session");
        }

        const data = await response.json();
        console.log("Session Data:", data);
        //document.getElementById("sessionStatus").innerText = `Logged in as: ${data.username}`;
        window.location.href = "index.html"; // Redirect
    } catch (error) {
        console.error("Error:", error);
        //document.getElementById("sessionStatus").innerText = "No active session";
    }
}

// ✅ Run when page loads
window.onload = checkSession;
