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
    model="llama3.1:8b", # gpt-4.1-mini
    messages=message_text,
    temperature=0.7,
    max_tokens=4098, # if more than 2048, it will increase `num_ctx` of the model
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
