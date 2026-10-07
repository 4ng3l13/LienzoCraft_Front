package com.example.lienzocraft

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1. Enlazamos la barra de navegación de tu XML
        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // 2. Al abrir la app, cargamos la Pantalla de Inicio automáticamente
        if (savedInstanceState == null) {
            replaceFragment(InicioFragment())
        }

        // 3. Configuramos qué pasa cuando tocas los botones del menú inferior
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    replaceFragment(InicioFragment())
                    true
                }
                R.id.nav_estudio -> {
                    replaceFragment(EstudioFragment())
                    true
                }
                R.id.nav_agenda -> {
                    replaceFragment(AgendaFragment())
                    true
                }
                R.id.nav_perfil -> {
                    replaceFragment(PerfilFragment())
                    true
                }
                else -> false
            }
        }
    }

    // Esta es la función que hace la "magia" de cambiar las pantallas
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}