package it.polito.madgroup4.model


data class User(
    val id: Long = 0,
    val name: String? = null,
    val surname: String? = null,
    val nickname: String? = null,
    val email: String? = null,
    val photo: String? = null,
    val sports: List<Sport> = listOf(),
)

data class Sport(
    val name: String? = null,
    val level: String? = null,
    val achievements: List<Achievement> = arrayListOf()
)

data class Achievement(
    val description: String? = null,
    val title: String? = null
)