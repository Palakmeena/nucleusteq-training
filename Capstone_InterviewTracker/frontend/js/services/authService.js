// Handles authentication-related operations like login/logout
// Communicates with backend auth endpoints

import { request } from "./api.js";

export async function loginUser(email, password) {
    return request("/auth/login", "POST", {
        email,
        password
    });
}