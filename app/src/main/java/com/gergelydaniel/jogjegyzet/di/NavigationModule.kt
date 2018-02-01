package com.gergelydaniel.jogjegyzet.di

import com.gergelydaniel.app.routing.Navigator
import com.gergelydaniel.app.routing.RouteConfig
import com.gergelydaniel.app.routing.Router
import dagger.Module
import dagger.Provides

@Module
class NavigationModule {
    @Provides
    fun provideRouter(config: RouteConfig) : Router = Router(config)

    @Provides
    fun provideNavigator(router: Router) : Navigator = router
}