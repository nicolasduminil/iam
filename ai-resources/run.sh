docker run -d -v ai-resources_ollama_models:/root/.ollama -p 11434:11434 --name ollama-temp ollama/ollama
docker exec ollama-temp ollama pull llama3.2
docker exec -ti ollama-temp ollama list
docker stop ollama-temp && docker rm ollama-temp
echo "All volumes BEFORE maven:"
docker volume ls
mvn install
echo "All volumes AFTER maven:"
docker volume ls
echo "Inspecting ollama container volume mounts:"
docker inspect ollama | grep -A 10 "Mounts"
echo "Checking ollama_models volume:"
docker run --rm -v ai-resources_ollama_models:/root/.ollama alpine ls -la /root/.ollama/models
docker exec -ti ollama ollama list