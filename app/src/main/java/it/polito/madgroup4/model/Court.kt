package it.polito.madgroup4.model

data class Court(

    val name: String? = null,

    val price: Double? = null,

    val openingTime: String? = null,

    val closingTime: String? = null,

    val sport: String? = null,

    val address: String? = null,

    val city: String? = null,

    val province: String? = null,

    val phone: String? = null,

    val email: String? = null,

    val maxNumber: Int? = null,

    val stuff: List<Stuff> = emptyList(),
)
