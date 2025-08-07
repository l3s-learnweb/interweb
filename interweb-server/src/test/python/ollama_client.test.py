import os
from ollama import Client

client = Client(
  host=os.getenv("INTERWEB_HOST", "https://interweb.l3s.uni-hannover.de/v1"),
  headers={'Authorization': 'Bearer ' + os.getenv("INTERWEB_APIKEY")}
)

response = client.chat(model='llama3.1:8b', messages=[
  {
    "role": "system",
    "content": "You are an AI assistant that helps people find information."
  },
  {
    'role': 'user',
    'content': 'Why is the sky blue?',
  },
])
print('assistant:', response['message']['content'])

embed = client.embed(model='bge-m3:latest', input='The sky is blue because of rayleigh scattering')
print(embed)
