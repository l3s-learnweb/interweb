package de.l3s.interweb.server.features.user;

import java.net.URI;
import java.util.Optional;

import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.UriInfo;

import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.security.Authenticated;
import io.quarkus.security.identity.SecurityIdentity;
import io.smallrye.jwt.build.Jwt;
import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import de.l3s.interweb.server.Roles;

@Tag(name = "Auth & Identity", description = "User management")
@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UsersResource {
    private static final Logger log = Logger.getLogger(UsersResource.class);

    private static final String NEW_USER_EMAIL_SUBJECT = "Interweb: New user awaiting approval";
    private static final String NEW_USER_EMAIL_BODY = """
        There is a new user awaiting approval:
        %s

        Best regards,
        Interweb Team
        """;

    private static final String LOGIN_APPROVAL_REQUIRED = "Thank you for registration, unfortunately your account is not yet approved. Please wait until we have reviewed your registration.";
    private static final String LOGIN_EMAIL_SUBJECT = "Interweb: Login Link";
    private static final String LOGIN_EMAIL_BODY = """
        Hello,

        To login to Interweb, please click the following link:
        %s

        This link is valid for 6 hours.

        Best regards,
        Interweb Team
        """;

    @ConfigProperty(name = "interweb.admin.email")
    Optional<String> adminEmail;

    @ConfigProperty(name = "interweb.auto-approve.pattern")
    String autoApprovePattern;

    @Inject
    ReactiveMailer mailer;

    @Context
    SecurityIdentity securityIdentity;

    @POST
    @Path("/register")
    @WithTransaction
    @Operation(summary = "Register a new user", description = "Use this method to register a new user")
    public Uni<String> register(@Valid CreateUser user, @Context UriInfo uriInfo) {
        return login(user.email, uriInfo);
    }

    @GET
    @Path("/login")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(summary = "Request JWT token for the given email", description = "Use this method to login to the app and manage tokens")
    public Uni<String> login(@NotEmpty @QueryParam("email") String email, @Context UriInfo uriInfo) {
        return findOrCreateUser(email).chain(user -> {
                if (!user.approved) {
                    return Uni.createFrom().failure(new BadRequestException(LOGIN_APPROVAL_REQUIRED));
                } else {
                    return createAndSendToken(user, uriInfo);
                }
            }).chain(() -> Uni.createFrom().item("The login link has been sent to your email."));
    }

    private Uni<User> findOrCreateUser(String email) {
        return User.findByEmail(email).onItem().ifNull().switchTo(() -> createUser(email).call(user -> {
            if (!user.approved && adminEmail.isPresent()) {
                return mailer.send(Mail.withText(adminEmail.get(), NEW_USER_EMAIL_SUBJECT, NEW_USER_EMAIL_BODY.formatted(user.email)));
            }

            return Uni.createFrom().voidItem();
        }));
    }

    private Uni<Void> createAndSendToken(User user, UriInfo uriInfo) {
        return createToken(user)
            .chain(token -> {
                log.infof("Login token for user %s created: %s", user.email, token.token);
                URI tokenUrl = uriInfo.getBaseUriBuilder().path("/jwt").queryParam("token", token.token).build();
                return mailer.send(Mail.withText(user.email, LOGIN_EMAIL_SUBJECT, LOGIN_EMAIL_BODY.formatted(tokenUrl)));
            });
    }

    @WithTransaction
    protected Uni<User> createUser(String email) {
        User user = new User();
        user.email = email;
        user.approved = email.matches(autoApprovePattern);
        user.role = Roles.USER;
        return user.persist();
    }

    @WithTransaction
    protected Uni<UserToken> createToken(User user) {
        UserToken token = UserToken.generate(UserToken.Type.login);
        token.user = user;
        return token.persist();
    }

    @GET
    @Path("/jwt")
    @Produces(MediaType.TEXT_PLAIN)
    @Operation(summary = "Request JWT token for the given email and password")
    public Uni<String> jwt(@NotEmpty @QueryParam("token") String token) {
        return UserToken.findByToken(UserToken.Type.login, token)
            .onItem().ifNotNull().transform(loginToken -> Jwt.upn(loginToken.user.getName()).groups(loginToken.user.role).sign())
            .onItem().ifNull().failWith(() -> new BadRequestException("The token is invalid or expired"));
    }

    @GET
    @Path("/users/me")
    @Authenticated
    @Operation(summary = "Return the current user", description = "Use this method to get the current user")
    public User me() {
        return (User) securityIdentity.getPrincipal();
    }

    public record CreateUser(@NotNull @NotEmpty @Email @Size(max = 255) String email) {
    }
}
