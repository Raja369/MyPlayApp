package org.myplay.configure;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;


@EnableWebSecurity
public class AppAuthentication extends WebSecurityConfigurerAdapter {

	@Autowired
	DataSource dataSource;
	
	@Autowired
	AuthenticateUser userDetailsService;

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService);
	}
	// Enable jdbc authentication
		@Autowired
		public void configAuthentication(AuthenticationManagerBuilder auth) throws Exception {
			auth.jdbcAuthentication().dataSource(dataSource).passwordEncoder(passwordEncoder());
		}

		@Bean
		public JdbcUserDetailsManager jdbcUserDetailsManager() throws Exception {
			JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager();
			jdbcUserDetailsManager.setDataSource(dataSource);
			return jdbcUserDetailsManager;
		}
	@Override
    protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable();
        http.authorizeRequests().antMatchers("/register").permitAll().antMatchers("/Profile**").hasAnyRole("USER")
        .antMatchers("/recommendations/**").hasAnyRole("USER").antMatchers("/favourites/**").hasAnyRole("USER")
                .antMatchers("/").permitAll().
                and().formLogin().loginPage("/login").permitAll();
    }

	@Bean
	public PasswordEncoder getPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}
	@Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder;
    }
	
}