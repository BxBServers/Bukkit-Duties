package me.th3pf.plugins.duties.actions;

import me.th3pf.plugins.duties.Duties;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class TemporaryGroupsAction implements Action
{
   @Override
   public void onEnable( Player player ) throws ActionException
   {
      if( !Duties.Config.GetBoolean( "Vault.Permissions" ) || !Duties.GetInstance().getServer().getPluginManager().isPluginEnabled( "Vault" ) )
         return;

      try
      {
         if( Duties.Config.GetStringList( "Actions.TemporaryGroups" ) == null ) return;
         for( String group : Duties.Config.GetStringList( "Actions.TemporaryGroups" ) )
         {
            try
            {
               //Adding group
               if( Duties.GetInstance().getServer().getPluginManager().isPluginEnabled( "bPermissions" ) )
               {
                  for( World world : Duties.GetInstance().getServer().getWorlds() )
                     Duties.VaultAdapter.permission.playerAddGroup( world, player.getName(), group.replaceAll( "%PLAYER_NAME%", player.getName() ) );
               }
               else if( !Duties.VaultAdapter.permission.playerInGroup( (String) null, player.getName(), group.replaceAll( "%PLAYER_NAME%", player.getName() ) ) )
                  Duties.VaultAdapter.permission.playerAddGroup( (String) null, player.getName(), group.replaceAll( "%PLAYER_NAME%", player.getName() ) );
            }
            catch( Exception exception )
            {
               Duties.GetInstance().LogMessage( "Failed while enabling temporary groups: Not a valid group: " + group.replaceAll( "%PLAYER_NAME%", player.getName() ) );
               Duties.GetInstance().LogMessage( "Error occurred: " + exception.getMessage() + ". Ignoring it!" );

            }
         }
      }
      catch( Exception exception )
      {
         throw new ActionException( "Failed while enabling temporary groups: ", exception );
      }
   }

   @Override
   public void onDisable( Player player ) throws ActionException
   {
      if( !Duties.Config.GetBoolean( "Vault.Permissions" ) || !Duties.GetInstance().getServer().getPluginManager().isPluginEnabled( "Vault" ) )
         return;

      try
      {
         if( Duties.Config.GetStringList( "Actions.TemporaryGroups" ) == null ) return;
         for( String group : Duties.Config.GetStringList( "Actions.TemporaryGroups" ) )
         {
            try
            {
               //Removing group
               if( Duties.GetInstance().getServer().getPluginManager().isPluginEnabled( "bPermissions" ) )
               {
                  for( World world : Duties.GetInstance().getServer().getWorlds() )
                     Duties.VaultAdapter.permission.playerRemoveGroup( world, player.getName(), group.replaceAll( "%PLAYER_NAME%", player.getName() ) );
               }
               else if( Duties.VaultAdapter.permission.playerInGroup( (String) null, player.getName(), group.replaceAll( "%PLAYER_NAME%", player.getName() ) ) )
                  Duties.VaultAdapter.permission.playerRemoveGroup( (String) null, player.getName(), group.replaceAll( "%PLAYER_NAME%", player.getName() ) );
            }
            catch( Exception exception )
            {
               Duties.GetInstance().LogMessage( "Failed while disabling temporary groups: Not a valid group: " + group.replaceAll( "%PLAYER_NAME%", player.getName() ) );
               Duties.GetInstance().LogMessage( "Error occurred: " + exception.getMessage() + ". Ignoring it!" );
            }
         }
      }
      catch( Exception exception )
      {
         throw new ActionException( "Failed while disabling temporary groups: ", exception );
      }
   }
}
