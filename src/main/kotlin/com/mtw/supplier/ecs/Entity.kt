package com.mtw.supplier.ecs

import kotlin.reflect.KClass

class Entity(
    val id: Int,
    val name: String
) {
    private val components: MutableList<Component> = mutableListOf()

    fun addComponent(component: Component): Entity {
        this.components.add(component)
        component.notifyAdded(this)
        return this
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