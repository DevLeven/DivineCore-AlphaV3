package fun.divinetales.Core.Events.ChatEvents;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fun.divinetales.Core.CoreMain;
import fun.divinetales.Core.GUI.ElementGUI;
import fun.divinetales.Core.Tasks.SkinApplier;
import fun.divinetales.Core.Utils.ChatUtils.MessageUtils;
import fun.divinetales.Core.Utils.InventoryUtils.GUIManager;
import fun.divinetales.Core.Utils.MYSQL.Data.SQLChangeSkin;
import fun.divinetales.Core.Utils.MYSQL.Data.SQLPlayerData;
import fun.divinetales.Core.Utils.MYSQL.Data.SQLPlayerProfile;
import fun.divinetales.Core.Utils.PlayerInfoChanger;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_16_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import static fun.divinetales.Core.Utils.ColorUtil.*;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.UUID;


public class JoinEvent implements Listener {

    private final GUIManager manager = CoreMain.getInstance().getGUIManager();

    MessageUtils msgUtil = CoreMain.getInstance().getMsgUtil();
    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        CoreMain.getInstance().getPlayerManager().registerPlayer(p.getUniqueId());
        CoreMain.getInstance().getPlayerProfileManager().registerPlayer(p.getUniqueId());
        SQLPlayerData playerData = CoreMain.getInstance().getSqlPlayerData();
        SQLPlayerProfile profile = CoreMain.getInstance().getSqlPlayerProfile();
        SQLChangeSkin skin = new SQLChangeSkin(CoreMain.getInstance());


        if (p.isOp() || p.hasPermission("staff.welcome")) {

            String welcome = this.msgUtil.getCReplaceMessage(MessageUtils.Message.DIVINESTAFF) + " " + color("&f&lWelcome! :)");

            TextComponent msg = new TextComponent(welcome);
            msg.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(color("&f&lPlugin is working as expected!")).create()));
            p.spigot().sendMessage(msg);
        }

        if (CoreMain.getInstance().getConfigUtils().getBoolean("use_sql") && CoreMain.getInstance().getSql().isConnected()) {
            if (!CoreMain.getInstance().getSqlPlayerData().exists(p.getUniqueId())) {
                playerData.createPlayer(p);
                profile.createProfile(p);
            }
        }


        skin.createPlayerSkin(p);

        if (skin.is_Skin(p.getUniqueId())) {
            PlayerInfoChanger.ChangeSkin(p, p.getUniqueId());
            msgPlayer(p, color("&a&lSkin has been changed!"));
        }


        if (CoreMain.getInstance().getSql().isConnected() && playerData.exists(p.getUniqueId())) {
            if (playerData.getElement(p.getUniqueId()) == null) {
                ElementGUI elementGUI = new ElementGUI("BMF-ELEMENT", 9 * 3, color("&8&l>>&5&lElement Selector&8&l<<"));
                manager.openGUI(p, elementGUI);
            }
        }

    }


}

