package com.spectrasonic.QueNoTeTumben.listeners;

import com.spectrasonic.QueNoTeTumben.Main;
import com.spectrasonic.QueNoTeTumben.managers.GameManager;
import com.spectrasonic.QueNoTeTumben.models.GameState;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

@RequiredArgsConstructor
public class GameListener implements Listener {

    private final GameManager gameManager;

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        // Solo verificar si el juego está en ejecución
        if (gameManager.getGameState() == GameState.STOPPED) {
            return;
        }

        Player player = event.getPlayer();

        // Verificar si el jugador ya es espectador
        if (gameManager.isSpectator(player.getUniqueId())) {
            return;
        }

        // Verificar si el jugador está por debajo de la altura límite
        if (player.getLocation().getY() <= gameManager.getSpectatorHeight()) {
            gameManager.setPlayerAsSpectator(player);
        }
    }
}
