package com.mineprom.skinny;

import java.awt.Graphics;

import static com.google.common.base.Preconditions.checkNotNull;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.ChunkGenerator.BiomeGrid;
import org.bukkit.generator.ChunkGenerator.ChunkData;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.noise.SimplexOctaveGenerator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.mineprom.islandgen.IslandGen;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import io.netty.handler.codec.base64.Base64;
import net.minecraft.server.v1_15_R1.EntityPlayer;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_15_R1.PacketPlayOutPlayerInfo.EnumPlayerInfoAction;
import net.minecraft.server.v1_15_R1.PacketPlayOutRespawn;


public class Skinny extends JavaPlugin implements Listener{
	World world;
	private static final String ID_FORMAT     = "https://api.mineskin.org/get/id/%s";
	private static final String URL_FORMAT    = "https://api.mineskin.org/generate/url?url=%s&%s";
	private static final String UPLOAD_FORMAT = "https://api.mineskin.org/generate/upload?%s";
	private static final String USER_FORMAT   = "https://api.mineskin.org/generate/user/%s?%s";
	private String pathformat = "plugins/Skinny/skins/";
	private final JsonParser jsonParser = new JsonParser();
	private final Gson       gson       = new Gson();
	private final String alexvalue = "eyJ0aW1lc3RhbXAiOjE1NjgzMTAxNzkwMjIsInByb2ZpbGVJZCI6IjVkZTZlMTg0YWY4ZDQ5OGFiYmRlMDU1ZTUwNjUzMzE2IiwicHJvZmlsZU5hbWUiOiJBc3Nhc2luSmlhbmVyMjUiLCJzaWduYXR1cmVSZXF1aXJlZCI6dHJ1ZSwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlL2ZiOWFiMzQ4M2Y4MTA2ZWNjOWU3NmJkNDdjNzEzMTJiMGYxNmE1ODc4NGQ2MDY4NjRmM2IzZTljYjFmZDdiNmMiLCJtZXRhZGF0YSI6eyJtb2RlbCI6InNsaW0ifX19fQ";
	private final String alexsign = "Ss9ksiEgOjPerSeY0Kee2CovT72eEMKreDCdDZu8mGFMmiPNeL0Yj1P7Wh+i84XFu1c8zYhetubf0PtwX71myH9Kp9ByUlxSWLYs9sPo511rQDceXDIaz6otNEnvnDpIp7C6aZ0IQghpD/hOXYwBVfttqqUM/g1SlA5Y1pUk5pbt7W0eaF/U6ACvP9O6pWgapkr+VMpc2GpKxh/jsWJTC7s36Jg7C7ipRx0Cv+zUHvkpiTN9HY1Kl2p8moa2Lyo6KL26tih/vqKK3heFO7B771Fvc/CwRjM+OHeCVU0G5fWRF8nJYry6wzCtC+zElGQrwwAkPJElN+J1f33yheml34l251cH3vY5DWo6HyyTgH+fhweOGDURVtV3SM7ZaosOXspbNt3C+OI7ll5oW6rmfgKRs3Ma0K3LyXChWojnCO0e9fpOn5G4LC4R47YyKDE6hUZnhihQT3kSm9KUIVmbpwLeoyxUR06AbN3HnyLlp+C6Saqkaai2tokEJXLvdTvtW3NlhOn7GvDQvSAQt5aqe1Kzj/OVB1jyQ77MnElJKRbKct2xU9KIYhQ+U6fT8TC2B62S8tw5JMVffl5RAnx9GPgq3or5TKdFvryCUylldox9kUHvHfQOwnjaQX/06vKGJOSRPHpea1m05HKJ0yYLDW2Mg5YzjqJ78EgOx2x893k=";
	private final String stevevalue = "eyJ0aW1lc3RhbXAiOjE1MzU0Mzk5Njc4OTcsInByb2ZpbGVJZCI6ImIwZDczMmZlMDBmNzQwN2U5ZTdmNzQ2MzAxY2Q5OGNhIiwicHJvZmlsZU5hbWUiOiJPUHBscyIsInNpZ25hdHVyZVJlcXVpcmVkIjp0cnVlLCJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGM3YjA0NjgwNDRiZmVjYWNjNDNkMDBhM2E2OTMzNWE4MzRiNzM5Mzc2ODgyOTJjMjBkMzk4OGNhZTU4MjQ4ZCJ9fX0";
	private final String stevesign = "BkBpiTf/18n9steF8Uixhr8mk0ck9TUilDafb3dC3PyDaw05qfD41iUQLICz9irOX0PH8vW4f3OC+ebeG51RbGTA8w99uLgss7tzjeTwXxZDxa4OkB28N+3+m+DUDnGqhkL7t8aAVDBWab4i7oVg7s64PqtWPdgnYhtDcugqK4qV73VuQCFZ5fgJSBcTx4ZC//6Kx2seePIeW9xrbzVaaP0amrI0oXfS7gPFUfFegiU6z+C5kMLOLvNYULnvSDIyWJe4FEpvCy3grDcjSe751WTEm0nhixwo94ptLFqzfVw4dLmCBPM2VhN7VLJcVIK791wWS+7bSeRAyN9FTDG7ohogMnk20a6vB82u17WTi+5j/Od8PTxn0+d/yqqTxJi58Y9FOuD0tXIz4GjPucg1vAc+L5JL9eX2j67fIDft9AswXXykBjbJ0szp48wHdwDfulhFGTaMmiuIPvlW4kPA2XRSs32t9eA3+ySJYq81O3PmLpDldy1igeiqzi9aSGqoC0oUH7q71s8GQIEhlbGUyDiI0k9EbCNTAhuV1tQYoo6sCS6gmRzapHyhJjYDl8o1sytfWaiT/SYhNNLO6Z51aD6bC08tXCNXeQKQW4ANXnqQ+6oDAmXKCLzn6FPOMrLyHF6YsMd4rl4JuVs5QsTSkCtIvZHP4pOPc3DfbOc9sKA=";
	Skinny plugin;
	//MineskinClient client;
	@Override
	public void onEnable() {
		//client = new MineskinClient();
		// This will throw a NullPointerException if you don't have the command defined in your plugin.yml file!
		plugin = this;
		WorldCreator creator = new WorldCreator("placeholder");
		creator.generator(new IslandGen(500));
		world = creator.createWorld();
		this.getCommand("skinny").setExecutor(new SkinnyExecutor(this));
		getServer().getPluginManager().registerEvents(this, this);
	
	}

	@EventHandler
	public void onLogin(PlayerJoinEvent event) {
		getLogger().info("bruh");
		loadSkinAsync(event.getPlayer(), "mask.png");
		
	}
	
	
	
	
	public class SkinnyExecutor implements CommandExecutor{
		Skinny plugin;
		public SkinnyExecutor(Skinny plugin){
			this.plugin = plugin;
		}
		
		
		
		
		
		@Override
		public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			if (cmd.getName().equalsIgnoreCase("skinny") && sender instanceof Player) { // If the player typed /basic then do the following...
				if(args.length == 2) {
					if(args[0].equalsIgnoreCase("steal")) {
						UUID uid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
						Skin playerskin = loadPlayerSkin(uid);
						Model playermodel = getModelFromSkin(playerskin, ((Player) sender).getUniqueId());
						if(!playermodel.equals(Model.ALEX) && !playermodel.equals(Model.STEVE)) {
							changePlayerSkin((Player) sender, playerskin);
						}else if(playermodel.equals(Model.ALEX)){
							changePlayerSkin((Player) sender, alexvalue, alexsign);
						}else if(playermodel.equals(Model.STEVE)) {
							changePlayerSkin((Player) sender, stevevalue, stevesign);
						}
					}
				}
			
				else if(args.length == 1) {
					
					if(args[0].equalsIgnoreCase("mask")) {
						loadSkinAsync((Player) sender, "mask.png");
					}else if(args[0].equalsIgnoreCase("clear")) {
						resetDefaultSkin((Player) sender);
					}else if(args[0].equalsIgnoreCase("testworld")) {
						((Player) sender).teleport(world.getSpawnLocation());
					}
				
					return true;
				}
				
				return true;
			 
			
			}
			return false;
		}
	}

public void resetDefaultSkin(Player sender) {
	new BukkitRunnable() {
		@Override
		public void run() {
		Skin playerskin = loadPlayerSkin(((Player)sender).getUniqueId());
		Model playermodel = getModelFromSkin(playerskin, ((Player) sender).getUniqueId());
			if(playermodel.equals(Model.ALEX)){
				new BukkitRunnable() {public void run() {
					changePlayerSkin((Player) sender, alexvalue, alexsign);
					
				}}.runTask(plugin);
				return;
			}else if(playermodel.equals(Model.STEVE)) {
				new BukkitRunnable() {public void run() {
					changePlayerSkin((Player) sender, stevevalue, stevesign);
					
				}}.runTask(plugin);
				return;
			}
			
		final Skin plskin = playerskin;
			new BukkitRunnable() {public void run() {
				changePlayerSkin((Player) sender, plskin);
				
			}}.runTask(plugin);
		}
	
	}.runTaskAsynchronously(plugin);
}

public void loadSkinAsync(Player player, String filename) {
	new BukkitRunnable() {

		@Override
		public void run() {
			Bukkit.getLogger().info("ee");
			Skin playerskin = loadPlayerSkin(player.getUniqueId());
			Skin ahkh = mergeSkin(new File(pathformat + filename), player, getModelFromSkin(playerskin, player.getUniqueId()));
			new BukkitRunnable(){
				@Override
				public void run() {
				    changePlayerSkin(player, ahkh);
				}
			}.runTask(plugin);
		}
		
		
	}.runTaskAsynchronously(this);
}

public Skin getAlex() {
	return getSkinFromFile(new File(pathformat + "alex.png"), Model.ALEX);
}

public Skin getSteve() {
	return getSkinFromFile(new File(pathformat + "steve.png"), Model.STEVE);
}

public Skin getSkinFromFile(File file, Model model) {
	
	CloseableHttpClient httpClient = HttpClients.createDefault();
	HttpPost uploadFile = new HttpPost(String.format(UPLOAD_FORMAT, SkinOptions.create("", model, Visibility.PUBLIC).toUrlParam()));
	MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	
		try {
			BufferedImage imga = ImageIO.read(file);
			 BufferedImage output = new BufferedImage(imga.getWidth(),imga.getHeight(),BufferedImage.TYPE_INT_ARGB);
			 
			 Graphics g = output.getGraphics();
			 g.drawImage(imga,0,0,null);
			 ByteArrayOutputStream os = new ByteArrayOutputStream();
			 ImageIO.write(output, "png", os);
			 InputStream is = new ByteArrayInputStream(os.toByteArray());
			builder.addBinaryBody(
				    "file",
				     is
				    ,
				    ContentType.APPLICATION_OCTET_STREAM,
				    file.getPath()
				);
				HttpEntity multipart = builder.build();
				uploadFile.setEntity(multipart);
				CloseableHttpResponse response;
			response = httpClient.execute(uploadFile);
			HttpEntity responseEntity = response.getEntity();
			if (responseEntity != null) {
		           String retSrc = EntityUtils.toString(responseEntity); 
		           // parsing JSON
		           try {
		   			JsonObject jsonObject = jsonParser.parse(retSrc).getAsJsonObject();
		   			if (jsonObject.has("error")) {
		   				//callback.error(jsonObject.get("error").getAsString());
		   				//return;
		   				throw new JsonParseException("Json Parsing Error");
		   			}else {

		   				Skin overload = gson.fromJson(jsonObject, Skin.class);
		   				//changePlayerSkin(player, overload);
		   				return overload;
		   			}
		   			//this.nextRequest = System.currentTimeMillis() + ((long) ((skin.nextRequest + 10) * 1000L));
		   			//callback.done(skin);
		   		} catch (JsonParseException e) {
		   			//callback.parseException(e, body);
		   			throw new JsonParseException(e);
		   		} catch (Throwable throwable) {
		   			throw new RuntimeException(throwable);
		   		}
		    }
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new NullPointerException();
}
		
public Skin mergeSkin(File file, Player player, Model model) {
	CloseableHttpClient httpClient = HttpClients.createDefault();
	HttpPost uploadFile = new HttpPost(String.format(UPLOAD_FORMAT, SkinOptions.create("", model, Visibility.PUBLIC).toUrlParam()));
	MultipartEntityBuilder builder = MultipartEntityBuilder.create();
	//builder.addTextBody("field1", "yes", ContentType.TEXT_PLAIN);

	// This attaches the file to the POST:
	builder.addBinaryBody(
	    "file",
	    processImages((player).getUniqueId(), file)
	    ,
	    ContentType.APPLICATION_OCTET_STREAM,
	    file.getPath()
	);

	HttpEntity multipart = builder.build();
	uploadFile.setEntity(multipart);
	CloseableHttpResponse response;
	try {
		response = httpClient.execute(uploadFile);
		HttpEntity responseEntity = response.getEntity();
		if (responseEntity != null) {
	           String retSrc = EntityUtils.toString(responseEntity); 
	           // parsing JSON
	           try {
	   			JsonObject jsonObject = jsonParser.parse(retSrc).getAsJsonObject();
	   			if (jsonObject.has("error")) {
	   				//callback.error(jsonObject.get("error").getAsString());
	   				//return;
	   				throw new JsonParseException("Json Parsing Error");
	   			}else {

	   				Skin overload = gson.fromJson(jsonObject, Skin.class);
	   				//changePlayerSkin(player, overload);
	   				return overload;
	   			}
	   			//this.nextRequest = System.currentTimeMillis() + ((long) ((skin.nextRequest + 10) * 1000L));
	   			//callback.done(skin);
	   		} catch (JsonParseException e) {
	   			//callback.parseException(e, body);
	   			throw new JsonParseException(e);
	   		} catch (Throwable throwable) {
	   			throw new RuntimeException(throwable);
	   		}
	    }
		
		
	} catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	return null;
}
	
	
	
	
public InputStream processImages(UUID uniq, File f) {
	 try {
		 BufferedImage imga = ImageIO.read(f);
		 BufferedImage output = new BufferedImage(imga.getWidth(),imga.getHeight(),BufferedImage.TYPE_INT_ARGB);
		 BufferedImage imgb = getPlayerSkin(uniq);
		 Graphics g = output.getGraphics();
		 
		 g.drawImage(imgb, 0,0,null);
		 g.drawImage(imga,0,0,null);
		 
		 ByteArrayOutputStream os = new ByteArrayOutputStream();
		 ImageIO.write(output, "png", os);
		 InputStream is = new ByteArrayInputStream(os.toByteArray());
		 return is;
	 }catch(IOException x) {
		 x.printStackTrace();
		 return null;
	 }
 }



public Model getDefaultSkin(UUID uid) {
	if ((uid.hashCode() & 1) != 0) {
      return Model.ALEX;
    } else {
      return Model.STEVE;
    }
}
public Model getDefaultSkin(String uid) {
	UUID buid = UUID.fromString(uid);
    return getDefaultSkin(buid);
}



public Model getModelFromSkin(Skin skin, UUID uid) {
	try {
	String jsonString = new String(java.util.Base64.getDecoder().decode(skin.data.texture.value.getBytes()));
	getLogger().info(jsonString);
	JsonObject jsonObject = jsonParser.parse(jsonString).getAsJsonObject();
	
	
	if(jsonObject.getAsJsonObject("textures").has("SKIN")) {
		if (jsonObject.getAsJsonObject("textures").getAsJsonObject("SKIN").has("metadata")) {
			return Model.SLIM;
		}else {
			return Model.DEFAULT;
		}
	}
	}catch(Exception e) {	
	}
	
	return getDefaultSkin(uid);
	
	
	//JsonArray jsonArray = jsonObject.getAsJsonArray("textures");
}

public Skin loadPlayerSkin(UUID uuid) {
	checkNotNull(uuid);
	try {
	URL target = new URL("https://api.mineskin.org/generate/user/" + uuid.toString());
    HttpURLConnection con = (HttpURLConnection) target.openConnection();
    con.setRequestMethod("GET");
    con.setDoOutput(true);
    con.setConnectTimeout(1000);
    con.setReadTimeout(10000);
    BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
    JsonObject jsonObject = jsonParser.parse(reader).getAsJsonObject();
    if (jsonObject.has("error")) {
			//callback.error(jsonObject.get("error").getAsString());
			//return;
			throw new JsonParseException("Json Parsing Error");
		}else {

			Skin overload = gson.fromJson(jsonObject, Skin.class);
			//changePlayerSkin(player, overload);
			return overload;
		}
	}catch(IOException io) {
    return null;	
	}
}

public BufferedImage getPlayerSkin(UUID uniq) {
	// Thank you to Crafatar for providing avatars.
	try {
		URL url = new URL("https://crafatar.com/skins/" + uniq.toString());
		 BufferedImage imgc = ImageIO.read(url);
		 return imgc;
	}catch(IOException x) {
		x.printStackTrace();
		return null;
	}
}

public void changePlayerSkin(Player player, Skin skin) {
	if(player instanceof CraftPlayer) {
		GameProfile gp = ((CraftPlayer) player).getProfile();
		PropertyMap pm = gp.getProperties();
		Property skinProperty = new Property("textures", skin.data.texture.value, skin.data.texture.signature);
		Property current = Iterables.getFirst(gp.getProperties().get("textures"), null);
		if (current != null && current.getValue().equals(skinProperty.getValue())
                && (current.getSignature() != null && current.getSignature().equals(skinProperty.getSignature()))) {
            return;
        }
		pm.removeAll("textures");
	    pm.put("textures", skinProperty);
	}
	updatePlayers(player);
}

public void changePlayerSkin(Player player, String skinvalue, String skinsig) {
	if(player instanceof CraftPlayer) {
		GameProfile gp = ((CraftPlayer) player).getProfile();
		PropertyMap pm = gp.getProperties();
		Property skinProperty = new Property("textures", skinvalue, skinsig);
		Property current = Iterables.getFirst(gp.getProperties().get("textures"), null);
		if (current != null && current.getValue().equals(skinProperty.getValue())
                && (current.getSignature() != null && current.getSignature().equals(skinProperty.getSignature()))) {
            return;
        }
		pm.removeAll("textures");
	    pm.put("textures", skinProperty);
	}
	updatePlayers(player);
}

@SuppressWarnings( "deprecation" )
public void updatePlayers(Player player) {
	for (Player p : Bukkit.getOnlinePlayers()) {
	    p.hidePlayer(this, player);
	    p.showPlayer(this, player);
	}
	
	final EntityPlayer ep = ((CraftPlayer) player).getHandle();
    final PacketPlayOutPlayerInfo removeInfo = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, ep);
    final PacketPlayOutPlayerInfo addInfo = new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.ADD_PLAYER, ep);
    final Location loc = player.getLocation().clone();
    ep.playerConnection.sendPacket(removeInfo);
    ep.playerConnection.sendPacket(addInfo);
    player.teleport(world.getSpawnLocation());
    player.teleport(loc);
 /* Unnecessary:
    new BukkitRunnable() {
        @Override
        public void run() {
            player.teleport(loc);
            ep.playerConnection.sendPacket(new PacketPlayOutRespawn(ep.dimension, player.getWorld().getDifficulty().getValue(), ep.getWorld().getWorldData().getType(), ep.playerInteractManager.getGameMode()));
            player.updateInventory();
        }
    }.runTaskLater(this, 2L);

*/
 
}
}
