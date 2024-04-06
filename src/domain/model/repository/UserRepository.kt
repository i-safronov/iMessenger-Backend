package domain.model.repository

import domain.model.user.User
import java.io.PrintWriter

interface UserRepository: BaseRepository {
    fun registration(push: PrintWriter, user: User)
}