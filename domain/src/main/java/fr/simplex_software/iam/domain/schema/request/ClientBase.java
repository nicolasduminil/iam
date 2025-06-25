package fr.simplex_software.iam.domain.schema.request;

import java.io.*;
import java.util.*;

public class ClientBase implements Serializable
{
  private String clientId;
  private GrantType grantType;
  private List<String> scopes = new ArrayList<>(List.of("profile", "email"));

  public ClientBase()
  {
  }

  public ClientBase(GrantType grantType)
  {
    this.grantType = grantType;
  }

  public ClientBase(String clientId, List<String> scopes)
  {
    this.clientId = clientId;
    this.scopes = scopes;
  }

  public ClientBase(String clientId, GrantType grantType, List<String> scopes)
  {
    this (clientId, scopes);
    this.grantType = grantType;
  }

  public ClientBase(String clientId, GrantType grantType)
  {
    this (clientId, grantType, List.of("profile", "email"));
  }

  public String getClientId()
  {
    return clientId;
  }

  public void setClientId(String clientId)
  {
    this.clientId = clientId;
  }

  public List<String> getScopes()
  {
    return scopes;
  }

  public void setScopes(List<String> scopes)
  {
    this.scopes = new ArrayList<>(scopes);
  }

  public GrantType getGrantType()
  {
    return grantType;
  }

  public void setGrantType(GrantType grantType)
  {
    this.grantType = grantType;
  }
}
