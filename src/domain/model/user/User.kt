package domain.model.user

data class User(
    val email: String,
    val password: String
)

data class SimpleUser(
    val email: String
)
