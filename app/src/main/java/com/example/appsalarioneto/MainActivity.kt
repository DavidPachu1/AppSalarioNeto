package com.example.appsalarioneto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import java.util.Locale
import com.example.appsalarioneto.ui.theme.AppSalarioNetoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppSalarioNetoTheme {
                NavegacionApp()
            }
        }
    }
}

@Composable
fun NavegacionApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "pantalla1") {
        composable("pantalla1") {
            Pantalla1 { nombre, horas, tarifa ->

                navController.currentBackStackEntry?.savedStateHandle?.set("nombre", nombre)
                navController.currentBackStackEntry?.savedStateHandle?.set("horas", horas)
                navController.currentBackStackEntry?.savedStateHandle?.set("tarifa", tarifa)
                navController.navigate("pantalla2")
            }
        }

        composable("pantalla2") {
            val savedStateHandle = navController.previousBackStackEntry?.savedStateHandle
            val nombre = savedStateHandle?.get<String>("nombre") ?: ""
            val horas = savedStateHandle?.get<Double>("horas") ?: 0.0
            val tarifa = savedStateHandle?.get<Double>("tarifa") ?: 0.0

            Pantalla2(nombre, horas, tarifa, onVolver = { navController.popBackStack() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pantalla1(onNavegar: (String, Double, Double) -> Unit) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var horas by rememberSaveable { mutableStateOf("") }
    var tarifa by rememberSaveable { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Calculadora Salario Neto"
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Copyright -> David Fernández Martínez"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Por favor, introduce tus datos",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = horas,
                onValueChange = { horas = it },
                label = { Text("Horas trabajadas") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = tarifa,
                onValueChange = { tarifa = it },
                label = { Text("Tarifa por hora (€)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    val h = horas.toDoubleOrNull() ?: 0.0
                    val t = tarifa.toDoubleOrNull() ?: 0.0
                    if (nombre.isNotBlank()) {
                        onNavegar(nombre, h, t)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calcular salario")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Pantalla2(nombre: String, horas: Double, tarifa: Double, onVolver: () -> Unit) {
    val salarioBruto = horas * tarifa
    val irpf = salarioBruto * 0.25
    val deducciones = 100.0
    val salarioNeto = salarioBruto - irpf - deducciones

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Resultados del cálculo"
                    )
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.primary,
            ) {
                Button(
                    onClick = onVolver,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Volver")
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(24.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Empleado: $nombre")
            Text("Salario bruto: " + String.format(Locale.US, "%.2f €", salarioBruto))
            Text("Retención IRPF (25%): " + String.format(Locale.US, "%.2f €", irpf))
            Text("Deducciones: " + String.format(Locale.US, "%.2f €", deducciones))
            Text("Salario neto: " + String.format(Locale.US, "%.2f €", salarioNeto))
        }
    }
}

