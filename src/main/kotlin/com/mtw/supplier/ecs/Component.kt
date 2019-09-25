package com.mtw.supplier.ecs

import java.lang.UnsupportedOperationException

abstract class Component {
    private var _parent: Entity? = null

    val parent: Entity
      get() = _parent!!

    internal fun notifyAdded(parent: Entity) {
        if (this._parent != null) {
            throw UnsupportedOperationException("You can't double-add a Component!")
        }

        this._parent = parent
    }

    internal fun notifyRemoved() {
        if (this._parent == null) {
            throw UnsupportedOperationException("You can't remove a component that's never been added!")
        }

        this._parent = null
    }
}