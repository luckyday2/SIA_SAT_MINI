package com.sandria.sia_sat_mini.model

data class Matkul(
    val id: String = "",
    val kode: String = "",
    val nama: String = "",
    val sks: Int = 0,
    val ruangan: String = "",
    val dosen: String = "",
    var nilai: Int? = null
)
