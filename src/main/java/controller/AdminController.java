package controller;

import gui.InterfacciaAmministratore;
import model.Amministratore;

public class AdminController {
    private InterfacciaAmministratore view;
    private Amministratore admin;

    public AdminController(InterfacciaAmministratore view, Amministratore admin) {
        this.view = view;
        this.admin = admin;

        }

}