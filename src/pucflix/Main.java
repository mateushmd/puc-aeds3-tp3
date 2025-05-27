package pucflix;

import pucflix.view.*;
import pucflix.model.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        /*
        try
        {
            Prompt prompt = new Prompt();
            prompt.start();
            prompt.close();
        } catch(Exception ex) { System.err.println(ex.getMessage()); }
        */
        try {
        File f = new File("./dados");
        if(!f.exists())
            f.mkdir();
        PostingsList l = new PostingsList(4, "./dados/dicionario.listaenv.bd", "./dados/blocos.listaenv.bd");

        l.add("Três tigres com três pratos para três tigres", 123);
        } catch(Exception ex){ System.err.println(ex.getMessage());}
    }
}
