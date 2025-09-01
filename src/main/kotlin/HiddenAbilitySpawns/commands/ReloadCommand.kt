package HiddenAbilitySpawns.commands

import HiddenAbilitySpawns.HiddenAbilitySpawns
import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.context.CommandContext
import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.ServerCommandSource

object ReloadCommand {
    const val RELOAD_PERMISSION_NODE: String = "hiddenabilityspawns.reload"

    /**
     * Registers the Hidden Ability Spawns commands.
     *
     * @param dispatcher The command dispatcher to register commands on.
     */
    fun register(dispatcher: CommandDispatcher<ServerCommandSource>) {
        dispatcher.register(
            CommandManager.literal("hiddenabilityspawns")
                .then(CommandManager.literal("reload")
                    .requires(Permissions.require(RELOAD_PERMISSION_NODE, 2))
                    .executes { ctx -> reloadConfigs(ctx) }
                )
        )
    }

    /**
     * Reloads the Hidden Ability Spawns configurations.
     *
     * @param ctx The command context.
     * @return 1 if successful, 0 otherwise.
     */
    private fun reloadConfigs(ctx: CommandContext<ServerCommandSource>): Int {
        try {
            HiddenAbilitySpawns.reloadConfigurations()
            ctx.source.sendFeedback(
                { net.minecraft.text.Text.literal("Hidden Ability Spawns configuration reloaded successfully.") },
                false
            )
            return Command.SINGLE_SUCCESS
        } catch (e: Exception) {
            ctx.source.sendError(net.minecraft.text.Text.literal("Failed to reload configurations: ${e.message}"))
            return 0
        }
    }
}