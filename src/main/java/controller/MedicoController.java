package controller;

import gui.InterfacciaMedico;
import model.Medico;

public class MedicoController {
    private InterfacciaMedico view;
    private Medico model;

    public MedicoController(InterfacciaMedico view, Medico model) {
        this.view = view;
        this.model = model;
    }
}