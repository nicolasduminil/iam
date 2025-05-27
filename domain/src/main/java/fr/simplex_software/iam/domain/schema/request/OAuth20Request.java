package fr.simplex_software.iam.domain.schema.request;

import jakarta.ws.rs.core.*;

import java.net.*;

public interface OAuth20Request
{
  URI buildTokenUri(String tokenEndpoint);
  Form toForm();
  void setClientSecret(String clientSecret);
}
