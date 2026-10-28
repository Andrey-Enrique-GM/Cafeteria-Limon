package mx.limon.cafeterialimon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
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
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");
    private static final String[] CATEGORIES = {"Cafés", "Bebidas", "Repostería", "Alimentos"};

    private final JFrame frame = new JFrame("Cafetería Limón");
    private final List<Product> products = new ArrayList<>();
    private final List<Order> orders = new ArrayList<>();
    private final Map<Product, CartLine> cart = new LinkedHashMap<>();
    private final SecureRandom random = new SecureRandom();
    private final EnumMap<OrderStatus, Color> statusColors = new EnumMap<>(OrderStatus.class);
    private boolean administrator;
    private boolean receivingOrders = true;
    private String openingTime = "08:00";
    private String closingTime = "20:00";
    private String customerSession;
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
        statusColors.put(OrderStatus.PENDING, new Color(255, 242, 211));
        statusColors.put(OrderStatus.PREPARING, new Color(221, 235, 255));
        statusColors.put(OrderStatus.READY, new Color(226, 242, 219));
        statusColors.put(OrderStatus.DELIVERED, new Color(235, 235, 235));
        statusColors.put(OrderStatus.CANCELLED, new Color(251, 224, 221));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1080, 700));
        frame.setSize(1280, 820);
        frame.setLocationRelativeTo(null);
        showLanding();
    }

    private void show() {
        frame.setVisible(true);
    }

    private void loadDemoProducts() {
        products.add(new Product("Café americano", "Cafés", "Espresso y agua caliente", 42, 18, "CA"));
        products.add(new Product("Latte de vainilla", "Cafés", "Espresso, leche y vainilla", 58, 12, "LV"));
        products.add(new Product("Cappuccino", "Cafés", "Espresso con espuma de leche", 55, 10, "CP"));
        products.add(new Product("Cold brew", "Cafés", "Café extraído en frío", 60, 8, "CB"));
        products.add(new Product("Limonada natural", "Bebidas", "Limón fresco y un toque de miel", 38, 15, "LN"));
        products.add(new Product("Té chai", "Bebidas", "Té negro con especias y leche", 52, 9, "TC"));
        products.add(new Product("Panqué de limón", "Repostería", "Rebanada horneada en casa", 45, 7, "PL"));
        products.add(new Product("Galleta de avena", "Repostería", "Avena, canela y chispas", 28, 20, "GA"));
        products.add(new Product("Croissant", "Repostería", "Hojaldre de mantequilla", 40, 11, "CR"));
    }

    private void showLanding() {
        administrator = false;
        customerSession = null;
        currentPage = "Inicio";
        JPanel root = new JPanel(new java.awt.GridBagLayout());
        root.setBackground(CREAM);
        JPanel content = new JPanel(new BorderLayout(0, 22));
        content.setBackground(CREAM);
        content.setPreferredSize(new Dimension(560, 500));

        JPanel brand = new JPanel(new GridLayout(2, 1, 0, 7));
        brand.setOpaque(false);
        JLabel lemon = label("LIMÓN", 38, GREEN, Font.BOLD);
        lemon.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel subtitle = label("CAFETERÍA  ·  HECHO CON CALMA", 12, MUTED, Font.BOLD);
        subtitle.setHorizontalAlignment(SwingConstants.CENTER);
        brand.add(lemon);
        brand.add(subtitle);

        JPanel welcome = new JPanel(new GridLayout(2, 1, 0, 8));
        welcome.setOpaque(false);
        JLabel greeting = label("¿Qué te gustaría hacer?", 24, TEXT, Font.BOLD);
        greeting.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel description = label("Explora el menú y prepara tu pedido en unos pasos.", 14, MUTED, Font.PLAIN);
        description.setHorizontalAlignment(SwingConstants.CENTER);
        welcome.add(greeting);
        welcome.add(description);

        JPanel choices = new JPanel(new GridLayout(2, 1, 0, 12));
        choices.setOpaque(false);
        JButton customer = button("Hacer un pedido", GREEN, WHITE);
        customer.setPreferredSize(new Dimension(0, 54));
        customer.addActionListener(event -> enterCustomer());
        JButton admin = button("Iniciar sesión como administrador", WHITE, GREEN);
        admin.setPreferredSize(new Dimension(0, 50));
        admin.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(10, 14, 10, 14)));
        admin.addActionListener(event -> administratorLogin());
        choices.add(customer);
        choices.add(admin);

        JPanel note = new JPanel(new GridLayout(2, 1, 0, 6));
        note.setOpaque(false);
        JLabel hours = label(openingLabel(), 13, GREEN, Font.BOLD);
        hours.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel privacy = label("Sin cuenta para clientes · Consulta tus pedidos durante esta sesión", 12,
                MUTED, Font.PLAIN);
        privacy.setHorizontalAlignment(SwingConstants.CENTER);
        note.add(hours);
        note.add(privacy);

        JPanel stack = new JPanel(new GridLayout(4, 1, 0, 28));
        stack.setOpaque(false);
        stack.add(brand);
        stack.add(welcome);
        stack.add(choices);
        stack.add(note);
        content.add(stack, BorderLayout.CENTER);
        root.add(content);
        frame.setContentPane(root);
        frame.revalidate();
        frame.repaint();
    }

    private void enterCustomer() {
        customerSession = UUID.randomUUID().toString();
        administrator = false;
        cart.clear();
        showPage("Menú");
    }

    private void administratorLogin() {
        JPasswordField password = new JPasswordField(20);
        JPanel prompt = new JPanel(new BorderLayout(0, 8));
        prompt.add(label("Prototipo: escribe cualquier contraseña para continuar.", 13, TEXT, Font.PLAIN),
                BorderLayout.NORTH);
        prompt.add(password, BorderLayout.CENTER);
        int result = JOptionPane.showConfirmDialog(frame, prompt, "Acceso de administrador",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            administrator = true;
            customerSession = UUID.randomUUID().toString();
            cart.clear();
            showPage("Resumen");
        }
    }

    private void showPage(String pageName) {
        if (pageName.equals("Tablero") || pageName.equals("Productos") || pageName.equals("Reportes")
                || pageName.equals("Configuración")) {
            if (!administrator) {
                showLanding();
                return;
            }
        } else if (pageName.equals("Mis pedidos") && customerSession == null) {
            showLanding();
            return;
        }
        currentPage = pageName;
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(CREAM);
        root.add(buildSidebar(), BorderLayout.WEST);
        JPanel page = new JPanel(new BorderLayout());
        page.setBackground(CREAM);
        page.setBorder(new EmptyBorder(28, 30, 25, 30));
        switch (pageName) {
            case "Menú" -> page.add(buildCatalogPage(), BorderLayout.CENTER);
            case "Mis pedidos" -> page.add(buildCustomerOrders(), BorderLayout.CENTER);
            case "Tablero" -> page.add(buildKanbanPage(), BorderLayout.CENTER);
            case "Productos" -> page.add(buildProductsPage(), BorderLayout.CENTER);
            case "Reportes" -> page.add(buildReportsPage(), BorderLayout.CENTER);
            case "Configuración" -> page.add(buildSettingsPage(), BorderLayout.CENTER);
            default -> page.add(buildAdminDashboard(), BorderLayout.CENTER);
        }
        root.add(page, BorderLayout.CENTER);
        frame.setContentPane(root);
        frame.revalidate();
        frame.repaint();
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(GREEN);
        sidebar.setPreferredSize(new Dimension(224, 0));
        sidebar.setBorder(new EmptyBorder(26, 17, 20, 17));

        JPanel brand = new JPanel(new GridLayout(2, 1, 0, 4));
        brand.setOpaque(false);
        brand.add(label("CAFETERÍA", 11, new Color(210, 224, 212), Font.BOLD));
        brand.add(label("Limón", 23, WHITE, Font.BOLD));
        JPanel navigation = new JPanel(new GridLayout(0, 1, 0, 8));
        navigation.setOpaque(false);
        navigation.setBorder(new EmptyBorder(34, 0, 0, 0));

        String[] pages = administrator
                ? new String[]{"Resumen", "Tablero", "Menú", "Mis pedidos", "Productos", "Reportes", "Configuración"}
                : new String[]{"Menú", "Mis pedidos"};
        String[] symbols = administrator
                ? new String[]{"⌂", "▥", "≡", "▤", "✎", "$", "◷"}
                : new String[]{"≡", "▤"};
        for (int i = 0; i < pages.length; i++) {
            String page = pages[i];
            JButton nav = button(symbols[i] + "    " + page,
                    page.equals(currentPage) ? LEMON : GREEN,
                    page.equals(currentPage) ? GREEN_DARK : new Color(230, 236, 228));
            nav.setHorizontalAlignment(SwingConstants.LEFT);
            nav.setBorder(new EmptyBorder(12, 12, 12, 8));
            nav.setFont(new Font("SansSerif", Font.PLAIN, 13));
            nav.addActionListener(event -> showPage(page));
            navigation.add(nav);
        }
        JPanel footer = new JPanel(new GridLayout(0, 1, 0, 8));
        footer.setOpaque(false);
        footer.add(label(administrator ? "Modo administrador" : "Pedido como invitado",
                12, new Color(213, 225, 214), Font.PLAIN));
        JButton exit = button(administrator ? "Cerrar sesión" : "Salir",
                new Color(57, 91, 69), WHITE);
        exit.setHorizontalAlignment(SwingConstants.LEFT);
        exit.addActionListener(event -> {
            cart.clear();
            showLanding();
        });
        footer.add(exit);
        sidebar.add(brand, BorderLayout.NORTH);
        sidebar.add(navigation, BorderLayout.CENTER);
        sidebar.add(footer, BorderLayout.SOUTH);
        return sidebar;
    }

    private JPanel buildAdminDashboard() {
        JPanel dashboard = new JPanel(new BorderLayout(0, 22));
        dashboard.setOpaque(false);
        dashboard.add(pageHeading("Resumen de la sesión", "Ventas, pedidos e inventario de Cafetería Limón"),
                BorderLayout.NORTH);
        List<Order> today = ordersToday();
        long active = orders.stream().filter(order -> !terminal(order.status)).count();
        double total = today.stream().filter(order -> order.status != OrderStatus.CANCELLED)
                .mapToDouble(Order::total).sum();
        JPanel stats = new JPanel(new GridLayout(1, 4, 12, 0));
        stats.setOpaque(false);
        stats.add(statCard("Ventas de hoy", money(total), today.size() + " pedidos hoy"));
        stats.add(statCard("En proceso", String.valueOf(active), "Por hacer, preparando o listos"));
        stats.add(statCard("Productos", String.valueOf(products.size()), "En el catálogo"));
        stats.add(statCard("Horario", receivingOrders ? openingTime + " – " + closingTime : "Cerrado",
                isReceivingNow() ? "Recibiendo pedidos" : "No se reciben pedidos ahora"));
        JPanel content = new JPanel(new BorderLayout(0, 18));
        content.setOpaque(false);
        JPanel shortcuts = new JPanel(new GridLayout(1, 3, 12, 0));
        shortcuts.setOpaque(false);
        shortcuts.add(shortcutCard("Revisar pedidos", "Actualiza el flujo de cocina", "Tablero"));
        shortcuts.add(shortcutCard("Gestionar catálogo", "Precios, existencias y productos", "Productos"));
        shortcuts.add(shortcutCard("Ver reportes", "Ventas por método y preparación", "Reportes"));
        JPanel recent = buildRecentOrders(6);
        content.add(shortcuts, BorderLayout.NORTH);
        content.add(recent, BorderLayout.CENTER);
        dashboard.add(stats, BorderLayout.NORTH);
        dashboard.add(content, BorderLayout.CENTER);
        return dashboard;
    }

    private JPanel shortcutCard(String title, String detail, String destination) {
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(17, 15, 17, 15)));
        JPanel text = new JPanel(new GridLayout(2, 1, 0, 5));
        text.setOpaque(false);
        text.add(label(title, 14, TEXT, Font.BOLD));
        text.add(label(detail, 11, MUTED, Font.PLAIN));
        JButton open = button("Abrir →", CREAM, GREEN);
        open.addActionListener(event -> showPage(destination));
        card.add(text, BorderLayout.CENTER);
        card.add(open, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildRecentOrders(int maxItems) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.add(label("Pedidos recientes", 17, TEXT, Font.BOLD), BorderLayout.NORTH);
        JPanel rows = new JPanel();
        rows.setOpaque(false);
        rows.setLayout(new javax.swing.BoxLayout(rows, javax.swing.BoxLayout.Y_AXIS));
        int shown = 0;
        for (int i = orders.size() - 1; i >= 0 && shown < maxItems; i--) {
            rows.add(orderSummaryRow(orders.get(i), true));
            rows.add(Box.createVerticalStrut(7));
            shown++;
        }
        if (shown == 0) {
            rows.add(emptyCard("Los pedidos nuevos aparecerán aquí."));
        }
        JScrollPane scroll = new JScrollPane(rows);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CREAM);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildCatalogPage() {
        JPanel catalog = new JPanel(new BorderLayout(0, 16));
        catalog.setOpaque(false);
        JPanel top = new JPanel(new BorderLayout(0, 12));
        top.setOpaque(false);
        JPanel title = new JPanel(new BorderLayout());
        title.setOpaque(false);
        title.add(pageHeading(administrator ? "Menú de la cafetería" : "¿Qué se te antoja?",
                administrator ? "Consulta los productos y su disponibilidad" : "Busca algo rico y arma tu pedido"),
                BorderLayout.CENTER);
        if (!administrator) {
            JLabel hours = label(openingLabel(), 12, isReceivingNow() ? GREEN : new Color(172, 87, 67), Font.BOLD);
            title.add(hours, BorderLayout.EAST);
        }
        top.add(title, BorderLayout.NORTH);
        JTextField search = new JTextField(searchText);
        search.setFont(new Font("SansSerif", Font.PLAIN, 14));
        search.setPreferredSize(new Dimension(200, 39));
        search.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(0, 10, 0, 10)));
        search.putClientProperty("JTextField.placeholderText", "Buscar por nombre...");
        search.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                searchText = search.getText().trim();
                refreshProductGrid();
            }
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent event) { update(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent event) { update(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent event) { update(); }
        });
        JPanel filters = new JPanel(new BorderLayout(12, 0));
        filters.setOpaque(false);
        filters.add(search, BorderLayout.CENTER);
        JPanel categories = new JPanel(new FlowLayout(FlowLayout.LEFT, 7, 0));
        categories.setOpaque(false);
        for (String category : categoryOptions()) {
            JButton filter = button(category,
                    category.equals(selectedCategory) ? GREEN : WHITE,
                    category.equals(selectedCategory) ? WHITE : TEXT);
            filter.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(category.equals(selectedCategory) ? GREEN : BORDER),
                    new EmptyBorder(8, 11, 8, 11)));
            filter.addActionListener(event -> {
                selectedCategory = category;
                showPage("Menú");
            });
            categories.add(filter);
        }
        filters.add(categories, BorderLayout.SOUTH);
        top.add(filters, BorderLayout.SOUTH);
        catalog.add(top, BorderLayout.NORTH);

        JPanel content = new JPanel(new BorderLayout(15, 0));
        content.setOpaque(false);
        productGrid = new JPanel(new GridLayout(0, administrator ? 2 : 2, 12, 12));
        productGrid.setOpaque(false);
        refreshProductGrid();
        JScrollPane productScroll = new JScrollPane(productGrid);
        productScroll.setBorder(null);
        productScroll.getViewport().setBackground(CREAM);
        content.add(productScroll, BorderLayout.CENTER);
        content.add(buildCartPanel(), BorderLayout.EAST);
        catalog.add(content, BorderLayout.CENTER);
        return catalog;
    }

    private List<String> categoryOptions() {
        List<String> options = new ArrayList<>();
        options.add("Todos");
        for (String category : CATEGORIES) {
            if (products.stream().anyMatch(product -> product.category.equals(category))) {
                options.add(category);
            }
        }
        return options;
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
            boolean searchMatches = searchText.isBlank()
                    || product.name.toLowerCase(Locale.ROOT).contains(searchText.toLowerCase(Locale.ROOT));
            if (categoryMatches && searchMatches) {
                productGrid.add(productCard(product));
                count++;
            }
        }
        if (count == 0) {
            productGrid.setLayout(new BorderLayout());
            productGrid.add(label("No hay productos que coincidan con la búsqueda.", 14, MUTED, Font.PLAIN),
                    BorderLayout.NORTH);
        } else {
            productGrid.setLayout(new GridLayout(0, 2, 12, 12));
        }
        productGrid.revalidate();
        productGrid.repaint();
    }

    private JPanel productCard(Product product) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(13, 12, 13, 12)));
        RoundedPanel icon = new RoundedPanel(14, product.category.equals("Cafés")
                ? new Color(238, 230, 211) : product.category.equals("Bebidas")
                ? new Color(224, 239, 222) : new Color(250, 232, 204));
        icon.setPreferredSize(new Dimension(48, 48));
        icon.setLayout(new BorderLayout());
        JLabel initials = label(product.initials, 14, GREEN, Font.BOLD);
        initials.setHorizontalAlignment(SwingConstants.CENTER);
        icon.add(initials);
        JPanel details = new JPanel(new BorderLayout(0, 6));
        details.setOpaque(false);
        details.add(label(product.name, 13, TEXT, Font.BOLD), BorderLayout.NORTH);
        details.add(label(money(product.price), 13, GREEN, Font.BOLD), BorderLayout.CENTER);
        details.add(label(product.stock > 0 ? product.stock + " disponibles" : "Agotado",
                11, product.stock > 0 ? MUTED : new Color(176, 76, 63), Font.PLAIN), BorderLayout.SOUTH);
        card.add(icon, BorderLayout.WEST);
        card.add(details, BorderLayout.CENTER);
        if (administrator) {
            JPanel actions = new JPanel(new GridLayout(2, 1, 0, 5));
            actions.setOpaque(false);
            JButton edit = button("Editar", CREAM, GREEN);
            edit.addActionListener(event -> editProduct(product));
            JButton add = button(product.stock > 0 && isReceivingNow() ? "+ Pedido" : "Agotado",
                    product.stock > 0 && isReceivingNow() ? LEMON : BORDER,
                    product.stock > 0 && isReceivingNow() ? GREEN_DARK : MUTED);
            add.setEnabled(product.stock > 0 && isReceivingNow());
            add.addActionListener(event -> addToCart(product));
            actions.add(edit);
            actions.add(add);
            card.add(actions, BorderLayout.EAST);
        } else {
            JButton add = button(product.stock > 0 ? "+" : "×",
                    product.stock > 0 && isReceivingNow() ? LEMON : BORDER,
                    product.stock > 0 && isReceivingNow() ? GREEN_DARK : MUTED);
            add.setPreferredSize(new Dimension(37, 37));
            add.setFont(new Font("SansSerif", Font.BOLD, 18));
            add.setEnabled(product.stock > 0 && isReceivingNow());
            add.setToolTipText(!isReceivingNow() ? "La cafetería no recibe pedidos ahora"
                    : product.stock == 0 ? "Producto agotado" : "Agregar al pedido");
            add.addActionListener(event -> addToCart(product));
            card.add(add, BorderLayout.EAST);
        }
        return card;
    }

    private void addToCart(Product product) {
        if (!isReceivingNow()) {
            showClosedMessage();
            showPage("Menú");
            return;
        }
        int alreadySelected = cart.containsKey(product) ? cart.get(product).quantity : 0;
        if (alreadySelected >= product.stock) {
            JOptionPane.showMessageDialog(frame, "No hay más existencias disponibles de este producto.",
                    "Inventario insuficiente", JOptionPane.WARNING_MESSAGE);
            return;
        }
        JTextField note = new JTextField(22);
        JPanel prompt = new JPanel(new BorderLayout(0, 8));
        prompt.add(label("Instrucciones especiales (opcional)", 13, TEXT, Font.PLAIN), BorderLayout.NORTH);
        prompt.add(note, BorderLayout.CENTER);
        prompt.add(label("Ejemplo: sin hielo, leche deslactosada.", 11, MUTED, Font.PLAIN), BorderLayout.SOUTH);
        int result = JOptionPane.showConfirmDialog(frame, prompt, product.name,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            CartLine current = cart.get(product);
            cart.put(product, new CartLine(alreadySelected + 1,
                    note.getText().trim().isEmpty() && current != null ? current.note : note.getText().trim()));
            showPage("Menú");
        }
    }

    private JPanel buildCartPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 13));
        panel.setBackground(WHITE);
        panel.setPreferredSize(new Dimension(310, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(17, 15, 15, 15)));
        JPanel title = new JPanel(new BorderLayout());
        title.setOpaque(false);
        title.add(label("Tu pedido", 17, TEXT, Font.BOLD), BorderLayout.WEST);
        title.add(label(cart.values().stream().mapToInt(line -> line.quantity).sum() + " artículos",
                11, MUTED, Font.PLAIN), BorderLayout.EAST);
        panel.add(title, BorderLayout.NORTH);
        JPanel lines = new JPanel();
        lines.setOpaque(false);
        lines.setLayout(new javax.swing.BoxLayout(lines, javax.swing.BoxLayout.Y_AXIS));
        if (cart.isEmpty()) {
            lines.add(label("Todavía no agregas productos.", 12, MUTED, Font.PLAIN));
        } else {
            for (Map.Entry<Product, CartLine> entry : cart.entrySet()) {
                lines.add(cartItem(entry.getKey(), entry.getValue()));
                lines.add(Box.createVerticalStrut(6));
            }
        }
        JScrollPane scroll = new JScrollPane(lines);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(WHITE);
        panel.add(scroll, BorderLayout.CENTER);
        JPanel summary = new JPanel(new BorderLayout(0, 10));
        summary.setOpaque(false);
        JPanel total = new JPanel(new BorderLayout());
        total.setOpaque(false);
        total.add(label("Total", 15, TEXT, Font.BOLD), BorderLayout.WEST);
        total.add(label(money(cartTotal()), 18, GREEN, Font.BOLD), BorderLayout.EAST);
        JButton order = button("Continuar con el pedido", GREEN, WHITE);
        order.setPreferredSize(new Dimension(0, 43));
        order.setEnabled(!cart.isEmpty() && isReceivingNow());
        order.addActionListener(event -> placeOrder());
        summary.add(total, BorderLayout.NORTH);
        summary.add(order, BorderLayout.SOUTH);
        panel.add(summary, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel cartItem(Product product, CartLine line) {
        JPanel row = new JPanel(new BorderLayout(4, 5));
        row.setOpaque(false);
        row.setBorder(new EmptyBorder(7, 0, 6, 0));
        JPanel text = new JPanel(new GridLayout(line.note.isBlank() ? 2 : 3, 1, 0, 3));
        text.setOpaque(false);
        text.add(label(line.quantity + " × " + product.name, 12, TEXT, Font.BOLD));
        text.add(label(money(product.price * line.quantity), 11, MUTED, Font.PLAIN));
        if (!line.note.isBlank()) {
            text.add(label("Nota: " + line.note, 10, MUTED, Font.PLAIN));
        }
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        controls.setOpaque(false);
        JButton minus = smallButton("−");
        minus.addActionListener(event -> changeCartQuantity(product, -1));
        JButton plus = smallButton("+");
        plus.setEnabled(line.quantity < product.stock);
        plus.addActionListener(event -> changeCartQuantity(product, 1));
        controls.add(minus);
        controls.add(plus);
        row.add(text, BorderLayout.CENTER);
        row.add(controls, BorderLayout.EAST);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, line.note.isBlank() ? 53 : 65));
        return row;
    }

    private void changeCartQuantity(Product product, int amount) {
        CartLine line = cart.get(product);
        if (line == null) {
            return;
        }
        int quantity = line.quantity + amount;
        if (quantity <= 0) {
            cart.remove(product);
        } else if (quantity <= product.stock) {
            cart.put(product, new CartLine(quantity, line.note));
        }
        showPage("Menú");
    }

    private void placeOrder() {
        if (cart.isEmpty()) {
            return;
        }
        if (!isReceivingNow()) {
            showClosedMessage();
            showPage("Menú");
            return;
        }
        for (Map.Entry<Product, CartLine> entry : cart.entrySet()) {
            if (entry.getValue().quantity > entry.getKey().stock) {
                JOptionPane.showMessageDialog(frame,
                        "La existencia de " + entry.getKey().name + " cambió. Ajusta tu pedido.",
                        "Inventario actualizado", JOptionPane.WARNING_MESSAGE);
                showPage("Menú");
                return;
            }
        }
        JTextField name = new JTextField(22);
        JPanel form = new JPanel(new GridLayout(0, 1, 0, 7));
        form.add(label("¿A nombre de quién registramos el pedido?", 13, TEXT, Font.PLAIN));
        form.add(name);
        form.add(label("Elige el método de pago:", 13, TEXT, Font.PLAIN));
        JComboBox<String> payment = new JComboBox<>(new String[]{"Efectivo", "Tarjeta", "Transferencia"});
        form.add(payment);
        int result = JOptionPane.showConfirmDialog(frame, form, "Confirmar pedido",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        if (name.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Escribe el nombre para identificar tu pedido.",
                    "Falta el nombre", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String orderId = String.format(Locale.ROOT, "CL-%06d", orders.size() + 1);
        String pickupCode = newPickupCode();
        LocalDateTime now = LocalDateTime.now();
        List<OrderLine> lines = new ArrayList<>();
        for (Map.Entry<Product, CartLine> entry : cart.entrySet()) {
            Product product = entry.getKey();
            CartLine line = entry.getValue();
            lines.add(new OrderLine(product, product.name, product.price, line.quantity, line.note));
            product.stock -= line.quantity;
        }
        Order order = new Order(orderId, pickupCode, customerSession, name.getText().trim(),
                (String) payment.getSelectedItem(), now, lines);
        orders.add(order);
        cart.clear();
        showTicket(order);
    }

    private void showTicket(Order order) {
        JLabel title = label("¡Gracias, " + order.customerName + "!", 21, GREEN, Font.BOLD);
        BufferedImage qrImage = createDecorativeCodeImage();
        JDialog dialog = new JDialog(frame, "Pedido confirmado", true);
        JPanel panel = new JPanel(new BorderLayout(0, 14));
        panel.setBackground(WHITE);
        panel.setBorder(new EmptyBorder(22, 24, 22, 24));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        JPanel center = new JPanel(new BorderLayout(0, 9));
        center.setOpaque(false);
        center.add(title, BorderLayout.NORTH);
        center.add(new QrPanel(qrImage), BorderLayout.CENTER);
        JPanel ticket = new JPanel(new GridLayout(0, 1, 0, 6));
        ticket.setOpaque(false);
        JLabel scan = label("TOMA FOTO A ESTO PARA RECOGER TU PEDIDO", 12, TEXT, Font.BOLD);
        scan.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel folio = label("Folio " + order.id, 14, GREEN, Font.BOLD);
        folio.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel code = label("Clave para recoger:  " + order.pickupCode, 17, GREEN_DARK, Font.BOLD);
        code.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel total = label("Total: " + money(order.total()) + "  ·  " + order.paymentMethod,
                12, MUTED, Font.PLAIN);
        total.setHorizontalAlignment(SwingConstants.CENTER);
        ticket.add(scan);
        ticket.add(folio);
        ticket.add(code);
        ticket.add(total);
        center.add(ticket, BorderLayout.SOUTH);
        panel.add(center, BorderLayout.CENTER);
        JButton done = button("Listo", GREEN, WHITE);
        done.addActionListener(event -> dialog.dispose());
        panel.add(done, BorderLayout.SOUTH);
        dialog.setContentPane(panel);
        dialog.setSize(370, 510);
        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
        showPage("Mis pedidos");
    }

    private String newPickupCode() {
        String code;
        do {
            code = String.format(Locale.ROOT, "%06d", random.nextInt(1_000_000));
        } while (pickupCodeExists(code));
        return code;
    }

    private boolean pickupCodeExists(String code) {
        return orders.stream().anyMatch(order -> order.pickupCode.equals(code));
    }

    private JPanel buildCustomerOrders() {
        JPanel page = new JPanel(new BorderLayout(0, 16));
        page.setOpaque(false);
        page.add(pageHeading("Tus pedidos", "Consulta su preparación y conserva tu clave de recolección"),
                BorderLayout.NORTH);
        JPanel list = new JPanel();
        list.setOpaque(false);
        list.setLayout(new javax.swing.BoxLayout(list, javax.swing.BoxLayout.Y_AXIS));
        List<Order> mine = orders.stream().filter(order -> customerSession.equals(order.customerSession))
                .toList();
        if (mine.isEmpty()) {
            list.add(emptyCard("Aún no tienes pedidos. Explora el menú para empezar."));
        }
        for (int i = mine.size() - 1; i >= 0; i--) {
            Order order = mine.get(i);
            if (order.status == OrderStatus.READY) {
                JPanel ready = new JPanel(new BorderLayout());
                ready.setBackground(new Color(226, 242, 219));
                ready.setBorder(new EmptyBorder(13, 15, 13, 15));
                ready.add(label("¡Tu pedido " + order.id + " está listo! Preséntate para recogerlo.",
                        14, GREEN, Font.BOLD));
                list.add(ready);
                list.add(Box.createVerticalStrut(8));
            }
            JPanel card = new JPanel(new BorderLayout(12, 9));
            card.setBackground(WHITE);
            card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),
                    new EmptyBorder(15, 17, 15, 17)));
            JPanel details = new JPanel(new GridLayout(0, 1, 0, 6));
            details.setOpaque(false);
            details.add(label(order.id + "  ·  " + order.createdAt.format(DATE_FORMAT),
                    14, TEXT, Font.BOLD));
            details.add(label(order.linesSummary(), 12, MUTED, Font.PLAIN));
            details.add(label(order.status.label + "  ·  Clave " + order.pickupCode,
                    12, GREEN, Font.BOLD));
            details.add(label(money(order.total()) + "  ·  " + order.paymentMethod,
                    12, MUTED, Font.PLAIN));
            card.add(details, BorderLayout.CENTER);
            if (order.status == OrderStatus.PENDING) {
                JButton cancel = button("Cancelar pedido", new Color(251, 224, 221), new Color(145, 55, 46));
                cancel.addActionListener(event -> cancelOrder(order));
                card.add(cancel, BorderLayout.EAST);
            }
            list.add(card);
            list.add(Box.createVerticalStrut(9));
        }
        JScrollPane scroll = new JScrollPane(list);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CREAM);
        page.add(scroll, BorderLayout.CENTER);
        return page;
    }

    private void cancelOrder(Order order) {
        if (order.status != OrderStatus.PENDING) {
            JOptionPane.showMessageDialog(frame, "Solo se pueden cancelar pedidos pendientes.",
                    "No se puede cancelar", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int answer = JOptionPane.showConfirmDialog(frame,
                "¿Cancelar el pedido " + order.id + "?", "Cancelar pedido", JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            order.status = OrderStatus.CANCELLED;
            for (OrderLine line : order.lines) {
                line.product.stock += line.quantity;
            }
            showPage("Mis pedidos");
        }
    }

    private JPanel buildKanbanPage() {
        JPanel page = new JPanel(new BorderLayout(0, 15));
        page.setOpaque(false);
        page.add(pageHeading("Flujo de cocina", "Mueve cada orden por su estado; al marcarla como lista se notifica al cliente"),
                BorderLayout.NORTH);
        JPanel board = new JPanel(new GridLayout(1, 3, 12, 0));
        board.setOpaque(false);
        board.add(kanbanColumn("Por hacer", OrderStatus.PENDING));
        board.add(kanbanColumn("En preparación", OrderStatus.PREPARING));
        board.add(kanbanColumn("Por recoger", OrderStatus.READY));
        page.add(board, BorderLayout.CENTER);
        return page;
    }

    private JPanel kanbanColumn(String title, OrderStatus status) {
        JPanel column = new JPanel(new BorderLayout(0, 10));
        column.setBackground(new Color(245, 244, 238));
        column.setBorder(new EmptyBorder(13, 11, 13, 11));
        long count = orders.stream().filter(order -> order.status == status).count();
        JLabel heading = label(title + "  ·  " + count, 15, TEXT, Font.BOLD);
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.add(heading, BorderLayout.WEST);
        JPanel cards = new JPanel();
        cards.setOpaque(false);
        cards.setLayout(new javax.swing.BoxLayout(cards, javax.swing.BoxLayout.Y_AXIS));
        for (int i = orders.size() - 1; i >= 0; i--) {
            Order order = orders.get(i);
            if (order.status == status) {
                cards.add(kanbanCard(order));
                cards.add(Box.createVerticalStrut(9));
            }
        }
        if (count == 0) {
            cards.add(label("Sin pedidos en esta etapa.", 12, MUTED, Font.PLAIN));
        }
        JScrollPane scroll = new JScrollPane(cards);
        scroll.setBorder(null);
        scroll.getViewport().setOpaque(false);
        column.add(header, BorderLayout.NORTH);
        column.add(scroll, BorderLayout.CENTER);
        return column;
    }

    private JPanel kanbanCard(Order order) {
        JPanel card = new JPanel(new BorderLayout(0, 9));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 4, 0, 0, statusColors.get(order.status)),
                BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),
                        new EmptyBorder(12, 12, 12, 12))));
        JPanel details = new JPanel(new GridLayout(0, 1, 0, 5));
        details.setOpaque(false);
        details.add(label(order.id + "  ·  " + order.customerName, 13, TEXT, Font.BOLD));
        details.add(label(order.createdAt.format(DATE_FORMAT), 10, MUTED, Font.PLAIN));
        details.add(label(order.linesSummary(), 11, TEXT, Font.PLAIN));
        details.add(label("Total " + money(order.total()) + "  ·  " + order.paymentMethod,
                11, GREEN, Font.BOLD));
        if (order.status == OrderStatus.READY) {
            details.add(label("Clave: " + order.pickupCode, 12, GREEN, Font.BOLD));
        }
        card.add(details, BorderLayout.CENTER);
        JButton next = button(nextActionLabel(order.status), LEMON, GREEN_DARK);
        next.setEnabled(order.status == OrderStatus.PENDING || order.status == OrderStatus.PREPARING
                || order.status == OrderStatus.READY);
        next.addActionListener(event -> advanceOrder(order));
        card.add(next, BorderLayout.SOUTH);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));
        return card;
    }

    private String nextActionLabel(OrderStatus status) {
        return switch (status) {
            case PENDING -> "Iniciar preparación →";
            case PREPARING -> "Marcar listo →";
            case READY -> "Marcar entregado";
            default -> "Pedido cerrado";
        };
    }

    private void advanceOrder(Order order) {
        if (order.status == OrderStatus.PENDING) {
            order.status = OrderStatus.PREPARING;
            order.preparationStartedAt = LocalDateTime.now();
        } else if (order.status == OrderStatus.PREPARING) {
            order.status = OrderStatus.READY;
            order.readyAt = LocalDateTime.now();
        } else if (order.status == OrderStatus.READY) {
            order.status = OrderStatus.DELIVERED;
        } else {
            return;
        }
        showPage("Tablero");
    }

    private JPanel buildProductsPage() {
        JPanel page = new JPanel(new BorderLayout(0, 16));
        page.setOpaque(false);
        JPanel heading = new JPanel(new BorderLayout(10, 0));
        heading.setOpaque(false);
        heading.add(pageHeading("Productos e inventario", "Actualiza precios, descripciones y existencias"),
                BorderLayout.CENTER);
        JButton add = button("+ Agregar producto", GREEN, WHITE);
        add.addActionListener(event -> editProduct(null));
        heading.add(add, BorderLayout.EAST);
        page.add(heading, BorderLayout.NORTH);
        JPanel rows = new JPanel();
        rows.setOpaque(false);
        rows.setLayout(new javax.swing.BoxLayout(rows, javax.swing.BoxLayout.Y_AXIS));
        for (Product product : products) {
            JPanel row = new JPanel(new BorderLayout(12, 0));
            row.setBackground(WHITE);
            row.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),
                    new EmptyBorder(13, 16, 13, 16)));
            JPanel details = new JPanel(new GridLayout(2, 1, 0, 5));
            details.setOpaque(false);
            details.add(label(product.name + "  ·  " + product.category, 14, TEXT, Font.BOLD));
            details.add(label(product.description + "  ·  " + product.stock + " unidades",
                    11, MUTED, Font.PLAIN));
            row.add(details, BorderLayout.CENTER);
            row.add(label(money(product.price), 15, GREEN, Font.BOLD), BorderLayout.EAST);
            JButton edit = button("Editar", CREAM, GREEN);
            edit.addActionListener(event -> editProduct(product));
            row.add(edit, BorderLayout.WEST);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 76));
            rows.add(row);
            rows.add(Box.createVerticalStrut(8));
        }
        JScrollPane scroll = new JScrollPane(rows);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(CREAM);
        page.add(scroll, BorderLayout.CENTER);
        return page;
    }

    private void editProduct(Product product) {
        JTextField name = new JTextField(product == null ? "" : product.name, 20);
        JComboBox<String> category = new JComboBox<>(CATEGORIES);
        JTextField description = new JTextField(product == null ? "" : product.description, 20);
        JTextField price = new JTextField(product == null ? "" : String.valueOf(product.price), 10);
        JSpinner stock = new JSpinner(new SpinnerNumberModel(product == null ? 0 : product.stock, 0, 100000, 1));
        if (product != null) {
            category.setSelectedItem(product.category);
        }
        JPanel form = new JPanel(new GridLayout(0, 2, 9, 9));
        form.add(label("Nombre", 12, TEXT, Font.PLAIN));
        form.add(name);
        form.add(label("Categoría", 12, TEXT, Font.PLAIN));
        form.add(category);
        form.add(label("Descripción", 12, TEXT, Font.PLAIN));
        form.add(description);
        form.add(label("Precio", 12, TEXT, Font.PLAIN));
        form.add(price);
        form.add(label("Existencias", 12, TEXT, Font.PLAIN));
        form.add(stock);
        int result = JOptionPane.showConfirmDialog(frame, form,
                product == null ? "Nuevo producto" : "Editar producto",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        double parsedPrice;
        try {
            parsedPrice = Double.parseDouble(price.getText().trim());
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(frame, "Escribe un precio válido.",
                    "Datos inválidos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (name.getText().trim().isEmpty() || description.getText().trim().isEmpty()
                || !Double.isFinite(parsedPrice) || parsedPrice <= 0) {
            JOptionPane.showMessageDialog(frame, "Completa nombre y descripción, e ingresa un precio mayor a cero.",
                    "Datos inválidos", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (product == null) {
            products.add(new Product(name.getText().trim(), (String) category.getSelectedItem(),
                    description.getText().trim(), parsedPrice, (Integer) stock.getValue(),
                    initials(name.getText().trim())));
        } else {
            product.name = name.getText().trim();
            product.category = (String) category.getSelectedItem();
            product.description = description.getText().trim();
            product.price = parsedPrice;
            product.stock = (Integer) stock.getValue();
            product.initials = initials(product.name);
        }
        showPage(currentPage);
    }

    private JPanel buildReportsPage() {
        JPanel page = new JPanel(new BorderLayout(0, 18));
        page.setOpaque(false);
        JPanel heading = new JPanel(new BorderLayout(10, 0));
        heading.setOpaque(false);
        heading.add(pageHeading("Corte de caja e indicadores", "Ventas registradas hoy y tiempos de preparación"),
                BorderLayout.CENTER);
        JButton export = button("Exportar CSV", GREEN, WHITE);
        export.addActionListener(event -> exportReport());
        heading.add(export, BorderLayout.EAST);
        page.add(heading, BorderLayout.NORTH);
        JPanel content = new JPanel(new BorderLayout(0, 16));
        content.setOpaque(false);
        List<Order> today = ordersToday();
        double gross = today.stream().filter(order -> order.status != OrderStatus.CANCELLED)
                .mapToDouble(Order::total).sum();
        double averagePrep = today.stream().filter(order -> order.preparationStartedAt != null
                        && order.readyAt != null)
                .mapToLong(order -> Duration.between(order.preparationStartedAt, order.readyAt).toMinutes())
                .average().orElse(0);
        JPanel stats = new JPanel(new GridLayout(1, 3, 12, 0));
        stats.setOpaque(false);
        stats.add(statCard("Ventas de hoy", money(gross), "Se excluyen pedidos cancelados"));
        stats.add(statCard("Tiempo promedio", String.format(Locale.ROOT, "%.0f min", averagePrep),
                "Pedidos con preparación completada"));
        stats.add(statCard("Pedidos de hoy", String.valueOf(today.size()), "Todos los estados"));
        JPanel payments = new JPanel(new BorderLayout(0, 10));
        payments.setBackground(WHITE);
        payments.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(17, 18, 17, 18)));
        payments.add(label("Corte de caja de hoy por método de pago", 16, TEXT, Font.BOLD), BorderLayout.NORTH);
        JPanel rows = new JPanel(new GridLayout(0, 2, 10, 8));
        rows.setOpaque(false);
        for (String method : new String[]{"Efectivo", "Tarjeta", "Transferencia"}) {
            double sum = today.stream().filter(order -> order.paymentMethod.equals(method)
                            && order.status != OrderStatus.CANCELLED)
                    .mapToDouble(Order::total).sum();
            long count = today.stream().filter(order -> order.paymentMethod.equals(method)
                    && order.status != OrderStatus.CANCELLED).count();
            rows.add(label(method + "  ·  " + count + " pedidos", 13, MUTED, Font.PLAIN));
            rows.add(label(money(sum), 13, GREEN, Font.BOLD));
        }
        payments.add(rows, BorderLayout.CENTER);
        JPanel list = buildRecentOrders(1000);
        content.add(stats, BorderLayout.NORTH);
        content.add(payments, BorderLayout.CENTER);
        content.add(list, BorderLayout.SOUTH);
        page.add(content, BorderLayout.CENTER);
        return page;
    }

    private JPanel buildSettingsPage() {
        JPanel page = new JPanel(new BorderLayout(0, 18));
        page.setOpaque(false);
        page.add(pageHeading("Configuración de atención", "Los pedidos solo se aceptan durante el horario habilitado"),
                BorderLayout.NORTH);
        JPanel settings = new JPanel(new GridLayout(0, 1, 0, 16));
        settings.setBackground(WHITE);
        settings.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(22, 22, 22, 22)));
        JCheckBox enabled = new JCheckBox("Recibir pedidos", receivingOrders);
        enabled.setOpaque(false);
        JPanel hours = new JPanel(new FlowLayout(FlowLayout.LEFT, 9, 0));
        hours.setOpaque(false);
        JTextField opening = new JTextField(openingTime, 6);
        JTextField closing = new JTextField(closingTime, 6);
        hours.add(label("Desde", 13, TEXT, Font.PLAIN));
        hours.add(opening);
        hours.add(label("Hasta", 13, TEXT, Font.PLAIN));
        hours.add(closing);
        JLabel info = label("El catálogo y el botón para pedir se deshabilitan cuando el local está cerrado.",
                12, MUTED, Font.PLAIN);
        JButton save = button("Guardar horario", GREEN, WHITE);
        save.addActionListener(event -> {
            try {
                LocalTime.parse(opening.getText().trim(), TIME_FORMAT);
                LocalTime.parse(closing.getText().trim(), TIME_FORMAT);
            } catch (RuntimeException exception) {
                JOptionPane.showMessageDialog(frame, "Usa el formato de 24 horas HH:mm, por ejemplo 08:00.",
                        "Horario inválido", JOptionPane.WARNING_MESSAGE);
                return;
            }
            receivingOrders = enabled.isSelected();
            openingTime = opening.getText().trim();
            closingTime = closing.getText().trim();
            JOptionPane.showMessageDialog(frame, "Horario y recepción de pedidos actualizados.",
                    "Configuración guardada", JOptionPane.INFORMATION_MESSAGE);
            showPage("Configuración");
        });
        settings.add(enabled);
        settings.add(hours);
        settings.add(info);
        settings.add(save);
        page.add(settings, BorderLayout.NORTH);
        return page;
    }

    private void showClosedMessage() {
        JOptionPane.showMessageDialog(frame, "No recibimos pedidos en este momento.\n"
                        + (receivingOrders ? "Horario: " + openingTime + "–" + closingTime : "Recepción deshabilitada."),
                "Cafetería cerrada", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exportReport() {
        javax.swing.JFileChooser chooser = new javax.swing.JFileChooser();
        chooser.setDialogTitle("Guardar reporte diario de ventas");
        chooser.setSelectedFile(new java.io.File("reporte-diario-cafeteria-limon.csv"));
        if (chooser.showSaveDialog(frame) != javax.swing.JFileChooser.APPROVE_OPTION) {
            return;
        }
        java.nio.file.Path destination = chooser.getSelectedFile().toPath();
        try (java.io.BufferedWriter writer = java.nio.file.Files.newBufferedWriter(destination,
                java.nio.charset.StandardCharsets.UTF_8)) {
            writer.write("Folio,Cliente,Fecha,Estado,Metodo de pago,Total,Productos");
            writer.newLine();
            for (Order order : ordersToday()) {
                writer.write(csv(order.id) + "," + csv(order.customerName) + ","
                        + csv(order.createdAt.format(DATE_FORMAT)) + "," + csv(order.status.label) + ","
                        + csv(order.paymentMethod) + "," + String.format(Locale.ROOT, "%.2f", order.total())
                        + "," + csv(order.linesSummary()));
                writer.newLine();
            }
        } catch (java.io.IOException exception) {
            JOptionPane.showMessageDialog(frame, "No se pudo guardar el reporte: " + exception.getMessage(),
                    "Error al exportar", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(frame, "Reporte diario guardado en:\n" + destination,
                "Reporte exportado", JOptionPane.INFORMATION_MESSAGE);
    }

    private String csv(String value) {
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private boolean isReceivingNow() {
        if (!receivingOrders) {
            return false;
        }
        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.parse(openingTime, TIME_FORMAT);
        LocalTime end = LocalTime.parse(closingTime, TIME_FORMAT);
        if (start.equals(end)) {
            return true;
        }
        return start.isBefore(end)
                ? !now.isBefore(start) && now.isBefore(end)
                : !now.isBefore(start) || now.isBefore(end);
    }

    private String openingLabel() {
        return receivingOrders
                ? (isReceivingNow() ? "Pedidos abiertos" : "Cerrado ahora")
                        + "  ·  " + openingTime + "–" + closingTime
                : "Recepción de pedidos cerrada";
    }

    private JPanel orderSummaryRow(Order order, boolean showCustomer) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(WHITE);
        row.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(12, 15, 12, 15)));
        JPanel left = new JPanel(new GridLayout(0, 1, 0, 5));
        left.setOpaque(false);
        left.add(label(order.id + (showCustomer ? "  ·  " + order.customerName : ""),
                13, TEXT, Font.BOLD));
        left.add(label(order.createdAt.format(DATE_FORMAT) + "  ·  " + order.linesSummary(),
                11, MUTED, Font.PLAIN));
        row.add(left, BorderLayout.CENTER);
        JLabel status = label(order.status.label + "  ·  " + money(order.total()),
                12, GREEN, Font.BOLD);
        row.add(status, BorderLayout.EAST);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        return row;
    }

    private JPanel emptyCard(String message) {
        JPanel empty = new JPanel(new BorderLayout());
        empty.setBackground(WHITE);
        empty.setBorder(new EmptyBorder(19, 17, 19, 17));
        empty.add(label(message, 13, MUTED, Font.PLAIN));
        empty.setMaximumSize(new Dimension(Integer.MAX_VALUE, 65));
        return empty;
    }

    private JPanel statCard(String title, String value, String detail) {
        JPanel card = new JPanel(new GridLayout(3, 1, 0, 5));
        card.setBackground(WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(BORDER),
                new EmptyBorder(14, 14, 13, 14)));
        card.add(label(title, 11, MUTED, Font.PLAIN));
        card.add(label(value, 20, TEXT, Font.BOLD));
        card.add(label(detail, 10, MUTED, Font.PLAIN));
        return card;
    }

    private JPanel pageHeading(String title, String subtitle) {
        JPanel heading = new JPanel(new GridLayout(2, 1, 0, 5));
        heading.setOpaque(false);
        heading.add(label(title, 25, TEXT, Font.BOLD));
        heading.add(label(subtitle, 12, MUTED, Font.PLAIN));
        return heading;
    }

    private JButton smallButton(String text) {
        JButton result = button(text, CREAM, GREEN);
        result.setPreferredSize(new Dimension(25, 25));
        result.setFont(new Font("SansSerif", Font.BOLD, 13));
        return result;
    }

    private JButton button(String text, Color background, Color foreground) {
        JButton result = new JButton(text);
        result.setFont(new Font("SansSerif", Font.BOLD, 12));
        result.setForeground(foreground);
        result.setBackground(background);
        result.setFocusPainted(false);
        result.setBorder(new EmptyBorder(10, 13, 10, 13));
        result.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return result;
    }

    private JLabel label(String text, int size, Color color, int style) {
        JLabel result = new JLabel(text);
        result.setFont(new Font("SansSerif", style, size));
        result.setForeground(color);
        return result;
    }

    private double cartTotal() {
        return cart.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().price * entry.getValue().quantity).sum();
    }

    private String money(double amount) {
        return NumberFormat.getCurrencyInstance(new Locale("es", "MX")).format(amount);
    }

    private String initials(String name) {
        String[] words = name.trim().split("\\s+");
        return words.length == 1 ? words[0].substring(0, Math.min(2, words[0].length())).toUpperCase(Locale.ROOT)
                : (words[0].substring(0, 1) + words[1].substring(0, 1)).toUpperCase(Locale.ROOT);
    }

    private boolean terminal(OrderStatus status) {
        return status == OrderStatus.DELIVERED || status == OrderStatus.CANCELLED;
    }

    private List<Order> ordersToday() {
        return orders.stream().filter(order -> order.createdAt.toLocalDate().equals(LocalDate.now())).toList();
    }

    private BufferedImage createDecorativeCodeImage() {
        int modules = 29;
        int moduleSize = 8;
        int quietZone = 4;
        int size = (modules + quietZone * 2) * moduleSize;
        BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, size, size);
        graphics.setColor(Color.BLACK);
        for (int y = 0; y < modules; y++) {
            for (int x = 0; x < modules; x++) {
                if (inFinderPattern(x, y, modules)) {
                    continue;
                }
                if (random.nextBoolean()) {
                    graphics.fillRect((x + quietZone) * moduleSize, (y + quietZone) * moduleSize,
                            moduleSize, moduleSize);
                }
            }
        }
        drawFinderPattern(graphics, quietZone * moduleSize, quietZone * moduleSize, moduleSize);
        drawFinderPattern(graphics, (quietZone + modules - 7) * moduleSize,
                quietZone * moduleSize, moduleSize);
        drawFinderPattern(graphics, quietZone * moduleSize,
                (quietZone + modules - 7) * moduleSize, moduleSize);
        graphics.dispose();
        return image;
    }

    private boolean inFinderPattern(int x, int y, int modules) {
        return (x < 7 && y < 7) || (x >= modules - 7 && y < 7)
                || (x < 7 && y >= modules - 7);
    }

    private void drawFinderPattern(Graphics2D graphics, int left, int top, int moduleSize) {
        graphics.setColor(Color.BLACK);
        graphics.fillRect(left, top, 7 * moduleSize, 7 * moduleSize);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(left + moduleSize, top + moduleSize, 5 * moduleSize, 5 * moduleSize);
        graphics.setColor(Color.BLACK);
        graphics.fillRect(left + 2 * moduleSize, top + 2 * moduleSize, 3 * moduleSize, 3 * moduleSize);
    }

    private static final class Product {
        private String name;
        private String category;
        private String description;
        private double price;
        private int stock;
        private String initials;

        private Product(String name, String category, String description, double price, int stock, String initials) {
            this.name = name;
            this.category = category;
            this.description = description;
            this.price = price;
            this.stock = stock;
            this.initials = initials;
        }
    }

    private static final class CartLine {
        private final int quantity;
        private final String note;

        private CartLine(int quantity, String note) {
            this.quantity = quantity;
            this.note = note;
        }
    }

    private static final class OrderLine {
        private final Product product;
        private final String name;
        private final double unitPrice;
        private final int quantity;
        private final String note;

        private OrderLine(Product product, String name, double unitPrice, int quantity, String note) {
            this.product = product;
            this.name = name;
            this.unitPrice = unitPrice;
            this.quantity = quantity;
            this.note = note;
        }
    }

    private enum OrderStatus {
        PENDING("Por hacer"),
        PREPARING("En preparación"),
        READY("Por recoger"),
        DELIVERED("Entregado"),
        CANCELLED("Cancelado");

        private final String label;

        OrderStatus(String label) {
            this.label = label;
        }
    }

    private static final class Order {
        private final String id;
        private final String pickupCode;
        private final String customerSession;
        private final String customerName;
        private final String paymentMethod;
        private final LocalDateTime createdAt;
        private final List<OrderLine> lines;
        private OrderStatus status = OrderStatus.PENDING;
        private LocalDateTime preparationStartedAt;
        private LocalDateTime readyAt;

        private Order(String id, String pickupCode, String customerSession, String customerName,
                String paymentMethod, LocalDateTime createdAt, List<OrderLine> lines) {
            this.id = id;
            this.pickupCode = pickupCode;
            this.customerSession = customerSession;
            this.customerName = customerName;
            this.paymentMethod = paymentMethod;
            this.createdAt = createdAt;
            this.lines = List.copyOf(lines);
        }

        private double total() {
            return lines.stream().mapToDouble(line -> line.unitPrice * line.quantity).sum();
        }

        private String linesSummary() {
            return lines.stream().map(line -> line.quantity + " × " + line.name
                            + (line.note.isBlank() ? "" : " (" + line.note + ")"))
                    .reduce((first, second) -> first + ", " + second).orElse("Sin productos");
        }
    }

    private static final class QrPanel extends JPanel {
        private static final long serialVersionUID = 1L;
        private final transient BufferedImage image;

        private QrPanel(BufferedImage image) {
            this.image = image;
            setOpaque(false);
            setPreferredSize(new Dimension(240, 240));
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            super.paintComponent(graphics);
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            int size = Math.min(getWidth(), getHeight()) - 10;
            int x = (getWidth() - size) / 2;
            int y = (getHeight() - size) / 2;
            g.drawImage(image, x, y, size, size, null);
            g.dispose();
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

}
