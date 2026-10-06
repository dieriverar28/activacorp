package com.example.activacorp.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.activacorp.pantallas.InicioScreen
import com.example.activacorp.pantallas.OportunidadesScreen
import com.example.activacorp.pantallas.CrearOportunidadScreen


@Composable
public fun Navegacion() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        composable("inicio") {
            InicioScreen(
                onIrAOportunidades = { navController.navigate("oportunidades") },
                onIrACrear = { navController.navigate("crear_oportunidad") }
            )
        }
        composable("oportunidades") {
            OportunidadesScreen(
                onIrACrear = { navController.navigate("crear_oportunidad") },
                onVolver = { navController.popBackStack() }
            )
        }
        composable("crear_oportunidad") {
            CrearOportunidadScreen(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
