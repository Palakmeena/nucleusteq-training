import { request } from "./api.js";

export async function loginUser(email, password) {
    return request("/auth/login", "POST", {
        email,
        password
    });
}