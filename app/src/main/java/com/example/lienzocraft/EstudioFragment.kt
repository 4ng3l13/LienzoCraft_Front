package com.example.lienzocraft

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.card.MaterialCardView

class EstudioFragment : Fragment() {

    // LANZADOR DE CÁMARA y GALERÍA...
    private val tomarFotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            Toast.makeText(requireContext(), "¡Foto capturada con éxito!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "Se canceló la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    private val abrirGaleriaLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            Toast.makeText(requireContext(), "¡Imagen seleccionada desde la galería!", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(requireContext(), "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
        }
    }

    // Vistas de los filtros
    private lateinit var filterTodos: MaterialCardView
    private lateinit var filterPublicados: MaterialCardView
    private lateinit var filterBorradores: MaterialCardView
    private lateinit var tvFilterTodos: TextView
    private lateinit var tvFilterPublicados: TextView
    private lateinit var tvFilterBorradores: TextView

    // RecyclerView y Adapter
    private lateinit var rvProyectos: RecyclerView
    private lateinit var adapter: ProyectoAdapter

    // Utilizamos el repositorio compartido para que Agenda también los vea
    private val todosLosProyectos = ProyectoRepository.proyectos

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_estudio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnCamara = view.findViewById<ImageView>(R.id.btn_camara_estudio)
        btnCamara.setOnClickListener { mostrarMenuOpcionesFoto() }

        // Inicializar vistas de filtros
        filterTodos = view.findViewById(R.id.filter_todos)
        filterPublicados = view.findViewById(R.id.filter_publicados)
        filterBorradores = view.findViewById(R.id.filter_borradores)
        tvFilterTodos = view.findViewById(R.id.tv_filter_todos)
        tvFilterPublicados = view.findViewById(R.id.tv_filter_publicados)
        tvFilterBorradores = view.findViewById(R.id.tv_filter_borradores)

        // Configurar RecyclerView
        rvProyectos = view.findViewById(R.id.rv_proyectos)
        val layoutManager = GridLayoutManager(requireContext(), 2)
        
        // Hacemos que los ítems grandes ocupen las 2 columnas del grid
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter.getItemViewType(position) == ProyectoAdapter.VIEW_TYPE_GRANDE) 2 else 1
            }
        }
        rvProyectos.layoutManager = layoutManager

        adapter = ProyectoAdapter(todosLosProyectos) { proyecto ->
            mostrarBottomSheetEdicion(proyecto)
        }
        rvProyectos.adapter = adapter

        // Click listeners para los filtros
        filterTodos.setOnClickListener {
            seleccionarFiltro(FilterType.TODOS)
        }
        filterPublicados.setOnClickListener {
            seleccionarFiltro(FilterType.PUBLICADOS)
        }
        filterBorradores.setOnClickListener {
            seleccionarFiltro(FilterType.BORRADORES)
        }

        // Seleccionar "Todos" por defecto al abrir la pantalla
        seleccionarFiltro(FilterType.TODOS)
    }

    private enum class FilterType { TODOS, PUBLICADOS, BORRADORES }
    private var currentFilter = FilterType.TODOS

    private fun seleccionarFiltro(tipo: FilterType) {
        currentFilter = tipo
        // Restaurar todos los estilos a inactivos
        setFilterInactive(filterTodos, tvFilterTodos)
        setFilterInactive(filterPublicados, tvFilterPublicados)
        setFilterInactive(filterBorradores, tvFilterBorradores)

        // Aplicar estilo activo y filtrar lista
        when (tipo) {
            FilterType.TODOS -> {
                setFilterActive(filterTodos, tvFilterTodos)
                // En 'Todos' mostramos TODOS los proyectos
                // El primer elemento será grande (id == 1, o position == 0), los demás pequeños
                val todosFormateados = todosLosProyectos.mapIndexed { index, proyecto -> 
                    proyecto.copy(isGrandeEnTodos = index == 0) 
                }
                adapter.actualizarProyectos(todosFormateados) 
            }
            FilterType.PUBLICADOS -> {
                setFilterActive(filterPublicados, tvFilterPublicados)
                // Filtramos publicados y hacemos que todos sean pequeños (ocupen 1 columna) como en el screenshot
                val filtrados = todosLosProyectos.filter { it.estado == EstadoProyecto.PUBLICADO }
                    .map { it.copy(isGrandeEnTodos = false) }
                adapter.actualizarProyectos(filtrados)
            }
            FilterType.BORRADORES -> {
                setFilterActive(filterBorradores, tvFilterBorradores)
                // Filtramos borradores (y programados si lo desean, pero la imagen dice solo borradores)
                val filtrados = todosLosProyectos.filter { it.estado == EstadoProyecto.BORRADOR }
                    .map { it.copy(isGrandeEnTodos = false) }
                adapter.actualizarProyectos(filtrados)
            }
        }
    }

    private fun setFilterActive(card: MaterialCardView, text: TextView) {
        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.accent_lime))
        card.strokeWidth = 0
        text.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
        text.typeface = android.graphics.Typeface.DEFAULT_BOLD
    }

    private fun setFilterInactive(card: MaterialCardView, text: TextView) {
        card.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        val strokePx = (1 * resources.displayMetrics.density).toInt()
        card.strokeWidth = strokePx
        card.strokeColor = ContextCompat.getColor(requireContext(), R.color.gray_light)
        text.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
        text.typeface = android.graphics.Typeface.DEFAULT
    }

    private fun mostrarBottomSheetEdicion(proyecto: Proyecto) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_editar_proyecto, null)
        dialog.setContentView(view)

        // Close button
        view.findViewById<View>(R.id.btn_close_edit).setOnClickListener { dialog.dismiss() }

        // Setup fields
        val imgPreview = view.findViewById<View>(R.id.img_preview)
        val etTitulo = view.findViewById<TextView>(R.id.et_titulo)
        val etTexto = view.findViewById<TextView>(R.id.et_texto)
        val tvCharCount = view.findViewById<TextView>(R.id.tv_char_count)

        imgPreview.setBackgroundColor(android.graphics.Color.parseColor(proyecto.colorHex))
        etTitulo.text = proyecto.titulo
        etTexto.text = proyecto.texto
        tvCharCount.text = "${proyecto.texto.length}/220"

        // Setup platforms
        val llPlataformas = view.findViewById<android.widget.LinearLayout>(R.id.ll_plataformas)
        val availablePlatforms = listOf("Instagram", "TikTok", "Pinterest", "LinkedIn")
        val selectedPlatforms = proyecto.redesSociales.toMutableSet() // Track selections

        availablePlatforms.forEach { platformName ->
            val chipView = layoutInflater.inflate(R.layout.item_plataforma_chip, llPlataformas, false) as MaterialCardView
            val tvName = chipView.findViewById<TextView>(R.id.tv_plataforma_nombre)
            tvName.text = platformName

            fun updateChipStyle(isSelected: Boolean) {
                if (isSelected) {
                    chipView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.text_primary))
                    chipView.strokeWidth = 0
                    tvName.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                } else {
                    chipView.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
                    val strokePx = (1 * resources.displayMetrics.density).toInt()
                    chipView.strokeWidth = strokePx
                    chipView.strokeColor = ContextCompat.getColor(requireContext(), R.color.gray_light)
                    tvName.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary))
                }
            }

            // Init state
            updateChipStyle(selectedPlatforms.contains(platformName))

            chipView.setOnClickListener {
                if (selectedPlatforms.contains(platformName)) {
                    selectedPlatforms.remove(platformName)
                    updateChipStyle(false)
                } else {
                    selectedPlatforms.add(platformName)
                    updateChipStyle(true)
                }
            }
            llPlataformas.addView(chipView)
        }

        // Action containers
        val llBorrador = view.findViewById<View>(R.id.ll_actions_borrador)
        val llProgramado = view.findViewById<View>(R.id.ll_actions_programado)
        val llPublicado = view.findViewById<View>(R.id.ll_actions_publicado)

        llBorrador.visibility = View.GONE
        llProgramado.visibility = View.GONE
        llPublicado.visibility = View.GONE

        // Common handler for "Publicar ahora"
        val publicarAhoraAction = {
            proyecto.redesSociales = selectedPlatforms.toMutableList()
            if (proyecto.redesSociales.isEmpty()) {
                proyecto.estado = EstadoProyecto.BORRADOR
                Toast.makeText(requireContext(), "Sin redes seleccionadas. Vuelve a ser borrador.", Toast.LENGTH_SHORT).show()
            } else {
                proyecto.estado = EstadoProyecto.PUBLICADO
                Toast.makeText(requireContext(), "Publicado en ${proyecto.redesSociales.size} redes", Toast.LENGTH_SHORT).show()
            }
            
            // Actualizar el item original en la lista general
            val index = todosLosProyectos.indexOfFirst { it.id == proyecto.id }
            if (index != -1) {
                todosLosProyectos[index] = proyecto
            }
            
            seleccionarFiltro(currentFilter)
            dialog.dismiss()
        }

        when (proyecto.estado) {
            EstadoProyecto.BORRADOR -> {
                llBorrador.visibility = View.VISIBLE
                
                view.findViewById<View>(R.id.btn_publicar_ahora_borrador).setOnClickListener {
                    publicarAhoraAction()
                }
                
                view.findViewById<View>(R.id.btn_programar_borrador).setOnClickListener {
                    mostrarDialogoProgramar(proyecto, selectedPlatforms, dialog)
                }
                
                view.findViewById<View>(R.id.btn_eliminar_borrador).setOnClickListener {
                    todosLosProyectos.remove(proyecto)
                    seleccionarFiltro(currentFilter) // re-filter
                    dialog.dismiss()
                }
            }
            EstadoProyecto.PROGRAMADO -> {
                llProgramado.visibility = View.VISIBLE
                
                view.findViewById<View>(R.id.btn_publicar_ahora_prog).setOnClickListener {
                    publicarAhoraAction()
                }
                
                view.findViewById<View>(R.id.btn_volver_borrador_prog).setOnClickListener {
                    proyecto.redesSociales = selectedPlatforms.toMutableList()
                    proyecto.estado = EstadoProyecto.BORRADOR
                    
                    val index = todosLosProyectos.indexOfFirst { it.id == proyecto.id }
                    if (index != -1) {
                        todosLosProyectos[index] = proyecto
                    }
                    
                    seleccionarFiltro(currentFilter)
                    dialog.dismiss()
                }
                
                view.findViewById<View>(R.id.btn_editar_fecha_prog).setOnClickListener {
                    mostrarDialogoProgramar(proyecto, selectedPlatforms, dialog)
                }
            }
            EstadoProyecto.PUBLICADO -> {
                llPublicado.visibility = View.VISIBLE
                
                view.findViewById<View>(R.id.btn_guardar_publicado).setOnClickListener {
                    proyecto.redesSociales = selectedPlatforms.toMutableList()
                    // Si se desmarcan todas, pasa a borrador
                    if (proyecto.redesSociales.isEmpty()) {
                        proyecto.estado = EstadoProyecto.BORRADOR
                    }
                    
                    // Actualizar el item original en la lista general
                    val index = todosLosProyectos.indexOfFirst { it.id == proyecto.id }
                    if (index != -1) {
                        todosLosProyectos[index] = proyecto
                    }
                    
                    seleccionarFiltro(currentFilter) // refresh view
                    dialog.dismiss()
                    Toast.makeText(requireContext(), "Cambios guardados", Toast.LENGTH_SHORT).show()
                }
                
                view.findViewById<View>(R.id.btn_eliminar_publicacion).setOnClickListener {
                    todosLosProyectos.remove(proyecto)
                    seleccionarFiltro(currentFilter) // refresh view
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    private fun mostrarDialogoProgramar(
        proyecto: Proyecto,
        selectedPlatforms: Set<String>,
        bottomSheetDialog: BottomSheetDialog
    ) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_programar, null)
        builder.setView(view)
        
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        view.findViewById<View>(R.id.btn_cancelar_prog).setOnClickListener {
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.btn_guardar_prog).setOnClickListener {
            proyecto.redesSociales = selectedPlatforms.toMutableList()
            
            // Extraer fecha del input de programar
            val etFecha = view.findViewById<android.widget.EditText>(R.id.et_fecha)
            val fechaInput = etFecha.text.toString()
            if (fechaInput.isNotEmpty()) {
                proyecto.fechaEvento = fechaInput
            }

            if (proyecto.redesSociales.isEmpty()) {
                proyecto.estado = EstadoProyecto.BORRADOR
                Toast.makeText(requireContext(), "Sin redes seleccionadas. Vuelve a ser borrador.", Toast.LENGTH_SHORT).show()
            } else {
                proyecto.estado = EstadoProyecto.PROGRAMADO
                Toast.makeText(requireContext(), "Publicación programada", Toast.LENGTH_SHORT).show()
            }
            
            // Actualizar el item original en la lista general
            val index = todosLosProyectos.indexOfFirst { it.id == proyecto.id }
            if (index != -1) {
                todosLosProyectos[index] = proyecto
            }
            
            seleccionarFiltro(currentFilter)
            dialog.dismiss()
            bottomSheetDialog.dismiss()
        }

        dialog.show()
    }

    private fun mostrarMenuOpcionesFoto() {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_foto, null)
        dialog.setContentView(bottomSheetView)

        val btnClose = bottomSheetView.findViewById<View>(R.id.btn_close_sheet)
        val btnTomarFoto = bottomSheetView.findViewById<View>(R.id.card_tomar_foto)
        val btnGaleria = bottomSheetView.findViewById<View>(R.id.btn_galeria)

        btnClose.setOnClickListener { dialog.dismiss() }

        btnTomarFoto.setOnClickListener {
            dialog.dismiss()
            tomarFotoLauncher.launch(null)
        }

        btnGaleria.setOnClickListener {
            dialog.dismiss()
            abrirGaleriaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        dialog.show()
    }
}