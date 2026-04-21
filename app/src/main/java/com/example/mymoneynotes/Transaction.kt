package com.example.mymoneynotes

data class Transaction(
    val id: Int,
    val title: String,
    val amount: Double,
    val type: String,
    val category: String
)
