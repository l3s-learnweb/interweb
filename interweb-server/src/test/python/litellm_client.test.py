import os
from litellm import completion

## set ENV variables
os.environ["OPENAI_API_BASE"] = os.getenv("INTERWEB_HOST", "https://interweb.l3s.uni-hannover.de")
os.environ["OPENAI_API_KEY"] = os.getenv("INTERWEB_APIKEY")

response = completion(
    model = "openai/llama3.1:8b",
    messages = [{ "content": "Hello, how are you?","role": "user"}]
)

print(response)