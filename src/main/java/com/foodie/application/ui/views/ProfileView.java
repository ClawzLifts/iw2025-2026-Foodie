package com.foodie.application.ui.views;

import com.foodie.application.domain.User;
import com.foodie.application.service.UserService;
import com.foodie.application.ui.MainLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Profile view for user account management.
 * Allows users to view and edit their personal information.
 *
 * @author Jesus Rodriguez
 * @version 1.0
 * @since 2025
 */
@Route(value = "profile", layout = MainLayout.class)
@PageTitle("Mi Perfil | Foodie")
public class ProfileView extends VerticalLayout {

    private final UserService userService;
    private User currentUser;

    private TextField fullNameField;
    private EmailField emailField;
    private TextField phoneField;
    private TextField addressField;
    private TextField usernameField;

    public ProfileView(UserService userService) {
        this.userService = userService;

        addClassName("profile-view");
        setSizeFull();
        setPadding(true);
        setSpacing(true);

        // Get current user
        currentUser = userService.getCurrentUser();

        if (currentUser == null) {
            add(createLoginRequiredMessage());
            return;
        }

        // Header
        add(createHeader());

        // Main content
        add(createMainContent());
    }

    /**
     * Creates message when user is not logged in
     */
    private VerticalLayout createLoginRequiredMessage() {
        VerticalLayout layout = new VerticalLayout();
        layout.setAlignItems(FlexComponent.Alignment.CENTER);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        layout.setSizeFull();

        Div iconDiv = new Div();
        iconDiv.getStyle().set("font-size", "64px");
        iconDiv.setText("🔒");

        H2 title = new H2("Debes iniciar sesión");
        title.addClassNames(LumoUtility.TextColor.SECONDARY);

        Button loginButton = new Button("Ir a Login", new Icon(VaadinIcon.USER));
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("login")));

        layout.add(iconDiv, title, loginButton);
        return layout;
    }

    /**
     * Creates the header section
     */
    private HorizontalLayout createHeader() {
        H1 title = new H1("Mi Perfil");
        title.addClassNames(
                LumoUtility.Margin.Top.NONE,
                LumoUtility.TextColor.PRIMARY
        );

        Button backButton = new Button("Volver", new Icon(VaadinIcon.ARROW_LEFT));
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate("foodmenu")));

        HorizontalLayout header = new HorizontalLayout(title);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        header.setWidthFull();
        header.expand(title);
        header.add(backButton);

        return header;
    }

    /**
     * Creates the main content area
     */
    private HorizontalLayout createMainContent() {
        HorizontalLayout mainContent = new HorizontalLayout();
        mainContent.setWidthFull();
        mainContent.setSpacing(true);
        mainContent.getStyle().set("gap", "2rem");

        // Left section: Profile info
        VerticalLayout leftSection = createProfileSection();
        leftSection.setFlexGrow(1, leftSection);

        // Right section: Additional options
        VerticalLayout rightSection = createOptionsSection();
        rightSection.setWidth("350px");
        rightSection.setMinWidth("300px");

        mainContent.add(leftSection, rightSection);
        return mainContent;
    }

    /**
     * Creates the profile information section
     */
    private VerticalLayout createProfileSection() {
        VerticalLayout section = new VerticalLayout();
        section.setPadding(false);
        section.setSpacing(true);

        H3 title = new H3("Información Personal");
        title.addClassNames(LumoUtility.Margin.Top.NONE);

        FormLayout formLayout = new FormLayout();
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0px", 1),
                new FormLayout.ResponsiveStep("500px", 2)
        );

        // Username field (read-only)
        usernameField = new TextField("Nombre de Usuario");
        usernameField.setValue(currentUser.getUsername() != null ? currentUser.getUsername() : "");
        usernameField.setReadOnly(true);
        usernameField.setWidthFull();

        // Full Name field
        fullNameField = new TextField("Nombre Completo");
        fullNameField.setValue(currentUser.getFullName() != null ? currentUser.getFullName() : "");
        fullNameField.setWidthFull();
        fullNameField.addValueChangeListener(e -> {
            // Validation
            if (e.getValue().length() > 100) {
                fullNameField.setInvalid(true);
                fullNameField.setErrorMessage("Máximo 100 caracteres");
            } else {
                fullNameField.setInvalid(false);
            }
        });

        // Email field
        emailField = new EmailField("Correo Electrónico");
        emailField.setValue(currentUser.getEmail() != null ? currentUser.getEmail() : "");
        emailField.setWidthFull();
        emailField.setRequiredIndicatorVisible(true);

        // Phone field
        phoneField = new TextField("Teléfono");
        phoneField.setValue(currentUser.getPhoneNumber() != null ? currentUser.getPhoneNumber().toString() : "");
        phoneField.setWidthFull();
        phoneField.setPlaceholder("Ej: 612345678");
        phoneField.addValueChangeListener(e -> {
            // Validation
            String value = e.getValue();
            if (!value.isEmpty() && !value.matches("\\d+")) {
                phoneField.setInvalid(true);
                phoneField.setErrorMessage("Solo se permiten números");
            } else {
                phoneField.setInvalid(false);
            }
        });

        // Address field
        addressField = new TextField("Dirección");
        addressField.setValue(currentUser.getAddress() != null ? currentUser.getAddress() : "");
        addressField.setWidthFull();
        addressField.setPlaceholder("Calle, número, piso");

        formLayout.add(usernameField, fullNameField, emailField, phoneField, addressField);

        // Save button
        Button saveButton = new Button("Guardar Cambios", new Icon(VaadinIcon.CHECK));
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
        saveButton.addClickListener(e -> saveProfile());

        section.add(title, formLayout, saveButton);
        return section;
    }

    /**
     * Creates the options section (password change, danger zone, etc.)
     */
    private VerticalLayout createOptionsSection() {
        VerticalLayout section = new VerticalLayout();
        section.addClassNames(
                LumoUtility.Background.BASE,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.BoxShadow.MEDIUM,
                LumoUtility.Padding.LARGE
        );
        section.setSpacing(true);

        // Change password section
        H3 passwordTitle = new H3("Seguridad");
        passwordTitle.addClassNames(LumoUtility.Margin.Top.NONE);

        Button changePasswordButton = new Button("Cambiar Contraseña", new Icon(VaadinIcon.LOCK));
        changePasswordButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        changePasswordButton.setWidthFull();
        changePasswordButton.addClickListener(e -> showChangePasswordDialog());

        // Danger zone
        H3 dangerTitle = new H3("Zona de Peligro");
        dangerTitle.addClassNames(
                LumoUtility.Margin.Top.LARGE,
                LumoUtility.TextColor.ERROR
        );

        Button deleteAccountButton = new Button("Eliminar Cuenta", new Icon(VaadinIcon.TRASH));
        deleteAccountButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
        deleteAccountButton.setWidthFull();
        deleteAccountButton.addClickListener(e -> showDeleteAccountDialog());

        Div warningDiv = new Div();
        warningDiv.addClassNames(
                LumoUtility.Background.ERROR_10,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.TextColor.ERROR
        );
        Paragraph warning = new Paragraph("⚠️ Esta acción no se puede deshacer");
        warning.getStyle().setMargin("0");
        warningDiv.add(warning);

        section.add(
                passwordTitle,
                changePasswordButton,
                dangerTitle,
                deleteAccountButton,
                warningDiv
        );

        return section;
    }

    /**
     * Saves the profile information
     */
    private void saveProfile() {
        try {
            // Validate inputs
            if (emailField.isEmpty()) {
                Notification.show("El correo es requerido")
                        .addThemeVariants(NotificationVariant.LUMO_WARNING);
                return;
            }


            // Parse phone number
            Long phoneNumber = null;
            if (!phoneField.isEmpty()) {
                try {
                    phoneNumber = Long.parseLong(phoneField.getValue());
                } catch (NumberFormatException e) {
                    Notification.show("El teléfono debe ser un número válido")
                            .addThemeVariants(NotificationVariant.LUMO_WARNING);
                    return;
                }
            }

            // Update profile
            userService.updateUserProfile(
                    currentUser.getId(),
                    fullNameField.getValue(),
                    emailField.getValue(),
                    phoneNumber,
                    addressField.getValue()
            );

            // Refresh current user data
            currentUser = userService.getCurrentUser();

            Notification success = Notification.show(
                    "Perfil actualizado correctamente",
                    3000,
                    Notification.Position.BOTTOM_CENTER
            );
            success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

        } catch (IllegalArgumentException ex) {
            Notification.show("Error: " + ex.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        } catch (Exception ex) {
            Notification.show("Error al actualizar perfil: " + ex.getMessage())
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    /**
     * Shows the change password dialog
     */
    private void showChangePasswordDialog() {
        com.vaadin.flow.component.dialog.Dialog dialog = new com.vaadin.flow.component.dialog.Dialog();
        dialog.setHeaderTitle("Cambiar Contraseña");
        dialog.setWidth("400px");
        dialog.setMaxWidth("90vw");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);

        PasswordField currentPasswordField = new PasswordField("Contraseña Actual");
        currentPasswordField.setWidthFull();
        currentPasswordField.setRequiredIndicatorVisible(true);

        PasswordField newPasswordField = new PasswordField("Nueva Contraseña");
        newPasswordField.setWidthFull();
        newPasswordField.setRequiredIndicatorVisible(true);

        PasswordField confirmPasswordField = new PasswordField("Confirmar Contraseña");
        confirmPasswordField.setWidthFull();
        confirmPasswordField.setRequiredIndicatorVisible(true);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        buttonLayout.getStyle().set("margin-top", "1.5rem");

        Button cancelButton = new Button("Cancelar");
        cancelButton.addClickListener(e -> dialog.close());

        Button changeButton = new Button("Cambiar Contraseña");
        changeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        changeButton.addClickListener(e -> {
            if (newPasswordField.isEmpty() || confirmPasswordField.isEmpty()) {
                Notification.show("Todos los campos son requeridos")
                        .addThemeVariants(NotificationVariant.LUMO_WARNING);
                return;
            }

            if (!newPasswordField.getValue().equals(confirmPasswordField.getValue())) {
                Notification.show("Las contraseñas no coinciden")
                        .addThemeVariants(NotificationVariant.LUMO_WARNING);
                return;
            }

            if (newPasswordField.getValue().length() < 6) {
                Notification.show("La contraseña debe tener al menos 6 caracteres")
                        .addThemeVariants(NotificationVariant.LUMO_WARNING);
                return;
            }

            // TODO: Implement password change with current password verification
            Notification.show("Función no implementada aún - Requiere verificación de contraseña actual")
                    .addThemeVariants(NotificationVariant.LUMO_PRIMARY);
        });

        buttonLayout.add(cancelButton, changeButton);

        content.add(currentPasswordField, newPasswordField, confirmPasswordField, buttonLayout);
        dialog.add(content);
        dialog.open();
    }

    /**
     * Shows the delete account confirmation dialog
     */
    private void showDeleteAccountDialog() {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Eliminar Cuenta");
        dialog.setText(
                "¿Estás seguro de que deseas eliminar tu cuenta? " +
                        "Esta acción no se puede deshacer y todos tus datos serán eliminados permanentemente."
        );
        dialog.setConfirmText("Eliminar Mi Cuenta");
        dialog.setCancelText("Cancelar");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(e -> {
            try {
                userService.deleteUser(currentUser.getId());

                Notification success = Notification.show(
                        "Cuenta eliminada. Redirigiendo...",
                        3000,
                        Notification.Position.MIDDLE
                );
                success.addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                getUI().ifPresent(ui -> {
                    ui.getSession().close();
                    ui.navigate("login");
                });
            } catch (Exception ex) {
                Notification.show("Error al eliminar cuenta: " + ex.getMessage())
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        dialog.open();
    }
}

