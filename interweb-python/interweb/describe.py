from interweb.utils import send_get_request, send_post_request


def describe(query):
    return send_post_request("/describe", query)


def describe_link(link):
    params = {"link": link}
    return send_get_request("/describe", params)
