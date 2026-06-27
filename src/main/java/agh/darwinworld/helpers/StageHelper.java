package agh.darwinworld.helpers;

import agh.darwinworld.helpers.natives.Dwmapi;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import java.lang.reflect.Method;
import java.util.logging.Logger;

/**
 * A utility class for stages.
 */
public class StageHelper {
    private static final Logger logger = Logger.getLogger(StageHelper.class.getName());

    /**
     * Checks stage's decoration insets.
     *
     * @param stage instance of the stage.
     * @return insets of the window.
     */
    public static Insets getWindowInsets(Stage stage) {
        double stageWidth = stage.getWidth();
        double stageHeight = stage.getHeight();
        double contentWidth = stage.getScene().getWidth();
        double contentHeight = stage.getScene().getHeight();
        double horizontalInsets = (stageWidth - contentWidth) / 2;
        double verticalInsets = stageHeight - contentHeight - horizontalInsets;
        return new Insets(verticalInsets, horizontalInsets, verticalInsets, horizontalInsets);
    }

    /**
     * Binds stage's min height and min width into root element's
     * min height and min width. Also default width ang height
     * of the stage is set to its min width and min height
     *
     * @param stage instance of the stage.
     * @param root  root element of the scene.
     */
    private static final int BASE_FONT_PX;

    static {
        double scale = Math.max(1.0, Screen.getPrimary().getDpi() / 96.0);
        BASE_FONT_PX = (int) Math.round(14 * scale);
    }

    public static void bindMinSize(Stage stage, Pane root) {
        root.setStyle("-fx-font-size: " + BASE_FONT_PX + "px;");
        EventHandler<WindowEvent> oldOnShown = stage.getOnShown();
        stage.setOnShown(e -> {
            if (oldOnShown != null)
                oldOnShown.handle(e);
            stage.setMinWidth(stage.getWidth());
            stage.setMinHeight(stage.getHeight());
        });
    }

    /**
     * Enables or disables dark mode on window.
     *
     * @param stage    instance of the window.
     * @param darkMode should dark mode be enabled?
     */
    public static void setDarkMode(Window stage, boolean darkMode) {
        if (stage.isShowing()) {
            setDarkModeInternal(stage, darkMode);
        } else {
            EventHandler<WindowEvent> oldOnShown = stage.getOnShown();
            stage.setOnShown(e -> {
                if (oldOnShown != null)
                    oldOnShown.handle(e);
                setDarkModeInternal(stage, darkMode);
            });
        }
    }

    private static void setDarkModeInternal(Window stage, boolean darkMode) {
        String os = System.getProperty("os.name").toLowerCase();
        if (!os.contains("win")) return;
        WinDef.HWND hwnd = getNativeHandleForWindow(stage);
        if (hwnd == null) {
            logger.warning("Cannot set dark mode: native window handle is unavailable.");
            return;
        }
        Dwmapi dwmapi = Dwmapi.INSTANCE;
        WinDef.BOOLByReference darkModeRef = new WinDef.BOOLByReference(new WinDef.BOOL(darkMode));
        int result = dwmapi.DwmSetWindowAttribute(hwnd, 20, darkModeRef, Native.getNativeSize(WinDef.BOOLByReference.class));
        if (result != 0)
            logger.warning("DwmSetWindowAttribute failed with code: " + result);
    }


    private static WinDef.HWND getNativeHandleForWindow(Window stage) {
        try {
            final Method getPeer = Window.class.getDeclaredMethod("getPeer");
            getPeer.setAccessible(true);
            final Object tkStage = getPeer.invoke(stage);
            final Method getRawHandle = tkStage.getClass().getMethod("getRawHandle");
            getRawHandle.setAccessible(true);
            final Pointer pointer = new Pointer((Long) getRawHandle.invoke(tkStage));
            return new WinDef.HWND(pointer);
        } catch (Exception ex) {
            logger.warning("Unable to determine native handle for window: " + ex.getMessage());
            return null;
        }
    }
}
