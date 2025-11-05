package sbs.mira.pvp;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_21_R6.entity.CraftPlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import sbs.mira.core.model.MiraPlayerModel;
import sbs.mira.core.model.map.MiraTeamModel;
import sbs.mira.pvp.stats.WarStats;

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
  
  /**
   * @see sbs.mira.pvp.stats.WarStats
   */
  private WarStats stats;
  /***
   * true if the player has indicated that they would like to join a team (once available).
   */
  private boolean joined;
  private MiraTeamModel team;
  
  public
  MiraVersePlayer( CraftPlayer player, MiraVersePulse pulse )
  {
    super( player, pulse );
    
    this.joined = false;
    this.team = null;
    
    changes_visibility( );
  }
  
  /**
   * @see #stats
   */
  public
  WarStats stats( )
  {
    return stats;
  }
  
  /**
   * @see #joined
   */
  public
  boolean joined( )
  {
    return joined;
  }
  
  /**
   * @see #joined
   */
  public
  void joined( boolean joined )
  {
    this.joined = joined;
  }
  
  /**
   * @return true if the mira pvp stan has an [sic, lol] designated team.
   */
  public
  boolean has_team( )
  {
    return team != null;
  }
  
  /**
   * returns the team that the player is currently associated with.
   * this is the team that the player currently on during a match.
   *
   * @return Player's associated team.
   */
  public @NotNull
  MiraTeamModel team( )
  {
    return team;
  }
  
  /**
   * @param new_team the player is joining this team (consensually).
   */
  public
  void joins( @Nullable MiraTeamModel new_team )
  {
    this.team = new_team;
    
    changes_visibility( );
    changes_name( );
  }
  
  /**
   * players participating in the match should not be able to see spectators flying around.
   * spectators should be able to see everyone, but not interfere with the match participants at all.
   * <ul>
   *   <li>`setCollidable(false)` ensures spectators cannot bump match participants around.</li>
   * </ul>
   */
  private
  void changes_visibility( )
  {
    if ( has_team( ) )
    {
      this.crafter( ).setCollidable( true );
      for ( MiraVersePlayer player : pulse( ).model( ).players( ).values( ) )
      {
        if ( !player.equals( this ) )
        {
          if ( player.has_team( ) )
          {
            player.crafter( ).showPlayer( this.player );
            this.player.showPlayer( player.crafter( ) );
          }
          else
          {
            player.crafter( ).showPlayer( this.player );
            this.player.hidePlayer( player.crafter( ) );
          }
        }
      }
    }
    else
    {
      player.setCollidable( false );
      for ( MiraVersePlayer player : pulse( ).model( ).players( ).values( ) )
      {
        if ( !player.equals( this ) )
        {
          if ( player.has_team( ) )
          {
            player.crafter( ).hidePlayer( this.player );
            this.player.showPlayer( player.crafter( ) );
          }
          else
          {
            player.crafter( ).showPlayer( this.player );
            this.player.showPlayer( player.crafter( ) );
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
    if ( crafter( ).hasPermission( "war.admin" ) )
    {
      prefix = ChatColor.GOLD + "@";
    }
    else
    {
      if ( crafter( ).hasPermission( "war.mod" ) )
      {
        prefix = ChatColor.DARK_PURPLE + "@";
      }
      
      if ( crafter( ).hasPermission( "war.donatorplus" ) )
      {
        prefix = ChatColor.YELLOW + "#" + prefix;
      }
      else if ( crafter( ).hasPermission( "war.donator" ) )
      {
        prefix = ChatColor.GREEN + "#" + prefix;
      }
    }
    /*if ( pulse( ).cache( ).getCurrentMap( ).isCreator( crafter( ).getUniqueId( ) ) )
    {
      prefix = ChatColor.DARK_RED + "#" + prefix;
    }*/
    
    ChatColor teamColor = has_team( ) ? team( ).colour( ) : ChatColor.LIGHT_PURPLE;
    crafter( ).setDisplayName( prefix + teamColor + name( ) + ChatColor.WHITE );
  }
}
