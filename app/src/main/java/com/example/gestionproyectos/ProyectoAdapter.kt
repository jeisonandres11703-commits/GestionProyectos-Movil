package com.example.gestionproyectos.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.gestionproyectos.data.Proyecto
import com.example.gestionproyectos.databinding.ItemProyectoBinding
import java.text.NumberFormat
import java.util.*

class ProyectoAdapter(
    private val onEditClick: (Proyecto) -> Unit,
    private val onDeleteClick: (Proyecto) -> Unit
) : ListAdapter<Proyecto, ProyectoAdapter.ProyectoViewHolder>(ProyectoComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProyectoViewHolder {
        val binding = ItemProyectoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ProyectoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProyectoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ProyectoViewHolder(private val binding: ItemProyectoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(proyecto: Proyecto) {
            val currencyFormat = NumberFormat.getCurrencyInstance(Locale("es", "CO"))

            binding.apply {
                tvNombreProyecto.text = proyecto.nombreProyecto
                tvEmpresa.text = proyecto.nombreEmpresa
                tvFechaInicio.text = "Inicio: ${proyecto.fechaInicio}"
                tvFechaFinal.text = "Fin: ${proyecto.fechaFinal}"
                tvDuracion.text = "${proyecto.duracionMeses} ${if(proyecto.duracionMeses == 1) "mes" else "meses"}"
                tvPresupuesto.text = currencyFormat.format(proyecto.presupuesto)

                btnEditar.setOnClickListener { onEditClick(proyecto) }
                btnEliminar.setOnClickListener { onDeleteClick(proyecto) }
            }
        }
    }

    class ProyectoComparator : DiffUtil.ItemCallback<Proyecto>() {
        override fun areItemsTheSame(oldItem: Proyecto, newItem: Proyecto): Boolean {
            return oldItem.idProyecto == newItem.idProyecto
        }

        override fun areContentsTheSame(oldItem: Proyecto, newItem: Proyecto): Boolean {
            return oldItem == newItem
        }
    }
}
