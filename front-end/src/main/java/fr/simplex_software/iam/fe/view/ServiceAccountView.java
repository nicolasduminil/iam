package fr.simplex_software.iam.fe.view;

import fr.simplex_software.iam.common.api.*;
import fr.simplex_software.iam.common.api.exceptions.*;
import fr.simplex_software.iam.domain.schema.request.*;
import jakarta.annotation.*;
import jakarta.faces.context.*;
import jakarta.inject.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.config.inject.*;

import java.nio.charset.*;
import java.util.*;

@Named
public class ServiceAccountView extends AbstractCommonView<ServiceAccountTokenRequest>
{
  @ConfigProperty(name = "FE_SAC_SECRET")
  protected String clientSecret;
  @ConfigProperty(name = "iam-frontend.sac.sandbox-redirect")
  String sandBoxRedirect;
  private ServiceAccountTokenRequest serviceAccountTokenRequest = new ServiceAccountTokenRequest();

  public ServiceAccountTokenRequest getServiceAccountTokenRequest()
  {
    return serviceAccountTokenRequest;
  }

  @Override
  protected ServiceAccountTokenRequest createTokenRequest()
  {
    return getServiceAccountTokenRequest();
  }

  @Override
  protected String[] extractTokenData(Map tokenMap) throws InvalidTokenException
  {
    getTokenData().setaccessToken((String)tokenMap.get("access_token"));
    if (getTokenData().getAccessToken() == null)
      throw new InvalidTokenException("The access token is null");
    return getTokenData().getAccessToken().split("\\.");
  }

  @Override
  protected String getSandBoxRedirect()
  {
    return sandBoxRedirect;
  }

  @Override
  protected String getClientSecret()
  {
    return clientSecret;
  }
}
