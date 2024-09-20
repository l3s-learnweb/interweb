import os
from litellm import completion

## set ENV variables
os.environ["OPENAI_API_KEY"] = os.getenv("INTERWEB_APIKEY")
os.environ["OPENAI_API_BASE"] = "http://localhost:8080"

response = completion(
    model = "openai/llama3.1:latest",
    messages = [{ "content": "Hello, how are you?","role": "user"}]
)
