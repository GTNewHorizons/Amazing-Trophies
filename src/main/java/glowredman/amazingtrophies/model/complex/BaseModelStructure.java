package glowredman.amazingtrophies.model.complex;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import com.gtnewhorizon.gtnhlib.util.data.BlockMeta;

import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap.Entry;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap.FastEntrySet;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharOpenHashSet;
import it.unimi.dsi.fastutil.chars.CharSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;

public class BaseModelStructure {

    /**
     * This is an instance of {@code com.gtnewhorizons.angelica.compat.mojang.VertexBuffer} when Angelica is installed.
     */
    protected Object vertexBuffer;
    protected RenderFacesInfo[][][] renderFacesArray;
    protected Char2ObjectMap<BlockMeta> charToBlock = new Char2ObjectOpenHashMap<>();
    protected CharSet skipHalfOffset = new CharOpenHashSet();

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

    protected final BlockMeta getAssociatedBlockInfo(final char letter) {
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
        CharSet transparentBlocks = getTransparentBlocks();

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

    private void removeTransparentBlocks(String[][] structure, CharSet transparentBlocks) {
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

    private CharSet getTransparentBlocks() {
        CharSet transparentBlocks = new CharOpenHashSet();

        // Iterate over all blocks to find transparent ones.
        ObjectSet<Entry<BlockMeta>> entrySet = charToBlock.char2ObjectEntrySet();

        if (entrySet instanceof FastEntrySet) {
            ((FastEntrySet<BlockMeta>) entrySet).fastForEach(entry -> addTransparentBlock(transparentBlocks, entry));
        } else {
            entrySet.forEach(entry -> addTransparentBlock(transparentBlocks, entry));
        }

        return transparentBlocks;
    }

    private static void addTransparentBlock(CharSet transparentBlocks, Entry<BlockMeta> entry) {
        if (!entry.getValue()
            .getBlock()
            .isOpaqueCube()) {
            transparentBlocks.add(entry.getCharKey());
        }
    }
}
