package dev.lilianagorga.wearagain;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.beans.factory.annotation.Value;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Autowired
  private UserDetailsService userDetailsService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

  private static final String CLI_PROFILE = "cli";
  private static final String TEST_PROFILE = "test";

  @Autowired
  private ApplicationContext context;


  @Bean
  public DaoAuthenticationProvider authProvider() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setUserDetailsService(userDetailsService);
    provider.setPasswordEncoder(passwordEncoder);
    return provider;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    String[] activeProfiles = context.getEnvironment().getActiveProfiles();
    if (Arrays.asList(activeProfiles).contains(CLI_PROFILE) || Arrays.asList(activeProfiles).contains(TEST_PROFILE)) {
      http.authorizeHttpRequests((requests) -> requests.anyRequest().permitAll());
    } else {
            http
                    .csrf(AbstractHttpConfigurer::disable)
                    .authenticationProvider(authProvider())
                    .authorizeHttpRequests((requests) -> requests
                            .requestMatchers("/css/**", "/js/**", "/img/**").permitAll()
                            .requestMatchers("/register", "/register/**").permitAll()
                            .requestMatchers("/profile", "/profile/**").authenticated()
                            .requestMatchers("/sales", "/sales/**").authenticated()
                            .requestMatchers("/items", "/items/**").authenticated()
                            .requestMatchers("/api/**").permitAll()
                            .anyRequest().authenticated()
                    )
                    .formLogin(formLogin -> formLogin
                            .failureHandler(customAuthenticationFailureHandler)
                            .loginPage("/login")
                            .loginProcessingUrl("/login")
                            .defaultSuccessUrl("/", true)
                            .failureUrl("/login?error=true")
                            .permitAll()
                    )
                    .oauth2Login(oauth2Login -> oauth2Login
                            .loginPage("/login")
                            .defaultSuccessUrl("/", true)
                            .failureUrl("/login?error=true")
                            .permitAll()
                    )
                    .logout(logout -> logout
                            .logoutUrl("/logout")
                            .logoutSuccessUrl("/login?logout=true")
                            .clearAuthentication(true)
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID")
                            .permitAll()
                    );


            }
      return http.build();
    }
  }