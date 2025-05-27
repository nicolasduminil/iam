# SaC It Up: Dive Deep into DevSecOps with Java, Quarkus and Keycloak

In the ever advancing world of software industry, code and security often 
feel like antagonistic vectors. But what if security could move at the pace of code ?
Enter *Security as Code* (SaC): the next phase of the DevSecOps evolution. By 
including security rules, expectations and policies directly into the development lifecycle,
SaC shifts security left, turning traditionally manual and repetitive tasks into
automated, version-controlled artifacts. This isn’t just a technical upgrade 
but a cultural change where developers, security teams, and operations speak the
same language: code. 

Whilst software delivery pipelines progressively become automated, making security
an integral part of the code isn't anymore a best practice, but a necessity. From
this perspective, SaC is considered as being the
natural progression of DevSecOps. Addressing security strategies, controls and policies
as development artifacts which could be tested, audited and versioned, just like infrastructure
and application code, not only scales better but also ensures security
is enforced consistently across environments and teams.

In this article, we’ll explore how SaC allows DevOps and DevSecOps teams to build fast, 
to ensure appropriate security and to trustfully scale in modern cloud-native environments
based on Java, Quarkus and Keycloak platforms.

## From DevOps to SaC: a short piece of history

The term of DevOps, as a contraction of "Development" and "Operations", emerged
in the late Y2K as a response to the traditional software development process, 
where development and IT operations were often disconnected. It aimed at breaking
down barriers between development and operations teams such that to favorize faster
deployment cycles, increased collaboration and automation of the software delivery
process through CI/CD platforms.

However, in these early days of DevOps, security was often an afterthought. Traditional
security practices were manual and slow, to such an extent that they became a bottleneck
in the DevOps process and in the whole development lifecycle. Starting with 2012, 
organizations realized that security had to be integrated into the DevOps workflow.
The core idea was to "shift left security", i.e. bringing it earlier in the 
development process. A couple of years later, in 2015, the term of DevSecOps was
coined, standing for "Development, Security and Operations", as it brought security
as a shared responsibility across these three fields.

DevSecOps transformed security, from a separate team post-deployment concern, into
an embedded from start, shared responsibility. IaC (*Infrastructure as Code*) was
one of the most important approaches to inforce DevSecOps practices, by using the
programming languages power in order to automate security startegies, configurations
and policies. And since a continuously increasing part of the IaC was dedicated
to make security repeatable, scalable and version-controlled, this part finished
by becoming a separate discipline in itself and was named *Security as Code* (SaC). 

Today SaC is considered as being the next logical maturity step for organizations
already having embraced DevSecOps. As a matter of fact, if DevSecOps made security
collaborative and brought it in the earliest stages of the development lifecycle, 
SaC makes it reliable, scalable and automated.

## The IAM Service: a must-have for any SaC platform

During the software development evolution from DevOps to DevSecOps and then to SaC,
the requirements around *Identity and Access Management* (IAM) services evolved as well.
Once a secondary concern handled by infrastructure or external providers, IAM services became
gradually first-class citizens of the development lifecycle, driven by the need for automated,
auditable, and scalable security. 

With IaC permitting the automated deployment of infrastructure at scale, managing
who can access what within these systems becomes more complex and more critical.
This is where IAM comes into the picture, providing the foundation for secure, role
based control across cloud-native environments. In this context, IAM has become
an essential service of the infrastructure automation,
ensuring that only the right users, services, and machines have access to the
right resources. 

With the growth of DevSecOps and IaC, development teams have started more and 
more to get into the habit of including security policies and controls earlier 
in the development process. This implied to delegate authentication and authorization
to centralized IAM platforms. By leveraging standards like OpenID Connect (OIDC)
and OAuth 2.0, these platforms allowed developers to offload security logic from
applications while still maintaining strict control over access policies. However,
manually configuring IAM, as it was often the case, used to dramatically limit 
its scalability and its repeatability.

SaC was the critical point at which IAM configurations were no longer manually 
managed but instead treated as code. Among the many IAM available solutions, 
Keycloak, a RedHat open-source identity server, stands out as a powerful platform
designed to provide centralized authentication and authorization for modern 
applications. With tools like Keycloak, teams can now codify realms, clients, 
roles, and access policies, store them in version control, and deploy them automatically
using tools like Terraform, Ansible or Helm. This approach makes IAM a fully 
integrated, testable, and repeatable infrastructure element which guarantees 
consistent and traceable security across environments.

Keycloak’s extensive support for automation makes it an ideal candidate for IaC.
Its CLI, REST API, and declarative configuration import / export feature allow
organizations to enforce zero-trust principles, enable fine-grained access control,
and maintain compliance, by incorporating Keycloak configurations into the broader 
DevSecOps toolchain, without sacrificing development speed. This integration
ensures that identity policies evolve with the application, not as an afterthought
but as a core part of the software lifecycle.

## Introducing Keycloak

Keycloak is a IAM server dedicated to supply identity services to modern applications
such as SPA (*Single Page Application*), mobile applications or REST APIs. Started
at RedHat in 2014 as an open-source project, it has grown little by little into
a well recognized product, with a solid community and a strong user base.

Keycloak supports industry standard protocols like OAuth 2.0, OpenID Connect
and SAML 2.0, this way allowing the developers to disregard the necessity of 
mastering the full complexity of the authentication and authorization process,
by delegating its responsibility to the server, while guaranteeing a high security
level to applications that don't have access to the users' credentials.

It is important to mention also that Keycloak provides a wide range of authentication
mechanisms, including but not limited to MFA (*Multi Factor Authentication*), 
SA (*Strong Authentication*), using OTPs (*One Time Password*), security devices,
WebAuthn passwords or a combination of them all. Thanks to its session management
capabilities, Keycloak is an SSO (*Single Sign On*) service as well, allowing 
users to access several applications, while only having to authenticate once.

As any IAM server, the notion of user is central to Keycloack but, as opposed to
other IAM servers, Keycloak comes with its own user database. For simplicity's 
sake and in order to avoid possible licensing issues, this default database is 
a very simple H2 file-based one that shouldn't be used in production. Instead,
any other production-ready database, like Oracle, PostgreSQL, MySQL, MariaDB, 
etc., may be configured. Additionally, Keycloak provides a strong caching layer
designed to avoid database hits as much as possible. And as the vast majority 
of organizations uses LDAP directories as their single source
of truth for user management and digital identities. Consequently, Keycloak 
supports integration with different LDAP directory implementations like Microsoft
Active Directory, RedHat Directory Server, ApacheDS, OpenLDAP, etc.

## SaC and Keycloak

While Keycloak itself is not SaC tool, the way to manage and deploy it and its 
configurations can absolutely become part of a SaC strategy. Its extensive support
for automation through its CLI, REST API and declarative import/export makes 
it an ideal platform for SaC. Here are the most essential criteria showing how 
Keycloak fits into SaC:

1. Configuration as code.
  - Defining realms, clients, roles, users, identity providers, etc. using the `kadm` scripts and storing these scripts in GIT repositories.
  - Defining realms, clients, roles, users, identity providers, etc. ind JSON or YAML files, storing these files in GIT repositories and importing them using the `kadm` tool. 
  - Exporting Keycloak realm configurations as JSON files, storing them in GIT repositories and re-import them during deployments.
2. Automated deployment.
  - Using OCI (*Open Container Initiative*) compliant images to run Keycloak and dynamically apply configurations described by versioned scripts or JSON/YAML files.
  - Using tools like Terraform, Ansible, Helm or Kubernetes Operators to deploy and configure Keycloak.
3. Programmatic access policies.
  - Using RBAC (*Resource Based Acces Control*) policies as JSR 250 annotations in versioned Java code.
  - Using RBAC (*Resource Based Acces Control*) policies as versioned JSON files.
4. CI/CD integration.
  - Using CI/CD pipelines to automatically test and deploy security artifacts.
  - Making security repeatable, auditable and scalable.

One of the most classical scenario of implementing SaC with Keycloak would probably
include the use of `kadm` versioned scripts to create OAuth 2.0 clients, users, 
groups, roles, authentication flows, authorization policies, etc. and automatically
applying them using OCI complinat images or tools like Terraform, Ansible, Helm
or Kubernetes Operators. However, doing the same thing by manually clicking around
the Keycloak administration console without versioning and documenting these 
one-off changes, would *not* be SaC.

## Running Keycloak

While Keycloak may be installed locally, like any other software, by downloading
and uncompressing it, the easiest way to run it is as an OCI compliant image.

Here is how you can run it as a Docker image:

    $ docker run -d --name keycloak \
        --rm -e KEYCLOAK_ADMIN=admin \
        -e KEYCLOAK_ADMIN_PASSWORD=admin \
        -p 8080:8080 quay.io/keycloak/keycloak:latest start-dev

This command will pull the Docker image `quay.io/keycloak/keycloak:latest` from
the RedHat repository and, if it isn't already present locally, it will store it
there. Then, the Docker daemon will run it in the background (option `-d`), listening 
for HTTP trafic on the container TCP port 8080, mapped on the same TCP port of 
the host (option `-p`). The temporary administrator user name, as well as the 
associated password, are `admin` (options `-e KEYCLAOK_ADMIN` and 
`-e KEYCLOAK_ADMIN_PASSWORD`). Ultimately, the name of the running container 
is `keycloak` (option `--name`) and, when the Docker container is stopped,
the associated image will be removed (option `--rm`).

You can check that everything is working as expected by executing the following
command:

    $ docker images
    REPOSITORY                     TAG               IMAGE ID       CREATED        SIZE
    ...
    quay.io/keycloak/keycloak      latest            152827b20b9e   2 months ago   443MB
    ...

The output above shows that the Docker image `quay.io/keycloak/keycloak:latest`
was pulled and installed locally.

    $ docker ps
    CONTAINER ID   IMAGE                              COMMAND                  CREATED         STATUS         PORTS                                                           NAMES
    ...
    ba8d912aeb1c   quay.io/keycloak/keycloak:latest   "/opt/keycloak/bin/k…"   6 seconds ago   Up 5 seconds   8443/tcp, 0.0.0.0:8080->8080/tcp, :::8080->8080/tcp, 9000/tcp   keycloak
    ...

Here you can see that the Docker container named `keycloak` is up and running.
You can see its log file as shown below:

    $ docker logs keycloak --details --follow 
    Updating the configuration and installing your custom providers, if any. Please wait.
    2025-05-15 16:06:29,366 WARN  [io.qua.config] (build-28) Unrecognized configuration key "quarkus.smallrye-health.extensions.enabled" was provided; it will be ignored; verify that the dependency extension for this configuration is set or that you did not make a typo
    2025-05-15 16:06:30,301 INFO  [io.qua.hib.orm.dep.HibernateOrmProcessor] (build-50) Persistence unit 'keycloak-default': Enforcing Quarkus defaults for dialect 'org.hibernate.dialect.H2Dialect' by automatically setting 'jakarta.persistence.database-product-version=2.3.230'.
    2025-05-15 16:06:30,304 INFO  [io.qua.hib.orm.dep.HibernateOrmProcessor] (build-50) A legacy persistence.xml file is present in the classpath. This file will be used to configure JPA/Hibernate ORM persistence units, and any configuration of the Hibernate ORM extension will be ignored. To ignore persistence.xml files instead, set the configuration property 'quarkus.hibernate-orm.persistence-xml.ignore' to 'true'.
    2025-05-15 16:06:34,954 INFO  [io.qua.dep.QuarkusAugmentor] (main) Quarkus augmentation completed in 6489ms
    Running the server in development mode. DO NOT use this configuration in production.
    ...

Last but not least, firing your prefered browser at http://localhost:8080 and 
connecting as `admin/admin` to the login dialog shown below:

![Keycloak logging](keycloak1.png)

will allow you to get acces to the Keycloak 
administration console. This proves that your IAM server is fully operational.

## Getting started with Keycloak CLI

As already mentioned above, Keycloak comes with an administration console which
allows you to configure and manage the IAM server. But using this administration 
console wouldn't be a SaC compliant approach because, whatever you do with it:

  - isn't repeatable;
  - isn't deterministic;
  - isn't versionable;
  - isn't auditable;
  - isn't documented;
  - is repetitive;
  - is error prone.

So, here is where the Keycloak CLI (*Command Language Interpreter*) comes into he play.

The Keycloak CLI consists in a `bash` script named `kadm.sh`, found in the `bin`
directory of the server. Therefore, you can run it as follows:

    $ export PATH=$PATH:$KEYCLOAK_HOME/bin
    $ kcadm.sh

Using this script, you can do whatever you do by clicking around the administration
console, but in a controled and fully SaC compliant mode. Let's have now a quick 
overview of the most essential Keycloak concepts:

  - **Realms**. A realm is a logical namespace grouping different security artifacts like applications, services, users, groups, roles, etc. They are isolated from one another and can only manage the artifacts that they control.
  - **Clients**. Before applications are able to use Keycloak services, they need to be registered first, as Keycloak clients. They represent basic entities that may request Keycloak authentication and authorization.
  - **Users**. The notion of users is the same as with any other kind of server, i.e. entities able to log with Keycloak. They are stored in the Keycloak internal datrabase or, in the case of the users federation, in external LDAP directories. Users belong to and log into realms.
  - **Groups**. Users can be grouped in user groups. This facilitates the management of their common attributes.
  - **Roles**. Roles are permission types that can be defined at either the realm or the client level. They are assigned to specific users or user groups.
  - **Role mappers**. These Keycloak artifacts are used in order to assign roles, i.e. sets of permissions, to specific users or user groups.

Now that we got a basic understanding of the most important Keycloak artifacts,
let's dive into the writing of `kcdm` scripts that handle them. The first thing 
to do when starting using Keycloak as a security provider is to create a new 
realm. Here are the required steps:

### Configure the temporary admin credentials.

Keycloak comes with an already configured security realm named `master`. As its 
name implies, it the master of realms, the place where the server administrators
create their accounts allowing them to manage any other realm created on the same
server instance. So, it is used by the server itself and, while you can use it to
manage your own realm, this isn't recommended.

When Keycloak is installed on-prem and, hence, an installation and configuration
process has been executed, the server comes with a default administrator in the
`master` realm. The default credentials for this administrator are `admin/admin`.
This isn't the case when Keycloak is run as an OCI compliant image and, in this
case, the first thing to do before getting access to the `master` realm, is to 
set up its temporary credentials. With Keycloak CLI, this can be done using the
following command:

    $ kcadm config credentials --server <server-url> --realm master --user <user-name> --password <user-password>

Of course, this command can only be executed once that the Keyclaok server has 
started. Here `<server-url` is the full URL of the Keycloak server, for example
http://localhost:8080, while the options `--user` and `password` enable you to 
define the user name and, respectivelly, the associated password of the `master`
realm administrator.

### Creating a new realm

Having defined these temporary credentials, you can now create a new realm:

    $ kcadm create realms -s realm=<realm-name> -s enabled=true

Here the `-s` option, for `set`, allows you to set up attributes values. In this
case we're creating a new realm which name is defined by the `-s realm` option 
and, since realm aren't enabled by default, we need to do it using the argument
`-s enabled=true`.

### Creating users

Now is the time to create the Keycloak users. 

    $ kcadm create users -r <realm-name> -s username=<user-name> -s enabled=true -s "emailVerified=true" \
      -s "email=<user-email>" -s "firstName=<user-first-name>" -s "lastName=<user-last-name>"
    $ kcadm set-password -r <realm-name> --username <user-name> --new-password <user-password>

The sequence above creates a new user in the newly created realm and defines the
associated password. To be noted that users have several properties like their 
associated first and last name, as well as their email address. These properties
are initialized on the behalf of `-s "name=value"` options. Also, the boolean 
property `emailVerified` helps to define trusted users, which email address has
been verified after their creation.

### Creating clients

Creating clients is a more complicated operations due to the large number of 
properties and parameters that need to be defined. This is why, in practice,
all these parameters and properties are stored in JSON files that are used as 
input for `kcadm` commands. Here is an example:

    $ kcadm create clients -r <realm-name> -f <input-file>

In this example, `<input-file>` is the full path of a local JSON file containing
the description of the new client that has to be created. We'll come back later
with more details concerning the client types as well as their properties.

### Creating roles and assigning them to users

In order to assingn permission to users, we're using Keycloak roles. These roles
can be assigned, as explained, to users, in which case we're talking about realm
roles, or to clients. Here is an example of creating a new realm role and to 
assign it to an user:

    $ kcadm create roles -r <realm-name> -s name=<role-name>
    $ kcadm add-roles --uusername <user-name> --rolename <role-name> -r <realm-name>

The sequence above creates a new role which name is defined by the option 
`-s name=<role-name>` in the realm which name is defined by the option `-r <realm-name>`.
Then, this role is assigned to the user which name is defined by the option 
`--uusername <user-name>`.

Once we complete these steps above, we can consider having a new realm, quite 
complete, providing all the required artifacts, which should allow us to run 
the sample application, as shown in the next section. Of course, the mentioned
steps don't have to be executed manually, but we'll demonstrate how to automate
them, in the most authentic SaC way, using tools like `docker` and `docker-compose`,
integrated with Quarkus.

## Running the sample application.

In order to illustrate all the concepts introduced above, we provide a sample 
application, available here: .... It's a Java application, using Quarkus, the 
famous supersonic and subatomic stack. It consists of several Maven modules or
subprojects, as follows:

  - The `front-end` Maven module which deploys an SPA (*Single Page Application*) which uses the Jakarta Faces and PrimeFaces extension for Quarkus.
  - The `back-end` Maven module which exposes a simple REST API called by the `front-end` module.
  - The `infra` Maven module which orchestrates the others ones, including the Keycloak server.

Let's look in a greater detail at each one of hese modules.

### The `infra` module.

We start with this module, named `infra`, because, as its name implies, it defines
our overall infrastructure and, in this respect, it is the most important. It is
responsible for running the `front-end` and the `back-end` applications, as well
as the Keycloak server, as Docker images. The `docker-compose.yaml` file in the
`src/main/resources` directory orchestrates this process, as shown below:

    version: "3.7"
    services:
    keycloak:
      image: quay.io/keycloak/keycloak
      hostname: keycloak
      container_name: keycloak
      environment:
        KC_BOOTSTRAP_ADMIN_USERNAME: admin
        KC_BOOTSTRAP_ADMIN_PASSWORD: admin
        KC_HEALTH_ENABLED: "true"
      volumes:
        - ./scripts/customize.sh:/opt/keycloak/customization/customize.sh
        - ./fe-client.json:/opt/keycloak/customization/fe-client.json
        - ./be-client.json:/opt/keycloak/customization/be-client.json
      healthcheck:
        test: [ "CMD", "curl", "--head", "http://localhost:8080/health/ready", "-sf" ]
        interval: 10s
        timeout: 2s
        retries: 15
        command: ["start-dev"]
        network_mode: "host"
    iam-backend:
      image: nicolasduminil/iam-back-end:1.0-SNAPSHOT
      depends_on:
        - keycloak
      container_name: iam-backend
      hostname: iam-backend
      network_mode: "host"
    iam-frontend:
      image: nicolasduminil/iam-front-end:1.0-SNAPSHOT
      depends_on:
        - iam-backend
      container_name: iam-frontend
      hostname: iam-frontend
      network_mode: "host"

Let's try to deconstruct one by one the services described by this file. 

First, the `keycloak` service runs the Docker image `quay.io/keycloak/keycloak`
provided by RedHat in their ecosystem catalog. A couple of environment variables
are defined, especially in order to set up the temporary credentials for the
`master` realm administrator. Then, several volumes are mounted on the container's
`/opt/keycloak` mountpoint. Let's look in detail at the first one, which mounts
the file `scr/main/resources/scripts/customize.sh`:

    #!/bin/bash
    KCADM=/opt/keycloak/bin/kcadm.sh
    $KCADM config credentials --server http://$1 --realm master --user admin --password admin
    $KCADM create realms -s realm=myrealm -s enabled=true
    $KCADM create users -r myrealm -s username=john -s enabled=true -s "emailVerified=true" \
      -s "email=john.doe@emailcom" -s "firstName=John" -s "lastName=Doe"
    $KCADM set-password -r myrealm --username john --new-password password1
    $KCADM create users -r myrealm -s username=jane -s enabled=true -s "emailVerified=true" \
      -s "email=jane.doe@emailcom" -s "firstName=Jane" -s "lastName=Doe"
    $KCADM set-password -r myrealm --username jane --new-password password1
    $KCADM create clients -r myrealm -f /opt/keycloak/customization/fe-client.json
    $KCADM create clients -r myrealm -f /opt/keycloak/customization/be-client.json
    $KCADM create roles -r myrealm -s name=manager
    $KCADM add-roles --uusername john --rolename manager -r myrealm
    $KCADM create roles -r myrealm -s name=employee
    $KCADM add-roles -r myrealm --uusername jane --rolename employee

This is a `bash` script used to customize our Keycloak service by creating a new
security realm, named `myrealm`. This realm contains two users, `john` and `jane`
having the roles of `manager` and, respectively, `employee`. Two clients are 
created as well and their description is provided by the files 
`src/main/resources/fe-client.json` and, respectively, 
`src/main/resources/be-client.json`. These two clients correspond to our two 
applications: `front-end` and `back-end`. We'll come back later with full details
concerning these clients and there JSON definitions but, for now, let's have a 
quick look at the first one:

    {
      "clientId" : "fe",
      "enabled" : true,
      "protocol" : "openid-connect",
      "publicClient" : false,
      "bearerOnly" : false,
      "standardFlowEnabled" : true,
      "implicitFlowEnabled" : false,
      "directAccessGrantsEnabled" : false,
      "serviceAccountsEnabled" : false,
      "redirectUris" : ["http://localhost:8082/callback"],
      "webOrigins" : ["http://localhost:8082"],
      "defaultClientScopes": [
        "openid",
        "profile",
        "email"
      ]
    }

The description above shows a client having the ID `fe`. It is enabled and uses
the Open ID Connect protocol. Our client is not *public* as stated by the property
`"publicClient" : false`. This requires a bit more explanation.

Within an OAuth 2.0 flow, there are two client types: *confidential* and *public*.
Confidential clients are applications such as server-side web ones that
are able to safely store credentials, which they can use to authenticate with 
the authorization server. Typically, these are classical web applications running
on web or application servers. Public clients, on the other hand, are client-side 
applications that are not able to safely store credentials. These are typically
SPAs, mobile applications or browser-based ones. 

Our client is also a *regular* or *interactive* one, as stated by the property
`"bearerOnly": false`. Another classification of the OAuth 2.0 clients, based on
their authentication capabilities, divides them in *regular/interactive* ones, 
who can initiate login flows, or *bearer only*, who can only validate tokens but
cannot initiate logins. In our case, given that our application is a front end 
and, consequently, it is interactive, the property `"bearerOnly": false` states
that the application is able to login on the behalf of Keycloak users.

The next property in the JSON definition file above is `"standardFlowEnabled"`.
The OAuth 2.0 protocol defines so-called *grants* or *flows*. There are several
such flows, which one called *standard flow* or *authorization code flow*. We'll
come back later with more details concerning the OAuth 2.0 grants or flows but,
for now, just notice that our client uses the *standard* or *authorization code* flow.

Another OAuth 2.0 flow is the so-called "implicit flow", now considered 
deprecated as not secure enough. And since it is deprecated, there is no point
to explain it here, the only thing to notice is that our client disables the 
implicit flow by setting to `false` the property `"implicitFlowEnabled"`.

Another grant or flow type defined by the OAuth 2.0 protocol is the so-called 
*direct access* or *resource owner password* one. When enabled, this type of 
flow allows clients to obtain tokens directly, based on users credentials. These
credentials are directly sent to token endpoints which directly reply with tokens,
instead of authorization codes, like in the case of the *standard flow*. Our 
client doesn't enable this flow, hence the property 
`"directAccessGrantsEnabled" = false`. 

A yet another grant ot flow type defined by the OAuth 2.0 protocol is the one 
known as *service account* or *client credentials*. Like the *direct access* flow,
the *service account* one is able to obtain tokens directly, by providing credentials
but, as opposed to it, these credentials aren't associated with an user and they 
rather act on the own behalf of the client itself. This kind of flow is generally
reserved to machine-to-machine communication or for all the cases where no user
interaction is involved. It is disabled in our case.

The `redirectUri` defines the URI to which the Keycloak service redirects after
successful authentification. We're using the *standard* OAuth 2.0 flow type for our 
`fe-client` and, hence, once successfully authenticated, the server calls the URI
provided by the `redirectUri` property passing to it the authorization code. 
Consequently, the application needs to provide a REST endpoint listening on the
provided URI and accepting as one of the input parameters the authorization code
which should be safely saved for further utilization. In our case, we expose REST
endpoint at http://localhost:8082/callback which performs exactly the described
operations.

The `webOrigins` property is used to handle CORS (*Cross-Origin Resource Sharing*)
by specifying which origins are allowed to make requests to our client. In our 
case, this property is http://localhost:8082, which is the front-end application
root. This means that all the browser requests sent to Keycloak contain the 
header `Origin: http://localhost:8082`. Upon reception, the Keycloak server checks
whether the URL defined by the `Origin` HTTP header is in the list defined by 
the property `webOrigins` and, if not, it rejects the requests. 

In OAuth 2.0, *scopes* are a mechanism to limit and control what access an application
has to resources. They are like permissions or access rights. The property 
`defaultScopes` allows to defines the scopes that will automatically be assigned
to every authorization request made by the client. There are two categories of 
scopes: standard and user defined. Here we're using the following standard scopes:

  - `openid`: this is a mandatory scope for any OpenID Connect based flow; as we'll see later, using it makes the Keycloak server returning the `sub` claim in the ID token.
  - `profile`: this scope allows to include into the user profile information claims like `name`, `prefered_name`, `family_name`, `given_name`, etc.
  - `email`: this scope allows to include the email address into the user profile information.

