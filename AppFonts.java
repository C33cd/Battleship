import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.InputStream;
import java.io.File;
import java.io.IOException;

public final class AppFonts {
    private static Font headingBase;
    private static Font bodyBase;
    private static Font uiBase;
    private static boolean loaded;

    private AppFonts() {}

    private static synchronized void ensureLoaded() {
        if (loaded) {
            return;
        }

        headingBase = loadHeadingFont();
        bodyBase = loadBodyFont();
        uiBase = loadFont("fonts/NotoSans-Variable.ttf", "SansSerif");
        loaded = true;
    }

    private static Font loadHeadingFont() {
        // Option 1: user-provided licensed copy in the jar or on disk
        Font exactFont = loadFontResource("/fonts/BlackadderITC.ttf");
        if (exactFont == null) {
            exactFont = loadFontFile("fonts/BlackadderITC.ttf");
        }
        if (exactFont != null) {
            return exactFont;
        }

        // Option 2: installed system font
        String[] families = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String family : families) {
            if ("Blackadder ITC".equalsIgnoreCase(family)) {
                return new Font("Blackadder ITC", Font.PLAIN, 12);
            }
        }

        // Option 3: bundled open-source fallback
        return loadFont("fonts/GreatVibes-Regular.ttf", "Serif");
    }

    private static Font loadBodyFont() {
        // Option 1: user-provided licensed copy in the jar or on disk
        Font exactFont = loadFontResource("/fonts/BookAntiqua.ttf");
        if (exactFont == null) {
            exactFont = loadFontFile("fonts/BookAntiqua.ttf");
        }
        if (exactFont != null) {
            return exactFont;
        }

        // Option 2: installed system font
        String[] families = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        for (String family : families) {
            if ("Book Antiqua".equalsIgnoreCase(family)) {
                return new Font("Book Antiqua", Font.PLAIN, 12);
            }
        }

        // Option 3: bundled open-source fallback
        return loadFont("fonts/LibreBaskerville-Variable.ttf", "Serif");
    }

    private static Font loadFontFile(String relativePath) {
        try {
            File file = new File(relativePath);
            if (!file.exists()) {
                return null;
            }
            Font fileFont = Font.createFont(Font.TRUETYPE_FONT, file);
            return fileFont.deriveFont(Font.PLAIN, 12f);
        } catch (FontFormatException | IOException ex) {
            return null;
        }
    }

    private static Font loadFontResource(String resourcePath) {
        try (InputStream inputStream = AppFonts.class.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                return null;
            }
            Font fileFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            return fileFont.deriveFont(Font.PLAIN, 12f);
        } catch (FontFormatException | IOException ex) {
            return null;
        }
    }

    public static Font heading(int style, float size) {
        ensureLoaded();
        return headingBase.deriveFont(style, size);
    }

    public static Font body(int style, float size) {
        ensureLoaded();
        return bodyBase.deriveFont(style, size);
    }

    public static Font ui(int style, float size) {
        ensureLoaded();
        return uiBase.deriveFont(style, size);
    }

    public static Font uiBold(float size) {
        return ui(Font.BOLD, size);
    }

    private static Font loadFont(String relativePath, String fallbackFamily) {
        Font font = loadFontFile(relativePath);
        if (font != null) {
            return font;
        }
        return new Font(fallbackFamily, Font.PLAIN, 12);
    }
}
