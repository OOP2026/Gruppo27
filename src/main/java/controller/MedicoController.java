package controller;

import gui.InterfacciaMedico;

import model.Medico;
import model.TurnoLavorativo;
import model.PrestazioneMedica;

public class MedicoController {
    private InterfacciaMedico view;
    private Medico model;

    public MedicoController(InterfacciaMedico view, Medico model) {
        this.view = view;
        this.model = model;
        this.view.setBenvenuto("Benvenuto Dott. " + model.getCognome());
    }
}