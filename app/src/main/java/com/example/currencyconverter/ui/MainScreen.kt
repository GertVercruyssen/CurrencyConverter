package com.example.currencyconverter.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.accompanist.appcompattheme.AppCompatTheme
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.currencyconverter.R

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MainScreen(
    viewModel: MainViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    AppCompatTheme {
        Surface(color = MaterialTheme.colors.background) {
            Column(modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(), horizontalAlignment = Alignment.End) {
                LatestError(uiState.latestError)
                AmountInput(viewModel)
                CurrencySelector(viewModel, uiState.convertedCurrencyList.map {it.first})
                CurrencyList(uiState.convertedCurrencyList ,Modifier.weight(1f))
            }
        }
    }
}

/**
 * Very simple way to show Errors. In a more complex app you could include a retry button here
 */
@Composable
fun LatestError(latestError: String?) {
    if(latestError != null)
        Text(text = latestError)
}

@Composable
fun AmountInput(viewModel: MainViewModel) {
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = {
            text = it
            viewModel.setNewAmount(text)},
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        placeholder = { Text("0.0")},
        singleLine = true,
        modifier = Modifier.padding(16.dp)
    )
}

/**
 * This component is somewhat overloaded with more than 100 elements, it is not that responsive
 * known issue: https://issuetracker.google.com/issues/203328119
 */
@Composable
fun CurrencySelector(viewModel: MainViewModel, currencies: List<String>) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCurrency by remember { mutableStateOf("") }
    Row {
        Spacer(modifier = Modifier.fillMaxWidth().weight(1.0f))
        Box(modifier = Modifier.padding(16.dp)) {
            val previewText = selectedCurrency + stringResource(R.string.arrow_down)
            Text(text = previewText,
                modifier = Modifier
                    .clickable(onClick = { expanded = true })
                    .background(
                        MaterialTheme.colors.primary
                    ).padding(10.dp))
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                currencies.forEach { currency ->
                    DropdownMenuItem(onClick = {
                        selectedCurrency = currency
                        expanded = false
                        viewModel.setNewCurrency(currency)
                    }) {
                        Text(text = currency)
                    }
                }
            }
        }
    }
}

@Composable
fun CurrencyList(currenciesList: List<Pair<String,Double>>, modifier: Modifier) {
    LazyColumn(modifier = modifier) {
        items(items  = currenciesList) {
            Currency(it)
            Divider()
        }
    }
}

@Composable
private fun Currency(value: Pair<String, Double>) {
    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp)) {
        Text(text = value.first)
        Spacer(Modifier.weight(1f))
        Text(text = String.format("%.2f", value.second))
    }
}