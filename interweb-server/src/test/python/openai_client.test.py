import os
from openai import OpenAI

client = OpenAI(
    base_url=os.getenv("INTERWEB_HOST", "https://interweb.l3s.uni-hannover.de/v1"),
    api_key=os.getenv("INTERWEB_APIKEY"),
)

# List available models
models = client.models.list()
print(models)

# chat completion
completion = client.chat.completions.create(
    model="llama3.1:8b", # make sure the model is available, see the list below
    messages=[
      {
        "role": "system",
        "content": "You are an AI assistant that helps people find information."
      },
      {
        "role": "user",
        "content": "Hello, who are you?"
      },
    ],
)
print('assistant:', completion.choices[0].message.content)

# Get embeddings
embed = client.embeddings.create(
    model="bge-m3:latest",
    input="The food was delicious and the waiter..."
)
print(embed)
