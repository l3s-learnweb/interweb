import os
from openai import OpenAI

client = OpenAI(
    base_url=os.getenv("INTERWEB_HOST", "https://interweb.l3s.uni-hannover.de"),
    api_key=os.getenv("INTERWEB_APIKEY"),
)

models = client.models.list()
print(models)

message_text = [{
    "role": "system",
    "content": "You are an AI assistant that helps people find information."
}]

completion = client.chat.completions.create(
    model="llama3.1:8b", # gpt-35-turbo
    messages=message_text,
    temperature=0.7,
    max_tokens=800,
    top_p=0.95,
    frequency_penalty=0,
    presence_penalty=0,
    stop=None
)

print('user:', message_text[0]['content'])
print('assistant:', completion.choices[0].message.content)
