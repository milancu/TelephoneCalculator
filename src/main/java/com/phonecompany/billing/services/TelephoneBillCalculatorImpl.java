package com.phonecompany.billing.services;

import com.phonecompany.billing.TelephoneBillCalculator;
import com.phonecompany.billing.domain.entities.CallRecord;
import com.phonecompany.billing.domain.enums.BillingRate;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * The implementation of the TelephoneBillCalculator interface.
 * This class provides methods to calculate the total cost of a telephone bill based on call records.
 */
public class TelephoneBillCalculatorImpl implements TelephoneBillCalculator {
    private static final Logger logger = Logger.getLogger(TelephoneBillCalculatorImpl.class.getName());
    private static final int LOW_BOUNCE  = 8;
    private static final int UP_BOUNCE  = 16;
    private static final int LONG_CALL  = 5;
    private static final String DATE_FORMAT = "dd-MM-yyyy HH:mm:ss";
    private static final int PHONE_LOG_FIELDS = 3;

    /**
     * Calculates the total cost of phone calls based on the provided phone log.
     *
     * @param phoneLog a string containing the phone log in a specific format
     * @return the total cost of phone calls as a BigDecimal
     */
    @Override
    public BigDecimal calculate(String phoneLog) {
        BigDecimal totalCost = BigDecimal.ZERO;

        try {
            List<CallRecord> callRecords = parseCallRecords(phoneLog);
            String mostFrequentPhoneNumber = findMostFrequentPhoneNumber(callRecords);

            totalCost = callRecords.stream()
                    .map(call -> calculateCallCost(call, mostFrequentPhoneNumber))
                    .reduce(BigDecimal::add)
                    .orElse(BigDecimal.ZERO);

        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error occurred while calculating phone bill.", e);
        }

        return totalCost;
    }

    /**
     * Parses the given phone log and returns a list of call records.
     *
     * @param phoneLog the phone log to be parsed
     * @return a list of call records parsed from the phone log
     * @throws ParseException if an error occurs while parsing the phone log
     */
    private List<CallRecord> parseCallRecords(String phoneLog) throws ParseException {
        List<CallRecord> callRecords = new ArrayList<>();
        String[] lines = phoneLog.split("\n");

        for (String line : lines) {
            String[] parts = line.split(",");
            parseCallRecordParts(callRecords, line, parts);
        }
        return callRecords;
    }

    /**
     * Parses the parts of a call record and adds it to the list of call records.
     *
     * @param callRecords the list of call records to add the parsed call record to
     * @param line the line containing the call record parts
     * @param parts the array of call record parts
     * @throws ParseException if the line format is invalid
     */
    private void parseCallRecordParts(List<CallRecord> callRecords, String line, String[] parts) throws ParseException {
        if (parts.length != PHONE_LOG_FIELDS) {
            logger.log(Level.WARNING, "Invalid line format: " + line);
            return;
        }
        String phoneNumber = parts[0];
        Date startTime = parseDate(parts[1]);
        Date endTime = parseDate(parts[2]);
        callRecords.add(new CallRecord(phoneNumber, startTime, endTime));
    }

    /**
     * Parses a given date string using the specified date format.
     *
     * @param dateString The date string to be parsed.
     * @return The parsed Date object.
     * @throws ParseException If the parsing fails.
     */
    private Date parseDate(String dateString) throws ParseException {
        return new SimpleDateFormat(DATE_FORMAT).parse(dateString);
    }

    /**
     * Finds the most frequent phone number within a list of call records.
     *
     * @param callRecords a list of CallRecord objects representing call records
     * @return the most frequent phone number as a String
     */
    private String findMostFrequentPhoneNumber(List<CallRecord> callRecords) {
        Map<String, Integer> phoneNumberCounts = new HashMap<>();
        countCallRecords(callRecords, phoneNumberCounts);

        List<String> mostFrequentPhoneNumbers = getNumbers(phoneNumberCounts);

        return getMaximumPhoneNumber(mostFrequentPhoneNumbers);
    }

    /**
     * Counts the number of occurrences of each phone number in the given list of call records.
     * The counts are stored in the provided map, where the phone number is the key and the count is the value.
     *
     * @param callRecords The list of call records to count.
     * @param phoneNumberCounts The map to store the phone number counts.
     */
    private void countCallRecords(List<CallRecord> callRecords, Map<String, Integer> phoneNumberCounts) {
        for (CallRecord call : callRecords) {
            String phoneNumber = call.getPhoneNumber();
            phoneNumberCounts.put(phoneNumber, phoneNumberCounts.getOrDefault(phoneNumber, 0) + 1);
        }
    }

    /**
     * Returns the maximum phone number from the list of most frequent phone numbers.
     *
     * @param mostFrequentPhoneNumbers a list of phone numbers
     * @return the maximum phone number
     */
    private String getMaximumPhoneNumber(List<String> mostFrequentPhoneNumbers) {
        return Collections.max(mostFrequentPhoneNumbers);
    }

    /**
     * Retrieves the most frequent phone numbers from a map of phone number counts.
     *
     * @param phoneNumberCounts a map containing phone numbers as keys and their corresponding counts as values
     * @return a list of the most frequent phone numbers
     */
    private List<String> getNumbers(Map<String, Integer> phoneNumberCounts) {
        List<String> mostFrequentPhoneNumbers = new ArrayList<>();
        populateFrequentPhoneNumbers(phoneNumberCounts, mostFrequentPhoneNumbers);
        return mostFrequentPhoneNumbers;
    }

    /**
     * Populates the list of most frequent phone numbers based on the given phone number counts.
     * Only the phone numbers with the highest count will be added to the list.
     *
     * @param phoneNumberCounts a Map containing phone numbers as keys and their corresponding counts as values
     * @param mostFrequentPhoneNumbers the List to which the most frequent phone numbers will be added
     */
    private void populateFrequentPhoneNumbers(Map<String, Integer> phoneNumberCounts, List<String> mostFrequentPhoneNumbers) {
        int maxCount = Collections.max(phoneNumberCounts.values());

        phoneNumberCounts.entrySet()
                .stream()
                .filter(entry -> entry.getValue() == maxCount)
                .forEach(entry -> mostFrequentPhoneNumbers.add(entry.getKey()));
    }

    /**
     * Calculates the call cost based on the given CallRecord and the most frequent phone number.
     *
     * @param call                   the CallRecord containing the call information
     * @param mostFrequentPhoneNumber the most frequent phone number
     * @return the call cost as a BigDecimal value
     */
    private BigDecimal calculateCallCost(CallRecord call, String mostFrequentPhoneNumber) {
        if (call.getPhoneNumber().equals(mostFrequentPhoneNumber)) {
            return BigDecimal.ZERO;
        }

        BigDecimal callCost;
        long callDurationMinutes = getMinutesDuration(call);

        callCost = calculateDurationCost(call, callDurationMinutes);

        callCost = calculateAdditionalCost(callDurationMinutes, callCost);

        return callCost;
    }

    /**
     * Calculates the duration of a call in minutes.
     *
     * @param call The CallRecord object representing the call.
     * @return The duration of the call in minutes.
     */
    private long getMinutesDuration(CallRecord call) {
        return (call.getEndTime().getTime() - call.getStartTime().getTime()) / (60 * 1000);
    }

    /**
     * Calculates the additional cost of a call, if the call duration exceeds the specified limit.
     *
     * @param callDurationMinutes the duration of the call in minutes
     * @param callCost the cost of the call before applying additional charges
     * @return the adjusted call cost with additional charges, if applicable
     */
    private BigDecimal calculateAdditionalCost(long callDurationMinutes, BigDecimal callCost) {
        if (callDurationMinutes > LONG_CALL) {
            callCost = callCost.add(BillingRate.ADDITIONAL_RATE.getRate()
                    .multiply(BigDecimal.valueOf(callDurationMinutes - LONG_CALL)));
        }
        return callCost;
    }

    /**
     * Calculates the cost of a call based on its duration.
     *
     * @param call the call for which to calculate the cost
     * @param callDurationMinutes the duration of the call in minutes
     * @return the cost of the call
     */
    private BigDecimal calculateDurationCost(CallRecord call, long callDurationMinutes) {
        BigDecimal callCost;
        if (isNormalRateHour(call)) {
            callCost = BillingRate.NORMAL_RATE.getRate()
                    .multiply(BigDecimal.valueOf(callDurationMinutes));
        } else {
            callCost = BillingRate.REDUCED_RATE.getRate()
                    .multiply(BigDecimal.valueOf(callDurationMinutes));
        }
        return callCost;
    }

    /**
     * Determines if the given call record falls within the normal rate hour range.
     *
     * @param call The call record to check.
     * @return {@code true} if the call falls within the normal rate hour range, {@code false} otherwise.
     */
    private boolean isNormalRateHour(CallRecord call) {
        return (call.getStartTime().getHours() >= LOW_BOUNCE) && (call.getStartTime().getHours() < UP_BOUNCE);
    }
}