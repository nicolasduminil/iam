package fr.simplex_software.iam.domain.schema.data;

public class AuthData
{
  private String authRequest;
  private boolean showAuthRequest;
  private String formattedAuthRequest;
  private String authCode;

  public AuthData()
  {
  }

  public AuthData(String authenticationRequest, boolean showAuthRequest, String formattedAuthRequest, String authCode)
  {
    this.authRequest = authenticationRequest;
    this.showAuthRequest = showAuthRequest;
    this.formattedAuthRequest = formattedAuthRequest;
    this.authCode = authCode;
  }

  public String getAuthRequest()
  {
    return authRequest;
  }

  public void setAuthRequest(String authRequest)
  {
    this.authRequest = authRequest;
  }

  public boolean isShowAuthRequest()
  {
    return showAuthRequest;
  }

  public void setShowAuthRequest(boolean showAuthRequest)
  {
    this.showAuthRequest = showAuthRequest;
  }

  public String getFormattedAuthRequest()
  {
    return formattedAuthRequest;
  }

  public void setFormattedAuthRequest(String formattedAuthRequest)
  {
    this.formattedAuthRequest = formattedAuthRequest;
  }

  public String getAuthCode()
  {
    return authCode;
  }

  public void setAuthCode(String authCode)
  {
    this.authCode = authCode;
  }

  public void reset()
  {
    this.authRequest = null;
    this.showAuthRequest = false;
    this.formattedAuthRequest = null;
    this.authCode = null;
  }
}
