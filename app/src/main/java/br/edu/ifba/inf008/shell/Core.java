package br.edu.ifba.inf008.shell;

import br.edu.ifba.inf008.interfaces.*;
import br.edu.ifba.inf008.MariaDBProvider;
import javafx.scene.control.MenuBar;
import javafx.scene.control.TabPane;
import javafx.application.Application;
import javafx.application.Platform;

public class Core extends ICore
{
    private final IDataProvider dataProvider;
    private final IUIController uiController;
    private final IAuthenticationController authenticationController;
    private final IIOController ioController;
    private final IPluginController pluginController;

    public Core(TabPane mainTabPane, MenuBar menubar) {
        if (instance != null) {
            System.err.println("Fatal error: core is already initialized!");
            System.exit(-1);
        }

        instance = this;

        this.dataProvider = new MariaDBProvider();
        System.out.println("Core: DataProvider inicializado.");

        this.uiController = new UIController(mainTabPane,menubar);
        this.authenticationController = new AuthenticationController();
        this.ioController = new IOController();
        this.pluginController = new PluginController();
    }

    @Override
    public IDataProvider getDataProvider() {
        return this.dataProvider;
    }

    @Override
    public IUIController getUIController() {
        return this.uiController;
    }

    @Override
    public IAuthenticationController getAuthenticationController() {
        return this.authenticationController;
    }

    @Override
    public IIOController getIOController() {
        return this.ioController;
    }

    @Override
    public IPluginController getPluginController() {
        return this.pluginController;
    }
}
