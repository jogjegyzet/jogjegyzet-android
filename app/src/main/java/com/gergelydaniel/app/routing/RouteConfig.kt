package com.gergelydaniel.app.routing

import android.support.v4.app.Fragment
import com.gergelydaniel.jogjegyzet.ui.home.HomeFragment
import kotlin.reflect.KClass

class RouteConfig(val home: KClass<HomeFragment>, val map: Map<Any, Fragment>)
