package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class EquipmentService {
    private static final Logger LOG = Logger.getLogger(EquipmentService.class.getName());
    private final EquipmentDAO dao = new EquipmentDAO();

    public List<Equipment> getAll() throws SQLException {
        return dao.getAll();
    }

    public Equipment getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Equipment equipment) throws SQLException {
        validate(equipment);
        dao.add(equipment);
        LOG.info("Додано обладнання");
    }

    public void update(Equipment equipment) throws SQLException {
        validate(equipment);
        dao.update(equipment);
        LOG.info("Оновлено обладнання");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено обладнання");
    }

    static void validate(Equipment e) {
        if (e == null || blank(e.getName()) || blank(e.getCategory())) {
            throw new IllegalArgumentException("Назва та категорія обладнання є обов'язковими");
        }
        if (e.getRentalRate() <= 0) {
            throw new IllegalArgumentException("Вартість оренди має бути більше нуля");
        }
    }

    private static boolean blank(String v) {
        return v == null || v.isBlank();
    }
}