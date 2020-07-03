package me.frep.thotpatrol.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.Map.Entry;

public class UtilActionMessage {
	
    private List<AMText> Text = new ArrayList<AMText>();

    public AMText addText(String Message) {
        AMText Text = new AMText(Message);
        this.Text.add(Text);
        return Text;
    }

    public String getFormattedMessage() {
        String Chat = "[\"\",";
        for (AMText Text : this.Text) {
            Chat = Chat + Text.getFormattedMessage() + ",";
        }
        Chat = Chat.substring(0, Chat.length() - 1);
        Chat = Chat + "]";
        return Chat;
    }

    public void sendToPlayer(Player Player) {
        try {
            String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            Object base = null;

            Constructor<?> titleConstructor = getNMSClass("PacketPlayOutChat")
                    .getConstructor(getNMSClass("IChatBaseComponent"));
            if (version.contains("1_7") || version.contains("1_8_R1")) {
                base = getNMSClass("ChatSerializer").getMethod("a", String.class).invoke(null, getFormattedMessage());
            } else {
                base = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
                        .invoke(null, getFormattedMessage());
            }

            Object packet = titleConstructor.newInstance(base);

            sendPacket(Player, packet);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    public void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Class<?> getNMSClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("net.minecraft.server." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Class<?> getCBClass(String name) {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            return Class.forName("org.bukkit.craftbukkit." + version + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public enum ClickableType {
        RunCommand("run_command"), SuggestCommand("suggest_command"), OpenURL("open_url");

        public String Action;

        ClickableType(String Action) {
            this.Action = Action;
        }
    }

    public class AMText {
        private String Message = "";
        private Map<String, Map.Entry<String, String>> Modifiers = new HashMap<String, Entry<String, String>>();

        public AMText(String Text) {
            this.Message = Text;
        }

        public String getMessage() {
            return this.Message;
        }

        public String getFormattedMessage() {
            String Chat = "{\"text\":\"" + this.Message + "\"";
            for (String Event : this.Modifiers.keySet()) {
                Map.Entry<String, String> Modifier = this.Modifiers.get(Event);
                Chat = Chat + ",\"" + Event + "\":{\"action\":\"" + Modifier.getKey() + "\",\"value\":"
                        + Modifier.getValue() + "}";
            }
            Chat = Chat + "}";
            return Chat;
        }

        public AMText addHoverText(String... Text) {
            String Event = "hoverEvent";
            String Key = "show_text";
            String Value = "";
            if (Text.length == 1) {
                Value = "{\"text\":\"" + Text[0] + "\"}";
            } else {
                Value = "{\"text\":\"\",\"extra\":[";
                String[] arrayOfString;
                int j = (arrayOfString = Text).length;
                for (int i = 0; i < j; i++) {
                    String Message = arrayOfString[i];
                    Value = Value + "{\"text\":\"" + Message + "\"},";
                }
                Value = Value.substring(0, Value.length() - 1);
                Value = Value + "]}";
            }
            Map.Entry<String, String> Values = new AbstractMap.SimpleEntry<String, String>(Key, Value);
            this.Modifiers.put(Event, Values);
            return this;
        }

        public AMText addHoverItem(org.bukkit.inventory.ItemStack Item) {
            try {
                String Event = "hoverEvent";
                String Key = "show_item";
                Object craftItemStack = getCBClass("CraftItemStack");
                Class<?> items = Class.forName("org.bukkit.inventory.ItemStack");
                Object NMS = craftItemStack.getClass().getMethod("asNMSCopy", items).invoke(Item);
                String Value = NMS.getClass().getMethod("getTag").toString();
                Map.Entry<String, String> Values = new AbstractMap.SimpleEntry<String, String>(Key, Value);
                this.Modifiers.put(Event, Values);
                return this;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        public AMText setClickEvent(UtilActionMessage.ClickableType Type, String Value) {
            String Event = "clickEvent";
            String Key = Type.Action;
            Map.Entry<String, String> Values = new AbstractMap.SimpleEntry<String, String>(Key, "\"" + Value + "\"");
            this.Modifiers.put(Event, Values);
            return this;
        }
    }
}
