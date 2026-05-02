package com.nucleusteq.interviewtracker.service;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class GoogleDriveServicePrivateTest {

    @Test
    void getCredentials_shouldThrowWhenClientSecretMissing() throws Exception {
        GoogleDriveService svc = new GoogleDriveService();
        Method m = GoogleDriveService.class.getDeclaredMethod("getCredentials");
        m.setAccessible(true);

        Exception ex = assertThrows(Exception.class, () -> m.invoke(svc));
        // underlying cause should indicate missing resource (IOException)
        assertNotNull(ex.getCause());
        String msg = ex.getCause().getMessage();
        assertTrue(msg.contains("client_secret.json") || msg.toLowerCase().contains("not found"));
    }
}
