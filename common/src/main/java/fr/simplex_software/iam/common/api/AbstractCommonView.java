package fr.simplex_software.iam.common.api;

import fr.simplex_software.iam.common.api.exceptions.*;
import fr.simplex_software.iam.domain.schema.data.*;
import fr.simplex_software.iam.domain.schema.request.*;
import jakarta.annotation.*;
import jakarta.enterprise.context.*;
import jakarta.faces.context.*;
import jakarta.inject.*;
import jakarta.ws.rs.client.*;
import jakarta.ws.rs.core.*;
import org.apache.commons.lang3.*;
import org.eclipse.microprofile.config.inject.*;

import java.nio.charset.*;
import java.util.*;

@ApplicationScoped
public abstract class AbstractCommonView<T extends OAuth20Request>
{
  @Inject
  ClientManager clientManager;
  @Inject
  Util util;
  @ConfigProperty(name = "quarkus.oidc.auth-server-url")
  String issuer;
  @ConfigProperty(name = "keycloak.discovery.endpoint")
  String discoveryEndpoint;
  @ConfigProperty(name = "keycloak.realm")
  String realm;
  @ConfigProperty(name = "iam-backend.secured.service.url")
  String securedServiceUrl;
  @ConfigProperty(name = "iam-backend.public.service.url")
  String publicServiceUrl;
  private DiscoveryData discoveryData = new DiscoveryData();
  private AuthData authData = new AuthData();
  private TokenData tokenData = new TokenData();
  private UserInfoData userInfoData = new UserInfoData();
  private ServiceData serviceData = new ServiceData();
  private T tokenRequest;

  protected abstract T createTokenRequest();
  protected abstract String[] extractTokenData(Map<String, Object> tokenMap) throws InvalidTokenException;
  protected abstract String getSandBoxRedirect();
  protected abstract String getClientSecret();

  @PostConstruct
  public void postConstruct()
  {
    discoveryData.setDiscovery(clientManager.getClient()
      .target(issuer + discoveryEndpoint)
      .request(MediaType.APPLICATION_JSON)
      .get(new GenericType<>() {}));
  }

  public ClientManager getClientManager()
  {
    return clientManager;
  }

  public Util getUtil()
  {
    return util;
  }

  public String getIssuer()
  {
    return issuer;
  }

  public String getDiscoveryEndpoint()
  {
    return discoveryEndpoint;
  }

  public String getRealm()
  {
    return realm;
  }

  public String getSecuredServiceUrl()
  {
    return securedServiceUrl;
  }

  public String getPublicServiceUrl()
  {
    return publicServiceUrl;
  }

  public DiscoveryData getDiscoveryData()
  {
    return discoveryData;
  }

  public void setClientManager(ClientManager clientManager)
  {
    this.clientManager = clientManager;
  }

  public void setDiscoveryData(DiscoveryData discoveryData)
  {
    this.discoveryData = discoveryData;
  }

  public AuthData getAuthData()
  {
    return authData;
  }

  public void setAuthData(AuthData authData)
  {
    this.authData = authData;
  }

  public TokenData getTokenData()
  {
    return tokenData;
  }

  public void setTokenData(TokenData tokenData)
  {
    this.tokenData = tokenData;
  }

  public UserInfoData getUserInfoData()
  {
    return userInfoData;
  }

  public void setUserInfoData(UserInfoData userInfoData)
  {
    this.userInfoData = userInfoData;
  }

  public ServiceData getServiceData()
  {
    return serviceData;
  }

  public void setServiceData(ServiceData serviceData)
  {
    this.serviceData = serviceData;
  }

  /*public T getTokenRequest()
  {
    return tokenRequest;
  }*/

  public void loadDiscovery()
  {
    if (StringUtils.isEmpty(discoveryData.getDiscoveryJson()))
    {
      discoveryData.setDiscoveryJson(util.prettyPrintJsonB(discoveryData.getDiscovery()));
      discoveryData.setShowDiscoveryJson(true);
    }
    else
      discoveryData.setShowDiscoveryJson(!discoveryData.isShowDiscoveryJson());
  }

  public void sendTokenRequest() throws InvalidTokenException
  {
    try (Response response = actionGetTokenResponse())
    {
      Map<String, Object> map = response.readEntity(new GenericType<>() {});
      String[] tokenParts = extractTokenData(map);
      getTokenData().setHeaderJson(getUtil().prettyPrintJsonB(new String(Base64.getUrlDecoder()
        .decode(tokenParts[0]), StandardCharsets.UTF_8)));
      getTokenData().setPayloadJson(getUtil().prettyPrintJsonB(new String(Base64.getUrlDecoder()
        .decode(tokenParts[1]), StandardCharsets.UTF_8)));
      getTokenData().setIdTokenSignature(tokenParts[2]);
      getTokenData().setTokenResponse(getUtil().prettyPrintJsonB(getUtil().truncateTokens(map)));
    }
    catch (InvalidTokenException e)
    {
      getUtil().facesErrorMessage(e.getMessage());
    }
  }

  public void reset() throws Exception
  {
    getDiscoveryData().reset();
    getAuthData().reset();
    getTokenData().reset();
    getUserInfoData().reset();
    getUserInfoData().reset();
    ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
    externalContext.redirect(UriBuilder
      .fromPath(externalContext.getRequestContextPath() + "/" + getSandBoxRedirect())
      .queryParam("activeIndex", "0").build().toString());
  }

  public void invokePublicService()
  {
    getServiceData().setPublicServiceResponse(getClientManager().getClient()
      .target(getPublicServiceUrl()).request().get(String.class));
  }

  public void invokeSecuredService()
  {
    try
    {
      getServiceData().setSecuredServiceResponse(getClientManager().getClient().target(getSecuredServiceUrl())
        .request().header("Authorization", "Bearer " + getTokenData().getAccessToken()).get(String.class));
    }
    catch (Exception e)
    {
      getUtil().facesErrorMessage("Secured service response: %s".formatted(e.getMessage()));
    }
  }

  private Response actionGetTokenResponse()
  {
    tokenRequest = createTokenRequest();
    tokenRequest.setClientSecret(getClientSecret());
    String tokenEndpoint = (String) getDiscoveryData().getDiscovery().get("token_endpoint");
    getTokenData().setFormattedTokenRequest(getUtil()
      .formatRequest(tokenRequest.buildTokenUri(tokenEndpoint)));
    return getClientManager().getClient().target(tokenEndpoint)
      .request(MediaType.APPLICATION_JSON)
      .post(Entity.form(tokenRequest.toForm()));
  }
}
