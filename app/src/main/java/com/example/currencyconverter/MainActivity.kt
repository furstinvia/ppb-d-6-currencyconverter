package com.example.currencyconverter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

class MainActivity : ComponentActivity() {

    private val exchangeRates = mapOf(
        "IDR" to mapOf("USD" to 0.00005955, "EUR" to 0.00005243),
        "USD" to mapOf("IDR" to 16924.49, "EUR" to 0.89),
        "EUR" to mapOf("IDR" to 19072.87, "USD" to 1.12)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = lightColorScheme(
                    primary = Color(0xFFFFC1CC), // Soft pastel pink
                    onPrimary = Color.White,
                    background = Color(0xFFFFF1F3),
                    surface = Color.White,
                    onSurface = Color(0xFF333333)
                )
            ) {
                CurrencyConverterApp()
            }
        }
    }

    @Composable
    fun CurrencyConverterApp() {
        val currencies = listOf("🇮🇩 IDR", "🇺🇸 USD", "🇪🇺 EUR")
        var amountInput by remember { mutableStateOf("") }
        var fromCurrency by remember { mutableStateOf("🇮🇩 IDR") }
        var toCurrency by remember { mutableStateOf("🇺🇸 USD") }
        var result by remember { mutableStateOf("") }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "💱 Kalkulator Konversi Mata Uang",
                    fontSize = 22.sp,
                    style = TextStyle(fontFamily = FontFamily.SansSerif)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = {
                        Text("Jumlah Uang", fontFamily = FontFamily.SansSerif)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = TextStyle(fontFamily = FontFamily.SansSerif)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CurrencyDropdown("Dari", currencies, fromCurrency) { fromCurrency = it }
                    CurrencyDropdown("Ke", currencies, toCurrency) { toCurrency = it }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        val amount = amountInput.toDoubleOrNull()
                        if (amount == null) {
                            result = "Masukkan jumlah yang valid"
                            return@Button
                        }

                        val from = fromCurrency.takeLast(3)
                        val to = toCurrency.takeLast(3)

                        if (from == to) {
                            result = "%.2f $to".format(amount)
                        } else {
                            val rate = exchangeRates[from]?.get(to)
                            result = if (rate != null) {
                                val converted = amount * rate
                                "%.2f $to".format(converted)
                            } else {
                                "Kurs tidak tersedia"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text("Konversi", fontFamily = FontFamily.SansSerif)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Hasil: $result",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.SansSerif
                )
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun CurrencyDropdown(
        label: String,
        options: List<String>,
        selectedOption: String,
        onOptionSelected: (String) -> Unit
    ) {
        var expanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedOption,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(label, fontFamily = FontFamily.SansSerif)
                },
                modifier = Modifier.menuAnchor(),
                textStyle = TextStyle(fontFamily = FontFamily.SansSerif)
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, fontFamily = FontFamily.SansSerif) },
                        onClick = {
                            onOptionSelected(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
