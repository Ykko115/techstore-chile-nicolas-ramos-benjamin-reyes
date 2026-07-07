package cl.techstore.api.security;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

class JwtUtilTest {

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secret", "TechStoreJwtSecretKeyForHs256Signing_ChangeInProd2026");
        ReflectionTestUtils.setField(jwtUtil, "expirationSeconds", 3600L);
    }

    @Test
    void generateToken_deberiaCrearTokenValido() {
        String token = jwtUtil.generateToken("admin@techstore.cl");
        assertNotNull(token);
        assertFalse(token.isBlank());
    }

    @Test
    void extractUsername_deberiaRetornarUsuario() {
        String token = jwtUtil.generateToken("admin@techstore.cl");
        assertEquals("admin@techstore.cl", jwtUtil.extractUsername(token));
    }

    @Test
    void isTokenValid_deberiaRetornarTrueParaTokenValido() {
        String token = jwtUtil.generateToken("admin@techstore.cl");
        UserDetails user = User.withUsername("admin@techstore.cl").password("").roles("ADMIN").build();
        assertTrue(jwtUtil.isTokenValid(token, user));
    }

    @Test
    void isTokenValid_deberiaRetornarFalseSiUsuarioNoCoincide() {
        String token = jwtUtil.generateToken("admin@techstore.cl");
        UserDetails otro = User.withUsername("otro@test.cl").password("").roles("USER").build();
        assertFalse(jwtUtil.isTokenValid(token, otro));
    }

    @Test
    void getExpirationSeconds_deberiaRetornarValorDefault() {
        assertEquals(3600, jwtUtil.getExpirationSeconds());
    }
}
