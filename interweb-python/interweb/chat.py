from interweb.utils import send_get_request, send_post_request


def completions(query):
    return send_post_request("/chat/completions", query)


def chat_all(user):
    params = {"user": user}
    return send_get_request("/chat", params)


def chat_by_id(uuid):
    return send_get_request("/chat/" + uuid)


def chat_complete(conversation):
    results = send_post_request("/chat/completions", conversation)
    if conversation.get("id") is None:
        conversation["id"] = results.get("chat_id")
    if conversation.get("title") is None and results.get("chat_title") is not None:
        conversation["title"] = results.get("chat_title")
    if results.get("cost") is not None:
        conversation["estimated_cost"] = results["cost"].get("chat")
    if results.get("usage") is not None:
        conversation["used_tokens"] = results["usage"].get("total_tokens")
    if conversation.get("created") is None and results.get("created") is not None:
        conversation["created"] = results["created"]
    if results.get("last_message") is not None:
        conversation["messages"].append(results["last_message"])

    return conversation
