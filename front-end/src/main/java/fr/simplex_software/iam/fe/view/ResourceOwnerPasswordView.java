package fr.simplex_software.iam.fe.view;

import fr.simplex_software.iam.common.api.*;
import fr.simplex_software.iam.common.api.exceptions.*;
import fr.simplex_software.iam.domain.schema.request.*;
import jakarta.inject.*;
import org.eclipse.microprofile.config.inject.*;

import java.util.*;

@Named
public class ResourceOwnerPasswordView extends AbstractCommonView<ResourceOwnerPasswordTokenRequest>
{
  @ConfigProperty(name = "FE_ROPC_SECRET")
  protected String clientSecret;
  @ConfigProperty(name = "iam-frontend.ropc.sandbox-redirect")
  String sandBoxRedirect;

  private ResourceOwnerPasswordTokenRequest resourceOwnerPasswordTokenRequest = new ResourceOwnerPasswordTokenRequest();

  public ResourceOwnerPasswordTokenRequest getResourceOwnerPasswordTokenRequest()
  {
    return resourceOwnerPasswordTokenRequest;
  }

  @Override
  protected ResourceOwnerPasswordTokenRequest createTokenRequest()
  {
    return getResourceOwnerPasswordTokenRequest();
  }

  @Override
  protected String[] extractTokenData(Map<String, Object> tokenMap) throws InvalidTokenException
  {
    getTokenData().setaccessToken((String) tokenMap.get("access_token"));
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
