package com.example.activacorp.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.activacorp.pantallas.CrearOportunidadScreen
import com.example.activacorp.pantallas.DashboardScreen
import com.example.activacorp.pantallas.DetalleOportunidadScreen
import com.example.activacorp.pantallas.LoginScreen
import com.example.activacorp.pantallas.OportunidadesScreen

@Composable
fun Navegacion() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginExitoso = {
                    // Se quita el login de la pila para que "atrás" no vuelva a él
                    navController.navigate("dashboard") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("dashboard") {
            DashboardScreen(
                onIrAOportunidades = { navController.navigate("oportunidades") },
                onIrACrear = { navController.navigate("crear_oportunidad") },
                onCerrarSesion = {
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }
        composable("oportunidades") {
            OportunidadesScreen(
                onIrADetalle = { id -> navController.navigate("detalle/$id") },
                onIrACrear = { navController.navigate("crear_oportunidad") },
                onVolver = { navController.popBackStack() }
            )
        }
        composable(
            route = "detalle/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) {
            DetalleOportunidadScreen(
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
