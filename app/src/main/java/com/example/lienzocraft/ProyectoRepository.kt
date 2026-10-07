package com.example.lienzocraft

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object ProyectoRepository {
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val hoyStr = sdf.format(Date())
    
    val mananaCal = Calendar.getInstance()
    init {
        mananaCal.add(Calendar.DAY_OF_YEAR, 1)
    }
    val mananaStr = sdf.format(mananaCal.time)

    val proyectos = mutableListOf(
        Proyecto(1, "Ritual de mañana", mutableListOf("Instagram"), EstadoProyecto.PUBLICADO, "#E8C6CA", true, "Menos pasos, mejores resultados. Descubre nuestra nueva fórmula.", hoyStr, "09:30"),
        Proyecto(2, "Esencia nocturna", mutableListOf("Pinterest"), EstadoProyecto.BORRADOR, "#C1C8A3", false, "Tu momento de calma empieza con una rutina creada para reparar.", hoyStr, "18:00"),
        Proyecto(3, "Línea solar", mutableListOf("TikTok"), EstadoProyecto.PROGRAMADO, "#E6DCA7", false, "Protección solar para el día a día.", mananaStr, "14:15"),
        Proyecto(4, "Nueva fórmula", mutableListOf("Instagram"), EstadoProyecto.PUBLICADO, "#C4C4C4", false, "Descubre cómo lo preparamos.", hoyStr, "11:00")
    )
}
