package com.example.lienzocraft

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class AgendaFragment : Fragment() {

    private lateinit var calendarView: CalendarView
    private lateinit var rvAgendaProyectos: RecyclerView
    private lateinit var tvFechaSeleccionada: TextView
    private lateinit var tvEmptyState: TextView
    private lateinit var adapter: AgendaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_agenda, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        calendarView = view.findViewById(R.id.calendar_view)
        rvAgendaProyectos = view.findViewById(R.id.rv_agenda_proyectos)
        tvFechaSeleccionada = view.findViewById(R.id.tv_fecha_seleccionada)
        tvEmptyState = view.findViewById(R.id.tv_empty_state)

        // Configurar RecyclerView con formato de lista vertical
        val layoutManager = androidx.recyclerview.widget.LinearLayoutManager(requireContext())
        rvAgendaProyectos.layoutManager = layoutManager
        
        adapter = AgendaAdapter(emptyList())
        rvAgendaProyectos.adapter = adapter

        // Filtrar con la fecha actual al abrir la pantalla
        filtrarProyectosPorFecha(ProyectoRepository.hoyStr)
        tvFechaSeleccionada.text = "Proyectos del ${ProyectoRepository.hoyStr}"

        // Escuchar cambios en el calendario
        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val mesFormateado = String.format("%02d", month + 1)
            val diaFormateado = String.format("%02d", dayOfMonth)
            val fechaSeleccionada = "$diaFormateado/$mesFormateado/$year"
            
            tvFechaSeleccionada.text = "Proyectos del $fechaSeleccionada"
            filtrarProyectosPorFecha(fechaSeleccionada)
        }
    }

    private fun filtrarProyectosPorFecha(fecha: String) {
        // Obtenemos de la "base de datos" global los proyectos que coincidan con la fecha.
        // Convertimos todos a vista pequeña (isGrandeEnTodos = false) para la cuadrícula
        val filtrados = ProyectoRepository.proyectos
            .filter { it.fechaEvento == fecha }
            .map { it.copy(isGrandeEnTodos = false) }

        if (filtrados.isEmpty()) {
            tvEmptyState.visibility = View.VISIBLE
            rvAgendaProyectos.visibility = View.GONE
        } else {
            tvEmptyState.visibility = View.GONE
            rvAgendaProyectos.visibility = View.VISIBLE
            adapter.actualizarProyectos(filtrados)
        }
    }
}