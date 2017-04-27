package me.th3pf.plugins.duties;

import me.th3pf.plugins.duties.adapters.VaultAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.UUID;

public class API
{
   private Plugin registeredPlugin = null;

   public void RegisterAddon( Plugin plugin, String name, boolean hidden )
   {
      //if(!Duties.Addons.containsKey(registeredPlugin))return;

      if( Duties.Addons.containsKey( plugin ) ) return;

      if( plugin == null || name == null )
      {
         return;
      }

      try
      {
         Duties.Addons.put( plugin, name );

         registeredPlugin = plugin;
         if( !hidden )
         {
            Duties.GetInstance().LogMessage( "Registered addon: " + name );
         }
      }
      catch( Exception exception )
      {
         if( !hidden )
         {
            Duties.GetInstance().LogMessage( "Addon " + "'" + name + "'" + " failed to register addon " + "'" + name + "'" + "due to an unknown cause." );
         }
      }
   }

   public void UnregisterAddon( Plugin plugin, String name, boolean hidden )
   {
      if( !Duties.Addons.containsKey( registeredPlugin ) ) return;

      if( plugin == null || name == null )
      {
         return;
      }

      try
      {
         Duties.Addons.remove( plugin );

         registeredPlugin = null;
         if( !hidden )
         {
            Duties.GetInstance().LogMessage( "Unregistered addon: " + name );
         }
      }
      catch( Exception exception )
      {
         if( !hidden )
         {
            Duties.GetInstance().LogMessage( "Addon " + "'" + name + "'" + " failed to register addon " + "'" + name + "'" + "due to an unknown cause." );
         }
      }
   }

   public void EnableDutyModeForPlayer( Player player )
   {
      if( !Duties.Addons.containsKey( registeredPlugin ) ) return;

      ModeSwitcher.EnableDutyMode( player );
   }

   public void DisableDutyModeForPlayer( Player player )
   {
      if( !Duties.Addons.containsKey( registeredPlugin ) ) return;

      ModeSwitcher.DisableDutyMode( player );
   }

   @Deprecated
   public boolean IsPlayerInDutyMode( String player )
   {
      if( !Duties.Addons.containsKey( registeredPlugin ) ) return false;

      UUID uuid = Bukkit.getPlayer( player ).getUniqueId();

      return Duties.Memories.keySet().contains( uuid );
   }

   public boolean IsPlayerInDutyMode( UUID player )
   {
      if( !Duties.Addons.containsKey( registeredPlugin ) )
         return false;

      return Duties.Memories.keySet().contains( player );
   }

   public boolean HasPlayerCompleteDutyRights( Player player )
   {
      if( !Duties.Addons.containsKey( registeredPlugin ) ) return false;

      return player.hasPermission( "duties.staff" );
   }

   public Configuration.Main GetMainConfiguration()
   {
      return Duties.Config;
   }

   public Configuration.Messages GetMessagesConfiguration()
   {
      return Duties.Messages;
   }

   public String GetDataFolderPath()
   {
      return Duties.GetInstance().getDataFolder().getAbsolutePath();
   }

   public VaultAdapter GetVaultAdapter()
   {
      return Duties.VaultAdapter;
   }

   public void LogMessage( String message )
   {
      Duties.GetInstance().LogMessage( message );
   }

   public Duties GetInstance()
   {
      return Duties.GetInstance();
   }
}
