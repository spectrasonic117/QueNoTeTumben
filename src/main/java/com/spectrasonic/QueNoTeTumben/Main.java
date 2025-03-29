package com.spectrasonic.QueNoTeTumben;

import co.aikar.commands.PaperCommandManager;
import com.spectrasonic.QueNoTeTumben.Utils.MessageUtils;
import com.spectrasonic.QueNoTeTumben.commands.GameCommand;
import com.spectrasonic.QueNoTeTumben.listeners.GameListener;
import com.spectrasonic.QueNoTeTumben.managers.GameManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class Main extends JavaPlugin {

    private GameManager gameManager;
    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        // Guardar configuración por defecto
        saveDefaultConfig();

        // Inicializar el gestor del juego
        gameManager = new GameManager(this);

        // Inicializar el gestor de comandos
        commandManager = new PaperCommandManager(this);

        // Registrar comandos y eventos
        registerCommands();
        registerEvents();

        // Mostrar mensaje de inicio
        MessageUtils.sendStartupMessage(this);
    }

    @Override
    public void onDisable() {
        // Detener el juego si está en ejecución
        if (gameManager != null) {
            gameManager.stopGame();
        }

        MessageUtils.sendShutdownMessage(this);
    }

    public void registerCommands() {
        commandManager.registerCommand(new GameCommand(this, gameManager));
    }

    public void registerEvents() {
        getServer().getPluginManager().registerEvents(new GameListener(gameManager), this);
    }
}
