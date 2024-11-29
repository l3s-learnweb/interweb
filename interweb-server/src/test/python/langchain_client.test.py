import os
from langchain_openai import ChatOpenAI

## set ENV variables
os.environ["OPENAI_API_BASE"] = os.getenv("INTERWEB_HOST", "https://interweb.l3s.uni-hannover.de/v1")
os.environ["OPENAI_API_KEY"] = os.getenv("INTERWEB_APIKEY")

llm = ChatOpenAI(model="llama3.1:8b") # gpt-4o-mini

messages = [
    ("system", "You are a helpful assistant that translates English to French. Translate the user sentence."),
    ("human", "I love programming."),
]
ai_msg = llm.invoke(messages)

print(ai_msg)
