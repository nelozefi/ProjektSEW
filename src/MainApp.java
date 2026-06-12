import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Die Benutzeroberfläche: Knöpfe, Listen und das bunte Dashboard-Design.
 * @author Matia
 */
public class MainApp {
    private JFrame frame;
    private DefaultListModel<TaskManager.Task> listModel;
    private JList<TaskManager.Task> visualTaskList;
    private JTextField inputField;
    private JComboBox<String> priorityDropdown;
    private JComboBox<String> categoryDropdown;
    private JComboBox<String> statusFilter;
    private JComboBox<String> priorityFilter;

    // Elemente für die Live-Statistik oben im Fenster
    private JLabel totalTasksLabel;
    private JLabel progressLabel;
    private JProgressBar progressBar;

    private TaskManager manager; // Verbindung zu Antonelos Backend

    public static void main(String[] args) {
        // Macht, dass das Fenster auf Windows/Mac moderner aussieht als das alte Java-Design
        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch(Exception ignored) {}
        SwingUtilities.invokeLater(() -> new MainApp().initialize());
    }

    public void initialize() {
        manager = new TaskManager();
        frame = new JFrame("🚀 DevTeam Project Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(650, 550);
        frame.setMinimumSize(new Dimension(550, 450));

        // --- OBERER BEREICH: DIE LIVE-STATISTIK ---
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        statsPanel.setBackground(new Color(240, 244, 248)); // Heller, moderner Graublau-Ton
        statsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, Color.LIGHT_GRAY),
                new EmptyBorder(10, 15, 10, 15)));

        totalTasksLabel = new JLabel("Total Tasks: 0");
        totalTasksLabel.setFont(new Font("Arial", Font.BOLD, 13));

        progressLabel = new JLabel("Progress: 0%");
        progressLabel.setFont(new Font("Arial", Font.BOLD, 13));

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true); // Prozentzahl im Ladebalken anzeigen

        statsPanel.add(totalTasksLabel);
        statsPanel.add(progressLabel);
        statsPanel.add(progressBar);

        // --- MITTLERER BEREICH: FILTER-BOXEN UND DIE TASK-LISTE ---
        JPanel centerPanel = new JPanel(new BorderLayout(0, 10));
        centerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Filter-Zeile erstellen
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statusFilter = new JComboBox<>(new String[]{"All Tasks", "Active ⏳", "Completed ✅"});
        priorityFilter = new JComboBox<>(new String[]{"All Priorities", "High", "Medium", "Low"});
        filterBar.add(new JLabel("Filter Status:"));
        filterBar.add(statusFilter);
        filterBar.add(new JLabel("Priority:"));
        filterBar.add(priorityFilter);

        // Die sichtbare Liste stylen
        listModel = new DefaultListModel<>();
        visualTaskList = new JList<>(listModel);
        visualTaskList.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        visualTaskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        visualTaskList.setFixedCellHeight(35); // Jede Zeile bekommt mehr Platz

        // Extra-Feature: Doppel-Klick auf eine Aufgabe markiert sie sofort als ERLEDIGT!
        visualTaskList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TaskManager.Task selected = visualTaskList.getSelectedValue();
                    if (selected != null) {
                        selected.setDone(!selected.isDone()); // Status umdrehen
                        refreshUI(); // Bildschirm aktualisieren
                    }
                }
            }
        });

        // Farb-Maler: Färbt fertige Aufgaben grün und wichtige Aufgaben rot
        visualTaskList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof TaskManager.Task) {
                    TaskManager.Task task = (TaskManager.Task) value;
                    setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230))); // Dünne Trennlinie

                    if (isSelected) {
                        setBackground(new Color(180, 210, 245)); // Farbe wenn man draufklickt
                        setForeground(Color.BLACK);
                    } else {
                        // Wenn fertig -> hellgrüner Hintergrund, sonst weiß
                        setBackground(task.isDone() ? new Color(235, 247, 235) : Color.WHITE);

                        // Wenn "High" und nicht fertig -> Text wird dunkelrot als Warnung
                        if ("High".equalsIgnoreCase(task.getPriority()) && !task.isDone()) {
                            setForeground(new Color(180, 20, 20));
                        } else {
                            setForeground(Color.DARK_GRAY);
                        }
                    }
                }
                return c;
            }
        });

        centerPanel.add(filterBar, BorderLayout.NORTH);
        centerPanel.add(new JScrollPane(visualTaskList), BorderLayout.CENTER);

        // --- UNTERER BEREICH: EINGABEFELDER UND BUTTONS ---
        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, Color.LIGHT_GRAY),
                new EmptyBorder(15, 15, 15, 15)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        inputField = new JTextField();
        priorityDropdown = new JComboBox<>(new String[]{"High", "Medium", "Low"});
        categoryDropdown = new JComboBox<>(new String[]{"Code 💻", "Design 🎨", "Docs 📝", "Testing 🧪"});

        // Schicker grüner "Erstellen"-Button
        JButton addButton = new JButton("➕ Create Task");
        addButton.setBackground(new Color(40, 160, 80));
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("Arial", Font.BOLD, 12));

        JButton completeButton = new JButton("✓ Toggle Done");
        JButton deleteButton = new JButton("🗑 Delete Selected");
        deleteButton.setForeground(new Color(150, 30, 30)); // Rote Schrift fürs Löschen

        // Die Elemente ordentlich in Reihen unten nebeneinander setzen
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        bottomPanel.add(inputField, gbc);
        gbc.gridx = 1; gbc.weightx = 0.0;
        bottomPanel.add(priorityDropdown, gbc);
        gbc.gridx = 2;
        bottomPanel.add(categoryDropdown, gbc);
        gbc.gridx = 3;
        bottomPanel.add(addButton, gbc);

        JPanel actionButtonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        actionButtonPanel.add(completeButton);
        actionButtonPanel.add(deleteButton);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4; gbc.weightx = 1.0;
        bottomPanel.add(actionButtonPanel, gbc);

        // --- AKTIONEN BEI BUTTON-KLICKS ---

        // Wenn man auf "Create Task" klickt
        addButton.addActionListener(e -> {
            String desc = inputField.getText();
            String priority = (String) priorityDropdown.getSelectedItem();
            String category = (String) categoryDropdown.getSelectedItem();
            if(!desc.trim().isEmpty()){
                manager.addTask(desc, priority, category); // Schickt es zu Antonelos Code
                refreshUI(); // Bild neu zeichnen
                inputField.setText(""); // Textfeld leeren
            }
        });

        // Wenn man auf "Toggle Done" klickt (Abhaken)
        completeButton.addActionListener(e -> {
            TaskManager.Task selected = visualTaskList.getSelectedValue();
            if (selected != null) {
                selected.setDone(!selected.isDone());
                refreshUI();
            }
        });

        // Wenn man auf "Delete" klickt
        deleteButton.addActionListener(e -> {
            TaskManager.Task selected = visualTaskList.getSelectedValue();
            if (selected != null) {
                manager.removeTask(selected);
                refreshUI();
            }
        });

        // Wenn man oben die Filter-Dropdowns umschaltet, soll sich die Liste sofort anpassen
        statusFilter.addActionListener(e -> refreshUI());
        priorityFilter.addActionListener(e -> refreshUI());

        // Alles im Hauptfenster verankern und sichtbar machen
        frame.getContentPane().add(BorderLayout.NORTH, statsPanel);
        frame.getContentPane().add(BorderLayout.CENTER, centerPanel);
        frame.getContentPane().add(BorderLayout.SOUTH, bottomPanel);
        frame.setLocationRelativeTo(null); // Fenster in der Bildschirm-Mitte öffnen
        frame.setVisible(true);

        refreshUI(); // Einmal am Start alles sauber laden
    }

    /**
     * Holt die gefilterten Aufgaben aus dem Backend und berechnet die Statistik-Zahlen neu.
     */
    private void refreshUI() {
        listModel.clear(); // Alte Listenansicht löschen
        String activeStatus = (String) statusFilter.getSelectedItem();
        String activePriority = (String) priorityFilter.getSelectedItem();

        // Holt die passenden Aufgaben über Antonelos Filter-Schleife
        for (TaskManager.Task t : manager.getFilteredTasks(activeStatus, activePriority)) {
            listModel.addElement(t);
        }

        // Live-Statistiken oben updaten
        totalTasksLabel.setText("Total Tasks: " + manager.getTotalCount());
        int pct = manager.getCompletionPercentage();
        progressLabel.setText("Progress: " + pct + "%");
        progressBar.setValue(pct); // Ladebalken anpassen
    }
}