package com.example.lienzocraft

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProyectoAdapter(
    private var proyectos: List<Proyecto>,
    private val onEditClick: (Proyecto) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val VIEW_TYPE_GRANDE = 1
        const val VIEW_TYPE_PEQUENO = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (proyectos[position].isGrandeEnTodos) VIEW_TYPE_GRANDE else VIEW_TYPE_PEQUENO
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_GRANDE) {
            val view = inflater.inflate(R.layout.item_proyecto_grande, parent, false)
            ProyectoGrandeViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_proyecto_pequeno, parent, false)
            ProyectoPequenoViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val proyecto = proyectos[position]
        if (holder is ProyectoGrandeViewHolder) {
            holder.bind(proyecto, onEditClick)
        } else if (holder is ProyectoPequenoViewHolder) {
            holder.bind(proyecto, onEditClick)
        }
    }

    override fun getItemCount(): Int = proyectos.size

    fun actualizarProyectos(nuevaLista: List<Proyecto>) {
        proyectos = nuevaLista
        notifyDataSetChanged()
    }

    class ProyectoGrandeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgProyecto: View = itemView.findViewById(R.id.img_proyecto)
        private val cardEstado: com.google.android.material.card.MaterialCardView = itemView.findViewById(R.id.card_estado)
        private val tvEstado: TextView = itemView.findViewById(R.id.tv_estado)
        private val tvRedSocial: TextView = itemView.findViewById(R.id.tv_red_social)
        private val tvTitulo: TextView = itemView.findViewById(R.id.tv_titulo)
        private val btnEditar: com.google.android.material.button.MaterialButton = itemView.findViewById(R.id.btn_editar)

        fun bind(proyecto: Proyecto, onEditClick: (Proyecto) -> Unit) {
            imgProyecto.setBackgroundColor(Color.parseColor(proyecto.colorHex))
            tvEstado.text = proyecto.estado.nombre
            tvRedSocial.text = proyecto.redSocial
            tvTitulo.text = proyecto.titulo
            
            val context = itemView.context
            when (proyecto.estado) {
                EstadoProyecto.PUBLICADO -> {
                    cardEstado.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(context, R.color.accent_lime))
                    tvEstado.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.text_primary))
                }
                EstadoProyecto.BORRADOR -> {
                    cardEstado.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(context, R.color.white))
                    tvEstado.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.text_primary))
                }
                EstadoProyecto.PROGRAMADO -> {
                    cardEstado.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(context, R.color.text_primary))
                    tvEstado.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.white))
                }
            }
            
            btnEditar.setOnClickListener {
                onEditClick(proyecto)
            }
        }
    }

    class ProyectoPequenoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgProyecto: View = itemView.findViewById(R.id.img_proyecto)
        private val cardEstado: com.google.android.material.card.MaterialCardView = itemView.findViewById(R.id.card_estado)
        private val tvEstado: TextView = itemView.findViewById(R.id.tv_estado)
        private val tvRedSocial: TextView = itemView.findViewById(R.id.tv_red_social)
        private val tvTitulo: TextView = itemView.findViewById(R.id.tv_titulo)
        private val btnEditar: com.google.android.material.button.MaterialButton = itemView.findViewById(R.id.btn_editar)

        fun bind(proyecto: Proyecto, onEditClick: (Proyecto) -> Unit) {
            imgProyecto.setBackgroundColor(Color.parseColor(proyecto.colorHex))
            tvEstado.text = proyecto.estado.nombre
            tvRedSocial.text = proyecto.redSocial
            tvTitulo.text = proyecto.titulo
            
            val context = itemView.context
            when (proyecto.estado) {
                EstadoProyecto.PUBLICADO -> {
                    cardEstado.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(context, R.color.accent_lime))
                    tvEstado.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.text_primary))
                }
                EstadoProyecto.BORRADOR -> {
                    cardEstado.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(context, R.color.white))
                    tvEstado.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.text_primary))
                }
                EstadoProyecto.PROGRAMADO -> {
                    cardEstado.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(context, R.color.text_primary))
                    tvEstado.setTextColor(androidx.core.content.ContextCompat.getColor(context, R.color.white))
                }
            }
            
            btnEditar.setOnClickListener {
                onEditClick(proyecto)
            }
        }
    }
}
