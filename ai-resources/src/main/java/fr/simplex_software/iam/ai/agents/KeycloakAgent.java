package fr.simplex_software.iam.ai.agents;

import dev.langchain4j.service.*;
import io.quarkiverse.langchain4j.*;

@RegisterAiService
public interface KeycloakAgent
{
  @SystemMessage("""
        You are a Keycloak analytics assistant. 
        You can answer questions about login activity, failed logins, role assignments, and OAuth2 token usage. 
        When needed, call Java tools to retrieve data.
    """)
  String chat(String userMessage);
}
