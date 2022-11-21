package com.suyoung.springsecurityproject.filter.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.SigningKeyResolverAdapter;

import java.security.Key;


/* jwsheader를 통하여 signature 복호화에 필요한 key를 조회한다*/
public class SigningKeyResolver extends SigningKeyResolverAdapter {
    public static SigningKeyResolver instance = new SigningKeyResolver();
    @Override
    public Key resolveSigningKey(JwsHeader jwsHeader, Claims claims) {
        String kid = jwsHeader.getKeyId();
        if (kid == null)
            return null;
        return JwtKey.getKey(kid);
    }
}
