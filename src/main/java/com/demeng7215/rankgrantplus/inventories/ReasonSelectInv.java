package com.demeng7215.rankgrantplus.inventories;

import com.demeng7215.demlib.api.gui.CustomInventory;
import com.demeng7215.demlib.api.messages.MessageUtils;
import com.demeng7215.rankgrantplus.RankGrantPlus;
import com.demeng7215.rankgrantplus.utils.DurationUtils;
import com.demeng7215.rankgrantplus.utils.XMaterial;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

class ReasonSelectInv extends CustomInventory {

	private final RankGrantPlus i;
	private final DurationUtils duration;

	ReasonSelectInv(RankGrantPlus i, OfflinePlayer target, Player op, String rank, DurationUtils duration) {
		super(i.getConfiguration().getInt("gui-size.reasons"),
				MessageUtils.colorize(i.getLang().getString("gui-names.select-reason")
						.replace("%target%", target.getName())));

		this.i = i;
		this.duration = duration;

		List<Integer> slotsOccupied = new ArrayList<>();

		for (String reason : i.getConfiguration().getConfigurationSection("reasons").getKeys(false)) {

			String path = "reasons." + reason + ".";

			if (op.hasPermission(i.getConfiguration().getString(path + "permission"))) {

				List<String> finalLore = new ArrayList<>();
				for (String lore : i.getConfiguration().getStringList(path + "lore")) {
					finalLore.add(replaceInfo(lore, rank, target));
				}

				int slot = i.getConfiguration().getInt(path + "slot") - 1;

				if (slot <= 54 && !slotsOccupied.contains(slot)) {

					slotsOccupied.add(slot);

					setItem(slot,
							XMaterial.valueOf(i.getConfiguration().getString(path + "item"))
									.parseItem(),
							replaceInfo(i.getConfiguration().getString(path + "name"), rank, target),
							finalLore, player -> new ConfirmationInv(i, target, op, rank, duration,
									i.getConfiguration().getString(path + "name")).open(op));
				} else {
					MessageUtils.console("&cYou have chosen to display 2 reasons in the same slot " +
							"or have a slot ID higher than 54. Please check your configuration.yml.");
				}
			}
		}
	}

	private String replaceInfo(String s, String rank, OfflinePlayer target) {

		String rankName;

		if (i.getRanks().getString("ranks." + rank + ".name") == null) {
			rankName = rank;
		} else {
			rankName = RankGrantPlus.stripColorCodes(i.getRanks().getString("ranks." + rank + ".name"));
		}

		String duration;

		if (this.duration.isPermanent()) {
			duration = i.getConfiguration().getString("duration.word-permanent");
		} else {
			duration = replaceTimes(i.getConfiguration().getString("duration.duration-format"));
		}

		return s.replace("%rank%", rankName)
				.replace("%target%", target.getName())
				.replace("%duration%",
						MessageUtils.colorize(duration));
	}

	private String replaceTimes(String s) {
		return s.replace("%weeks%", duration.getWeeks())
				.replace("%days%", this.duration.getDays())
				.replace("%hours%", this.duration.getHours())
				.replace("%minutes%", this.duration.getMinutes())
				.replace("%seconds%", this.duration.getSeconds());
	}
}
