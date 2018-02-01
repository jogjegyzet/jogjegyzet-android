package com.gergelydaniel.app.routing

interface Navigator {
    fun home()

    fun navigateTo(key: Any)

    fun navigateTo(key: Any, options: NavigationOptions)
}