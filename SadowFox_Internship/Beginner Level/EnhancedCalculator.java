import java.util.InputMismatchException;
import java.util.Scanner;

public class EnhancedCalculator {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            System.out.println("\n--- Enhanced Console Calculator ---");
            System.out.println("1. Basic Arithmetic (+, -, *, /)");
            System.out.println("2. Scientific Functions (sqrt, pow, log, sin, cos, tan)");
            System.out.println("3. Unit Conversions (Temperature, Length)");
            System.out.println("4. Exit");
            System.out.print("Enter your choice: ");

            try {
                int choice = scanner.nextInt();
                switch (choice) {
                    case 1:
                        performArithmetic();
                        break;
                    case 2:
                        performScientific();
                        break;
                    case 3:
                        performConversion();
                        break;
                    case 4:
                        running = false;
                        System.out.println("Exiting calculator. Goodbye!");
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 4.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); 
            }
        }
        scanner.close();
    }

    private static void performArithmetic() {
        System.out.println("\n--- Basic Arithmetic ---");
        try {
            System.out.print("Enter the first number: ");
            double num1 = scanner.nextDouble();
            System.out.print("Enter the operator (+, -, *, /): ");
            String operator = scanner.next();
            System.out.print("Enter the second number: ");
            double num2 = scanner.nextDouble();

            double result = 0;
            switch (operator) {
                case "+":
                    result = num1 + num2;
                    break;
                case "-":
                    result = num1 - num2;
                    break;
                case "*":
                    result = num1 * num2;
                    break;
                case "/":
                    if (num2 == 0) {
                        throw new ArithmeticException("Error: Division by zero is not allowed.");
                    }
                    result = num1 / num2;
                    break;
                default:
                    System.out.println("Invalid operator.");
                    return;
            }
            System.out.println("Result: " + result);

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter valid numbers.");
            scanner.nextLine(); // Clear the invalid input
        } catch (ArithmeticException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void performScientific() {
        System.out.println("\n--- Scientific Functions ---");
        System.out.println("Available functions: sqrt, pow, log, sin, cos, tan");
        System.out.print("Enter function name: ");
        String function = scanner.next().toLowerCase();

        try {
            double result = 0;
            if ("pow".equals(function)) {
                System.out.print("Enter the base number: ");
                double base = scanner.nextDouble();
                System.out.print("Enter the exponent: ");
                double exponent = scanner.nextDouble();
                result = Math.pow(base, exponent);
                System.out.println("Result: " + result);
            } else {
                System.out.print("Enter a number: ");
                double num = scanner.nextDouble();
                switch (function) {
                    case "sqrt":
                        if (num < 0) {
                            System.out.println("Error: Cannot calculate the square root of a negative number.");
                            return;
                        }
                        result = Math.sqrt(num);
                        break;
                    case "log":
                        if (num <= 0) {
                            System.out.println("Error: Logarithm is only defined for positive numbers.");
                            return;
                        }
                        result = Math.log(num);
                        break;
                    case "sin":
                        result = Math.sin(Math.toRadians(num));
                        break;
                    case "cos":
                        result = Math.cos(Math.toRadians(num));
                        break;
                    case "tan":
                        result = Math.tan(Math.toRadians(num));
                        break;
                    default:
                        System.out.println("Invalid function.");
                        return;
                }
                System.out.println("Result: " + result);
            }

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine();
        }
    }

    private static void performConversion() {
        System.out.println("\n--- Unit Conversions ---");
        System.out.println("1. Temperature");
        System.out.println("2. Length");
        System.out.print("Enter your choice: ");

        try {
            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    convertTemperature();
                    break;
                case 2:
                    convertLength();
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine();
        }
    }

    private static void convertTemperature() {
        System.out.println("\n--- Temperature Conversion ---");
        System.out.println("1. Celsius to Fahrenheit");
        System.out.println("2. Fahrenheit to Celsius");
        System.out.print("Enter your choice: ");

        try {
            int choice = scanner.nextInt();
            System.out.print("Enter the temperature value: ");
            double temp = scanner.nextDouble();
            double result = 0;
            switch (choice) {
                case 1:
                    result = (temp * 9 / 5) + 32;
                    System.out.println(temp + "°C is equal to " + result + "°F.");
                    break;
                case 2:
                    result = (temp - 32) * 5 / 9;
                    System.out.println(temp + "°F is equal to " + result + "°C.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine();
        }
    }

    private static void convertLength() {
        System.out.println("\n--- Length Conversion ---");
        System.out.println("1. Meters to Feet");
        System.out.println("2. Feet to Meters");
        System.out.print("Enter your choice: ");

        try {
            int choice = scanner.nextInt();
            System.out.print("Enter the length value: ");
            double length = scanner.nextDouble();
            double result = 0;
            switch (choice) {
                case 1:
                    result = length * 3.28084;
                    System.out.println(length + " meters is equal to " + result + " feet.");
                    break;
                case 2:
                    result = length / 3.28084;
                    System.out.println(length + " feet is equal to " + result + " meters.");
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine();
        }
    }
}