package com.example.store.domain.model

// Enumeración para los roles de usuario
enum class UserRole(val roleName: String) {
    ADMIN("admin"),
    USER("user");

    companion object {
        // Convertir el String de Firebase a un UserRole
        fun fromString(role: String): UserRole {
            return UserRole.entries.firstOrNull { it.roleName == role } ?: USER
        }
    }

}
