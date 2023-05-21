package it.polito.madgroup4.model

import com.google.firebase.Timestamp

data class User(
    val id: Long = 0,
    val name: String? = null,
    val surname: String? = null,
    val nickname: String? = null,
    val email: String? = null,
    val photo: String? = null,
    var sports: List<Sport> = listOf(),
)

data class Sport(
    val name: String? = null,
    var level: String? = null,
    var achievements: List<Achievement> = arrayListOf()
)

data class Achievement(
    val description: String? = null,
    val date: Timestamp? = null,
    val title: String? = null
)