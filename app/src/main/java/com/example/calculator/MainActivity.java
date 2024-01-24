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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result = findViewById(R.id.textViewRes);
    }

    public void funcNumber(View view) {
        if(isError){
            ClearText(view);
            isError = false;
        }
        Button button = (Button) view;
        String clickBtn = button.getText().toString();
        if(isNewOp) {
            result.setText("");
        }
        isNewOp = false;
        if(result.getText().toString().startsWith("0"))
            return;
        result.append(clickBtn);
    }

    public void ClearText(View view) {
        result.setText("");
        firstNumber = null;
        operator = null;
        isNewOp = true;
        isError = false;
    }

    public void funcOperator(View view) {
        if (isError) return;
        Button button = (Button) view;
        String clickOperator = button.getText().toString();

        if (!isNewOp && !endsWithOperator(result.getText().toString())) {
            performCalculation();
            firstNumber = Double.parseDouble(result.getText().toString());
            isNewOp = true; // A new operation starts after calculation
        }

        if (clickOperator.equals("√") || clickOperator.equals("ln")) {
            operator = clickOperator;
            result.append(clickOperator + "( ");
        } else {
            operator = clickOperator;
            result.append(" " + clickOperator + " ");
        }

        isNewOp = false;
    }


    private boolean endsWithOperator(String text) {
        // Check if the text ends with a space followed by an operator
        return text.matches(".*[\\+\\-\\*\\/] $");
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void performCalculation() {
        if (operator == null) return;

        double secondNumber = 0.0;
        boolean isUnaryOperation = operator.equals("√") || operator.equals("ln");

        if (!isUnaryOperation && firstNumber == null) {
            String text = result.getText().toString();
            String numberStr = text.substring(0, text.indexOf(operator)).trim();
            firstNumber = Double.parseDouble(numberStr);
        }

        if (!isUnaryOperation) {
            String text = result.getText().toString();
            String numberStr = text.substring(text.indexOf(operator) + 1).trim();
            secondNumber = numberStr.isEmpty() ? firstNumber : Double.parseDouble(numberStr);
        }

        double calculationResult = 0.0;

        // Extended calculation logic
        try {
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
                    if (secondNumber == 0) throw new ArithmeticException("Cannot divide by zero");
                    calculationResult = firstNumber / secondNumber;
                    break;
                case "^":
                    calculationResult = Math.pow(firstNumber, secondNumber);
                    break;
                case "√":
                    if (firstNumber < 0) throw new ArithmeticException("Square root of negative number");
                    calculationResult = Math.sqrt(firstNumber);
                    break;
                case "ln":
                    if (firstNumber <= 0) throw new ArithmeticException("Log of non-positive number");
                    calculationResult = Math.log(firstNumber);
                    break;
            }

            if (calculationResult == (long) calculationResult) {
                result.setText(String.format("%d", (long) calculationResult));
            } else {
                DecimalFormat decimalFormat = new DecimalFormat("#.#####");
                result.setText(decimalFormat.format(calculationResult));
            }

            firstNumber = calculationResult;
        } catch (ArithmeticException e) {
            result.setText("Error: " + e.getMessage());
            firstNumber = null;
            isError = true;
        }

        operator = null;
        isOperatorPressed = false;
    }

    public void onEqual(View view) {
        if(!isNewOp) {
            performCalculation();
            isNewOp = true;
//            firstNumber = null;
            operator = null;
        }
    }
}
