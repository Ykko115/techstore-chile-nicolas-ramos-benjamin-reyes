package cl.techstore.api.security;

import static org.mockito.Mockito.*;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class JwtFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        jwtFilter = new JwtFilter(jwtUtil, userDetailsService);
    }

    @Test
    void doFilter_deberiaContinuarSiNoHayHeader() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void doFilter_deberiaContinuarSiHeaderNoEsBearer() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Basic someencodedstuff");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void doFilter_deberiaAutenticarSiTokenValido() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtUtil.extractUsername("valid-token")).thenReturn("admin@techstore.cl");

        UserDetails userDetails = User.withUsername("admin@techstore.cl").password("").roles("ADMIN").build();
        when(userDetailsService.loadUserByUsername("admin@techstore.cl")).thenReturn(userDetails);
        when(jwtUtil.isTokenValid("valid-token", userDetails)).thenReturn(true);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(jwtUtil).extractUsername("valid-token");
        verify(userDetailsService).loadUserByUsername("admin@techstore.cl");
        verify(jwtUtil).isTokenValid("valid-token", userDetails);
    }

    @Test
    void doFilter_deberiaContinuarSiTokenInvalido() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(jwtUtil.extractUsername("invalid-token")).thenThrow(new JwtException("bad token"));

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_deberiaContinuarSiUsernameNoCoincide() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer valid-token");
        when(jwtUtil.extractUsername("valid-token")).thenReturn("admin@techstore.cl");

        UserDetails userDetails = User.withUsername("admin@techstore.cl").password("").roles("ADMIN").build();
        when(userDetailsService.loadUserByUsername("admin@techstore.cl")).thenReturn(userDetails);
        when(jwtUtil.isTokenValid("valid-token", userDetails)).thenReturn(false);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilter_deberiaContinuarSiUsernameEsNull() throws ServletException, IOException {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalid-token");
        when(jwtUtil.extractUsername("invalid-token")).thenReturn(null);

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
