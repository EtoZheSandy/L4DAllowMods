package su.afk.l4d2.domain.model

import kotlinx.serialization.Serializable

/**
 * Addon metadata used by UI, cache, and gameinfo update flows.
 */
@Serializable
data class AddonInfo(
    val title: String,
    val description: String?,
    val filename: String,
    val imagePath: String?
)
