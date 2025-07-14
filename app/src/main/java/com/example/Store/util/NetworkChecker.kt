package com.example.Store.util

/**
 * Interfaz para verificar la conectividad de red.
 */
interface NetworkChecker {

    /**
     * Verifica si el dispositivo tiene acceso a Internet.
     *
     * @return `true` si está conectado a una red con acceso a Internet, `false` en caso contrario.
     */
    fun isConnected(): Boolean
}
