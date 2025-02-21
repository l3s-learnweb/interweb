import os
from openai import OpenAI

client = OpenAI(
    base_url=os.getenv("INTERWEB_HOST", "https://interweb.l3s.uni-hannover.de/v1"),
    api_key=os.getenv("INTERWEB_APIKEY"),
)

# List available models
models = client.models.list()
print(models)

# Create a completion
message_text = [{
    "role": "system",
    "content": "You are an AI assistant that helps people find information."
}]

completion = client.chat.completions.create(
    model="llama3.1:8b", # gpt-35-turbo
    messages=message_text,
    num_ctx=4096,
    temperature=0.7,
    max_tokens=800,
    top_p=0.95,
    frequency_penalty=0,
    presence_penalty=0,
    stop=None
)

print('user:', message_text[0]['content'])
print('assistant:', completion.choices[0].message.content)

# Get embeddings
embed = client.embeddings.create(
    model="snowflake-arctic-embed:33m",
    input="The food was delicious and the waiter..."
)

print(embed)
