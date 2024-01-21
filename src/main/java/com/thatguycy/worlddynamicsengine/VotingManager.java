package com.thatguycy.worlddynamicsengine;

import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class VotingManager {
    private final NationManager nationManager;
    private final Map<Nation, Vote> ongoingVotes = new HashMap<>();

    public VotingManager(NationManager nationManager, JavaPlugin plugin) {
        this.nationManager = nationManager;
        startVoteChecker(plugin);
    }

    private static class Vote {
        Nation nation;
        String law;
        Map<String, Boolean> votes; // Tracks player votes
        long endTime;

        public Vote(Nation nation, String law) {
            this.nation = nation;
            this.law = law;
            this.votes = new HashMap<>();
            this.endTime = System.currentTimeMillis() + (WorldDynamicsEngine.lawVotingTime() * 1000L);
        }
    }

    private void startVoteChecker(JavaPlugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
                checkVotes();
            }
        }.runTaskTimer(plugin, 20L, 20L); // Check every second
    }

    public void startVote(Nation nation, String voteType) {
        if (ongoingVotes.containsKey(nation)) {
            notifyGovernmentMembers(nation, "An active vote is already in progress.");
            return;
        }

        Vote newVote = new Vote(nation, voteType);
        ongoingVotes.put(nation, newVote);

        String voteDescription = getVoteDescription(voteType);
        notifyGovernmentMembers(nation, "A new vote has started: " + voteDescription);
        notifyGovernmentMembers(nation, "Please vote using /wde vote yes or /wde vote no.");

        if (getOnlineGovernmentMembers(nation).isEmpty()) {
            applyVoteResult(newVote, true);
        }
    }

    public void castVote(Nation nation, Player player, boolean vote) {
        Vote currentVote = ongoingVotes.get(nation);
        if (currentVote == null || System.currentTimeMillis() > currentVote.endTime) {
            player.sendMessage("No active vote or vote has ended.");
            return;
        }

        currentVote.votes.put(player.getName(), vote);
        player.sendMessage("Your vote has been cast.");
    }

    public void checkVotes() {
        Iterator<Map.Entry<Nation, Vote>> it = ongoingVotes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Nation, Vote> entry = it.next();
            Vote vote = entry.getValue();

            if (System.currentTimeMillis() > vote.endTime) {
                boolean result = tallyVotes(vote);
                applyVoteResult(vote, result);
                it.remove(); // Remove the vote after processing
            }
        }
    }

    public boolean isVoteOngoing(Nation nation) {
        return ongoingVotes.containsKey(nation);
    }

    private boolean tallyVotes(Vote vote) {
        int yesCount = (int) vote.votes.values().stream().filter(Boolean::booleanValue).count();
        int noCount = vote.votes.size() - yesCount;
        return yesCount > noCount;
    }

    private void applyVoteResult(Vote vote, boolean result) {
        if (result) {
            if (vote.law.startsWith("AddLaw: ")) {
                String law = vote.law.substring(8); // Extract actual law text
                NationProperties properties = nationManager.getNationProperties(vote.nation.getName());
                properties.addLaw(law); // Add the law
                notifyGovernmentMembers(vote.nation, "The law has been approved and added: " + law);
                nationManager.saveNations();
            } else if (vote.law.startsWith("RemoveLaw: ")) {
                int lawId = Integer.parseInt(vote.law.substring(11));
                NationProperties properties = nationManager.getNationProperties(vote.nation.getName());
                properties.removeLaw(lawId); // Remove the law
                notifyGovernmentMembers(vote.nation, "The law has been removed: ID " + lawId);
                nationManager.saveNations();
            }
        } else {
            notifyGovernmentMembers(vote.nation, "The vote did not pass.");
        }
    }

    private void notifyGovernmentMembers(Nation nation, String message) {
        for (Resident resident : nation.getResidents()) {
            Player player = Bukkit.getPlayer(resident.getName());
            if (player != null && player.isOnline()) {
                player.sendMessage(message);
            }
        }
    }

    private Set<Player> getOnlineGovernmentMembers(Nation nation) {
        Set<Player> onlineMembers = new HashSet<>();
        for (Resident resident : nation.getResidents()) {
            Player player = Bukkit.getPlayer(resident.getName());
            if (player != null && player.isOnline()) {
                onlineMembers.add(player);
            }
        }
        return onlineMembers;
    }

    private String getVoteDescription(String voteType) {
        if (voteType.startsWith("AddLaw: ")) {
            return "Add Law: " + voteType.substring(8);
        } else if (voteType.startsWith("RemoveLaw: ")) {
            return "Remove Law ID: " + voteType.substring(11);
        }
        return "Unknown Vote Type";
    }
}
