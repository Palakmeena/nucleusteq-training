"""Tests for logger utility."""

import logging
from logging.handlers import RotatingFileHandler

from utils.logger import get_logger


def test_get_logger_returns_logger_instance():
    """Should return a Logger instance."""

    logger = get_logger("test_logger")

    assert isinstance(logger, logging.Logger)


def test_logger_has_correct_name():
    """Logger should have the provided name."""

    logger = get_logger("my_logger")

    assert logger.name == "my_logger"


def test_logger_level_is_debug():
    """Logger level should be DEBUG."""

    logger = get_logger("debug_logger")

    assert logger.level == logging.DEBUG


def test_logger_has_stream_handler():
    """Logger should contain a StreamHandler."""

    logger = get_logger("stream_logger")

    assert any(
        isinstance(handler, logging.StreamHandler)
        for handler in logger.handlers
    )


def test_logger_has_rotating_file_handler():
    """Logger should contain a RotatingFileHandler."""

    logger = get_logger("file_logger")

    assert any(
        isinstance(handler, RotatingFileHandler)
        for handler in logger.handlers
    )


def test_logger_does_not_duplicate_handlers():
    """Calling get_logger twice should not duplicate handlers."""

    logger = get_logger("duplicate_logger")

    initial_handler_count = len(logger.handlers)

    logger = get_logger("duplicate_logger")

    assert len(logger.handlers) == initial_handler_count