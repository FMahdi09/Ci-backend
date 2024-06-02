package Ci.Backend.Security;

import Ci.Backend.Services.Token.ExpiredTokenException;
import Ci.Backend.Services.Token.InvalidTokenException;
import Ci.Backend.Services.Token.TokenService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter
{
    @Autowired
    private TokenService tokenService;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException
    {
        if (request.getServletPath().contains("api/auth"))
        {
            filterChain.doFilter(request, response);
            return;
        }

        try
        {
            final String token = getTokenFromRequest(request);

            final String username = tokenService.getUsernameFromAccessToken(token);

            final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            tokenService.validateAccessToken(token, userDetails);

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder
                    .getContext()
                    .setAuthentication(authenticationToken);

            filterChain.doFilter(request, response);
        }
        catch (InvalidTokenException | ExpiredTokenException ex)
        {
            filterChain.doFilter(request, response);
        }
    }

    private String getTokenFromRequest(HttpServletRequest request)
            throws InvalidTokenException
    {
        String bearerToken = request.getHeader("Authorization");

        if (!StringUtils.hasText(bearerToken) ||
                !bearerToken.startsWith("Bearer "))
        {
            throw new InvalidTokenException("malformed authorization header");
        }

        return bearerToken.substring(7);
    }
}
