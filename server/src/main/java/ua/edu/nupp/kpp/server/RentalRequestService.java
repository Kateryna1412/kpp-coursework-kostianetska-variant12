package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class RentalRequestService {
    private static final Logger LOG = Logger.getLogger(RentalRequestService.class.getName());
    private final RentalRequestDAO dao = new RentalRequestDAO();
    private final ClientDAO clientDAO = new ClientDAO();
    private final EquipmentDAO equipmentDAO = new EquipmentDAO();

    public List<RentalRequest> getAll() throws SQLException {
        return dao.getAll();
    }

    public RentalRequest getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(RentalRequest request) throws SQLException {
        validate(request);
        requireClient(request.getClientId());
        requireEquipment(request.getEquipmentId());

        // Розрахунок інтегрального показника
        double calculatedIndex = RiskCalculator.calculate(request.getF1(), request.getF2(), request.getF3());
        request.setIntegralIndex(calculatedIndex);

        // Початковий стан для нової заявки
        request.setState(RentalRequestState.CREATED);

        dao.add(request);
        LOG.info("Створено заявку на прокат");
    }

    public void update(RentalRequest request) throws SQLException {
        validate(request);
        requireClient(request.getClientId());
        requireEquipment(request.getEquipmentId());

        double calculatedIndex = RiskCalculator.calculate(request.getF1(), request.getF2(), request.getF3());
        request.setIntegralIndex(calculatedIndex);

        dao.update(request);
        LOG.info("Оновлено заявку на прокат");
    }

    // Метод зміни стану з перевіркою життєвого циклу
    public void changeState(long id, RentalRequestState next) throws SQLException {
        RentalRequest current = dao.getById(id);
        if (current == null) {
            throw new IllegalArgumentException("Заявку на прокат не знайдено");
        }
        if (!isAllowedTransition(current.getState(), next)) {
            LOG.warning("Недопустимий перехід стану заявки: " + current.getState() + " -> " + next);
            throw new IllegalArgumentException("Недопустимий перехід стану заявки");
        }
        dao.updateState(id, next);
        LOG.info("Змінено стан заявки " + current.getState()+ " -> " + next);

    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено заявку на прокат: ");
    }


    // Правила переходів станів життєвого циклу під твої реальні стани
    public static boolean isAllowedTransition(RentalRequestState from, RentalRequestState to) {
        return (from == RentalRequestState.CREATED && to == RentalRequestState.CONFIRMED)
                || (from == RentalRequestState.CONFIRMED && to == RentalRequestState.ISSUED)
                || (from == RentalRequestState.ISSUED && to == RentalRequestState.RETURNED);
    }

    static void validate(RentalRequest r) {
        if (r == null || r.getClientId() <= 0 || r.getEquipmentId() <= 0) {
            throw new IllegalArgumentException("Некоректні основні дані заявки на прокат");
        }
        if (r.getF1() < 0 || r.getF1() > 100 || r.getF2() < 0 || r.getF2() > 100 || r.getF3() < 0 || r.getF3() > 100) {
            throw new IllegalArgumentException("Фактори F1, F2 і F3 мають бути в діапазоні 0..100");
        }
    }

    private void requireClient(long id) throws SQLException {
        if (clientDAO.getById(id) == null) {
            throw new IllegalArgumentException("Клієнта не знайдено");
        }
    }

    private void requireEquipment(long id) throws SQLException {
        if (equipmentDAO.getById(id) == null) {
            throw new IllegalArgumentException("Обладнання не знайдено");
        }
    }
}