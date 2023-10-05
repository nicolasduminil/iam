package fr.simplex_software.iam.common.api;

import fr.simplex_software.iam.domain.schema.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.faulttolerance.*;
import org.eclipse.microprofile.metrics.*;
import org.eclipse.microprofile.metrics.annotation.*;
import org.eclipse.microprofile.openapi.annotations.*;
import org.eclipse.microprofile.openapi.annotations.media.*;
import org.eclipse.microprofile.openapi.annotations.parameters.*;
import org.eclipse.microprofile.openapi.annotations.responses.*;

@Path("/api/be")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface BackEndAPI
{
  @Operation(summary = "Load discovery metadata", description = "Display the OpenId Connect Metadata")
  @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = DiscoveryMetadata.class)))
  @Counted(name = "countLoadDiscoveryMetadata", description = "Counts how many times the loadDiscoveryMetadata method has been invoked")
  @Timed(name = "timeLoadDiscoveryMetadata", description = "Times how long it takes to invoke the loadDiscoveryMetadata method", unit = MetricUnits.MILLISECONDS)
  @Timeout(250)
  @GET
  Response loadDiscoveryMetadata();
  @Operation(summary = "Send authentication request", description = "Send an authentication request")
  @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = String.class)))
  @Counted(name = "countSendAuthenticationRequest", description = "Counts how many times the sendAuthenticationRequest method has been invoked")
  @Timed(name = "timeSendAuthenticationRequest", description = "Times how long it takes to invoke the sendAuthenticationRequest method", unit = MetricUnits.MILLISECONDS)
  @Timeout(250)
  @GET
  Response sendAuthenticationRequest (@Parameter(description = "the authentication request", schema = @Schema(implementation = AuthenticationInput.class, required = true)) AuthenticationInput authenticationInput);
  @Operation(summary = "Send token request", description = "Send a token request")
  @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = TokenResponse.class)))
  @Counted(name = "countSendTokenRequest", description = "Counts how many times the sendTokenRequest method has been invoked")
  @Timed(name = "timeSendTokenRequest", description = "Times how long it takes to invoke the sendTokenRequest method", unit = MetricUnits.MILLISECONDS)
  @Timeout(250)
  @GET
  Response sendTokenRequest (@Parameter(description = "the send token request", schema = @Schema(implementation = TokenRequest.class, required = true)) TokenRequest tokenRequest);
  @Operation(summary = "Send refresh request", description = "Send a refresh request")
  @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = TokenResponse.class)))
  @Counted(name = "countSendRefreshRequest", description = "Counts how many times the sendRefreshRequest method has been invoked")
  @Timed(name = "timeSendRefreshRequest", description = "Times how long it takes to invoke the sendRefreshRequest method", unit = MetricUnits.MILLISECONDS)
  @Timeout(250)
  @GET
  Response sendRefreshRequest (@Parameter(description = "the authentication request", schema = @Schema(implementation = TokenRequest.class, required = true)) TokenRequest tokenRequest);
  @Operation(summary = "Send user info request", description = "Send an user info request")
  @APIResponse(responseCode = "200", content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserInfoResponse.class)))
  @Counted(name = "countSendUserInfoRequest", description = "Counts how many times the sendUserInfoRequest method has been invoked")
  @Timed(name = "timeSendUserInfoRequest", description = "Times how long it takes to invoke the sendUserInfoRequest method", unit = MetricUnits.MILLISECONDS)
  @Timeout(250)
  @GET
  Response sendUserInfoRequest ();
}
