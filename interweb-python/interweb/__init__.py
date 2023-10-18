# Interweb Python bindings.

import os

from interweb.suggest import (
    suggest,
)

from interweb.search import (
    search,
)

from interweb.describe import (
    describe,
    describe_link,
)

from interweb.chat import (
    completions,
    chat_all,
    chat_complete,
    chat_by_id,
)

from interweb.error import InvalidRequestError, InterwebError, Timeout
from interweb.version import VERSION

api_key = os.environ.get("INTERWEB_API_KEY")
api_base = os.environ.get("INTERWEB_API_BASE", "https://interweb.l3s.uni-hannover.de")

log = None  # Set to either 'debug' or 'info', controls console logging

__version__ = VERSION
__all__ = [
    "InvalidRequestError",
    "InterwebError",
    "Timeout",
    "api_base",
    "api_key",
    "log",
]
