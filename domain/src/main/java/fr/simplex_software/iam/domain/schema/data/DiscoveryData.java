package fr.simplex_software.iam.domain.schema.data;

import java.util.*;

public class DiscoveryData
{
  private Map<String, Object> discovery;
  private String discoveryJson;
  private boolean showDiscoveryJson;

  public DiscoveryData()
  {
  }

  public DiscoveryData(Map<String, Object> discovery, String discoveryJson, boolean showDiscoveryJson)
  {
    this.discovery = discovery;
    this.discoveryJson = discoveryJson;
    this.showDiscoveryJson = showDiscoveryJson;
  }

  public Map<String, Object> getDiscovery()
  {
    return discovery;
  }

  public void setDiscovery(Map<String, Object> discovery)
  {
    this.discovery = discovery;
  }

  public String getDiscoveryJson()
  {
    return discoveryJson;
  }

  public void setDiscoveryJson(String discoveryJson)
  {
    this.discoveryJson = discoveryJson;
  }

  public boolean isShowDiscoveryJson()
  {
    return showDiscoveryJson;
  }

  public void setShowDiscoveryJson(boolean showDiscoveryJson)
  {
    this.showDiscoveryJson = showDiscoveryJson;
  }

  public void reset()
  {
    discovery = null;
    discoveryJson = null;
    showDiscoveryJson = false;
  }
}
