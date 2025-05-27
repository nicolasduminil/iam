package fr.simplex_software.iam.domain.schema.data;

public class TokenData
{
  private String idToken;
  private String tokenResponse;
  private String headerJson;
  private String payloadJson;
  private String idTokenSignature;
  private String formattedRefreshRequest;
  private String refreshResponse;
  private String refreshPayloadJson;
  private String formattedPayloadJson;
  private String formattedTokenRequest;
  private String accessToken;
  private String refreshToken;

  public TokenData()
  {
  }

  public TokenData(String idToken, String tokenResponse, String headerJson, String payloadJson, String idTokenSignature, String formattedRefreshRequest, String refreshResponse, String refreshPayloadJson, String formattedPayloadJson, String formattedTokenRequest, String accessToken, String refreshToken)
  {
    this.idToken = idToken;
    this.tokenResponse = tokenResponse;
    this.headerJson = headerJson;
    this.payloadJson = payloadJson;
    this.idTokenSignature = idTokenSignature;
    this.formattedRefreshRequest = formattedRefreshRequest;
    this.refreshResponse = refreshResponse;
    this.refreshPayloadJson = refreshPayloadJson;
    this.formattedPayloadJson = formattedPayloadJson;
    this.formattedTokenRequest = formattedTokenRequest;
    this.accessToken = accessToken;
    this.refreshToken = refreshToken;
  }

  public String getIdToken()
  {
    return idToken;
  }

  public void setIdToken(String idToken)
  {
    this.idToken = idToken;
  }

  public String getTokenResponse()
  {
    return tokenResponse;
  }

  public void setTokenResponse(String tokenResponse)
  {
    this.tokenResponse = tokenResponse;
  }

  public String getHeaderJson()
  {
    return headerJson;
  }

  public void setHeaderJson(String headerJson)
  {
    this.headerJson = headerJson;
  }

  public String getPayloadJson()
  {
    return payloadJson;
  }

  public void setPayloadJson(String payloadJson)
  {
    this.payloadJson = payloadJson;
  }

  public String getIdTokenSignature()
  {
    return idTokenSignature;
  }

  public void setIdTokenSignature(String idTokenSignature)
  {
    this.idTokenSignature = idTokenSignature;
  }

  public String getFormattedRefreshRequest()
  {
    return formattedRefreshRequest;
  }

  public void setFormattedRefreshRequest(String formattedRefreshRequest)
  {
    this.formattedRefreshRequest = formattedRefreshRequest;
  }

  public String getRefreshResponse()
  {
    return refreshResponse;
  }

  public void setRefreshResponse(String refreshResponse)
  {
    this.refreshResponse = refreshResponse;
  }

  public String getRefreshPayloadJson()
  {
    return refreshPayloadJson;
  }

  public void setRefreshPayloadJson(String refreshPayloadJson)
  {
    this.refreshPayloadJson = refreshPayloadJson;
  }

  public String getFormattedPayloadJson()
  {
    return formattedPayloadJson;
  }

  public void setFormattedPayloadJson(String formattedPayloadJson)
  {
    this.formattedPayloadJson = formattedPayloadJson;
  }

  public String getFormattedTokenRequest()
  {
    return formattedTokenRequest;
  }

  public void setFormattedTokenRequest(String formattedTokenRequest)
  {
    this.formattedTokenRequest = formattedTokenRequest;
  }

  public String getAccessToken()
  {
    return accessToken;
  }

  public void setaccessToken(String accessToken)
  {
    this.accessToken = accessToken;
  }

  public String getRefreshToken()
  {
    return refreshToken;
  }

  public void setRefreshToken(String refreshToken)
  {
    this.refreshToken = refreshToken;
  }

  public void reset()
  {
    idToken = null;
    tokenResponse = null;
    headerJson = null;
    payloadJson = null;
    idTokenSignature = null;
    formattedRefreshRequest = null;
    refreshResponse = null;
    refreshPayloadJson = null;
    formattedPayloadJson = null;
    formattedTokenRequest = null;
    accessToken = null;
    refreshToken = null;
  }
}
