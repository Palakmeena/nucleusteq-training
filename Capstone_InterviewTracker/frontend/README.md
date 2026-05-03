# Interview Tracker — Frontend

Lightweight static frontend for the Interview Tracker project.

## Overview
- Static HTML, CSS and vanilla JavaScript.
- Communicates with the backend API at `http://localhost:8080/api` by default.

## Quick start (development)
1. Open the project folder `frontend` in your editor.
2. Serve the files with a simple HTTP server (recommended) or open `index.html` in the browser.

Examples:

```bash
# using Python 3 built-in server
cd frontend
python -m http.server 8000
# open http://localhost:8000 in your browser

# or using npm http-server
cd frontend
npx http-server -p 8000
```

## Configuration
- API base URL is defined in `frontend/js/services/api.js` as `BASE_URL` — change if backend runs on a different host/port.
- Auth token is stored in `localStorage` under the key `token`.

## Important files
- Pages: `pages/` (HTML entry points for different roles)
- Scripts: `js/` (features, services, utils)
- Styles: `css/` (component and page styles)
- Shared SVGs: `js/svg-icons.js` (centralized icon literals)

## Development notes
- The frontend is intentionally small and framework-free so it can be hosted as static files.
- When testing features that call the backend, ensure the backend is running and `BASE_URL` points to it.

## Deployment
- Copy the `frontend` directory contents to any static hosting (GitHub Pages, S3, Netlify, etc.).

## Troubleshooting
- If pages show `Loading...` indefinitely, open browser devtools Network tab and verify API responses.
- If authentication fails, confirm a valid `token` is present in `localStorage`.

## Contact
For backend-related runtime issues (DB, auth, mail), see `backend/README.md`.
