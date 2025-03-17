package com.example.unitconverterapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.view.View;
import android.widget.ArrayAdapter;
import java.util.HashMap;
import java.util.Map;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    EditText inputValue;
    Button convertButton;
    TextView resultText;

    Spinner sourceSpinner;
    Spinner destinationSpinner;
    Map<String, Double> conversionMap;

    private boolean isSameUnitType(String unit, String[] type) {
        for (String typeUnit : type) {
            if (unit.equals(typeUnit)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        inputValue = findViewById(R.id.inputValue);
        convertButton = findViewById(R.id.convertButton);
        resultText = findViewById(R.id.resultText);
        sourceSpinner = findViewById(R.id.sourceSpinner);
        destinationSpinner = findViewById(R.id.destinationSpinner);

        // Use a hashmap to convert to a base unit
        conversionMap = new HashMap<>();

        // Distance units
        conversionMap.put("Inch", 2.54);        // cm
        conversionMap.put("Foot", 30.48);       // cm
        conversionMap.put("Yard", 91.44);       // cm
        conversionMap.put("Mile", 1609.34);     // cm

        // Weight Units
        conversionMap.put("Pound", 1.0);        // pound
        conversionMap.put("Ounce", 1.0/16.0);   // pound
        conversionMap.put("Ton", 2000.0);       // pound

        // Temperature Units - logic handled later
        conversionMap.put("Celsius", 1.0);
        conversionMap.put("Fahrenheit", 1.0);
        conversionMap.put("Kelvin", 1.0);

        String[] lengthUnits = {"Inch", "Foot", "Yard", "Mile"};
        String[] weightUnits = {"Pound", "Ounce", "Ton"};
        String[] temperatureUnits = {"Celsius", "Fahrenheit", "Kelvin"};

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.units,
                android.R.layout.simple_spinner_item
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sourceSpinner.setAdapter(adapter);
        destinationSpinner.setAdapter(adapter);

        convertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputString = inputValue.getText().toString();

                if (inputString.isEmpty()) {
                    resultText.setText("Please enter a value");
                    return;
                }

                Double input = Double.parseDouble(inputString);
                String fromUnit = sourceSpinner.getSelectedItem().toString();
                String toUnit = destinationSpinner.getSelectedItem().toString();
                double result;

                // Handle conversion to same unit type
                if (fromUnit.equals(toUnit)){
                    resultText.setText(String.format("Result: %.2f %s", input, toUnit));
                    return;
                }

                // Handle invalid conversion requests
                if (!((isSameUnitType(fromUnit, lengthUnits) && isSameUnitType(toUnit, lengthUnits)) ||
                        (isSameUnitType(fromUnit, weightUnits) && isSameUnitType(toUnit, weightUnits) ||
                                (isSameUnitType(fromUnit, temperatureUnits) && isSameUnitType(toUnit, temperatureUnits))))) {

                    resultText.setText("Cannot convert between different unit types");
                    return;
                }

                // Handle temperature conversion
                if (fromUnit.equals("Celsius") && toUnit.equals("Fahrenheit")) {
                    result = (input * 1.8) + 32;
                    resultText.setText(String.format("Result: %.2f F", result));
                    return;
                } else if (fromUnit.equals("Fahrenheit") && toUnit.equals("Celsius")) {
                    result = (input - 32) / 1.8;
                    resultText.setText(String.format("Result: %.2f C", result));
                    return;
                } else if (fromUnit.equals("Celsius") && toUnit.equals("Kelvin")) {
                    result = input + 273.15;
                    resultText.setText(String.format("Result: %.2f K", result));
                    return;
                } else if (fromUnit.equals("Kelvin") && toUnit.equals("Celsius")) {
                    result = input - 273.15;
                    resultText.setText(String.format("Result: %.2f C", result));
                    return;
                } else if (fromUnit.equals("Fahrenheit") && toUnit.equals("Kelvin")) {
                    result = (input - 32) / 1.8 + 273.15;
                    resultText.setText(String.format("Result: %.2f K", result));
                    return;
                } else if (fromUnit.equals("Kelvin") && toUnit.equals("Fahrenheit")) {
                    result = (input - 273.15) * 1.8 + 32;
                    resultText.setText(String.format("Result: %.2f F", result));
                    return;
                }

                // Handle all other conversions
                double baseValue = input * conversionMap.get(fromUnit);
                result = baseValue / conversionMap.get(toUnit);

                resultText.setText(String.format("Result: %.2f %s", result, toUnit));

            }
        });
    }
}