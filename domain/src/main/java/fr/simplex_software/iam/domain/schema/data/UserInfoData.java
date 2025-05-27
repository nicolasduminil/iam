package fr.simplex_software.iam.domain.schema.data;

public class UserInfoData
{
  private String userInfoResponse;
  private String formattedUserInfoRequest;

  public UserInfoData()
  {
  }

  public UserInfoData(String userInfoResponse, String formattedUserInforRequest)
  {
    this.userInfoResponse = userInfoResponse;
    this.formattedUserInfoRequest = formattedUserInforRequest;
  }

  public String getUserInfoResponse()
  {
    return userInfoResponse;
  }

  public void setUserInfoResponse(String userInfoResponse)
  {
    this.userInfoResponse = userInfoResponse;
  }

  public String getFormattedUserInfoRequest()
  {
    return formattedUserInfoRequest;
  }

  public void setFormattedUserInfoRequest(String formattedUserInforRequest)
  {
    this.formattedUserInfoRequest = formattedUserInforRequest;
  }

  public void reset()
  {
    this.userInfoResponse = null;
    this.formattedUserInfoRequest = null;
  }
}
