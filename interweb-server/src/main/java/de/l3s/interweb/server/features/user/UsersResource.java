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
import de.l3s.interweb.server.features.api.UsageSummary;

@Tag(name = "Auth & Identity", description = "User management (These endpoints does not work with Api-Key authentication)")
@Path("/")
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

        If the above doesn't work, use the following link to retrieve your token:
        %s

        This link is valid for 6 hours.

        Best regards,
        Interweb Team
        """;

    private static final String TOKEN_INVALID = "The token is invalid or expired";

    @ConfigProperty(name = "interweb.admin.email")
    Optional<String> adminEmail;

    @ConfigProperty(name = "interweb.auto-approve.pattern")
    String autoApprovePattern;

    @Inject
    ReactiveMailer mailer;

    @Context
    SecurityIdentity securityIdentity;

    @POST
    @Path("/login")
    @Operation(summary = "Request JWT token for the given email", description = "Use this method to login to the app and manage tokens")
    public Uni<String> login(@Valid LoginBody body, @Context UriInfo uriInfo) {
        return findOrCreateUser(body.email).chain(user -> {
            if (!user.approved) {
                return Uni.createFrom().failure(new BadRequestException(LOGIN_APPROVAL_REQUIRED));
            } else {
                return createAndSendToken(user, uriInfo);
            }
        }).chain(() -> Uni.createFrom().item("The login link sent to your email."));
    }

    private Uni<User> findOrCreateUser(String email) {
        return User.findByEmail(email).onFailure().recoverWithUni(() -> createUser(email).call(user -> {
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
                URI loginUrl = uriInfo.getBaseUriBuilder().queryParam("token", token.token).build();
                URI tokenUrl = uriInfo.getBaseUriBuilder().path("/jwt").queryParam("token", token.token).build();
                return mailer.send(Mail.withText(user.email, LOGIN_EMAIL_SUBJECT, LOGIN_EMAIL_BODY.formatted(loginUrl, tokenUrl)));
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
    @Operation(summary = "Request JWT token for the given email and password")
    public Uni<String> jwt(@NotEmpty @QueryParam("token") String token) {
        return UserToken.findByToken(UserToken.Type.login, token)
            .onItem().ifNotNull().transform(loginToken -> Jwt.upn(loginToken.user.getName()).groups(loginToken.user.role).sign())
            .onItem().ifNull().failWith(() -> new BadRequestException(TOKEN_INVALID));
    }

    @GET
    @Path("/users/me")
    @Authenticated
    @Operation(summary = "Return the current user", description = "Use this method to get the current user")
    public User me() {
        return (User) securityIdentity.getPrincipal();
    }

    @GET
    @Path("/users/usage")
    @Authenticated
    @Operation(summary = "Return the usage of the current user")
    public Uni<UsageSummary> chat() {
        User user = (User) securityIdentity.getPrincipal();
        return UsageSummary.findByUser(user);
    }

    public record LoginBody(@NotNull @NotEmpty @Email @Size(max = 255) String email) {
    }
}
