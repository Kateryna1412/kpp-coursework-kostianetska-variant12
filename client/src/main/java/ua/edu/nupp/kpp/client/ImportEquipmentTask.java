package ua.edu.nupp.kpp.client;

import javafx.concurrent.Task;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ImportEquipmentTask extends Task<Integer> {
    private static final Logger LOG = Logger.getLogger(ImportEquipmentTask.class.getName());
    private static final String EXPECTED_HEADER = "name;category;rental_rate";

    private final Path file;
    private final HttpEquipmentRentalClient api;
    private final List<String> warnings = new ArrayList<>();
    private int processed = 0;
    private int skipped = 0;

    public ImportEquipmentTask(Path file, HttpEquipmentRentalClient api) {
        this.file = file;
        this.api = api;
    }

    public List<String> getWarnings() {
        return List.copyOf(warnings);
    }

    public int getProcessed() {
        return processed;
    }

    public int getSkipped() {
        return skipped;
    }

    @Override
    protected Integer call() throws Exception {
        String fileName = (file != null && file.getFileName() != null) ? file.getFileName().toString() : "unknown.csv";
        LOG.info("Початок CSV-імпорту: " + fileName);

        if (file == null || !Files.exists(file) || !Files.isReadable(file)) {
            String msg = "Не вдалося відкрити файл CSV";
            LOG.severe(msg + ": " + fileName);
            throw new IllegalStateException(msg);
        }

        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
        if (lines.isEmpty()) {
            String msg = "CSV-файл порожній (відсутній заголовок)";
            LOG.severe(msg);
            throw new IllegalArgumentException(msg);
        }

        String header = lines.get(0).replace("\uFEFF", "").trim();
        if (!EXPECTED_HEADER.equals(header)) {
            String msg = "Некоректний заголовок CSV (очікується: " + EXPECTED_HEADER + ")";
            LOG.severe(msg + ", отримано: " + header);
            throw new IllegalArgumentException(msg);
        }

        List<String> dataLines = new ArrayList<>();
        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line != null && !line.trim().isEmpty()) {
                dataLines.add(line);
            }
        }

        int total = dataLines.size();
        int imported = 0;

        // 3.14: порожня вибірка (є лише заголовок) — без ділення на нуль і без нескінченного прогресу
        if (total == 0) {
            updateProgress(1, 1);
            updateMessage("Немає рядків для імпорту");
            LOG.info("Завершено CSV-імпорт: рядків даних немає (імпортовано: 0)");
            return 0;
        }

        updateProgress(0, total);
        updateMessage("Опрацьовано 0 із " + total);

        for (int i = 0; i < dataLines.size(); i++) {
            // 3.15: перевірка скасування до запуску наступного POST
            if (isCancelled()) {
                LOG.info("Імпорт скасовано. Опрацьовано: " + processed + " із " + total + ", імпортовано: " + imported);
                updateMessage("Імпорт скасовано (опрацьовано " + processed + " із " + total + ", імпортовано: " + imported + ")");
                break;
            }

            int lineNumber = i + 2;
            String[] p = dataLines.get(i).split(";", -1);

            if (p.length != 3) {
                skipped++;
                processed++;
                String warn = "Рядок " + lineNumber + ": невірна кількість колонок (" + p.length + ", очікується 3)";
                LOG.warning(warn);
                warnings.add(warn);
                updateProgress(processed, total);
                updateMessage("Опрацьовано " + processed + " із " + total);
                continue;
            }

            String name = p[0].trim();
            String category = p[1].trim();
            String rateText = p[2].trim();

            if (name.isEmpty() || category.isEmpty() || rateText.isEmpty()) {
                skipped++;
                processed++;
                String warn = "Рядок " + lineNumber + ": порожнє обов'язкове поле";
                LOG.warning(warn);
                warnings.add(warn);
                updateProgress(processed, total);
                updateMessage("Опрацьовано " + processed + " із " + total);
                continue;
            }

            double rate;
            try {
                rate = Double.parseDouble(rateText);
            } catch (NumberFormatException ex) {
                skipped++;
                processed++;
                String warn = "Рядок " + lineNumber + ": некоректне число '" + rateText + "'";
                LOG.warning(warn);
                warnings.add(warn);
                updateProgress(processed, total);
                updateMessage("Опрацьовано " + processed + " із " + total);
                continue;
            }

            Equipment eq = new Equipment(null, name, category, rate);

            // 3.17: помилки сервера/HTTP не приховуємо, переводимо Task у FAILED
            try {
                api.addEquipment(eq);
            } catch (RuntimeException ex) {
                LOG.log(Level.SEVERE, "Помилка HTTP під час імпорту рядка " + lineNumber, ex);
                throw new IllegalStateException("Сервер недоступний або відхилив запит (рядок " + lineNumber + ")", ex);
            }

            imported++;
            processed++;
            updateProgress(processed, total);
            updateMessage("Опрацьовано " + processed + " із " + total);
        }

        LOG.info("Завершено CSV-імпорт. Опрацьовано: " + processed + " із " + total + ", імпортовано: " + imported + ", пропущено: " + skipped);
        return imported;
    }
}