package com.mtw.supplier.ecs

import java.util.UUID
import kotlin.reflect.KClass

class Entity(val uuid: UUID) {
    private val components: MutableList<Component> = mutableListOf()

    fun addComponent(component: Component) {
        this.components.add(component)
        component.notifyAdded(this)
    }

    fun removeComponent(component: Component) {
        this.components.remove(component)
        component.notifyRemoved()
    }

    fun hasComponent(componentClass: KClass<*>): Boolean {
        return components.any { componentClass.isInstance(it) }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T : Component> getComponent(clazz: KClass<T>): T {
        return components.first { clazz.isInstance(it) } as T
    }
}