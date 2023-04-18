package it.polito.madgroup4.Model

import javax.inject.Inject

abstract class PlayingCourtRepository @Inject constructor(private val playingCourtDao: PlayingCourtDAO) {
    fun getAll() = playingCourtDao.getAll()
    fun getById(id: String) = playingCourtDao.getById(id)
    fun getAllBySport(sport: String) = playingCourtDao.getAllBySport(sport)
    fun save(playingCourt: PlayingCourt) = playingCourtDao.save(playingCourt)
    fun delete(playingCourt: PlayingCourt) = playingCourtDao.delete(playingCourt)
}