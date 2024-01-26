package com.example.calculator;
import java.text.DecimalFormat;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView result;
    Double firstNumber = null;
    boolean isNewOp = true; // Flag to indicate a new operation
    String operator = null;
    boolean isOperatorPressed = false;
    boolean isError = false;

    private int currentBase = 10; // Default is decimal
    Double lastResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result = findViewById(R.id.textViewRes);
    }

    public void funcNumber(View view) {
        if (isError) {
            ClearText(view);
            isError = false;
        }

        Button button = (Button) view;
        String clickBtn = button.getText().toString();
        String currentText = result.getText().toString();

        if (isNewOp) {
            result.setText(clickBtn.equals("0") ? "0" : clickBtn); // If the first button pressed is 0, keep it as 0.
            isNewOp = false;
        } else {
            // If current display is "0", replace it with the new number unless it's a decimal point
            if (currentText.equals("0") && !clickBtn.equals(".")) {
                result.setText(clickBtn);
            } else {
                result.append(clickBtn);
            }
        }
    }

    public void ClearText(View view) {
        resetCalculator();
    }

    public void funcOperator(View view) {
        if (isError) return;

        Button button = (Button) view;
        String clickOperator = button.getText().toString();
        String currentText = result.getText().toString().trim();
        if (clickOperator.equals("ln") || clickOperator.equals("√")) {
            result.setText("");
        }
        if (!currentText.isEmpty()) {
            if (endsWithOperator(currentText)) {
                // If the last character is an operator and the new operator is a minus sign,
                // allow it for negative numbers.
                if (clickOperator.equals("-") && !currentText.endsWith("-")) {
                    result.append(" " + clickOperator + " ");
                }
                // If it's another operator, replace the last one
                else if (!clickOperator.equals("-")) {
                    // Replace the last operator
                    String newText = currentText.replaceAll(" [\\+\\-\\*\\/] $", " " + clickOperator + " ");
                    result.setText(newText);
                }
            } else {
                result.append(" " + clickOperator + " ");
                isNewOp = false;
            }
        } else {
            // If the first operator is a minus sign, allow it for negative numbers
            if (clickOperator.equals("-")) {
                result.setText(clickOperator);
                isNewOp = false;
            }
        }

        operator = clickOperator;
    }


    private boolean endsWithOperator(String text) {
        // Check if the text ends with a space followed by an operator
        return text.matches(".*[\\+\\-\\*\\/] $");
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void performCalculation() {
        if (operator == null) return;

//        String display = result.getText().toString();
//        if (display.contains("ANS")) {
//            display = display.replace("ANS", lastResult.toString());
//        }

        double secondNumber = 0.0;
        double calculationResult = 0.0;
        String text = result.getText().toString().trim();

        try {
            if (operator.equals("√") || operator.equals("ln")) {
                int startIndex = text.indexOf(operator) + operator.length();
                String numberStr = text.substring(startIndex).trim();
                if (!numberStr.isEmpty() && numberStr.matches("[-+]?\\d*\\.?\\d+")) {
                    firstNumber = Double.parseDouble(numberStr);
                } else {
                    throw new NumberFormatException("Invalid number format");
                }

                // Perform the calculation based on the operator
                switch (operator) {
                    case "√":
                        if (firstNumber < 0)
                            throw new ArithmeticException("Square root of negative number");
                        calculationResult = Math.sqrt(firstNumber);
                        break;
                    case "ln":
                        if (firstNumber <= 0)
                            throw new ArithmeticException("Log of non-positive number");
                        calculationResult = Math.log(firstNumber);
                        break;
                }
            } else {
                int operatorIndex = text.indexOf(" " + operator + " ");
                if (operatorIndex != -1) {
                    String firstNumberStr = text.substring(0, operatorIndex).trim();
                    firstNumber = Double.parseDouble(firstNumberStr);
                    String secondNumberStr = text.substring(operatorIndex + operator.length() + 1).trim();
                    secondNumber = secondNumberStr.isEmpty() ? firstNumber : Double.parseDouble(secondNumberStr);

                    // Perform the calculation based on the operator
                    switch (operator) {
                        case "+":
                            calculationResult = firstNumber + secondNumber;
                            break;
                        case "-":
                            calculationResult = firstNumber - secondNumber;
                            break;
                        case "X":
                            calculationResult = firstNumber * secondNumber;
                            break;
                        case "/":
                            if (secondNumber == 0)
                                throw new ArithmeticException("Cannot divide by zero");
                            calculationResult = firstNumber / secondNumber;
                            break;
                        case "^":
                            calculationResult = Math.pow(firstNumber, secondNumber);
                            break;
                        // Unary operations are already handled above.
                    }
                } else {
                    // This handles the scenario where the operator is not found in the text.
                    throw new IllegalStateException("Operator not found in the expression");
                }
            }

            // Format and display the result
            if (calculationResult == (long) calculationResult) {
                result.setText(String.format("%d", (long) calculationResult));
            } else {
                DecimalFormat decimalFormat = new DecimalFormat("#.#####");
                result.setText(decimalFormat.format(calculationResult));
            }

            firstNumber = calculationResult; // Store result for consecutive calculations

        } catch (NumberFormatException e) {
            result.setText("Error: Invalid Number");
            isError = true;
        } catch (ArithmeticException e) {
            result.setText("Error: " + e.getMessage());
            isError = true;
        } catch (Exception e) {
            result.setText("Error: Other Error");
            isError = true;
        } finally {
            operator = null;
            isOperatorPressed = false;
        }
        // At the end of the calculation before you set the result TextView:
        if (!Double.isNaN(calculationResult) && !Double.isInfinite(calculationResult)) {
            // Format and store the last result based on whether it's a whole number or not
            if (calculationResult == (long) calculationResult) {
                lastResult = (double) (long) calculationResult; // Store as Double for whole numbers
            } else {
                // Store the result up to 5 decimal places for non-whole numbers
                lastResult = Double.parseDouble(new DecimalFormat("#.#####").format(calculationResult));
            }
        } else {
            lastResult = null; // Reset last answer if the current result is not valid
        }
    }

    public void onEqual(View view) {
        if (!isNewOp) {
            performCalculation();
            isNewOp = true;
            firstNumber = null;
            operator = null;
        }
    }

    private void resetCalculator() {
        firstNumber = null;
        operator = null;
        isNewOp = true;
        isOperatorPressed = false;
        isError = true;
        result.setText("");
    }

    public void onDel(View view) {
        if(isError) return;
        String display = result.getText().toString();
        if (!display.isEmpty()) {
            if (display.endsWith(" ")) {
                display = display.substring(0, display.lastIndexOf(" ", display.length() - 2));
            } else {
                display = display.substring(0, display.length() - 1);
            }
            result.setText(display);
        }
        // Reset the calculation if the entire string is deleted
        if (display.isEmpty()) {
            firstNumber = null;
            operator = null;
            isNewOp = true;
        }
    }

    public void onAns(View view) {
        // Check if there is a last answer to use
        if (lastResult != null) {
            if (isNewOp || result.getText().toString().isEmpty()) {
                // If it's a new operation or the result is empty, set the result to lastAnswer
                result.setText(String.valueOf(lastResult));
                isNewOp = false;
            } else {
                // If an operation is in progress, append lastAnswer to the current operation
                result.append(String.valueOf(lastResult));
            }
        }
    }
    public void onDec(View view) {
        convertNumber(10);
    }

    public void onHEX(View view) {
        convertNumber(16);
    }

    public void onBin(View view) {
        convertNumber(2);
    }

    public void onOct(View view) {
        convertNumber(8);
    }

    @SuppressLint("SetTextI18n")
    private void convertNumber(int base) {
        String display = result.getText().toString();
        try {
            long number;
            String converted;
            if (currentBase != 10) {
                // Convert from the current base to decimal
                number = BaseToDecimal(display, currentBase);
            } else {
                // If the current base is decimal, parse directly
                number = Long.parseLong(display);
            }
            if (currentBase == 10) {
                // If the current base is decimal, parse the number directly
                number = Long.parseLong(display);
            } else {
                // If the current base is not decimal, convert it to decimal first
                number = BaseToDecimal(display, currentBase);
            }

            switch (base) {
                case 2:
                    converted = Long.toBinaryString(number);
                    currentBase = 2;
                    break;
                case 8:
                    converted = Long.toOctalString(number);
                    currentBase = 8;
                    break;
                case 16:
                    converted = Long.toHexString(number);
                    currentBase = 16;
                    break;
                case 10:
                    converted = String.valueOf(number); // No conversion needed for decimal
                    currentBase = 10;
                    break;
                default:
                    throw new IllegalStateException("Unsupported base");
            }

            result.setText(converted.toUpperCase());
        } catch (NumberFormatException e) {
            result.setText("Error: Invalid Number");
            isError = true;
        } catch (IllegalStateException e) {
            result.setText("Error: " + e.getMessage());
            isError = true;
        }
    }
    @SuppressLint("SetTextI18n")
    private long BaseToDecimal(String number, int base) {
        try {
            return Long.parseLong(number, base);
        } catch (NumberFormatException e) {
            result.setText("Error: Invalid Number Format");
            isError = true;
            return -1; // Return -1 or handle the error as you see fit
        }
    }
}
