-- used in tests that use HSQL
CREATE TABLE IF NOT EXISTS public.oauth_client_details (
  client_id               VARCHAR(256) PRIMARY KEY,
  resource_ids            VARCHAR(256),
  client_secret           VARCHAR(256),
  scope                   VARCHAR(256),
  authorized_grant_types  VARCHAR(256),
  web_server_redirect_uri VARCHAR(256),
  authorities             VARCHAR(256),
  access_token_validity   INTEGER,
  refresh_token_validity  INTEGER,
  additional_information  VARCHAR(4096),
  autoapprove             VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS public.oauth_client_token (
  token_id          VARCHAR(256),
  token             BYTEA,
  authentication_id VARCHAR(256) PRIMARY KEY,
  user_name         VARCHAR(256),
  client_id         VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS public.oauth_access_token (
  token_id          VARCHAR(256),
  token             BYTEA,
  authentication_id VARCHAR(256) PRIMARY KEY,
  user_name         VARCHAR(256),
  client_id         VARCHAR(256),
  authentication    BYTEA,
  refresh_token     VARCHAR(256)
);

CREATE TABLE IF NOT EXISTS public.oauth_refresh_token (
  token_id       VARCHAR(256),
  token          BYTEA,
  authentication BYTEA
);

CREATE TABLE IF NOT EXISTS public.oauth_code (
  code           VARCHAR(256),
  authentication BYTEA
);

CREATE TABLE IF NOT EXISTS public.oauth_approvals (
  userId         VARCHAR(256),
  clientId       VARCHAR(256),
  scope          VARCHAR(256),
  status         VARCHAR(10),
  expiresAt      TIMESTAMP,
  lastModifiedAt TIMESTAMP
);


INSERT INTO public.oauth_client_details (
  client_id, client_secret, scope, authorized_grant_types, web_server_redirect_uri, additional_information, autoapprove)
VALUES ('sampleClientId', '$2a$04$Pf9ztUjKgYs2ZYew.4GdGerExOzdwB7.ogU0vx6PeuurYuQRdoUcm', 'all', 'authorization_code',
        'http://localhost:8080/prova/', '{}', TRUE);
