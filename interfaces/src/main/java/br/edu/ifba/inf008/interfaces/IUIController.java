package br.edu.ifba.inf008.interfaces;

import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;

public interface IUIController
{
    boolean addMenuItem(String menuText, MenuItem menuitem);
    boolean addTab(Tab tab);

}
