package com.coders.two.movies.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.coders.two.movies.data.model.MovieDto
import com.coders.two.movies.presentation.details.DetailsScreen
import com.coders.two.movies.presentation.main.MainScreen
import com.coders.two.movies.presentation.theme.Coders2MoviesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController: NavHostController = rememberNavController()

            Coders2MoviesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    NavHost(
                        navController = navController,
                        startDestination = "main"
                    ) {
                        composable("main") {
                            MainScreen(
                                modifier = Modifier.padding(innerPadding)
                            ) {
                                navController.currentBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("movie", it)
                                navController.navigate("detail")
                            }
                        }

                        composable("detail") {
                            val movie =
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.get<MovieDto>("movie")

                            if (movie != null) {
                                DetailsScreen(
                                    movie = movie,
                                    modifier = Modifier.padding(innerPadding)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}