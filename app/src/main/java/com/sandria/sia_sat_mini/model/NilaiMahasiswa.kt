package com.sandria.sia_sat_mini.model

data class NilaiMahasiswa(
    val matkulId: String = "",
    val matkulNama: String = "",
    val mahasiswaId: String = "",
    val mahasiswaNama: String = "",
    val nilai: Int = 0,
    val kode: String = "",
    val dosen: String = ""
)
