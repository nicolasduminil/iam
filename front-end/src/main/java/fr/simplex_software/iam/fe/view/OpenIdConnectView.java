package fr.simplex_software.iam.fe.view;

import fr.simplex_software.iam.domain.schema.*;
import fr.simplex_software.iam.fe.service.*;
import jakarta.enterprise.context.*;
import jakarta.inject.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.rest.client.inject.*;

@Named
@RequestScoped
public class OpenIdConnectView
{
  @RestClient
  private IamServiceClient iamServiceClient;
  private String issuerUrl;

  public String getIssuerUrl()
  {
    return issuerUrl;
  }

  public void setIssuerUrl(String issuerUrl)
  {
    this.issuerUrl = issuerUrl;
  }

  public boolean isMetadata()
  {
    return getDiscoveryMetadata() != null;
  }

  public DiscoveryMetadata getDiscoveryMetadata()
  {
    try (DiscoveryMetadata discoveryMetadata = iamServiceClient.loadDiscoveryMetadata().readEntity(DiscoveryMetadata.class))
    {
      return discoveryMetadata;
    }
  }
}
