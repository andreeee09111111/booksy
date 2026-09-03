package com.example.booksy

import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.booksy.databinding.ItemLibroBinding

class LibrosAdapter(
    private val onFavoriteClick: (Book) -> Unit = {}
) : ListAdapter<Book, LibrosAdapter.BookViewHolder>(DiffCallback) {

    fun updateBooks(books: List<Book>?) {
        submitList(books)
    }

    class BookViewHolder(val binding: ItemLibroBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemLibroBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = getItem(position)

        cargarPortada(holder, book)

        val context = holder.binding.root.context
        holder.binding.tvCategoriaLibro.text = book.categoria.uppercase()
        holder.binding.tvTituloLibro.text = book.titulo
        holder.binding.tvAutorLibro.text = book.autor
        holder.binding.tvCalificacionLibro.text = book.calificacion.toString()
        holder.binding.ratingBar.rating = book.calificacion

        val favColor = if (book.esFavorito)
            ContextCompat.getColor(context, R.color.color_favorito)
        else
            ContextCompat.getColor(context, R.color.color_gris_icono)

        holder.binding.ivFavoritoLibro.setColorFilter(favColor)

        holder.binding.ivFavoritoLibro.setOnClickListener {
            onFavoriteClick(book)
        }
    }

    private fun cargarPortada(holder: BookViewHolder, book: Book) {
        val context = holder.binding.root.context
        if (book.portada.isNullOrEmpty()) {
            holder.binding.ivPortadaLibro.setImageResource(R.drawable.portada_destacado)
            return
        }

        try {
            val uri = Uri.parse(book.portada)
            val inputStream = context.contentResolver.openInputStream(uri)
            val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream?.close()

            options.inSampleSize = calcularInSampleSize(options, 400, 400)
            options.inJustDecodeBounds = false

            val inputStream2 = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream2, null, options)
            inputStream2?.close()

            if (bitmap != null) {
                holder.binding.ivPortadaLibro.setImageBitmap(bitmap)
            } else {
                holder.binding.ivPortadaLibro.setImageResource(R.drawable.portada_destacado)
            }
        } catch (e: Exception) {
            Log.e("LibrosAdapter", "No se pudo cargar la portada: ${book.portada}", e)
            holder.binding.ivPortadaLibro.setImageResource(R.drawable.portada_destacado)
        }
    }

    // Cálculo limpio
    private fun calcularInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<Book>() {
            override fun areItemsTheSame(oldItem: Book, newItem: Book) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Book, newItem: Book) = oldItem == newItem
        }
    }
}