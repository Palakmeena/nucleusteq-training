"""HTTP request logging middleware configuration."""

from time import perf_counter
from uuid import uuid4

from fastapi import FastAPI, Request, Response

from utils.logger import get_logger


logger = get_logger(__name__)


def configure_request_logging_middleware(app: FastAPI) -> None:
    """Log each request and attach a correlation ID to its response."""

    @app.middleware("http")
    async def log_request(request: Request, call_next) -> Response:
        request_id = str(uuid4())
        request.state.request_id = request_id
        start_time = perf_counter()

        response = await call_next(request)
        duration_ms = (perf_counter() - start_time) * 1000
        response.headers["X-Request-ID"] = request_id

        logger.info(
            "%s %s completed with %s in %.2f ms [request_id=%s]",
            request.method,
            request.url.path,
            response.status_code,
            duration_ms,
            request_id,
        )
        return response
