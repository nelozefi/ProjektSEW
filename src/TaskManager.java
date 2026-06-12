import java.util.ArrayList;

/**
 * Das Gehirn der App: Hier werden Aufgaben gespeichert und gefiltert.
 * @author Antonelo
 */
public class TaskManager {

    // Eine kleine Hilfsklasse: Jede Aufgabe hat einen Text, eine Wichtigkeit und eine Kategorie
    public static class Task {
        private String description; // Der Text der Aufgabe
        private String priority;    // "High", "Medium" oder "Low"
        private String category;    // "Code", "Design", "Docs" oder "Testing"
        private boolean isDone;     // Zeigt an, ob die Aufgabe fertig ist (true) oder nicht (false)

        public Task(String description, String priority, String category) {
            this.description = description;
            this.priority = priority;
            this.category = category;
            this.isDone = false; // Neue Aufgaben sind am Anfang nie fertig
        }

        public String getDescription() { return description; }
        public String getPriority() { return priority; }
        public String getCategory() { return category; }
        public boolean isDone() { return isDone; }
        public void setDone(boolean done) { this.isDone = done; }

        // Bestimmt, wie die Aufgabe in der Liste auf dem Bildschirm Text-mäßig aussieht
        @Override
        public String toString() {
            String statusSymbol = isDone ? "✅ " : "⏳ ";
            return statusSymbol + "[" + priority + "] (" + category + ") " + description;
        }
    }

    // Die Hauptliste, in der alle Aufgaben auf dem Computer gespeichert werden
    private ArrayList<Task> taskList;

    public TaskManager() {
        this.taskList = new ArrayList<>();
    }

    // Fügt eine neue Aufgabe zur Liste hinzu (aber nur, wenn das Textfeld nicht leer ist)
    public void addTask(String desc, String priority, String category) {
        if (desc != null && !desc.trim().isEmpty()) {
            taskList.add(new Task(desc, priority, category));
        }
    }

    // Löscht eine Aufgabe komplett aus der Liste
    public void removeTask(Task task) {
        taskList.remove(task);
    }

    // Filter-Funktion: Sucht nur die Aufgaben heraus, die zu den ausgewählten Filtern passen
    public ArrayList<Task> getFilteredTasks(String statusFilter, String priorityFilter) {
        ArrayList<Task> result = new ArrayList<>();
        for (Task t : taskList) {
            // Checkt den Status (Alle, Aktiv oder Fertig)
            boolean matchesStatus = statusFilter.equals("All Tasks") ||
                    (statusFilter.equals("Active ⏳") && !t.isDone()) ||
                    (statusFilter.equals("Completed ✅") && t.isDone());

            // Checkt die Priorität (Alle, High, Medium oder Low)
            boolean matchesPriority = priorityFilter.equals("All Priorities") ||
                    t.getPriority().equalsIgnoreCase(priorityFilter);

            // Wenn beides passt, kommt die Aufgabe in die gefilterte Auswahl
            if (matchesStatus && matchesPriority) {
                result.add(t);
            }
        }
        return result;
    }

    // Prozent-Rechner für den Ladebalken: Wie viel Prozent der Aufgaben sind abgehakt?
    public int getCompletionPercentage() {
        if (taskList.isEmpty()) return 0;
        int completed = 0;
        for (Task t : taskList) {
            if (t.isDone()) completed++;
        }
        return (completed * 100) / taskList.size();
    }

    // Gibt einfach die Gesamtzahl aller Aufgaben zurück
    public int getTotalCount() { return taskList.size(); }
}