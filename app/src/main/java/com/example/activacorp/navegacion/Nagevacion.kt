package com.example.activacorp.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.activacorp.pantallas.CrearContactoScreen
import com.example.activacorp.pantallas.CrearOportunidadScreen
import com.example.activacorp.pantallas.CrearOrganizacionScreen
import com.example.activacorp.pantallas.DashboardScreen
import com.example.activacorp.pantallas.DetalleOportunidadScreen
import com.example.activacorp.pantallas.DetalleOrganizacionScreen
import com.example.activacorp.pantallas.EditarOportunidadScreen
import com.example.activacorp.pantallas.LoginScreen
import com.example.activacorp.pantallas.OportunidadesScreen
import com.example.activacorp.pantallas.OrganizacionesScreen

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
                onIrAOrganizaciones = { navController.navigate("organizaciones") },
                onCerrarSesion = {
                    navController.navigate("login") {
                        popUpTo("dashboard") { inclusive = true }
                    }
                }
            )
        }

        // ----- Oportunidades -----
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
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            DetalleOportunidadScreen(
                onIrAEditar = { navController.navigate("editar_oportunidad/$id") },
                onVolver = { navController.popBackStack() }
            )
        }
        composable(
            route = "editar_oportunidad/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt("id") ?: 0
            EditarOportunidadScreen(
                oportunidadId = id,
                onVolver = { navController.popBackStack() },
                onGuardarExito = { navController.popBackStack() }
            )
        }
        composable("crear_oportunidad") {
            CrearOportunidadScreen(
                onVolver = { navController.popBackStack() }
            )
        }

        // ----- Organizaciones y contactos -----
        composable("organizaciones") {
            OrganizacionesScreen(
                onIrADetalle = { id -> navController.navigate("organizacion/$id") },
                onIrACrear = { navController.navigate("crear_organizacion") },
                onVolver = { navController.popBackStack() }
            )
        }
        composable("crear_organizacion") {
            CrearOrganizacionScreen(
                onVolver = { navController.popBackStack() }
            )
        }
        composable(
            route = "organizacion/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) {
            DetalleOrganizacionScreen(
                onIrACrearContacto = { id -> navController.navigate("crear_contacto/$id") },
                onVolver = { navController.popBackStack() }
            )
        }
        composable(
            route = "crear_contacto/{organizacionId}",
            arguments = listOf(
                navArgument("organizacionId") { type = NavType.IntType }
            )
        ) {
            CrearContactoScreen(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
