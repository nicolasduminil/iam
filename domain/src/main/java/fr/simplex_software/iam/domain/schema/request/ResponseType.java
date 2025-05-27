package fr.simplex_software.iam.domain.schema.request;

public enum ResponseType
{
  CODE("code"),
  TOKEN("token"),
  ID_TOKEN("id_token"),
  NONE("none"),
  CODE_ID_TOKEN("code id_token"),
  CODE_TOKEN("code token"),
  ID_TOKEN_TOKEN("id_token token"),
  CODE_ID_TOKEN_TOKEN("code id_token token");

  private final String value;

  ResponseType(String value)
  {
    this.value = value;
  }

  public String getValue()
  {
    return value;
  }

  @Override
  public String toString()
  {
    return value;
  }
}
