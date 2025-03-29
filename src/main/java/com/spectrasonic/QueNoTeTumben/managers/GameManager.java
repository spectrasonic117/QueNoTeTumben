package com.spectrasonic.QueNoTeTumben.managers;

import com.spectrasonic.QueNoTeTumben.Main;
import com.spectrasonic.QueNoTeTumben.Utils.ItemBuilder;
import com.spectrasonic.QueNoTeTumben.Utils.MessageUtils;
import com.spectrasonic.QueNoTeTumben.Utils.SoundUtils;
import com.spectrasonic.QueNoTeTumben.Utils.PointsManager;
import com.spectrasonic.QueNoTeTumben.models.GameState;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GameManager {

    private final Main plugin;
    private final PointsManager pointsManager;

    public GameManager(Main plugin) {
        this.plugin = plugin;
        this.pointsManager = new PointsManager(plugin);
        loadConfig();
    }

    @Getter
    private GameState gameState = GameState.STOPPED;

    @Getter
    @Setter
    private Location teleportLocation;

    @Getter
    @Setter
    private int spectatorHeight;

    private final Set<UUID> spectators = new HashSet<>();

    public void loadConfig() {
        plugin.reloadConfig();

        // Cargar ubicación de teleporte
        int x = plugin.getConfig().getInt("teleport_block.x");
        int y = plugin.getConfig().getInt("teleport_block.y");
        int z = plugin.getConfig().getInt("teleport_block.z");
        teleportLocation = new Location(Bukkit.getWorlds().get(0), x, y, z);

        // Cargar altura de espectador
        spectatorHeight = plugin.getConfig().getInt("spectator_height", 50);
    }

    public void startGame() {
        if (gameState == GameState.RUNNING) {
            return;
        }

        gameState = GameState.RUNNING;
        spectators.clear();

        // Dar pico a todos los jugadores y ponerlos en modo aventura
        ItemStack pickaxe = createGamePickaxe();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.getInventory().addItem(pickaxe);
            // player.teleport(teleportLocation);
            player.setGameMode(GameMode.SURVIVAL);
        }

        MessageUtils.broadcastTitle("<gold><bold>No te Caigas", "", 1, 2, 1);
        // MessageUtils.broadcastActionBar("<green>¡El juego ha comenzado! ¡No te
        // caigas!");
        SoundUtils.broadcastPlayerSound(Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    public void stopGame() {
        if (gameState == GameState.STOPPED) {
            return;
        }

        gameState = GameState.STOPPED;

        // Ejecutar comandos para reiniciar el mapa primero
        resetMap();

        // Programar el resto de acciones para después de resetear el mapa (3 segundos =
        // 60 ticks)
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            // Quitar picos y restaurar jugadores
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.getInventory().remove(Material.DIAMOND_PICKAXE);
                player.getInventory().remove(Material.BLACK_CONCRETE);
                player.getInventory().remove(Material.WHITE_CONCRETE);
                player.getInventory().remove(Material.TERRACOTTA);
                player.teleport(teleportLocation);
                player.setGameMode(GameMode.ADVENTURE);

                // Restaurar jugadores que eran espectadores
                if (spectators.contains(player.getUniqueId())) {
                    player.setGameMode(GameMode.ADVENTURE);
                }
            }

            spectators.clear();

            MessageUtils.broadcastTitle("<red><bold>GG", "", 1, 2, 1);
            MessageUtils.broadcastActionBar("<red>El juego ha terminado");
            SoundUtils.broadcastPlayerSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 0.5f, 1.0f);
        }, 60L);
    }

    private void resetMap() {
        // Ejecutar comandos para reiniciar el mapa inmediatamente
        MessageUtils.sendConsoleMessage("<yellow>Reiniciando el mapa del juego...");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/world world");
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/schem loadall tumbado_suelo");

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/paste -o");
            MessageUtils.sendConsoleMessage("<green>¡Mapa reiniciado correctamente!");
        }, 10L); // 0.5 segundos después
    }

    private Player findOperator() {
        // Buscar un jugador con permisos de operador
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.isOp() || player.hasPermission("worldedit.schematic.load")) {
                return player;
            }
        }
        return null;
    }

    public void setPlayerAsSpectator(Player player) {
        if (gameState == GameState.STOPPED || spectators.contains(player.getUniqueId())) {
            return;
        }

        // Si el jugador tiene el permiso de bypass, no lo convierte en espectador
        if (player.hasPermission("game.bypass")) {
            // MessageUtils.sendMessage(player, "<green>¡Te has salvado gracias a tus
            // permisos especiales!");
            return;
        }

        // Restar 1 punto al jugador
        pointsManager.subtractPoints(player, 1);

        player.setGameMode(GameMode.SPECTATOR);
        spectators.add(player.getUniqueId());

        // Mostrar título al jugador
        MessageUtils.sendTitle(player,
                "<dark_red><bold>Te has caido",
                "<gray>Ahora eres Espectador\n<red>¡Has perdido 1 punto!",
                1, 3, 1);

        // Notificar a todos los jugadores
        MessageUtils.sendBroadcastMessage("<yellow>" + player.getName() + " <red>se ha caido y ha perdido 1 punto");
        SoundUtils.broadcastPlayerSound(Sound.ENTITY_PLAYER_DEATH, 1.0f, 1.0f);
    }

    private ItemStack createGamePickaxe() {
        return ItemBuilder.setMaterial("DIAMOND_PICKAXE")
                .setName("<aqua><bold>PicoSpleef</bold></aqua>")
                .setLore("<gray>Rompe los bloques debajo de tus enemigos",
                        "<yellow>¡Pero no te caigas tú!")
                .addEnchantment("efficiency", 100)
                .setFlag("HIDE_ENCHANTS")
                .setUnbreakable(false)
                .build();
    }

    public boolean isSpectator(UUID playerId) {
        return spectators.contains(playerId);
    }
}
