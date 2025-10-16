package com.example.gestionproyectos.data

import androidx.lifecycle.LiveData

class ProyectoRepository(private val proyectoDao: ProyectoDao) {
    val allProyectos: LiveData<List<Proyecto>> = proyectoDao.getAllProyectos()
    val proyectosCount: LiveData<Int> = proyectoDao.getProyectosCount()

    suspend fun insert(proyecto: Proyecto) {
        proyectoDao.insertProyecto(proyecto)
    }

    suspend fun update(proyecto: Proyecto) {
        proyectoDao.updateProyecto(proyecto)
    }

    suspend fun delete(proyecto: Proyecto) {
        proyectoDao.deleteProyecto(proyecto)
    }

    suspend fun getById(id: Int): Proyecto? {
        return proyectoDao.getProyectoById(id)
    }
}