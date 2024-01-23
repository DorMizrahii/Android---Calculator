package com.example.calculator;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        result = findViewById(R.id.textViewRes);
    }

    public void funcNumber(View view) {
        if(isNewOp) {
            result.setText("");
        }
        isNewOp = false;

        Button button = (Button) view;
        String clickBtn = button.getText().toString();
        result.append(clickBtn);
    }

    public void ClearText(View view) {
        result.setText("");
        firstNumber = null;
        operator = null;
        isNewOp = true;
    }

    public void funcOperator(View view) {
        String currentText = result.getText().toString();
        Button button = (Button) view;
        String clickOperator = button.getText().toString();

        // If the current text is empty and the minus operator is clicked, let it be the first character
        if (currentText.isEmpty() && clickOperator.equals("-")) {
            result.setText(clickOperator);
            isNewOp = false;
        }
        // If the current text is not empty and does not end with an operator, append the new operator
        else if (!currentText.isEmpty() && !endsWithOperator(currentText)) {
            if(!isNewOp) {
                performCalculation();
                firstNumber = Double.parseDouble(result.getText().toString());
                isNewOp = true; // A new operation starts after calculation
            }
            result.append(" " + clickOperator + " ");
        }
        // Save the clicked operator for calculation
        if (isNewOp) {
            operator = clickOperator;
            isNewOp = false;
        }
    }

    private boolean endsWithOperator(String text) {
        // Check if the text ends with a space followed by an operator
        return text.matches(".*[\\+\\-\\*\\/] $");
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    private void performCalculation() {
        if (operator == null || firstNumber == null) return;

        String text = result.getText().toString();
        double secondNumber;
        if(text.endsWith(operator)) {
            secondNumber = firstNumber;
        } else {
            String numberStr = text.substring(text.indexOf(operator) + 1);
            secondNumber = Double.parseDouble(numberStr);
        }
        double calculationResult = 0.0;

        // ... calculation logic ...
        switch (operator) {
            case "+":
                calculationResult = firstNumber + secondNumber;
                break;
            case "-":
                calculationResult = firstNumber - secondNumber;
                break;
            case "*":
                calculationResult = firstNumber * secondNumber;
                break;
            case "/":
                if (secondNumber == 0) {
                    result.setText("Error");
                    firstNumber = null;
                    operator = null;
                    isOperatorPressed = false;
                    return;
                } else {
                    calculationResult = firstNumber / secondNumber;
                }
                break;
        }

        // Check if the result is an integer and display accordingly
        if (calculationResult == (long) calculationResult) {
            result.setText(String.format("%d", (long) calculationResult));
        } else {
            result.setText(String.format("%s", calculationResult));
        }
        firstNumber = calculationResult; // Store result for consecutive calculations
        operator = null;
    }

    public void onEqual(View view) {
        if(!isNewOp) {
            performCalculation();
            isNewOp = true;
            firstNumber = null;
            operator = null;
        }
    }
}
