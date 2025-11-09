package sbs.mira.verse;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_21_R6.entity.CraftPlayer;
import sbs.mira.core.model.MiraPlayerModel;

/**
 * [ruh roh raggy...]
 * created on 2025-08-17.
 *
 * @author jj.mira.sbs
 * @author jd.mira.sbs
 * @version 1.0.1
 * @see MiraVersePulse
 * @since 1.0.0
 */
public final
class MiraVersePlayer
  extends MiraPlayerModel<MiraVersePulse>
{
  
  public
  MiraVersePlayer( CraftPlayer player, MiraVersePulse pulse )
  {
    super( player, pulse );
    
    toggle_visibilities( );
  }
  
  /**
   * players participating in the match should not be able to see spectators flying around.
   * spectators should be able to see everyone, but not interfere with the match participants at all.
   * <ul>
   *   <li>`setCollidable(false)` ensures spectators cannot bump match participants around.</li>
   * </ul>
   */
  private
  void toggle_visibilities( )
  {
    if ( has_team( ) )
    {
      this.bukkit( ).setCollidable( true );
      
      for ( MiraVersePlayer mira_player : pulse( ).model( ).players( ) )
      {
        if ( !mira_player.equals( this ) )
        {
          if ( mira_player.has_team( ) )
          {
            mira_player.bukkit( ).showPlayer( this.pulse( ).plugin( ), this.player );
            this.player.showPlayer( this.pulse( ).plugin( ), mira_player.bukkit( ) );
          }
          else
          {
            mira_player.bukkit( ).showPlayer( this.pulse( ).plugin( ), this.player );
            this.player.hidePlayer( this.pulse( ).plugin( ), mira_player.bukkit( ) );
          }
        }
      }
    }
    else
    {
      player.setCollidable( false );
      for ( MiraVersePlayer mira_player : pulse( ).model( ).players( ) )
      {
        if ( !mira_player.equals( this ) )
        {
          if ( mira_player.has_team( ) )
          {
            mira_player.bukkit( ).hidePlayer( this.pulse( ).plugin( ), this.player );
            this.player.showPlayer( this.pulse( ).plugin( ), mira_player.bukkit( ) );
          }
          else
          {
            mira_player.bukkit( ).showPlayer( this.pulse( ).plugin( ), this.player );
            this.player.showPlayer( this.pulse( ).plugin( ), mira_player.bukkit( ) );
          }
        }
      }
    }
  }
  
  /**
   * updates this player's display name.
   * this should be called whenever their
   * team changes or rank changes.
   */
  public
  void changes_name( )
  {
    String prefix = "";
    if ( bukkit( ).hasPermission( "mira.administrator" ) )
    {
      prefix = ChatColor.GOLD + "#";
    }
    else if ( bukkit( ).hasPermission( "mira.moderator" ) )
    {
      prefix = ChatColor.DARK_PURPLE + "#";
    }
    else
    {
      
      
      if ( bukkit( ).hasPermission( "mira.founder" ) )
      {
        prefix = ChatColor.DARK_BLUE + "#" + prefix;
      }
      else if ( bukkit( ).hasPermission( "war.donator" ) )
      {
        prefix = ChatColor.GREEN + "#" + prefix;
      }
    }
    /*if ( pulse( ).cache( ).getCurrentMap( ).isCreator( crafter( ).getUniqueId( ) ) )
    {
      prefix = ChatColor.DARK_RED + "#" + prefix;
    }*/
    
    ChatColor teamColor = has_team( ) ? team( ).color( ) : ChatColor.LIGHT_PURPLE;
    bukkit( ).setDisplayName( prefix + teamColor + name( ) + ChatColor.WHITE );
  }
}
