package mx.limon.cafeterialimon;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

public class CafeteriaLimon {
    private static final Color GREEN = new Color(40, 76, 57);
    private static final Color GREEN_DARK = new Color(29, 55, 42);
    private static final Color LEMON = new Color(245, 203, 76);
    private static final Color CREAM = new Color(250, 248, 241);
    private static final Color WHITE = Color.WHITE;
    private static final Color TEXT = new Color(45, 50, 45);
    private static final Color MUTED = new Color(130, 135, 127);
    private static final Color BORDER = new Color(232, 231, 222);
    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy  HH:mm");

    private final JFrame frame = new JFrame("Cafetería Limón");
    private final List<Product> products = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final Map<Product, Integer> cart = new LinkedHashMap<>();
    private String currentPage = "Inicio";
    private String selectedCategory = "Todos";
    private String searchText = "";
    private JPanel productGrid;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception exception) {
                System.err.println("No se pudo aplicar el tema del sistema: " + exception.getMessage());
            }
            new CafeteriaLimon().show();
        });
    }

    private CafeteriaLimon() {
        loadDemoProducts();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1080, 700));
        frame.setSize(1280, 800);
        frame.setLocationRelativeTo(null);
        showPage("Inicio");
    }

    private void show() {
        frame.setVisible(true);
    }

    private void loadDemoProducts() {
        products.add(new Product("Café americano", "Cafés", "Espresso y agua caliente", 42, "CA"));
        products.add(new Product("Latte de vainilla", "Cafés", "Espresso, leche y vainilla", 58, "LV"));
        products.add(new Product("Cappuccino", "Cafés", "Espresso con espuma de leche", 55, "CP"));
        products.add(new Product("Cold brew", "Cafés", "Café extraído en frío", 60, "CB"));
        products.add(new Product("Limonada natural", "Bebidas", "Limón fresco y un toque de miel", 38, "LN"));
        products.add(new Product("Té chai", "Bebidas", "Té negro con especias y leche", 52, "TC"));
        products.add(new Product("Panqué de limón", "Repostería", "Rebanada horneada en casa", 45, "PL"));
        products.add(new Product("Galleta de avena", "Repostería", "Avena, canela y chispas", 28, "GA"));
        products.add(new Product("Croissant", "Repostería", "Hojaldre de mantequilla", 40, "CR"));
    }

    private void showPage(String pageName) {
        currentPage = pageName;
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CREAM);
        root.add(buildSidebar(), BorderLayout.WEST);
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(CREAM);
        page.setBorder(new EmptyBorder(30, 34, 28, 34));
        if ("Inicio".equals(pageName)) {
            page.add(buildDashboard(), BorderLayout.CENTER);
        } else if ("Nueva venta".equals(pageName)) {
            page.add(buildSalesPage(), BorderLayout.CENTER);
        } else if ("Menú".equals(pageName)) {
            page.add(buildMenuPage(), BorderLayout.CENTER);
        } else {
            page.add(buildOrdersPage(), BorderLayout.CENTER);
        }
        root.add(page, BorderLayout.CENTER);
        frame.setContentPane(root);
        frame.revalidate();
        frame.repaint();
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(GREEN);
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBorder(new EmptyBorder(28, 18, 22, 18));

        JPanel brand = new JPanel(new BorderLayout(12, 0));
        brand.setOpaque(false);
        RoundedPanel lemonMark = new RoundedPanel(16, LEMON);
        lemonMark.setPreferredSize(new Dimension(44, 44));
        lemonMark.setLayout(new BorderLayout());
        JLabel mark = label("L", 23, GREEN_DARK, Font.BOLD);
        mark.setHorizontalAlignment(SwingConstants.CENTER);
        lemonMark.add(mark);
        JPanel brandText = new JPanel(new GridLayout(2, 1));
        brandText.setOpaque(false);
        brandText.add(label("CAFETERÍA", 11, new Color(210, 224, 212), Font.BOLD));
        brandText.add(label("Limón", 23, WHITE, Font.BOLD));
        brand.add(lemonMark, BorderLayout.WEST);
        brand.add(brandText, BorderLayout.CENTER);

        JPanel navigation = new JPanel(new GridLayout(0, 1, 0, 9));
        navigation.setOpaque(false);
        navigation.setBorder(new EmptyBorder(42, 0, 0, 0));
        String[] pages = {"Inicio", "Nueva venta", "Menú", "Pedidos"};
        String[] symbols = {"⌂", "+", "≡", "▤"};
        for (int i = 0; i < pages.length; i++) {
            final String page = pages[i];
            JButton nav = new JButton(symbols[i] + "    " + page);
            nav.setHorizontalAlignment(SwingConstants.LEFT);
            nav.setFont(new Font("SansSerif", Font.PLAIN, 14));
            nav.setForeground(page.equals(currentPage) ? GREEN_DARK : new Color(230, 236, 228));
            nav.setBackground(page.equals(currentPage) ? LEMON : GREEN);
            nav.setBorder(new EmptyBorder(13, 14, 13, 10));
            nav.setFocusPainted(false);
            nav.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            nav.addActionListener(event -> showPage(page));
            navigation.add(nav);
        }

        JPanel footer = new JPanel(new GridLayout(2, 1, 0, 5));
        footer.setOpaque(false);
        footer.add(label("Punto de venta", 12, new Color(199, 215, 202), Font.PLAIN));
        footer.add(label("Sucursal Centro  ·  Caja 01", 11, WHITE, Font.PLAIN));
        sidebar.add(brand, BorderLayout.NORTH);
        sidebar.add(navigation, BorderLayout.CENTER);
        sidebar.add(footer, BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel buildDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout(0, 24));
        dashboard.setOpaque(false);
        dashboard.add(pageHeading("Hola, barista", "Bienvenido al punto de venta de Cafetería Limón"), BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(0, 24));
        content.setOpaque(false);
        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setOpaque(false);
        double revenue = orders.stream().mapToDouble(order -> order.total).sum();
        stats.add(statCard("Ventas de hoy", money(revenue), orders.size() + " pedidos registrados"));
        stats.add(statCard("Pedidos", String.valueOf(orders.size()), "En esta sesión"));
        stats.add(statCard("Productos", String.valueOf(products.size()), "En el menú"));

        RoundedPanel welcome = new RoundedPanel(20, GREEN);
        welcome.setLayout(new BorderLayout(20, 0));
        welcome.setBorder(new EmptyBorder(30, 32, 30, 32));
        JPanel welcomeCopy = new JPanel(new GridLayout(0, 1, 0, 9));
        welcomeCopy.setOpaque(false);
        welcomeCopy.add(label("Un buen día empieza con algo rico.", 23, WHITE, Font.BOLD));
        welcomeCopy.add(label("Toma un pedido nuevo y prepara algo especial.", 14, new Color(222, 231, 222), Font.PLAIN));
        JButton start = button("Iniciar una venta", LEMON, GREEN_DARK);
        start.addActionListener(event -> showPage("Nueva venta"));
        JPanel actionWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 8));
        actionWrap.setOpaque(false);
        actionWrap.add(start);
        welcomeCopy.add(actionWrap);
        JLabel lemon = label("LIMÓN", 34, LEMON, Font.BOLD);
        lemon.setHorizontalAlignment(SwingConstants.CENTER);
        welcome.add(welcomeCopy, BorderLayout.CENTER);
        welcome.add(lemon, BorderLayout.EAST);

        JPanel recent = new JPanel(new BorderLayout(0, 14));
        recent.setOpaque(false);
        recent.add(label("Actividad reciente", 18, TEXT, Font.BOLD), BorderLayout.NORTH);
        JPanel latest = new JPanel(new BorderLayout());
        latest.setBackground(WHITE);
        latest.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(17, 18, 17, 18)));
        if (orders.isEmpty()) {
            latest.add(label("Todavía no hay pedidos. Tus ventas aparecerán aquí.", 13, MUTED, Font.PLAIN));
        } else {
            Order order = orders.get(orders.size() - 1);
            latest.add(label("Pedido #" + order.id + "     " + order.time.format(DATE_FORMAT)
                    + "     " + order.itemCount() + " productos     " + money(order.total), 13, TEXT, Font.PLAIN));
        }
        recent.add(latest, BorderLayout.CENTER);

        JPanel lower = new JPanel(new BorderLayout(0, 24));
        lower.setOpaque(false);
        lower.add(welcome, BorderLayout.NORTH);
        lower.add(recent, BorderLayout.CENTER);
        content.add(stats, BorderLayout.NORTH);
        content.add(lower, BorderLayout.CENTER);
        dashboard.add(content, BorderLayout.CENTER);
        return dashboard;
    }

    private JPanel statCard(String title, String value, String detail) {
        JPanel card = new JPanel(new GridLayout(3, 1, 0, 5));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(17, 18, 15, 18)));
        card.add(label(title, 13, MUTED, Font.PLAIN));
        card.add(label(value, 25, TEXT, Font.BOLD));
        card.add(label(detail, 12, MUTED, Font.PLAIN));
        return card;
    }

    private JPanel buildSalesPage() {
        JPanel sales = new JPanel(new BorderLayout(22, 0));
        sales.setOpaque(false);
        JPanel left = new JPanel(new BorderLayout(0, 16));
        left.setOpaque(false);
        JPanel heading = new JPanel(new BorderLayout(0, 15));
        heading.setOpaque(false);
        heading.add(pageHeading("Nueva venta", "Selecciona productos para agregarlos al pedido"), BorderLayout.NORTH);

        JPanel filters = new JPanel(new BorderLayout(12, 0));
        filters.setOpaque(false);
        JTextField search = new JTextField(searchText);
        search.setFont(new Font("SansSerif", Font.PLAIN, 14));
        search.setPreferredSize(new Dimension(220, 42));
        search.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(0, 12, 0, 12)));
        search.putClientProperty("JTextField.placeholderText", "Buscar producto...");
        search.addActionListener(event -> {
            searchText = search.getText().trim();
            refreshProductGrid();
        });
        JButton searchButton = button("Buscar", GREEN, WHITE);
        searchButton.addActionListener(event -> {
            searchText = search.getText().trim();
            refreshProductGrid();
        });
        filters.add(search, BorderLayout.CENTER);
        filters.add(searchButton, BorderLayout.EAST);
        heading.add(filters, BorderLayout.SOUTH);
        left.add(heading, BorderLayout.NORTH);

        JPanel catalog = new JPanel(new BorderLayout(0, 12));
        catalog.setOpaque(false);
        JPanel categories = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        categories.setOpaque(false);
        String[] names = {"Todos", "Cafés", "Bebidas", "Repostería"};
        for (String name : names) {
            JButton category = button(name,
                    name.equals(selectedCategory) ? GREEN : WHITE,
                    name.equals(selectedCategory) ? WHITE : TEXT);
            category.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(name.equals(selectedCategory) ? GREEN : BORDER),
                    new EmptyBorder(8, 13, 8, 13)));
            category.addActionListener(event -> {
                selectedCategory = name;
                searchText = search.getText().trim();
                showPage("Nueva venta");
            });
            categories.add(category);
        }
        productGrid = new JPanel(new GridLayout(0, 2, 13, 13));
        productGrid.setOpaque(false);
        refreshProductGrid();
        catalog.add(categories, BorderLayout.NORTH);
        catalog.add(productGrid, BorderLayout.CENTER);
        left.add(catalog, BorderLayout.CENTER);
        sales.add(left, BorderLayout.CENTER);
        sales.add(buildCartPanel(), BorderLayout.EAST);
        return sales;
    }

    private void refreshProductGrid() {
        if (productGrid == null) {
            return;
        }
        productGrid.removeAll();
        int count = 0;
        for (Product product : products) {
            boolean categoryMatches = "Todos".equals(selectedCategory)
                    || product.category.equals(selectedCategory);
            boolean searchMatches = searchText.isEmpty()
                    || product.name.toLowerCase(Locale.ROOT).contains(searchText.toLowerCase(Locale.ROOT))
                    || product.category.toLowerCase(Locale.ROOT).contains(searchText.toLowerCase(Locale.ROOT));
            if (categoryMatches && searchMatches) {
                productGrid.add(productCard(product));
                count++;
            }
        }
        if (count == 0) {
            productGrid.setLayout(new BorderLayout());
            productGrid.add(label("No encontramos productos con esa búsqueda.", 14, MUTED, Font.PLAIN),
                    BorderLayout.NORTH);
        } else {
            productGrid.setLayout(new GridLayout(0, 2, 13, 13));
        }
        productGrid.revalidate();
        productGrid.repaint();
    }

    private JPanel productCard(Product product) {
        JPanel card = new JPanel(new BorderLayout(11, 0));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(13, 12, 13, 12)));
        RoundedPanel icon = new RoundedPanel(14, product.category.equals("Cafés")
                ? new Color(238, 230, 211) : product.category.equals("Bebidas")
                ? new Color(224, 239, 222) : new Color(250, 232, 204));
        icon.setPreferredSize(new Dimension(52, 52));
        icon.setLayout(new BorderLayout());
        JLabel initials = label(product.initials, 15, GREEN, Font.BOLD);
        initials.setHorizontalAlignment(SwingConstants.CENTER);
        icon.add(initials);
        JPanel details = new JPanel(new BorderLayout(0, 6));
        details.setOpaque(false);
        details.add(label(product.name, 13, TEXT, Font.BOLD), BorderLayout.NORTH);
        details.add(label(money(product.price), 13, GREEN, Font.BOLD), BorderLayout.CENTER);
        JButton add = button("+", LEMON, GREEN_DARK);
        add.setPreferredSize(new Dimension(35, 35));
        add.setFont(new Font("SansSerif", Font.BOLD, 18));
        add.addActionListener(event -> {
            cart.put(product, cart.getOrDefault(product, 0) + 1);
            showPage("Nueva venta");
        });
        card.add(icon, BorderLayout.WEST);
        card.add(details, BorderLayout.CENTER);
        card.add(add, BorderLayout.EAST);
        return card;
    }

    private JPanel buildCartPanel() {
        JPanel cartPanel = new JPanel(new BorderLayout(0, 14));
        cartPanel.setBackground(WHITE);
        cartPanel.setPreferredSize(new Dimension(330, 0));
        cartPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER), new EmptyBorder(19, 17, 18, 17)));
        JPanel title = new JPanel(new BorderLayout());
        title.setOpaque(false);
        title.add(label("Pedido actual", 18, TEXT, Font.BOLD), BorderLayout.WEST);
        title.add(label(cart.values().stream().mapToInt(Integer::intValue).sum() + " artículos", 12, MUTED, Font.PLAIN),
                BorderLayout.EAST);
        cartPanel.add(title, BorderLayout.NORTH);

        JPanel items = new JPanel();
        items.setOpaque(false);
        items.setLayout(new javax.swing.BoxLayout(items, javax.swing.BoxLayout.Y_AXIS));
        if (cart.isEmpty()) {
            items.add(label("Aún no has agregado productos.", 13, MUTED, Font.PLAIN));
        } else {
            for (Map.Entry<Product, Integer> entry : cart.entrySet()) {
                items.add(cartItem(entry.getKey(), entry.getValue()));
                items.add(BoxSpacer.vertical(8));
            }
        }
        JScrollPane scroll = new JScrollPane(items);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(WHITE);
        cartPanel.add(scroll, BorderLayout.CENTER);

        JPanel summary = new JPanel(new BorderLayout(0, 12));
        summary.setOpaque(false);
        JPanel totalRow = new JPanel(new BorderLayout());
        totalRow.setOpaque(false);
        totalRow.add(label("Total", 16, TEXT, Font.BOLD), BorderLayout.WEST);
        totalRow.add(label(money(cartTotal()), 20, GREEN, Font.BOLD), BorderLayout.EAST);
        JButton pay = button("Cobrar pedido", GREEN, WHITE);
        pay.setPreferredSize(new Dimension(0, 46));
        pay.setEnabled(!cart.isEmpty());
        pay.addActionListener(event -> checkout());
        summary.add(totalRow, BorderLayout.NORTH);
        summary.add(pay, BorderLayout.SOUTH);
        cartPanel.add(summary, BorderLayout.SOUTH);
        return cartPanel;
    }

    private JPanel cartItem(Product product, int quantity) {
        JPanel row = new JPanel(new BorderLayout(5, 6));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(10, 0, 8, 0));
        JPanel productText = new JPanel(new GridLayout(2, 1, 0, 4));
        productText.setOpaque(false);
        productText.add(label(product.name, 13, TEXT, Font.BOLD));
        productText.add(label(money(product.price * quantity), 12, MUTED, Font.PLAIN));
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        controls.setOpaque(false);
        JButton minus = smallButton("−");
        minus.addActionListener(event -> changeQuantity(product, -1));
        JLabel count = label(String.valueOf(quantity), 13, TEXT, Font.BOLD);
        JButton plus = smallButton("+");
        plus.addActionListener(event -> changeQuantity(product, 1));
        controls.add(minus);
        controls.add(count);
        controls.add(plus);
        row.add(productText, BorderLayout.CENTER);
        row.add(controls, BorderLayout.EAST);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62));
        return row;
    }

    private JButton smallButton(String text) {
        JButton button = button(text, CREAM, GREEN);
        button.setPreferredSize(new Dimension(27, 27));
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        return button;
    }

    private void changeQuantity(Product product, int change) {
        int quantity = cart.getOrDefault(product, 0) + change;
        if (quantity <= 0) {
            cart.remove(product);
        } else {
            cart.put(product, quantity);
        }
        showPage("Nueva venta");
    }

    private void checkout() {
        if (cart.isEmpty()) {
            return;
        }
        double total = cartTotal();
        JDialog dialog = new JDialog(frame, "Cobrar pedido", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(WHITE);
        panel.setBorder(new EmptyBorder(22, 24, 22, 24));
        panel.add(label("Total a cobrar: " + money(total), 20, GREEN, Font.BOLD), BorderLayout.NORTH);

        JPanel paymentOptions = new JPanel(new GridLayout(0, 1, 0, 10));
        paymentOptions.setOpaque(false);
        JRadioButton cash = new JRadioButton("Efectivo");
        JRadioButton card = new JRadioButton("Tarjeta");
        cash.setSelected(true);
        cash.setOpaque(false);
        card.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        group.add(cash);
        group.add(card);
        JTextField received = new JTextField(String.format(Locale.US, "%.2f", total), 12);
        JPanel cashRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        cashRow.setOpaque(false);
        cashRow.add(label("Recibido $", 13, TEXT, Font.PLAIN));
        cashRow.add(received);
        cash.addActionListener(event -> received.setEnabled(true));
        card.addActionListener(event -> received.setEnabled(false));
        paymentOptions.add(cash);
        paymentOptions.add(cashRow);
        paymentOptions.add(card);
        panel.add(paymentOptions, BorderLayout.CENTER);

        JButton confirm = button("Confirmar pago", GREEN, WHITE);
        confirm.setPreferredSize(new Dimension(0, 44));
        confirm.addActionListener(event -> {
            double cashReceived = total;
            if (cash.isSelected()) {
                try {
                    cashReceived = Double.parseDouble(received.getText().trim());
                } catch (NumberFormatException exception) {
                    JOptionPane.showMessageDialog(dialog, "Ingresa una cantidad válida.", "Cantidad inválida",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (!Double.isFinite(cashReceived) || cashReceived < total) {
                    JOptionPane.showMessageDialog(dialog, "El efectivo recibido no cubre el total.",
                            "Pago insuficiente", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            String method = cash.isSelected() ? "Efectivo" : "Tarjeta";
            double change = cash.isSelected() ? cashReceived - total : 0;
            orders.add(new Order(1001 + orders.size(), LocalDateTime.now(), copyCart(), total, method));
            cart.clear();
            dialog.dispose();
            JOptionPane.showMessageDialog(frame,
                    "Pago registrado correctamente.\nCambio: " + money(change),
                    "Venta completada", JOptionPane.INFORMATION_MESSAGE);
            showPage("Inicio");
        });
        panel.add(confirm, BorderLayout.SOUTH);
        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(390, 285));
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    private Map<Product, Integer> copyCart() {
        return new LinkedHashMap<>(cart);
    }

    private JPanel buildMenuPage() {
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setOpaque(false);
        page.add(pageHeading("Nuestro menú", "Productos disponibles en Cafetería Limón"), BorderLayout.NORTH);
        JPanel grid = new JPanel(new GridLayout(0, 3, 14, 14));
        grid.setOpaque(false);
        for (Product product : products) {
            JPanel card = new JPanel(new BorderLayout(0, 10));
            card.setBackground(WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER), new EmptyBorder(18, 18, 16, 18)));
            card.add(label(product.category.toUpperCase(Locale.ROOT), 11, MUTED, Font.BOLD), BorderLayout.NORTH);
            JPanel description = new JPanel(new GridLayout(2, 1, 0, 7));
            description.setOpaque(false);
            description.add(label(product.name, 17, TEXT, Font.BOLD));
            description.add(label(product.description, 12, MUTED, Font.PLAIN));
            card.add(description, BorderLayout.CENTER);
            card.add(label(money(product.price), 16, GREEN, Font.BOLD), BorderLayout.SOUTH);
            grid.add(card);
        }
        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CREAM);
        page.add(scroll, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildOrdersPage() {
        JPanel page = new JPanel(new BorderLayout(0, 20));
        page.setOpaque(false);
        page.add(pageHeading("Pedidos", "Historial de ventas registradas en esta sesión"), BorderLayout.NORTH);
        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new javax.swing.BoxLayout(list, javax.swing.BoxLayout.Y_AXIS));
        if (orders.isEmpty()) {
            JPanel empty = new JPanel(new BorderLayout());
            empty.setBackground(WHITE);
            empty.setBorder(new EmptyBorder(22, 20, 22, 20));
            empty.add(label("Todavía no hay pedidos. Completa una venta para verla aquí.", 14, MUTED, Font.PLAIN));
            list.add(empty);
        } else {
            for (int i = orders.size() - 1; i >= 0; i--) {
                Order order = orders.get(i);
                JPanel row = new JPanel(new BorderLayout(12, 0));
                row.setBackground(WHITE);
                row.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER), new EmptyBorder(16, 18, 16, 18)));
                JPanel details = new JPanel(new GridLayout(2, 1, 0, 6));
                details.setOpaque(false);
                details.add(label("Pedido #" + order.id + "   ·   " + order.time.format(DATE_FORMAT),
                        14, TEXT, Font.BOLD));
                details.add(label(order.itemCount() + " artículos   ·   Pago con " + order.paymentMethod,
                        12, MUTED, Font.PLAIN));
                row.add(details, BorderLayout.CENTER);
                row.add(label(money(order.total), 17, GREEN, Font.BOLD), BorderLayout.EAST);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
                list.add(row);
                list.add(BoxSpacer.vertical(10));
            }
        }
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CREAM);
        page.add(scroll, BorderLayout.CENTER);
        return page;
    }

    private JPanel pageHeading(String title, String subtitle) {
        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 5));
        heading.setOpaque(false);
        heading.add(label(title, 27, TEXT, Font.BOLD));
        heading.add(label(subtitle, 13, MUTED, Font.PLAIN));
        return heading;
    }

    private JButton button(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        button.setForeground(foreground);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(10, 14, 10, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JLabel label(String text, int size, Color color, int style) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", style, size));
        label.setForeground(color);
        return label;
    }

    private double cartTotal() {
        return cart.entrySet().stream().mapToDouble(entry -> entry.getKey().price * entry.getValue()).sum();
    }

    private String money(double amount) {
        return NumberFormat.getCurrencyInstance(new Locale("es", "MX")).format(amount);
    }

    private static final class Product {
        private final String name;
        private final String category;
        private final String description;
        private final double price;
        private final String initials;

        private Product(String name, String category, String description, double price, String initials) {
            this.name = name;
            this.category = category;
            this.description = description;
            this.price = price;
            this.initials = initials;
        }
    }

    private static final class Order {
        private final int id;
        private final LocalDateTime time;
        private final Map<Product, Integer> items;
        private final double total;
        private final String paymentMethod;

        private Order(int id, LocalDateTime time, Map<Product, Integer> items, double total, String paymentMethod) {
            this.id = id;
            this.time = time;
            this.items = items;
            this.total = total;
            this.paymentMethod = paymentMethod;
        }

        private int itemCount() {
            return items.values().stream().mapToInt(Integer::intValue).sum();
        }
    }

    private static final class RoundedPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final int radius;
        private final Color background;

        private RoundedPanel(int radius, Color background) {
            this.radius = radius;
            this.background = background;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(background);
            g.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static final class BoxSpacer {
        private static Component vertical(int height) {
            return javax.swing.Box.createVerticalStrut(height);
        }
    }
}
