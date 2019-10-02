package com.mtw.supplier.ecs.components

import com.mtw.supplier.ecs.Component

class FighterComponent(
    var hitDamage: Int,
    var toHit: Int,
    var toDodge: Int
): Component()