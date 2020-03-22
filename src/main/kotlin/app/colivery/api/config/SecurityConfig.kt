package app.colivery.api.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import java.io.IOException
import java.time.Instant
import javax.servlet.Filter
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.WebUtils

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
class RestSecurityConfig(
    private val objectMapper: ObjectMapper,
    private val restSecurityProperties: RestSecurityProperties,
    private val tokenfilter: TokenFilter
) : WebSecurityConfigurerAdapter() {

    @Bean
    fun restAuthenticationEntryPoint(): AuthenticationEntryPoint {
        return AuthenticationEntryPoint { _, httpServletResponse, _ ->
            val errorCode = 401
            val errorObject = mapOf(
                "message" to "Access Denied",
                "error" to HttpStatus.UNAUTHORIZED,
                "code" to errorCode,
                "timestamp" to Instant.now()
            )
            httpServletResponse.contentType = "application/json;charset=UTF-8"
            httpServletResponse.status = errorCode
            httpServletResponse.writer.write(objectMapper.writeValueAsString(errorObject))
        }
    }

    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {
        http.cors().and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().csrf()
            .disable().formLogin().disable().httpBasic().disable().exceptionHandling()
            .authenticationEntryPoint(restAuthenticationEntryPoint()).and().authorizeRequests()
            .antMatchers(*restSecurityProperties.allowedPublicApis.toTypedArray()).permitAll()
            .anyRequest().authenticated()
        http.addFilterBefore(tokenfilter, UsernamePasswordAuthenticationFilter::class.java)
    }
}

@Component
class TokenFilter(
    private val restSecurityProperties: RestSecurityProperties,
    private val securityUtils: SecurityUtils
) : OncePerRequestFilter() {

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val path = request.requestURI
        if (!restSecurityProperties.allowedPublicApis.contains(path)) {
            val decodedToken = securityUtils.getTokenFromRequest(request)?.let { idToken ->
                try {
                    FirebaseAuth.getInstance().verifyIdToken(idToken)
                } catch (e: FirebaseAuthException) {
                    logger.error("Firebase Exception:: $e.loc", e)
                    null
                }
            }

            if (decodedToken != null) {
                val authContext = with(decodedToken) {
                    AuthContext(
                        uid = uid,
                        name = name,
                        email = email,
                        picture = picture,
                        issuer = issuer,
                        isEmailVerified = isEmailVerified
                    )
                }
                val authentication = UsernamePasswordAuthenticationToken(authContext,
                    decodedToken, null)
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        }
        filterChain.doFilter(request, response)
    }
}

data class AuthContext(
    val uid: String,
    val email: String,
    val name: String?,
    val isEmailVerified: Boolean,
    val issuer: String,
    val picture: String?
)

@Validated
@Configuration
@ConfigurationProperties(prefix = "security")
class RestSecurityProperties {
    lateinit var allowedDomains: List<String>
    lateinit var allowedHeaders: List<String>
    lateinit var allowedMethods: List<String>
    lateinit var allowedPublicApis: List<String>
}

@Component
class SecurityUtils {

    fun getTokenFromRequest(request: HttpServletRequest): String? {
        val cookieToken: Cookie? = WebUtils.getCookie(request, "token")
        if (cookieToken != null) {
            return cookieToken.value
        } else {
            val bearerToken = request.getHeader("Authorization")
                ?: return null
            if (bearerToken.isNotBlank() && bearerToken.startsWith("Bearer ")) {
                return bearerToken.substring(7, bearerToken.length)
            }
        }
        return null
    }

    val principal: AuthContext?
        get() {
            val securityContext: SecurityContext = SecurityContextHolder.getContext()
            val principal: Any = securityContext.authentication.principal
            return principal as? AuthContext
        }
}

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class RestSecurityFilter(private val restSecurityProperties: RestSecurityProperties) : Filter {

    @Throws(IOException::class, ServletException::class)
    override fun doFilter(req: ServletRequest, res: ServletResponse, chain: FilterChain) {
        val response = res as HttpServletResponse
        val request = req as HttpServletRequest
        val allowedMethods: String = restSecurityProperties.allowedMethods.joinToString(", ")
        val allowedDomains: String = restSecurityProperties.allowedDomains.joinToString(", ")
        val allowedHeaders: String = restSecurityProperties.allowedHeaders.joinToString(", ")
        response.setHeader("Access-Control-Allow-Methods", allowedMethods)
        response.setHeader("Access-Control-Allow-Origin", allowedDomains)
        response.setHeader("Access-Control-Allow-Credentials", "true")
        response.setHeader("Access-Control-Allow-Headers", allowedHeaders)
        if ("OPTIONS".equals(request.method, ignoreCase = true)) {
            response.status = HttpServletResponse.SC_OK
        } else {
            chain.doFilter(req, res)
        }
    }
}
