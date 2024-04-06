package domain.model.repository

import java.io.File

interface BaseRepository {
    fun init(): File
}