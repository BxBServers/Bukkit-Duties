package me.th3pf.plugins.duties.actions;

import me.th3pf.plugins.duties.Duties;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class CommandsAction implements Action
{
   private final boolean console;

   public CommandsAction( boolean console )
   {
      this.console = console;
   }

   private void commands( Player player, List<String> commands )
   {
      if( commands == null )
         return;

      for( String command : commands )
      {
         String parsedCommand = ( "/".equals( command.charAt( 0 ) ) ? command.substring( 1 ) : command )
                 .replaceAll( "%PLAYER_NAME%", player.getName() )
                 .replaceAll( "%PLAYER_GAMEMODE%", player.getGameMode().toString() );

         Bukkit.dispatchCommand( console ? Bukkit.getConsoleSender() : player, parsedCommand );
      }
   }

   @Override
   public void onEnable( Player player )
   {
      commands( player, Duties.Config.GetStringList( "Actions.onEnable.Commands" + ( console ? "ByConsole" : "" ) ) );
   }

   @Override
   public void onDisable( Player player )
   {
      commands( player, Duties.Config.GetStringList( "Actions.onDisable.Commands" + ( console ? "ByConsole" : "" ) ) );
   }
}
