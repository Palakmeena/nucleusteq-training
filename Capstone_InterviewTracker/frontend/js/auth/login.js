//  Handle login form submission
//  Validates user input before sending API request


document.getElementById("loginForm").addEventListener("submit", async function (e) {
    e.preventDefault();

    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const errorMsg = document.getElementById("errorMsg");

    try {
        if(errorMsg) errorMsg.style.display = "none";
        
        // 1. Send REAL request to Spring Boot Backend
        const response = await fetch("http://localhost:8080/auth/login", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ email: email, password: password })
        });

        if (!response.ok) {
            throw new Error("Invalid credentials");
        }

        const data = await response.json();
        console.log("Login Success:", data);

        // 2. Save the real JWT token
        localStorage.setItem("token", data.token);
        localStorage.setItem("userEmail", email);
        
        // 3. Decode JWT payload to get role
        const payloadBase64 = data.token.split('.')[1];
        const decodedPayload = JSON.parse(atob(payloadBase64));
        
        let redirectUrl = "../../index.html"; 
        
        if (email.includes("hr")) {
            localStorage.setItem("role", "HR");
            redirectUrl = "../hr/pipeline.html";
        } else if (email.includes("panel")) {
            localStorage.setItem("role", "PANEL");
            redirectUrl = "../panel/interview-dashboard.html";
        } else {
            localStorage.setItem("role", "CANDIDATE");
        }

        window.location.href = redirectUrl;

    } catch (error) {
        if(errorMsg) {
            errorMsg.innerText = "Login failed: " + error.message;
            errorMsg.style.display = "block";
        } else {
            alert("Login failed: " + error.message);
        }
    }
});