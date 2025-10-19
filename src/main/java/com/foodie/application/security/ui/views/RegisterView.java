package com.foodie.application.security.ui.views;

import com.foodie.application.security.AuthService;
import com.foodie.application.domain.User;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;
import org.springframework.dao.DataIntegrityViolationException;

@Route("register")
@PageTitle("Registro | Foodie")
@PermitAll
public class RegisterView extends VerticalLayout {

    private final AuthService authService;

    public RegisterView(AuthService authService) {
        this.authService = authService;

        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        // 🔹 Título
        H1 title = new H1("Crear cuenta en Foodie");

        // 🔹 Campos del formulario
        TextField username = new TextField("Usuario");
        EmailField email = new EmailField("Correo electrónico");
        PasswordField password = new PasswordField("Contraseña");
        PasswordField confirmPassword = new PasswordField("Confirmar contraseña");

        username.setRequired(true);
        email.setRequired(true);
        password.setRequired(true);
        confirmPassword.setRequired(true);

        // 🔹 Botón de registro
        Button registerButton = new Button("Registrarse", event -> {
            if (!password.getValue().equals(confirmPassword.getValue())) {
                Notification.show("Las contraseñas no coinciden", 3000, Notification.Position.MIDDLE);
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
                        3000, Notification.Position.MIDDLE);

                // Redirige al login
                getUI().ifPresent(ui -> ui.navigate("login"));
            } catch (DataIntegrityViolationException e) {
                Notification.show("El usuario o correo ya está en uso", 3000, Notification.Position.MIDDLE);
            } catch (Exception e) {
                Notification.show("Error al registrar usuario", 3000, Notification.Position.MIDDLE);
                e.printStackTrace();
            }
        });

        registerButton.setWidth("100%");

        // 🔹 Layout del formulario
        FormLayout form = new FormLayout(username, email, password, confirmPassword, registerButton);
        form.setMaxWidth("400px");

        add(title, form);
    }
}
