// ============================================================
// api.js — centralized API service
// All API calls go through here — base URL and auth headers
// handled in one place so nothing is repeated across pages.
// ============================================================

const BASE_URL = 'http://localhost:8080/api';

function getToken() {
    return localStorage.getItem('token');
}

function buildHeaders() {
    const headers = { 'Content-Type': 'application/json' };
    const token = getToken();
    if (token) headers['Authorization'] = 'Bearer ' + token;
    return headers;
}

async function request(method, path, body = null) {
    const options = { method, headers: buildHeaders() };
    if (body) options.body = JSON.stringify(body);
    const res = await fetch(BASE_URL + path, options);
    const data = await res.json();
    if (!res.ok) throw new Error(data.message || 'Something went wrong');
    return data;
}

const api = {
    // AUTH
    login: (email, password) => request('POST', '/auth/login', { email, password }),
    signup: (fullName, email, password) => request('POST', '/auth/signup', { fullName, email, password }),
    activate: (token, password) => request('POST', `/auth/activate?token=${token}&password=${encodeURIComponent(password)}`),

    // JD - public
    getAllPublicJds: () => request('GET', '/jd/all'),
    getJdById: (id) => request('GET', `/jd/${id}`),

    // JD - HR only
    getAllJdsForHr: () => request('GET', '/hr/jd/all'),
    createJd: (body) => request('POST', '/hr/jd', body),
    updateJd: (id, body) => request('PUT', `/hr/jd/${id}`, body),
    deactivateJd: (id) => request('DELETE', `/hr/jd/${id}`),

    // CANDIDATES - public
    registerCandidate: (body) => request('POST', '/candidate/register', body),

    // CANDIDATES - HR
    createCandidateByHr: (body) => request('POST', '/hr/candidate', body),
    getAllCandidates: () => request('GET', '/hr/candidates'),
    getCandidateById: (id) => request('GET', `/hr/candidate/${id}`),
    updateCandidateStage: (id, stage) => request('PUT', `/hr/candidate/${id}/stage?stage=${stage}`),

    // CANDIDATES - candidate
    getMyProfile: () => request('GET', '/candidate/profile'),
    updateMyProfile: (body) => request('PUT', '/candidate/profile', body),
    getMyInterviews: () => request('GET', '/candidate/interviews'),

    // RESUME UPLOAD
    uploadResume: async (candidateId, file) => {
        const formData = new FormData();
        formData.append('file', file);
        const res = await fetch(`${BASE_URL}/candidate/resume/${candidateId}`, {
            method: 'POST',
            body: formData
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Upload failed');
        return data;
    },

    uploadProfileResume: async (file) => {
        const formData = new FormData();
        formData.append('file', file);
        const token = getToken();
        const res = await fetch(`${BASE_URL}/candidate/profile/resume`, {
            method: 'POST',
            headers: token ? { 'Authorization': 'Bearer ' + token } : {},
            body: formData
        });
        const data = await res.json();
        if (!res.ok) throw new Error(data.message || 'Upload failed');
        return data;
    },

    // PANEL - HR
    createPanelMember: (body) => request('POST', '/hr/panel', body),
    getAllPanelMembers: () => request('GET', '/hr/panels'),
    getPanelMemberById: (id) => request('GET', `/hr/panel/${id}`),
    updatePanelMember: (id, body) => request('PUT', `/hr/panel/${id}`, body),
    deletePanelMember: (id) => request('DELETE', `/hr/panel/${id}`),

    // PANEL - panel member
    getMyPanelProfile: () => request('GET', '/panel/profile'),
    getMyAssignedInterviews: () => request('GET', '/panel/interviews'),

    // INTERVIEWS - Panel/HR
    scheduleInterview: (body) => request('POST', '/hr/interview', body),
    getInterviewById: (id) => request('GET', `/hr/interview/${id}`),
    getInterviewsForCandidate: (candidateId) => request('GET', `/hr/interview/candidate/${candidateId}`),
    updateInterviewFeedback: (id, body) => request('PUT', `/panel/interview/${id}/feedback`, body),
};

window.api = api;
