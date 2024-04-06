package domain.model.repository

import domain.model.user.User
import java.io.PrintWriter

interface UserRepository {
    fun registration(user: User): User
}