package dev.skydynamic.where_are_you;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.StringUtils;

public class Utils {
    public static Vec3d getDimensionPos(String dimensionTypePath, Vec3d currentPos) {
        double x = currentPos.x;
        double y = currentPos.y;
        double z = currentPos.z;
        switch (dimensionTypePath) {
            case "overworld" -> {
                return new Vec3d(x / 8, y, z / 8);
            }
            case "the_nether" -> {
                return new Vec3d(x * 8, y, z * 8);
            }
        }
        return null;
    }

    public static String getOtherDimensionString(Identifier dimension) {
        switch (dimension.getPath()) {
            case "overworld" -> {
                return "minecraft:the_nether";
            }
            case "the_nether" -> {
                return "minecraft:overworld";
            }
        }
        return "";
    }

    public static Formatting getColor(String dimensionString) {
        switch (dimensionString) {
            case "overworld" -> {
                return Formatting.DARK_GREEN;
            }
            case "the_nether" -> {
                return Formatting.DARK_RED;
            }
            case "the_end" -> {
                return Formatting.DARK_PURPLE;
            }
        }
        return Formatting.WHITE;
    }

    public static Formatting getCoordinateColor(String dimensionString) {
        switch (dimensionString) {
            case "overworld" -> {
                return Formatting.GREEN;
            }
            case "the_nether" -> {
                return Formatting.RED;
            }
            case "the_end" -> {
                return Formatting.LIGHT_PURPLE;
            }
        }
        return Formatting.WHITE;
    }

    public static boolean giveEffect(ServerPlayerEntity player) {
        return player.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 15*20, 1, false, false), player);
    }

    public static MutableText buildText(ServerPlayerEntity targetPlayer) {
        MutableText text = Text.literal("");
        // 当前世界信息
        String dimensionTypePath = targetPlayer.getWorld().getDimensionKey().getValue().getPath();
        Vec3d currentDimensionPos = targetPlayer.getPos();
        // 另一个维度信息 (末地除外)
        Vec3d currentPos;
        if (!dimensionTypePath.equals("the_end")) {
            currentPos = getDimensionPos(dimensionTypePath, currentDimensionPos);
        } else {
            currentPos = targetPlayer.getPos();
        }
        String targetPlayerName = targetPlayer.getGameProfile().getName();
        Identifier currentDimensionId = targetPlayer.getWorld().getDimensionKey().getValue();
        String otherDimensionString = getOtherDimensionString(currentDimensionId);
        // 构建Text
        MutableText playerNameText = Text.literal(targetPlayerName).styled(style -> style
            .withColor(Formatting.YELLOW)
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityContent(targetPlayer.getType(), targetPlayer.getUuid(), targetPlayer.getName()))));
        MutableText dimensionTypeText = Text.literal(StringUtils.capitalize(dimensionTypePath)).styled(style -> style
            .withColor(getColor(currentDimensionId.getPath()))
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of(targetPlayer.getWorld().getDimensionKey().getValue().toString()))));
        MutableText currentDimensionPosText = Text.literal("[%s, %s, %s]".formatted(Math.round(currentDimensionPos.x), Math.round(currentDimensionPos.y), Math.round(currentDimensionPos.z)))
            .styled(style -> style
                .withColor(getCoordinateColor(currentDimensionId.getPath()))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("点我传送!")))
                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp %s %s %s".formatted(currentDimensionPos.x, currentDimensionPos.y, currentDimensionPos.z))));
        MutableText addVoxelMapWayPointText = Text.literal("[+V]").styled(style -> style
            .withColor(Formatting.AQUA)
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("§bVoxelmap§r: 点此以高亮坐标点, 或者Ctrl点击添加路径点§bVoxelmap§r: 点此以高亮坐标点, 或者Ctrl点击添加路径点")))
            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/newWaypoint x:%s, y:%s, z:%s, dim:%s"
                .formatted(currentPos.x, currentPos.y, currentPos.z, otherDimensionString))));
        MutableText addXaeroWayPointText = Text.literal("[+X]").styled(style -> style
            .withColor(Formatting.GOLD)
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("§6Xaeros Minimap§r: 点击添加路径点§6Xaeros Minimap§r: 点击添加路径点")))
            .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "xaero_waypoint_add:%s's Location:%s:%s:%s:%s:6:false:0"
                .formatted(targetPlayerName, targetPlayerName.substring(0, 1), currentPos.x, currentPos.y, currentPos.z))));
        MutableText currentPosText = Text.literal("[%s, %s, %s]".formatted(Math.round(currentPos.x), Math.round(currentPos.y), Math.round(currentPos.z)))
            .styled(style -> style
                .withColor(getCoordinateColor(otherDimensionString.replace("minecraft:", "")))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("点我传送!")))
                .withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/execute in %s run tp %s %s %s".formatted(otherDimensionString, currentPos.x, currentPos.y, currentPos.z))));
        MutableText otherDimensionText = Text.literal(StringUtils.capitalize(otherDimensionString.replace("minecraft:", ""))).styled(style -> style
            .withColor(getColor(otherDimensionString.replace("minecraft:", "")))
            .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of(otherDimensionString))));
        if (!currentDimensionId.equals(new Identifier("minecraft", "the_end"))) {
            text.append(playerNameText).append(" @ ")
                .append(dimensionTypeText).append(" ")
                .append(currentDimensionPosText).append(" ")
                .append(addVoxelMapWayPointText).append(" ")
                .append(addXaeroWayPointText).append(" -> ")
                .append(otherDimensionText).append(" ")
                .append(currentPosText);
        } else {
            text.append(playerNameText).append(" @ ")
                .append(dimensionTypeText).append(" ")
                .append(currentDimensionPosText).append(" ")
                .append(addVoxelMapWayPointText).append(" ")
                .append(addXaeroWayPointText);
        }

        return text;
    }
}
