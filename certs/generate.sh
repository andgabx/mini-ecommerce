#!/bin/bash
# Gera o certificado self-signed usado por todos os serviços em Docker
keytool -genkeypair \
  -alias ecommerce \
  -keyalg RSA \
  -keysize 2048 \
  -validity 365 \
  -keystore certs/keystore.jks \
  -storepass keystorepass \
  -keypass keystorepass \
  -dname "CN=ecommerce, OU=Dev, O=Ecommerce, L=SP, ST=SP, C=BR" \
  -ext "SAN=DNS:gateway,DNS:users-service,DNS:products-primary,DNS:products-replica,DNS:orders-service,DNS:localhost"

echo "keystore.jks gerado em certs/"
