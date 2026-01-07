package com.foodie.application.security.ui.views;

import com.foodie.application.security.AuthService;
import com.foodie.application.domain.User;
import com.foodie.application.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.annotation.security.PermitAll;
import org.springframework.dao.DataIntegrityViolationException;

@Route("register")
@PageTitle("Registro | Foodie")
@AnonymousAllowed
public class RegisterView extends VerticalLayout implements BeforeEnterObserver {

    private final AuthService authService;
    private final UserService userService;

    public RegisterView(AuthService authService, UserService userService) {
        this.authService = authService;
        this.userService = userService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        addClassName("register-view");

        VerticalLayout registerContainer = new VerticalLayout();
        registerContainer.setWidth("400px");
        registerContainer.setMaxWidth("90%");
        registerContainer.setPadding(true);
        registerContainer.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.LARGE,
                LumoUtility.Padding.XLARGE
        );
        registerContainer.setAlignItems(Alignment.CENTER);

        Image logo = new Image("/image/Foodie.png", "Foodie Logo");
        logo.setWidth("200px");
        logo.setHeight("200px");
        logo.addClassNames(LumoUtility.Margin.Bottom.SMALL);

        H2 title = new H2("Crear cuenta en Foodie");
        title.addClassNames(
                LumoUtility.Margin.Top.NONE,
                LumoUtility.Margin.Bottom.MEDIUM
        );

        // Campos del formulario
        TextField username = new TextField("Usuario");
        username.setRequired(true);
        username.setWidthFull();

        EmailField email = new EmailField("Correo electrónico");
        email.setRequired(true);
        email.setWidthFull();

        PasswordField password = new PasswordField("Contraseña");
        password.setRequired(true);
        password.setWidthFull();

        PasswordField confirmPassword = new PasswordField("Confirmar contraseña");
        confirmPassword.setRequired(true);
        confirmPassword.setWidthFull();

        // Botón de registro
        Button registerButton = new Button("Registrarse", event -> {
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Notification.show("Todos los campos son requeridos", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_WARNING);
                return;
            }

            if (!password.getValue().equals(confirmPassword.getValue())) {
                Notification.show("Las contraseñas no coinciden", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_WARNING);
                return;
            }

            try {
                User user = authService.registerUser(
                        username.getValue(),
                        password.getValue(),
                        email.getValue(),
                        "USER" // rol por defecto
                );

                Notification.show("Usuario " + user.getUsername() + " registrado con éxito 🎉",
                        3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                // Redirige al login
                getUI().ifPresent(ui -> ui.navigate("login"));
            } catch (DataIntegrityViolationException e) {
                Notification.show("El usuario o correo ya está en uso", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            } catch (Exception e) {
                Notification.show("Error al registrar usuario", 3000, Notification.Position.MIDDLE)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        registerButton.setWidthFull();
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        // Layout del formulario
        FormLayout form = new FormLayout(username, email, password, confirmPassword, registerButton);
        form.setWidthFull();
        form.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1)
        );

        // Link para ir al login
        Anchor loginLink = new Anchor("login", "¿Ya tienes cuenta? Inicia sesión");
        loginLink.addClassNames(
                LumoUtility.Margin.Top.MEDIUM,
                LumoUtility.TextAlignment.CENTER
        );

        registerContainer.add(logo, title, form, loginLink);
        add(registerContainer);

        getElement().getStyle()
                .set("background-image", "url('/image/login-bg.jpg')")
                .set("background-size", "cover")
                .set("background-position", "center")
                .set("background-repeat", "no-repeat");
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        // Verificar si el usuario ya está autenticado
        var currentUser = userService.getCurrentUser();
        if (currentUser != null) {
            // Ya está autenticado, redirigir según el rol
            if (currentUser.getRole() != null && "ADMIN".equalsIgnoreCase(currentUser.getRole().getName())) {
                event.forwardTo("admin");
            } else {
                event.forwardTo("foodmenu");
            }
        }
    }
}
