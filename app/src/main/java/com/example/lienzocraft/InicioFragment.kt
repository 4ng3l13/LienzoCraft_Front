package com.example.lienzocraft

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetDialog

class InicioFragment : Fragment() {

    // 1. LANZADOR DE CÁMARA: Abre la cámara nativa y devuelve la foto como un Bitmap
    private val tomarFotoLauncher = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            Toast.makeText(requireContext(), "¡Foto capturada con éxito!", Toast.LENGTH_SHORT).show()
            mostrarConfiguracionProyecto()
        } else {
            Toast.makeText(requireContext(), "Se canceló la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    // 2. LANZADOR DE GALERÍA: Abre el selector de fotos moderno y devuelve la URI del archivo
    private val abrirGaleriaLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        if (uri != null) {
            Toast.makeText(requireContext(), "¡Imagen seleccionada desde la galería!", Toast.LENGTH_SHORT).show()
            mostrarConfiguracionProyecto()
        } else {
            Toast.makeText(requireContext(), "No se seleccionó ninguna imagen", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_inicio, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardCrearProyecto = view.findViewById<View>(R.id.card_crear_proyecto)

        cardCrearProyecto.setOnClickListener {
            mostrarMenuOpcionesFoto()
        }
    }

    private fun mostrarMenuOpcionesFoto() {
        val dialog = BottomSheetDialog(requireContext())
        val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet_foto, null)
        dialog.setContentView(bottomSheetView)

        val btnClose = bottomSheetView.findViewById<View>(R.id.btn_close_sheet)
        val btnTomarFoto = bottomSheetView.findViewById<View>(R.id.card_tomar_foto)
        val btnGaleria = bottomSheetView.findViewById<View>(R.id.btn_galeria)

        btnClose.setOnClickListener {
            dialog.dismiss()
        }

        // 3. Ejecutar la Cámara
        btnTomarFoto.setOnClickListener {
            dialog.dismiss()
            // Lanzamos la petición a la cámara del teléfono
            tomarFotoLauncher.launch(null)
        }

        // 4. Ejecutar la Galería
        btnGaleria.setOnClickListener {
            dialog.dismiss()
            // Lanzamos el selector indicando que solo queremos imágenes (no videos)
            abrirGaleriaLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        dialog.show()
    }

    private fun mostrarConfiguracionProyecto() {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_configurar_proyecto, null)
        dialog.setContentView(view)

        // Close button
        view.findViewById<View>(R.id.btn_close_config).setOnClickListener { dialog.dismiss() }

        // Setup Platforms
        val llPlataformas = view.findViewById<android.widget.LinearLayout>(R.id.ll_plataformas_nuevo)
        val availablePlatforms = listOf("Instagram", "TikTok", "Pinterest", "LinkedIn")
        val selectedPlatforms = mutableSetOf<String>()

        availablePlatforms.forEach { platformName ->
            val chipView = layoutInflater.inflate(R.layout.item_plataforma_chip, llPlataformas, false) as com.google.android.material.card.MaterialCardView
            val tvName = chipView.findViewById<android.widget.TextView>(R.id.tv_plataforma_nombre)
            tvName.text = platformName

            fun updateChipStyle(isSelected: Boolean) {
                if (isSelected) {
                    chipView.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_primary))
                    chipView.strokeWidth = 0
                    tvName.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.white))
                } else {
                    chipView.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.white))
                    val strokePx = (1 * resources.displayMetrics.density).toInt()
                    chipView.strokeWidth = strokePx
                    chipView.strokeColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.gray_light)
                    tvName.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_secondary))
                }
            }

            // Init state
            updateChipStyle(false)

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

        // Setup Backgrounds
        val llFondos = view.findViewById<android.widget.LinearLayout>(R.id.ll_fondos_nuevo)
        val availableBackgrounds = listOf("Minimalista", "Rústico", "Estudio")
        var selectedBackground = ""

        availableBackgrounds.forEach { bgName ->
            val chipView = layoutInflater.inflate(R.layout.item_plataforma_chip, llFondos, false) as com.google.android.material.card.MaterialCardView
            val tvName = chipView.findViewById<android.widget.TextView>(R.id.tv_plataforma_nombre)
            tvName.text = bgName

            fun updateBgStyle(isSelected: Boolean) {
                if (isSelected) {
                    chipView.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.accent_lime))
                    chipView.strokeWidth = 0
                    tvName.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_primary))
                } else {
                    chipView.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.white))
                    val strokePx = (1 * resources.displayMetrics.density).toInt()
                    chipView.strokeWidth = strokePx
                    chipView.strokeColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.gray_light)
                    tvName.setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_secondary))
                }
            }
            updateBgStyle(false)

            chipView.setOnClickListener {
                selectedBackground = bgName
                // Update all bg chips styles
                for (i in 0 until llFondos.childCount) {
                    val child = llFondos.getChildAt(i) as com.google.android.material.card.MaterialCardView
                    val childText = child.findViewById<android.widget.TextView>(R.id.tv_plataforma_nombre).text.toString()
                    if (childText == selectedBackground) {
                        child.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.accent_lime))
                        child.strokeWidth = 0
                        child.findViewById<android.widget.TextView>(R.id.tv_plataforma_nombre).setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_primary))
                    } else {
                        child.setCardBackgroundColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.white))
                        child.strokeWidth = (1 * resources.displayMetrics.density).toInt()
                        child.strokeColor = androidx.core.content.ContextCompat.getColor(requireContext(), R.color.gray_light)
                        child.findViewById<android.widget.TextView>(R.id.tv_plataforma_nombre).setTextColor(androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_secondary))
                    }
                }
            }
            llFondos.addView(chipView)
        }

        val etNombre = view.findViewById<android.widget.EditText>(R.id.et_nombre_nuevo)
        val etDesc = view.findViewById<android.widget.EditText>(R.id.et_desc_nuevo)

        val btnGenImagen = view.findViewById<View>(R.id.btn_gen_imagen)
        val btnGenVideo = view.findViewById<View>(R.id.btn_gen_video)

        val actionCrear = { isVideo: Boolean ->
            val nombre = etNombre.text.toString().ifEmpty { "Proyecto Nuevo" }
            val desc = etDesc.text.toString()
            
            dialog.dismiss()
            mostrarPreviewPost(nombre, desc, selectedPlatforms, isVideo, selectedBackground)
        }

        btnGenImagen.setOnClickListener { actionCrear(false) }
        btnGenVideo.setOnClickListener { actionCrear(true) }

        dialog.show()
    }

    private fun mostrarPreviewPost(nombre: String, desc: String, platforms: Set<String>, isVideo: Boolean, bg: String) {
        val dialog = BottomSheetDialog(requireContext())
        val view = layoutInflater.inflate(R.layout.bottom_sheet_preview_post, null)
        dialog.setContentView(view)

        view.findViewById<View>(R.id.btn_close_preview).setOnClickListener { dialog.dismiss() }

        val llPreviews = view.findViewById<android.widget.LinearLayout>(R.id.ll_previews_container)
        
        val displayPlatforms = if (platforms.isEmpty()) setOf("N/A") else platforms

        // Determinar un color basado en el fondo (mock)
        val colorHex = when (bg) {
            "Minimalista" -> "#F5F5F5"
            "Rústico" -> "#DDC2A4"
            "Estudio" -> "#BDBDBD"
            else -> "#D3E0EA"
        }

        displayPlatforms.forEach { platform ->
            val cardView = layoutInflater.inflate(R.layout.item_preview_card, llPreviews, false)
            val tvPlatform = cardView.findViewById<android.widget.TextView>(R.id.tv_preview_platform)
            val viewMedia = cardView.findViewById<View>(R.id.view_preview_media)
            val ivPlay = cardView.findViewById<View>(R.id.iv_preview_play)
            val tvDesc = cardView.findViewById<android.widget.TextView>(R.id.tv_preview_desc)

            tvPlatform.text = platform.uppercase()
            viewMedia.setBackgroundColor(android.graphics.Color.parseColor(colorHex))
            ivPlay.visibility = if (isVideo) View.VISIBLE else View.GONE
            tvDesc.text = desc.ifEmpty { "Sin descripción..." }

            llPreviews.addView(cardView)
        }

        val guardarProyecto = { estado: EstadoProyecto, msg: String ->
            val nuevoProyecto = Proyecto(
                id = ProyectoRepository.proyectos.size + 1,
                titulo = nombre,
                redesSociales = platforms.toMutableList(),
                estado = estado,
                colorHex = colorHex,
                isGrandeEnTodos = false,
                texto = desc,
                fechaEvento = ProyectoRepository.hoyStr,
                horaEvento = ""
            )
            ProyectoRepository.proyectos.add(nuevoProyecto)
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }

        view.findViewById<View>(R.id.btn_publicar_ahora_preview).setOnClickListener {
            guardarProyecto(EstadoProyecto.PUBLICADO, "¡Post publicado con éxito!")
        }

        view.findViewById<View>(R.id.btn_guardar_borrador_preview).setOnClickListener {
            guardarProyecto(EstadoProyecto.BORRADOR, "Guardado en borradores.")
        }

        dialog.show()
    }
}