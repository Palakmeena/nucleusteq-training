package com.nucleusteq.interviewtracker.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ApiResponseTest {

    @Test
    void successFactoryShouldPopulateAllFields() {
        ApiResponse<String> response = ApiResponse.success("Saved", "payload");

        assertTrue(response.isSuccess());
        assertEquals("Saved", response.getMessage());
        assertEquals("payload", response.getData());
    }

    @Test
    void errorFactoryShouldSetSuccessFalseAndNullData() {
        ApiResponse<String> response = ApiResponse.error("Failed");

        assertFalse(response.isSuccess());
        assertEquals("Failed", response.getMessage());
        assertNull(response.getData());
    }

    @Test
    void constructorsAndSettersShouldWork() {
        ApiResponse<String> response = new ApiResponse<>();
        response.setSuccess(true);
        response.setMessage("Updated");
        response.setData("value");

        assertTrue(response.isSuccess());
        assertEquals("Updated", response.getMessage());
        assertEquals("value", response.getData());

        ApiResponse<String> withData = new ApiResponse<>(true, "Created", "body");
        assertTrue(withData.isSuccess());
        assertEquals("Created", withData.getMessage());
        assertEquals("body", withData.getData());

        ApiResponse<String> withoutData = new ApiResponse<>(false, "Rejected");
        assertFalse(withoutData.isSuccess());
        assertEquals("Rejected", withoutData.getMessage());
        assertNull(withoutData.getData());
    }
}