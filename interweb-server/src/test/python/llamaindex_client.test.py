import os
from llama_index.embeddings.openai import OpenAIEmbedding
from llama_index.llms.openai_like import OpenAILike
from llama_index.core.llms import ChatMessage, MessageRole

os.environ["OPENAI_API_BASE"] = os.getenv("INTERWEB_HOST", "https://interweb.l3s.uni-hannover.de/v1")
os.environ["OPENAI_API_KEY"] = os.getenv("INTERWEB_APIKEY")

# Create a completion
llm = OpenAILike(model="llama3.1:8b", is_chat_model=True) # gpt-4o-mini

resp = llm.chat([
    ChatMessage(role=MessageRole.SYSTEM, content="Always answer the question, even if the context isn't helpful."),
    ChatMessage(role=MessageRole.USER, content="What is your mission?"),
])
print(resp)

# Get Embeddings
embed_model = OpenAIEmbedding(
    model_name="bge-m3:567m"
)

embeddings = embed_model.get_text_embedding(
    "Open AI new Embeddings models is great. Lol, Ollama is opensource, and free to use."
)

print(embeddings)
