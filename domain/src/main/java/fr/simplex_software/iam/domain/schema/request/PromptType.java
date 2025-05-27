package fr.simplex_software.iam.domain.schema.request;

public enum PromptType
{
  NONE("none"),
  LOGIN("login"),
  CONSENT("consent"),
  SELECT_ACCOUNT("select_account");

  private String value;

  PromptType(String value)
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
