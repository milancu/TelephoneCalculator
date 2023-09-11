package com.phonecompany.billing;

import com.phonecompany.billing.services.TelephoneBillCalculatorImpl;

import java.math.BigDecimal;

public class Main {
    public static void main(String[] args) {
        calculateTelephoneBills();
    }

    private static void calculateTelephoneBills() {
        TelephoneBillCalculator telephoneBillCalculator = new TelephoneBillCalculatorImpl();

        String callsData = "420774567453,01-09-2023 07:30:00,01-09-2023 07:40:00\n" +
                "420776562353,01-09-2023 08:00:00,01-09-2023 08:20:00\n" +
                "420774567453,01-09-2023 09:30:00,01-09-2023 10:00:00\n" +
                "420776562353,01-09-2023 14:00:00,01-09-2023 14:30:00\n" +
                "420774567453,01-09-2023 15:00:00,01-09-2023 15:30:00\n" +
                "420777777777,01-09-2023 12:00:00,01-09-2023 12:15:00";

        BigDecimal billAmount = telephoneBillCalculator.calculate(callsData);

        printBillAmount(billAmount);
    }

    private static void printBillAmount(BigDecimal amount) {
        System.out.println(amount);
    }
}