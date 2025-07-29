package com.skinny.skinnyapp.api.response

data class ProductRecommendResponse(
    val status: Int,
    val message: String?,
    val result: List<Product>?
)

data class Product(
    val id: String,
    val name: String,
    val price: String,
    val description: String,
    val wrinkle: Int,
    val pore: Int,
    val elasticity: Int,
    val moisture: Int,
    val image_url: String,
    val commerce_url: String,
    val company: Company
)

data class Company(
    val name: String,
    val url: String
)
