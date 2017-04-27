package me.th3pf.plugins.duties.actions;

import me.th3pf.plugins.duties.Duties;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.ArrayList;

public class TemporaryPermissionsAction implements Action
{
   @Override
   public void onEnable( Player player ) throws ActionException
   {
      //Adds temporary permissions
      if( Duties.Config.GetStringList( "Actions.TemporaryPermissions" ) == null ) return;

      try
      {
         Duties.Memories.get( player.getUniqueId() ).TemporaryPermissions = new ArrayList<>();

         for( String node : Duties.Config.GetStringList( "Actions.TemporaryPermissions" ) )
         {
            try
            {
               if( Duties.Config.GetBoolean( "Vault.Permissions" ) && Duties.GetInstance().getServer().getPluginManager().isPluginEnabled( "Vault" ) )
               {
                  if( node.contains( ": " ) )
                  {
                     if( node.split( ": " )[1].equalsIgnoreCase( "true" ) )
                     {
                        Duties.VaultAdapter.permission.playerAddTransient( player, node.split( ": " )[0].replaceAll( "%PLAYER_NAME%", player.getName() ) );
                     }
                     else if( node.split( ": " )[1].equalsIgnoreCase( "false" ) )
                     {
                        Duties.VaultAdapter.permission.playerRemoveTransient( player, node.split( ": " )[0].replaceAll( "%PLAYER_NAME%", player.getName() ) );
                     }
                     else
                     {
                        Duties.GetInstance().LogMessage( "Failed while enabling temporary permissions: '" + ( node.split( ": " )[1] ) + "' is not a valid value for node: " + node.split( ( ": " ) )[0] + ". Ignoring it!" );
                        continue;
                     }
                  }
                  else
                  {
                     Duties.VaultAdapter.permission.playerAddTransient( player, node.replaceAll( "%PLAYER_NAME%", player.getName() ) );
                  }
               }
               else
               {
                  if( !player.isOnline() )
                  {
                     return;
                  }
                  else
                  {
                     Duties.Memories.get( player.getUniqueId() ).TemporaryPermissions.add( player.addAttachment( Duties.GetInstance(), node.split( ": " )[0].replaceAll( "%PLAYER_NAME%", player.getName() ), Boolean.parseBoolean( node.split( ": " )[1] ) ) );
                  }
               }
            }
            catch( Exception exception )
            {
               Duties.GetInstance().LogMessage( "Failed while enabling temporary permissions: Not a valid permission node: '" + node.replaceAll( "%PLAYER_NAME%", player.getName() ) );
               Duties.GetInstance().LogMessage( "Error occurred: " + exception.getMessage() + ". Ignoring it!" );
            }
         }
      }
      catch( Exception exception )
      {
         throw new ActionException( "Failed while enabling temporary permissions: ", exception );
      }
   }

   @Override
   public void onDisable( Player player ) throws ActionException
   {
      //Removes temporary permissions
      if( Duties.Config.GetStringList( "Actions.TemporaryPermissions" ) == null ) return;
      try
      {
         int count = 0;
         for( String node : Duties.Config.GetStringList( "Actions.TemporaryPermissions" ) )
         {
            try
            {
               if( Duties.Config.GetBoolean( "Vault.Permissions" ) && Duties.GetInstance().getServer().getPluginManager().isPluginEnabled( "Vault" ) )
               {
                  if( node.contains( ": " ) )
                  {
                     if( node.split( ": " )[1].equalsIgnoreCase( "true" ) )
                     {
                        Duties.VaultAdapter.permission.playerRemoveTransient( player, node.split( ": " )[0].replaceAll( "%PLAYER_NAME%", player.getName() ) );
                     }
                     else if( node.split( ": " )[1].equalsIgnoreCase( "false" ) )
                     {
                        Duties.VaultAdapter.permission.playerAddTransient( player, node.split( ": " )[0].replaceAll( "%PLAYER_NAME%", player.getName() ) );
                     }
                     else
                     {
                        Duties.GetInstance().LogMessage( "Failed while disabling temporary permissions: '" + ( node.split( ": " )[1] ) + "' is not a valid value for node: " + node.split( ( ": " ) )[0] + ". Ignoring it!" );
                        continue;
                     }
                  }
                  else
                  {
                     Duties.VaultAdapter.permission.playerRemoveTransient( player, node.replaceAll( "%PLAYER_NAME%", player.getName() ) );
                  }
               }
               else
               {
                  if( !player.isOnline() )
                  {
                     return;
                  }
                  else
                  {
                     player.removeAttachment( Duties.Memories.get( player.getUniqueId() ).TemporaryPermissions.get( count ) );
                  }
               }
            }
            catch( Exception exception )
            {
               Duties.GetInstance().LogMessage( "Failed while disabling temporary permissions: Not a valid permission node: '" + node.replaceAll( "%PLAYER_NAME%", player.getName() ) );
               Duties.GetInstance().LogMessage( "Error occurred: " + exception.getMessage() + ". Ignoring it!" );
            }

            count++;
         }
      }
      catch( Exception exception )
      {
         throw new ActionException( "Failed while removing temporary permissions: ", exception );
      }
   }
}
