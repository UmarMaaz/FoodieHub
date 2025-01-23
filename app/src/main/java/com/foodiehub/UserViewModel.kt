package com.foodiehub

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val userDao = Room.databaseBuilder(application, AppDatabase::class.java, "app_database")
        .build().userDao()

    val currentUser = mutableStateOf<User?>(null)


    fun signUp(username: String, email: String, password: String) {
        viewModelScope.launch {
            val user = User(username = username, email = email, password = password)
            userDao.insertUser(user)
        }
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            val user = userDao.getUserByEmailAndPassword(email, password)
            if (user != null) {
                currentUser.value = user
            } else {
                currentUser.value = null // Ensure no user is logged in with invalid credentials
            }
        }
    }

}
