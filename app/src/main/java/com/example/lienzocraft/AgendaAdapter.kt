package com.example.lienzocraft

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class AgendaAdapter(private var proyectos: List<Proyecto>) : RecyclerView.Adapter<AgendaAdapter.AgendaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_proyecto_agenda, parent, false)
        return AgendaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AgendaViewHolder, position: Int) {
        holder.bind(proyectos[position])
    }

    override fun getItemCount(): Int = proyectos.size

    fun actualizarProyectos(nuevaLista: List<Proyecto>) {
        proyectos = nuevaLista
        notifyDataSetChanged()
    }

    class AgendaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgProyecto: View = itemView.findViewById(R.id.img_proyecto_agenda)
        private val tvTitulo: TextView = itemView.findViewById(R.id.tv_titulo_agenda)
        private val tvRedSocial: TextView = itemView.findViewById(R.id.tv_red_social_agenda)
        private val tvFechaContexto: TextView = itemView.findViewById(R.id.tv_fecha_contexto)
        private val cardEstado: MaterialCardView = itemView.findViewById(R.id.card_estado_agenda)
        private val tvEstado: TextView = itemView.findViewById(R.id.tv_estado_agenda)

        fun bind(proyecto: Proyecto) {
            imgProyecto.setBackgroundColor(Color.parseColor(proyecto.colorHex))
            tvTitulo.text = proyecto.titulo
            tvRedSocial.text = proyecto.redSocial
            tvEstado.text = proyecto.estado.nombre
            
            val context = itemView.context
            
            val timeStr = if (proyecto.horaEvento.isNotEmpty()) proyecto.horaEvento else "12:00"
            val dateStr = if (proyecto.fechaEvento.isNotEmpty()) proyecto.fechaEvento else "Hoy"
            
            when (proyecto.estado) {
                EstadoProyecto.PUBLICADO -> {
                    cardEstado.setCardBackgroundColor(ContextCompat.getColor(context, R.color.accent_lime))
                    tvEstado.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                    tvFechaContexto.text = "Publicado: $dateStr a las $timeStr"
                }
                EstadoProyecto.BORRADOR -> {
                    cardEstado.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white))
                    cardEstado.strokeWidth = (1 * context.resources.displayMetrics.density).toInt()
                    cardEstado.strokeColor = ContextCompat.getColor(context, R.color.gray_light)
                    tvEstado.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
                    tvFechaContexto.text = "Creado: $dateStr a las $timeStr"
                }
                EstadoProyecto.PROGRAMADO -> {
                    cardEstado.setCardBackgroundColor(ContextCompat.getColor(context, R.color.text_primary))
                    tvEstado.setTextColor(ContextCompat.getColor(context, R.color.white))
                    tvFechaContexto.text = "Se publicará: $dateStr a las $timeStr"
                }
            }
        }
    }
}