package com.ed.anotationsapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alex3g.anotationsapp.ui.database.AppDatabase
import com.alex3g.anotationsapp.ui.database.NoteDao
import com.alex3g.anotationsapp.ui.screens.details.DetailsScreen
import com.alex3g.anotationsapp.ui.screens.details.DetailsViewModel
import com.alex3g.anotationsapp.ui.screens.home.HomeScreen
import com.alex3g.anotationsapp.ui.screens.home.HomeViewModel
import com.alex3g.anotationsapp.ui.screens.newNode.AddNoteScreen
import com.alex3g.anotationsapp.ui.screens.newNode.AddNoteViewModel
import com.alex3g.anotationsapp.ui.theme.AnotationsAppTheme

class MainActivity : ComponentActivity() {

    lateinit var dao: NoteDao
    lateinit var homeViewModel: HomeViewModel
    lateinit var addNoteViewModel: AddNoteViewModel
    lateinit var detailsViewModel: DetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dao = AppDatabase.getDatabase(this.applicationContext).noteDao()
        homeViewModel = HomeViewModel(dao)
        addNoteViewModel = AddNoteViewModel(dao)
        detailsViewModel = DetailsViewModel(dao)

        enableEdgeToEdge()
        setContent {
            AnotationsAppTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = "home") {
                    // Tela Home
                    composable("home") {
                        HomeScreen(
                            viewModel = homeViewModel,
                            onAddClick = { navController.navigate("add-note") },
                            onNoteClick = { noteId ->
                                navController.navigate("details/$noteId")
                            }
                        )
                    }

                    // Tela Adicionar Nota
                    composable("add-note") {
                        AddNoteScreen(
                            viewModel = addNoteViewModel,
                            onNavigateBack = {
                                navController.navigate("home") {
                                    popUpTo("home") {
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }

                    // Tela de Detalhes
                    composable("details/{noteId}") { backStackEntry ->
                        val noteId = backStackEntry.arguments?.getString("noteId")?.toInt() ?: 0
                        detailsViewModel.fetchNoteDetails(noteId) // Atualiza os dados da nota

                        DetailsScreen(
                            viewModel = detailsViewModel,
                            noteId = noteId,
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
