package fr.simplex_software.iam.fe.view;

import fr.simplex_software.iam.common.api.*;
import fr.simplex_software.iam.common.api.exceptions.*;
import fr.simplex_software.iam.domain.schema.request.*;
import fr.simplex_software.iam.fe.service.*;
import jakarta.faces.context.*;
import jakarta.inject.*;
import jakarta.servlet.http.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.apache.commons.lang3.*;
import org.eclipse.microprofile.config.inject.*;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.util.*;

@Named
public class AuthorizationCodeView extends AbstractCommonView<AuthorizationCodeTokenRequest>
{
  @Inject
  AuthorizationCodeRedirectCallbackService authorizationCodeRedirectCallbackService;
  @ConfigProperty(name = "iam-frontend.sandbox-redirect")
  String sandBoxRedirect;
  @ConfigProperty(name = "FE_ACC_SECRET")
  protected String clientSecret;
  public String issuer;

  private AuthorizationCodeLoginRequest authorizationCodeLoginRequest = new AuthorizationCodeLoginRequest();

  @Override
  protected AuthorizationCodeTokenRequest createTokenRequest()
  {
    return new AuthorizationCodeTokenRequest(authorizationCodeLoginRequest, getAuthCode(), clientSecret);
  }

  @Override
  protected String[] extractTokenData(Map<String, Object> tokenMap) throws InvalidTokenException
  {
    getTokenData().setIdToken((String) tokenMap.get("id_token"));
    if (getTokenData().getIdToken() == null)
      throw new InvalidTokenException("id_token is null");
    getTokenData().setaccessToken((String) tokenMap.get("access_token"));
    getTokenData().setRefreshToken((String) tokenMap.get("refresh_token"));
    return getTokenData().getIdToken().split("\\.");
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

  public AuthorizationCodeLoginRequest getAuthRequest()
  {
    return authorizationCodeLoginRequest;
  }

  public void generateAuthRequest()
  {
    if (getDiscoveryData().getDiscovery() == null || getDiscoveryData().getDiscovery().isEmpty())
    {
      getUtil().facesErrorMessage("Discovery document not loaded");
      getAuthData().setShowAuthRequest(false);
    }
    else if (StringUtils.isEmpty(getAuthData().getAuthRequest()))
    {
      String authEndpoint = (String) getDiscoveryData().getDiscovery().get("authorization_endpoint");
      try
      {
        String redirectUri = getUtil().getRedirectUri(getRealm(), authorizationCodeLoginRequest.getClientId());
        authorizationCodeLoginRequest.setRedirectUri(redirectUri);
        URI authUri = authorizationCodeLoginRequest.buildAuthUri(authEndpoint);
        getAuthData().setAuthRequest(authUri.toString());
        getAuthData().setFormattedAuthRequest(getUtil().formatRequest(authUri));
        getAuthData().setShowAuthRequest(true);
      }
      catch (NoSuchClientException ex)
      {
        getUtil().facesErrorMessage(ex.getMessage());
        getAuthData().setShowAuthRequest(false);
      }
    }
    else
      getAuthData().setShowAuthRequest(!getAuthData().isShowAuthRequest());
  }

  public void sendAuthRequest() throws IOException
  {
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
    try
    {
      if (!getAuthData().getAuthRequest().contains("?"))
        getAuthData().setAuthRequest(getAuthData().getAuthRequest() + "?");
      else if (!getAuthData().getAuthRequest().endsWith("&"))
        getAuthData().setAuthRequest(getAuthData().getAuthRequest() + "&");
      getAuthData().setAuthRequest(getAuthData().getAuthRequest()
        + "error_page=" + URLEncoder.encode("/login-error.xhtml", "UTF-8"));
      externalContext.redirect(getAuthData().getAuthRequest());
    }
    catch (Exception e)
    {
      externalContext.redirect(request.getContextPath() + "/system-error.xhtml");
    }
  }

  public void sendRefreshRequest()
  {
    RefreshRequest refreshRequest = new RefreshRequest(authorizationCodeLoginRequest.getClientId(),
      authorizationCodeLoginRequest.getScopes(), getTokenData().getRefreshToken());
    refreshRequest.setClientSecret(clientSecret);
    String tokenEndpoint = (String) getDiscoveryData().getDiscovery().get("token_endpoint");
    getTokenData().setFormattedRefreshRequest(getUtil().formatRequest(refreshRequest.buildTokenUri(tokenEndpoint)));
    try (Response response = getClientManager().getClient().target(tokenEndpoint)
      .request(MediaType.APPLICATION_JSON)
      .post(Entity.form(refreshRequest.toForm())))
    {
      Map<String, Object> map = response.readEntity(new GenericType<>() {});
      getTokenData().setIdToken((String) map.get("id_token"));
      getTokenData().setRefreshToken((String) map.get("refresh_token"));
      getTokenData().setRefreshResponse(getUtil().prettyPrintJsonB(getUtil().truncateTokens(map)));
      String[] idTokenParts = getTokenData().getIdToken().split("\\.");
      getTokenData().setRefreshPayloadJson(getUtil().prettyPrintJsonB(new String(Base64.getUrlDecoder()
        .decode(idTokenParts[1]), StandardCharsets.UTF_8)));
    }
  }

  public void sendUserInfoRequest()
  {
    String userInfoEndpoint = (String) getDiscoveryData().getDiscovery().get("userinfo_endpoint");
    getUserInfoData().setFormattedUserInfoRequest(getUtil().formatRequest(URI.create(userInfoEndpoint)));
    getUserInfoData().setUserInfoResponse(getUtil().prettyPrintJsonB(getClientManager().getClient().target(userInfoEndpoint)
      .request(MediaType.APPLICATION_JSON)
      .header("Authorization", "Bearer " + getTokenData().getAccessToken())
      .get(String.class)));
  }

  public String getAuthCode()
  {
    if (getAuthData().getAuthCode() == null && getAuthData().isShowAuthRequest())
      getAuthData().setAuthCode(authorizationCodeRedirectCallbackService.getAuthCode());
    return getAuthData().getAuthCode();
  }
}
