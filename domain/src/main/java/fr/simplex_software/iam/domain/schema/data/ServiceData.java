package fr.simplex_software.iam.domain.schema.data;

public class ServiceData
{
  private String publicServiceResponse;
  private String securedServiceResponse;

  public ServiceData()
  {
  }

  public ServiceData(String publicServiceResponse, String securedServiceResponse)
  {
    this.publicServiceResponse = publicServiceResponse;
    this.securedServiceResponse = securedServiceResponse;
  }

  public String getPublicServiceResponse()
  {
    return publicServiceResponse;
  }

  public void setPublicServiceResponse(String publicServiceResponse)
  {
    this.publicServiceResponse = publicServiceResponse;
  }

  public String getSecuredServiceResponse()
  {
    return securedServiceResponse;
  }

  public void setSecuredServiceResponse(String securedServiceResponse)
  {
    this.securedServiceResponse = securedServiceResponse;
  }

  public void reset()
  {
    publicServiceResponse = null;
    securedServiceResponse = null;
  }
}
