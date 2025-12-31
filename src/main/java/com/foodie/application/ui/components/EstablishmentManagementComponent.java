package com.foodie.application.ui.components;

import com.foodie.application.domain.Establishment;
import com.foodie.application.dto.EstablishmentDto;
import com.foodie.application.service.EstablishmentService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.timepicker.TimePicker;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.time.LocalTime;
import java.util.Locale;

/**
 * Component for managing establishment information.
 * Allows viewing and editing establishment details like name, description, address, phone, and hours.
 */
public class EstablishmentManagementComponent extends VerticalLayout {

    private final EstablishmentService establishmentService;
    private EstablishmentDto currentEstablishment;

    public EstablishmentManagementComponent(EstablishmentService establishmentService) {
        this.establishmentService = establishmentService;

        setPadding(false);
        setSpacing(true);
        setWidthFull();
        setFlexGrow(1, this);

        initializeComponent();
    }

    private void initializeComponent() {
        // Header with title
        H2 title = new H2("Información del Establecimiento");
        title.addClassNames(LumoUtility.Margin.Top.NONE);

        HorizontalLayout titleLayout = new HorizontalLayout();
        titleLayout.setWidthFull();
        titleLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        titleLayout.add(title);
        add(titleLayout);

        // Load and display establishment info
        loadEstablishment();
    }

    /**
     * Loads the establishment information from the service
     */
    private void loadEstablishment() {
        try {
            Establishment establishment = establishmentService.getEstablishment();
            currentEstablishment = EstablishmentDto.fromEstablishment(establishment);
            displayEstablishmentInfo();
        } catch (Exception e) {
            Notification.show("Error al cargar la información del establecimiento: " + e.getMessage(),
                    3000, Notification.Position.TOP_CENTER)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }

    /**
     * Displays the establishment information in a form-like layout
     */
    private void displayEstablishmentInfo() {
        VerticalLayout infoLayout = new VerticalLayout();
        infoLayout.setSpacing(true);
        infoLayout.setWidthFull();
        infoLayout.addClassNames(
                LumoUtility.Padding.MEDIUM,
                LumoUtility.BorderRadius.MEDIUM,
                LumoUtility.Background.BASE,
                LumoUtility.BoxShadow.SMALL
        );

        // Name
        HorizontalLayout nameLayout = createInfoRow("Nombre:", currentEstablishment.getName());
        infoLayout.add(nameLayout);

        // Description
        HorizontalLayout descriptionLayout = createInfoRow("Descripción:",
                currentEstablishment.getDescription() != null ? currentEstablishment.getDescription() : "N/A");
        infoLayout.add(descriptionLayout);

        // Address
        HorizontalLayout addressLayout = createInfoRow("Dirección:",
                currentEstablishment.getAddress() != null ? currentEstablishment.getAddress() : "N/A");
        infoLayout.add(addressLayout);

        // Phone
        HorizontalLayout phoneLayout = createInfoRow("Teléfono:",
                currentEstablishment.getPhone() != null ? currentEstablishment.getPhone() : "N/A");
        infoLayout.add(phoneLayout);

        // Opening Time
        HorizontalLayout openingTimeLayout = createInfoRow("Hora de Apertura:",
                currentEstablishment.getOpeningTime() != null ? currentEstablishment.getOpeningTime().toString() : "N/A");
        infoLayout.add(openingTimeLayout);

        // Closing Time
        HorizontalLayout closingTimeLayout = createInfoRow("Hora de Cierre:",
                currentEstablishment.getClosingTime() != null ? currentEstablishment.getClosingTime().toString() : "N/A");
        infoLayout.add(closingTimeLayout);

        // Edit Button
        Button editBtn = new Button("Editar Información", new Icon(VaadinIcon.EDIT));
        editBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        editBtn.addClickListener(e -> openEditDialog());

        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttonLayout.add(editBtn);

        add(infoLayout);
        add(buttonLayout);
    }

    /**
     * Creates a read-only info row
     */
    private HorizontalLayout createInfoRow(String label, String value) {
        HorizontalLayout row = new HorizontalLayout();
        row.setSpacing(true);
        row.setWidthFull();

        TextField labelField = new TextField();
        labelField.setValue(label);
        labelField.setReadOnly(true);
        labelField.setWidth("200px");

        TextField valueField = new TextField();
        valueField.setValue(value);
        valueField.setReadOnly(true);
        valueField.setWidthFull();

        row.add(labelField, valueField);
        return row;
    }

    /**
     * Opens a dialog to edit establishment information
     */
    private void openEditDialog() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Editar Información del Establecimiento");
        dialog.setWidth("700px");

        VerticalLayout content = new VerticalLayout();
        content.setSpacing(true);

        // Name field
        TextField nameField = new TextField("Nombre del Establecimiento");
        nameField.setValue(currentEstablishment.getName());
        nameField.setWidthFull();
        nameField.setRequired(true);

        // Description field
        TextArea descriptionField = new TextArea("Descripción");
        descriptionField.setValue(currentEstablishment.getDescription() != null ? currentEstablishment.getDescription() : "");
        descriptionField.setWidthFull();
        descriptionField.setHeight("100px");

        // Address field
        TextField addressField = new TextField("Dirección");
        addressField.setValue(currentEstablishment.getAddress() != null ? currentEstablishment.getAddress() : "");
        addressField.setWidthFull();

        // Phone field
        TextField phoneField = new TextField("Teléfono");
        phoneField.setValue(currentEstablishment.getPhone() != null ? currentEstablishment.getPhone() : "");
        phoneField.setWidthFull();

        // Opening Time field
        TimePicker openingTimeField = new TimePicker("Hora de Apertura");
        openingTimeField.setLocale(new Locale("es", "ES"));
        if (currentEstablishment.getOpeningTime() != null) {
            openingTimeField.setValue(currentEstablishment.getOpeningTime());
        }
        openingTimeField.setWidthFull();

        // Closing Time field
        TimePicker closingTimeField = new TimePicker("Hora de Cierre");
        closingTimeField.setLocale(new Locale("es", "ES"));
        if (currentEstablishment.getClosingTime() != null) {
            closingTimeField.setValue(currentEstablishment.getClosingTime());
        }
        closingTimeField.setWidthFull();

        content.add(nameField, descriptionField, addressField, phoneField, openingTimeField, closingTimeField);

        // Save and Cancel buttons
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setSpacing(true);
        buttonLayout.setWidthFull();
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        Button saveBtn = new Button("Guardar", e -> {
            if (nameField.getValue().isEmpty()) {
                Notification.show("El nombre del establecimiento es obligatorio", 3000,
                        Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
                return;
            }

            try {
                // Update establishment
                establishmentService.updateEstablishmentName(nameField.getValue());
                establishmentService.updateEstablishmentDescription(descriptionField.getValue());
                establishmentService.updateEstablishmentAddress(addressField.getValue());
                establishmentService.updateEstablishmentPhone(phoneField.getValue());

                if (openingTimeField.getValue() != null) {
                    establishmentService.updateEstablishmentOpeningTime(openingTimeField.getValue());
                }

                if (closingTimeField.getValue() != null) {
                    establishmentService.updateEstablishmentClosingTime(closingTimeField.getValue());
                }

                Notification.show("Establecimiento actualizado exitosamente", 3000,
                        Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_SUCCESS);

                dialog.close();
                // Reload the component to show updated info
                removeAll();
                initializeComponent();
            } catch (Exception ex) {
                Notification.show("Error al actualizar: " + ex.getMessage(), 3000,
                        Notification.Position.TOP_CENTER)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        saveBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelBtn = new Button("Cancelar", e -> dialog.close());

        buttonLayout.add(saveBtn, cancelBtn);
        dialog.add(content);
        dialog.getFooter().add(buttonLayout);
        dialog.open();
    }
}

