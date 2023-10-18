from interweb.utils import send_post_request


def search(query):
    return send_post_request("/search", query)
