package com.vantage.sportsregistration.config;


import com.vantage.sportsregistration.security.FormReleafAccessDeniedHandler;
import com.vantage.sportsregistration.security.FormReleafAuthenticationProvider;
import com.vantage.sportsregistration.security.SecurityAuthenticationFailureHandler;
import com.vantage.sportsregistration.security.SecurityAuthenticationSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.switchuser.SwitchUserFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private FormReleafAuthenticationProvider authenticationProvider;

    @Autowired
    private FormReleafAccessDeniedHandler formReleafAccessDeniedHandler;

    @Autowired
    private UserDetailsService userDetailService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/termsofservice", "/change-password", "/reset-password", "/user/register","/signup", "/login", "/signup-auth", "/login-auth", "/login-error/*",
                        "/api/v1/about",
                        "/webjars/**", "/css/**", "/js/**", "/images/**", "/img/**", "/font-awesome-4.1.0/**").permitAll()
                .antMatchers("/user/profile").authenticated() //hasAnyRole( "USER", "ADMIN" )
                .antMatchers("/user/**", "/organization/**", "/admin/**").hasAnyRole("APPLICATION", "ADMIN")
                .antMatchers("/organizations/list","/organizations/search", "/organizations/search?*").authenticated() //hasAnyRole("ADMIN", "USER")
                .antMatchers("/organizations/*", "/organizations/**").permitAll()
                .antMatchers("/program/**").authenticated()
                .antMatchers("/user/**").authenticated()
                .antMatchers("/api/v1/program/public/**").permitAll()
                .antMatchers("/api/v1/program/**").authenticated()
                .antMatchers("/api/v1/user/public/**").permitAll()
                .antMatchers("/api/v1/user/**").authenticated()
                .antMatchers("/api/v1/user/profile/**").authenticated()
                .antMatchers("/api/**").hasAnyRole("APPLICATION", "ADMIN", "USER")
                .antMatchers("/api/v1/registration/**").hasAnyRole("ADMIN", "USER")
                .antMatchers("/media/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .successHandler(new SecurityAuthenticationSuccessHandler())
                .failureHandler(new SecurityAuthenticationFailureHandler())
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login?logout").invalidateHttpSession(false)
                .permitAll()
                .and()
                .exceptionHandling().accessDeniedPage("/accessDenied")
                .and()
                .authenticationProvider(authenticationProvider)
                .exceptionHandling().accessDeniedHandler(formReleafAccessDeniedHandler)
                .and()
                .addFilterAfter(switchUserFilter(), SecurityContextPersistenceFilter.class)
                .authorizeRequests()
                .antMatchers("/switchuserto").hasRole("APPLICATION")
                .antMatchers("/switchuserExit").hasRole("PREVIOUS_ADMINISTRATOR")
                .and()
                .sessionManagement()
                .invalidSessionUrl("/login?expired");
    }

    @Bean
    public SwitchUserFilter switchUserFilter() {
        SwitchUserFilter switchUserFilter = new SwitchUserFilter();
        switchUserFilter.setUserDetailsService(userDetailService);
        switchUserFilter.setSwitchUserUrl("/switchuserto");
        switchUserFilter.setUsernameParameter("username");
        switchUserFilter.setExitUserUrl("/switchuserlogout");
        switchUserFilter.setSuccessHandler(new SecurityAuthenticationSuccessHandler());
        switchUserFilter.setFailureHandler(new SecurityAuthenticationFailureHandler());

        return switchUserFilter;
    }
}
