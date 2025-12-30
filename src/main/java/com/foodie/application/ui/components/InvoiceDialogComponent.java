package com.foodie.application.ui.components;

import com.foodie.application.dto.OrderDto;
import com.foodie.application.dto.ProductListDto;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

/**
 * Component for displaying invoice/ticket with IVA breakdown
 */
public class InvoiceDialogComponent {


    public static void showInvoice(OrderDto order) {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Ticket/Factura #" + order.getId());
        dialog.setWidth("600px");
        dialog.setMaxHeight("90vh");

        VerticalLayout content = new VerticalLayout();
        content.setPadding(true);
        content.setSpacing(true);
        content.getStyle().set("font-family", "monospace");

        // Header
        H2 restaurantName = new H2("🍔 FOODIE");
        restaurantName.addClassNames(LumoUtility.Margin.Top.NONE, LumoUtility.Margin.Bottom.SMALL);
        restaurantName.getStyle().set("text-align", "center");

        Span restaurantInfo = new Span("Restaurant & Food Service");
        restaurantInfo.getStyle().set("text-align", "center");
        restaurantInfo.getStyle().set("display", "block");

        // Separator
        Div separator1 = new Div();
        separator1.getStyle().set("border-top", "1px dashed #000");
        separator1.getStyle().set("margin", "10px 0");

        content.add(restaurantName, restaurantInfo, separator1);

        // Invoice details
        VerticalLayout invoiceDetails = new VerticalLayout();
        invoiceDetails.setPadding(false);
        invoiceDetails.setSpacing(false);

        Span ticketNum = new Span("Ticket #" + order.getId());
        ticketNum.getStyle().set("font-weight", "bold");

        Span date = new Span("Fecha: " + order.getDate());

        Span client = new Span("Cliente: " + order.getUsername());

        invoiceDetails.add(ticketNum, date, client);

        // Separator
        Div separator2 = new Div();
        separator2.getStyle().set("border-top", "1px dashed #000");
        separator2.getStyle().set("margin", "10px 0");

        content.add(invoiceDetails, separator2);

        // Items header
        HorizontalLayout itemHeader = new HorizontalLayout();
        itemHeader.setWidthFull();
        itemHeader.setSpacing(false);

        Span descCol = new Span("Descripción");
        descCol.setWidth("50%");
        descCol.getStyle().set("font-weight", "bold");

        Span cantCol = new Span("Cant.");
        cantCol.setWidth("15%");
        cantCol.getStyle().set("font-weight", "bold");
        cantCol.getStyle().set("text-align", "right");

        Span precioCol = new Span("P.Unit");
        precioCol.setWidth("17%");
        precioCol.getStyle().set("font-weight", "bold");
        precioCol.getStyle().set("text-align", "right");

        Span totalCol = new Span("Total");
        totalCol.setWidth("18%");
        totalCol.getStyle().set("font-weight", "bold");
        totalCol.getStyle().set("text-align", "right");

        itemHeader.add(descCol, cantCol, precioCol, totalCol);
        content.add(itemHeader);

        // Separator
        Div separator3 = new Div();
        separator3.getStyle().set("border-top", "1px solid #000");
        separator3.getStyle().set("margin", "5px 0");

        content.add(separator3);

        // Items
        double subtotalCalc = 0.0;
        VerticalLayout itemsLayout = new VerticalLayout();
        itemsLayout.setPadding(false);
        itemsLayout.setSpacing(false);

        if (order.getItems() != null) {
            for (ProductListDto item : order.getItems()) {
                double itemTotal = item.getPrice() * item.getQuantity();
                subtotalCalc += itemTotal;

                HorizontalLayout itemRow = new HorizontalLayout();
                itemRow.setWidthFull();
                itemRow.setSpacing(false);

                Span desc = new Span(item.getProductName());
                desc.setWidth("50%");

                Span cant = new Span(String.valueOf(item.getQuantity()));
                cant.setWidth("15%");
                cant.getStyle().set("text-align", "right");

                Span precio = new Span(String.format("%.2f€", item.getPrice()));
                precio.setWidth("17%");
                precio.getStyle().set("text-align", "right");

                Span totalItem = new Span(String.format("%.2f€", itemTotal));
                totalItem.setWidth("18%");
                totalItem.getStyle().set("text-align", "right");

                itemRow.add(desc, cant, precio, totalItem);
                itemsLayout.add(itemRow);
            }
        }

        content.add(itemsLayout);

        // Separator
        Div separator4 = new Div();
        separator4.getStyle().set("border-top", "1px solid #000");
        separator4.getStyle().set("margin", "5px 0");

        content.add(separator4);

        // Calculations - IVA is already included in prices
        // Total pagado = subtotalCalc (incluye IVA)
        // Subtotal sin IVA = Total / 1.21
        // IVA = Total - Subtotal sin IVA
        final double totalWithIVA = subtotalCalc;
        final double subtotal = totalWithIVA / 1.21;
        final double iva = totalWithIVA - subtotal;

        VerticalLayout calcLayout = new VerticalLayout();
        calcLayout.setPadding(false);
        calcLayout.setSpacing(false);

        // Subtotal
        HorizontalLayout subtotalRow = new HorizontalLayout();
        subtotalRow.setWidthFull();
        subtotalRow.setSpacing(true);
        Span subtotalLabel = new Span("Subtotal:");
        Span subtotalValue = new Span(String.format("%.2f€", subtotal));
        subtotalValue.getStyle().set("text-align", "right");
        subtotalValue.setWidth("100px");
        subtotalRow.add(subtotalLabel, subtotalValue);
        subtotalRow.setFlexGrow(1, subtotalLabel);

        // IVA
        HorizontalLayout ivaRow = new HorizontalLayout();
        ivaRow.setWidthFull();
        ivaRow.setSpacing(true);
        Span ivaLabel = new Span("IVA (21%):");
        Span ivaValue = new Span(String.format("%.2f€", iva));
        ivaValue.getStyle().set("text-align", "right");
        ivaValue.setWidth("100px");
        ivaRow.add(ivaLabel, ivaValue);
        ivaRow.setFlexGrow(1, ivaLabel);

        // Total
        HorizontalLayout totalRow = new HorizontalLayout();
        totalRow.setWidthFull();
        totalRow.setSpacing(true);
        Span totalLabel = new Span("TOTAL:");
        totalLabel.getStyle().set("font-weight", "bold");
        Span totalValue = new Span(String.format("%.2f€", totalWithIVA));
        totalValue.getStyle().set("font-weight", "bold");
        totalValue.getStyle().set("text-align", "right");
        totalValue.setWidth("100px");
        totalRow.add(totalLabel, totalValue);
        totalRow.setFlexGrow(1, totalLabel);

        calcLayout.add(subtotalRow, ivaRow, totalRow);
        content.add(calcLayout);

        // Separator
        Div separator5 = new Div();
        separator5.getStyle().set("border-top", "1px dashed #000");
        separator5.getStyle().set("margin", "10px 0");

        content.add(separator5);

        // Footer
        VerticalLayout footerLayout = new VerticalLayout();
        footerLayout.setPadding(false);
        footerLayout.setSpacing(false);

        Span thanks = new Span("¡Gracias por su compra!");
        thanks.getStyle().set("text-align", "center");
        thanks.getStyle().set("display", "block");

        Span footerText = new Span("Vuelva pronto");
        footerText.getStyle().set("text-align", "center");
        footerText.getStyle().set("display", "block");

        footerLayout.add(thanks, footerText);
        content.add(footerLayout);

        // Buttons
        HorizontalLayout buttonLayout = new HorizontalLayout();
        buttonLayout.setWidthFull();
        buttonLayout.setSpacing(true);
        buttonLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        Button printBtn = new Button("Imprimir", new Icon(VaadinIcon.PRINT));
        printBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        printBtn.addClickListener(e -> {
            String printContent = generatePrintContent(order, subtotal, iva, totalWithIVA);
            printTicket(printContent);
        });

        Button closeBtn = new Button("Cerrar", e -> dialog.close());

        buttonLayout.add(printBtn, closeBtn);
        content.add(buttonLayout);

        dialog.add(content);
        dialog.open();
    }

    private static String generatePrintContent(OrderDto order, double subtotal, double iva, double total) {
        StringBuilder html = new StringBuilder();
        html.append("<html><head><meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: monospace; width: 300px; margin: 0 auto; padding: 10px; }");
        html.append("h2 { text-align: center; margin: 5px 0; }");
        html.append(".center { text-align: center; }");
        html.append(".separator { border-top: 1px dashed #000; margin: 10px 0; }");
        html.append(".items { border-top: 1px solid #000; border-bottom: 1px solid #000; padding: 5px 0; }");
        html.append(".row { display: flex; justify-content: space-between; font-size: 12px; }");
        html.append(".total-section { margin-top: 10px; }");
        html.append(".total { font-weight: bold; font-size: 14px; display: flex; justify-content: space-between; }");
        html.append("</style></head><body>");

        html.append("<h2>🍔 FOODIE</h2>");
        html.append("<p class='center'>Restaurant & Food Service</p>");
        html.append("<div class='separator'></div>");

        html.append("<p><b>Ticket #").append(order.getId()).append("</b></p>");
        html.append("<p>Fecha: ").append(order.getDate()).append("</p>");
        html.append("<p>Cliente: ").append(order.getUsername()).append("</p>");

        html.append("<div class='separator'></div>");

        html.append("<div class='items'>");
        if (order.getItems() != null) {
            for (ProductListDto item : order.getItems()) {
                double itemTotal = item.getPrice() * item.getQuantity();
                html.append("<div class='row'>");
                html.append("<span>").append(item.getProductName()).append(" x").append(item.getQuantity()).append("</span>");
                html.append("<span>").append(String.format("%.2f€", itemTotal)).append("</span>");
                html.append("</div>");
            }
        }
        html.append("</div>");

        html.append("<div class='total-section'>");
        html.append("<div class='row'>");
        html.append("<span>Subtotal (sin IVA):</span>");
        html.append("<span>").append(String.format("%.2f€", subtotal)).append("</span>");
        html.append("</div>");

        html.append("<div class='row'>");
        html.append("<span>IVA (21%):</span>");
        html.append("<span>").append(String.format("%.2f€", iva)).append("</span>");
        html.append("</div>");

        html.append("<div class='total'>");
        html.append("<span>TOTAL (con IVA):</span>");
        html.append("<span>").append(String.format("%.2f€", total)).append("</span>");
        html.append("</div>");
        html.append("</div>");

        html.append("<div class='separator'></div>");
        html.append("<p class='center'>¡Gracias por su compra!</p>");
        html.append("<p class='center'>Vuelva pronto</p>");

        html.append("</body></html>");

        return html.toString();
    }

    private static void printTicket(String htmlContent) {
        // Create a popup window for printing
        com.vaadin.flow.component.html.IFrame frame = new com.vaadin.flow.component.html.IFrame();
        frame.setVisible(false);

        // Use JavaScript to print the content
        com.vaadin.flow.component.UI.getCurrent().getPage().executeJs(
                "var win = window.open('', '_blank'); " +
                "win.document.write(arguments[0]); " +
                "win.document.close(); " +
                "win.print(); " +
                "win.close();",
                htmlContent
        );
    }
}

