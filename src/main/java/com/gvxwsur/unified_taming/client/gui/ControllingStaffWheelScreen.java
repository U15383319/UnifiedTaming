package com.gvxwsur.unified_taming.client.gui;

import com.gvxwsur.unified_taming.UnifiedTaming;
import com.gvxwsur.unified_taming.network.NetworkHandler;
import com.gvxwsur.unified_taming.network.packet.SelectStaffModePacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ControllingStaffWheelScreen extends Screen {
    private static final int WHEEL_RADIUS = 80;
    private static final int INNER_RADIUS = 20; // Dead zone radius
    private static final int SEGMENT_COUNT = 7; // Number of modes
    private static final double SEGMENT_ANGLE = 360.0 / SEGMENT_COUNT;

    private int selectedMode = -1;
    private final int currentMode;

    public ControllingStaffWheelScreen(int currentMode) {
        super(Component.empty());
        this.currentMode = currentMode;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.selectedMode = getHoveredMode(mouseX, mouseY);

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        // Render semi-transparent background
        guiGraphics.fill(0, 0, this.width, this.height, 0x80000000);

        // Draw mode segments
        for (int i = 0; i < SEGMENT_COUNT; i++) {
            drawModeSegment(guiGraphics, centerX, centerY, i, i == selectedMode);
        }

        // Draw inner and outer circles (boundaries of the wheel)
        drawCircle(guiGraphics, centerX, centerY, INNER_RADIUS, 0xFFFFFFFF);
        drawCircle(guiGraphics, centerX, centerY, WHEEL_RADIUS, 0xFFFFFFFF);

        // Draw mode name
        if (selectedMode >= 0) {
            Component modeName = Component.translatable(getModeLangKey(selectedMode));
            int textWidth = this.font.width(modeName);
            guiGraphics.drawString(this.font, modeName, centerX - textWidth / 2, centerY + WHEEL_RADIUS + 20, 0xFFFFFF);
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void drawModeSegment(GuiGraphics guiGraphics, int centerX, int centerY, int mode, boolean highlighted) {
        double startAngle = SEGMENT_ANGLE * mode - 90 - SEGMENT_ANGLE / 2; // Start from top, offset by half segment
        double endAngle = startAngle + SEGMENT_ANGLE;

        // Determine colors based on state
        int borderColor = 0xFF888888; // Default gray
        int textColor;

        if (highlighted) {
            textColor = 0xFFFFAA00; // Orange text
        } else if (mode == currentMode) {
            textColor = 0xFF00FF00; // Green text
        } else {
            textColor = 0xFFCCCCCC; // Light gray
        }

        // Draw sector boundaries (only 2 radial lines per sector)
        drawLine(guiGraphics, centerX, centerY, startAngle, INNER_RADIUS, WHEEL_RADIUS, borderColor);

        // Draw mode text inside the sector
        Component modeText = Component.translatable(getModeLangKey(mode));
        double midAngle = (startAngle + endAngle) / 2;
        double midAngleRad = Math.toRadians(midAngle);
        int textRadius = (INNER_RADIUS + WHEEL_RADIUS) / 2;
        int textX = centerX + (int)(Math.cos(midAngleRad) * textRadius);
        int textY = centerY + (int)(Math.sin(midAngleRad) * textRadius);

        // Draw text centered at position
        int textWidth = this.font.width(modeText);
        guiGraphics.drawString(this.font, modeText, textX - textWidth / 2, textY - 4, textColor);
    }

    private void drawLine(GuiGraphics guiGraphics, int centerX, int centerY, double angle, int innerRadius, int outerRadius, int color) {
        double angleRad = Math.toRadians(angle);
        int x1 = centerX + (int)(Math.cos(angleRad) * innerRadius);
        int y1 = centerY + (int)(Math.sin(angleRad) * innerRadius);
        int x2 = centerX + (int)(Math.cos(angleRad) * outerRadius);
        int y2 = centerY + (int)(Math.sin(angleRad) * outerRadius);

        // Draw line using simple Bresenham's algorithm
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            guiGraphics.fill(x1, y1, x1 + 1, y1 + 1, color);

            if (x1 == x2 && y1 == y2) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    private void drawCircle(GuiGraphics guiGraphics, int centerX, int centerY, int radius, int color) {
        // Draw circle using Bresenham's circle algorithm
        int x = 0;
        int y = radius;
        int d = 3 - 2 * radius;

        while (x <= y) {
            // Draw 8 symmetric points
            guiGraphics.fill(centerX + x, centerY + y, centerX + x + 1, centerY + y + 1, color);
            guiGraphics.fill(centerX - x, centerY + y, centerX - x + 1, centerY + y + 1, color);
            guiGraphics.fill(centerX + x, centerY - y, centerX + x + 1, centerY - y + 1, color);
            guiGraphics.fill(centerX - x, centerY - y, centerX - x + 1, centerY - y + 1, color);
            guiGraphics.fill(centerX + y, centerY + x, centerX + y + 1, centerY + x + 1, color);
            guiGraphics.fill(centerX - y, centerY + x, centerX - y + 1, centerY + x + 1, color);
            guiGraphics.fill(centerX + y, centerY - x, centerX + y + 1, centerY - x + 1, color);
            guiGraphics.fill(centerX - y, centerY - x, centerX - y + 1, centerY - x + 1, color);

            if (d < 0) {
                d = d + 4 * x + 6;
            } else {
                d = d + 4 * (x - y) + 10;
                y--;
            }
            x++;
        }
    }

    private int getHoveredMode(int mouseX, int mouseY) {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        double deltaX = mouseX - centerX;
        double deltaY = mouseY - centerY;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

        // Dead zone in center
        if (distance < INNER_RADIUS) {
            return -1;
        }

        // Calculate angle
        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX)) + 90;
        if (angle < 0) {
            angle += 360;
        }

        // Offset by half segment to make top segment centered
        angle += SEGMENT_ANGLE / 2;
        if (angle >= 360) {
            angle -= 360;
        }

        int mode = (int)(angle / SEGMENT_ANGLE);
        return Math.min(mode, SEGMENT_COUNT - 1);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        // When key is released, switch to the hovered mode (if any)
        if (selectedMode >= 0) {
            // Send packet to server to change mode
            NetworkHandler.CHANNEL.sendToServer(new SelectStaffModePacket(selectedMode));
        }
        this.onClose();
        return true;
    }

    private String getModeLangKey(int mode) {
        String[] modes = {
            "follow_or_sit",
            "follow_or_stroll",
            "ride_mode",
            "ride",
            "carry",
            "stop_riding",
            "feed"
        };
        if (mode >= 0 && mode < modes.length) {
            return "item." + UnifiedTaming.MOD_ID + ".controlling_staff.mode." + modes[mode];
        }
        return "";
    }
}

