package glowredman.amazingtrophies.model.complex;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.minecraft.block.Block;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.gtnewhorizons.angelica.compat.mojang.VertexBuffer;

public class BaseModelStructure {

    // TODO: Make this an optional dependency
    protected VertexBuffer vertexBuffer;
    protected RenderFacesInfo[][][] renderFacesArray;
    protected Map<Character, Pair<Block, Integer>> charToBlock = new HashMap<>();

    protected final int getXLength() {
        return getStructureString().length;
    }

    protected final int getYLength() {
        return getStructureString()[0][0].length();
    }

    protected final int getZLength() {
        return getStructureString()[0].length;
    }

    protected String[][] getStructureString() {
        return null;
    }

    protected final float maxAxisSize() {
        return Math.max(getXLength(), Math.max(getYLength(), getZLength()));
    }

    protected final Pair<Block, Integer> getAssociatedBlockInfo(final char letter) {
        return charToBlock.get(letter);
    }

    protected static void reverseInnerArrays(String[][] array) {
        for (String[] innerArray : array) {
            ArrayUtils.reverse(innerArray);
        }
    }

    protected static String[][] transpose(String[][] array) {
        String[][] newArray = new String[array[0].length][array.length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                newArray[j][i] = array[i][j];
            }
        }
        return newArray;
    }

    private static String[][] deepCopy(String[][] original) {
        if (original == null) {
            return null;
        }

        final String[][] result = new String[original.length][];
        for (int i = 0; i < original.length; i++) {
            result[i] = Arrays.copyOf(original[i], original[i].length);
        }
        return result;
    }

    protected void processStructureMap() {

        String[][] structureCopy = deepCopy(getStructureString());
        Set<Character> transparentBlocks = getTransparentBlocks();

        // These will be replaced with air, so that blocks behind
        // them are rendered as normal.
        removeTransparentBlocks(structureCopy, transparentBlocks);
        generateRenderFacesInfo(structureCopy);
    }

    private void generateRenderFacesInfo(String[][] structureCopy) {

        renderFacesArray = new RenderFacesInfo[getXLength()][getZLength()][getYLength()];

        for (int x = 0; x < getXLength(); x++) {
            for (int y = 0; y < getYLength(); y++) {
                for (int z = 0; z < getZLength(); z++) {

                    RenderFacesInfo renderFacesInfo = new RenderFacesInfo(true);

                    // yNeg Face
                    char yNegBlock = ' ';
                    if (z != 0) {
                        yNegBlock = structureCopy[x][z - 1].charAt(y);
                    }

                    if (yNegBlock != ' ') renderFacesInfo.setYNeg(false);

                    // yPos Face
                    char yPosBlock = ' ';
                    if (z != getZLength() - 1) {
                        yPosBlock = structureCopy[x][z + 1].charAt(y);
                    }

                    if (yPosBlock != ' ') renderFacesInfo.setYPos(false);

                    // xNeg Face
                    char xNegBlock = ' ';
                    if (y != 0) {
                        xNegBlock = structureCopy[x][z].charAt(y - 1);
                    }

                    if (xNegBlock != ' ') renderFacesInfo.setZNeg(false);

                    // xPos Face
                    char xPosBlock = ' ';
                    if (y != getYLength() - 1) {
                        xPosBlock = structureCopy[x][z].charAt(y + 1);
                    }

                    if (xPosBlock != ' ') renderFacesInfo.setZPos(false);

                    // zNeg Face
                    char zNegBlock = ' ';
                    if (x != 0) {
                        zNegBlock = structureCopy[x - 1][z].charAt(y);
                    }

                    if (zNegBlock != ' ') renderFacesInfo.setXNeg(false);

                    // zPos Face
                    char zPosBlock = ' ';
                    if (x != getXLength() - 1) {
                        zPosBlock = structureCopy[x + 1][z].charAt(y);
                    }

                    if (zPosBlock != ' ') renderFacesInfo.setXPos(false);

                    renderFacesArray[x][z][y] = renderFacesInfo;

                }
            }
        }

    }

    private void removeTransparentBlocks(String[][] structure, Set<Character> transparentBlocks) {
        if (structure == null || transparentBlocks == null) {
            return; // Nothing to do if either of them is null
        }

        for (int i = 0; i < structure.length; i++) {
            for (int j = 0; j < structure[i].length; j++) {
                StringBuilder newStr = new StringBuilder();

                // Check each character in the string
                for (char c : structure[i][j].toCharArray()) {
                    if (!transparentBlocks.contains(c)) {
                        // If the character is not in the transparentBlocks, append it to the new string.
                        newStr.append(c);
                    } else {
                        // Otherwise air block.
                        newStr.append(' ');
                    }
                }

                // Update the string in the structure.
                structure[i][j] = newStr.toString();
            }
        }
    }

    private Set<Character> getTransparentBlocks() {
        Set<Character> transparentBlocks = new HashSet<>();

        // Iterate over all blocks to find transparent ones.
        for (Map.Entry<Character, Pair<Block, Integer>> entry : charToBlock.entrySet()) {

            Block block = entry.getValue()
                .getLeft();

            // Block cannot be seen through.
            if (!block.isOpaqueCube()) {
                transparentBlocks.add(entry.getKey());
            }
        }

        return transparentBlocks;
    }
}
