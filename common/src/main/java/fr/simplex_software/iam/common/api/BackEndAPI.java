package fr.simplex_software.iam.common.api;

import fr.simplex_software.iam.domain.schema.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.*;
import org.eclipse.microprofile.openapi.annotations.media.*;
import org.eclipse.microprofile.openapi.annotations.parameters.*;
import org.eclipse.microprofile.openapi.annotations.responses.*;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BackEndAPI
{
  @Operation(summary = "Load discovery metadata", description = "Display the OpenId Connect Metadata")
  @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = DiscoveryMetadata.class)))
  @GET
  @Path("load")
  Response loadDiscoveryMetadata();
  @Operation(summary = "Send authentication request", description = "Send an authentication request")
  @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = String.class)))
  @POST
  @Path("auth")
  Response sendAuthenticationRequest (@Parameter(description = "the authentication request", schema = @Schema(implementation = AuthenticationInput.class, required = true)) AuthenticationInput authenticationInput);
  @Operation(summary = "Send token request", description = "Send a token request")
  @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = TokenResponse.class)))
  @POST
  @Path("token")
  Response sendTokenRequest (@Parameter(description = "the send token request", schema = @Schema(implementation = TokenRequest.class, required = true)) TokenRequest tokenRequest);
  @Operation(summary = "Send refresh request", description = "Send a refresh request")
  @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = TokenResponse.class)))
  @POST
  @Path("refresh")
  Response sendRefreshRequest (@Parameter(description = "the authentication request", schema = @Schema(implementation = TokenRequest.class, required = true)) TokenRequest tokenRequest);
  @Operation(summary = "Send user info request", description = "Send an user info request")
  @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserInfoResponse.class)))
  @GET
  @Path("user")
  Response sendUserInfoRequest();
}
