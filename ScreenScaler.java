import java.awt.Dimension;
import java.awt.Toolkit;

public class ScreenScaler {
    private static final double BASE_WIDTH = 1920;
    private static final double BASE_HEIGHT = 1080;
    private static final int STANDARD_DPI = 96;
    private static double scaleX = 1.0;
    private static double scaleY = 1.0;
    private static boolean initialized = false;
    
    public static void initialize() {
        if (initialized) {
            return;
        }
        
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
        
        // Calculate DPI scale factor (at 125% scaling, DPI is 120; standard is 96)
        double dpiScale = (double) dpi / STANDARD_DPI;
        
        // Calculate actual physical resolution by accounting for OS-level DPI scaling
        double physicalWidth = screenSize.getWidth() * dpiScale;
        double physicalHeight = screenSize.getHeight() * dpiScale;
        
        // Calculate scale factors relative to 1920x1080 baseline
        scaleX = physicalWidth / BASE_WIDTH;
        scaleY = physicalHeight / BASE_HEIGHT;
        
        initialized = true;
    }
    
    public static int scaleX(int value) {
        return (int)(value * scaleX);
    }
    
    public static int scaleY(int value) {
        return (int)(value * scaleY);
    }
    
    public static double getScaleX() {
        return scaleX;
    }
    
    public static double getScaleY() {
        return scaleY;
    }
}
