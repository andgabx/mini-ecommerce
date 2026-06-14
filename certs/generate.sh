#!/bin/bash
# Gera o certificado self-signed e configura o keystore para uso como truststore

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

# Exporta o certificado público para um arquivo temporário
keytool -exportcert \
  -alias ecommerce \
  -keystore certs/keystore.jks \
  -storepass keystorepass \
  -file certs/ecommerce.crt

# Reimporta como TrustedCertEntry — necessário para o TrustManagerFactory funcionar
keytool -importcert \
  -alias ecommerce-trusted \
  -file certs/ecommerce.crt \
  -keystore certs/keystore.jks \
  -storepass keystorepass \
  -noprompt

rm certs/ecommerce.crt

echo "keystore.jks gerado com KeyEntry + TrustedCertEntry em certs/"
