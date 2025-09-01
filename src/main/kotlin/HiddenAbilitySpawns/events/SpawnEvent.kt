package HiddenAbilitySpawns.events

import HiddenAbilitySpawns.HiddenAbilitySpawns
import HiddenAbilitySpawns.util.HiddenAbilitySpawnsLogger
import HiddenAbilitySpawns.util.Utils
import com.cobblemon.mod.common.api.Priority
import com.cobblemon.mod.common.api.events.CobblemonEvents
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity
import kotlin.random.Random

class SpawnEvent {

    private val utils = Utils()

    init {
        // High Priority
        CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(priority = Priority.HIGH) { event ->
            val pokemonEntity = event.entity as? PokemonEntity ?: return@subscribe
            val pokemon = pokemonEntity.pokemon

            val config = HiddenAbilitySpawns.getMainConfig()
                ?: return@subscribe

            val hiddenAbilityChance = config.getDouble("HiddenAbilityChance", 0.005)

            // Roll to see if we should apply a Hidden Ability
            if (Random.nextDouble() < hiddenAbilityChance) {
                utils.setHiddenAbility(pokemon)

                // Check if the Pokémon actually ended up with a Hidden Ability
                if (utils.pokemonHasHiddenAbility(pokemon)) {
                    HiddenAbilitySpawnsLogger.info(
                        "${pokemon.species.name} spawned with its hidden ability: ${pokemon.ability.name}"
                    )
                }
            }
        }
    }
}
