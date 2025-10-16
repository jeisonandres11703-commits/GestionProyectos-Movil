package com.example.gestionproyectos.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "proyectos")
data class Proyecto(
    @PrimaryKey(autoGenerate = true)
    val idProyecto: Int = 0,
    val nombreProyecto: String,
    val nombreEmpresa: String,
    val fechaInicio: String,
    val duracionMeses: Int,
    val fechaFinal: String,
    val presupuesto: Double
) {
    companion object {
        const val VALOR_MES = 5_000_000.0

        fun calcularFechaFinal(fechaInicio: String, duracionMeses: Int): String {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val calendar = Calendar.getInstance()

            try {
                calendar.time = sdf.parse(fechaInicio) ?: Date()
                calendar.add(Calendar.MONTH, duracionMeses)
                return sdf.format(calendar.time)
            } catch (e: Exception) {
                return fechaInicio
            }
        }

        fun calcularPresupuesto(duracionMeses: Int): Double {
            return duracionMeses * VALOR_MES
        }

        fun parseFecha(dia: Int, mes: Int, anio: Int): String {
            return String.format("%02d/%02d/%04d", dia, mes, anio)
        }
    }
}