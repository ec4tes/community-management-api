package com.devkit.hsd.config.security;

import com.devkit.hsd.common.enums.Role;
import com.devkit.hsd.common.enums.SystemRole;
import com.devkit.hsd.common.tenant.TenantContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.jspecify.annotations.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {


    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    public JwtAuthFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    private void buildTenantContext(String token){

        Role businessRole = null;
        Long userId = jwtService.extractUserId(token);
        Long communityId = jwtService.extractCommunityId(token);
        SystemRole systemRole = SystemRole.valueOf(jwtService.extractSystemRole(token));
        String role =  jwtService.extractRole(token);

        if (role != null){
             businessRole = Role.valueOf(role);
        }

        TenantContext.TenantInfo tenantInfo = new TenantContext.TenantInfo(
                userId,
                communityId,
                systemRole,
                businessRole
        );
        TenantContext.setTenantInfo(tenantInfo);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        try {
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            final String jwt;
            final String username;

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            jwt = authHeader.substring(7);
            try {
                username = jwtService.extractUsername(jwt);
            } catch (RuntimeException exception) {
                filterChain.doFilter(request, response);
                return;
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (userDetails.isEnabled() && jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    buildTenantContext(jwt);


                }
            }

            filterChain.doFilter(request, response);
        } finally {

            TenantContext.clear();
        }
    }

}
