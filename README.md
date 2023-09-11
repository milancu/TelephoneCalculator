# Telephone Bill Calculator

The Telephone Bill Calculator is a Java application that calculates the total cost of phone calls based on call records provided in a specific format. It adheres to the billing rules of a phone operator, including rate calculations and promotions.

## Table of Contents

- [Installation](#installation)
- [How to Run](#how-to-run)

## Installation

1. Clone or download the repository:

   ```bash
   git clone https://github.com/milancu/TelephoneCalculator.git

## How to run
1. Create an instance of the TelephoneBillCalculator interface.
2. Use the calculate method to calculate the total cost of phone calls, passing in the call records as a string in CSV format.
3. The method will return a BigDecimal representing the total cost.
4. Handle the result as needed in your application.

## Sample Input
420774567453,01-09-2023 07:30:00,01-09-2023 07:40:00 

420776562353,01-09-2023 08:00:00,01-09-2023 08:20:00

420774567453,01-09-2023 09:30:00,01-09-2023 10:00:00

420776562353,01-09-2023 14:00:00,01-09-2023 14:30:00

420774567453,01-09-2023 15:00:00,01-09-2023 15:30:00

420777777777,01-09-2023 12:00:00,01-09-2023 12:15:00