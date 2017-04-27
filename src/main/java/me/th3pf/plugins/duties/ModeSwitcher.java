package me.th3pf.plugins.duties;

import me.th3pf.plugins.duties.actions.*;
import me.th3pf.plugins.duties.events.DutyModeDisabledEvent;
import me.th3pf.plugins.duties.events.DutyModeEnabledEvent;
import me.th3pf.plugins.duties.events.DutyModePreDisabledEvent;
import me.th3pf.plugins.duties.events.DutyModePreEnabledEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.kitteh.tag.TagAPI;

import java.util.HashMap;
import java.util.Map;

//import me.th3pf.plugins.duties.temporaryfixes.PotionEffectRemoval;

public class ModeSwitcher
{
   private static final Map<String, EnableAction> enableActions = new HashMap<>();
   private static final Map<String, DisableAction> disableActions = new HashMap<>();

   private static void addAction( String id, Object action )
   {
      if( action instanceof EnableAction )
         enableActions.put( id, (EnableAction) action );

      if( action instanceof DisableAction )
         disableActions.put( id, (DisableAction) action );
   }

   static
   {
      addAction( "Broadcast", new BroadcastAction() );
      addAction( "Cleanups", new CleanupsAction() );
      addAction( "Commands", new CommandsAction( false ) );
      addAction( "CommandsByConsole", new CommandsAction( true ) );
      addAction( "DataRemoval", new DataRemovalAction() );
      addAction( "Messages", new MessagesAction() );
      addAction( "TemporaryGroups", new TemporaryGroupsAction() );
      addAction( "TemporaryPermissions", new TemporaryPermissionsAction() );

      MemoryImportExportAction memoryAction = new MemoryImportExportAction();

      enableActions.put( "MemoryImport", memoryAction );
      disableActions.put( "MemoryExport", memoryAction );
   }

   public static boolean EnableDutyMode( Player player )
   {
      try
      {
         DutyModePreEnabledEvent event = new DutyModePreEnabledEvent( player, Duties.Config.GetStringList( "Actions.onEnable.Order" ) );
         Bukkit.getServer().getPluginManager().callEvent( event );
         if( event.getCancelled() ) return false;

         for( String plugin : Duties.Config.GetStringList( "Actions.Requirements.Dependencies" ) )
         {
            try
            {
               if( Duties.GetInstance().pluginManager.getPlugin( plugin ).isEnabled() ) continue;
               Duties.GetInstance().pluginManager.enablePlugin( Duties.GetInstance().pluginManager.getPlugin( plugin ) );
            }
            catch( Exception exception )
            {
            }
         }

         boolean fail = false;

         for( String module : event.getActions() )
         {
            if( !enableActions.containsKey( module ) )
            {
               Duties.GetInstance().LogMessage( "Invalid action: " + module );

               continue;
            }

            try
            {
               enableActions.get( module ).onEnable( player );
            }
            catch( ActionException ex )
            {
               fail = true;

               ex.printStackTrace();
            }
         }

         if( Duties.GetInstance().pluginManager.isPluginEnabled( "TagAPI" ) && player.isOnline() )
            TagAPI.refreshPlayer( player );

         //Returns that duty mode activation failed
         if( fail )
         {
            return false;
         }
         else
         {
            Duties.GetInstance().LogMessage( Duties.Messages.GetString( "Server.Enabled" ).replaceAll( "%PLAYER_NAME%", player.getName() ) );

            Bukkit.getServer().getPluginManager().callEvent( new DutyModeEnabledEvent( player ) );
            return true;
         }
      }
      catch( Exception exception )
      {
         Duties.GetInstance().LogMessage( Duties.Messages.GetString( "Server.Fail.Enable" ).replaceAll( "%PLAYER_NAME%", player.getName().replaceAll( "%REASON%", exception.getMessage() ) ) );
         return false;
      }
   }

   public static boolean DisableDutyMode( Player player )
   {
      try
      {
         DutyModePreDisabledEvent event = new DutyModePreDisabledEvent( player, Duties.Config.GetStringList( "Actions.onDisable.Order" ) );
         Bukkit.getServer().getPluginManager().callEvent( event );
         if( event.getCancelled() ) return false;

         for( String plugin : Duties.Config.GetStringList( "Actions.Requirements.Dependencies" ) )
         {
            try
            {
               if( Duties.GetInstance().pluginManager.getPlugin( plugin ).isEnabled() ) continue;
               Duties.GetInstance().pluginManager.enablePlugin( Duties.GetInstance().pluginManager.getPlugin( plugin ) );
            }
            catch( Exception exception )
            {
            }
         }

         boolean fail = false;

         for( String module : event.getActions() )
         {
            if( !disableActions.containsKey( module ) )
            {
               Duties.GetInstance().LogMessage( "Invalid action: " + module );

               continue;
            }

            try
            {
               disableActions.get( module ).onDisable( player );
            }
            catch( ActionException ex )
            {
               fail = true;

               ex.printStackTrace();
            }
         }

         if( Duties.GetInstance().pluginManager.isPluginEnabled( "TagAPI" ) && player.isOnline() )
            TagAPI.refreshPlayer( player );

         //Returns that duty mode inactivation failed
         if( fail )
         {
            return false;
         }
         else
         {
            Duties.GetInstance().LogMessage( Duties.Messages.GetString( "Server.Disabled" ).replaceAll( "%PLAYER_NAME%", player.getName() ) );

            Bukkit.getServer().getPluginManager().callEvent( new DutyModeDisabledEvent( player ) );
            return true;
         }
      }
      catch( Exception exception )
      {
         Duties.GetInstance().LogMessage( Duties.Messages.GetString( "Server.Fail.Disable" ).replaceAll( "%PLAYER_NAME%", player.getName().replaceAll( "%REASON%", exception.getMessage() ) ) );
         return false;
      }
   }
}
