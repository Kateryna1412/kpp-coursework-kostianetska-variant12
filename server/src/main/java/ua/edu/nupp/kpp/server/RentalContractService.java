package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class RentalContractService {
    private static final Logger LOG = Logger.getLogger(RentalContractService.class.getName());
    private final RentalContractDAO dao = new RentalContractDAO();
    private final RentalRequestDAO requestDAO = new RentalRequestDAO();

    public List<RentalContract> getAll() throws SQLException {
        return dao.getAll();
    }

    public RentalContract getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(RentalContract contract) throws SQLException {
        validate(contract);
        requireRequest(contract.getRequestId());
        dao.add(contract);
        LOG.info("Створено договір прокату");
    }

    public void update(RentalContract contract) throws SQLException {
        validate(contract);
        requireRequest(contract.getRequestId());
        dao.update(contract);
        LOG.info("Оновлено договір прокату");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено договір прокату");
    }

    static void validate(RentalContract c) {
        if (c == null || c.getRequestId() <= 0 || c.getTotalCost() < 0) {
            throw new IllegalArgumentException("Некоректні дані договору прокату");
        }
        if (c.getContractDate() == null || c.getContractDate().isBlank()) {
            throw new IllegalArgumentException("Дата договору є обов'язковою");
        }
    }

    private void requireRequest(long id) throws SQLException {
        if (requestDAO.getById(id) == null) {
            throw new IllegalArgumentException("Заявку на прокат не знайдено");
        }
    }
}