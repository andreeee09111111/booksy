package com.example.booksy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.booksy.databinding.ItemLibrosBinding

class LibrosAdapter(
    private var books: List<Book> = emptyList()
) : RecyclerView.Adapter<LibrosAdapter.BookViewHolder>() {

    class BookViewHolder(val binding: ItemLibrosBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val binding = ItemLibrosBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.binding.tvBookAuthor.text = book.autor
        holder.binding.tvBookFavorite.text = "Favorito: ${book.esFavorito}"
    }

    override fun getItemCount(): Int = books.size

    fun updateBooks(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }
}