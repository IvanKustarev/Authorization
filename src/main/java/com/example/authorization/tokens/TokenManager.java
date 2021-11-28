package com.example.authorization.tokens;

import io.jsonwebtoken.*;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.util.Date;

@Service
public class TokenManager  {

    protected static PublicKey publicKey = readPublicKey();
    protected static PrivateKey privateKey = readPrivateKey();

//    @Autowired
//    private UserDBService userDBService;

//
//    @Override
//    public Token generateSmall(String userIdentify) {
//        return generate(userIdentify, 5000);
//    }
//
//    @Override
//    public Token generateAccess(String userIdentify) {
//        return generate(userIdentify, 1800000);
//    }
//
//    @Override
//    public Token generateRefresh(String userIdentify) {
//        return generate(userIdentify, 86400000);
//    }
//
//    @Override
//    public String findUserIdentify(Token token) {
//        return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token.toString()).getBody().get("userIdentify", String.class);
//    }
//
//    @Override
//    public AuthResponse updateTokens(Token refreshToken) {
//        if (check(refreshToken)) {
//            String userId = findUserIdentify(refreshToken);
//            if(userDBService.findUserById(new Long(userId)).getRefreshToken().equals(refreshToken.toString())){
//                Token newAccessToken = generateAccess(userId);
//                Token newRefreshToken = generateRefresh(userId);
//                userDBService.addAccessToken(new Long(userId), newAccessToken);
//                userDBService.addRefreshToken(new Long(userId), newRefreshToken);
//                return new AuthResponse(newAccessToken, newRefreshToken, new Long(userId));
//            }
//        }
//        return null;
//    }

    public Token generate(String userIdentify, long period) {
        readPrivateKey();

        JwtBuilder builder = Jwts.builder();
        builder.claim("userIdentify", userIdentify);
        Date date = new Date();
        builder.setIssuedAt(date);
        builder.setExpiration(new Date(date.getTime() + period));
        builder.signWith(privateKey);
        return new Token(builder.compact());
    }

//    @Override
    public boolean check(Token token) {
        try {
            Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token.toString());
//            return State.VALID;
            return true;
        } catch (MalformedJwtException e) {
//            return State.IN_VALID;
            return false;
        } catch (IllegalArgumentException e) {
            //БОЛЬШЕ ДЛЯ ПРОВЕРКИ, ТК ВЫСКАКИВАЕТ КОГДА ПРИШЁЛ НЕ ВАЛИДНЫЙ КЛЮЧ,
            //ЧТО ОЗНАЧАЕТ ОШУБКУ СО СТОРОНЫ РАЗРАБОТЧИКА
//            return State.WRONG_KEY;
            return false;
        } catch (SignatureException e) {
            e.printStackTrace();
//            return State.WRONG_SIGNATURE;
            return false;
        } catch (ExpiredJwtException e) {
//            return State.LIFE_TIME_OUT;
            return false;
        }
    }

    private static PrivateKey readPrivateKey() {

        try {
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

//            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
//            File file = new File(classLoader.getResource("key.pem").getFile());
            PEMParser pemParser = new PEMParser(new StringReader("-----BEGIN RSA PRIVATE KEY-----\n" +
                    "MIIEpAIBAAKCAQEAnHqQgGeepc2EWwujCtbPj0u+MK08brVsvlMe1BU7GVFq5481\n" +
                    "dgDAHp9/38l1EXsc8IHWMtvDkZt8MRCdaW4tg/U0bwUXmMd96XlKN3nw1ptyjH5p\n" +
                    "TUx3HlFAg7oOPplJUSYG2FLR2XZw7W0KdPIO50V1u9O4MwKol4bm3m6OtkrVdKQk\n" +
                    "mksw2zxOu3MqnqbP796BLzTBwN4hpe89PkwCw9Pw46Su74Kb+Zci3GMeS3FrOGbg\n" +
                    "UGAROLbtbgHoCbInXbfsNfFUZXlArhfyvSIYJoTle6dR0vQAtbW4QrKZtTGmMdFC\n" +
                    "8gBNiGhu3K66PQ7FXcvembXddMgUYRwNdMJs0wIDAQABAoIBACdR1QWvA/p9zghi\n" +
                    "wDF1EOrdCYmJvmnDRPP+Rvzb6OHPx6EwJyrjvWL0iVlecgBWg0scFQrqlhX8kBR0\n" +
                    "LFnVybIi524ORYOqfUUxqSiPh6klnn8XNLarxDLmD+QJ2JJ6SiYN/CJXjUurq2y9\n" +
                    "vabtPDl5BsCPzvVicaLBzzmNZmb/ulUq26CVBmqMOHK4GVRAgpBXHWpnDjyuonLf\n" +
                    "GLe5pgPf0fhFRvgEY3O//i4AaVvbNGYNLyxQY70Awt8P6XL9JkJS7faK/0lyieRW\n" +
                    "XoeFc1lvYaF8mQtvCL2T4bquP1pBLwtaHPbTARTUmaHpImN7E79BMhlaaCNIgzyi\n" +
                    "14kBGIECgYEAyPxD/TIxVG6W56+vJhfrc4ykwG7jHTf6Ap5mz8FS+wsFtNIGWuyA\n" +
                    "Si5QV2QJejMcCuN8veHPPpC1Xw3LwD0lMvCVABXYte13jFao336Z7L6+gJkWPChe\n" +
                    "WzqMn2IYqUi9qWYU5HuzuczUup7DEjhqmurf80DEkNdyH4SS/qvkO9ECgYEAx0+Q\n" +
                    "9C2UEAqbiAS7EBYnwtlYAgSWKA1S0TejHBx+Mfs/r8lgKKa9/Zk6s5+rqv41aUQp\n" +
                    "yfyoE+zFTfV3IHqOLdPnST/67h2w8PxVmTch9hI7vplYRhrh8KYOTs+QZNGs/Mma\n" +
                    "iZnt8FK2nJLw/870w8BielMW9TlGrtPcIRF8W2MCgYEAiVm0UO0jPxMQd6bdKEhC\n" +
                    "dMXRtVxSQx6E8ZM0qZfxUHR44tiA1VQ4W/mImCBaRbN1348VKWKO0a/s8tenvWlL\n" +
                    "6aL2AIftgG6XO0XhxFJteJfl8lYsFtJzoR7DCQzt04pNat3DO126Nihf3GUw5Fu/\n" +
                    "pr0KbN9/NYi2igl8tyPbJnECgYA/vlK9xVX1S28ysVCidHOC/cAH5S+g36H9B65C\n" +
                    "71ns4k4cCmuWlL1uvfLoP8jHh0XNxN8YmpWmybLQnBDdclTGk18FLlEyloxghlNa\n" +
                    "DSfvayVwb9qrkS1xqaopyniDxgttTYz/NRvEILAGo3gwWCAv+veD588P3qGciGV+\n" +
                    "rfj3wQKBgQChkWU20OtECBdrEtPtnfGKKMZcOVdrWrUSLmrEYNg/w5YHIdToIwml\n" +
                    "8H1D15X0BmTk7BmvGsS08BDPhTjAYd157govpk7gPnjrdR5EFSGOruoXFgXzHTtc\n" +
                    "2aVqajKoN6AJaQ4Zqn0uaNLuEOP0rWizvhZco/BnHsvgBj6JnBdOKw==\n" +
                    "-----END RSA PRIVATE KEY-----\n")/*new FileReader(file)*/);

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");
            Object object = pemParser.readObject();
            KeyPair kp = converter.getKeyPair((PEMKeyPair) object);
            return kp.getPrivate();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static PublicKey readPublicKey() {
        try {
//            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
//            File file = new File(classLoader.getResource("public.pem").getFile());

            try (StringReader stringReader = new StringReader("-----BEGIN PUBLIC KEY-----\n" +
                    "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnHqQgGeepc2EWwujCtbP\n" +
                    "j0u+MK08brVsvlMe1BU7GVFq5481dgDAHp9/38l1EXsc8IHWMtvDkZt8MRCdaW4t\n" +
                    "g/U0bwUXmMd96XlKN3nw1ptyjH5pTUx3HlFAg7oOPplJUSYG2FLR2XZw7W0KdPIO\n" +
                    "50V1u9O4MwKol4bm3m6OtkrVdKQkmksw2zxOu3MqnqbP796BLzTBwN4hpe89PkwC\n" +
                    "w9Pw46Su74Kb+Zci3GMeS3FrOGbgUGAROLbtbgHoCbInXbfsNfFUZXlArhfyvSIY\n" +
                    "JoTle6dR0vQAtbW4QrKZtTGmMdFC8gBNiGhu3K66PQ7FXcvembXddMgUYRwNdMJs\n" +
                    "0wIDAQAB\n" +
                    "-----END PUBLIC KEY-----\n")/*FileReader keyReader = new FileReader(file)*/) {
                PEMParser pemParser = new PEMParser(stringReader/*keyReader*/);
                JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
                SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(pemParser.readObject());
                return converter.getPublicKey(publicKeyInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
