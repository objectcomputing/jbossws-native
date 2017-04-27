 -------------------------------------
       Certificates & keystores
 -------------------------------------


                Alice - Client 1     John - Client 2     Bob - Server

Signature
-> Keystore     alice-sign.jks       john-sign.jks       bob-sign.jks
-> Truststore   wsse10.truststore    wsse10.truststore   wsse10.truststore


> keytool -printcert -file Alice.cer 

Proprietario: CN=Alice, OU=OASIS Interop Test Cert, O=OASIS
Organismo di emissione: CN=OASIS Interop Test CA, O=OASIS
Numero di serie: 33a6047fb155631fed6721178150a899
Valido da Sat Mar 19 01:00:00 CET 2005 a Tue Mar 20 00:59:59 CET 2018
Impronte digitali certificato:
         MD5: 57:CE:81:F1:03:C4:2C:F7:5B:1A:DE:AC:43:64:0A:84
         SHA1: 6E:0E:88:F3:6E:BB:87:44:D4:70:F6:2F:60:4D:03:EA:4E:BE:50:94


--------------------------------------------------------------------------------------
> keytool -printcert -file Bob.cer 
Proprietario: CN=Bob, OU=OASIS Interop Test Cert, O=OASIS
Organismo di emissione: CN=OASIS Interop Test CA, O=OASIS
Numero di serie: 6038eedbfeac9bbec89d87d3abae71f8
Valido da Sat Mar 19 01:00:00 CET 2005 a Tue Mar 20 00:59:59 CET 2018
Impronte digitali certificato:
         MD5: 89:3E:86:D2:4F:9C:E7:39:B6:71:8A:EF:00:C5:89:DC
         SHA1: 35:03:34:20:1B:EE:A6:50:2D:11:34:2F:93:EE:A0:9F:C0:B5:DF:01

--------------------------------------------------------------------------------------
> keytool -printcert -file John.cer 
Proprietario: CN=John, OU=Test, O=Test, L=Test, ST=Test, C=IT
Organismo di emissione: CN=John, OU=Test, O=Test, L=Test, ST=Test, C=IT
Numero di serie: 4832ac71
Valido da Tue May 20 12:48:17 CEST 2008 a Fri May 18 12:48:17 CEST 2018
Impronte digitali certificato:
         MD5: C8:64:7A:4A:67:AC:73:A2:48:26:0A:B3:84:1D:0C:BB
         SHA1: 0A:22:01:1C:11:E0:CC:33:D7:D1:97:D6:BF:0B:3B:77:A3:6C:93:70


--------------------------------------------------------------------------------------
keytool -list -keystore wsse10.truststore 
Immettere la password del keystore:  password

Tipo keystore: jks
Provider keystore: SUN

Il keystore contiene 3 entry

alice, 9-mar-2006, trustedCertEntry,
Impronta digitale certificato (MD5): 57:CE:81:F1:03:C4:2C:F7:5B:1A:DE:AC:43:64:0A:84
bob, 9-mar-2006, trustedCertEntry,
Impronta digitale certificato (MD5): 89:3E:86:D2:4F:9C:E7:39:B6:71:8A:EF:00:C5:89:DC
john, 20-mag-2008, trustedCertEntry,
Impronta digitale certificato (MD5): C8:64:7A:4A:67:AC:73:A2:48:26:0A:B3:84:1D:0C:BB


--------------------------------------------------------------------------------------
> keytool -list -keystore alice-sign.jks 
Immettere la password del keystore:  password

Tipo keystore: jks
Provider keystore: SUN

Il keystore contiene 2 entry

1, 27-ott-2007, keyEntry,
Impronta digitale certificato (MD5): 57:CE:81:F1:03:C4:2C:F7:5B:1A:DE:AC:43:64:0A:84


--------------------------------------------------------------------------------------
> keytool -list -keystore bob-sign.jks 
Immettere la password del keystore:  password

Tipo keystore: jks
Provider keystore: SUN

Il keystore contiene 3 entry

1, 27-ott-2007, keyEntry,
Impronta digitale certificato (MD5): 89:3E:86:D2:4F:9C:E7:39:B6:71:8A:EF:00:C5:89:DC


--------------------------------------------------------------------------------------
> keytool -list -keystore john-sign.jks 
Immettere la password del keystore:  password

Tipo keystore: jks
Provider keystore: SUN

Il keystore contiene 2 entry

1, 20-mag-2008, keyEntry,
Impronta digitale certificato (MD5): C8:64:7A:4A:67:AC:73:A2:48:26:0A:B3:84:1D:0C:BB


--------------------------------------------------------------------------------------
keytool -list -keystore keystore.jks 
Immettere la password del keystore:  password

Tipo keystore: jks
Provider keystore: SUN

Il keystore contiene 3 entry

alice, 9-mar-2006, trustedCertEntry,
Impronta digitale certificato (MD5): 57:CE:81:F1:03:C4:2C:F7:5B:1A:DE:AC:43:64:0A:84
john, 20-mag-2008, trustedCertEntry,
Impronta digitale certificato (MD5): C8:64:7A:4A:67:AC:73:A2:48:26:0A:B3:84:1D:0C:BB

