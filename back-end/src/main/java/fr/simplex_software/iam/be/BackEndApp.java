package fr.simplex_software.iam.be;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.openapi.annotations.*;
import org.eclipse.microprofile.openapi.annotations.info.*;
import org.eclipse.microprofile.openapi.annotations.tags.*;

@ApplicationPath("/")
@OpenAPIDefinition(
  info = @Info(title = "Keycloak API",
    description = "This API allows to demonstrate Keycloak most essential features",
    version = "1.0-SNAPSHOT",
    contact = @Contact(name = "Nicolas DUMINIL", url = "http://www.simplex-software.fr")),
  externalDocs = @ExternalDocumentation(url = "https://github.com/nicolasduminil/iam", description = "Keycloak sandbox"),
  tags = {
    @Tag(name = "api", description = "API to demonstrate Keycloak essential features in a sandbox"),
    @Tag(name = "backend", description = "This is the backend layer")
  }
)
public class BackEndApp extends Application
{
}
