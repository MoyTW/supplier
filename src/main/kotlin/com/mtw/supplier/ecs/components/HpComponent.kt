package com.mtw.supplier.ecs.components

import com.mtw.supplier.ecs.Component

class HpComponent(
    var maxHp: Int,
    var currentHp: Int
): Component() {
    fun removeHp(hp: Int) {
        this.currentHp -= hp
    }

    fun healHp(hp: Int) {
        this.currentHp += hp
        if (this.currentHp > maxHp) {
            this.currentHp = this.maxHp
        }
    }
}