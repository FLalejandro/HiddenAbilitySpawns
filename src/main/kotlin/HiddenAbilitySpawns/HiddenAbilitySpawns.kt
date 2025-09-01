package HiddenAbilitySpawns

import HiddenAbilitySpawns.commands.ReloadCommand
import HiddenAbilitySpawns.config.Configuration
import HiddenAbilitySpawns.config.YamlConfiguration
import HiddenAbilitySpawns.events.SpawnEvent
import HiddenAbilitySpawns.util.HiddenAbilitySpawnsLogger
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.server.MinecraftServer
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class HiddenAbilitySpawns : ModInitializer {
	private var server: MinecraftServer? = null

	override fun onInitialize() {
		// Display ASCII art - no ascii this time :(
		displayAsciiArt()

		// Initialize configuration
		this.configManager()

		// Register all the commands available in the mod.
		registerCommands()

		// Defer event registration until the server is fully started
		registerServerLifecycleListeners()
	}

	/**
	 * Register all commands provided by the mod.
	 */
	private fun registerCommands() {
		CommandRegistrationCallback.EVENT.register { dispatcher, registryAccess, environment ->
			ReloadCommand.register(dispatcher)
		}
	}

	private fun registerServerLifecycleListeners() {
		// Capture server instance and register events
		ServerLifecycleEvents.SERVER_STARTED.register { minecraftServer ->
			server = minecraftServer

			// Register events that require the server instance
			registerEventListeners()
			HiddenAbilitySpawnsLogger.info("Server instance captured and event listeners registered!")
		}
	}

	fun configManager() {
		mainConfig = getConfig("config.yml")
		if (mainConfig == null) {
			HiddenAbilitySpawnsLogger.error("Failed to load main configuration. Default values will be used.")
		} else {
			HiddenAbilitySpawnsLogger.info("Configuration loaded successfully.")
		}
	}

	@Throws(IOException::class)
	fun getOrCreateConfigurationFile(fileName: String): File {
		val configFolder = configFolder
		val configFile = File(configFolder, fileName)

		if (!configFile.exists()) {
			configFolder.mkdirs() // Ensure the folder exists
			val resourcePath = "/HiddenAbilitySpawns/$fileName" // Path inside resources folder
			val resourceStream = javaClass.getResourceAsStream(resourcePath)
				?: throw IOException("Default configuration file not found in resources: $resourcePath")

			// Copy the resource file to the configuration folder
			resourceStream.use { input ->
				FileOutputStream(configFile).use { output ->
					input.copyTo(output)
				}
			}
		}
		return configFile
	}

	fun getConfig(fileName: String): Configuration? {
		var config: Configuration? = null
		try {
			config = YamlConfiguration.loadConfiguration(getOrCreateConfigurationFile(fileName))
		} catch (e: IOException) {
			e.printStackTrace()
		}
		return config
	}

	/**
	 * Displays an ASCII Art representation of the mod's name in the log. -- didn't get to it :(
	 */
	private fun displayAsciiArt() {
		HiddenAbilitySpawnsLogger.info("HiddenAbilitySpawns Loaded Successfully")
	}

	private fun registerEventListeners() {
		if (mainConfig != null && server != null) {
			SpawnEvent()
		} else {
			HiddenAbilitySpawnsLogger.error("Failed to register events: mainConfig or server is null.")
		}
		HiddenAbilitySpawnsLogger.info("Event listeners registered!")
	}

	companion object {
		private var mainConfig: Configuration? = null

		fun getMainConfig(): Configuration? {
			return mainConfig
		}

		val configFolder: File
			get() {
				val configFolder = FabricLoader.getInstance().configDir.resolve("HiddenAbilitySpawns").toFile()
				if (!configFolder.exists()) configFolder.mkdirs()
				return configFolder
			}

		/**
		 * Reloads configurations from the config file.
		 */
		fun reloadConfigurations() {
			try {
				mainConfig = YamlConfiguration.loadConfiguration(
					HiddenAbilitySpawns().getOrCreateConfigurationFile("config.yml")
				)
				HiddenAbilitySpawnsLogger.info("HiddenAbilitySpawns configuration reloaded successfully.")
			} catch (e: Exception) {
				HiddenAbilitySpawnsLogger.error("Failed to reload configuration: ${e.message}", e)
				throw e
			}
		}
	}
}