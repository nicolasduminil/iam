package fr.simplex_software.iam.fe.view;

import fr.simplex_software.iam.fe.service.*;
import jakarta.faces.application.*;
import jakarta.faces.context.*;
import jakarta.faces.view.*;
import jakarta.inject.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.config.inject.*;
import org.eclipse.microprofile.rest.client.inject.*;

import java.io.*;

@Named
@ViewScoped
public class OpenIdConnectView implements Serializable
{
  @RestClient
  private IamServiceClient iamServiceClient;
  @ConfigProperty(name = "keycloak.issuer-uri")
  String issuerUrl;
  private String metaData = null;

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
    System.out.println ("### Calling isMetadata()");
    if (metaData != null)
      System.out.println (">>> metadata is not null");
    else
      System.out.println (">>> metadata is null");
    return metaData != null;
  }

  public void getDiscoveryMetadata()
  {
    System.out.println ("### Before calling loadDiscoveryMetadata()");
    if (metaData != null)
      System.out.println (">>> metadata is not null");
    else
      System.out.println (">>> metadata is null");
    metaData = iamServiceClient.loadDiscoveryMetadata().readEntity(String.class);
    System.out.println ("### After calling loadDiscoveryMetadata()");
    if (metaData != null)
      System.out.println (">>> metadata is not null");
    else
      System.out.println (">>> metadata is null");
  }

  public String getMetaData()
  {
    return metaData;
  }
}
