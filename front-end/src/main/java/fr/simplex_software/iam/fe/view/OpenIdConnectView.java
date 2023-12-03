package fr.simplex_software.iam.fe.view;

import fr.simplex_software.iam.fe.service.*;
import jakarta.faces.application.*;
import jakarta.faces.context.*;
import jakarta.faces.view.*;
import jakarta.inject.*;
import jakarta.json.*;
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
    return metaData != null;
  }

  public void getDiscoveryMetadata()
  {
    final String fmt = "issuer: %s%nauthorization_endpoint: %s%ntoken_endpoint: %s%nuserinfo_endpoint: %s";
    JsonObject jsonObject = Json.createReader(new StringReader(iamServiceClient.loadDiscoveryMetadata().readEntity(String.class))).readObject();
    /*String issuer = jsonObject.getString("issuer");
    String authorizationEndpoint = jsonObject.getString("authorization_endpoint");
    String tokenEndpoint = jsonObject.getString("token_endpoint");
    String userInfoEndpoint = jsonObject.getString("userinfo_endpoint");
    new StringWriter().write(*/
    metaData = String.format(fmt, jsonObject.getString("issuer"), jsonObject.getString("authorization_endpoint"), jsonObject.getString("token_endpoint"), jsonObject.getString("userinfo_endpoint"));
  }

  public String getMetaData()
  {
    return metaData;
  }
}
