//  Handle login form submission
//  Validates user input before sending API request

import { loginUser } from "../services/authService.js";

document.getElementById("loginForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    try {
        const response = await loginUser(email, password);

        console.log("Login Success:", response);

        // Save token
        localStorage.setItem("token", response.token);

        // Redirect (example HR dashboard)
        window.location.href = "../hr/dashboard.html";

    } catch (error) {
        alert("Login failed: " + error.message);
    }
});