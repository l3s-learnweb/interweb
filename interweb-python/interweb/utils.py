import json
import requests
import interweb
from interweb.error import Unauthorized, InterwebError


def send_get_request(api_path: str, params: dict = None) -> dict:
    url = interweb.api_base + api_path
    response = requests.get(url, params=params, headers={"Api-Key": interweb.api_key, "Accept": "application/json"})
    if response.status_code == 401:
        raise Unauthorized(http_status=response.status_code, message=response.reason, body=response.text)
    if response.status_code != 200:
        raise InterwebError(http_status=response.status_code, message=response.reason, body=response.text)
    return response.json()


def send_post_request(api_path: str, query: dict) -> dict:
    url = interweb.api_base + api_path
    headers = {"Api-Key": interweb.api_key, "Content-Type": "application/json", "Accept": "application/json"}
    response = requests.post(url, data=json.dumps(query), headers=headers)
    return response.json()
