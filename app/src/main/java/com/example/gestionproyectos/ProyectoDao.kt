package com.example.gestionproyectos.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ProyectoDao {
    @Query("SELECT * FROM proyectos ORDER BY idProyecto DESC")
    fun getAllProyectos(): LiveData<List<Proyecto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProyecto(proyecto: Proyecto)

    @Update
    suspend fun updateProyecto(proyecto: Proyecto)

    @Delete
    suspend fun deleteProyecto(proyecto: Proyecto)

    @Query("SELECT * FROM proyectos WHERE idProyecto = :id")
    suspend fun getProyectoById(id: Int): Proyecto?

    @Query("SELECT COUNT(*) FROM proyectos")
    fun getProyectosCount(): LiveData<Int>
}