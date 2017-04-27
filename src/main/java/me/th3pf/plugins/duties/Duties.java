package me.th3pf.plugins.duties;

import me.th3pf.plugins.duties.adapters.VaultAdapter;
import me.th3pf.plugins.duties.commandexecutors.DutiesCommandExecutor;
import me.th3pf.plugins.duties.commandexecutors.DutymodeCommandExecutor;
import me.th3pf.plugins.duties.listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class Duties extends JavaPlugin
{
   //Server readpoint
   private static Duties Instance;

   //Public source points
   public PluginManager pluginManager;
   public PluginDescriptionFile PDFile;
   public static Configuration.Main Config;
   public static Configuration.Messages Messages;
   public static HashMap<UUID, Memory> Memories = new HashMap<>();
   public static List<Player> Hidden = new ArrayList<>();
   public static HashMap<String, Long> LastChestReminderTime = new HashMap<>();
   public static HashMap<String, Long> LastDropReminderTime = new HashMap<>();
   public static HashMap<Plugin, String> Addons = new HashMap<>();
   public static VaultAdapter VaultAdapter;
   public static boolean latestEventCancelled = false;

   public Duties()
   {
      Instance = this;
   }

   @Override
   public void onEnable()
   {
      pluginManager = this.getServer().getPluginManager();
      PDFile = this.getDescription();

      Config = ( new Configuration().new Main( new File( Duties.GetInstance().getDataFolder().getAbsolutePath() + File.separator + "config.yml" ) ) );
      Messages = ( new Configuration().new Messages( new File( Duties.GetInstance().getDataFolder().getAbsolutePath() + File.separator + "messages.yml" ) ) );

      if( !Config.GetBoolean( "Enabled" ) )
      {
         pluginManager.disablePlugin( this );
      }

      //Initialize Vault
      if( pluginManager.isPluginEnabled( "Vault" ) )
         VaultAdapter = new VaultAdapter();

      getCommand( "duties" ).setExecutor( new DutiesCommandExecutor() );
      getCommand( "dutymode" ).setExecutor( new DutymodeCommandExecutor() );

      pluginManager.registerEvents( new PlayerDropItemListener(), this );
      pluginManager.registerEvents( new PlayerInteractListener(), this );
      pluginManager.registerEvents( new EntityDeathListener(), this );
      pluginManager.registerEvents( new RemindListener(), this );

      if( pluginManager.isPluginEnabled( "TagAPI" ) )
      {
         pluginManager.registerEvents( new TagAPIListener(), this );
         Duties.GetInstance().LogMessage( "TagAPI hooked." );
      }

      if( Config.GetBoolean( "KeepStateOffline" ) )
      {
         pluginManager.registerEvents( new PlayerJoinListener(), this );
      }
      else
      {
         pluginManager.registerEvents( new PlayerQuitListener(), this );
      }

      LogMessage( "by " + PDFile.getAuthors().get( 0 ) + " was successfully enabled!" );
   }

   @Override
   public void onDisable()
   {
      this.getServer().savePlayers();

      if( Config.GetBoolean( "KeepStateOffline" ) ) //TODO
      {
         for( Map.Entry<UUID, Memory> playerMemory : Memories.entrySet() )
         {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer( playerMemory.getKey() );

            if( offlinePlayer.isOnline() )
            {
               if( !ModeSwitcher.DisableDutyMode( offlinePlayer.getPlayer() ) )
                  LogMessage( "Couldn't disable duty mode for " + offlinePlayer.getName() + "(" + offlinePlayer.getUniqueId() + ")." );
            }
            else
            {
               Player player = playerMemory.getValue().Player;

               player.loadData();

               if( !ModeSwitcher.DisableDutyMode( player ) )
                  LogMessage( "Dutymode inactivation for " + player.getName() + "(" + player.getUniqueId() + ") couldn't complete. Sorry for the inconvenience." );

               player.saveData();
            }
         }
      }
      else
      {
         for( Memory playerMemory : Memories.values() )
         {
            Player player = playerMemory.Player;

            if( !ModeSwitcher.DisableDutyMode( player ) )
               LogMessage( "Dutymode inactivation for " + player.getName() + "(" + player.getUniqueId() + ") couldn't complete. Sorry for the inconvenience." );
         }
      }

      LogMessage( "by " + PDFile.getAuthors().get( 0 ) + " was successfully disabled!" );
   }

   public static Duties GetInstance()
   {
      return Instance;
   }

   public static API GetAPI()
   {
      return new API();
   }

   public void LogMessage( String Message )
   {
      System.out.println( "[" + PDFile.getName() + " " + PDFile.getVersion() + "] " + Message );
   }

}
