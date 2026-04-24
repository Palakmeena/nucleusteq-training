// Generic function to handle all API calls
// Centralizes fetch logic and error handling

const BASE_URL = "http://localhost:8080/api";

export async function request(endpoint, method = "GET", body = null) {

    const options = {
        method,
        headers: {
            "Content-Type": "application/json"
        }
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    const response = await fetch(BASE_URL + endpoint, options);

    if (!response.ok) {
        throw new Error("API Error");
    }

    return response.json();
}