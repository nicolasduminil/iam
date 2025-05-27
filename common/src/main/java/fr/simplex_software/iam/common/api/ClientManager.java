package fr.simplex_software.iam.common.api;

import jakarta.annotation.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.ws.rs.client.*;

@Named
@ApplicationScoped
public class ClientManager
{
  private Client client;

  @PostConstruct
  public void init()
  {
    client = ClientBuilder.newClient();
  }

  @PreDestroy
  public void cleanup()
  {
    if (client != null)
    {
      client.close();
    }
  }

  public Client getClient()
  {
    return client;
  }
}
