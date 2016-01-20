#!/bin/sh
## Utility Functions :
execCmd() {
  cmd=$*
  tmpcmd=/opt/PlexView/ELCM/tmp/cmd.exe
  [ -f ${tmpcmd} ] && /bin/rm -f ${tmpcmd}
  /bin/echo "#!/bin/sh" > ${tmpcmd}
  /bin/echo ${cmd} >> ${tmpcmd}
  /bin/echo 'exit $?' >> ${tmpcmd}
  /bin/chmod +x ${tmpcmd}

  tmpfic=/opt/PlexView/ELCM/tmp/cmd.out
  [ -f ${tmpfic} ] && /bin/rm -f ${tmpfic}
  ${tmpcmd} > ${tmpfic} 2>&1
  res=$?
  /bin/rm -f ${tmpcmd}
  return ${res}
}

SSLDir=/opt/PlexView/ELCM/tmp
mkdir -p ${SSLDir}
# Error if environment definitions not available
cfgFile="${SSLDir}/gen_ssl_certificate.cfg"
# OMC-15208 the CA certificate is provided with the delivery
cakey="${SSLDir}/cakey.pem"
cacrt="${SSLDir}/cacert.pem"

if [ ! -f ${cfgFile} ]; then
  echo "-E- could not find ${cfgFile}"
  exit 1
fi

mkdir -p ${SSLDir}/ssl.key
mkdir -p ${SSLDir}/ssl.crt
# OMC-15208 copy the CA certificate from the delivery if not prosent
rm -f ${SSLDir}/ssl.key/ca.key
cafile=${SSLDir}/ssl.key/ca.key
[ ! -f $cafile ] && /bin/cp $cakey $cafile
rm -f ${SSLDir}/ssl.key/server.key
cafile=${SSLDir}/ssl.crt/ca.crt
[ ! -f $cafile ] && /bin/cp $cacrt $cafile
rm -f ${SSLDir}/ssl.crt/server.crt
rm -f ${SSLDir}/ssl.crt/server.csr
rm -f ${SSLDir}/ssl.crt/server.pkcs12

conffile="${SSLDir}/gen_ssl_certificate.cfg"
type="server"
conf=1
chmod 755 $conffile
. $conffile

crtdir="${SSLDir}/ssl.crt"
keydir="${SSLDir}/ssl.key"
WHERE_CMD=which
openssl=`$WHERE_CMD openssl`
$openssl genrsa -out $keydir/server.key 2048

cat >${SSLDir}/.mkcert.cfg <<EOT
[ req ]
default_bits          = 2048
distinguished_name    = req_distinguished_name
prompt                = no

[ req_distinguished_name ]
C                     = $SRV_COUNTRY_NAME
ST                    = $SRV_STATE_OR_PROVINCE_NAME
L                     = $SRV_LOCALITY_NAME
O                     = $SRV_ORGANIZATION_NAME
OU                    = $SRV_ORGANIZATIONAL_UNIT_NAME
CN                    = $(hostname)
EOT
$openssl req -config ${SSLDir}/.mkcert.cfg -new -key $keydir/server.key -out $crtdir/server.csr
passphrase=$CA_PASS_PHRASE


extfile="-extfile ${SSLDir}/.mkcert.cfg"
day=$SRV_CERTIFICATE_VALIDITY
cat >${SSLDir}/.mkcert.cfg <<EOT
extensions = x509v3
[ x509v3 ]
subjectAltName   = email:copy
nsComment        = "mod_ssl generated custom server certificate"
nsCertType       = server
EOT
cmd="$openssl x509 $extfile -days $day -CAcreateserial -CAserial .mkcert.serial \
        -CA $crtdir/ca.crt -CAkey $keydir/ca.key      \
         -passin pass:\"${passphrase}\"  \
         -in $crtdir/server.csr -req -out $crtdir/server.crt"
execCmd ${cmd}

$openssl verify -CAfile $crtdir/ca.crt $crtdir/server.crt
openssl pkcs12 -export -in ${SSLDir}/ssl.crt/server.crt -inkey ${SSLDir}/ssl.key/server.key -out ${SSLDir}/ssl.crt/server.pkcs12 -name tomcat -CAfile ${SSLDir}/ssl.crt/ca.crt -passout pass:serverpkcs12

apachePKCS12File=/opt/PlexView/ELCM/tmp/ssl.crt/server.pkcs12
tomcatHomeDir=/opt/PlexView/ELCM/server
tomcatKeyStoreDir="$tomcatHomeDir/ssl/keystore"
if [ ! -f $tomcatKeyStoreDir/ELCM.jks ]
then
        cd /opt/PlexView/ELCM/tmp
        if [ -f $apachePKCS12File ]; then
                if [ ! -d $tomcatKeyStoreDir ]; then
                        mkdir -p $tomcatKeyStoreDir
                fi

                export RANDFILE=$tomcatKeyStoreDir/.rnd
                if [ -f "$tomcatKeyStoreDir/*.rnd" ]; then
                        chmod  755  $tomcatKeyStoreDir/*.rnd
                fi

                echo "Convert Apache PKCS12 file to Java PKCS12 key store ..."
                openssl pkcs12 -in $apachePKCS12File -out server.pem -passin pass:serverpkcs12 -passout pass:serverpkcs12
                openssl pkcs12 -export -in server.pem -out server.p12 -name "qosacert" -passin pass:serverpkcs12 -passout pass:serverpkcs12
                echo "Convert Java PKCS12 file to a JKS key store ..."
                keytool -importkeystore -srcstorepass serverpkcs12 -srckeystore server.p12 -destkeystore ELCM.jks -deststorepass serverpkcs12 -srcstoretype pkcs12 -deststoretype JKS

                echo "Remove file server.pem ..."
                rm -f server.pem echo "Remove file server.p12 ..."
                rm -f server.p12

                echo "Install Java Keystore file in Tomcat ..."
                mv -f ELCM.jks $tomcatKeyStoreDir

                echo "Tomcat keystore file elcm.jks has been created successfully!"
        else
                echo "Tomcat keystore file will not be created.";
        fi
else
        echo "Tomcat keystore file already exists, will not be created again.";
fi

