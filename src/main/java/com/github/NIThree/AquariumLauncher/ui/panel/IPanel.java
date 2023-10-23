package com.github.NIThree.AquariumLauncher.ui.panel;

import com.github.NIThree.AquariumLauncher.ui.PanelManager;
import javafx.scene.layout.GridPane;

public interface IPanel {
    void init(PanelManager panelManager);
    GridPane getLayout();
    void onShow();
    String getName();
    String getStylesheetPath();
}
