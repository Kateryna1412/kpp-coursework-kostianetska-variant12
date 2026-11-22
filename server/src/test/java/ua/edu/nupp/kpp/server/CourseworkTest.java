package ua.edu.nupp.kpp.server;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CourseworkTest {
    @Test void factor1ClampedToZeroWhenExceedingMaxDays() { assertEquals(0.0, RiskCalculator.factor1(15.0, 10.0)); }
    @Test void factor1ForValidDaysIs80() { assertEquals(80.0, RiskCalculator.factor1(2.0, 10.0)); }
    @Test void calculateIntegralIndexForDemoFactors() { assertEquals(58.5, RiskCalculator.calculate(50.0, 60.0, 70.0), 1e-9); }
    @Test void calculateRejectsFactorAbove100() { assertThrows(IllegalArgumentException.class, () -> RiskCalculator.calculate(101.0, 20.0, 30.0)); }
    @Test void createdToConfirmedAllowed() { assertTrue(RentalRequestService.isAllowedTransition(RentalRequestState.CREATED, RentalRequestState.CONFIRMED)); }
    @Test void confirmedToIssuedAllowed() { assertTrue(RentalRequestService.isAllowedTransition(RentalRequestState.CONFIRMED, RentalRequestState.ISSUED)); }
    @Test void issuedToReturnedAllowed() { assertTrue(RentalRequestService.isAllowedTransition(RentalRequestState.ISSUED, RentalRequestState.RETURNED)); }
    @Test void createdToReturnedRejected() { assertFalse(RentalRequestService.isAllowedTransition(RentalRequestState.CREATED, RentalRequestState.RETURNED)); }
    @Test void validateRejectsInvalidClientId() {
        RentalRequest r = new RentalRequest();
        r.setClientId(0L);
        r.setEquipmentId(1L);
        assertThrows(IllegalArgumentException.class, () -> RentalRequestService.validate(r));
    }
    @Test void validateRejectsFactorOutOfRange() {
        RentalRequest r = new RentalRequest();
        r.setClientId(1L);
        r.setEquipmentId(1L);
        r.setF1(120.0);
        assertThrows(IllegalArgumentException.class, () -> RentalRequestService.validate(r));
    }
}