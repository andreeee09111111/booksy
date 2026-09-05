package com.example.booksy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.booksy.ui.BooksyNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExplorarFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                MaterialTheme(
                    colorScheme = lightColorScheme(
                        primary = Color(0xFF135A58),
                        background = Color(0xFFF5EFEB),
                        surface = Color.White
                    )
                ) {
                    BooksyNavHost()
                }
            }
        }
    }
}