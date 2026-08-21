package com.example.booksy

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment

class InicioFragment : Fragment(R.layout.fragment_home) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ivSearch = view.findViewById<ImageView>(R.id.ivSearch)

        ivSearch.setOnClickListener {
            val mainActivity = activity as? MainActivity
            mainActivity?.irABuscar()
        }
    }
}