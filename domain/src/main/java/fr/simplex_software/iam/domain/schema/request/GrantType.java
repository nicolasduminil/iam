package fr.simplex_software.iam.domain.schema.request;

public enum GrantType
{
  AUTHORIZATION_CODE("authorization_code"),
  IMPLICIT("implicit"),
  PASSWORD("password"),
  CLIENT_CREDENTIALS("client_credentials"),
  REFRESH_TOKEN("refresh_token");

  private final String value;

  GrantType(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public String toString() {
    return value;
  }
}
