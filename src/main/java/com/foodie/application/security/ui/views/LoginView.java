package com.foodie.application.security.ui.views;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.login.LoginI18n;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.*;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vaadin.flow.theme.lumo.LumoUtility;

@Route("login")
@PageTitle("Iniciar Sesión | Foodie")
@AnonymousAllowed
public class LoginView extends VerticalLayout implements BeforeEnterObserver {

    private final LoginForm login = new LoginForm();

    public LoginView() {
        setSizeFull();
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);

        addClassName("login-view");

        VerticalLayout loginContainer = new VerticalLayout();
        loginContainer.setWidth("400px");
        loginContainer.setMaxWidth("90%");
        loginContainer.setPadding(true);
        loginContainer.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.LARGE,
                LumoUtility.BoxShadow.LARGE,
                LumoUtility.Padding.XLARGE
        );
        loginContainer.setAlignItems(Alignment.CENTER);

        Image logo = new Image("/image/Foodie.png", "Foodie Logo");
        logo.setWidth("250px");
        logo.setHeight("250px");
        logo.addClassNames(LumoUtility.Margin.Bottom.SMALL);

        login.setAction("/perform_login");
        login.setI18n(createSpanishI18n());
        login.setForgotPasswordButtonVisible(false);

        login.addClassNames(
                LumoUtility.Width.FULL,
                LumoUtility.TextAlignment.CENTER
        );

        loginContainer.add(logo, login);
        add(loginContainer);

        getElement().getStyle()
                .set("background-image", "url('/image/login-bg.jpg')")
                .set("background-size", "cover")
                .set("background-position", "center")
                .set("background-repeat", "no-repeat");
    }

    // ✅ Este método se ejecuta automáticamente cuando se navega a esta vista
    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Location location = event.getLocation();
        QueryParameters queryParameters = location.getQueryParameters();

        if (queryParameters.getParameters().containsKey("error")) {
            login.setError(true);
        }

        // También puedes verificar logout
        if (queryParameters.getParameters().containsKey("logout")) {
            // Mostrar mensaje de logout exitoso si quieres
        }
    }

    private LoginI18n createSpanishI18n() {
        var i18n = LoginI18n.createDefault();
        var form = i18n.getForm();
        form.setTitle("Iniciar sesión");
        form.setUsername("Usuario");
        form.setPassword("Contraseña");
        form.setSubmit("Ingresar");

        var error = i18n.getErrorMessage();
        error.setTitle("Credenciales incorrectas");
        error.setMessage("Usuario o contraseña inválidos");

        i18n.setForm(form);
        i18n.setErrorMessage(error);
        return i18n;
    }
}