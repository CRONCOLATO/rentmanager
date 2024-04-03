package com.epf.rentmanager.ui.cli;

public class Cli {

    private Cli (){

    }

    public static void main(final String[] args) {
        CliMenu cliMenu = new CliMenu();
        cliMenu.start();
    }

}

