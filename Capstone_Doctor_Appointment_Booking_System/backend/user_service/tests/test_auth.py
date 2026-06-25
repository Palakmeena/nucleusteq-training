import pytest

BASE_URL = "http://test"




# ✅ Test 1 — Patient Registration Success
async def test_register_patient_success(client):
    response = await client.post("/api/v1/auth/register/patient", json={
        "full_name": "Test Patient",
        "email": "testpatient@gmail.com",
        "password": "Test@1234",
        "phone": "9876543211",
        "gender": "Female",
        "date_of_birth": "2000-01-01"
    })
    assert response.status_code == 201
    assert response.json()["role"] == "PATIENT"
    assert response.json()["email"] == "testpatient@gmail.com"


# ✅ Test 2 — Duplicate Email Registration
async def test_register_patient_duplicate_email(client):
    data = {
        "full_name": "Test Patient",
        "email": "duplicate@gmail.com",
        "password": "Test@1234",
        "phone": "9876543212",
        "gender": "Female",
        "date_of_birth": "2000-01-01"
    }
    await client.post("/api/v1/auth/register/patient", json=data)
    response = await client.post("/api/v1/auth/register/patient", json=data)
    assert response.status_code == 409


# ✅ Test 3 — Invalid Email Format
async def test_register_patient_invalid_email(client):
    response = await client.post("/api/v1/auth/register/patient", json={
        "full_name": "Test Patient",
        "email": "notanemail",
        "password": "Test@1234",
        "phone": "9876543213",
        "gender": "Female",
        "date_of_birth": "2000-01-01"
    })
    assert response.status_code == 422


# ✅ Test 4 — Invalid Password (no uppercase)
async def test_register_patient_invalid_password(client):
    response = await client.post("/api/v1/auth/register/patient", json={
        "full_name": "Test Patient",
        "email": "test2@gmail.com",
        "password": "test@1234",
        "phone": "9876543214",
        "gender": "Female",
        "date_of_birth": "2000-01-01"
    })
    assert response.status_code == 422


# ✅ Test 5 — Invalid Phone (less than 10 digits)
async def test_register_patient_invalid_phone(client):
    response = await client.post("/api/v1/auth/register/patient", json={
        "full_name": "Test Patient",
        "email": "test3@gmail.com",
        "password": "Test@1234",
        "phone": "12345",
        "gender": "Female",
        "date_of_birth": "2000-01-01"
    })
    assert response.status_code == 422


# ✅ Test 6 — Doctor Registration Success
async def test_register_doctor_success(client):
    response = await client.post("/api/v1/auth/register/doctor", json={
        "full_name": "Test Doctor",
        "email": "testdoctor@gmail.com",
        "password": "Test@1234",
        "phone": "9876543215",
        "qualification": "MBBS",
        "specialization": "Cardiologist",
        "experience": 5,
        "license_number": "LIC123456"
    })
    assert response.status_code == 201
    assert response.json()["role"] == "DOCTOR"
    assert response.json()["is_active"] == False


# ✅ Test 7 — Login Success
async def test_login_success(client):
    await client.post("/api/v1/auth/register/patient", json={
        "full_name": "Login Test",
        "email": "logintest@gmail.com",
        "password": "Test@1234",
        "phone": "9876543216",
        "gender": "Male",
        "date_of_birth": "1999-01-01"
    })
    response = await client.post("/api/v1/auth/login", json={
        "email": "logintest@gmail.com",
        "password": "Test@1234"
    })
    assert response.status_code == 200
    assert "access_token" in response.json()
    assert response.json()["role"] == "PATIENT"


# ✅ Test 8 — Login Wrong Password
async def test_login_wrong_password(client):
    response = await client.post("/api/v1/auth/login", json={
        "email": "logintest@gmail.com",
        "password": "Wrong@1234"
    })
    assert response.status_code == 401


# ✅ Test 9 — Login User Not Found
async def test_login_user_not_found(client):
    response = await client.post("/api/v1/auth/login", json={
        "email": "notexist@gmail.com",
        "password": "Test@1234"
    })
    assert response.status_code == 401


# ✅ Test 10 — Health Check
async def test_health_check(client):
    response = await client.get("/health")
    assert response.status_code == 200