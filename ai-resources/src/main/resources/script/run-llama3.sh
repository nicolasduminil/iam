docker run -d -v ollama_models:/root/.ollama -p 11434:11434 --name ollama-temp ollama/ollama
docker exec ollama-temp ollama pull llama3.2
docker stop ollama-temp && docker rm ollama-temp
#docker run -d -v ollama_models:/root/.ollama -p 11434:11434 --name ollama ollama/ollama