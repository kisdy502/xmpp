# xmpp
PushServer代码是完整的
Android 客户端地址https://github.com/kisdy502/androidpn-client
注意，证书请自行替换，我目前项目中的证书的ip是192.168.66.77，换到别人的机器上，ip肯定是不能用了，ssl验证无法通过
生成服务器和android客户端的证书的命令我放在下面了


1、生成服务器证书库
keytool -validity 365 -genkey -v -alias server -keyalg RSA -keystore E:\ssl\server.keystore -dname "CN=192.168.66.77,OU=fengmang,O=fengmang,L=shenzhen,ST=shenzhen,c=cn" -storepass 123456 -keypass 123456

2、生成客户端证书库

keytool -validity 365 -genkeypair -v -alias client -keyalg RSA -storetype PKCS12 -keystore E:\ssl\client.p12 -dname "CN=client,OU=fengmang,O=fengmang,L=shenzhen,ST=shenzhen,c=cn" -storepass 123456 -keypass 123456

3、从客户端证书库中导出客户端证书

keytool -export -v -alias client -keystore E:\ssl\client.p12 -storetype PKCS12 -storepass 123456 -rfc -file E:\ssl\client.cer

4、从服务器证书库中导出服务器证书

keytool -export -v -alias server -keystore E:\ssl\server.keystore -storepass 123456 -rfc -file E:\ssl\server.cer

5、生成客户端信任证书库(由服务端证书生成的证书库)

keytool -import -v -alias server -file E:\ssl\server.cer -keystore E:\ssl\client.truststore -storepass 123456

6、将客户端证书导入到服务器证书库(使得服务器信任客户端证书)

keytool -import -v -alias client -file E:\ssl\client.cer -keystore E:\ssl\server.keystore -storepass 123456

7、查看证书库中的全部证书

keytool -list -keystore E:\ssl\server.keystore -storepass 123456

keytool -list -keystore E:\ssl\client.p12 -storepass 123456

keytool -list -keystore E:\ssl\client.truststore -storepass 123456
