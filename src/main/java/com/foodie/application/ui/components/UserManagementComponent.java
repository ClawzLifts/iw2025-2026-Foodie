package com.foodie.application.ui.components;

import com.foodie.application.dto.UserDto;
import com.foodie.application.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.util.stream.Collectors;

/**
 * Component for managing users.
 * Provides functionality to view, edit, and delete users using DTOs.
 */
public class UserManagementComponent extends VerticalLayout {

    private final UserService userService;
    private Grid<UserDto> usersGrid;
    private java.util.List<UserDto> allUsers = new java.util.ArrayList<>();

    public UserManagementComponent(UserService userService) {
        this.userService = userService;

        setPadding(false);
        setSpacing(true);
        setWidthFull();
        setFlexGrow(1, this);

        initializeComponent();
    }

    private void initializeComponent() {
        // Header with title
        H2 title = new H2("Gestión de Usuarios");
        title.addClassNames(LumoUtility.Margin.Top.NONE);

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidthFull();
        titleLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        titleLayout.add(title);
        add(titleLayout);

        // Search bar
        TextField searchField = new TextField();
        searchField.setPlaceholder("Buscar usuario por nombre o email...");
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setWidth("300px");

        HorizontalLayout searchLayout = new HorizontalLayout();
        searchLayout.setWidthFull();
        searchLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        searchLayout.add(searchField);
        add(searchLayout);

        // Users Grid
        usersGrid = new Grid<>(UserDto.class, false);
        usersGrid.setWidthFull();
        usersGrid.setHeightFull();
        usersGrid.addThemeVariants(GridVariant.LUMO_COMPACT);
        usersGrid.setSelectionMode(Grid.SelectionMode.NONE);

        searchField.addValueChangeListener(e -> filterUsers(e.getValue()));

        usersGrid.addColumn(UserDto::getId).setHeader("ID").setFlexGrow(1).setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.CENTER);
        usersGrid.addColumn(UserDto::getUsername).setHeader("Usuario").setFlexGrow(1).setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.CENTER);
        usersGrid.addColumn(UserDto::getEmail).setHeader("Email").setFlexGrow(1).setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.CENTER);
        usersGrid.addColumn(UserDto::getFullName).setHeader("Nombre Completo").setFlexGrow(1).setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.CENTER);
        usersGrid.addColumn(UserDto::getRoleName).setHeader("Rol").setFlexGrow(1).setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.CENTER);
        usersGrid.addColumn(UserDto::getPhoneNumber).setHeader("Teléfono").setFlexGrow(1).setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.CENTER);

        usersGrid.addComponentColumn(userDto -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);
            actions.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            actions.setWidthFull();

            Button viewBtn = new Button(new Icon(VaadinIcon.EYE));
            viewBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);
            viewBtn.addClickListener(e -> openViewUserDialog(userDto));

            Button editBtn = new Button(new Icon(VaadinIcon.EDIT));
            editBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_CONTRAST);
            editBtn.addClickListener(e -> openEditUserDialog(userDto));

            Button deleteBtn = new Button(new Icon(VaadinIcon.TRASH));
            deleteBtn.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
            deleteBtn.addClickListener(e -> openDeleteUserDialog(userDto));

            actions.add(viewBtn, editBtn, deleteBtn);
            return actions;
        }).setHeader("Acciones").setFlexGrow(1).setTextAlign(com.vaadin.flow.component.grid.ColumnTextAlign.CENTER);

        add(usersGrid);

        loadUsers();
    }

    /**
     * Opens a dialog to view user details
     */
    private void openViewUserDialog(UserDto userDto) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Detalles del Usuario");
        dialog.setWidth("600px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        content.add(createReadOnlyField("ID", String.valueOf(userDto.getId())));
        content.add(createReadOnlyField("Usuario", userDto.getUsername()));
        content.add(createReadOnlyField("Email", userDto.getEmail()));
        content.add(createReadOnlyField("Nombre Completo", userDto.getFullName() != null ? userDto.getFullName() : "N/A"));
        content.add(createReadOnlyField("Rol", userDto.getRoleName()));
        content.add(createReadOnlyField("Teléfono", userDto.getPhoneNumber() != null ? userDto.getPhoneNumber().toString() : "N/A"));
        content.add(createReadOnlyField("Dirección", userDto.getAddress() != null ? userDto.getAddress() : "N/A"));

        Button closeBtn = new Button("Cerrar", e -> dialog.close());
        closeBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        dialog.add(content);
        dialog.getFooter().add(closeBtn);
        dialog.open();
    }

    /**
     * Opens a dialog to edit user details
     */
    private void openEditUserDialog(UserDto userDto) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Editar Usuario");
        dialog.setWidth("600px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        TextField fullNameField = new TextField("Nombre Completo");
        fullNameField.setValue(userDto.getFullName() != null ? userDto.getFullName() : "");
        fullNameField.setWidthFull();

        TextField emailField = new TextField("Email");
        emailField.setValue(userDto.getEmail());
        emailField.setWidthFull();
        emailField.setReadOnly(true);

        TextField phoneField = new TextField("Teléfono");
        phoneField.setValue(userDto.getPhoneNumber() != null ? userDto.getPhoneNumber().toString() : "");
        phoneField.setWidthFull();

        TextField addressField = new TextField("Dirección");
        addressField.setValue(userDto.getAddress() != null ? userDto.getAddress() : "");
        addressField.setWidthFull();

        content.add(fullNameField, emailField, phoneField, addressField);

        HorizontalLayout buttonLayout = new HorizontalLayout();
        Button saveBtn = new Button("Guardar", e -> {
            if (fullNameField.getValue().isEmpty()) {
                Notification.show("Por favor completa el nombre", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                userService.updateUserProfile(userDto.getId(), fullNameField.getValue(), null,
                        phoneField.getValue().isEmpty() ? null : Long.parseLong(phoneField.getValue()),
                        addressField.getValue());

                Notification.show("Usuario actualizado exitosamente", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                loadUsers();
                dialog.close();
            } catch (NumberFormatException ex) {
                Notification.show("El teléfono debe ser un número válido", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancelar", e -> dialog.close());

        buttonLayout.add(saveBtn, cancelBtn);
        buttonLayout.setSpacing(true);

        dialog.add(content);
        dialog.getFooter().add(buttonLayout);
        dialog.open();
    }

    /**
     * Opens a dialog to confirm user deletion
     */
    private void openDeleteUserDialog(UserDto userDto) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Confirmar eliminación");
        dialog.setText("¿Estás seguro de que deseas eliminar al usuario '" + userDto.getUsername() + "'?");
        dialog.setConfirmText("Eliminar");
        dialog.setCancelText("Cancelar");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(e -> {
            try {
                userService.deleteUser(userDto.getId());
                Notification.show("Usuario eliminado exitosamente", 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                loadUsers();
            } catch (Exception ex) {
                Notification.show("Error al eliminar usuario: " + ex.getMessage(), 3000, Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });

        dialog.open();
    }

    /**
     * Creates a read-only field for displaying information
     */
    private HorizontalLayout createReadOnlyField(String label, String value) {
        HorizontalLayout layout = new HorizontalLayout();
        layout.setSpacing(true);

        TextField field = new TextField(label);
        field.setValue(value);
        field.setReadOnly(true);
        field.setWidthFull();

        layout.add(field);
        layout.setWidthFull();
        return layout;
    }

    /**
     * Loads all users and converts them to DTOs
     */
    private void loadUsers() {
        var users = userService.getAllUsers();
        allUsers = users.stream()
                .map(UserDto::fromUser)
                .collect(Collectors.toList());

        if (usersGrid != null) {
            usersGrid.setItems(allUsers);
        }
    }

    /**
     * Filters users based on username or email search term
     */
    private void filterUsers(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            usersGrid.setItems(allUsers);
        } else {
            java.util.List<UserDto> filteredUsers = allUsers.stream()
                    .filter(user -> user.getUsername().toLowerCase().contains(searchTerm.toLowerCase()) ||
                                    user.getEmail().toLowerCase().contains(searchTerm.toLowerCase()))
                    .collect(Collectors.toList());
            usersGrid.setItems(filteredUsers);
        }
    }
}

