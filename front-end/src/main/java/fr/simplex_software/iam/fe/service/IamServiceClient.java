package fr.simplex_software.iam.fe.service;

import fr.simplex_software.iam.common.api.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import org.eclipse.microprofile.rest.client.inject.*;

@RegisterRestClient(configKey = "iam_client")
@Path("/api/be")
public interface IamServiceClient extends BackEndAPI {}
