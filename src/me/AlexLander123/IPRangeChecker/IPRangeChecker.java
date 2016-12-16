package me.AlexLander123.IPRangeChecker;

import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.google.common.net.InetAddresses;

import fr.xephi.authme.api.NewAPI;
import fr.xephi.authme.cache.auth.PlayerAuth;
import fr.xephi.authme.datasource.DataSource;

public class IPRangeChecker extends JavaPlugin{
	
	
	public final Logger logger = Logger.getLogger("Minecraft");
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " Has Been Disabled!");
	}
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info(pdfFile.getName() + " Version "  + pdfFile.getVersion() + " by " + pdfFile.getAuthors() + " Has Been Enabled!");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLable, String[] args){
		if(commandLable.equalsIgnoreCase("checkiprange")){
			if(sender.hasPermission("iprangechecker.check")){
			if(args.length == 2){
				if((InetAddresses.isInetAddress(args[0])) && (InetAddresses.isInetAddress(args[1]))){
					List<String> matchingPlayer = new ArrayList<String>();
					try {
						Field dataSourceField = NewAPI.getInstance().getClass().getDeclaredField("dataSource");
						dataSourceField.setAccessible(true);
						DataSource database = (DataSource) dataSourceField.get(NewAPI.getInstance());
						List<PlayerAuth> auths = database.getAllAuths();
						for(PlayerAuth auth : auths){
							if(isValidRange(args[0], args[1], auth.getIp())){
								matchingPlayer.add(auth.getRealName());
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					if(matchingPlayer.size() > 0){
						sender.sendMessage("[IPRangeChecker] There are " + matchingPlayer.size() + " matching players:");
						StringBuilder stringbuilder = new StringBuilder();
						for(String name : matchingPlayer){
							if(stringbuilder.length() > 0){
								stringbuilder.append(",");
							}
							stringbuilder.append(name);
						}
						sender.sendMessage("[IPRangeChecker] " + stringbuilder.toString() + ".");
					}else{
						sender.sendMessage("[IPRangeChecker] There are no matching players.");
					}
				}else{
					sender.sendMessage("[IPRangeChecker] Invalid IP.");
				}
			}else{
				sender.sendMessage("[IPRangeChecker] Correct Usage: /checkiprange <Starting IP> <Ending IP>");
			}
			}
		}

		return false;
	}
	
	public static long ipToLong(InetAddress ip) {
		byte[] octets = ip.getAddress();
		long result = 0;
		for (byte octet : octets) {
			result <<= 8;
			result |= octet & 0xff;
		}
		return result;
	}

	public static boolean isValidRange(String ipStart, String ipEnd,
			String ipToCheck) {
		try {
			long ipLo = ipToLong(InetAddress.getByName(ipStart));
			long ipHi = ipToLong(InetAddress.getByName(ipEnd));
			long ipToTest = ipToLong(InetAddress.getByName(ipToCheck));
			return (ipToTest >= ipLo && ipToTest <= ipHi);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return false;
		}
	}

}
