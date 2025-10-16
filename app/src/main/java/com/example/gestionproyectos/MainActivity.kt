package com.example.gestionproyectos

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gestionproyectos.adapter.ProyectoAdapter
import com.example.gestionproyectos.data.Proyecto
import com.example.gestionproyectos.databinding.ActivityMainBinding
import com.example.gestionproyectos.viewmodel.ProyectoViewModel
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ProyectoViewModel
    private lateinit var adapter: ProyectoAdapter
    private var proyectoEditando: Proyecto? = null
    private var fechaInicioSeleccionada = ""
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[ProyectoViewModel::class.java]
        setupRecyclerView()
        setupListeners()
        observeData()
        actualizarInfoValorMes()
    }

    private fun setupRecyclerView() {
        adapter = ProyectoAdapter(
            onEditClick = { proyecto -> editarProyecto(proyecto) },
            onDeleteClick = { proyecto -> confirmarEliminar(proyecto) }
        )

        binding.recyclerViewProyectos.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupListeners() {
        binding.btnNuevoProyecto.setOnClickListener {
            mostrarFormulario()
        }

        binding.btnSeleccionarFecha.setOnClickListener {
            mostrarDatePicker()
        }

        binding.etDuracion.setOnFocusChangeListener { _, _ ->
            calcularYMostrarFechaFinal()
        }

        binding.btnGuardar.setOnClickListener {
            if (validarCampos()) {
                guardarProyecto()
            }
        }

        binding.btnCancelar.setOnClickListener {
            ocultarFormulario()
        }
    }

    private fun observeData() {
        viewModel.allProyectos.observe(this) { proyectos ->
            proyectos?.let {
                adapter.submitList(it)

                if (it.isEmpty()) {
                    binding.tvEmptyState.visibility = View.VISIBLE
                    binding.recyclerViewProyectos.visibility = View.GONE
                } else {
                    binding.tvEmptyState.visibility = View.GONE
                    binding.recyclerViewProyectos.visibility = View.VISIBLE
                }
            }
        }

        viewModel.proyectosCount.observe(this) { count ->
            actualizarContador(count ?: 0)
        }
    }

    private fun actualizarInfoValorMes() {
        val formato = NumberFormat.getCurrencyInstance(Locale("es", "CO"))
        binding.tvInfoValorMes.text = "Valor por mes: ${formato.format(Proyecto.VALOR_MES)}"
    }

    private fun mostrarDatePicker() {
        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                fechaInicioSeleccionada = Proyecto.parseFecha(dayOfMonth, month + 1, year)
                binding.tvFechaSeleccionada.text = "Fecha seleccionada: $fechaInicioSeleccionada"
                binding.tvFechaSeleccionada.visibility = View.VISIBLE
                calcularYMostrarFechaFinal()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun calcularYMostrarFechaFinal() {
        val duracionStr = binding.etDuracion.text.toString()
        if (fechaInicioSeleccionada.isNotEmpty() && duracionStr.isNotEmpty()) {
            val duracion = duracionStr.toIntOrNull() ?: 0
            if (duracion > 0) {
                val fechaFinal = Proyecto.calcularFechaFinal(fechaInicioSeleccionada, duracion)
                val presupuesto = Proyecto.calcularPresupuesto(duracion)
                val formato = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

                binding.tvCalculos.text = buildString {
                    append("CÁLCULOS AUTOMÁTICOS\n\n")
                    append("Fecha Final: $fechaFinal\n")
                    append("Presupuesto: ${formato.format(presupuesto)}\n")
                    append("Duración: $duracion ${if(duracion == 1) "mes" else "meses"}")
                }
                binding.tvCalculos.visibility = View.VISIBLE
            } else {
                binding.tvCalculos.visibility = View.GONE
            }
        } else {
            binding.tvCalculos.visibility = View.GONE
        }
    }

    private fun validarCampos(): Boolean {
        val nombreProyecto = binding.etNombreProyecto.text.toString().trim()
        val nombreEmpresa = binding.etNombreEmpresa.text.toString().trim()
        val duracionStr = binding.etDuracion.text.toString()

        return when {
            nombreProyecto.isEmpty() -> {
                Toast.makeText(this, " Ingresa el nombre del proyecto", Toast.LENGTH_SHORT).show()
                binding.etNombreProyecto.requestFocus()
                false
            }
            nombreEmpresa.isEmpty() -> {
                Toast.makeText(this, "Ingresa el nombre de la empresa", Toast.LENGTH_SHORT).show()
                binding.etNombreEmpresa.requestFocus()
                false
            }
            fechaInicioSeleccionada.isEmpty() -> {
                Toast.makeText(this, "Selecciona la fecha de inicio", Toast.LENGTH_SHORT).show()
                false
            }
            duracionStr.isEmpty() || duracionStr.toIntOrNull() == null -> {
                Toast.makeText(this, "Ingresa una duración válida", Toast.LENGTH_SHORT).show()
                binding.etDuracion.requestFocus()
                false
            }
            duracionStr.toInt() <= 0 -> {
                Toast.makeText(this, "La duración debe ser mayor a 0 meses", Toast.LENGTH_SHORT).show()
                binding.etDuracion.requestFocus()
                false
            }
            else -> true
        }
    }

    private fun guardarProyecto() {
        val nombreProyecto = binding.etNombreProyecto.text.toString().trim()
        val nombreEmpresa = binding.etNombreEmpresa.text.toString().trim()
        val duracion = binding.etDuracion.text.toString().toInt()
        val fechaFinal = Proyecto.calcularFechaFinal(fechaInicioSeleccionada, duracion)
        val presupuesto = Proyecto.calcularPresupuesto(duracion)

        val proyecto = if (proyectoEditando != null) {
            proyectoEditando!!.copy(
                nombreProyecto = nombreProyecto,
                nombreEmpresa = nombreEmpresa,
                fechaInicio = fechaInicioSeleccionada,
                duracionMeses = duracion,
                fechaFinal = fechaFinal,
                presupuesto = presupuesto
            )
        } else {
            Proyecto(
                nombreProyecto = nombreProyecto,
                nombreEmpresa = nombreEmpresa,
                fechaInicio = fechaInicioSeleccionada,
                duracionMeses = duracion,
                fechaFinal = fechaFinal,
                presupuesto = presupuesto
            )
        }

        if (proyectoEditando != null) {
            viewModel.update(proyecto)
            Toast.makeText(this, "Proyecto actualizado correctamente", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.insert(proyecto)
            Toast.makeText(this, "Proyecto creado correctamente", Toast.LENGTH_SHORT).show()
        }

        ocultarFormulario()
    }

    private fun editarProyecto(proyecto: Proyecto) {
        proyectoEditando = proyecto
        mostrarFormulario()

        binding.etNombreProyecto.setText(proyecto.nombreProyecto)
        binding.etNombreEmpresa.setText(proyecto.nombreEmpresa)
        binding.etDuracion.setText(proyecto.duracionMeses.toString())
        fechaInicioSeleccionada = proyecto.fechaInicio
        binding.tvFechaSeleccionada.text = "Fecha seleccionada: $fechaInicioSeleccionada"
        binding.tvFechaSeleccionada.visibility = View.VISIBLE

        binding.btnGuardar.text = "Actualizar Proyecto"
        binding.tvTituloFormulario.text = "Editar Proyecto"

        calcularYMostrarFechaFinal()
    }

    private fun confirmarEliminar(proyecto: Proyecto) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Proyecto")
            .setMessage("¿Estás seguro de eliminar el proyecto '${proyecto.nombreProyecto}'?\n\nEsta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                viewModel.delete(proyecto)
                Toast.makeText(this, "Proyecto eliminado", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancelar", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun mostrarFormulario() {
        binding.cardFormulario.visibility = View.VISIBLE
        binding.btnNuevoProyecto.visibility = View.GONE
        binding.etNombreProyecto.requestFocus()
    }

    private fun ocultarFormulario() {
        binding.cardFormulario.visibility = View.GONE
        binding.btnNuevoProyecto.visibility = View.VISIBLE
        limpiarFormulario()
    }

    private fun limpiarFormulario() {
        binding.etNombreProyecto.text?.clear()
        binding.etNombreEmpresa.text?.clear()
        binding.etDuracion.text?.clear()
        binding.tvFechaSeleccionada.visibility = View.GONE
        binding.tvCalculos.visibility = View.GONE
        fechaInicioSeleccionada = ""
        proyectoEditando = null
        binding.btnGuardar.text = "Crear Proyecto"
        binding.tvTituloFormulario.text = "Nuevo Proyecto"
    }

    private fun actualizarContador(cantidad: Int) {
        binding.tvContador.text = "Total de proyectos registrados: $cantidad"
    }
}