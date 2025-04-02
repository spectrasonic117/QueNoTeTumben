package com.spectrasonic.QueNoTeTumben.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.spectrasonic.QueNoTeTumben.Main;
import com.spectrasonic.QueNoTeTumben.Utils.MessageUtils;
import com.spectrasonic.QueNoTeTumben.managers.GameManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("quenotetumben|qnt")
@RequiredArgsConstructor
public class GameCommand extends BaseCommand {

    private final Main plugin;
    private final GameManager gameManager;

    @Subcommand("game")
    @CommandPermission("quenotetumben.admin")
    @Description("Controla el estado del juego")
    public class GameCommands extends BaseCommand {

        @Subcommand("start")
        @Description("Inicia el minijuego")
        public void onStart(CommandSender sender) {
            Player player = (Player) sender;
            player.performCommand("id false");
            gameManager.startGame();
            MessageUtils.sendMessage(sender, "<green>¡El juego ha sido iniciado correctamente!");
        }

        @Subcommand("stop")
        @Description("Detiene el minijuego")
        public void onStop(CommandSender sender) {
            Player player = (Player) sender;
            player.performCommand("id true");
            gameManager.stopGame();
            MessageUtils.sendMessage(sender, "<red>El juego ha sido detenido.");
        }
    }

    @Subcommand("reload")
    @CommandPermission("quenotetumben.admin")
    @Description("Recarga la configuración del plugin")
    public void onReload(CommandSender sender) {
        gameManager.loadConfig();
        MessageUtils.sendMessage(sender, "<green>Configuración recargada correctamente.");
    }

    @Default
    @HelpCommand
    public void onHelp(CommandSender sender) {
        MessageUtils.sendMessage(sender, "<yellow>Comandos disponibles:");
        MessageUtils.sendMessage(sender, "<gray>/quenotetumben game start <green>- Inicia el minijuego");
        MessageUtils.sendMessage(sender, "<gray>/quenotetumben game stop <green>- Detiene el minijuego");
        MessageUtils.sendMessage(sender, "<gray>/quenotetumben reload <green>- Recarga la configuración");
    }
}
