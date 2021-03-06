# OpenID Connect using Spring Boot

OpenID Connect is a security mechanism for an application to contact an identity service, verify the identity of the End-User, and obtain End-User information using REST API's in a secure way.

OpenID Connect extends the OAuth 2.0 protocol. In other words, OpenID Connect is a simple identity layer that sits on the top of the OAuth 2.0 protocol. OpenID Connect is needed because even though OAuth provides authorization, it does not provide authentication.

### Key Concepts

To understand how OpenID Connect works we’ll review basic key concepts such as:

**OpenID Provider**: Authorization Server that offers authentication as a service and providing Claims to a Relying Party about the Authentication event and the End-User.

**Relying Party**: OpenID Connect Client application that relies on the OpenID Provider to authenticate users and request claims about that user.

**Scopes**: Scopes are identifiers used to specify what access privileges are being requested. For e.g : openid, email, profile etc.

**Claims**: Claims are simply key & value pairs that contain information about a end-user, as well meta-information about the authentication event. For e.g : Subject, Issuing Authority, Audience, Issue Date and Expiration Date etc.

- The Subject is a unique identifier assigned to a user by the Identity provider, for example a username.
- The Issuing Authority is the Identity provider that issued the token.
- The Audience identifies the Relying Party who can use this token.
- The Issue and Expiration Date is the date and time the token was issued and will expire.
- The Nonce values which mitigate replay attacks.

**Identity Token**:  Identity of the End User provided by OpenID Provider to the Relying Party. The identity token contains a number of claims about that end User and also attributes about the End-User authentication event.

**Access Token**:  Access Tokens are credentials used to access Protected Resources.

**providerURI**: On the OpenID Client registration, the configuration information for that OpenID Provider is retrieved from a “${providerUri}/.well-known/openid-configuration" location as a JSON document, including its Token Id, UserInfo and all other endpoint locations.

**redirectURI**: The Callback URL to which the Authorisation Server will redirect the Browser after authorisation has been granted by the User.

### Authentication Flow and Endpoints :
Before we dive into the application configuration details, let’s have a quick look at how OpenID works, and how we’ll interact with it.

Authorisation code flow diagram :

![OpenID Connect](https://user-images.githubusercontent.com/1628394/92324798-33c24200-f045-11ea-8607-f682d6b81148.png)

The OpenID Connect Provider has a number of EndPoints with which the End-User and Client Application interact. These are the Authorisation Endpoint, the Token Endpoint, the UserInfo endpoint and the JWKS endpoint.

The Authorisation endpoint is where the End-User is asked to authenticate and grant the client application consent to access their identity, and any other required information such as email, or address. Once consent is given by end-user, this endpoint passes back an Authorisation code.

The Relying party then makes a request for an ID token & Access Token to the token endpoint by exchanging the authorization code with client id & secret, to authenticate the client application for an ID token, an access token and optional refresh token.
             
The UserInfo Endpoint is a protected resource that is used by the OpenID Provider to return consented user information or claims to the client application, in exchange of a valid access token.

