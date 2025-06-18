#!/bin/bash
# Create a conditional authentication flow
$KCADM create authentication/flows -r myrealm -s alias="conditional-browser-flow" -s providerId="basic-flow" -s topLevel=true -s builtIn=false

# Add Cookie authenticator as ALTERNATIVE
$KCADM create authentication/flows/conditional-browser-flow/executions/execution -r myrealm -s provider="auth-cookie"
COOKIE_ID=$($KCADM get authentication/flows/conditional-browser-flow/executions -r myrealm | grep -B 2 '"displayName" : "Cookie"' | grep -o '"id" : "[^"]*"' | sed 's/"id" : "//g' | sed 's/"//g' | head -1)
echo '{"id":"'$COOKIE_ID'","requirement":"ALTERNATIVE"}' > /tmp/cookie_update.json
$KCADM update authentication/flows/conditional-browser-flow/executions -r myrealm -f /tmp/cookie_update.json

# Add username form (just username, not password)
$KCADM create authentication/flows/conditional-browser-flow/executions/execution -r myrealm -s provider="auth-username-form"
USERNAME_ID=$($KCADM get authentication/flows/conditional-browser-flow/executions -r myrealm | grep -B 2 '"displayName" : "Username Form"' | grep -o '"id" : "[^"]*"' | sed 's/"id" : "//g' | sed 's/"//g' | head -1)
echo '{"id":"'$USERNAME_ID'","requirement":"REQUIRED"}' > /tmp/username_update.json
$KCADM update authentication/flows/conditional-browser-flow/executions -r myrealm -f /tmp/username_update.json

# Create a forms subflow for authentication options
$KCADM create authentication/flows/conditional-browser-flow/executions/flow -r myrealm -s alias="auth-forms" -s type="basic-flow" -s provider="basic-flow" -s description="Authentication forms"
FORMS_FLOW_ID=$($KCADM get authentication/flows/conditional-browser-flow/executions -r myrealm | grep -B 2 '"displayName" : "auth-forms"' | grep -o '"id" : "[^"]*"' | sed 's/"id" : "//g' | sed 's/"//g' | head -1)
echo '{"id":"'$FORMS_FLOW_ID'","requirement":"REQUIRED"}' > /tmp/forms_update.json
$KCADM update authentication/flows/conditional-browser-flow/executions -r myrealm -f /tmp/forms_update.json

# Add a conditional subflow for WebAuthn users
$KCADM create authentication/flows/auth-forms/executions/flow -r myrealm -s alias="conditional-webauthn" -s type="basic-flow" -s provider="basic-flow" -s description="Conditional WebAuthn authentication"
CONDITIONAL_FLOW_ID=$($KCADM get authentication/flows/auth-forms/executions -r myrealm | grep -B 2 '"displayName" : "conditional-webauthn"' | grep -o '"id" : "[^"]*"' | sed 's/"id" : "//g' | sed 's/"//g' | head -1)
echo '{"id":"'$CONDITIONAL_FLOW_ID'","requirement":"ALTERNATIVE"}' > /tmp/flow_update.json
$KCADM update authentication/flows/auth-forms/executions -r myrealm -f /tmp/flow_update.json

# Add Condition - User Role (instead of User Attribute)
$KCADM create authentication/flows/conditional-webauthn/executions/execution -r myrealm -s provider="conditional-user-role"
ROLE_CONDITION_ID=$($KCADM get authentication/flows/conditional-webauthn/executions -r myrealm | grep -B 2 '"displayName" : "Condition - user role"' | grep -o '"id" : "[^"]*"' | sed 's/"id" : "//g' | sed 's/"//g' | head -1)
echo '{"id":"'$ROLE_CONDITION_ID'","requirement":"REQUIRED"}' > /tmp/condition_update.json
$KCADM update authentication/flows/conditional-webauthn/executions -r myrealm -f /tmp/condition_update.json

# Configure the user role condition
$KCADM create authentication/config -r myrealm -s alias="webauthn-role-config" -s config='{"condUserRole":"webauthn-users"}' -s id=$ROLE_CONDITION_ID

# Add WebAuthn authenticator to the conditional flow
$KCADM create authentication/flows/conditional-webauthn/executions/execution -r myrealm -s provider="webauthn-authenticator-passwordless"
WEBAUTHN_ID=$($KCADM get authentication/flows/conditional-webauthn/executions -r myrealm | grep -B 2 '"displayName" : "WebAuthn Passwordless Authenticator"' | grep -o '"id" : "[^"]*"' | sed 's/"id" : "//g' | sed 's/"//g' | head -1)
echo '{"id":"'$WEBAUTHN_ID'","requirement":"REQUIRED"}' > /tmp/webauthn_update.json
$KCADM update authentication/flows/conditional-webauthn/executions -r myrealm -f /tmp/webauthn_update.json

# Add password form as an alternative
$KCADM create authentication/flows/auth-forms/executions/execution -r myrealm -s provider="auth-password-form"
PASSWORD_ID=$($KCADM get authentication/flows/auth-forms/executions -r myrealm | grep -B 2 '"displayName" : "Password Form"' | grep -o '"id" : "[^"]*"' | sed 's/"id" : "//g' | sed 's/"//g' | head -1)
echo '{"id":"'$PASSWORD_ID'","requirement":"ALTERNATIVE"}' > /tmp/password_update.json
$KCADM update authentication/flows/auth-forms/executions -r myrealm -f /tmp/password_update.json

JANE_ID=$($KCADM get users -r myrealm -q username=jane | grep -B 2 '"username" : "jane"' | grep -o '"id" : "[^"]*"' | sed 's/"id" : "//g' | sed 's/"//g' | head -1)
$KCADM update users/$JANE_ID -r myrealm -s 'requiredActions=["webauthn-register-passwordless"]'

# Set this as the browser flow
$KCADM update realms/myrealm -r myrealm -s browserFlow=conditional-browser-flow
