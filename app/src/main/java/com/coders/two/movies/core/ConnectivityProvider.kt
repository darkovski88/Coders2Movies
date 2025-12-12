package com.coders.two.movies.core

import kotlinx.coroutines.flow.Flow

interface ConnectivityProvider {
    val isConnected: Flow<Boolean>
    fun isCurrentlyConnected(): Boolean
}