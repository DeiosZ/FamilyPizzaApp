package com.example.familypizza.domain.model

enum class Prize(val label: String, val description: String, val discount: Double) {
    GASEOSA(    "🥤 Gaseosa 2L",       "¡Gaseosa de 2 litros gratis!",       0.0),
    PAN_AJO(    "🧄 Pan al ajo",        "¡Pan al ajo gratis en tu pedido!",   0.0),
    DESC_20(    "20% OFF",              "20% de descuento en tu compra",      0.20),
    DESC_10(    "10% OFF",              "10% de descuento en tu compra",      0.10),
    POSTRE(     "🍮 Postre gratis",     "¡Un postre gratis te espera!",       0.0),
    SIGUE(      "Siga participando",    "¡Suerte para la próxima!",           0.0);
}