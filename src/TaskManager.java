import java.util.ArrayList;

/**
 * Verwaltet eine Liste von Aufgaben und ermöglicht die Filterung nach Prioritätsstufen.
 * Verantwortet für die zentrale Backend-Logik der Anwendung.
 * @author Antonelo
 */
public class TaskManager {

    /**
     * Eine innere Klasse, die ein einzelnes Task-Element darstellt.
     * Dadurch erhält unser Projekt eine ordentliche objektorientierte Struktur.
     */
    public static class Task {
        private String description;
        private String priority; // "High", "Medium", oder "Low"

        public Task(String description, String priority) {
            this.description = description;
            this.priority = priority;
        }

        public String getDescription() { return description; }
        public String getPriority() { return priority; }

        /** Overriding toString, damit die Liste den Text korrekt anzeigen kann */
        @Override
        public String toString() {
            return "[" + priority + "] " + description;
        }
    }

    /** Die interne Datenarray-Liste, in der alle Task-Objekte gespeichert sind */
    private ArrayList<Task> taskList;

    /**
     * Konstruktor zur Initialisierung des internen Task-Datenbank-Trackers.
     */
    public TaskManager() {
        this.taskList = new ArrayList<>();
    }

    /**
     * Erstellt eine neue Aufgabe und fügt sie dem Array-Listen-Tracking-System hinzu.
     * @param desc Die Beschreibung enthält Einzelheiten zur Aufgabe.
     * @param priority Der Wichtigkeitsgrad ("High", "Medium", "Low").
     */
    public void addTask(String desc, String priority) {
        if (desc != null && !desc.trim().isEmpty()) {
            taskList.add(new Task(desc, priority));
        }
    }

    /**
     * Entfernt eine Aufgabe direkt aus der Hauptliste.
     * @param taskToRemove Die konkrete Instanz des Task-Objekts, die gelöscht werden soll.
     */
    public void removeTask(Task taskToRemove) {
        taskList.remove(taskToRemove);
    }

    /**
     * Durchläuft alle Elemente und filtert sie nach Priorität.
     * @param targetPriority Die Filterung ("All", "High", "Medium", "Low").
     * @return Eine bereinigte, gefilterte Teilmenge von Task-Elementen.
     */
    public ArrayList<Task> getFilteredTasks(String targetPriority) {
        if (targetPriority.equals("All")) {
            return taskList;
        }

        ArrayList<Task> filteredResult = new ArrayList<>();
        for (Task t : taskList) {
            if (t.getPriority().equalsIgnoreCase(targetPriority)) {
                filteredResult.add(t);
            }
        }
        return filteredResult;
    }
}