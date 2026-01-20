package fun.plantya.auth.security.service;

import fun.plantya.auth.security.properties.JwtProperties;
import fun.plantya.auth.security.util.KeyUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtService {

    private final JwtProperties props;

    private PrivateKey privateKey;
    private PublicKey publicKey;

    @PostConstruct
    void init() throws Exception {
        this.privateKey = KeyUtil.loadPrivateKey(props.getPrivateKey());
        this.publicKey = KeyUtil.loadPublicKey(props.getPublicKey());
    }

    public String generateToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + props.getExpiration()))
                .signWith(privateKey, SignatureAlgorithm.RS256)
                .compact();
    }

    public Claims validate(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}