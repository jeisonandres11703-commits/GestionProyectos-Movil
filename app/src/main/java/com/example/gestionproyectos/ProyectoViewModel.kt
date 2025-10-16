package com.example.gestionproyectos.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.gestionproyectos.data.AppDatabase
import com.example.gestionproyectos.data.Proyecto
import com.example.gestionproyectos.data.ProyectoDao
import com.example.gestionproyectos.data.ProyectoRepository
import kotlinx.coroutines.launch

class ProyectoViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ProyectoRepository
    val allProyectos: LiveData<List<Proyecto>>
    val proyectosCount: LiveData<Int>

    init {
        val proyectoDao = AppDatabase.getDatabase(application).proyectoDao()
        repository = ProyectoRepository(proyectoDao)
        allProyectos = repository.allProyectos
        proyectosCount = repository.proyectosCount
    }

    fun insert(proyecto: Proyecto) = viewModelScope.launch {
        repository.insert(proyecto)
    }

    fun update(proyecto: Proyecto) = viewModelScope.launch {
        repository.update(proyecto)
    }

    fun delete(proyecto: Proyecto) = viewModelScope.launch {
        repository.delete(proyecto)
    }
}
