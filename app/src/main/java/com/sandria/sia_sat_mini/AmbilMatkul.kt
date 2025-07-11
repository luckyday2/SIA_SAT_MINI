package com.sandria.sia_sat_mini

data class AmbilMatkul(
    val mahasiswaId: String = "",
    val mahasiswaNama: String = "",
    val matkulId: String = "",
    val matkulNama: String = "",
    val kode: String = "",
    val sks: Int = 0,
    val dosen: String = "",
    var nilai: Int? = null
)
