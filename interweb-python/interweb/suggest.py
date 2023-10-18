from typing import TypedDict, List
from interweb.utils import send_post_request, send_get_request


class SuggestQuery(TypedDict):
    query: str
    services: List[str]
    language: str
    timeout: int


class SuggestConnectorResults(TypedDict):
    service: str
    service_url: str
    elapsed_time: int
    items: List[str]
    created: str


class SuggestResults(TypedDict):
    query: SuggestQuery
    elapsed_time: int
    results: List[SuggestConnectorResults]


def suggest(query: str, language: str = None, services: list = None, timeout: int = None) -> SuggestResults:
    params = SuggestQuery(query=query, language=language, services=services, timeout=timeout)
    return send_post_request("/suggest", params)
