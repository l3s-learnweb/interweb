import os
from llama_index.embeddings.openai import OpenAIEmbedding

os.environ["OPENAI_API_BASE"] = os.getenv("INTERWEB_HOST", "https://interweb.l3s.uni-hannover.de/v1")
os.environ["OPENAI_API_KEY"] = os.getenv("INTERWEB_APIKEY")

embed_model = OpenAIEmbedding(
    model_name="bge-m3:567m"
)

embeddings = embed_model.get_text_embedding(
    "Open AI new Embeddings models is great. Lol, Ollama is opensource, and free to use."
)

print(embeddings)
